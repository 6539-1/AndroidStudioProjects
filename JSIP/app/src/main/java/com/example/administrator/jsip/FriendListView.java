package com.example.administrator.jsip;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FriendListView extends AppCompatActivity {
    private ArrayList<Friend> friendList2=new ArrayList<>();
    public int QL;
    SQLManeger sqlManeger;
    FriendArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list_view);
        QL=getIntent().getIntExtra("qunliao",0);
        boolean Tag=false;
        if(QL==1){
             Tag=true;
        }
        getSupportActionBar().setTitle("好友列表");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sqlManeger=new SQLManeger(FriendListView.this);
        friendList2=sqlManeger.query();
        FriendArrayAdapter adapter = new FriendArrayAdapter(FriendListView.this, R.layout.friend_item, friendList2,Tag);

        ListView listviews = (ListView) findViewById(R.id.list_views);
        listviews.setAdapter(adapter);
        listviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Friend friend=friendList2.get(position);
            Toast.makeText(FriendListView.this,friend.getName(),Toast.LENGTH_SHORT).show();
        }
    });
}


    @Override
    public void onBackPressed() {
        sqlManeger.closeDatabase();
        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return super.onOptionsItemSelected(item);
    }


}

