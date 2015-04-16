package com.quiet.cryptySms.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.quiet.cryptySms.AndroidDatabaseManager;
import com.quiet.cryptySms.R;
import com.quiet.cryptySms.crypt.RSA;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


public class MainActivity extends ActionBarActivity {
    String TAG = "MainActivity";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            rsaInit();
        } catch (NoSuchAlgorithmException e) {
            Log.w(TAG, e);
        } catch (InvalidKeySpecException e) {
            Log.w(TAG, e);
        }
        getIntent().setAction("Already created");
    }

    @Override
    protected void onResume() {
        String action = getIntent().getAction();
        if (action == null || !action.equals("Already created")) {
            Log.d(TAG, "restart");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else
            getIntent().setAction(null);
        super.onResume();
    }

    private void rsaInit() throws NoSuchAlgorithmException, InvalidKeySpecException {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        BigInteger rsa_private = new BigInteger(sharedPreferences.getString("rsa_private", "0"));
        BigInteger rsa_public = new BigInteger(sharedPreferences.getString("rsa_public", "0"));
        BigInteger rsa_mod = new BigInteger(sharedPreferences.getString("rsa_mod", "0"));
        if (rsa_private.equals(new BigInteger("0"))) {
            Log.d(this.getLocalClassName(), "create rsa key");
            RSA rsa = new RSA();
            rsa.generateKey();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            rsa_private = rsa.getPrivateExponent();
            rsa_public = rsa.getPublicExponent();
            rsa_mod = rsa.getModulus();
            editor.putString("rsa_private", rsa_private.toString());
            editor.putString("rsa_public", rsa_public.toString());
            editor.putString("rsa_mod", rsa_mod.toString());
            editor.commit();
        }
        System.out.println(rsa_private + "," + rsa_public + "," + rsa_mod);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
            startActivity(dbmanager);
            return true;
        }

        if (id == R.id.action_contact) {
            Log.d(TAG, "adding contact");
            Intent intent = new Intent(getApplicationContext(), AddContactActiity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
