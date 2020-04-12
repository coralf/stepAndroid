package com.android.step.server.ui.config;

import android.util.Log;

import com.github.mikephil.charting.formatter.ValueFormatter;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DayFormatter extends ValueFormatter {


    @Override
    public String getFormattedValue(float value) {
        Log.d(TAG, "getFormattedValue: " + value);
        return ((int) value) + "日";
    }
}
