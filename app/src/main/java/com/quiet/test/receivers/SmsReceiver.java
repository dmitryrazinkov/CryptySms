package com.quiet.test.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.quiet.test.services.SmsService;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
        SmsMessage[] messages = new SmsMessage[pduArray.length];
        for (int i = 0; i < pduArray.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
        }

        String number = messages[0].getOriginatingAddress();


        StringBuilder bodyText = new StringBuilder();
        for (int i = 0; i < messages.length; i++) {
            bodyText.append(messages[i].getMessageBody());
        }
        String body = bodyText.toString();
        Intent mIntent = new Intent(context, SmsService.class);
        mIntent.putExtra("number", number);
        mIntent.putExtra("sms_body", body);
        context.startService(mIntent);

        abortBroadcast();
    }
}
