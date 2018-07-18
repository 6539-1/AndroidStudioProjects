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
    private String Id;
    SQLManeger sqlManeger;
    FriendArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list_view);
        Id=getIntent().getStringExtra("Id");
        getSupportActionBar().setTitle("好友列表");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sqlManeger=new SQLManeger(FriendListView.this);
        friendList2=sqlManeger.query(Id);
        sqlManeger.closeDatabase();
        FriendArrayAdapter adapter = new FriendArrayAdapter(FriendListView.this, R.layout.friend_item, friendList2);

        ListView listviews = (ListView) findViewById(R.id.list_views);
        listviews.setAdapter(adapter);
        listviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Friend friend=friendList2.get(position);
            String ID=Integer.toString(friend.getID());
            Intent intent=new Intent(FriendListView.this,chat_main.class);
            intent.putExtra("id_to",ID);
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

