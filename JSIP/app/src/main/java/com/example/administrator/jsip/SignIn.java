package com.example.administrator.jsip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class SignIn extends AppCompatActivity {
    private ArrayList<String> AccountList;
    private Button signInBtn;
    private AcDropView AccountView;
    private EditText PasswordView;
    private CheckBox is_show_psw;
    private TextView signUpBtn;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        String i = Integer.toString(R.layout.sign_in);

        Log.d("TEST ID",i);
        setContentView(R.layout.sign_in);
        AccountView = findViewById(R.id.dropview);
        setAccountList();
        AccountView.setDefaultAccount(AccountList);
        PasswordView = findViewById(R.id.psd_view);
        signInBtn = findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String psw = PasswordView.getText().toString();
                Intent intent = new Intent(SignIn.this,MainActivity.class);
                startActivity(intent);
            }
        });
        is_show_psw = findViewById(R.id.is_show_psw);
        is_show_psw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean is_shown) {
                if (is_shown){
                    PasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    PasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        signUpBtn = findViewById(R.id.sign_up_btn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this,SignUp.class);
                startActivity(intent);
            }
        });
    }

    public void setAccountList() {
        AccountList=new ArrayList<String>();
        AccountList.add("13154658");
        AccountList.add("54952655");
        AccountList.add("79514665");

    }
}
