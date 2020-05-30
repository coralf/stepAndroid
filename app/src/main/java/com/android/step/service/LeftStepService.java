package com.android.step.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.step.gsensor.AccChangeListener;
import com.android.step.gsensor.StepCount;
import com.android.step.gsensor.StepDetector;
import com.android.step.gsensor.StepService;
import com.android.step.gsensor.StepValuePassListener;
import com.android.step.gsensor.UpdateUiCallBack;

/**
 * Created by finnfu on 16/9/27.
 */

/*
 * 后台计步的service
 * */

public class LeftStepService extends StepService {

    private static final String TAG = "LeftStepService";

    @Override
    protected void changedStepData(int currCount, float oriValue, float oriValue1, float oriValue2) {
        Log.d(TAG, "LeftStepService: " + currCount + "," + oriValue + "," + oriValue1 + "," + oriValue2);
    }
}
