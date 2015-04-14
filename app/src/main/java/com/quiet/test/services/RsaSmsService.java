package com.quiet.test.services;

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

import com.quiet.test.crypt.AES;
import com.quiet.test.crypt.RSA;
import com.quiet.test.databases.Db;

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
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        System.out.println("rsa service");
        String number = intent.getExtras().getString("number");
        byte[] data=intent.getExtras().getByteArray("data");
        BigInteger rsa_key=new BigInteger(data);
        try {
            processSms(rsa_key,number);
        } catch (NoSuchAlgorithmException e) {

        } catch (InvalidKeySpecException e) {

        } catch (InvalidKeyException e) {

        } catch (BadPaddingException e) {

        } catch (NoSuchPaddingException e) {

        } catch (IllegalBlockSizeException e) {

        }
        return START_STICKY;
    }

    private void processSms(BigInteger rsa_key, String number) throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
        addToDatabase(rsa_key,number);
    }

    private void addToDatabase(BigInteger rsa_key, String number) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {
        ContentValues contentValues = new ContentValues();
        contentValues.put("rsa_key", rsa_key.intValue());

        System.out.println(number+":"+rsa_key);

        Db db=new Db(this);
        SQLiteDatabase database=db.getWritableDatabase();
        String[] args=new String[]{number};
        Cursor c=database.query("keys",null,"number=?",args,null,null,"null");
        if (c.moveToFirst()) {
            database.update("keys", contentValues,"number=?",args);
            keysProcess(rsa_key,number);
        } else {
            contentValues.put("number",number);
            database.insert("keys", null, contentValues);
        }
        db.close();
    }

    private void keysProcess(BigInteger rsa_key, String number) throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
        AES aes=new AES();
        aes.generateKey();
        byte[] aes_key=aes.getEncryptionKey().getEncoded();
        String string_aes= Base64.encodeToString(aes_key,Base64.DEFAULT);
        System.out.println("String aes " + string_aes);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("aes",string_aes);
        editor.commit();

        BigInteger rsa_mod=getRsaMod(number);
        RSAPublicKeySpec spec = new RSAPublicKeySpec(rsa_mod, rsa_key);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PublicKey pub = factory.generatePublic(spec);

        RSA rsa=new RSA();
        rsa.setPublicKey(pub);
        byte[] aes_encrypt=rsa.rsaEncrypt(aes_key);

        addAesKeyToDb(Base64.encodeToString(aes_key,Base64.DEFAULT),number);
        sendAesKey(aes_encrypt,number);

    }

    private void addAesKeyToDb(String aes_key, String number) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("aes_key", aes_key);

        Db db=new Db(this);
        SQLiteDatabase database=db.getWritableDatabase();
        String[] args=new String[]{number};
        Cursor c=database.query("keys",null,"number=?",args,null,null,"null");
        if (c.moveToFirst()) {
            database.update("keys", contentValues,"number=?",args);
        }
        db.close();

    }

    private void sendAesKey(byte[] aes_encrypt, String number) {
        SmsManager sms=SmsManager.getDefault();
        short port=4446;
        System.out.println("aes sending");
        sms.sendDataMessage(number,null,port,aes_encrypt,null,null);
    }

    private BigInteger getRsaMod(String number) {
        Db db=new Db(this);
        SQLiteDatabase database=db.getWritableDatabase();
        String[] args=new String[]{number};
        Cursor c=database.query("keys",null,"number=?",args,null,null,"null");
        if (c.moveToFirst()) {
            BigInteger mod=BigInteger.valueOf(c.getInt(c.getColumnIndex("rsa_mod")));
            db.close();
            return mod;
        }
        return null;
    }
}

