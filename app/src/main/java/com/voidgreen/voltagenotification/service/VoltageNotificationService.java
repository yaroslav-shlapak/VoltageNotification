package com.voidgreen.voltagenotification.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.voidgreen.voltagenotification.MainActivity;
import com.voidgreen.voltagenotification.R;
import com.voidgreen.voltagenotification.utilities.Constants;
import com.voidgreen.voltagenotification.utilities.Utility;

/**
 * Created by y.shlapak on Aug 10, 2015.
 */
public class VoltageNotificationService extends Service {
    private Notification.Builder notificationBuilder;
    final public static String TAG = "VoltageNotificationService";
    private final IBinder mBinder = new NotificationServiceBinder();
    private String state = "start";
    private NotificationManager mNotificationManager;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class NotificationServiceBinder extends Binder {
        public VoltageNotificationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return VoltageNotificationService.this;
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(Constants.DEBUG_TAG, "NotificationService : onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Resources resources = getResources();
        Log.d(Constants.DEBUG_TAG, "NotificationService : onStartCommand");


        startNotification(R.string.notificationTitle, R.string.notificationTickerTitle, R.drawable.voltage_white, R.drawable.voltage_white);
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(Constants.DEBUG_TAG, "NotificationService : onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //getApplicationContext().unregisterReceiver(screenOnOffReceiver);
        Log.d(Constants.DEBUG_TAG, "NotificationService : onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utility.saveTimeString(getApplicationContext(), Constants.ZERO_PROGRESS);
        Log.d(Constants.DEBUG_TAG, "NotificationService : onDestroy");
        mNotificationManager.cancel(Constants.NOTIFICATION_COUNTDOWN_ID);
    }

    private void startNotification(int titleText, int tickerText, int smallIcon, int largeIcon) {
        notificationBuilder = setNotification(titleText, tickerText, smallIcon, largeIcon, true);

        notificationBuilder.setContentText("");

        buildNotification(Constants.NOTIFICATION_COUNTDOWN_ID, notificationBuilder, true);

    }

    private void updateNotification(String notificationString) {
        if (notificationBuilder != null) {
            notificationBuilder.setContentText(notificationString);
            //startForeground(Constants.NOTIFICATION_COUNTDOWN_ID, notificationBuilder.build());
            mNotificationManager.notify(Constants.NOTIFICATION_COUNTDOWN_ID, notificationBuilder.build());
        }
    }

    private void buildNotification(int notificationId, Notification.Builder notificationBuilder, boolean startForegroundEnable) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        Notification notification = notificationBuilder.build();
        mNotificationManager.notify(notificationId, notification);
        if(startForegroundEnable) {
            startForeground(notificationId, notification);
        }
    }

    private Notification.Builder setNotification(int titleText, int tickerText, int smallIcon, int largeIcon, boolean onGoingEnable) {
        Resources resources = getResources();
        Notification.Builder notificationBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(smallIcon)
                        .setTicker(resources.getString(tickerText))
                        .setContentTitle(resources.getString(titleText))
                        .setOngoing(onGoingEnable);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

/*        Bitmap bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), largeIcon),
                getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
                getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_height),
                true);
        notificationBuilder.setLargeIcon(bm);*/

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        notificationBuilder.setContentIntent(resultPendingIntent);
        return notificationBuilder;
    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

}
