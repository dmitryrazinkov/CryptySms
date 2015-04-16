package com.quiet.cryptySms.services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.quiet.cryptySms.crypt.RSA;
import com.quiet.cryptySms.databases.Db;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AesSmsService extends Service {
    String TAG = "AesSmsService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "aes service");
        String number = intent.getExtras().getString("number");
        byte[] data = intent.getExtras().getByteArray("data");
        byte[] aes_key = new byte[0];
        try {
            aes_key = rsaEncrypt(data);
        } catch (NoSuchAlgorithmException e) {
            Log.w(TAG, e);
        } catch (InvalidKeySpecException e) {
            Log.w(TAG, e);
        } catch (InvalidKeyException e) {
            Log.w(TAG, e);
        } catch (BadPaddingException e) {
            Log.w(TAG, e);
        } catch (NoSuchPaddingException e) {
            Log.w(TAG, e);
        } catch (IllegalBlockSizeException e) {
            Log.w(TAG, e);
        }
        processSms(aes_key, number);
        return START_NOT_STICKY;
    }

    private byte[] rsaEncrypt(byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
        RSA rsa = new RSA();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        BigInteger rsa_private = new BigInteger(sharedPreferences.getString("rsa_private", "0"));
        BigInteger rsa_mod = new BigInteger(sharedPreferences.getString("rsa_mod", "0"));

        RSAPrivateKeySpec spec = new RSAPrivateKeySpec(rsa_mod, rsa_private);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PrivateKey priv = factory.generatePrivate(spec);
        rsa.setPrivateKey(priv);


        return rsa.rsaDecrypt(data);
    }

    private void processSms(byte[] aes_key, String number) {
        addToDatabase(aes_key, number);
    }

    private void addToDatabase(byte[] aes_key, String number) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("aes_key", android.util.Base64.encodeToString(aes_key, android.util.Base64.DEFAULT));

        Log.d(TAG, number + ":" + android.util.Base64.encodeToString(aes_key, android.util.Base64.DEFAULT));

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

