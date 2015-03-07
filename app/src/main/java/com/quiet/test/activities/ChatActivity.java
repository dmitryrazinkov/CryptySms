package com.quiet.test.activities;

import android.app.Activity;
import android.os.Bundle;

import com.quiet.test.R;

/**
 * Created by Дмитрий on 08.03.2015.
 */
public class ChatActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
    }
}
