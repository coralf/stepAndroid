package com.android.step.server.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.android.step.db.RecordAccelerometer;
import com.android.step.utils.Config;
import com.android.step.utils.TimeUtils;
import com.android.step.view.LineView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.android.step.R;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.litepal.LitePalApplication.getContext;

public class OnceGaitRecodActivity extends AppCompatActivity implements SensorEventListener {


    private static final String TAG = "OnceGaitRecodActivity";

    private SensorManager sensorManager;
    private Sensor sensor;

    private LineChart lineChartX;

    private LineView lineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_once_gait_recod);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Intent intent = getIntent();
        String s = intent.getStringExtra("param");
        toolbar.setTitle(s);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lineChartX = findViewById(R.id.chartLineX);
        lineView = new LineView(lineChartX, "水平加速度", getResources().getColor(R.color.colorAccent));
        startUpateLineData();


    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                RecordAccelerometer accelerometer = (RecordAccelerometer) msg.obj;
                lineView.setLineChartData(accelerometer.getX());
            }
            // 要做的事情
            super.handleMessage(msg);
        }
    };

    private void startUpateLineData() {
        new Thread(() -> {
            List<RecordAccelerometer> accelerometerList = LitePal.findAll(RecordAccelerometer.class);
            Queue<RecordAccelerometer> queue = new LinkedList<>();
            for (RecordAccelerometer item : accelerometerList) {
                queue.offer(item);
            }
            if (queue.isEmpty()) {
                return;
            }
            try {
                while (!queue.isEmpty()) {
                    RecordAccelerometer accelerometer = queue.poll();
                    Message message = new Message();
                    message.what = 1;
                    message.obj = accelerometer;
                    Log.d(TAG, "startUpateLineData: " + accelerometer.getX());
                    queue.offer(accelerometer);
                    handler.sendMessage(message);
                    Thread.sleep(30);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.8f;
        float gravity[] = new float[3];
        float linearAcceleration[] = new float[3];
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
        linearAcceleration[0] = event.values[0] - gravity[0];
        linearAcceleration[1] = event.values[1] - gravity[1];
        linearAcceleration[2] = event.values[2] - gravity[2];
//        lineView.setLineChartData(event.values[0]);
//        RecordAccelerometer recordAccelerometer = new RecordAccelerometer(TimeUtils.getNowDateString(), event.values[0], event.values[0], event.values[0], 1);
//        recordAccelerometer.save();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void finish() {
        super.finish();
    }
}
