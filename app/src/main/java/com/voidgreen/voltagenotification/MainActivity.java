package com.voidgreen.voltagenotification;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.voidgreen.voltagenotification.service.VoltageNotificationService;
import com.voidgreen.voltagenotification.settings.SettingsActivity;
import com.voidgreen.voltagenotification.utilities.Constants;

public class MainActivity extends Activity {
    Button startStopButton;
    String state = "start";
    VoltageNotificationService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startStopButton = (Button) findViewById(R.id.pauseButton);
        startStopButton.setText(state);


        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStartStopButton();


            }
        });
        Log.d(Constants.DEBUG_TAG, "MainActivity : onCrate");
    }

    public void setState(String state) {
        this.state = state;
    }

    private void updateStartStopButton() {


        switch (state) {
            case "start":
                startNotificationService();
                startStopButton.setText("STOP");
                setState("stop");
                mService.setState(state);
                Log.d(Constants.DEBUG_TAG, "MainActivity : updateStartStopButton : start");
                break;

            case "stop":
                unbindNotificationService();
                startStopButton.setText("START");
                setState("start");
                mService.setState(state);
                stopNotificationService();

                Log.d(Constants.DEBUG_TAG, "MainActivity : updateStartStopButton : stop");
                break;

            default:

                startStopButton.setText("START");
                Log.d(Constants.DEBUG_TAG, "MainActivity : updateStartStopButton : default");

                break;
        }

    }

    private void updateUI() {

        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            String state = mService.getState();
            setState(state);

            switch (state) {
                case "start":
                    startStopButton.setText("START");

                    Log.d(Constants.DEBUG_TAG, "MainActivity : updateUI : start");
                    break;

                case "stop":
                    startStopButton.setText("STOP");
                    Log.d(Constants.DEBUG_TAG, "MainActivity : updateUI : stop");
                    break;

                default:
                    startStopButton.setText("START");
                    Log.d(Constants.DEBUG_TAG, "MainActivity : updateUI : default");

                    break;
            }
        } else {
            startStopButton.setText("START");
            Log.d(Constants.DEBUG_TAG, "MainActivity : updateUI : else");
        }
    }





    public void unbindNotificationService() {
        if (mBound) {
            Log.d(Constants.DEBUG_TAG, "MainActivity : unbindNotificationService");
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void bindNotificationService() {
        Intent intent = new Intent(this, VoltageNotificationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d(Constants.DEBUG_TAG, "MainActivity : bindNotificationService");
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Unbind from the service
        Log.d(Constants.DEBUG_TAG, "MainActivity : onPause");
        unbindNotificationService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Constants.DEBUG_TAG, "MainActivity : onResume");
        bindNotificationService();

    }

    private void startNotificationService() {
        Log.d(Constants.DEBUG_TAG, "MainActivity : startNotificationService");
        Intent intent = new Intent(this, VoltageNotificationService.class);
        intent.addCategory(VoltageNotificationService.TAG);
        startService(intent);

    }

    private void stopNotificationService() {
        Log.d(Constants.DEBUG_TAG, "MainActivity : stopNotificationService");
        Intent intent = new Intent(this, VoltageNotificationService.class);
        intent.addCategory(VoltageNotificationService.TAG);
        stopService(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Context context = getApplicationContext();
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.d(Constants.DEBUG_TAG, "mConnection : onServiceConnected");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            VoltageNotificationService.NotificationServiceBinder binder = (VoltageNotificationService.NotificationServiceBinder) service;
            mService = binder.getService();
            mBound = true;
            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(Constants.DEBUG_TAG, "mConnection : onServiceDisconnected");
            mBound = false;
        }
    };
}
