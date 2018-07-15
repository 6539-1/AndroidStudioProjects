package com.example.administrator.jsip;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "communication.db";
    public static final String TABLE_NAME = "friendtable";
    private static final String FRIEND_CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + "id integer NOT NULL,"
            + "name varchar(20) NOT NULL,"
            +"image intger NOT NULL,"
            + "state integer NOT NULL"
            + ");";
    private static final String DELETE_TABLE="drop table if exists " + TABLE_NAME;

    public SQLHelper(Context context ,int version) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(FRIEND_CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                break;
            default:
                String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
                sqLiteDatabase.execSQL(sql);
                onCreate(sqLiteDatabase);
                break;
        }
    }

}
