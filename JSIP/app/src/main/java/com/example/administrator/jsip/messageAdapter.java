package com.example.administrator.jsip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class messageAdapter extends ArrayAdapter<message> {
    private int resourceId;
    public messageAdapter(Context context, int textViewResourceId, List<message> object){
        super(context,textViewResourceId,object);
        resourceId=textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        message msg=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ImageView img_id=(ImageView)view.findViewById(R.id.img_id);
        TextView name_id=(TextView)view.findViewById(R.id.name_id);
        TextView msg_last=(TextView)view.findViewById(R.id.msg_last) ;
        TextView time_last=(TextView)view.findViewById(R.id.time_last);
        img_id.setImageResource(msg.getImgId());
        name_id.setText(msg.getId_name());
        msg_last.setText(msg.getMsg_last());
        time_last.setText(msg.getTime());
        return view;
    }
}