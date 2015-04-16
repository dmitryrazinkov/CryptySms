package com.quiet.cryptySms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import com.quiet.cryptySms.services.AesSmsService;

public class DataAesSmsReceiver extends BroadcastReceiver {
    String TAG = "DataAesSmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "aes receiver");
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

        Intent intentService = new Intent(context, AesSmsService.class);
        intentService.putExtra("number", number);
        intentService.putExtra("data", data);
        if ((data != null) && (number != null)) {
            context.startService(intentService);
        }

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
