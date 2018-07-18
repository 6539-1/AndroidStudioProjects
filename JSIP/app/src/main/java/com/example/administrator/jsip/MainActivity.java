package com.example.administrator.jsip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jsip_ua.SipProfile;
import jsip_ua.impl.DeviceImpl;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private List<message> msgList=new ArrayList<>();
    private String friendName;
    private ArrayList<String> rcvMsg=new ArrayList<>();
    private SipProfile sipProfile;
    private messageAdapter msgAdapter = null;
    private long firstTime = 0;
    private ArrayList<Friend> friendList=new ArrayList<>();
    SQLManeger sqlManeger;
    private InnerReceiver receiver = new InnerReceiver();
    private java.util.logging.Handler MsgHandler;
    private List<Integer> integerList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this,MyService.class));
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());

        final Toolbar toolbar;toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //inidl
        sipProfile = new SipProfile();
        HashMap<String, String> customHeaders = new HashMap<>();
        customHeaders.put("customHeader1","customValue1");
        customHeaders.put("customHeader2","customValue2");
        onRestart();
        DeviceImpl.getInstance().Initialize(getApplicationContext(), sipProfile,customHeaders);




        initFriend();
        sqlManeger=new SQLManeger(MainActivity.this);
        sqlManeger.add(friendList);
        sqlManeger.closeDatabase();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initMessage();


        msgAdapter=new messageAdapter(MainActivity.this,R.layout.id_list_name,msgList);
        ListView messageList=(ListView)findViewById(R.id.message_list);
        messageList.setAdapter(msgAdapter);
        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                message msg=msgList.get(position);
                friendName = msg.getId_name();
                Intent intent=new Intent(MainActivity.this,chat_main.class);

                int sentAll=position;
                intent.putExtra("sentAll",sentAll);
                intent.putExtra("friendname",friendName);
                intent.putStringArrayListExtra("messageList",rcvMsg);
                startActivity(intent);

            }
        });
    }
    private void initMessage() {

            if (rcvMsg.size()>0){
                message msg0=new message("6539-1聊天室",R.mipmap.pic6,"","");
                message msg1=new message("卢冬冬",R.mipmap.pic5,rcvMsg.get(rcvMsg.size()-1),"20:11");
                message msg2=new message("梁夏华",R.mipmap.pic2,"泡吧???","12:00");
                message msg3=new message("熊昊",R.mipmap.pic3,"[动画表情]","06:34");
                message msg4=new message("吴宏俊",R.mipmap.pic4,"好!","17:56");
                msgList.add(msg0);
                msgList.add(msg1);
                msgList.add(msg2);
                msgList.add(msg3);
                msgList.add(msg4);
            }
            else {
                message msg0=new message("6539-1聊天室",R.mipmap.pic6,"","");
                message msg1=new message("卢冬冬",R.mipmap.pic5,"","20:11");
                message msg2=new message("梁夏华",R.mipmap.pic2,"泡吧???","12:00");
                message msg3=new message("熊昊",R.mipmap.pic3,"[动画表情]","06:34");
                message msg4=new message("吴宏俊",R.mipmap.pic4,"好!","17:56");
                msgList.add(msg0);
                msgList.add(msg1);
                msgList.add(msg2);
                msgList.add(msg3);
                msgList.add(msg4);
            }


    }

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
            Toast.makeText(MainActivity.this, "再按2次退出程序", Toast.LENGTH_SHORT).show();
            System.exit(0);
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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            String[] friend_qunliao=null;
            for (int j=0;j<friendList.size();j++){
                friend_qunliao[j]=friendList.get(j).getName();
            }
            final String[] items=friend_qunliao;
            integerList = new ArrayList<>();
            AlertDialog dialog=new AlertDialog.Builder(this).setTitle("选择成员").setIcon(R.mipmap.pic1)
                    .setNegativeButton("取消",null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String hint="";
                            for (int j=0;j<integerList.size();j++){
                                hint=items[integerList.get(j)]+hint;
                            }
                            Toast.makeText(MainActivity.this, "已向"+hint+"发送请求", Toast.LENGTH_SHORT).show();
                        }
                    }).setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                            int a=i;
                            if (b){
                                integerList.add(a);
                            }
                            else {
                                if(integerList.size()>0) {
                                    integerList.remove(a);
                                }
                            }
                        }
                    }).create();
            dialog.show();
            return true;
        }
        if(id==R.id.action_add){
            Intent intent_add=new Intent(this,addfriends.class);
            startActivity(intent_add);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,FriendListView.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            View view=this.getLayoutInflater().inflate(R.layout.edit_msg,null);
            AlertDialog dialog_edit=new AlertDialog.Builder(this).setView(view)
                    .setNegativeButton("取消",null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText edit_nickName=(EditText)findViewById(R.id.edit_username);
                            EditText edit_ID=(EditText)findViewById(R.id.edit_ID);

                        }
                    }).create();
            dialog_edit.show();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //注册广播
        IntentFilter filter = new IntentFilter("test");
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
            System.out.println("aaaaaaaaa:"+msg);
            if (msg.equals("DATABASE_CHANGED")){
                switch (intent.getStringExtra("nickname")){
                    case "p1992":
                        String msg_last=intent.getStringExtra("message_last");
                        message newMsg=new message("卢冬冬",R.mipmap.pic5,msg_last,"");
                        msgList.set(1,newMsg);
                        msgAdapter.notifyDataSetChanged();
                        break;
                }
            };

            //pushMessage(msg);
            rcvMsg.add(msg);
            message newMsg = new message("卢冬冬",R.mipmap.pic5,rcvMsg.get(rcvMsg.size()-1),"20:11");
            msgList.set(0,newMsg);
        }
    }
    private void initFriend(){
        for (int i=0 ;i<3;i++){

            Friend xiahua=new Friend(100+i*10,"xiahua",R.mipmap.pic1,1);
            friendList.add(xiahua);
            Friend xionghao=new Friend(124+i*10,"xionghao",R.mipmap.pic2,2);
            friendList.add(xionghao);
            Friend hongjun=new Friend(125+i*10,"hongjun",R.mipmap.pic3,1);
            friendList.add(hongjun);
            Friend people1=new Friend(126+i*10,"people1",R.mipmap.pic4,0);
            friendList.add(people1);
            Friend people2=new Friend(127+i*10,"people2",R.mipmap.pic5,2);
            friendList.add(people2);
        }
    }

}
