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
                String password=editTextPassword.getText().toString();
                if (key.equals("")) {
                    if (password.length()<6) {
                        Toast.makeText(getApplicationContext(), "Password must include more then" +
                                " 5 character", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("login_key", password);
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    if (key.equals(password)) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Incorrect password",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}
