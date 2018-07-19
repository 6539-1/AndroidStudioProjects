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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jsip_ua.SipProfile;
import jsip_ua.impl.DeviceImpl;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    //private String ServiceIp = "sip:alice@192.168.43.73:5006";
    private String ServiceIp = "sip:alice@10.206.17.104:5006";
    private List<message> msgList=new ArrayList<>();
    private String friendName;
    private ArrayList<String> rcvMsg=new ArrayList<>();
    private messageAdapter msgAdapter = null;
    private ArrayList<Friend> friendList=new ArrayList<>();
    private InnerReceiver receiver = new InnerReceiver();
    private AceptReceiver receiver_acept=new AceptReceiver();
    private String Id;
    private long exitTime = 0;
    private int[] arrImages = new int[]{
            R.mipmap.img1,R.mipmap.img2,R.mipmap.img3,
            R.mipmap.img4,R.mipmap.img5, R.mipmap.img6,
            R.mipmap.img7,R.mipmap.img8,R.mipmap.img9
    };
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
        onRestart();

        final Toolbar toolbar;toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView img_owner=(ImageView)headerLayout.findViewById(R.id.image_ID);
        int pic=getIntent().getIntExtra("pic",-1);
        img_owner.setImageResource(arrImages[pic]);
        TextView id_owner=(TextView)headerLayout.findViewById(R.id.nickName);
        String nickname=getIntent().getStringExtra("nickName");
        id_owner.setText(nickname);

        friendList=SQLManeger.getSqlManeger().query(Id);

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
                intent.putExtra("user",msg.getId());
                intent.putExtra("friendname",friendName);
                intent.putExtra("Id",Id);

                //intent.putStringArrayListExtra("messageList",rcvMsg);
                intent.putStringArrayListExtra("messageList",rcvMsg);
                startActivity(intent);

            }
        });
//
        final SwipeRefreshLayout swipeRefreshView=(SwipeRefreshLayout)findViewById(R.id.Swip_container) ;
        swipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // TODO 获取数据
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initMessage();
                        msgAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();

                        // 加载完数据设置为不刷新状态，将下拉进度收起来
                        swipeRefreshView.setRefreshing(false);
                    }
                }, 1200);


                // 这个不能写在外边，不然会直接收起来
                //swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
    private void initMessage() {

        ArrayList<Friend> friends = SQLManeger.getSqlManeger().query(Id);
        ArrayList<Integer> g_id = SQLManeger.getSqlManeger().get_group_Id(Id);

        ArrayList<Integer> head = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> id = new ArrayList<>();
        ArrayList<String> message = new ArrayList<>();

        ArrayList<LocalMessage> MessageList = new ArrayList<>();
        LocalMessage newMsg;
        int tag=0;
        for (int i=0;i<friends.size();i++){
            if(SQLManeger.getSqlManeger().get_message_by_id(Integer.toString(friends.get(i).getID()),Id).size()!=0){
                newMsg=SQLManeger.getSqlManeger().get_message_by_id(Integer.toString(friends.get(i).getID()),Id).get(0);
                MessageList.add(newMsg);
            }
        }
        for (int i=0;i<MessageList.size();i++){
            id.add(MessageList.get(i).getOrigin_Id());
        }
        for (int i=0;i<id.size();i++){
                name.add(SQLManeger.getSqlManeger().getNickname(Id,id.get(i)));
                head.add(SQLManeger.getSqlManeger().getHead(Id,id.get(i)));
        }

        for (int i=0;i<id.size();i++) {
                msgList.add(new message(id.get(i),name.get(i),head.get(i),MessageList.get(i).getContent(),"1"));
        }
        head.clear();
        name.clear();
        MessageList.clear();
        for (int i=0;i<g_id.size();i++){
            msgList.add(new message(Integer.toString(g_id.get(i)),Integer.toString(g_id.get(i)),1,SQLManeger.getSqlManeger().get_one_message(Id,Integer.toString(g_id.get(i))),Integer.toString(2)));
        }
        //SQLManeger.getSqlManeger().closeDatabase();

    }

    @Override
    public void onBackPressed() {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // if (drawer.isDrawerOpen(GravityCompat.START)) {
            //  drawer.closeDrawer(GravityCompat.START);
        // } else {
            //super.onBackPressed();
        // }
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次注销账户", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            DeviceImpl.getInstance().SendMessage(ServiceIp,"$quit");
            Toast.makeText(getApplicationContext(), "注销账户", Toast.LENGTH_SHORT).show();
            finish();
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
            intent_add.putExtra("Id",Id);
            startActivity(intent_add);
            String person_list="$addall "+Id+" $end";
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
             DeviceImpl.getInstance().SendMessage(ServiceIp,"$quit");
             Toast.makeText(getApplicationContext(), "注销账户", Toast.LENGTH_SHORT).show();
             finish();
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

            int is_add=intent.getIntExtra("add",-1);
            if(is_add==2){
                final String id=intent.getStringExtra("id");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                AlertDialog dialog=builder.setTitle("好友请求").setMessage("用户:"+id+"申请成为你的好友")
                        .setCancelable(false).setPositiveButton("接受", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String select="$addsuccess "+id;
                                DeviceImpl.getInstance().SendMessage(ServiceIp,select);

                            }
                        }).setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String select="$addfailed "+id;
                                DeviceImpl.getInstance().SendMessage(ServiceIp,select);

                            }
                        }).create();
                dialog.show();
            }
        }
    }

}
