package com.example.hshmobile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements ActionBottomDialogFragment.ItemClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private String token;
    public String usrpwd;
    private String name;
    private String email;
    private String selectedname;
    private String selectedurl;
    private String selecteduser;
    private String selectedpwd;
    private String selectedid;
    private Button btnsave, btncancel;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;


    Crypto banscrypt = new Crypto();

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        Toast.makeText(MainActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        name = intent.getStringExtra("name");
        token = intent.getStringExtra("token");
        usrpwd = intent.getStringExtra("usrpassword");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_shared, R.id.nav_generate_password,
                R.id.nav_history, R.id.nav_share, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();





        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Bundle bundle = new Bundle();
        bundle.putString("encpassword", usrpwd);
        bundle.putString("usremail", email);
        bundle.putString("usrname", name);
        bundle.putString("usrtoken", token);



        navController.setGraph(R.navigation.mobile_navigation, bundle);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationView navview = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navview.getHeaderView(0);
        TextView navEmail = (TextView) headerView.findViewById(R.id.EmailtextView);
        navEmail.setText(email);
        TextView navName = (TextView) headerView.findViewById(R.id.name);
        navName.setText(name);







        /*navigationView.getMenu().findItem(R.id.nav_home).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle().equals("Personal Vault")) {


                }
                return true;
            }
        });*/

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle().equals("Logout")) {
                    //make popup
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Are you sure you want to logout?");
                    builder.setCancelable(true);
                    builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            //do logout stuff

                            OkHttpClient logoutclient = new OkHttpClient();
                            RequestBody logoutbody = new FormBody.Builder()
                                    .add("token", token)
                                    .build();
                            Request request = new Request.Builder()
                                    .url("https://api.jamiez.co.uk/hshsa/logout/"+email)
                                    .delete(logoutbody)
                                    .build();
                            logoutclient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {

                                }
                            });
                        }
                    });
                    builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();
                }
                return true;
            }
        });





        /*FloatingActionButton fab =findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        //TextView myAwesomeTextView = (TextView)findViewById(R.id.EmailtextView);

//in your OnCreate() method
       //myAwesomeTextView.setText("My Awesome Text");

        //Old Login code moved to homefragment
      /*  try
        {
            String sha256hash=banscrypt.toHexString(banscrypt.getSHA(usrpwd));
            String secret=sha256hash.substring(32);
            //System.out.println(secret);
            //banscrypt.decrypt("theencpwd", secret);

        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown for incorrect algorithm: " + e);
        }

//Load personal logins
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
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(myResponse);
                            try {
                                String hmac = calculateHMAC(myResponse, token);
                                System.out.println(hmac);

                                final MediaType JSON= MediaType.get("application/json; charset=utf-8");

                                OkHttpClient client2 = new OkHttpClient();
                                String url = "https://api.jamiez.co.uk/hshsa/view/personal";
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
                                            MainActivity.this.runOnUiThread(new Runnable() {
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

                                                    LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);

                                                    //set the properties for button
                                                    //LinearLayout lout = new LinearLayout(fragment.getContext());
                                                    //lout.setLayoutParams(new );


                                                    for (int i=0; i < pwds.length(); i++)
                                                    {
                                                        try {
                                                            JSONObject oneObject = pwds.getJSONObject(i);
                                                            // Pulling items from the array
                                                            String sha256hash=banscrypt.toHexString(banscrypt.getSHA(usrpwd));
                                                            String secret=sha256hash.substring(32);

                                                            final String oneObjectsItem = oneObject.getString("name");
                                                            final String oneObjectsItem2 = oneObject.getString("pwd");
                                                            final String oneObjectsItem3 = oneObject.getString("url");
                                                            final String oneObjectsItem4 = oneObject.getString("user");
                                                            final String oneObjectsItem5 = oneObject.getString("id");
                                                            final String decname = banscrypt.decrypt(oneObjectsItem, secret);
                                                            final String decurl = banscrypt.decrypt(oneObjectsItem3, secret);
                                                            Button btnTag = new Button(fragment.getContext());
                                                            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                                                            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,250));
                                                            btnTag.setOnClickListener(new View.OnClickListener()
                                                                                      {
                                                                                          public void onClick(View v){
                                                                                              selectedname=oneObjectsItem;
                                                                                              selectedpwd=oneObjectsItem2;
                                                                                              selectedurl=oneObjectsItem3;
                                                                                              selecteduser=oneObjectsItem4;
                                                                                              selectedid=oneObjectsItem5;
                                                                                              //View mainview =findViewById(android.R.id.content);
                                                                                              showBottomSheet(v);
                                                                                              //Toast.makeText(MainActivity.this, selectedname, Toast.LENGTH_LONG).show();
                                                                                          }
                                                                                      }
                                                            );
                                                            btnTag.setText(decname);
                                                            btnTag.setHeight(200);
                                                            btnTag.setText(decname+"\n"+decurl);
                                                            //add button to the layout
                                                            layout.addView(btnTag);


                                                        } catch (JSONException e) {
                                                            // Oops
                                                        } catch (NoSuchAlgorithmException e) {
                                                            System.out.println("Exception thrown for incorrect algorithm: " + e);
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
       /*                     } catch (NoSuchAlgorithmException e) {
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
        });*/






       /* sendREQ req = new sendREQ();
        req.setParams("time");
        //req.execute();
        try {
            //String str_result = req.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/

        /*try {
            URL url = new URL("https://api.jamiez.co.uk/hshsa/time");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
            in.close();
            /*URL url = new URL("https://api.jamiez.co.uk/hshsa/view/personal");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
            in.close();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        //Load shared logins
        /*try {
            URL url = new URL("https://jamiez.co.uk/hshsa/api/admin/view?sig="); // +frmAdd2.HashString(Gettime(), frmLogin.token");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    // ... do something with line
                    System.out.println("beep: " + line);
                }
            } finally {
                urlConnection.disconnect();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }
    public static String pwd;
    public static String usrname;
    public static String usrtoken;
    public static String usremail;
     public static String getPwd(){

        return pwd;
        //find a way to move the loading of the passwords into the homefragment file so it works after changing tab similar to genpassword but it needs the encpwd
     }
     public static String getUserName(){
         return usrname;
     }
     public static String getUserToken(){
         return usrtoken;
     }
     public static String getUserEmail(){
         return usremail;
     }
     public static void setPwd(String passwd){
        pwd=passwd;
     }
    public static void setUserName(String uname){
        usrname=uname;
    }
    public static void setUserToken(String utoken){
        usrtoken=utoken;
    }
    public static void setUserEmail(String uemail){
        usremail=uemail;
    }

    public void showBottomSheet(View view) {
        ActionBottomDialogFragment addPhotoBottomDialogFragment =
                ActionBottomDialogFragment.newInstance();
        addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                ActionBottomDialogFragment.TAG);
    }
    public void onItemClick(String item) {
       // tvSelectedItem.setText("Selected action item is " + item);
        String sha256hash="";
        try {
            sha256hash = banscrypt.toHexString(banscrypt.getSHA(usrpwd));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String secret=sha256hash.substring(32);

        switch (Integer.parseInt(item)){
            case R.id.txtView://View
                
                break;
            case R.id.txtEdit://Edit
                createEditPasswordDialog(banscrypt.decrypt(selectedname, secret),banscrypt.decrypt(selectedurl, secret),banscrypt.decrypt(selecteduser, secret),banscrypt.decrypt(selectedpwd, secret), selectedid);
                break;
            case R.id.txtShowPassword://Show password
                createShowPasswordDialog(banscrypt.decrypt(selectedpwd, secret));
                break;
            case R.id.txtCopyPassword://Copy password
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clippwd = ClipData.newPlainText("Password", banscrypt.decrypt(selectedpwd, secret));
                clipboard.setPrimaryClip(clippwd);
                break;
            case R.id.txtCopyUsername://Copy username
                ClipboardManager clipboard2 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipuser = ClipData.newPlainText("Username", banscrypt.decrypt(selecteduser, secret));
                clipboard2.setPrimaryClip(clipuser);
                break;
            case R.id.txtCopyUrl://copy url
                ClipboardManager clipboard3 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipurl = ClipData.newPlainText("URL", banscrypt.decrypt(selectedurl, secret));
                clipboard3.setPrimaryClip(clipurl);
                break;
            case R.id.txtPasswordHistory:
                Intent viewhistory = new Intent(MainActivity.this, viewHistory.class);
                viewhistory.putExtra("usrpassword", usrpwd);
                viewhistory.putExtra("usrtoken", token);
                viewhistory.putExtra("usremail", email);
                viewhistory.putExtra("usrname", name);
                viewhistory.putExtra("loginid", selectedid);
                MainActivity.this.startActivity(viewhistory);
                break;
            case R.id.txtShare://share
                Toast.makeText(MainActivity.this, "Waiting implementation", Toast.LENGTH_SHORT).show();
                break;
            case R.id.txtDelete://delete
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to delete your login for " +  banscrypt.decrypt(selectedurl, secret) + "?");
                builder.setCancelable(true);
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                        OkHttpClient deletepersonaltimeclient = new OkHttpClient();
                        String url = "https://api.jamiez.co.uk/hshsa/time";
                        Request request = new Request.Builder()
                                .url(url)
                                .build();
                        deletepersonaltimeclient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    final String myResponse = response.body().string();
                                    String hmac="";
                                    try {
                                        hmac = calculateHMAC(myResponse, token);
                                        OkHttpClient deleteclient = new OkHttpClient();
                                        RequestBody deletebody = new FormBody.Builder()
                                                .add("email", email)
                                                .add("challenge", hmac)
                                                .build();
                                        Request request = new Request.Builder()
                                                .url("https://api.jamiez.co.uk/hshsa/delete/personal/" + selectedid)
                                                .delete(deletebody)
                                                .build();
                                        deleteclient.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {

                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {

                                            }
                                        });
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    } catch (SignatureException e) {
                                        e.printStackTrace();
                                    } catch (InvalidKeyException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        });
                    }
                });
                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item);
        }
        Toast.makeText(MainActivity.this, item, Toast.LENGTH_LONG).show();
    }


    public void createEditPasswordDialog(String name, String url, String username, String password, final String pwdid){
        dialogBuilder = new AlertDialog.Builder(this);
        final View EditPasswordView = getLayoutInflater().inflate(R.layout.popup, null);
        final EditText editName = (EditText) EditPasswordView.findViewById(R.id.editName);
        final EditText editURL = (EditText) EditPasswordView.findViewById(R.id.editURL);
        final EditText editUsername = (EditText) EditPasswordView.findViewById(R.id.editUsername);
        final EditText editPassword = (EditText) EditPasswordView.findViewById(R.id.editPassword);

        editName.setText(name);
        editURL.setText(url);
        editUsername.setText(username);
        editPassword.setText(password);

        Button btnSaveEdit = (Button) EditPasswordView.findViewById(R.id.btnSaveEdit);
        Button btnShowPassword = (Button) EditPasswordView.findViewById(R.id.btnshowpassword);
        Button btnCancelEdit = (Button) EditPasswordView.findViewById(R.id.btnCancelEdit);

        dialogBuilder.setView(EditPasswordView);
        dialog=dialogBuilder.create();
        dialog.show();

        btnSaveEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    String sha256hash = banscrypt.toHexString(banscrypt.getSHA(usrpwd));
                    String secret = sha256hash.substring(32);
                    final String newname = banscrypt.encrypt(editName.getText().toString().trim(), secret);
                    final String newurl = banscrypt.encrypt(editURL.getText().toString().trim(), secret);
                    final String newusername = banscrypt.encrypt(editUsername.getText().toString().trim(), secret);
                    final String newpassword = banscrypt.encrypt(editPassword.getText().toString().trim(), secret);
                    final String passwordID = pwdid;
                    //save to api after encrypt
                    OkHttpClient clientupdate = new OkHttpClient();
                    String url = "https://api.jamiez.co.uk/hshsa/time";
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    clientupdate.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                final String myResponse = response.body().string();
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println(myResponse);
                                        try {
                                            String hmac = calculateHMAC(myResponse, token);
                                            System.out.println(hmac);

                                            final MediaType JSON = MediaType.get("application/json; charset=utf-8");

                                            OkHttpClient clientupdate2 = new OkHttpClient();
                                            String url = "https://api.jamiez.co.uk/hshsa/update/personal";

                                            RequestBody body = new FormBody.Builder()
                                                    .add("email", email)
                                                    .add("challenge", hmac)
                                                    .add("id", passwordID)
                                                    .add("name", newname)
                                                    .add("url", newurl)
                                                    .add("username", newusername)
                                                    .add("password", newpassword)
                                                    .build();
                                            Request request = new Request.Builder()
                                                    .url(url)
                                                    .post(body)
                                                    .build();
                                            clientupdate2.newCall(request).enqueue(new Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {
                                                    e.printStackTrace();
                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {

                                                    dialog.dismiss();
                                                    if (response.isSuccessful()) {
                                                        final String myResponse2 = response.body().string();
                                                        /*MainActivity.this.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                System.out.println(myResponse2);
                                                                Toast.makeText(MainActivity.this, "Login saved", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });*/



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
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
        btnShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editPassword.getHint().toString()=="Hide") {
                    editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editPassword.setHint("Show");
                } else {
                    editPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    editPassword.setHint("Hide");
                    System.out.println(editPassword.getHint().toString());
                }


            }
        });


        btnCancelEdit.setOnClickListener((new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        }));
    }

    public void createShowPasswordDialog(final String password){
        dialogBuilder = new AlertDialog.Builder(this);
        final View EditPasswordView = getLayoutInflater().inflate(R.layout.showpassword_popup, null);
        final EditText editPassword = (EditText) EditPasswordView.findViewById(R.id.ShowPassword);

        editPassword.setText(password);
        editPassword.setKeyListener(null);

        Button btnCancelEdit = (Button) EditPasswordView.findViewById(R.id.btnCloseShowPassword);
        Button btnCopyPwd = (Button) EditPasswordView.findViewById(R.id.btnCopyShowPassword);

        dialogBuilder.setView(EditPasswordView);
        dialog=dialogBuilder.create();
        dialog.show();




        btnCancelEdit.setOnClickListener((new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        }));
        btnCopyPwd.setOnClickListener((new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                ClipData clippwd = ClipData.newPlainText("Password", password);
                clipboard.setPrimaryClip(clippwd);
                Toast.makeText(MainActivity.this, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }));
    }

    private static final String HMAC_SHA512 = "HmacSHA512";

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static String calculateHMAC(String data, String key)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
    {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA512);
        Mac mac = Mac.getInstance(HMAC_SHA512);
        mac.init(secretKeySpec);
        return toHexString(mac.doFinal(data.getBytes()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bottom_nav_drawer_menu, menu);
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void addPersonal(View view){
        Intent addpersonal = new Intent(MainActivity.this, addPersonal.class);
        addpersonal.putExtra("usrpassword", usrpwd);
        addpersonal.putExtra("usrtoken", token);
        addpersonal.putExtra("usremail", email);
        addpersonal.putExtra("usrname", name);
        MainActivity.this.startActivity(addpersonal);
    }
   /* public void encrypt(String time){
        Crypto banscrypt2 = new Crypto();
        String sha256hash=banscrypt2.toHexString(banscrypt2.getSHA(time));
        String secret=sha256hash.substring(32);
        //System.out.println(secret);
        //banscrypt.decrypt("theencpwd", secret);
    }*/
    class sendREQ extends AsyncTask<Void,Void,Void> {
        private String usrEmail;
        private String usrPWD;
        private String strurl;
        public void setParams(String txtUrl)
        {
            strurl=txtUrl;
        }
        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL url = new URL("https://api.jamiez.co.uk/hshsa/time");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                int status = httpURLConnection.getResponseCode();
                System.out.println(status);
                if(status!=500) {


                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                        String line;
                        String time="0";
                        while ((line = bufferedReader.readLine()) != null) {
                            // ... do something with line
                            System.out.println("beep: " + line);
                            time=line;
                        }


                    }
                } else {
                    Toast.makeText(MainActivity.this, "Personal Retrieval Failed.", Toast.LENGTH_LONG);
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
            }
            return null;
        }
    }


}
