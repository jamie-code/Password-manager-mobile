package com.example.hshmobile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity {

    Button btnLogin;
    TextView email, pwd;
    private String rtoken;
    private String usrpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "LOL, you expect help?", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }
    public void startLogin(View view){
        btnLogin=findViewById(R.id.button);
        email=findViewById(R.id.txtEmail);
        pwd=findViewById(R.id.txtPwd);

        if(email.getText().toString().isEmpty() || pwd.getText().toString().isEmpty()){
            Toast.makeText(Login.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
        } else {
            String txtemail=email.getText().toString().trim();
            String txtpassword=pwd.getText().toString().trim();
            if(txtemail=="" || txtemail.matches("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{1,5}|[0-9]{1,3})(\\]?)$") != true){
                Toast.makeText(Login.this, "Must use a valid email!", Toast.LENGTH_LONG).show();
                return;
            }
            if(txtpassword=="" || txtpassword.length()<8){
                Toast.makeText(Login.this, "Password must be at least 8 characters!", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(Login.this, "Attempting Login...", Toast.LENGTH_SHORT).show();
            sendPOST post = new sendPOST();
            post.setParams(txtemail, txtpassword);
            post.execute();
        }
    }
    public static Handler UIHandler;

    static
    {
        UIHandler = new Handler(Looper.getMainLooper());
    }
    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }
    class sendPOST extends AsyncTask<Void,Void,Void> {
        private String usrEmail;
        private String usrPWD;
        public void setParams(String txtEmail, String txtPWD)
        {
            usrEmail=txtEmail;
            usrPWD=txtPWD;
        }
        private String calculateSHA512Hash(String input) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-512");
                byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder();
                for (byte b : hashBytes) {
                    String hex = String.format("%02x", b);
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
        public String Argon2Encode(String password, String salt, int outputLength, int time){
            int parrallelism = 1;
            int memory = 65536;
            salt = salt.trim();
            password = password.trim();
            Argon2Parameters parameters = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                    .withSalt(salt.getBytes(StandardCharsets.UTF_8)) // byte[] salt
                    //.withPassword(usrPWD) // byte[] password
                    .withParallelism(parrallelism) // Number of threads
                    .withIterations(time) // Number of iterations
                    .withMemoryAsKB(memory) // Memory in kilobytes
                    //.withOutputLength(128) // Output hash length in bytes
                    .build();
            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(parameters);
            byte[] result = new byte[outputLength];
            generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), result, 0, result.length);
            String encodedHash = "$argon2id$v=19$m="+memory+",t="+time+",p="+parrallelism+"$" + Base64.toBase64String(salt.getBytes(StandardCharsets.UTF_8)).replaceAll("=+$", "") + "$" + Base64.toBase64String(result).replaceAll("=+$", "");
            return encodedHash;
        }
        public boolean isValid(String json) {
            try {
                new JSONObject(json);
            } catch (JSONException e) {
                return false;
            }
            return true;
        }
        @Override
        protected Void doInBackground(Void... params) {

            try {
                String Hash1 = Argon2Encode(usrPWD, usrEmail, 128, 5);
                String Hash2 = calculateSHA512Hash(Hash1);
                String Hash3 = Argon2Encode(Hash2, usrPWD, 128, 3);

                URL url = new URL("https://api.jamiez.co.uk/pwdmanager/login");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                try {
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    JSONObject jsonstring = new JSONObject();
                    jsonstring.put("email", usrEmail);
                    jsonstring.put("argon2hash", Hash3);
                    outputStream.write(jsonstring.toString().getBytes());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                int status = httpURLConnection.getResponseCode();
                System.out.println(status);
                if(status==200) {


                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                        String line;
                        StringBuilder response = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }
                        bufferedReader.close();
                        String ReplyFromServer = response.toString();
                        if(isValid(ReplyFromServer)){//JSON is returned
                            //Check if 2FA
                            JSONObject jsonReply = new JSONObject(ReplyFromServer);
                            int verified = jsonReply.getInt("verified");
                        } else {//Error
                            Login.runOnUI(new Runnable() {
                                public void run() {
                                    Toast.makeText(Login.this, ReplyFromServer, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                            /*if(line.equals("NO")){
                                Login.runOnUI(new Runnable() {
                                    public void run() {
                                        Toast.makeText(Login.this, "Incorrect Login Credentials", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                String[] split = line.split(":");
                                rtoken = split[1];
                                String name = split[0];
                                Login.runOnUI(new Runnable() {
                                                  public void run() {
                                                      TextView loginpwd = findViewById(R.id.txtPwd);
                                                      loginpwd.setText("");
                                                  }
                                              });

                                Intent mainpage = new Intent(Login.this, MainActivity.class);
                                mainpage.putExtra("email", usrEmail);
                                mainpage.putExtra("name", name);
                                mainpage.putExtra("usrpassword", usrPWD);
                                mainpage.putExtra("token", rtoken);
                                Login.this.startActivity(mainpage);
                            }*/
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                } else {
                    Login.runOnUI(new Runnable() {
                        public void run() {
                            Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_LONG).show();
                        }
                    });
                }
               // } else {
                    // ... do something with unsuccessful response
               // }

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
