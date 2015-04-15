package com.quiet.test.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import com.quiet.test.services.ModSmsService;

public class DataModSmsReceiver extends BroadcastReceiver {
    String TAG="DataModSmsReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"mod reciver");
        Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
        SmsMessage[] messages = new SmsMessage[pduArray.length];
        for (int i = 0; i < pduArray.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
        }

        String number = messages[0].getOriginatingAddress();

        byte[] data = new byte[]{};
        for (int i = 0; i < messages.length; i++) {
            data = concatArray(data, messages[i].getUserData());
        }

        Intent intentService = new Intent(context, ModSmsService.class);
        intentService.putExtra("number", number);
        intentService.putExtra("data", data);
        context.startService(intentService);

        abortBroadcast();
    }

    public byte[] concatArray(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
