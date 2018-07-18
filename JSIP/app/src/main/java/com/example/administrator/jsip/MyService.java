package com.example.administrator.jsip;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import jsip_ua.SipProfile;
import jsip_ua.SipUADeviceListener;
import jsip_ua.impl.DeviceImpl;
import jsip_ua.impl.SipEvent;

public class MyService extends Service implements SipUADeviceListener {
    private String ServiceIp = "sip:alice@192.168.43.73:5006";
    SipProfile sipProfile;
    String reciveMessage;
    String Id;
    Handler mHandler;
    ArrayList<LocalMessage> rmessage = new ArrayList<>();
    SharedPreferences prefs;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sipProfile = new SipProfile();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        sipProfile.setSipUserName(prefs.getString("pref_sip_user", "alice"));
        sipProfile.setLocalPort(5050);
        DeviceImpl.getInstance().sipuaDeviceListener = this;
        DeviceImpl.getInstance().Initialize(getApplicationContext(), sipProfile);
        return START_STICKY;
    }

    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSipUAConnectionArrived(SipEvent event) {

    }

    @Override
    public void onSipUAMessageArrived(SipEvent event) {
        String msg = event.content;
        setReciveMessage(msg);
        deal(msg);
    }
    public void setReciveMessage(String msg){
        this.reciveMessage = msg;
    }
    public String getReciveMessage(){
        return reciveMessage;
    }

    public void deal(String rmessage){
        String M[]=rmessage.split(" ");
        switch(M[0]){
            case "$reg":{
                if (M[1].equals("success"))
                    this.Id=M[2];//注册成功
                else
                    ;//注册失败
                break;
            }
            case "log":{
                switch(M[1]){
                    case "error1":{//账号有误
                        break;
                    }
                    case "error2": {//密码有误
                        break;
                    }
                    case "success":{//登录成功
                        String name=M[2];
                        int head=Integer.valueOf(M[3]).intValue();
                        this.Id=M[4];
                    }
                        break;
                    default:
                        break;
                }
                break;
            }
            case "add":{
                switch(M[1]) {
                    case "none": {//查无此人
                        break;
                    }
                    case "error": {//拒绝
                        String id = M[2];
                        String msg = "用户"+id+"拒绝你的好友申请";
                        SQLManeger sqlManeger = new SQLManeger(this);
                        sqlManeger.addSystem(msg,Id);
                        sqlManeger.closeDatabase();
                        break;
                    }
                    case "success": {//申请成功
                        String id = M[2];
                        String msg = "用户"+id+"同意你的好友申请，现在开始愉快地聊天吧";
                        SQLManeger sqlManeger = new SQLManeger(this);
                        sqlManeger.addSystem(msg,Id);
                        sqlManeger.closeDatabase();
                        break;
                    }
                    default:{
                        String id = M[1];
                        String name = M[2];
                        String msg = "用户"+name+"("+id+")"+"申请加为好友";//请求申请
                        SQLManeger sqlManeger = new SQLManeger(this);
                        sqlManeger.addSystem(msg,Id);
                        sqlManeger.closeDatabase();
                        break;
                    }
                }
            }
            case "$sent": {
                String id = M[1];
                String content = M[2];
                for (int i = 3;;i++){
                    if (M[i].equals("$end")){
                        break;
                    }
                    content+=" "+M[i];
                }
                SQLManeger sqlManeger = new SQLManeger(this);
                LocalMessage lmsg = new LocalMessage(sqlManeger.getNickname(Id,id),content,0,0,Id,id);
                sqlManeger.addMessage(lmsg,Id);
                sqlManeger.closeDatabase();
                break;
            }
            case "$sentall":{
                String g_id = M[1];
                String id = M[2];
                String content = M[3];
                for (int i = 4;;i++){
                    if (M[i].equals("$end")){
                        break;
                    }
                    content+=" "+M[i];
                }
                SQLManeger sqlManeger = new SQLManeger(this);
                LocalMessage lmsg = new LocalMessage(sqlManeger.getNickname(Id,id),content,0,0,Id,g_id);
                sqlManeger.addMessage(lmsg,Id);
                sqlManeger.closeDatabase();
                break;
            }
            case "$flush":{
                List<Friend> friendList= new ArrayList<>();
                int i=1;
                for(i=1;;i=i+4){
                    if(M[i].equals("$group"))
                        break;
                    Friend friend=new Friend(
                            Integer.valueOf(M[i]).intValue(),
                            M[i+1],
                            Integer.valueOf(M[i+2]).intValue(),
                            Integer.valueOf(M[i+3]).intValue()
                    );
                    friendList.add(friend);
                }
                for(i=1;;i++){
                    if(M[i].equals("$end"))
                        break;
                    Friend friend=new Friend(
                            Integer.valueOf(M[i]).intValue(),
                            "   ",
                            0,
                            2
                    );
                    friendList.add(friend);
                }
                SQLManeger sqlManeger = new SQLManeger(this);
                sqlManeger.add(friendList,Id);
                sqlManeger.closeDatabase();
                break;
            }
            case "$creategroup":{    // 创群成功
                DeviceImpl.getInstance().SendMessage(ServiceIp,"$flush");
                break;
            }

        }

    }

    public void setId(String Id){
        this.Id = Id;
    }
}
