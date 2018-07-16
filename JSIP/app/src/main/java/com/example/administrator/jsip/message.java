package com.example.administrator.jsip;

import java.util.ArrayList;

public class message {
    private String id_name;
    private int imgId;
    private  String msg_last;
    private  String time;
    private  String addr;
    public message(String id_name,int imgId,String msg_last,String time){
        this.id_name=id_name;
        this.imgId=imgId;
        this.msg_last=msg_last;
        this.time=time;
    }
    public String getId_name(){
        return id_name;
    }
    public int getImgId(){
        return imgId;
    }
    public String getMsg_last(){return  msg_last;}

    public String getTime() {
        return time;
    }
    public String getAddr(){return addr;}
}
