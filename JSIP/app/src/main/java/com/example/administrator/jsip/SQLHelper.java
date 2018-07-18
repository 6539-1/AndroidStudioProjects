package com.example.administrator.jsip;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "communication.db";
    public String PERSONAL_TABLE_NAME = "personaltable";

    private String PERSONAL_CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + PERSONAL_TABLE_NAME + "("
            + "id integer NOT NULL,"
            + "nickname varchar(20) NOT NULL,"
            + "imageid integer NOT NULL"
            + ");";


    public SQLHelper(Context context ,int version) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(PERSONAL_CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                break;
            default:
                break;
        }
    }

}
