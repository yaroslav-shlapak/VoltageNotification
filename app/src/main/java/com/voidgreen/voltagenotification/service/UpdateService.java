package com.voidgreen.voltagenotification.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.voidgreen.voltagenotification.WakeupReceiver;
import com.voidgreen.voltagenotification.utilities.Utility;

/**
 * Created by Void on 26-Jun-15.
 */
public class UpdateService extends Service {

    private BroadcastReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        receiver = new WakeupReceiver();
        registerReceiver(receiver, filter);

        Utility.showToast(getApplicationContext(), "UpdateService:onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        Utility.showToast(getApplicationContext(), "UpdateService:onDestroy");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
