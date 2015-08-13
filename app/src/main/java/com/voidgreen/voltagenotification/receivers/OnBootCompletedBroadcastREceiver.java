package com.voidgreen.voltagenotification.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.voidgreen.voltagenotification.services.VoltageNotificationService;
import com.voidgreen.voltagenotification.utilities.Constants;
import com.voidgreen.voltagenotification.utilities.SharedPrefUtility;

/**
 * Created by Void on 17-Jul-15.
 */
public class OnBootCompletedBroadcastREceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(SharedPrefUtility.isStartOnBootEnabled(context)) {
            Intent serviceIntent = new Intent(context, VoltageNotificationService.class);
            serviceIntent.addCategory(VoltageNotificationService.TAG);
            context.startService(serviceIntent);
            Log.d(Constants.DEBUG_TAG, "OnBootCompletedBroadcastREceiver : onReceive");
        }

    }
}
