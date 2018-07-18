package com.example.administrator.jsip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    private SignUp.InnerReceiver receiver = new SignUp.InnerReceiver();
    private String ServiceIp = "sip:alice@192.168.43.73:5006";
    Context context;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        onRestart();
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
    @Override
    protected void onRestart() {
        super.onRestart();
        //注册广播
        IntentFilter filter = new IntentFilter("com.app.deal_msg");
        registerReceiver(receiver, filter);
    }
    @Override
    protected void onStop() {
        super.onStop();
        //取消广播
        unregisterReceiver(receiver);
    }
    public class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //使用intent获取发送过来的数据
            boolean is_SignUp=intent.getBooleanExtra("reg",false);
            if(is_SignUp) {
                Intent intent_Id = new Intent(SignUp.this, MainActivity.class);
                //intent_Id.putExtra("Id", Id);
                startActivity(intent_Id);
            }
            else {
                Toast.makeText(SignUp.this,"注册失败", Toast.LENGTH_SHORT).show();
            }

        }
    }
}


