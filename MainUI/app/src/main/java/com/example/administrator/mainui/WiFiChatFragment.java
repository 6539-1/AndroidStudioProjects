package com.example.administrator.mainui;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment handles chat related UI which includes a list view for messages
 * and a message entry field with send button.
 */
public class WiFiChatFragment extends Fragment {

    private View view;
    private ChatManager chatManager;
    private TextView chatLine;
    private ListView listView;
    ChatMessageAdapter adapter = null;
    private List<String> items = new ArrayList<String>();

    @Override
    @SuppressLint("NewApi")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
        chatLine = (TextView) view.findViewById(R.id.txtChatLine);
        listView = (ListView) view.findViewById(android.R.id.list);
        adapter = new ChatMessageAdapter(getActivity(), android.R.id.text1,
                items);
        listView.setAdapter(adapter);
        view.findViewById(R.id.button1).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (chatManager != null) {
                            chatManager.write(chatLine.getText().toString()
                                    .getBytes());
                            pushMessage("Me: " + chatLine.getText().toString());
                            chatLine.setText("");
                            chatLine.clearFocus();
                        }
                    }
                });
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener(backlistener);
        return view;
    }
    private View.OnKeyListener backlistener=new View.OnKeyListener() {

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    Intent intent=new Intent(getActivity(),MainActivity.class);
                    startActivity(intent);
                    return true;
                }
            }
            return false;
        }
    };

    public interface MessageTarget {
        public Handler getHandler();
    }

    public void setChatManager(ChatManager obj) {
        chatManager = obj;
    }

    public void pushMessage(String readMessage) {
        adapter.add(readMessage);
        adapter.notifyDataSetChanged();
    }

    /**
     * ArrayAdapter to manage chat messages.
     */
    public class ChatMessageAdapter extends ArrayAdapter<String> {

        List<String> messages = null;
        LinearLayout leftlayout;
        LinearLayout rightlayout;
        TextView leftMsg;
        TextView rightMsg;
        View itemview;
        public ChatMessageAdapter(Context context, int textViewResourceId,
                                  List<String> items) {
            super(context, textViewResourceId, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v=LayoutInflater.from(parent.getContext()).inflate(R.layout.croom,parent,false);
            }
            String message = items.get(position);
            if (message != null && !message.isEmpty()) {
                leftlayout=(LinearLayout)v.findViewById(R.id.left_layout);
                rightlayout=(LinearLayout)v.findViewById(R.id.right_layout);
                leftMsg=(TextView)v.findViewById(R.id.left_msg);
                rightMsg=(TextView)v.findViewById(R.id.right_msg);

                if (leftMsg != null&&rightMsg!=null) {

                    if (message.startsWith("Me: ")) {
                        leftlayout.setVisibility(View.GONE);
                        rightlayout.setVisibility(View.VISIBLE);
                        rightMsg.setText(message.substring(3));
                    } else {
                        leftlayout.setVisibility(View.VISIBLE);
                        rightlayout.setVisibility(View.GONE);
                        leftMsg.setText(message);
                    }
                }
            }
            return v;
        }

    }
}
