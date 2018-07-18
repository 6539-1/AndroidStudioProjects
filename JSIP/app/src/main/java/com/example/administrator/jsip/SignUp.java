package com.example.administrator.jsip;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.jsip.R;

import java.lang.reflect.Field;

import jsip_ua.impl.DeviceImpl;

public class SignUp extends AppCompatActivity {
    private String ServiceIp = "sip:alice@192.168.43.73:5006";
    private EditText newAccout,newNickName,newPsw;
    private Button SignUpBtn;
    private ImageView newImage;
    private String ImageId ="0";
    private Handler handler;
    private Message ImageMsg;
    Context context;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==1){
                    ImageId=msg.obj.toString();
                }
            }
        };

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
                String Account = newAccout.getText().toString();
                String psw = newPsw.getText().toString();
                String nickname = newNickName.getText().toString();
                //imageDropView DropView = findViewById(R.id.image_input);
                //DropView.initHandler(handler);
                //handler.handleMessage(ImageMsg);
                //handler.handleMessage(Message );
                String regMsg = "$reg "+ Account +" "+nickname+" "+ psw+" " +ImageId;
                DeviceImpl.getInstance().SendMessage(ServiceIp,regMsg);
                Toast.makeText(context,regMsg,Toast.LENGTH_LONG).show();
                //等待服务器响应
            }
        });
    }
}
