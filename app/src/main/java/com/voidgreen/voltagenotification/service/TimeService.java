package com.voidgreen.voltagenotification.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.voidgreen.eyesrelax.MainActivity;
import com.voidgreen.eyesrelax.R;
import com.voidgreen.eyesrelax.utilities.Constants;
import com.voidgreen.eyesrelax.utilities.CountDownTimerWithPause;
import com.voidgreen.eyesrelax.utilities.SharedPrefUtility;
import com.voidgreen.eyesrelax.utilities.SoundUtility;
import com.voidgreen.eyesrelax.utilities.Utility;
import com.voidgreen.eyesrelax.utilities.VibratorUtility;

/**
 * Created by Void on 29-Jun-15.
 */
public class TimeService extends Service {
    private EyesRelaxCountDownTimer timer;
    private NotificationCompat.Builder notificationBuilder;
    private Notification notification;
    final public static String TAG = "TimeService";
    private LocalBroadcastManager broadcaster;
    private final IBinder mBinder = new TimeBinder();
    private String state = "start";
    private NotificationManager mNotificationManager;
    BroadcastReceiver screenOnOffReceiver;
    private boolean uiForbid;

    public void setStage(String stage) {
        this.stage = stage;
        sendStageString(stage);
        //Log.d("TimeService", "setStage : " + stage );
    }

    private String stage = "work";
    private long  stageTime;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class TimeBinder extends Binder {
        public TimeService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TimeService.this;
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        Log.d("TimeService", "setState : " + state);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        registerBroadcastReceiver();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Resources resources = getResources();
        Log.d("onStartCommand", "onStartCommand");

        String task = "";
        if (intent !=null && intent.getExtras()!=null) {
            task = intent.getStringExtra(resources.getString(R.string.serviceTask));
        }
        uiForbid = true;
        Log.d("task", task);
        Log.d("stage", stage);
        timeSequence(task, stage);
        return super.onStartCommand(intent, flags, startId);
    }

    private void timeSequence(String task, String stage) {
        Context context = getApplicationContext();
        sendStageString(stage);

        switch (task) {
            case "start":
                //Utility.showToast(context, "onHandleIntent:start");
                setState("stop");
                uiForbid = false;
                Log.d("onStartCommand", "start");
                if(timer == null) {
                    //Log.d("onStartCommand", "" + (SettingsDataUtility.getWorkTime(context)));
                    //Log.d("onStartCommand", "" + (SettingsDataUtility.getRelaxTime(context)));
                    switch (stage) {
                        case "work":
                            if(Utility.isScreenOn(context) || SharedPrefUtility.isPCmodeEnabled(context)) {
                                startCountdownNotification(R.string.workStageTitle, R.string.workStageMessage,
                                        R.drawable.ic_eye_open, R.drawable.eye_white_open_notification_large);
                                stageTime = SharedPrefUtility.getWorkTime(context) * Constants.MIN_TO_MILLIS_MULT
                                        + Constants.SEC_TO_MILLIS_MULT;
                                timer = new EyesRelaxCountDownTimer(stageTime, Constants.TICK_PERIOD, true);
                                timer.create();
                            }
                            break;
                        case "relax":
                            startCountdownNotification(R.string.relaxStageTitle, R.string.relaxStageMessage,
                                    R.drawable.ic_eye_closed, R.drawable.eye_white_closed_notification_large);
                            stageTime = SharedPrefUtility.getRelaxTime(context) * Constants.SEC_TO_MILLIS_MULT
                                    + Constants.SEC_TO_MILLIS_MULT;
                            timer = new EyesRelaxCountDownTimer(stageTime, Constants.TICK_PERIOD, true);
                            timer.create();
                            break;
                        default:
                            stageTime = 0;
                            break;
                    }
                    Log.d("timeSequence", "start");
                }
                break;

            case "pause":
                pauseTimer();
                break;

            case "resume":
                uiForbid = false;
                resumeTimer();
                break;

            case "stop":
                stopTimer();
                stopSelf();

                break;

            default:
                //Utility.showToast(context, "onHandleIntent:default");
                Log.d("onStartCommand", "default");
                break;
        }
    }

    private void stopTimer() {
        setStage("work");
        if(timer != null) {
            timer.cancel();
            Log.d("timeSequence", "stop");
        }
        timer = null;
        mNotificationManager.cancel(Constants.NOTIFICATION_FINISHED_ID);
        setState("start");
        sendStageString("");
    }

    private void pauseTimer() {
        setState("resume");
        if(timer != null) {
            Log.d("timeSequence", "pause");
            timer.pause();
        }
    }

    private void resumeTimer() {
        setState("pause");
        if(timer != null) {
            Log.d("timeSequence", "resume");
            timer.resume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
        Utility.saveTimeString(getApplicationContext(), Constants.ZERO_PROGRESS);
        sendTimeString(Constants.ZERO_PROGRESS);
        sendStageString("");
        mNotificationManager.cancel(Constants.NOTIFICATION_COUNTDOWN_ID);
        getApplicationContext().unregisterReceiver(screenOnOffReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("TimeService", "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //getApplicationContext().unregisterReceiver(screenOnOffReceiver);
        return super.onUnbind(intent);
    }


    private class EyesRelaxCountDownTimer extends CountDownTimerWithPause {
        private boolean preRelaxNotification = true;

        public EyesRelaxCountDownTimer(long millisOnTimer, long countDownInterval, boolean runAtStart) {
            super(millisOnTimer, countDownInterval, runAtStart);
        }


        @Override
        public void onTick(long millisUntilFinished) {
            // Puts the status into the Intent
            millisUntilFinished = (long)(Math.floor(millisUntilFinished / 1000) * 1000);
            if(millisUntilFinished > (stageTime - 1)) {
                millisUntilFinished--;
            }
            // Broadcasts the Intent to receivers in this app.
            //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
            String notificationString = Utility.combinationFormatter(millisUntilFinished);
            Log.d("onTick", "" + stageTime);
            Log.d("onTick", "" + millisUntilFinished);
            Log.d("onTick", notificationString);
            updateNotification(Constants.TIME_LEFT + notificationString);
            sendTimeString(notificationString);
            Context context = getApplicationContext();
            if(SharedPrefUtility.is30sEnabled(context) && millisUntilFinished < 30 * Constants.SEC_TO_MILLIS_MULT) {
                if(preRelaxNotification) {
                    preRelaxNotification = false;
                    switch (stage) {
                        case "work":
                            startTimerFinishedNotification(R.string.prerelaxStageTitle, R.string.prerelaxStageTitle,
                                    R.drawable.ic_eye_open, R.drawable.eye_white_open_notification_large);

                            VibratorUtility.vibrateShort(context);
                            SoundUtility.playNotify(context);
                            break;
                        case "relax":

                            break;
                        default:
                            break;
                    }
                }
            }
        }

        @Override
        public void onFinish() {
            finishAll();
            VibratorUtility.vibrateLong(getApplicationContext());
            Context context = getApplicationContext();

            //Log.d("TimerFinished", stage);
            //stopForeground(true);
            switch (stage) {
                case "work":
                    setStage("relax");
                    mNotificationManager.cancel(Constants.NOTIFICATION_FINISHED_ID);
                    SoundUtility.playWorkEnd(context);
                    break;
                case "relax":
                    setStage("work");
                    SoundUtility.playRelaxEnd(context);

                    break;
                default:
                    break;
            }

            //stopForeground(true);
            notificationBuilder.setOngoing(false);
            mNotificationManager.cancel(Constants.NOTIFICATION_COUNTDOWN_ID);
            timeSequence("start", stage);

        }
    }

    private void updateNotification(String notificationString) {
        if (notificationBuilder != null) {
            notificationBuilder.setContentText(notificationString);
            //startForeground(Constants.NOTIFICATION_COUNTDOWN_ID, notificationBuilder.build());
            mNotificationManager.notify(Constants.NOTIFICATION_COUNTDOWN_ID, notificationBuilder.build());
        }
    }

    private void finishAll() {
        sendTimeString(Constants.ZERO_PROGRESS);
        updateNotification(Constants.ZERO_PROGRESS);

        Utility.saveTimeString(getApplicationContext(), Constants.ZERO_PROGRESS);
        setState("start");
        timer = null;

    }

    private void startCountdownNotification(int titleText, int tickerText, int smallIcon, int largeIcon) {
        notificationBuilder = setNotification(titleText, tickerText, smallIcon, largeIcon, true);

        notificationBuilder.setContentText("");

        buildNotification(Constants.NOTIFICATION_COUNTDOWN_ID, notificationBuilder, true);

    }

    private void startTimerFinishedNotification(int titleText, int tickerText, int smallIcon, int largeIcon) {
        NotificationCompat.Builder notificationBuilder = setNotification(titleText, tickerText, smallIcon, largeIcon, false);
        buildNotification(Constants.NOTIFICATION_FINISHED_ID, notificationBuilder, false);

    }

    private void buildNotification(int notificationId, NotificationCompat.Builder notificationBuilder, boolean startForegroundEnable) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        Notification notification = notificationBuilder.build();
        mNotificationManager.notify(notificationId, notification);
        if(startForegroundEnable) {
            startForeground(notificationId, notification);
        }
    }

    private NotificationCompat.Builder setNotification(int titleText, int tickerText, int smallIcon, int largeIcon, boolean onGoingEnable) {
        Resources resources = getResources();
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
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

    public void sendTimeString(String message) {
        Intent intent = new Intent(Constants.BROADCAST_TIME_STRING_NAME);
        if(message != null) {
            intent.putExtra(Constants.BROADCAST_TIME_STRING_DATA, message);
        }
        broadcaster.sendBroadcast(intent);
    }

    public void sendStageString(String message) {
        Intent intent = new Intent(Constants.BROADCAST_STAGE_NAME);
        if(message != null) {
            intent.putExtra(Constants.BROADCAST_STAGE_DATA, message);
        }
        broadcaster.sendBroadcast(intent);
    }



    private void registerBroadcastReceiver() {
        final IntentFilter theFilter = new IntentFilter();
        /** System Defined Broadcast */
        theFilter.addAction(Intent.ACTION_SCREEN_ON);
        theFilter.addAction(Intent.ACTION_SCREEN_OFF);
        theFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);

        screenOnOffReceiver = new ScreenBroadcastReceiver();
        getApplicationContext().registerReceiver(screenOnOffReceiver, theFilter);
        Log.d("TimeService", "registerBroadcastReceiver");
    }

    public class ScreenBroadcastReceiver extends BroadcastReceiver {
        CountDownTimer countDownTimer = null;
        @Override
        public void onReceive(Context context, Intent intent) {
            String strAction = intent.getAction();
            String telephonyExtra = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            Log.d("ScreenBroadcastReceiver", strAction);
            Log.d("ScreenBroadcastReceiver", "" + SharedPrefUtility.isPCmodeEnabled(context));
            Log.d("ScreenBroadcastReceiver", "" + uiForbid);
            Log.d("ScreenBroadcastReceiver", "" + (!SharedPrefUtility.isPCmodeEnabled(context) && !uiForbid && stage.contentEquals("work")));
            if(!SharedPrefUtility.isPCmodeEnabled(context) && !uiForbid && stage.contentEquals("work")) {
                Log.d("ScreenBroadcastReceiver", "" + strAction.equals(Intent.ACTION_SCREEN_OFF));
                if (strAction.equals(Intent.ACTION_SCREEN_OFF) || (telephonyExtra != null
                        && (telephonyExtra.equals(TelephonyManager.EXTRA_STATE_RINGING)
                        || telephonyExtra.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)))) {
                    if(countDownTimer == null) {
                        pauseTimer();
                        setState("pause");
                        Log.d("ScreenBroadcastReceiver", "pause");
                        countDownTimer = new CountDownTimer(
                                SharedPrefUtility.getRelaxTime(context) * Constants.SEC_TO_MILLIS_MULT,
                                Constants.SEC_TO_MILLIS_MULT) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                stopTimer();
                                setState("pause");
                                Log.d("ScreenBroadcastReceiver", "onFinish");
                            }
                        };
                        countDownTimer.start();
                    }
                    //System.out.println("Screen off " + "LOCKED");
                } else if(strAction.equals(Intent.ACTION_SCREEN_ON) || (telephonyExtra != null
                        && telephonyExtra.equals(TelephonyManager.EXTRA_STATE_IDLE))) {
                    if (timer != null) {
                        resumeTimer();
                        Log.d("ScreenBroadcastReceiver", "resume");
                    } else {
                        timeSequence("start", "work");

                        Log.d("ScreenBroadcastReceiver", "start");
                    }

                    if(countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                        Log.d("ScreenBroadcastReceiver", "cancelTimer");
                    }

                    //System.out.println("Screen off " + "UNLOCKED");
                }
            }
        }

    };



}
