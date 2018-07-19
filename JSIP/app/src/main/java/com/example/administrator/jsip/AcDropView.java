package com.example.administrator.jsip;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class AcDropView extends LinearLayout {
    private Context context;
    private EditText AccountView;
    private ImageView DropBtn;
    private PopupWindow popupWindow = null;
    private ArrayList<String> AccountList;
    private Handler mHandler;
    private AccountAdapter mAdapter;
    public AcDropView(Context context){
        this(context,null);
    }

    public AcDropView(Context context, AttributeSet attrs){
        //this(context,attrs,null);
        super(context,attrs);
        this.context=context;
        initView();
    }

   // public AcDropView(Context context, AttributeSet attrs,)
    public void initView(){
        String infServie = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(infServie);
        View view = layoutInflater.inflate(R.layout.dropdownview,this);
        AccountView = (EditText)findViewById(R.id.Account);
        //AccountView.setText("123");
        DropBtn = (ImageView)findViewById(R.id.dropdownBtn);
        DropBtn.setImageResource(R.mipmap.dropdownbtn_down);
        view.invalidate();

        mHandler = new Handler() {
            @Override
            public void publish(LogRecord logRecord) {

            }

            @Override
            public void flush() {
                mAdapter.notifyDataSetChanged();
                View mview = findViewById(R.id.dropdownBtn);
                closePopupWindow(mview);
            }

            @Override
            public void close() throws SecurityException {

            }
        };

        DropBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccountList!=null) {
                    if (popupWindow == null) {
                        showPopupWindow();
                        Log.d("init:", "init window by if(true)");
                    } else {
                        View mview = findViewById(R.id.dropdownBtn);
                        closePopupWindow(mview);
                        Log.d("drop", "drop window by if(false)");
                    }
                }
            }

        });
    }

    private void showPopupWindow(){
        View contentView = LayoutInflater.from(context).inflate(R.layout.dropdownlist,null,false);
        RecyclerView listView = (RecyclerView)contentView.findViewById(R.id.dropdown_list);
        listView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        listView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        mAdapter = new AccountAdapter(context,AccountList,AccountView);
        mAdapter.initHandler(mHandler);
        listView.setAdapter(mAdapter);
        popupWindow = new PopupWindow(contentView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,true);

        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.dropdownlist_bg));
        popupWindow.setTouchable(true);
        popupWindow.showAsDropDown(this);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                View mview = findViewById(R.id.dropdownBtn);
                closePopupWindow(mview);
            }
        });
        DropBtn.setImageResource(R.mipmap.dropdownbtn_up);
        //popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
    }

    private void closePopupWindow(View view){
        popupWindow.dismiss();
        popupWindow = null;

        DropBtn.setImageResource(R.mipmap.dropdownbtn_down);
        view.invalidate();
        Log.d("drop","drop window by listener");
    }


    public void setDefaultAccount(ArrayList<String> list){
        AccountList = list;
        AccountView.setText(list.get(0).toString());

    }

    public int getLayoutWidth(){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }


}
