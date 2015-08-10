package com.voidgreen.voltagenotification.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.voidgreen.voltagenotification.R;

/**
 * Created by Void on 28-Jun-15.
 */
public class SharedPrefUtility {
    private final static int DEFAULT = 0;



    public static boolean isSoundEnabled(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean(resources.getString(R.string.pref_key_sound), false);
    }

    public static boolean isStartOnBootEnabled(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean(resources.getString(R.string.pref_key_start_on_boot), false);
    }

    public static boolean isVibrationEnabled(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean(resources.getString(R.string.pref_key_vibration), false);
    }

    public static boolean is30sEnabled(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean(resources.getString(R.string.pref_key_30s_notification), false);
    }

    public static boolean isPCmodeEnabled(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean(resources.getString(R.string.pref_key_pc_mode), false);
    }

}
