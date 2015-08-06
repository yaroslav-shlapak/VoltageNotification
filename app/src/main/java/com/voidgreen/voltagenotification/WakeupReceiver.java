package com.voidgreen.voltagenotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by y.shlapak on Jun 26, 2015.
 */
public class WakeupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Utility.startBatteryInfoService(context);
        Utility.updateAllWidgets(context);
        //Utility.startAlarm(context);
        Utility.showToast(context, "WakeupReceiver");

    }
}
