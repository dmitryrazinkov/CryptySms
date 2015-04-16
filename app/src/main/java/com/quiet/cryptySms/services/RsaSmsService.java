package com.quiet.cryptySms.services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;

import com.quiet.cryptySms.crypt.AES;
import com.quiet.cryptySms.crypt.RSA;
import com.quiet.cryptySms.databases.Db;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RsaSmsService extends Service {
    String TAG = "RsaSmsService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "rsa service");
        String number = intent.getExtras().getString("number");
        byte[] data = intent.getExtras().getByteArray("data");
        BigInteger rsa_key = new BigInteger(data);
        try {
            processSms(rsa_key, number);
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
        return START_NOT_STICKY;
    }

    private void processSms(BigInteger rsa_key, String number) throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
        addToDatabase(rsa_key, number);
    }

    private void addToDatabase(BigInteger rsa_key, String number) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {
        ContentValues contentValues = new ContentValues();
        contentValues.put("rsa_key", rsa_key.toString());

        Log.d(TAG, number + ":" + rsa_key);

        Db db = new Db(this);
        SQLiteDatabase database = db.getWritableDatabase();
        String[] args = new String[]{number};
        Cursor c = database.query("keys", null, "number=?", args, null, null, null);
        if (c.moveToFirst()) {
            database.update("keys", contentValues, "number=?", args);
            keysProcess(rsa_key, number);
        } else {
            contentValues.put("number", number);
            database.insert("keys", null, contentValues);
        }
        db.close();
    }

    private void keysProcess(BigInteger rsa_key, String number) throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
        AES aes = new AES();
        aes.generateKey();
        byte[] aes_key = aes.getEncryptionKey().getEncoded();
        String string_aes = Base64.encodeToString(aes_key, Base64.DEFAULT);
        Log.d(TAG, "String aes " + string_aes);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("aes", string_aes);
        editor.commit();

        BigInteger rsa_mod = getRsaMod(number);
        Log.d(TAG, rsa_mod + ":" + rsa_key);
        RSAPublicKeySpec spec = new RSAPublicKeySpec(rsa_mod, rsa_key);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PublicKey pub = factory.generatePublic(spec);
        Log.d(TAG, String.valueOf(pub.getEncoded().length));

        RSA rsa = new RSA();
        rsa.setPublicKey(pub);
        Log.d(TAG, String.valueOf(aes_key.length));
        byte[] aes_encrypt = rsa.rsaEncrypt(aes_key);

        addAesKeyToDb(Base64.encodeToString(aes_key, Base64.DEFAULT), number);
        sendAesKey(aes_encrypt, number);

    }

    private void addAesKeyToDb(String aes_key, String number) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("aes_key", aes_key);

        Db db = new Db(this);
        SQLiteDatabase database = db.getWritableDatabase();
        String[] args = new String[]{number};
        Cursor c = database.query("keys", null, "number=?", args, null, null, null);
        if (c.moveToFirst()) {
            database.update("keys", contentValues, "number=?", args);
        }
        db.close();

    }

    private void sendAesKey(byte[] aes_encrypt, String number) {
        SmsManager sms = SmsManager.getDefault();
        short port = 4449;
        Log.d(TAG, "aes sending" + aes_encrypt.length);
        Log.d(TAG, "number" + number);
        sms.sendDataMessage(number, null, port, aes_encrypt, null, null);
    }

    private BigInteger getRsaMod(String number) {
        Db db = new Db(this);
        SQLiteDatabase database = db.getWritableDatabase();
        String[] args = new String[]{number};
        Cursor c = database.query("keys", null, "number=?", args, null, null, null);
        if (c.moveToFirst()) {
            BigInteger mod = new BigInteger(c.getString(c.getColumnIndex("rsa_mod")));
            db.close();
            return mod;
        }
        return new BigInteger("65537");
    }
}

