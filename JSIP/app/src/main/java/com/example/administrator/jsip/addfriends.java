package com.example.administrator.jsip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentManager;
import java.util.ArrayList;

import jsip_ua.impl.DeviceImpl;

public class addfriends extends AppCompatActivity {

    //private ArrayList<Friend> friendList2=new ArrayList<>();
    //SQLManeger sqlManeger;
    private String ServiceIp = "sip:alice@192.168.43.73:5006";
    private String[] mStrs={"123456","654321","987654","12345","65432","1345","65231","97654"};
    private ArrayAdapter adapter;
    private ArrayList<String>   msearchList=new ArrayList<>();
    private String ID;
    private ArrayList<String> userList=new ArrayList<>();
    private InnerReceiver receiver = new InnerReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends);
        getSupportActionBar().setTitle("添加好友");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final LinearLayout addLine=(LinearLayout)findViewById(R.id.addLine);
        addLine.setVisibility(View.GONE);
        final SearchView searchView=(SearchView)findViewById(R.id.search);
        searchView.setIconifiedByDefault(false);
        final ListView listView_add=(ListView)findViewById(R.id.list_add);
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userList);
        listView_add.setAdapter(adapter);
        listView_add.setTextFilterEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s)){
                        adapter.getFilter().filter(s);
                        searchItem(s);
                        //adapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, msearchList);
                        addLine.setVisibility(View.VISIBLE);
                        //adapter.notifyDataSetChanged();
                }
                else {
                        listView_add.clearTextFilter();
                        addLine.setVisibility(View.GONE);
                }
                return false;
            }
        });
        listView_add.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    ID=msearchList.get(position);
                    CharSequence Id=ID;
                    SearchView.SearchAutoComplete textView = ( SearchView.SearchAutoComplete)
                            searchView.findViewById(R.id.search_src_text);
                    textView.setText(Id);
                    String add="$add "+ID;
                    DeviceImpl.getInstance().SendMessage(ServiceIp,add);
                    Toast.makeText(addfriends.this, "好友申请已发送", Toast.LENGTH_SHORT).show();
                    /*if(is_add==1) {
                        Toast.makeText(addfriends.this, "好友："+ID + "添加成功", Toast.LENGTH_SHORT).show();
                    }
                    else if(is_add==0){
                        Toast.makeText(addfriends.this, "用户"+ID + "残忍拒绝了你", Toast.LENGTH_SHORT).show();
                    }*/
                }
            });

    }

    public void searchItem(String s) {
        msearchList=new ArrayList<>();
        for (int i=0;i<userList.size();i++){
            int index=userList.get(i).indexOf(s);
            if (index==0) {
                msearchList.add(userList.get(i));
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return super.onOptionsItemSelected(item);
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
            int is_add=intent.getIntExtra("add",-1);
            userList=intent.getStringArrayListExtra("userList");
            if(is_add==0){
                Toast.makeText(addfriends.this, "用户"+ID + "残忍拒绝了你", Toast.LENGTH_SHORT).show();
            }
            else if(is_add==1){
                Toast.makeText(addfriends.this, "好友："+ID + "添加成功", Toast.LENGTH_SHORT).show();
            }
            else if(is_add==2){

            }
        }

    }
   }
