package com.quiet.test.services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.quiet.test.databases.Db;

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
        return START_STICKY;
    }

    private void processSms(String sms_body, String number) {
        addToDatabase(sms_body, number);
        Intent intent = new Intent("test.chat");
        intent.putExtra("number", number);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void addToDatabase(String sms_body, String number) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", number);
        contentValues.put("message", sms_body);
        contentValues.put("isIncome", 1);
        contentValues.put("date", new Date().getTime());

        Db db = new Db(this);
        SQLiteDatabase database = db.getWritableDatabase();
        database.insert("messages", null, contentValues);
        db.close();
    }
}
