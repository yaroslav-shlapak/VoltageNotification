package com.voidgreen.voltagenotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**+
 * Created by y.shlapak on Jun 15, 2015.
 */
public class BatteryInfoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 3000);

        String string;

        if (voltage < 3000) {
            string = "Your device is not supported";
        } else {
            string = Integer.toString(voltage);
        }


        Utility.saveBatteryInfo(context, string);
        Utility.updateAllWidgets(context);

        Utility.showToast(context, "BatteryInfoReceiver");
    }
}