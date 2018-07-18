package com.example.administrator.jsip;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/*
*
* */

public class SQLManeger {

    private SQLHelper sqlHelper;
    private SQLiteDatabase sqldb;
    private String Id;

    public SQLManeger(Context context,String Id){
        sqlHelper=new SQLHelper(context,1,Id);
        this.Id = Id;
        sqldb=sqlHelper.getWritableDatabase();
        //sqldb.execSQL("INSERT INTO friendtable(id,name,image,state) VALUES (1,'ss',1,1)");
    }
    /*
    * 往表Friendtable中添加数据
    * @parms List<Friend>
    * */
    public void add(List<Friend> list){
        sqldb.execSQL("delete from friendtable_"+Id);
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
    /*
    * 从数据库中读取好友列表
    * @parms
    * */
    public ArrayList<Friend> query(){
        ArrayList<Friend> friend_list=new ArrayList<>();
        Cursor cursor=sqldb.query("friendtable_"+Id,null,null,null,null,null,null);
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
    /*
    * 删除一名好友
    * */
    public void deleteFriend(Friend friend){
        String friendId = Integer.toString(friend.getID());
        sqldb.execSQL("DELETE " + friendId + " FROM FRIENDTABLE_"+Id);
    }
    /*
    * 往数据库的一张表名为OriginName的Message表中添加数据
    * @parms List<LocalMessage> , OriginName : String
    * */
    public void addMessage(List<LocalMessage> list){
        sqldb.beginTransaction();
        try{
            for (LocalMessage localMessage:list){
                sqldb.execSQL("INSERT INTO MESSAGE_"+Id +"(time,content,state,nickname,isMine) VALUES(?,?,?,?,?,?,?)",
                        new Object[]{localMessage.getTime(),localMessage.getId(),localMessage.getOrigin_Id(),localMessage.getNickname(),localMessage.getContent(),localMessage.getState(),localMessage.getIsMine()});
            }
            sqldb.setTransactionSuccessful();
        }finally {
            sqldb.endTransaction();
        }
    }
    /*
    * 从数据库中名为OriginName的表中读取消息
    * */
    public ArrayList<LocalMessage> Messagequery(String Origin_Id){
        ArrayList<LocalMessage> MessageList=new ArrayList<LocalMessage>();
        String[] args = {Origin_Id};
        Cursor cursor=sqldb.query("MESSAGE_"+Id,null,"origin_id=?",args,null,null,null);
        if (cursor != null) {
            while(cursor.moveToNext()) {
                LocalMessage localMessage = new LocalMessage(
                        cursor.getString(cursor.getColumnIndex("time")),
                        cursor.getString(cursor.getColumnIndex("content")),
                        cursor.getString(cursor.getColumnIndex("nickname")),
                        cursor.getInt(cursor.getColumnIndex("state")),
                        cursor.getInt(cursor.getColumnIndex("isMine")),
                        cursor.getString(cursor.getColumnIndex("Id")),
                        cursor.getString(cursor.getColumnIndex("origin_id"))
                );
                MessageList.add(localMessage);
            }
        }
        cursor.close();
        return MessageList;
    }
    /*
    * 往数据库中的Personal表添加一行信息
    * */
    public void addPerson(ArrayList<Personal> personals){
        sqldb.beginTransaction();
        sqldb.execSQL("DELETE FROM PERSONALTABLE");
        for(Personal personal:personals) {
            sqldb.execSQL("INSERT INTO PERSONALTABLE (id,nickname,imageid) VALUES(?,?,?)",
                    new Object[]{personal.getId(), personal.getNickname(), personal.getImage_id()});
        }
        sqldb.setTransactionSuccessful();
        sqldb.endTransaction();
    }
    /*
    * 从数据库中读取所有个人信息
    * */
    public ArrayList<Personal> Personalquery(){
        ArrayList<Personal> PersonalList = new ArrayList<>();
        Cursor cursor = sqldb.query("PersonalTable",null,null,null,null,null,null);
        if (cursor!=null){
            while(cursor.moveToNext()){
                Personal person = new Personal(
                        cursor.getString(cursor.getColumnIndex("nickname")),
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getInt(cursor.getColumnIndex("imageid"))
                );
                PersonalList.add(person);
            }
        }
        cursor.close();
        return PersonalList;
    }
    /*
    * 从Personal表中删除一行
    * */
    public void deletePerson(String id){
        sqldb.beginTransaction();
        sqldb.execSQL("DELETE FROM PERSONAL WHERE ID = "+ id);
        sqldb.setTransactionSuccessful();
        sqldb.endTransaction();
    }

    public void closeDatabase(){
        sqldb.close();
    }

}
