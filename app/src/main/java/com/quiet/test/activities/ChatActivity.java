package com.quiet.test.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.quiet.test.R;
import com.quiet.test.chat.ChatArrayAdapter;
import com.quiet.test.chat.ChatMessage;
import com.quiet.test.databases.Db;

import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;
import java.util.Date;

public class ChatActivity extends Activity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private Button buttonGetKey;

    private String phoneNumber;

    private byte[] aesKey;

    Intent intent;
    private boolean side = false;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        phoneNumber = i.getStringExtra("number");
        // String sms_body=i.getStringExtra("sms_body");

        setContentView(R.layout.chat_activity);

        buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonGetKey = (Button) findViewById(R.id.buttonKey);

        aesKey = getAesKey();
        if (aesKey == null) {
            buttonSend.setEnabled(false);
        } else {
            buttonGetKey.setEnabled(false);
        }

        listView = (ListView) findViewById(R.id.listView1);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.chat_message);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.chatText);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        buttonGetKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRSAmod();
                sendRSAkey();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        fillChat();
        //if (sms_body!=null) {
        //    chatArrayAdapter.add(new ChatMessage(true, sms_body));
        //}
    }

    private void sendRSAmod() {
        Log.d(this.getLocalClassName(),"send rsa mod");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        BigInteger rsa_mod = new BigInteger(sharedPreferences.getString("rsa_mod", "0"));
        System.out.println(rsa_mod);
        byte[] data = rsa_mod.toByteArray();
        SmsManager smsManager = SmsManager.getDefault();
        short port = 4445;
        smsManager.sendDataMessage(phoneNumber, null, port, data, null, null);
    }

    private void sendRSAkey() {
        Log.d(this.getLocalClassName(),"send rsa key");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        BigInteger rsa_public = new BigInteger(sharedPreferences.getString("rsa_public", "0"));
        System.out.println(rsa_public);
        byte[] data = rsa_public.toByteArray();
        SmsManager smsManager = SmsManager.getDefault();
        short port = 4444;
        smsManager.sendDataMessage(phoneNumber, null, port, data, null, null);

    }

    private byte[] getAesKey() {
        Db db = new Db(this);
        SQLiteDatabase database = db.getWritableDatabase();
        String[] args = new String[]{phoneNumber};
        Cursor c = database.query("keys", null, "number=?", args, null, null, null);
        if (c.moveToFirst()) {
            return android.util.Base64.decode(c.getString(c.getColumnIndex("aes_key")), 0);
        }
        return null;
    }

    private void fillChat() {
        String message = "";
        Integer isIncome = 0;
        Db db = new Db(this);
        SQLiteDatabase database = db.getWritableDatabase();
        String[] args = new String[]{phoneNumber};
        Cursor c = database.query("messages", null, "number=?", args, null, null, "date");
        if (c.moveToFirst()) {
            do {
                message = c.getString(c.getColumnIndex("message"));
                isIncome = c.getInt(c.getColumnIndex("isIncome"));
                if (isIncome == 0) {
                    chatArrayAdapter.add(new ChatMessage(false, message));
                } else {
                    chatArrayAdapter.add(new ChatMessage(true, message));
                }
            }
            while (c.moveToNext());
        }
        db.close();
    }

    private boolean sendChatMessage() {
        chatArrayAdapter.add(new ChatMessage(false, chatText.getText().toString()));
        sendSms();
        chatText.setText("");
        side = !side;
        return true;
    }

    private void sendSms() {
        Log.d(this.getLocalClassName(),"sending sms");
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, chatText.getText().toString(), null, null);
        addSmsToDb();
    }

    private void addSmsToDb() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", phoneNumber);
        contentValues.put("message", chatText.getText().toString());
        contentValues.put("isIncome", 0);
        contentValues.put("date", new Date().getTime());

        Db db = new Db(this);
        SQLiteDatabase database = db.getWritableDatabase();
        database.insert("messages", null, contentValues);
    }

}
