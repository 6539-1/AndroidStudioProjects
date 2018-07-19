package com.example.administrator.jsip;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.jsip.chat_main.Recorder;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    //private ArrayList<String> mAccountList; 
    private ArrayList<Recorder> rAccountList;
    private Context mContext;
    //private ImageView vcView;
    //private OnItemClickListener mOnItemClickListener; 
    //private TextView Account_View; 
    //private Handler mHandler; 
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView RAccount;
        ImageView LAccount;
        LinearLayout leftlayout;
        LinearLayout rightlayout;
        TextView leftMsg;
        TextView rightMsg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //System.out.println("11111111111");
            //  Account = (TextView)itemView.findViewById(R.id.editTextMessage); 
            //  RAccount = (ImageView)itemView.findViewById(R.id.sdButton); 
            //itemView.setLayoutParams();
            leftlayout = (LinearLayout) itemView.findViewById(R.id.left_layout);
            rightlayout = (LinearLayout) itemView.findViewById(R.id.right_layout);
            leftMsg = (TextView) itemView.findViewById(R.id.left_msg);
            rightMsg = (TextView) itemView.findViewById(R.id.right_msg);
            LAccount = (ImageView) itemView.findViewById(R.id.leftAdo);
            RAccount = (ImageView) itemView.findViewById(R.id.rightAdo);
        }
    }
    public ChatAdapter(Context context,ArrayList<Recorder> AccountList) {
        //System.out.println("2222222222");
        rAccountList = AccountList;
        this.mContext = context;
       // vcView = voiceView;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       // System.out.println("vvvvvvvvvvvvvvvvv");
        View view = View.inflate(mContext,R.layout.croom,null);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder viewHolder, final int position) {
       // System.out.println("sssssssssssssssssss");
        final ViewHolder viewHolder1 = viewHolder;
        Recorder aduioMessage = rAccountList.get(position);
        String message = aduioMessage.rcvMessage;
        if (message!=null){
        if (message != null && !message.isEmpty()) {
            if (viewHolder1.leftMsg != null&&viewHolder1.rightMsg!=null) {

                if (message.startsWith("Me: ")) {
                    viewHolder1.leftlayout.setVisibility(View.GONE);
                    viewHolder1.rightlayout.setVisibility(View.VISIBLE);
                    viewHolder1.rightMsg.setText(message.substring(3));
                } else {
                    viewHolder1.leftlayout.setVisibility(View.VISIBLE);
                    viewHolder1.rightlayout.setVisibility(View.GONE);
                    viewHolder1.leftMsg.setText(message);
                }
            }
        }
        }
        else {
            if (aduioMessage.filePath!=null&&!aduioMessage.filePath.isEmpty()){
                if (aduioMessage.filePath.startsWith("Me: ")){
                    viewHolder1.leftlayout.setVisibility(View.GONE);
                    viewHolder1.rightlayout.setVisibility(View.VISIBLE);
                    viewHolder1.RAccount.setImageResource(R.drawable.picr3);
                    viewHolder1.RAccount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String test1 = rAccountList.get(position).filePath;
                            MediaManager.playSound(test1, new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    System.out.println("finish play");
                                }
                            });
                        }
                    });

                   // viewHolder1.rightMsg.setText(message.substring(3));
                }else {
                    viewHolder1.leftlayout.setVisibility(View.VISIBLE);
                    viewHolder1.rightlayout.setVisibility(View.GONE);
                    viewHolder1.LAccount.setImageResource(R.drawable.picr3);
                    viewHolder1.LAccount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.out.println("filepppppppppppppppppppppp: "+rAccountList.get(position).filePath);
                            String filePathSpliter[] = rAccountList.get(position).filePath.split("/");
                            String absolutFilePath = filePathSpliter[filePathSpliter.length-2]+"/"+filePathSpliter[filePathSpliter.length-1];
                            System.out.println("-------->>>>>>>>>>"+absolutFilePath);
                            MediaManager.playSound(rAccountList.get(position).filePath, new MediaPlayer.OnCompletionListener() {
                                    @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    System.out.println("finish play");
                                }
                            });
                        }
                    });
                }
            }

        }

    }

    @Override
    public int getItemCount() {
        return rAccountList.size();
    }
}
