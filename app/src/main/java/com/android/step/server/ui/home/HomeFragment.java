package com.android.step.server.ui.home;

import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.step.R;
import com.github.mikephil.charting.charts.LineChart;

public class HomeFragment extends Fragment implements SensorEventListener, View.OnClickListener {

    private static final String TAG = "HomeFragment";

    private SensorManager sensorManager;
    private Sensor sensor;
    private LineChart lineStep;
    private TextView txToZero;
    private Button btnStartRecord;
    private Button btnEndRecord;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        txToZero = root.findViewById(R.id.tx_to_zero);
        btnStartRecord = root.findViewById(R.id.btn_start_record);
        btnStartRecord.setOnClickListener(this);
        btnEndRecord = root.findViewById(R.id.btn_end_record);
        btnEndRecord.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {

        Log.d(TAG, "onClick: ==========" + v.getId());
        switch (v.getId()) {
            case R.id.btn_start_record:
                clickStartRecoed();
                break;
            case R.id.btn_end_record:
                clickEndRecord();
                break;
            default:
                break;

        }
    }

    private void clickEndRecord() {
        btnEndRecord.setVisibility(View.GONE);
        btnStartRecord.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), "采集完成", Toast.LENGTH_LONG).show();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                int mins = Integer.parseInt(msg.obj.toString());
                if (mins != -1) {
                    if (btnStartRecord.getVisibility() != View.INVISIBLE) {
                        btnStartRecord.setVisibility(View.GONE);
                    }
                    txToZero.setVisibility(View.VISIBLE);
                    txToZero.setText(mins + "");
                } else {
                    txToZero.setVisibility(View.INVISIBLE);
                    btnEndRecord.setVisibility(View.VISIBLE);
                }
            }
            // 要做的事情
            super.handleMessage(msg);
        }
    };

    private void clickStartRecoed() {
        new Thread(() -> {
            int mins = 5;
            while (true) {
                try {
                    if (mins < -1) {
                        break;
                    }
                    Message message = new Message();
                    message.obj = mins--;
                    message.what = 1;
                    handler.sendMessage(message);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}