package com.quiet.cryptySms.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quiet.cryptySms.R;

public class LoginActivity extends Activity {
    SharedPreferences sharedPreferences;

    private EditText editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_activity);

        editTextPassword = (EditText) findViewById(R.id.editTxtPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String key = sharedPreferences.getString("login_key", "");

        if (key.equals("")) {
            buttonLogin.setText("Create password");
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (key.equals("")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("login_key", editTextPassword.getText().toString());
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (key.equals(editTextPassword.getText().toString())) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}
