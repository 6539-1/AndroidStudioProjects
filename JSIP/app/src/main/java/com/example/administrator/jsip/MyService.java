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
    SipProfile sipProfile;
    String reciveMessage;
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
        System.out.println("msgmsgmsgmsg"+msg);
        setReciveMessage(msg);

        SQLManeger sqlm = new SQLManeger(this);
        String M[]=msg.split(" ");
        String flag = M[0];
        String nickname=null;
        String message=null;
        switch (flag){
            case("$sent") :{
                nickname = M[1];
                String Rtime = M[2];
                message = M[3];
                for (int i = 4;;i++){
                    if (M[i].equals("$end")){
                        break;
                    }
                    message+=" "+M[i];
                }
                LocalMessage lmsg = new LocalMessage(Rtime,message,nickname,1,0);
                rmessage.add(lmsg);
                sqlm.addMessage(rmessage,"p1992");
                break;
            }
            case("$sentall"):{
                String Gid = M[1];
                nickname = M[2];
                String Rtime = M[3];
                message = M[4];
                for (int i = 5;;i++){
                    if (M[i].equals("$end")){
                        break;
                    }
                    message+=" "+M[i];
                }
                message=nickname+":"+message;
                LocalMessage lmsg = new LocalMessage(Rtime,message,Gid,1,0);
                rmessage.add(lmsg);
                sqlm.addMessage(rmessage,"p1992");
                break;
            }
        }
               ArrayList<LocalMessage> testList = new ArrayList<>();
                testList = sqlm.Messagequery("p1992");
                Intent intent = new Intent("com.app.test");
                intent.putExtra("message","DATABASE_CHANGED");
                intent.putExtra("nickname",nickname);
                intent.putExtra("message_last",message);
                sendBroadcast(intent);

        sqlm.closeDatabase();

    }
    public void setReciveMessage(String msg){
        this.reciveMessage = msg;
    }
    public String getReciveMessage(){
        return reciveMessage;
    }

    public void deal(String rmessage){
        Intent intent_deal=new Intent();
        String M[]=rmessage.split(" ");
        switch(M[0]){
            case "$reg":{
                if (M[1].equals("success"))
                    intent_deal.putExtra("reg",true);
                else {
                    intent_deal.putExtra("reg",false);
                }

                break;
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
                    case "list": {
                        break;
                    }
                    case "error2": {//拒绝
                        String id = M[2];
                        intent_deal.putExtra("add",0);
                        break;
                    }
                    case "success": {//申请成功
                        String id = M[2];
                        intent_deal.putExtra("add",1);
                        break;
                    }
                    default:{
                        String id = M[1];
                        String name = M[2];
                        intent_deal.putExtra("add",2);
                        //请求申请
                        break;
                    }
                }
            }
            case "$sent": {
                String id = M[1];
                String Rtime = M[2];
                String content = M[3];
                for (int i = 4;;i++){
                    if (M[i].equals("$end")){
                        break;
                    }
                    content+=" "+M[i];
                }
                LocalMessage lmsg = new LocalMessage(Rtime,content,id,1,0);
                break;
            }
            case "$sentall":{
                String q_id = M[1];
                String name = M[2];
                String Rtime = M[3];
                String content = M[4];
                for (int i = 5;;i++){
                    if (M[i].equals("$end")){
                        break;
                    }
                    content+=" "+M[i];
                }
                LocalMessage lmsg = new LocalMessage(Rtime,content,q_id,1,0);
                break;
            }
            case "$flush":{
                List<Friend> friendList= new ArrayList<>();
                for(int i=1;;i++){
                    if(M[i].equals("$end"))
                        break;
                    Friend friend=new Friend(
                            Integer.valueOf(M[i]).intValue(),
                            M[i+1],
                            Integer.valueOf(M[i+2]).intValue(),
                            Integer.valueOf(M[i+3]).intValue()
                    );
                    friendList.add(friend);
                }
                intent_deal.putExtra("flush",true);
                break;
            }
            case "$creategroup":{    // 创群成功
                String id=M[1];
                intent_deal.putExtra("creategroup",true);
                break;
            }

        }

    }
}
