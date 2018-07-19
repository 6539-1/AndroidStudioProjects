package com.example.administrator.jsip;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import jsip_ua.SipProfile;
import jsip_ua.SipUADeviceListener;
import jsip_ua.impl.DeviceImpl;
import jsip_ua.impl.SipEvent;

public class MyService extends Service implements SipUADeviceListener {
    SipProfile sipProfile;
    String reciveMessage;
    String Id;
    Handler mHandler;
    ArrayList<LocalMessage> rmessage = new ArrayList<>();
    SharedPreferences prefs;
    //private String ServiceIp = "sip:alice@10.206.17.104:5006";
    private String ServiceIp = "sip:alice@192.168.43.73:5006";
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
        deal(msg);
    }

    public void deal(String rmessage){
        Intent intent_deal=new Intent("com.app.deal_msg");
        String M[]=rmessage.split(" ");
        switch(M[0]){
            case "$reg":{
                if (M[1].equals("success")) {
                    this.Id=M[2];
                    //SQLManeger sqlManeger = new SQLManeger(this);
                    SQLManeger.getSqlManeger().CreateTable(Id);
                    //SQLManeger.getSqlManeger().closeDatabase();
                    intent_deal.putExtra("reg", true);
                }
                else {
                    intent_deal.putExtra("reg",false);
                }
            }
            case "$log":{
                switch(M[1]){
                    case "error1":{//账号有误
                        intent_deal.putExtra("log",1);
                        break;
                    }
                    case "error2": {//密码有误
                        intent_deal.putExtra("log",2);
                        break;
                    }
                    case "success":{//登录成功
                        String name=M[2];
                        int head=Integer.valueOf(M[3]).intValue();
                        this.Id=M[4];
                        //SQLManeger sqlManeger = new SQLManeger(this);
                        SQLManeger.getSqlManeger().CreateTable(Id);
                        //SQLManeger.getSqlManeger().closeDatabase();
                        intent_deal.putExtra("log",0);
                    }
                        break;
                    default:
                        break;
                }
                break;
            }
            case "$addall":{
                ArrayList<String> userlist=new ArrayList<>();
                for(int i=1 ;;i++) {
                    if (M[i].equals("$end"))
                        break;
                    userlist.add(M[i]);
                }
                intent_deal.putExtra("userList",userlist);
                break;

            }
            case "$add":{
                switch(M[1]) {

                    case "error2": {//拒绝
                        String id = M[2];
                        intent_deal.putExtra("add",0);
                        String msg = "用户"+id+"拒绝你的好友申请";
                        //SQLManeger sqlManeger = new SQLManeger(this);
                        SQLManeger.getSqlManeger().addSystem(msg,Id);
                        //SQLManeger.getSqlManeger().closeDatabase();
                        break;
                    }
                    case "success": {//申请成功
                        String id = M[2];
                        intent_deal.putExtra("add",1);
                        String msg = "用户"+id+"同意你的好友申请，现在开始愉快地聊天吧";
                        //SQLManeger sqlManeger = new SQLManeger(this);
                        SQLManeger.getSqlManeger().addSystem(msg,Id);
                        //SQLManeger.getSqlManeger().closeDatabase();
                        break;
                    }
                    default:{
                        String id = M[1];
                        String name = M[2];
                        intent_deal.putExtra("id",id);
                        intent_deal.putExtra("add",2);
                        String msg = "用户"+name+"("+id+")"+"申请加为好友";//请求申请
                        //SQLManeger sqlManeger = new SQLManeger(this);
                        SQLManeger.getSqlManeger().addSystem(msg,Id);
                        //SQLManeger.getSqlManeger().closeDatabase();
                        //请求申请
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
                //SQLManeger sqlManeger = new SQLManeger(this);
                LocalMessage lmsg = new LocalMessage(SQLManeger.getSqlManeger().getNickname(Id,id),content,0,0,id,Id);
                SQLManeger.getSqlManeger().addMessage(lmsg,Id);
                //SQLManeger.getSqlManeger().closeDatabase();
                intent_deal.putExtra("sent",id);
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
                //SQLManeger sqlManeger = new SQLManeger(this);
                LocalMessage lmsg = new LocalMessage(SQLManeger.getSqlManeger().getNickname(Id,id),content,0,0,g_id,Id);
                SQLManeger.getSqlManeger().addMessage(lmsg,Id);
                //SQLManeger.getSqlManeger().closeDatabase();
                intent_deal.putExtra("sent",g_id);
                break;
            }
            case "$flush":{
                List<Friend> friendList= new ArrayList<>();
                int i;
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
                for(;;i++){
                    if(M[i+1].equals("$end"))
                        break;
                    Friend friend=new Friend(
                            Integer.valueOf(M[i+1]).intValue(),
                            "   ",
                            0,
                            2
                    );
                    friendList.add(friend);
                }
                //SQLManeger sqlManeger = new SQLManeger(this);
                SQLManeger.getSqlManeger().add(friendList,Id);
                //SQLManeger.getSqlManeger().closeDatabase();
                intent_deal.putExtra("flush",true);
                break;
            }
            case "$creategroup":{    // 创群成功
                DeviceImpl.getInstance().SendMessage(ServiceIp,"$flush");
                String id=M[1];
                intent_deal.putExtra("qunhao",id);
                intent_deal.putExtra("creategroup",true);
                break;
            }

        }
        sendBroadcast(intent_deal);
    }
}
