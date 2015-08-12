package com.voidgreen.voltagenotification.other;

import android.content.Context;

import com.voidgreen.voltagenotification.R;

/**
 * Created by y.shlapak on Aug 12, 2015.
 */
public class VoltageValueToDrawableConverter {

    public static int convertVoltgeIntToDrawable(Context context, int voltage) {
        if(voltage == 0) {
            return R.drawable.notification_na;
        }
        String imageName = "white" + voltage / 10;
        return context.getResources().getIdentifier(imageName, "drawable", "com.voidgreen.voltagenotification");

    }
}
