package com.quiet.cryptySms.services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import com.quiet.cryptySms.databases.Db;

import java.math.BigInteger;

public class ModSmsService extends Service {
    String TAG = "ModSmsService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "mod service");
        String number = intent.getExtras().getString("number");
        byte[] data = intent.getExtras().getByteArray("data");
        BigInteger mod = new BigInteger(data);
        processSms(mod, number);
        return START_NOT_STICKY;
    }

    private void processSms(BigInteger mod, String number) {
        addToDatabase(mod, number);
    }

    private void addToDatabase(BigInteger mod, String number) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("rsa_mod", mod.toString());

        Log.d(TAG, number + ":" + mod);

        Db db = new Db(this);
        SQLiteDatabase database = db.getWritableDatabase();
        String[] args = new String[]{number};
        Cursor c = database.query("keys", null, "number=?", args, null, null, null);
        if (c.moveToFirst()) {
            database.update("keys", contentValues, "number=?", args);
        } else {
            contentValues.put("number", number);
            database.insert("keys", null, contentValues);
        }
        db.close();
    }
}
