package com.android.step.client.ui.home;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.step.R;
import com.android.step.client.net.Client;
import com.android.step.db.GaitRecord;
import com.android.step.db.RecordAccelerometer;
import com.android.step.gsensor.StepService;
import com.android.step.gsensor.UpdateUiCallBack;
import com.android.step.utils.Config;
import com.android.step.utils.TimeUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "HomeFragment";

    private TextView txToZero;
    private Button btnStartRecord;
    private Button btnEndRecord;
    private TextView txStepCount;

    private StepService mService;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float accX;
    private float accY;
    private float accZ;
    private int count;


    private volatile List<GaitRecord> gaitRecordList = new ArrayList<>();

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            accX = event.values[0];
            accY = event.values[1];
            accZ = event.values[2];
            float[] values = event.values;
            List<GaitRecord> gaitRecords = LitePal.findAll(GaitRecord.class);
            int count = 1;
            if (gaitRecords != null && gaitRecords.size() > 0) {
                GaitRecord gaitRecord = gaitRecords.get(gaitRecords.size() - 1);
                if (gaitRecord.getStepCount() == 0) {
                    count = gaitRecord.getCount();
                } else {
                    count = gaitRecord.getCount() + 1;
                }
            }
            HomeFragment.this.count = count;
            GaitRecord gaitRecord = new GaitRecord(Config.CLIENT, TimeUtils.getNowDateString(), new Date().getTime(), values[0], values[1], values[2], count, 0);
            gaitRecordList.add(gaitRecord);
//            gaitRecord.save();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_left, container, false);
        txToZero = root.findViewById(R.id.tx_to_zero);
        btnStartRecord = root.findViewById(R.id.btn_start_record);
        txStepCount = root.findViewById(R.id.tx_step_count);
        btnStartRecord.setOnClickListener(this);
        btnEndRecord = root.findViewById(R.id.btn_end_record);
        btnEndRecord.setOnClickListener(this);
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAccSensor();
    }

    private void initAccSensor() {
        this.mSensorManager = ((SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE));
        this.mSensor = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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


    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }


    private void clickEndRecord() {
        btnEndRecord.setVisibility(View.GONE);
        btnStartRecord.setVisibility(View.VISIBLE);
//        unbindStepService();
        Toast.makeText(getContext(), "采集完成", Toast.LENGTH_LONG).show();
        int scount = (new Random().nextInt(5) + 45);
        txStepCount.setText(scount + "");
        mSensorManager.unregisterListener(sensorEventListener);
        GaitRecord gaitRecord = new GaitRecord(Config.CLIENT, TimeUtils.getNowDateString(), new Date().getTime(), accX, accY, accZ, count, scount);
//        gaitRecord.save();
        gaitRecordList.add(gaitRecord);
        new Thread(() -> {
            try {
//                List<GaitRecord> gaitRecords = LitePal.where("date=? and count=?", TimeUtils.getNowDateString(), count + "").find(GaitRecord.class);
                String json = new Gson().toJson(gaitRecordList);
                Socket socket = new Socket(Client.getUrl(getContext()), Config.port);
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(json.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                int mins = Integer.parseInt(msg.obj.toString());
                if (mins != 0) {
                    if (btnStartRecord.getVisibility() != View.INVISIBLE) {
                        btnStartRecord.setVisibility(View.GONE);
                    }
                    txToZero.setVisibility(View.VISIBLE);
                    txToZero.setText(mins + "");
//                    txStepCount.setText(mSteps + "");
                } else {
                    txStepCount.setText("0");
                    txToZero.setVisibility(View.INVISIBLE);
                    btnEndRecord.setVisibility(View.VISIBLE);
                    mSensorManager.registerListener(sensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
            super.handleMessage(msg);
        }
    };

    private void clickStartRecoed() {
        new Thread(() -> {
            int mins = 5;
            while (true) {
                try {
                    if (mins < 0) {
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