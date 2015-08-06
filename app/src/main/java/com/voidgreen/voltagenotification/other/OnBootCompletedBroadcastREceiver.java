package com.voidgreen.voltagenotification.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.voidgreen.voltagenotification.service.TimeService;
import com.voidgreen.voltagenotification.utilities.Constants;
import com.voidgreen.voltagenotification.utilities.SharedPrefUtility;
import com.voidgreen.voltagenotification.utilities.Utility;
import com.voidgreen.voltagenotification.R;

/**
 * Created by Void on 17-Jul-15.
 */
public class OnBootCompletedBroadcastREceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("OnBootCompletedBroadcastReceiver", "onReceive before if");

        if(SharedPrefUtility.isStartOnBootEnabled(context)) {
            Log.d("OnBootCompletedBroadcastReceiver", "onReceive in if");

            Utility.saveTimeString(context, Constants.ZERO_PROGRESS);
            Utility.saveStageString(context, Constants.WORK_STAGE);
            Resources resources = context.getResources();
            Intent serviceIntent = new Intent(context, TimeService.class);
            serviceIntent.putExtra(resources.getString(R.string.serviceTask), resources.getString(R.string.startTask));
            serviceIntent.addCategory(TimeService.TAG);
            context.startService(serviceIntent);


        }

    }
}
