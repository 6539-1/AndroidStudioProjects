package com.example.administrator.mainui;

import android.app.ListFragment;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class WiFiDirectServicesList extends ListFragment {

    WiFiDevicesAdapter listAdapter = null;

    interface DeviceClickListener {
        public void connectP2p(WiFiP2pService wifiP2pService);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.devices_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       listAdapter = new WiFiDevicesAdapter(this.getActivity(),
                R.layout.id_list_name,R.id.message_list,
                new ArrayList<WiFiP2pService>());

        setListAdapter(listAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        ((DeviceClickListener) getActivity()).connectP2p((WiFiP2pService) l
                .getItemAtPosition(position));
        ((TextView) v.findViewById(R.id.msg_last)).setText("Connecting");

    }

    public  class WiFiDevicesAdapter extends ArrayAdapter<WiFiP2pService> {

        private List<WiFiP2pService> items;
        private int resourceId;
        public WiFiDevicesAdapter(Context context,int resource,
                                  int textViewResourceId, List<WiFiP2pService> items) {
            super(context,resource,textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.id_list_name, null);
                //v=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            }
            WiFiP2pService service = items.get(position);
            if (service != null) {
                TextView nameText = (TextView) v
                        .findViewById(R.id.name_id);

                if (nameText != null) {
                    if(service.device.deviceName.equals("Android_2aa3")) {
                        nameText.setText("卢冬冬");
                    }
                    else if(service.device.deviceName.equals("Android_ff8e")||
                            service.device.deviceAddress.equals("1a:f0:e4:73:b4:35")) {
                        nameText.setText("吴宏俊");
                    }
                    else if(service.device.deviceName.equals("OnePlus 3T")) {
                        nameText.setText("梁夏华");
                    }
                    else {
                        nameText.setText(service.device.deviceName + " - " + service.instanceName);
                    }
                }
                TextView statusText = (TextView) v
                        .findViewById(R.id.msg_last);
                if(service.device.deviceName.equals("Android_2aa3")||
                        service.device.deviceName.equals("Android_ff8e")||
                        service.device.deviceName.equals("OnePlus 3T"))
                    statusText.setText("好友-  "+getDeviceStatus(service.device.status));
                else
                    statusText.setText("陌生-  "+getDeviceStatus(service.device.status));
                ImageView img=(ImageView)v.findViewById(R.id.img_id);
                img.setImageResource(service.getImgId());
            }
            return v;
        }

    }

    public static String getDeviceStatus(int statusCode) {
        switch (statusCode) {
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }

}
