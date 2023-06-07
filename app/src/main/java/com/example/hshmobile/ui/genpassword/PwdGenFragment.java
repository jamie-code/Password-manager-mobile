package com.example.hshmobile.ui.genpassword;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.hshmobile.R;

import java.util.Random;

public class PwdGenFragment extends Fragment {

    private PwdGenViewModel pwdGenViewModel;

    private Context mContext;

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
        pwdGenViewModel =
                ViewModelProviders.of(this).get(PwdGenViewModel.class);
        View root = inflater.inflate(R.layout.fragment_genpassword, container, false);
        //final TextView textView = root.findViewById(R.id.text_share);
        //pwdGenViewModel.getText().observe(this, new Observer<String>() {
      //      @Override
    //        public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
  //      });
        Button generatePassword = root.findViewById(R.id.btnGenPassword);
        final EditText txtLength = root.findViewById(R.id.txtLength);
        final CheckBox cboxupper = root.findViewById(R.id.cboxupper);
        final CheckBox cboxlower = root.findViewById(R.id.cboxlower);
        final CheckBox cboxnumber = root.findViewById(R.id.cboxnumber);
        final CheckBox cboxspecial = root.findViewById(R.id.cboxspecial);
        generatePassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!cboxupper.isChecked()&&!cboxlower.isChecked()&&!cboxnumber.isChecked()&&!cboxspecial.isChecked()) {
                    System.out.println("You must have at least one box ticked!");
                    Toast.makeText(mContext, "You must have at least one box ticked!", Toast.LENGTH_SHORT).show();
                } else if(txtLength.getText().toString().equals("")||txtLength.getText().toString().equals("0")){
                    System.out.println("Length cannot be less than 1.");
                    Toast.makeText(mContext, "Length cannot be less than 1.", Toast.LENGTH_SHORT).show();
                } else {
                    String genpass = generatePassword(Integer.parseInt(txtLength.getText().toString()), cboxupper.isChecked(), cboxlower.isChecked(), cboxnumber.isChecked(), cboxspecial.isChecked());

                    //System.out.println(genpass);
                    Toast.makeText(getActivity().getApplicationContext(), "Copied to clipboard.", Toast.LENGTH_SHORT).show();


                }
            }

        });
        final SeekBar sbpwd = root.findViewById(R.id.sbPwd);
        final EditText passlength = root.findViewById(R.id.txtLength);

        passlength.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    sbpwd.setProgress(Integer.parseInt(s.toString()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        sbpwd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                passlength.setText(Integer.toString(progress));
                passlength.setSelection(Integer.toString(progress).length());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return root;
    }
    public String generatePassword(int length, boolean uppercase, boolean lowercase, boolean numbers, boolean special){
        String upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseChars = "abcdefghijklmnopqrstuvwxyz";
        String numberChars = "0123456789";
        String specialChars = "!@#$%^&*";
        String allowedChars = "";

        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        if(uppercase){
            allowedChars += upperCaseChars;
            sb.append(upperCaseChars.charAt(rnd.nextInt(upperCaseChars.length()-1)));
        }
        if(lowercase){
            allowedChars += lowerCaseChars;
            sb.append(lowerCaseChars.charAt(rnd.nextInt(lowerCaseChars.length()-1)));
        }
        if(numbers){
            allowedChars += numberChars;
            sb.append(numberChars.charAt(rnd.nextInt(numberChars.length()-1)));
        }
        if(special){
            allowedChars += specialChars;
            sb.append(specialChars.charAt(rnd.nextInt(specialChars.length()-1)));
        }
        //Gen password with x length
        for(int i=sb.length(); i<length; ++i){
            sb.append(allowedChars.charAt(rnd.nextInt(allowedChars.length())));
        }
        return sb.toString();
    }
}