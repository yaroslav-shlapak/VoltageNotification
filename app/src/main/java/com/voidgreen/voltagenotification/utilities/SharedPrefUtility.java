package com.voidgreen.voltagenotification.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import com.voidgreen.voltagenotification.R;

/**
 * Created by Void on 28-Jun-15.
 */
public class SharedPrefUtility {

    public static boolean isStartOnBootEnabled(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean(resources.getString(R.string.pref_key_start_on_boot), false);
    }

    public static String getColorListEntry(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getString(resources.getString(R.string.pref_text_color_key), resources.getString(R.string.pref_text_color_entryValue_white));
    }


    public static int convertVoltageIntToDrawable(Context context, int voltage) {
        if(voltage == 0) {
            return R.drawable.notification_na;
        }
        String textColor = SharedPrefUtility.getColorListEntry(context);
        Log.d(Constants.DEBUG_TAG, "SharedPrefUtility : convertVoltageIntToDrawable : " + textColor);
        String imageName = textColor + voltage / 10;
        return context.getResources().getIdentifier(imageName, "drawable", "com.voidgreen.voltagenotification");

    }
}
