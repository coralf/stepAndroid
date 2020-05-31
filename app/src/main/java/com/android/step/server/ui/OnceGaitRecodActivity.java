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

import com.android.step.db.GaitRecord;
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

public class OnceGaitRecodActivity extends AppCompatActivity {


    private static final String TAG = "OnceGaitRecodActivity";


    private LineChart lineChartX;

    private LineView lineView;

    private String date;
    private int count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_once_gait_recod);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Intent intent = getIntent();
        count = intent.getIntExtra("count", 0);
        date = intent.getStringExtra("date");
        toolbar.setTitle(date);
        lineChartX = findViewById(R.id.chartLineX);
        lineView = new LineView(lineChartX, Config.ACC_X_S, Config.ACC_X_C, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary));
        startUpateLineData();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Acc accelerometer = (Acc) msg.obj;
                lineView.setLineChartData(accelerometer.accServerX, accelerometer.accClientX);
            }
            super.handleMessage(msg);
        }
    };

    private class Acc {
        float accClientX;
        float accServerX;
    }

    private void startUpateLineData() {
        new Thread(() -> {
            List<GaitRecord> accelerometerList = LitePal.where("date=? and count=?", date, count + "").order("time").find(GaitRecord.class);

            Queue<Acc> queueClient = new LinkedList<>();
            for (GaitRecord item : accelerometerList) {
                Acc acc = new Acc();
                if (item.getTerminal().equals(Config.CLIENT)) {
                    acc.accClientX = item.getAccX();
                } else {
                    acc.accServerX = item.getAccX();
                }
                queueClient.offer(acc);
            }
            if (queueClient.isEmpty()) {
                return;
            }
            try {
                while (!queueClient.isEmpty()) {
                    Acc accelerometer = queueClient.poll();
                    Message message = new Message();
                    message.what = 1;
                    message.obj = accelerometer;
                    queueClient.offer(accelerometer);
                    handler.sendMessage(message);
                    Thread.sleep(30);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
