package com.quiet.test.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Db extends SQLiteOpenHelper {

    public Db(Context context) {
        super(context, "Db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table keys(" +
                "number text primary key," +
                " key integer" +
                ")");
        db.execSQL("create table messages(" +
                "number text," +
                " date text," +
                " isIncome integer," +
                " message text," +
                "primary key(number,date)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
