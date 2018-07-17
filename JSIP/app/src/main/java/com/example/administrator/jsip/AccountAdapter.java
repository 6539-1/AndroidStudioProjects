package com.example.administrator.jsip;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.logging.Handler;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private ArrayList<String> mAccountList;
    private Context mContext;
    //private OnItemClickListener mOnItemClickListener;
    final private TextView Account_View;
    private Handler mHandler;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView Account;
        ImageView DeleteBtn;
        View DropListView;
        public ViewHolder(View view) {
            super(view);
            Account = (TextView)view.findViewById(R.id.item_text);
            DeleteBtn = (ImageView)view.findViewById(R.id.delete_btn);
            DropListView=view;
        }
    }

    public AccountAdapter(Context context,ArrayList<String> AccountList,TextView textView){
        mAccountList=AccountList;
        this.mContext=context;
        Account_View = textView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = View.inflate(mContext, R.layout.dropdown_list_item,null);

        final ViewHolder holder = new ViewHolder(view);

        holder.Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                String mAccount = mAccountList.get(position);
                Account_View.setText(mAccount);
                //此处后面应改为对数据库的操作
                mAccountList.set(0,mAccountList.set(position,mAccountList.get(0)));
                if (mHandler!=null){
                    mHandler.flush();
                }
                //this.notifyAll();
            }
        });
        holder.DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                mAccountList.remove(position);
                if (mHandler!=null){
                    mHandler.flush();
                }
                //this.notifyAll();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        String account = mAccountList.get(position);
        holder.Account.setText(account);
        //holder.DeleteBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.DeleteBtn.setImageResource(R.mipmap.delete_btn);

    }

    @Override
    public int getItemCount(){
        return mAccountList.size();
    }

    public void initHandler(Handler itemListHandler){
        this.mHandler = itemListHandler;
    }
/*
    public interface OnItemClickListener{
        void OnClick(int position);
        void OnLongClick(int position);
    }

    public void setOnitemClickListener(OnItemClickListener onitemClickListener){
        this.mOnItemClickListener = onitemClickListener;
    }

    */
}
