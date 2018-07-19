package com.example.administrator.jsip;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;

import java.io.File;
import java.util.logging.Handler;

import android.os.Environment;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private String ServiceIp = "sip:server@10.206.17.100:5050";
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
    private String sent;
    private InnerReceiver receiver = new InnerReceiver();
    private ArrayAdapter<Recorder> mAdapter;
    private ArrayList<Recorder> mDatas =new ArrayList<>();
    private AudioRecorderButton mAudioRecorderButton = null;
    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Id = getIntent().getStringExtra("Id");
        sent=getIntent().getStringExtra("user");
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
        recyclerView = (RecyclerView) findViewById(R.id.rcyc_list);
        LinearLayoutManager linearLayout1 = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayout1);
        adapter = new ChatAdapter(chat_main.this,mDatas);
        recyclerView.setAdapter(adapter);
        Button btnSend = (Button) findViewById(R.id.btnSend);
        ImageButton sdButton = (ImageButton) findViewById(R.id.sdButton);
        mAudioRecorderButton=findViewById(R.id.adButton);
        btnSend.setOnClickListener(this);
        sdButton.setOnClickListener(this);
        mAudioRecorderButton.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                System.out.println("aaaaaaaaaaaaaaa: "+filePath);
                File fileTosent = new File(filePath);
                Log.i("chatmain file name", fileTosent.getName());
                FileTransfer fs = new FileTransfer(fileTosent);
                String bstypeFile = fs.getBasedFile();
                Log.i("99999999999", bstypeFile);
                DeviceImpl.getInstance().SendMessage(ServiceIp, "$sentv 1111 2222 "+filePath+" "+bstypeFile+" $end");
                Recorder recorder = new Recorder(seconds,filePath,null);
                pushMessage(recorder);
            }
        });
        //audiobutton

        //editTextTo = (EditText) findViewById(R.id.editTextTo);

        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        listView = (ListView) findViewById(android.R.id.list);
        adapter = new ChatMessageAdapter(chat_main.this, android.R.id.text1,
                items);
        listView.setAdapter(adapter);
        /*
        SQLManeger dbmanager = new SQLManeger(ctn);
        //ArrayList<LocalMessage> getDblist= new ArrayList<>();
        ArrayList<LocalMessage> testList = new ArrayList<>();

        testList = dbmanager.Messagequery("p1992");//p1992 = friendname

        dbmanager.closeDatabase();
        historyMsg(testList);
        */
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
       // DeviceImpl.getInstance().setHandler(msgHandler);
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
        String textMessage = null ;
        Recorder mulMessage = null;
        if (testList.size() >= 3) {
            for (int i = 0;i<3;i++)
                textMessage = (String) testList.get(testList.size() - 3+i).getContent();
                mulMessage = new Recorder(0,null,textMessage);
            pushMessage(mulMessage);

        } else if (testList.size() < 3) {
            for (int i = 0; i < testList.size(); i++) {
                textMessage = (String) testList.get(i).getContent();
                mulMessage = new Recorder(0,null,textMessage);
                pushMessage(mulMessage);
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
                int user=Integer.parseInt(sent);
                String add;
                if (user<10000)
                    add="$sentall ";
                else add="$sent ";
                String mess =  add + user +" "+ editTextMessage.getText().toString() + " $end";
                DeviceImpl.getInstance().SendMessage(ServiceIp,mess);
                Recorder mulMessage = new Recorder(0,null,"Me: " + editTextMessage.getText().toString());
                pushMessage(mulMessage);
                editTextMessage.setText("");
                editTextMessage.requestFocus();
                //DeviceImpl.getInstance().SendDTMF();
                break;
            case (R.id.sdButton):

        }
    }


    //存数据


    public void pushMessage(Recorder mulMessage) {
        int pstion = adapter.getItemCount();
        adapter.notifyItemInserted(pstion+1);
        mDatas.add(pstion,mulMessage);
        adapter.notifyDataSetChanged();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //使用intent获取发送过来的数据
            String msg = intent.getStringExtra("sent");
            if (msg.equals(sent)) {
                String Message=SQLManeger.getSqlManeger().get_one_message(Id,msg);
                pushMessage(Message);
                if(testList.get(testList.size()-1).getState()==1){
                    Recorder mul=new Recorder(0,null,(String) testList.get(testList.size()-1).getContent());
                    pushMessage(mul);
                }
                else if (testList.get(testList.size()-1).getState()==2){
                    System.out.println("this is fpath in Chatmain-------------------: "+testList.get(testList.size()-1).getContent());
                    Recorder mul = new Recorder(0,testList.get(testList.size()-1).getContent(),null);
                    pushMessage(mul);
                }
                //SQLManeger.getSqlManeger().closeDatabase();
            }
        }
    }
    class Recorder{

        float time;
        String filePath;
        String rcvMessage;

        public String getRcvMessage(){
            return rcvMessage;
        }
        public void setRcvMessage(String msg){
            this.rcvMessage = msg;
        }
        public float getTime() {
            return time;
        }

        public void setTime(float time) {
            this.time = time;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public Recorder(float time, String filePath,String msg) {
            super();
            this.time = time;
            this.filePath = filePath;
            this.rcvMessage = msg;
        }
    }


}

