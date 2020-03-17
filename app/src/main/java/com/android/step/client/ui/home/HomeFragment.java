package com.android.step.client.ui.home;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.step.R;

public class HomeFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "HomeFragment";

    private SensorManager sensorManager;
    private Sensor sensor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

//
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//
//        Log.d(TAG, "onCreateView: " + sensor.getName());

        return root;
    }


    public Object getSystemService(String service) {
        return getContext().getSystemService(service);
    }

    @Override
    public void onSensorChanged(SensorEvent e) {

        Log.d(TAG, "onSensorChanged: " + e.values);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onResume() {
        super.onResume();
//        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
//        sensorManager.unregisterListener(this);
    }
}