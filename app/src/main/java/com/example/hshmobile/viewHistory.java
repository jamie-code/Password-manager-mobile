package com.example.hshmobile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.hshmobile.MainActivity.calculateHMAC;

public class viewHistory extends AppCompatActivity {

    private String encryptionpwd;
    private String token;
    private String email;
    private String usrName;
    private String loginID;
    private String selectedtimestamp;
    private String selectedpwd;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private String secret;
    Crypto banscrypt = new Crypto();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        encryptionpwd = intent.getStringExtra("usrpassword");
        token = intent.getStringExtra("usrtoken");
        email = intent.getStringExtra("usremail");
        usrName = intent.getStringExtra("usrname");
        loginID = intent.getStringExtra("loginid");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        /*String sha256hash="";
        try {
            sha256hash = banscrypt.toHexString(banscrypt.getSHA(encryptionpwd));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        secret=sha256hash.substring(32);*/


//Load login history
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
                    viewHistory.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(myResponse);
                            try {
                                String hmac = calculateHMAC(myResponse, token);
                                System.out.println(hmac);

                                final MediaType JSON= MediaType.get("application/json; charset=utf-8");

                                OkHttpClient client2 = new OkHttpClient();
                                String url = "https://api.jamiez.co.uk/hshsa/view/"+loginID+"/history";
                                //RequestBody body = RequestBody.create(JSON, "{'email':'boop','challenge':'1'}");
                                RequestBody body = new FormBody.Builder()
                                        .add("email", email)
                                        .add("challenge", hmac)
                                        .build();
                                Request request = new Request.Builder()
                                        .url(url)
                                        .post(body)
                                        .build();
                                client2.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                    }
                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            final String myResponse2 = response.body().string();
                                            viewHistory.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    System.out.println(myResponse2);
                                                    JSONArray pwds;
                                                    try {
                                                        pwds = new JSONArray(myResponse2);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        pwds=new JSONArray();
                                                    }
                                                    FragmentManager fm = getSupportFragmentManager();


                                                    Fragment fragment = fm.findFragmentById(R.id.nav_host_fragment);
                                                    //TextView text = fragment.getView().findViewById(R.id.txthome);
                                                    //text.setText("Boopyboop");

                                                    LinearLayout layout = (LinearLayout) findViewById(R.id.passwordHistoryLayout);

                                                    //set the properties for button
                                                    //LinearLayout lout = new LinearLayout(fragment.getContext());
                                                    //lout.setLayoutParams(new );


                                                    for (int i=0; i < pwds.length(); i++)
                                                    {
                                                        try {
                                                            JSONObject oneObject = pwds.getJSONObject(i);
                                                            // Pulling items from the array
                                                            String sha256hash=banscrypt.toHexString(banscrypt.getSHA(encryptionpwd));
                                                            secret=sha256hash.substring(32);
                                                            System.out.println("AMHERE");

                                                            final String oneObjectsItem = oneObject.getString("timestamp");
                                                            System.out.println(oneObjectsItem);
                                                            final String oneObjectsItem2 = oneObject.getString("pwd");



                                                            Button btnTag = new Button(viewHistory.this);
                                                            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                                                            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,250));
                                                            btnTag.setOnClickListener(new View.OnClickListener()
                                                                                      {
                                                                                          public void onClick(View v){
                                                                                              selectedtimestamp=oneObjectsItem;
                                                                                              selectedpwd=oneObjectsItem2;
                                                                                              System.out.println("HERE...");
                                                                                              createShowPasswordHistoryDialog(banscrypt.decrypt(selectedpwd, secret));


                                                                                          }
                                                                                      }
                                                            );
                                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                                            SimpleDateFormat output = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
                                                            Date d = sdf.parse(oneObjectsItem);
                                                            String formattedTime = output.format(d);
                                                            btnTag.setText(formattedTime);
                                                            btnTag.setHeight(100);
                                                            //btnTag.setText(decname+"\n"+decurl);
                                                            //add button to the layout

                                                            layout.addView(btnTag);


                                                        } catch (JSONException e) {
                                                            // Oops
                                                        } catch (NoSuchAlgorithmException e) {
                                                            System.out.println("Exception thrown for incorrect algorithm: " + e);
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }






                                                }
                                            });
                                        }
                                    }
                                });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        /*Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        myIntent.putExtra("usrpassword", encryptionpwd);
        myIntent.putExtra("token", token);
        myIntent.putExtra("email", email);
        myIntent.putExtra("name", usrName);
        viewHistory.this.startActivity(myIntent);*/
        //startActivityForResult(myIntent, 0);
        finish();
        return true;
    }
    public void createShowPasswordHistoryDialog(final String password){
        dialogBuilder = new AlertDialog.Builder(this);
        final View EditPasswordView = getLayoutInflater().inflate(R.layout.showpassword_popup, null);
        final EditText editPassword = (EditText) EditPasswordView.findViewById(R.id.ShowPassword);

        editPassword.setText(password);
        editPassword.setKeyListener(null);

        Button btnCancelEdit = (Button) EditPasswordView.findViewById(R.id.btnCloseShowPassword);
        Button btnCopyPassword = (Button) EditPasswordView.findViewById(R.id.btnCopyShowPassword);

        dialogBuilder.setView(EditPasswordView);
        dialog=dialogBuilder.create();
        dialog.show();




        btnCancelEdit.setOnClickListener((new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        }));
        btnCopyPassword.setOnClickListener((new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                ClipData clippwd = ClipData.newPlainText("Password", password);
                clipboard.setPrimaryClip(clippwd);
                Toast.makeText(viewHistory.this, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }));
    }



}
