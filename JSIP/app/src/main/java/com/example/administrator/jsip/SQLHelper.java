package com.example.administrator.jsip;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {
    private String Id;

    private static final String DB_NAME = "communication.db";
    //表名
    private static String FRIEND_TABLE_NAME;
    private static String MESSAGE_TABLE_NAME;
    public static final String PERSONAL_TABLE_NAME = "personaltable";
    //建表语句
    private static final String FRIEND_CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + FRIEND_TABLE_NAME + "("
            + "id integer NOT NULL,"
            + "name varchar(20) NOT NULL,"
            + "image intger NOT NULL,"
            + "state integer NOT NULL"
            + ");";
    private static final String MESSAGE_CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + MESSAGE_TABLE_NAME + "("
            + "time varchar(20) NOT NULL,"
            + "id varchar(20) NOT NULL,"
            + "origin_id varchar(20) NOT NULL"
            + "nickname varchar(20) NOT NULL,"
            + "content varchar(20) NOT NULL,"
            + "state integer NOT NULL,"
            + "isMine integer NOT NULL,"
            + ");";
    private static final String PERSONAL_CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + PERSONAL_TABLE_NAME + "("
            + "id integer NOT NULL,"
            + "nickname varchar(20) NOT NULL,"
            + "imageid integer NOT NULL"
            + ");";

    private static final String DELETE_TABLE="drop table if exists " + FRIEND_TABLE_NAME;

    public SQLHelper(Context context ,int version,String Id) {
        super(context, DB_NAME, null, version);
        this.Id = Id;
        MESSAGE_TABLE_NAME = "MESSAGE_"+Id;
        FRIEND_TABLE_NAME = "FRIENDTABLE_"+Id;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(FRIEND_CREATE_TABLE_SQL);
        sqLiteDatabase.execSQL(MESSAGE_CREATE_TABLE_SQL);
        sqLiteDatabase.execSQL(PERSONAL_CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                break;
            default:
                String sql = "DROP TABLE IF EXISTS " + FRIEND_TABLE_NAME;
                sqLiteDatabase.execSQL(sql);
                onCreate(sqLiteDatabase);
                break;
        }
    }

}
