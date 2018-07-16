package com.example.administrator.jsip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import java.util.logging.Handler;

import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.jsip.R;
import jsip_ua.SipProfile;
import jsip_ua.SipProfile;
import jsip_ua.impl.DeviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.LogRecord;

public class chat_main extends AppCompatActivity implements OnClickListener,
        OnSharedPreferenceChangeListener {
    private SharedPreferences prefs;
    private EditText editTextTo;
    private EditText editTextMessage;
    private TextView textViewChat;
    private SipProfile sipProfile;
    private ListView listView;
    private ChatMessageAdapter adapter = null;
    private List<String> items = new ArrayList<String>();
    private Handler MsgHandler;


    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
        getSupportActionBar().setTitle(getIntent().getStringExtra("friendname"));

        sipProfile = new SipProfile();
        HashMap<String, String> customHeaders = new HashMap<>();
        customHeaders.put("customHeader1","customValue1");
        customHeaders.put("customHeader2","customValue2");

        DeviceImpl.getInstance().Initialize(getApplicationContext(), sipProfile,customHeaders);

        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);

        editTextTo = (EditText) findViewById(R.id.editTextTo);
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        listView = (ListView) findViewById(android.R.id.list);
        adapter = new ChatMessageAdapter(chat_main.this, android.R.id.text1,
                items);
        listView.setAdapter(adapter);
        android.os.Handler msgHandler = new android.os.Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 1:
                        pushMessage((String)msg.obj);
                        break;
                }
            }
        };
        DeviceImpl.getInstance().setHandler(msgHandler);

        // ////////////////////////////////////////////////////////////

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // register preference change listener
        prefs.registerOnSharedPreferenceChangeListener(this);
        initializeSipFromPreferences();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.btnSend):
                DeviceImpl.getInstance().SendMessage(editTextTo.getText().toString(), editTextMessage.getText().toString() );
                pushMessage("Me: " + editTextMessage.getText().toString());
                editTextMessage.setText("");
                editTextMessage.requestFocus();
                //DeviceImpl.getInstance().SendDTMF();
                break;
        }
    }


    //存数据

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

    public void pushMessage(String readMessage) {
        adapter.add(readMessage);
        adapter.notifyDataSetChanged();
    }



    public class ChatMessageAdapter extends ArrayAdapter<String> {
        List<String> messages = null;
        LinearLayout leftlayout;
        LinearLayout rightlayout;
        TextView leftMsg;
        TextView rightMsg;
        View itemview;

        public ChatMessageAdapter(Context context, int textViewResourceId,
                                  List<String> items) {
            super(context, textViewResourceId, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.croom,parent,false);
            }
            String message = items.get(position);
            if (message != null && !message.isEmpty()) {
                leftlayout=(LinearLayout)v.findViewById(R.id.left_layout);
                rightlayout=(LinearLayout)v.findViewById(R.id.right_layout);
                leftMsg=(TextView)v.findViewById(R.id.left_msg);
                rightMsg=(TextView)v.findViewById(R.id.right_msg);

                if (leftMsg != null&&rightMsg!=null) {

                    if (message.startsWith("Me: ")) {
                        leftlayout.setVisibility(View.GONE);
                        rightlayout.setVisibility(View.VISIBLE);
                        rightMsg.setText(message.substring(3));
                    } else {
                        leftlayout.setVisibility(View.VISIBLE);
                        rightlayout.setVisibility(View.GONE);
                        leftMsg.setText(message);
                    }
                }
            }
            return v;
        }



    }

}

