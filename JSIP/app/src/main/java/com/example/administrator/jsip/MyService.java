package com.example.administrator.jsip;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;

import java.util.ArrayList;

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

    //private static String to = "sip:server@192.168.43.73:5006";

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
//        Message msg_rec = mHandler.obtainMessage();
//        msg_rec.what = 1;
//        msg_rec.obj = msg;
//        mHandler.sendMessage(msg_rec);
//        ArrayList arrayList = new ArrayList();
//        arrayList.add(msg);
//        Intent intent = new Intent("test");
//        intent.putStringArrayListExtra("message",arrayList);
//        sendBroadcast(intent);
        SQLManeger sqlm = new SQLManeger(this);
        String M[]=msg.split(" ");

        String flag = M[0];

        switch (flag){
            case("$sent") :{
                String nickname = M[1];
                String Rtime = M[2];
                String message = M[3];
                for (int i = 4;;i++){
                    if (M[i].equals("$end")){
                        break;
                    }
                    message+=" "+M[i];
                }
                LocalMessage lmsg = new LocalMessage(Rtime,nickname,message,1,0);
                rmessage.add(lmsg);
                sqlm.addMessage(rmessage,"p1992");
                break;
            }
            case("$sentall"):{
                String Gid = M[1];
                String nickname = M[2];
                String Rtime = M[3];
                String message = M[4];
                for (int i = 5;;i++){
                    if (M[i].equals("$end")){
                        break;
                    }
                    message+=" "+M[i];
                }
                LocalMessage lmsg = new LocalMessage(Rtime,Gid,message,1,0);
                rmessage.add(lmsg);
                sqlm.addMessage(rmessage,nickname);
                break;
            }
        }
               ArrayList<LocalMessage> testList = new ArrayList<>();
                testList = sqlm.Messagequery("p1992");
                Intent intent = new Intent("com.app.test");
                intent.putExtra("message","DATABASE_CHANGED");
                sendBroadcast(intent);




    }
    public void setReciveMessage(String msg){
        this.reciveMessage = msg;
    }
    public String getReciveMessage(){
        return reciveMessage;
    }
}
