package com.example.administrator.jsip;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import jsip_ua.SipProfile;
import jsip_ua.impl.DeviceImpl;

public class SignIn extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private String ServiceIp = "sip:alice@192.168.43.73:5006";
    private ArrayList<String> AccountList;
    private Button signInBtn;
    private SipProfile sipProfile;
    private AcDropView AccountView;
    private EditText PasswordView;
    private CheckBox is_show_psw;
    private TextView signUpBtn;
    private SQLManeger signInSQL;
    private SharedPreferences prefs;
    private ArrayList<Personal> personals;
    private String Id;
    private SignIn.InnerReceiver receiver = new SignIn.InnerReceiver();
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //启动服务
        startService(new Intent(this,MyService.class));
        signInSQL = new SQLManeger(this);
        personals=signInSQL.Personalquery();
        //生成界面
        onRestart();
        setContentView(R.layout.sign_in);
        AccountView = findViewById(R.id.dropview);
        setAccountList();
        if(AccountList.size()!=0) {
            AccountView.setDefaultAccount(AccountList);
        }
        PasswordView = findViewById(R.id.psd_view);
        signInBtn = findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String psw = PasswordView.getText().toString();
                TextView Accout = findViewById(R.id.Account);
                String thisId = Accout.getText().toString();
                String SignInMsg = "$log"+thisId+psw;
                DeviceImpl.getInstance().SendMessage(ServiceIp,SignInMsg);
                //这里应该挂起等待服务器应答
                Intent intent = new Intent(SignIn.this,MainActivity.class);
                intent.putExtra("Id",Id);
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

        /*
        * 初始化发送方法
        * */
        sipProfile = new SipProfile();
        HashMap<String, String> customHeaders = new HashMap<>();
        customHeaders.put("customHeader1","customValue1");
        customHeaders.put("customHeader2","customValue2");
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // register preference change listener
        prefs.registerOnSharedPreferenceChangeListener(this);
        initializeSipFromPreferences();
        DeviceImpl.getInstance().Initialize(getApplicationContext(), sipProfile,customHeaders);

    }

    public void setAccountList() {
        AccountList=new ArrayList<String>();
        if(personals!=null) {
            for (int i = 0; i < personals.size(); i++) {
                AccountList.add(Integer.toString(personals.get(i).getId()));
            }
        }
        AccountList.add("123456");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals("pref_proxy_ip")) {
            sipProfile.setRemoteIp((prefs.getString("pref_proxy_ip", "10.28.144.154")));
        } else if (key.equals("pref_proxy_port")) {
            sipProfile.setRemotePort(Integer.parseInt(prefs.getString(
                    "pref_proxy_port", "5060")));
        }  else if (key.equals("pref_sip_user")) {
            sipProfile.setSipUserName(prefs.getString("pref_sip_user",
                    "alice"));
        } else if (key.equals("pref_sip_password")) {
            sipProfile.setSipPassword(prefs.getString("pref_sip_password",
                    "1234"));
        }

    }

    @SuppressWarnings("static-access")
    private void initializeSipFromPreferences() {
        sipProfile.setRemoteIp((prefs.getString("pref_proxy_ip", "127.0.0.1")));
        sipProfile.setRemotePort(Integer.parseInt(prefs.getString(
                "pref_proxy_port", "5050")));
        sipProfile.setSipUserName(prefs.getString("pref_sip_user", "alice"));
        sipProfile.setSipPassword(prefs
                .getString("pref_sip_password", "1234"));

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
            int is_SignIn = intent.getIntExtra("log",-1);
            if(is_SignIn==0) {
                Intent intent_Id = new Intent(SignIn.this, MainActivity.class);
                intent_Id.putExtra("Id", Id);
                startActivity(intent_Id);
            }
            else if(is_SignIn==1){
                Toast.makeText(SignIn.this,"账号有误", Toast.LENGTH_SHORT).show();
            }
            else if(is_SignIn==2){
                Toast.makeText(SignIn.this,"密码有误", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
