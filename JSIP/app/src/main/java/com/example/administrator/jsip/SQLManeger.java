package com.example.administrator.jsip;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SQLManeger {

    private SQLHelper sqlHelper;
    private SQLiteDatabase sqldb;

    public SQLManeger(Context context){
        sqlHelper=new SQLHelper(context,1);
        sqldb=sqlHelper.getWritableDatabase();
        //sqldb.execSQL("INSERT INTO friendtable(id,name,image,state) VALUES (1,'ss',1,1)");
    }
    public void add(List<Friend> list){
        sqldb.execSQL("delete from friendtable");
        sqldb.beginTransaction();
        try{
            for (Friend friend:list){
                ContentValues values = new ContentValues();
                values.put("id",friend.getID());
                values.put("name",friend.getName());
                values.put("image",friend.getImageId());
                values.put("state",friend.getState());
                sqldb.insert("friendtable",null,values);
                values.clear();
                //sqldb.execSQL("INSERT INTO friendtable(id,name,image,state) VALUES(?,?,?,?)",
                //new Object[]{friend.getID(),friend.getName(),friend.getImageId(),friend.getState()});
            }
            sqldb.setTransactionSuccessful();
        }finally {
            sqldb.endTransaction();
        }
    }

    public ArrayList<Friend> query(){
        ArrayList<Friend> friend_list=new ArrayList<>();
        Cursor cursor=sqldb.query("friendtable",null,null,null,null,null,null);
        if (cursor != null) {
            while(cursor.moveToNext()) {
                Friend friend = new Friend(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("image")),
                        cursor.getInt(cursor.getColumnIndex("state"))
                );
                friend_list.add(friend);
            }
        }
        cursor.close();
        return friend_list;
    }
    public void addMessage(List<LocalMessage> list,String OriginName){
        sqldb.beginTransaction();
        try{
            for (LocalMessage localMessage:list){
                sqldb.execSQL("INSERT INTO "+OriginName +"(time,content,state,nickname,isMine) VALUES(?,?,?,?,?)",
                        new Object[]{localMessage.getTime(),localMessage.getContent(),localMessage.getState(),localMessage.getNickname(),localMessage.getIsMine()});
            }
            sqldb.setTransactionSuccessful();
        }finally {
            sqldb.endTransaction();
        }
    }
    public ArrayList<LocalMessage> Messagequery(String OriginName){
        ArrayList<LocalMessage> MessageList=new ArrayList<LocalMessage>();
        Cursor cursor=sqldb.query(OriginName,null,null,null,null,null,null);
        if (cursor != null) {
            while(cursor.moveToNext()) {
                LocalMessage localMessage = new LocalMessage(
                        cursor.getString(cursor.getColumnIndex("time")),
                        cursor.getString(cursor.getColumnIndex("content")),
                        cursor.getString(cursor.getColumnIndex("nickname")),
                        cursor.getInt(cursor.getColumnIndex("state")),
                        cursor.getInt(cursor.getColumnIndex("isMine"))
                );
                MessageList.add(localMessage);
            }
        }
        cursor.close();
        return MessageList;
    }
    public void closeDatabase(){
        sqldb.close();
    }

}
