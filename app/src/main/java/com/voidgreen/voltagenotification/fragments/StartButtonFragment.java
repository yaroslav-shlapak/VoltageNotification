package com.voidgreen.voltagenotification.fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.voidgreen.eyesrelax.R;
import com.voidgreen.eyesrelax.service.TimeService;

/**
 * Created by Void on 28-Jun-15.
 */
public class StartButtonFragment extends Fragment {
    OnStartButtonClickListener startButtonCallBack;

    public interface OnStartButtonClickListener {
        public void onStartButtonClick();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            startButtonCallBack = (OnStartButtonClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.start_button_layout, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        final Activity activity = getActivity();

        final Button startButton = (Button) activity.findViewById(R.id.startButton);
/*        float density = getResources().getDisplayMetrics().density;
        startButton.setTextSize(Constants.TEXT_SIZE * density);*/
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AnimationProgressBarUtility.start();
                Log.d("startButton", "Click");

                Resources resources = getResources();
                Intent intent = new Intent(activity, TimeService.class);
                intent.putExtra(resources.getString(R.string.serviceTask), resources.getString(R.string.startTask));
                intent.addCategory(TimeService.TAG);
                activity.startService(intent);


                startButtonCallBack.onStartButtonClick();
            }
        });
    }
}
