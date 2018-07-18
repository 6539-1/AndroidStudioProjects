package com.example.administrator.jsip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Iterator;
import java.util.List;

import jsip_ua.SipProfile;
import jsip_ua.impl.DeviceImpl;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private List<message> msgList=new ArrayList<>();
    private String friendName;
    private ArrayList<String> rcvMsg=new ArrayList<>();
    private messageAdapter msgAdapter = null;
    private long lastBack = 0;
    private ArrayList<Friend> friendList=new ArrayList<>();
    SQLManeger sqlManeger;
    private InnerReceiver receiver = new InnerReceiver();
    private AceptReceiver receiver_acept=new AceptReceiver();
    private java.util.logging.Handler MsgHandler;
    private List<Integer> integerList = new ArrayList<>();
    private String Id;
    private String ServiceIp = "sip:alice@192.168.43.73:5006";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.Id = getIntent().getStringExtra("Id");
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

        onRestart();

        initFriend();
        //数据库
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
                intent.putExtra("Id",Id);
                //intent.putStringArrayListExtra("messageList",rcvMsg);
                intent.putStringArrayListExtra("messageList",rcvMsg);
                startActivity(intent);

            }
        });
//        android.os.Handler msgHandler = new android.os.Handler(){
//            @Override
//            public void handleMessage(Message msg){
//                switch (msg.what){
//                    case 1:
//                        rcvMsg.add((String)msg.obj);
//                        message newMsg = new message("卢冬冬",R.mipmap.pic5,rcvMsg.get(rcvMsg.size()-1),"20:11");
//                        msgList.set(0,newMsg);
//                        Intent intent = new Intent("test");
//                        intent.putExtra("message",(String)msg.obj);
//                        sendBroadcast(intent);
//                        break;
//                }
//            }
//        };
//        DeviceImpl.getInstance().setHandler(msgHandler);
        final SwipeRefreshLayout swipeRefreshView=(SwipeRefreshLayout)findViewById(R.id.Swip_container) ;
        swipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // 开始刷新，设置当前为刷新状态
                //swipeRefreshLayout.setRefreshing(true);

                // 这里是主线程
                // 一些比较耗时的操作，比如联网获取数据，需要放到子线程去执行
                // TODO 获取数据
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //message msg5=new message("王宇",R.mipmap.pic1,"我是泡吧王！","13:13");
                        refresh();
                        msgAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();

                        // 加载完数据设置为不刷新状态，将下拉进度收起来
                        swipeRefreshView.setRefreshing(false);
                    }
                }, 1200);

                // System.out.println(Thread.currentThread().getName());

                // 这个不能写在外边，不然会直接收起来
                //swipeRefreshLayout.setRefreshing(false);
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
    private void refresh(){

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            String[] friend_qunliao=new String[friendList.size()];
            final int[] friend_id=new int[friendList.size()];
            final boolean[] tag=new boolean[friendList.size()];
            for (int j=0;j<friendList.size();j++){
                friend_qunliao[j]=friendList.get(j).getName();
                friend_id[j]=friendList.get(j).getID();
                tag[j]=false;
            }
            final String[] items=friend_qunliao;
            AlertDialog dialog=new AlertDialog.Builder(this).setTitle("选择成员").setIcon(R.mipmap.pic1)
                    .setNegativeButton("取消",null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String hint="$creategroup ";
                            for (int j=0;j<friendList.size();j++){
                                if(tag[j])
                                    hint=hint+friend_id[j]+" ";
                            }
                            hint=hint+"$end";
                            DeviceImpl.getInstance().SendMessage(ServiceIp,hint);
                            Toast.makeText(MainActivity.this, "已发送请求", Toast.LENGTH_SHORT).show();
                        }
                    }).setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {

                                 tag[i]=b;

                        }
                    }).create();
            dialog.show();
            return true;
        }
        if(id==R.id.action_add){
            Intent intent_add=new Intent(this,addfriends.class);
            startActivity(intent_add);
            String person_list="$list "+Id+" $end";
            DeviceImpl.getInstance().SendMessage(ServiceIp,person_list);
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
            intent.putExtra("Id",Id);
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
        IntentFilter filter = new IntentFilter("com.app.test");
        registerReceiver(receiver, filter);
        IntentFilter filter_acept=new IntentFilter("com.app.deal_msg");
        registerReceiver(receiver_acept,filter_acept);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //取消广播
        unregisterReceiver(receiver);
        unregisterReceiver(receiver_acept);
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
    public class AceptReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String qunhao=intent.getStringExtra("qunhao");
            boolean is_qun=intent.getBooleanExtra("creategroup",false);
            if(is_qun){
                message newQun=new message(qunhao,R.mipmap.pic6,"","");
                msgList.add(newQun);
                msgAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,"创建成功", Toast.LENGTH_SHORT).show();
            }
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
