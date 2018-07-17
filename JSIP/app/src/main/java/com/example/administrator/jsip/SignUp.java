package com.example.administrator.jsip;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.jsip.R;

public class SignUp extends AppCompatActivity {
    private EditText newAccout,newNickName,newPsw;
    private Button SignUpBtn;
    private ImageView newImage;
    Context context;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        context = this;
        newAccout = findViewById(R.id.accout_new);
        newNickName = findViewById(R.id.nickname_new);
        newPsw = findViewById(R.id.psw_new);
        SignUpBtn = findViewById(R.id.sign_up);
        newImage = findViewById(R.id.image_new);

        newAccout.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(4)
        });
        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"注册成功",Toast.LENGTH_LONG).show();
            }
        });
    }
}
