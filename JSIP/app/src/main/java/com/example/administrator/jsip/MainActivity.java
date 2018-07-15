package com.example.administrator.jsip;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private List<message> msgList=new ArrayList<>();
    private long lastBack = 0;
    private ArrayList<Friend> friendList=new ArrayList<>();
    SQLManeger sqlManeger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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


        messageAdapter msgAdapter=new messageAdapter(MainActivity.this,R.layout.id_list_name,msgList);
        final ListView messageList=(ListView)findViewById(R.id.message_list);
        messageList.setAdapter(msgAdapter);
        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                message msg=msgList.get(position);
                Intent intent=new Intent(MainActivity.this,chat_main.class);
                startActivity(intent);

            }
        });
    }
    private void initMessage() {


            message msg1=new message("卢冬冬",R.mipmap.pic5,"[转账]你已确定收钱","20:11");
            message msg2=new message("梁夏华",R.mipmap.pic2,"泡吧???","12:00");
            message msg3=new message("熊昊",R.mipmap.pic3,"[动画表情]","06:34");
            message msg4=new message("吴宏俊",R.mipmap.pic4,"好!","17:56");
            msgList.add(msg1);
            msgList.add(msg2);
            msgList.add(msg3);
            msgList.add(msg4);


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

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (lastBack == 0 || System.currentTimeMillis() - lastBack > 800) {
            Toast.makeText(MainActivity.this, "再按一次返回退出程序", Toast.LENGTH_SHORT).show();
            lastBack = System.currentTimeMillis();
            return;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initFriend(){
        for (int i=0 ;i<3;i++){
            Friend xiahua=new Friend(100+i*10,"xiahua",R.mipmap.pic2,1);
            friendList.add(xiahua);
            Friend xionghao=new Friend(124+i*10,"xionghao",R.mipmap.pic3,2);
            friendList.add(xionghao);
            Friend hongjun=new Friend(125+i*10,"hongjun",R.mipmap.pic4,1);
            friendList.add(hongjun);
            Friend people1=new Friend(126+i*10,"people1",R.mipmap.pic1,0);
            friendList.add(people1);
            Friend people2=new Friend(127+i*10,"people2",R.mipmap.pic5,2);
            friendList.add(people2);
        }
    }
}
