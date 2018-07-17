package com.example.administrator.jsip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.administrator.jsip.R;

public class GirdAdapter extends BaseAdapter{
    private Context context;
    private LayoutInflater layoutInflater;
    //private ArrayList<Integer> pictures;
    private int[] arrImages = new int[]{
            R.mipmap.img1,R.mipmap.img2,R.mipmap.img3,
            R.mipmap.img4,R.mipmap.img5, R.mipmap.img6,
            R.mipmap.img7,R.mipmap.img8,R.mipmap.img9
    };

    public GirdAdapter(Context context){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public Object getItem(int position) {
        return arrImages[position];
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getCount() {
        return arrImages.length;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent){
        ImageView imageView;
        View view = layoutInflater.inflate(R.layout.image_item,null);
        imageView = view.findViewById(R.id.image_select);
        imageView.setImageResource(arrImages[position]);
        return view;
    }


}
