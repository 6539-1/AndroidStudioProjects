package com.example.administrator.jsip;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import java.util.ArrayList;

public class imageDropView extends GridLayout {
    private Context context;
    private ArrayList<Integer> imageList;
    //private ImageView imageView;
    private ImageView choose_image;
    private PopupWindow imageWindow;

    private int[] arrImages = new int[]{
            R.mipmap.img1,R.mipmap.img2,R.mipmap.img3,
            R.mipmap.img4,R.mipmap.img5, R.mipmap.img6,
            R.mipmap.img7,R.mipmap.img8,R.mipmap.img9
    };
    /*
     * Constructor
     */
    public imageDropView(Context context){this(context,null);}

    public imageDropView(Context context, AttributeSet attrs){
        super(context,attrs);
        this.context = context;
        imageList = new ArrayList<Integer>();
        for (int i = 1;i<=9;i++){
            imageList.add(i);
        }
        initView();
    }

    public void initView(){
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.image_select_view,this);
        choose_image = findViewById(R.id.image_new);
        choose_image.setImageResource(R.mipmap.ic_launcher_round);
        choose_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageWindow == null) {
                    showPopupWindow();
                }else{
                    closePopupWindow();
                }
            }
        });
    }

    private void showPopupWindow(){
        View contentView = LayoutInflater.from(context).inflate(R.layout.gridlist,null,false);
        final GridView imageView = contentView.findViewById(R.id.gridlist);
        imageView.setAdapter(new GirdAdapter(context));
        imageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                choose_image.setImageResource(arrImages[i]);
                closePopupWindow();
                //这里添加后面的设置数据库头像
            }
        });
        imageWindow = new PopupWindow(contentView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,true);
        imageView.setBackground(ContextCompat.getDrawable(context,R.drawable.dropdownlist_bg));
        imageWindow.setTouchable(true);
        imageWindow.showAsDropDown(this);
        imageWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                closePopupWindow();
            }
        });
    }

    private void closePopupWindow(){
        imageWindow.dismiss();
        imageWindow = null;
    }
}
