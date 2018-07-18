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
            view.setTag(viewHolder);
        }
        else {
            view=converView;
            viewHolder=(ViewHolder) view.getTag();
        }
        switch (friend.getImageId()){
            case 1:
                viewHolder.friendImage.setImageResource(R.mipmap.pic5);
                break;
            case 2:
                viewHolder.friendImage.setImageResource(R.mipmap.pic2);
                break;
            case 3:
                viewHolder.friendImage.setImageResource(R.mipmap.pic3);
                break;
            case 4:
                viewHolder.friendImage.setImageResource(R.mipmap.pic4);
                 break;
            default:
                viewHolder.friendImage.setImageResource(R.mipmap.pic1);
                 break;
        }
        viewHolder.friendName.setText(friend.getName());
        return view;
    }

    class ViewHolder{
        ImageView friendImage;
        TextView friendName;
        CheckBox checkBox;
    }
}
