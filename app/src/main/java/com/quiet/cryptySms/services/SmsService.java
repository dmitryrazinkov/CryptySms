package com.quiet.cryptySms.services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.quiet.cryptySms.databases.Db;

import java.util.Date;

public class SmsService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Sms service");
        String sms_body = intent.getExtras().getString("sms_body");
        String number = intent.getExtras().getString("number");
        processSms(sms_body, number);
        return START_NOT_STICKY;
    }

    private void processSms(String sms_body, String number) {
        Db db = new Db(this);
        SQLiteDatabase database = db.getWritableDatabase();
        String[] args = new String[]{number};
        Cursor c = database.query("keys", null, "number=?", args, null, null, null);
        if (c.moveToFirst()) {
            addToDatabase(database, sms_body, number);
            Intent intent = new Intent("test.chat");
            intent.putExtra("number", number);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        database.close();
    }

    private void addToDatabase(SQLiteDatabase database, String sms_body, String number) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", number);
        contentValues.put("message", sms_body);
        contentValues.put("isIncome", 1);
        contentValues.put("date", new Date().getTime());


        database.insert("messages", null, contentValues);

    }
}
