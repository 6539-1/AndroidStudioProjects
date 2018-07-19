package com.example.administrator.jsip;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SQLManeger {


    private static SQLManeger sqlManeger;
    private Context context;
    private SQLHelper sqlHelper;
    private SQLiteDatabase sqldb;

    private String FRIEND_TABLE_NAME;
    private String MESSAGE_TABLE_NAME;
    private String SYSTEM_TABLE_NAME;

    private String SYSTEM_CREATE_TABLE_SQL;
    private String FRIEND_CREATE_TABLE_SQL;
    private String MESSAGE_CREATE_TABLE_SQL;

    private SQLManeger(){

    }

    public void init(Context context){
        this.context = context;
        sqlHelper = new SQLHelper(context,1);
        sqldb = sqlHelper.getWritableDatabase();
    }

    public static SQLManeger getSqlManeger() {
        if(sqlManeger==null){
            sqlManeger = new SQLManeger();
        }
        return sqlManeger;
    }

    public void CreateTable(String Id){
        MESSAGE_TABLE_NAME = "MESSAGE_" + Id;
        FRIEND_TABLE_NAME = "FRIENDTABLE_" + Id;
        SYSTEM_TABLE_NAME = "SYSTEMTABLE_" + Id;
        SYSTEM_CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + SYSTEM_TABLE_NAME + "("
                + "content varchar(20) NOT NULL,"
                + "state integer NOT NULL"
                + ");";
        FRIEND_CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + FRIEND_TABLE_NAME + "("
                + "id integer NOT NULL,"
                + "name varchar(20) NOT NULL,"
                + "image intger NOT NULL,"
                + "state integer NOT NULL"
                + ");";
        MESSAGE_CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + MESSAGE_TABLE_NAME + "("
                + "id varchar(20) NOT NULL,"
                + "origin_id varchar(20) NOT NULL,"
                + "nickname varchar(20) NOT NULL,"
                + "content varchar(20) NOT NULL,"
                + "state integer NOT NULL,"
                + "isMine integer NOT NULL"
                + ");";
        sqldb.execSQL(FRIEND_CREATE_TABLE_SQL);
        sqldb.execSQL(MESSAGE_CREATE_TABLE_SQL);
        sqldb.execSQL(SYSTEM_CREATE_TABLE_SQL);
    }
    /*
    * 往表Friendtable中添加数据
    * @parms List<Friend>
    * */

    public void add(List<Friend> list,String Id){
        sqldb.execSQL("delete from friendtable_"+Id);
        sqldb.beginTransaction();
        try{
            for (Friend friend:list){
                ContentValues values = new ContentValues();
                values.put("id",friend.getID());
                values.put("name",friend.getName());
                values.put("image",friend.getImageId());
                values.put("state",friend.getState());
                sqldb.insert("friendtable_"+Id,null,values);
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
    public ArrayList<Friend> query(String Id){
        ArrayList<Friend> friend_list=new ArrayList<>();
        Cursor cursor=sqldb.query("friendtable_"+Id,null,null,null,null,null,null);
        if (cursor != null) {
            while(cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex("state"))==2)continue;
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
    //获取群ID
    public ArrayList<Integer> get_group_Id(String Id){
        ArrayList<Integer> Id_list=new ArrayList<>();
        Cursor cursor=sqldb.query("friendtable_"+Id,null,null,null,null,null,null);
        if (cursor != null) {
            while(cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex("state"))!=2)continue;
                Id_list.add(cursor.getInt(cursor.getColumnIndex("id")));
            }
        }
        cursor.close();
        return Id_list;
    }
    /*
    * 删除一名好友
    * */
    public void deleteFriend(Friend friend,String Id){
        String friendId = Integer.toString(friend.getID());
        sqldb.execSQL("DELETE " + friendId + " FROM FRIENDTABLE_"+Id);
    }

    public String getNickname(String Id,String id){
        String[] args = {id};
        String Nickname="";
        Cursor cursor=sqldb.query("friendtable_"+Id,null,"id=?",args,null,null,null);
        if (cursor!=null){
            while (cursor.moveToNext()) {
                //if (cursor.getInt(cursor.getColumnIndex("id")).equals(id))
                Nickname = cursor.getString(cursor.getColumnIndex("name"));
            }
        }
        cursor.close();
        return Nickname;
    }
    /*
    * 往数据库的一张表名为OriginName的Message表中添加数据
    * @parms List<LocalMessage> , OriginName : String
    * */
    public void addMessage(LocalMessage localMessage,String Id){
        Log.d("zsmj",localMessage.getContent()+localMessage.getOrigin_Id()+localMessage.getId());
        //sqldb.execSQL("INSERT INTO MESSAGE_"+Id +"(id,origin_id,nickname,content,state,isMine) VALUES(?,?,?,?,?,?)",
         //               new Object[]{localMessage.getId(),localMessage.getOrigin_Id(),localMessage.getNickname(),localMessage.getContent(),localMessage.getState(),localMessage.getIsMine()});

        ContentValues values = new ContentValues();
        values.put("id",localMessage.getId());
        values.put("origin_id",localMessage.getOrigin_Id());
        values.put("nickname",localMessage.getNickname());
        values.put("content",localMessage.getContent());
        values.put("state",localMessage.getState());
        values.put("isMine",localMessage.getIsMine());
        sqldb.insert("message_"+Id,null,values);
        values.clear();
        Cursor cursor=sqldb.query("MESSAGE_"+Id,null,null,null,null,null,null);
        if (cursor != null) {
            while (cursor.moveToNext()){
                Log.d( "qwwe",cursor.getString(cursor.getColumnIndex("content")));
            }
        }
        cursor.close();
    }
    /*
    * 从数据库中名为Message_id的表中读取消息
    * */
    public ArrayList<LocalMessage> get_message_by_id(String Origin_Id,String Id){
        ArrayList<LocalMessage> MessageList=new ArrayList<LocalMessage>();
        String[] args = {Origin_Id,"0"};
        Cursor cursor=sqldb.query("MESSAGE_"+Id,null,"origin_id=? and state=?",args,null,null,null);
        if (cursor != null) {
            while(cursor.moveToNext()) {
                LocalMessage localMessage = new LocalMessage(
                        cursor.getString(cursor.getColumnIndex("nickname")),
                        cursor.getString(cursor.getColumnIndex("content")),
                        cursor.getInt(cursor.getColumnIndex("state")),
                        cursor.getInt(cursor.getColumnIndex("isMine")),
                        cursor.getString(cursor.getColumnIndex("origin_id")),
                        cursor.getString(cursor.getColumnIndex("id"))
                );
                MessageList.add(localMessage);
            }
        }
        cursor.close();

        return MessageList;
    }

    public ArrayList<LocalMessage> get_message(String Id){
        ArrayList<LocalMessage> localMessages = new ArrayList<>();
        String[] args = {"0"};
        Cursor cursor=sqldb.query("MESSAGE_"+Id,null,"state=?",args,null,null,null);
        if (cursor != null) {
            while(cursor.moveToNext()) {
                LocalMessage localMessage = new LocalMessage(
                        cursor.getString(cursor.getColumnIndex("content")),
                        cursor.getString(cursor.getColumnIndex("nickname")),
                        cursor.getInt(cursor.getColumnIndex("state")),
                        cursor.getInt(cursor.getColumnIndex("isMine")),
                        cursor.getString(cursor.getColumnIndex("origin_id")),
                        cursor.getString(cursor.getColumnIndex("id"))
                );
                localMessages.add(localMessage);
            }
        }
        cursor.close();
        return localMessages;
    }

    public String get_one_message(String Id,String one){
        String Message="";
        String[] args = {one};
        List<String> list=new ArrayList<>();
        Cursor cursor=sqldb.query("MESSAGE_"+Id,null,"origin_id=?",args,null,null,null);
        if (cursor != null) {
            while (cursor.moveToNext()){
                list.add(cursor.getString(cursor.getColumnIndex("content")));
            }
        }
        if (list.size()>0)
            Message=list.get(list.size()-1);
        cursor.close();
        return Message;
    }
    /*
    * 往数据库中的Personal表添加信息
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

    public void addOnePerson(Personal person){
        ContentValues values = new ContentValues();
        values.put("id",person.getId());
        values.put("nickname",person.getNickname());
        values.put("imageid",person.getImage_id());
        sqldb.insert("personaltable",null,values);
        values.clear();
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

    public void addSystem(String systemMsgs,String Id){
        sqldb.beginTransaction();
        sqldb.execSQL("INSERT INTO SYSTEMTABLE_"+Id+" (content) VALUES(?)", new Object[]{systemMsgs});
        sqldb.setTransactionSuccessful();
        sqldb.endTransaction();
    }

    public ArrayList<String> Systemquery(String Id){
        ArrayList<String> SystemMsg = new ArrayList<>();
        Cursor cursor = sqldb.query("SYSTEMTABLE_"+Id,null,null,null,null,null,null);
        if (cursor!=null){
            while (cursor.moveToNext()){
                SystemMsg.add(cursor.getString(cursor.getColumnIndex("content")));
            }
        }
        cursor.close();
        return SystemMsg;
    }

    public int getHead(String Id,String origin_id){
        int Head=0;
        String[] col = {"image"};
        String[] args = {origin_id};
        Cursor cursor = sqldb.query("friendTable_"+Id,col,"id=?",args,null,null,null);
        if (cursor!=null){
            while (cursor.moveToNext()) {
                Head = cursor.getInt(cursor.getColumnIndex("image"));
            }
        }
        cursor.close();
        return Head;
    }

    public void closeDatabase(){
        sqldb.close();
    }

}
