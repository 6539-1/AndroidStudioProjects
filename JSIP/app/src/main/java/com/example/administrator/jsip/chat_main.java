package com.example.administrator.jsip;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.administrator.jsip.R;
import jsip_ua.SipProfile;
import jsip_ua.SipProfile;
import jsip_ua.impl.DeviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.LogRecord;

public class chat_main extends AppCompatActivity implements OnClickListener {
    private EditText editTextTo;
    private String ServiceIp = "sip:alice@192.168.43.73:5006";
    private EditText editTextMessage;
    private TextView textViewChat;
    private ListView listView;
    ArrayList<LocalMessage> rmessage = new ArrayList<>();
    private ChatMessageAdapter adapter = null;
    private List<String> items = new ArrayList<String>();
    private Handler MsgHandler;
    private String friendName;
    private Context ctn;
    private String Id;
    private InnerReceiver receiver = new InnerReceiver();
    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Id = getIntent().getStringExtra("Id");
        ctn= this;
        setContentView(R.layout.activity_chat_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
        // ini devl
        onRestart();
        Button btnSend = (Button) findViewById(R.id.btnSend);
        ImageButton sdButton = (ImageButton) findViewById(R.id.sdButton);
        btnSend.setOnClickListener(this);
        sdButton.setOnClickListener(this);
        //editTextTo = (EditText) findViewById(R.id.editTextTo);

        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        listView = (ListView) findViewById(android.R.id.list);
        adapter = new ChatMessageAdapter(chat_main.this, android.R.id.text1,
                items);
        listView.setAdapter(adapter);
        SQLManeger dbmanager = new SQLManeger(ctn,Id);
        //ArrayList<LocalMessage> getDblist= new ArrayList<>();
        ArrayList<LocalMessage> testList = new ArrayList<>();
        testList = dbmanager.Messagequery("p1992");//p1992 = friendname
        dbmanager.closeDatabase();
        historyMsg(testList);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
//        android.os.Handler msgHandler = new android.os.Handler(){
//            @Override
//            public void handleMessage(Message msg){
//                switch (msg.what){
//                    case 1:
//                        pushMessage((String)msg.obj);
//                        break;
//                }
//            }
//        };
       // DeviceImpl.getInstance().setHandler(msgHandler);      db
        friendName  = getIntent().getStringExtra("friendname");
        getSupportActionBar().setTitle(friendName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });



    }
    void historyMsg(ArrayList<LocalMessage> testList) {
        if (testList.size() >= 3) {
            for (int i = 0;i<3;i++)
            pushMessage((String) testList.get(testList.size() - 3+i).getContent());

        } else if (testList.size() < 3) {
            for (int i = 0; i < testList.size(); i++) {
                pushMessage((String) testList.get(i).getContent());
            }
        }
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
        else {
            finish();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.btnSend):
                int sentAll=getIntent().getIntExtra("sentAll",0);
                String add="$sent 123456 ";
                switch (sentAll) {
                    case 0:add="$sentall 666 ";
                        break;
                    case 1:add="$sent 654321 ";
                        break;
                    case 2:add="$sent 987654 ";
                        break;
                    case 3:
                        break;
                    case 4: add="$sent 123456 ";
                        break;
                    default:
                            break;
                }
                String mess =  add+ "20:20:20 " + editTextMessage.getText().toString() + " $end";

                DeviceImpl.getInstance().SendMessage(ServiceIp,mess);
                pushMessage("Me: " + editTextMessage.getText().toString());
                editTextMessage.setText("");
                editTextMessage.requestFocus();
                //DeviceImpl.getInstance().SendDTMF();
                break;
            case (R.id.sdButton):
                if (editTextMessage.isFocusable()){
                    editTextMessage.setFocusable(false);
                    editTextMessage.setFocusableInTouchMode(false);
                }
                else {
                    editTextMessage.setFocusable(true);
                    editTextMessage.setFocusableInTouchMode(true);
                }
        }
    }


    //存数据


    public void pushMessage(String readMessage) {
        adapter.add(readMessage);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //注册广播
        IntentFilter filter = new IntentFilter("com.app.test");
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
            String msg = intent.getStringExtra("message");
            System.out.println("bbbbbbbbb:"+msg);
            if (msg.equals("DATABASE_CHANGED")){
                SQLManeger dbmanager = new SQLManeger(ctn,Id);
                //ArrayList<LocalMessage> getDblist= new ArrayList<>();
                ArrayList<LocalMessage> testList = new ArrayList<>();
                testList = dbmanager.Messagequery("p1992");//p1992 = friendname
                pushMessage((String) testList.get(testList.size()-1).getContent());
                dbmanager.closeDatabase();
//                for (int i=0;i<testList.size();i++){
//
//                }
//
//            };
//            ArrayList rMessageList = new ArrayList();
            //rMessageList = getIntent().getStringArrayListExtra("messageList");

        }
    }


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

