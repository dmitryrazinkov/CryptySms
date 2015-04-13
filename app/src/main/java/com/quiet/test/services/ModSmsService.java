package com.quiet.test.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ModSmsService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
