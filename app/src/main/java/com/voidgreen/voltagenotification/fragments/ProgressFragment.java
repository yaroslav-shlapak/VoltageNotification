package com.voidgreen.voltagenotification.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.voidgreen.voltagenotification.R;
import com.voidgreen.voltagenotification.utilities.Constants;
import com.voidgreen.voltagenotification.utilities.Utility;

/**
 * Created by Void on 28-Jun-15.
 */
public class ProgressFragment extends Fragment {
    TextView progressTextView;
    TextView stageTextView;
    TextView timeLeftTextView;
    BroadcastReceiver timeStringReceiver;
    BroadcastReceiver stageStringReceiver;
    Activity activity;
    String progress = Constants.ZERO_PROGRESS;
    String stage = "";
    String timeLeft = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        updateProgressFromBundle(savedInstanceState);

        Log.d(Constants.LOG_ID, "onCreate " + stage);

        timeStringReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateProgressTextView(intent);

            }
        };
        stageStringReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateStageTextView(intent);
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.progress_layout, container, false);
        progressTextView = (TextView) view.findViewById(R.id.textViewTime);
        stageTextView = (TextView) view.findViewById(R.id.textViewStage);
        timeLeftTextView = (TextView) view.findViewById(R.id.textViewTimeLeft);
        return view;
    }

    @Override
    public void onStart() {        super.onStart();


        //AnimationProgressBarUtility.initAnimation(progressBar, activity.getApplicationContext());

    }

    @Override
    public void onResume() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(activity).registerReceiver((timeStringReceiver),
                new IntentFilter(Constants.BROADCAST_TIME_STRING_NAME));
        LocalBroadcastManager.getInstance(activity).registerReceiver((stageStringReceiver),
                new IntentFilter(Constants.BROADCAST_STAGE_NAME));
        Intent intent = activity.getIntent();
        updateProgressTextView(intent);
        updateStageTextView(intent);


        super.onResume();
    }

    private void updateProgressFromBundle(Bundle bundle) {
        if(bundle != null) {
            String s1 = bundle.getString(Constants.PROGRESS_KEY);
            if(s1 != null) {
                progress = s1;
            }
            String s2 = bundle.getString(Constants.STAGE_KEY);
            if(s2 != null) {

                setStage(s2);
                Log.d(Constants.LOG_ID, "updateProgressFromBundle " + stage);
            }

        } else {
            Context context = activity.getApplicationContext();
            progress = Utility.getSavedTimeString(context);
            stage = Utility.getStageString(context);
        }
        setTimeLeft(stage);
    }

    private void setStage(String s) {
        Log.d(Constants.LOG_ID, "setStage " + s);
        switch(s) {
            case Constants.WORK_STAGE:
            case "work":
                stage = Constants.WORK_STAGE;
                break;
            case Constants.RELAX_STAGE:
            case "relax":
                stage = Constants.RELAX_STAGE;
                break;
            default:
                stage = "";
        }
    }

    private void setTimeLeft(String s) {
        switch(s) {
            case Constants.RELAX_STAGE:
            case Constants.WORK_STAGE:
                timeLeft = Constants.TIME_LEFT;
                break;
            default:
                timeLeft = "";
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.PROGRESS_KEY, progress);
        outState.putString(Constants.STAGE_KEY, stage);
        super.onSaveInstanceState(outState);
    }

    private void updateProgressTextView(Intent intent) {
        String s = intent.getStringExtra(Constants.BROADCAST_TIME_STRING_DATA);
        if(s != null) {
            progress = s;
        }
        progressTextView.setText(progress);
    }

    private void updateStageTextView(Intent intent) {
        String s = intent.getStringExtra(Constants.BROADCAST_STAGE_DATA);
        if(s != null) {

            setStage(s);
            Log.d(Constants.LOG_ID, "updateStageTextView " + stage);
        }
        stageTextView.setText(stage);
        setTimeLeft(stage);
        timeLeftTextView.setText(timeLeft);
        Utility.saveStageString(activity.getApplicationContext(), stage);

    }
    @Override
    public void onPause() {

        LocalBroadcastManager.getInstance(activity).unregisterReceiver(timeStringReceiver);
        Context context = activity.getApplicationContext();
        Utility.saveTimeString(context, progress);
        Utility.saveStageString(context, stage);

        putStringToBundle();
        super.onPause();
    }

    private void putStringToBundle() {
        Intent mIntent = new Intent(activity, ProgressFragment.class);
        Bundle extras = mIntent.getExtras();
        updateProgressFromBundle(extras);
    }


}
