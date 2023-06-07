package com.example.hshmobile.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.hshmobile.ActionBottomDialogFragment;
import com.example.hshmobile.Crypto;
import com.example.hshmobile.MainActivity;
import com.example.hshmobile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private Context mContext;
    private String usrtoken;
    private String usrname;
    private String usremail;
    private String encpassword;
    private String selectedname;
    private String selectedurl;
    private String selecteduser;
    private String selectedpwd;
    private String selectedid;

    Crypto banscrypt = new Crypto();



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
       // final TextView textView = root.findViewById(R.id.text_home);
       // homeViewModel.getText().observe(this, new Observer<String>() {
       //     @Override
       //     public void onChanged(@Nullable String s) {
       //         textView.setText(s);
        //    }
        //});
        if (getArguments() != null) {
             encpassword = getArguments().getString("encpassword");
             usremail = getArguments().getString("usremail");
             usrname = getArguments().getString("usrname");
             usrtoken = getArguments().getString("usrtoken");
            MainActivity.setPwd(encpassword);
            MainActivity.setUserEmail(usremail);
            MainActivity.setUserName(usrname);
            MainActivity.setUserToken(usrtoken);
        } else {
            encpassword=MainActivity.getPwd();
            usremail=MainActivity.getUserEmail();
            usrname=MainActivity.getUserName();
            usrtoken=MainActivity.getUserToken();
        }

        final LinearLayout layout = (LinearLayout) root.findViewById(R.id.mainLayout);

        //Button btn = new Button(getActivity());
        //layout.addView(btn);







        try
        {
            String sha256hash=banscrypt.toHexString(banscrypt.getSHA(encpassword));
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
                    //HomeFragment.this.runOnUiThread(new Runnable() {
                    //    @Override
                    //    public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        //}
                    //});
                            System.out.println(myResponse);
                            try {
                                String hmac = MainActivity.calculateHMAC(myResponse, usrtoken);
                                System.out.println(hmac);

                                final MediaType JSON= MediaType.get("application/json; charset=utf-8");

                                OkHttpClient client2 = new OkHttpClient();
                                String url = "https://api.jamiez.co.uk/hshsa/view/personal";
                                //RequestBody body = RequestBody.create(JSON, "{'email':'boop','challenge':'1'}");
                                RequestBody body = new FormBody.Builder()
                                        .add("email", usremail)
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
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                               // }
                                            //});
                                            /*MainActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {*/

                                                    System.out.println(myResponse2);
                                                    JSONArray pwds;
                                                    try {
                                                        pwds = new JSONArray(myResponse2);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        pwds=new JSONArray();
                                                    }
                                                    //FragmentManager fm = getSupportFragmentManager();


                                                    //Fragment fragment = fm.findFragmentById(R.id.nav_host_fragment);
                                                    //TextView text = fragment.getView().findViewById(R.id.txthome);
                                                    //text.setText("Boopyboop");

                                                    //LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);

                                                    //set the properties for button
                                                    //LinearLayout lout = new LinearLayout(fragment.getContext());
                                                    //lout.setLayoutParams(new );


                                                    for (int i=0; i < pwds.length(); i++)
                                                    {
                                                        try {
                                                            JSONObject oneObject = pwds.getJSONObject(i);
                                                            // Pulling items from the array
                                                            String sha256hash=banscrypt.toHexString(banscrypt.getSHA(encpassword));
                                                            String secret=sha256hash.substring(32);

                                                            final String oneObjectsItem = oneObject.getString("name");
                                                            final String oneObjectsItem2 = oneObject.getString("pwd");
                                                            final String oneObjectsItem3 = oneObject.getString("url");
                                                            final String oneObjectsItem4 = oneObject.getString("user");
                                                            final String oneObjectsItem5 = oneObject.getString("id");
                                                            final String decname = banscrypt.decrypt(oneObjectsItem, secret);
                                                            final String decurl = banscrypt.decrypt(oneObjectsItem3, secret);

                                                            Button btnTag = new Button(getActivity());
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




        return root;
    }
    public void showBottomSheet(View view) {
        ActionBottomDialogFragment addPhotoBottomDialogFragment =
                ActionBottomDialogFragment.newInstance();
        addPhotoBottomDialogFragment.show(getFragmentManager(),
                ActionBottomDialogFragment.TAG);
    }

}