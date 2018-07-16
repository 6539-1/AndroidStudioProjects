package com.example.administrator.jsip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FriendArrayAdapter extends ArrayAdapter<Friend>{
    private int resourceId;
    public boolean Tag;

    public FriendArrayAdapter(Context context, int TextViewResourceId, List<Friend> objects){
        super(context,TextViewResourceId,objects);
        resourceId=TextViewResourceId;
    }

    public FriendArrayAdapter(Context context,int TextViewResourceId,List<Friend> objects,boolean Tag){
        super(context,TextViewResourceId,objects);
        resourceId=TextViewResourceId;
        this.Tag=Tag;
    }
    @Override
    public View getView(int position,View converView,ViewGroup parent){
        Friend friend=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(converView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.friendImage=(ImageView) view.findViewById(R.id.friend_image);
            viewHolder.friendName=(TextView) view.findViewById(R.id.friend_name);
            viewHolder.checkBox=(CheckBox)view.findViewById(R.id.checkbox);
            if (Tag) {
                viewHolder.checkBox.setVisibility(View.GONE);
            }
            view.setTag(viewHolder);
        }
        else {
            view=converView;
            viewHolder=(ViewHolder) view.getTag();
        }
        viewHolder.friendImage.setImageResource(friend.getImageId());
        viewHolder.friendName.setText(friend.getName());
        return view;
    }

    class ViewHolder{
        ImageView friendImage;
        TextView friendName;
        CheckBox checkBox;
    }
}
