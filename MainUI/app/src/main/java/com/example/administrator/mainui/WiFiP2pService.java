package com.example.administrator.mainui;

import android.net.wifi.p2p.WifiP2pDevice;

public class WiFiP2pService {
    private String id_name;
    int imgId=0;
    private  String msg_last;
    private  String time;
    WifiP2pDevice device;
    String instanceName = null;
    String serviceRegistrationType = null;
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
}
