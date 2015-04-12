package com.quiet.test.activities;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.quiet.test.R;
import com.quiet.test.crypt.RSA;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


public class MainActivity extends ActionBarActivity {
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            rsaInit();
        } catch (NoSuchAlgorithmException e) {

        } catch (InvalidKeySpecException e) {

        }
    }

    private void rsaInit() throws NoSuchAlgorithmException, InvalidKeySpecException {
        sharedPreferences=getPreferences(MODE_PRIVATE);
        BigInteger rsa_private=BigInteger.valueOf(sharedPreferences.getInt("rsa_private", 0));
        BigInteger rsa_public=BigInteger.valueOf(sharedPreferences.getInt("rsa_public", 0));
        BigInteger rsa_mod=BigInteger.valueOf(sharedPreferences.getInt("rsa_mod", 0));
        if (rsa_private.equals(new BigInteger("0"))) {
            RSA rsa=new RSA();
            rsa.generateKey();
            SharedPreferences.Editor editor=sharedPreferences.edit();
            rsa_private=rsa.getPrivateExponent();
            rsa_public=rsa.getPublicExponent();
            rsa_mod=rsa.getModulus();
            editor.putInt("rsa_private",rsa_private.intValue());
            editor.putInt("rsa_public",rsa_public.intValue());
            editor.putInt("rsa_mod",rsa_mod.intValue());
            editor.commit();
        }
        System.out.println(rsa_private+","+rsa_public+","+rsa_mod);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
