package com.voidgreen.voltagenotification.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.voidgreen.eyesrelax.R;

/**
 * Created by Void on 29-Jun-15.
 */
public class TempProgressFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.progress_layout, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();




    }
}
