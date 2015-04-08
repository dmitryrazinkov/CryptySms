package com.quiet.test.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SmsService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String sms_body = intent.getExtras().getString("sms_body");
        String number=intent.getExtras().getString("number");
        processSms(sms_body,number);
        return START_STICKY;
    }

    private void processSms(String sms_body, String number) {
        Intent intent=new Intent("test.chat");
        intent.putExtra("sms_body",sms_body);
        intent.putExtra("number", number);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
