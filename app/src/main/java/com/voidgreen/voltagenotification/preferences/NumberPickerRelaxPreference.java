package com.voidgreen.voltagenotification.preferences;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by y.shlapak on Jun 25, 2015.
 */
public class NumberPickerRelaxPreference extends NumberPickerPreference {
    public static final int MAX_VALUE = 300;
    public static final int MIN_VALUE = 10;
    public static final int STEP = 10;
    public final String[] VALUES = getValues(MIN_VALUE, MAX_VALUE, STEP);

    public NumberPickerRelaxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberPickerRelaxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        picker.setMinValue(0);
        picker.setMaxValue(VALUES.length - 1);
        picker.setDisplayedValues(VALUES);
        picker.setValue(getValue());
    }

    public static int getNumValue(int index) {
        return index * STEP + MIN_VALUE;
    }

}
