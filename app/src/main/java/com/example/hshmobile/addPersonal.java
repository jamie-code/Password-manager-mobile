package com.example.hshmobile;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class addPersonal extends AppCompatActivity {
    TextView name, url, username, password;
    private String encryptionpwd;
    private String token;
    private String email;
    private String usrName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        encryptionpwd = intent.getStringExtra("usrpassword");
        token = intent.getStringExtra("usrtoken");
        email = intent.getStringExtra("usremail");
        usrName = intent.getStringExtra("usrname");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_personal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        /*Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        myIntent.putExtra("usrpassword", encryptionpwd);
        myIntent.putExtra("token", token);
        myIntent.putExtra("email", email);
        myIntent.putExtra("name", usrName);
        addPersonal.this.startActivity(myIntent);*/
        finish();
        //startActivityForResult(myIntent, 0);
        return true;
    }

    public void addPassword(View view) {
        name = findViewById(R.id.txtNameAdd);
        url = findViewById(R.id.txtURLAdd);
        username = findViewById(R.id.txtUsernameAdd);
        password = findViewById(R.id.txtPasswordAdd);
        final String txtname = name.getText().toString().trim();
        final String txturl = url.getText().toString().trim();
        final String txtuser = username.getText().toString().trim();
        final String txtpassword = password.getText().toString().trim();

        OkHttpClient client = new OkHttpClient();
        String url = "https://api.jamiez.co.uk/hshsa/time";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    addPersonal.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(myResponse);
                            try {
                                String hmac = MainActivity.calculateHMAC(myResponse, token);
                                System.out.println(hmac);

                                final MediaType JSON = MediaType.get("application/json; charset=utf-8");

                                OkHttpClient client2 = new OkHttpClient();
                                String url = "https://api.jamiez.co.uk/hshsa/add/personal";


                                    Crypto banscrypt = new Crypto();
                                    String sha256hash = banscrypt.toHexString(banscrypt.getSHA(encryptionpwd));
                                    String secret = sha256hash.substring(32);
                                    String encname = banscrypt.encrypt(txtname, secret);
                                    String encurl = banscrypt.encrypt(txturl, secret);
                                    String encuser = banscrypt.encrypt(txtuser, secret);
                                    String encpwd = banscrypt.encrypt(txtpassword, secret);

                                    /*String sha256hash2 = banscrypt.toHexString(banscrypt.getSHA(encryptionpwd));
                                    String secret2 = sha256hash2.substring(32);
                                    String decname = banscrypt.decrypt(encname, secret2);
                                    String decurl = banscrypt.decrypt(encurl, secret2);
                                    String decuser = banscrypt.decrypt(encuser, secret2);
                                    String decpwd = banscrypt.decrypt(encpwd, secret2);

                                    System.out.println(txtname+decname);
                                    System.out.println(txturl+decurl);
                                    System.out.println(txtuser+decuser);
                                    System.out.println(txtpassword+decpwd);*/

                                    OkHttpClient client = new OkHttpClient();

                                    RequestBody body = new FormBody.Builder()
                                            .add("name", encname)
                                            .add("url", encurl)
                                            .add("username", encuser)
                                            .add("password", encpwd)
                                            .add("email", email)
                                            .add("challenge", hmac)
                                            .build();
                                    Request request = new Request.Builder()
                                            .url(url)
                                            .post(body)
                                            .build();
                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            final String myResponse2 = response.body().string();
                                            addPersonal.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    System.out.println(myResponse2);
                                                    Toast.makeText(addPersonal.this, "Login Added!", Toast.LENGTH_SHORT);
                                                    Intent mainactivity = new Intent(addPersonal.this, MainActivity.class);
                                                    mainactivity.putExtra("email", email);
                                                    mainactivity.putExtra("name", usrName);
                                                    mainactivity.putExtra("usrpassword", encryptionpwd);
                                                    mainactivity.putExtra("token", token);
                                                    addPersonal.this.startActivity(mainactivity);
                                                }
                                            });
                                        }
                                    }
                                });
                                /*
                                //try (Response response = client.newCall(request).execute()) {
//                                        return response.body().string();
//                                    }

                                    /*
                                OkHttpClient client2 = new OkHttpClient();

                                Request request = new Request.Builder()
                                        .url(url)
                                        .build();
                                */
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (SignatureException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

    }
}
