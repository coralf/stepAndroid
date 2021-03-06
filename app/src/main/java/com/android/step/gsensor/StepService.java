package com.android.step.gsensor;

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
import android.widget.Toast;

import androidx.annotation.Nullable;

public class StepService extends Service {
    private final IBinder mBinder = new StepBinder();
    private UpdateUiCallBack mCallback;
    private Sensor mSensor;
    private SensorManager mSensorManager;
    private StepCount mStepCount;
    private StepDetector mStepDetector;
    private PowerManager.WakeLock wakeLock;
    private SharedPreferences mSharePreference;
    private SharedPreferences.Editor mEdit;
    private int currCount = 0;
    private final static int GRAY_SERVICE_ID = 1001;

    private StepValuePassListener mValuePassListener = new StepValuePassListener() {
        @Override
        public void stepChanged(int steps) {
            StepService.this.currCount = steps;
            mEdit.putString("steps", steps + "");
            mEdit.commit();
            mCallback.updateUi();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    private AccChangeListener accChangeListener = (oriValue, oriValue1, oriValue2) -> {
        changedStepData(currCount, oriValue, oriValue1, oriValue2);
    };


    protected void changedStepData(int currCount, float oriValue, float oriValue1, float oriValue2) {

    }

    @SuppressLint("InvalidWakeLockTag")
    public void onCreate() {
        super.onCreate();
        this.wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(1, "StepService");
        this.wakeLock.acquire();
        this.mStepDetector = new StepDetector();
        this.mSensorManager = ((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        this.mSensor = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mSensorManager.registerListener(this.mStepDetector, this.mSensor, SensorManager.SENSOR_DELAY_UI);
        this.mStepCount = new StepCount();
        this.mStepCount.initListener(this.mValuePassListener);
        this.mStepDetector.initListener(this.mStepCount);
        this.mStepDetector.setAccChangeListener(accChangeListener);
        this.mSharePreference = getSharedPreferences("relevant_data", Activity.MODE_PRIVATE);
        this.mEdit = this.mSharePreference.edit();

    }


    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {
        Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startForeground(0, new Notification());
        } else {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }
        return START_STICKY;
    }


    public void onDestroy() {
        this.mSensorManager.unregisterListener(this.mStepDetector);
        Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
        this.wakeLock.release();
        mEdit.putString("steps", "0");
        mEdit.commit();
        super.onDestroy();
    }

    public void registerCallback(UpdateUiCallBack paramICallback) {
        this.mCallback = paramICallback;
    }

    public void resetValues() {
        mEdit.putString("steps", "0");
        mEdit.commit();
        this.mStepCount.setSteps(0);
    }

    public boolean onUnbind(Intent paramIntent) {
        return super.onUnbind(paramIntent);
    }

    public class StepBinder extends Binder {
        public StepService getService() {
            return StepService.this;
        }
    }


    public static class GrayInnerService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

}
