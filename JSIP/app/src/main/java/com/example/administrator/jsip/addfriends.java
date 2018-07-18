package com.example.administrator.jsip;

import android.content.DialogInterface;
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

public class addfriends extends AppCompatActivity {

    //private ArrayList<Friend> friendList2=new ArrayList<>();
    //SQLManeger sqlManeger;
    private String[] mStrs={"123456","654321","987654","12345","65432","1345","65231","97654"};
    private ArrayAdapter adapter;
    private ArrayList<String>   msearchList=new ArrayList<>();
    private String ID;
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
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrs);
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
                    AlertDialog.Builder dialog=new AlertDialog.Builder(addfriends.this);
                    dialog.setTitle("好友请求");
                    dialog.setMessage("用户"+Id+"申请成为你的好友");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("接受", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Toast.makeText(addfriends.this,"接受好友请求", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                          Toast.makeText(addfriends.this,"拒绝好友请求", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.show();
                    //Toast.makeText(addfriends.this,ID+"添加成功", Toast.LENGTH_SHORT).show();
                }
            });

    }

    public void searchItem(String s) {
        msearchList=new ArrayList<>();
        for (int i=0;i<mStrs.length;i++){
            int index=mStrs[i].indexOf(s);
            if (index==0) {
                msearchList.add(mStrs[i]);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return super.onOptionsItemSelected(item);
    }
   }
