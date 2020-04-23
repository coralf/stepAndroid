package com.android.step.server.ui.visualization.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.step.R;
import com.android.step.model.Orientation;
import com.android.step.server.server.Server;
import com.android.step.utils.Config;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import static android.content.ContentValues.TAG;

public class GPSFragment extends Fragment implements SensorEventListener {

    private LineChart lineChartX;
    private LineChart lineChartY;
    private LineChart lineChartZ;
    private SensorManager sensorManager;
    private Sensor sensor;
    private Queue<Orientation> orientationQueue = new LinkedList<>();
    protected Typeface tfLight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_visualization_gps, container, false);

        tfLight = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Light.ttf");

        lineChartX = root.findViewById(R.id.chartLineX);
        lineChartY = root.findViewById(R.id.chartLineY);
        lineChartZ = root.findViewById(R.id.chartLineZ);

        initLineChart(lineChartX, Config.serverX, Config.clientX);
        initLineChart(lineChartY, Config.serverY, Config.clientY);
        initLineChart(lineChartZ, Config.serverZ, Config.clientZ);

        return root;
    }


    private void initLineChart(LineChart lineChart, String serverLabel, String clientLabel) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(createSet(clientLabel));
        dataSets.add(createSet(serverLabel));
        LineData data = new LineData(dataSets);
        data.setDrawValues(false);
        lineChart.setData(data);
        lineChart.animateXY(2000, 2000);
        lineChart.setViewPortOffsets(0, 0, 0, 0);
//        lineChart.setBackgroundColor(Color.rgb(104, 241, 175));
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.getDescription().setEnabled(false);
//        lineChart.setDrawGridBackground(false);
        lineChart.setMaxHighlightDistance(300);

        XAxis xl = lineChart.getXAxis();
//        xl.setTypeface(tfLight);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = lineChart.getAxisLeft();
//        leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        Server server = Server.getServer();
        server.setOnServerMessageCallBack(msg -> {

            Orientation orientation = new Gson().fromJson(msg, Orientation.class);
            orientationQueue.offer(orientation);
        });

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        int z = (int) event.values[0];
        int x = (int) event.values[1];
        int y = (int) event.values[2];
        if (orientationQueue != null && orientationQueue.size() > 0) {
            Orientation orientation = orientationQueue.poll();
            assert orientation != null;
            setLineChartData(lineChartX, x, orientation.getX(), Config.serverX, Config.clientX);
            setLineChartData(lineChartY, y, orientation.getY(), Config.serverY, Config.clientY);
            setLineChartData(lineChartZ, z, orientation.getZ(), Config.serverZ, Config.clientZ);
        } else {
            setLineChartData(lineChartX, x, x, Config.serverX, Config.clientX);
            setLineChartData(lineChartY, y, y, Config.serverY, Config.clientY);
            setLineChartData(lineChartZ, z, z, Config.serverZ, Config.clientZ);
        }

    }

    private void setLineChartData(LineChart lineChart, float server, float client, String serverLabel, String clientLabel) {
        LineData data = lineChart.getData();
        if (data != null) {
            ILineDataSet serverDataSet = data.getDataSetByIndex(1);
            ILineDataSet clientDataSet = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (serverDataSet == null) {
                serverDataSet = createSet(serverLabel);
                data.addDataSet(serverDataSet);
            }

            if (clientDataSet == null) {
                clientDataSet = createSet(clientLabel);
                data.addDataSet(clientDataSet);
            }

            data.addEntry(new Entry(serverDataSet.getEntryCount(), server), 1);
            data.addEntry(new Entry(clientDataSet.getEntryCount(), client), 0);


            data.notifyDataChanged();

            // let the chart know it's data has changed
            lineChart.notifyDataSetChanged();

            // limit the number of visible entries
            lineChart.setVisibleXRangeMaximum(100);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            lineChart.moveViewToX(data.getEntryCount());

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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

    public Object getSystemService(String service) {
        return getContext().getSystemService(service);
    }

    private LineDataSet createSet(String label) {
        LineDataSet set = new LineDataSet(null, label);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        set.setDrawCircles(false);
        set.setLineWidth(1.8f);
        set.setCircleRadius(4f);
        set.setCircleColor(Color.WHITE);
        set.setHighLightColor(Color.rgb(244, 117, 117));

        if (label.equals(Config.clientX) || label.equals(Config.clientY) || label.equals(Config.clientZ)) {
            set.setColor(getResources().getColor(R.color.colorAccent));
        } else if (label.equals(Config.serverX) || label.equals(Config.serverY) || label.equals(Config.serverZ)) {
            set.setColor(ColorTemplate.getHoloBlue());
        }

//        set.setFillColor();
//        set.setFillAlpha(100);
        set.setDrawHorizontalHighlightIndicator(false);
        return set;
    }

}