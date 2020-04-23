package com.android.step.client.ui.visualization.fragments;

import android.content.Context;
import android.graphics.Color;
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
import androidx.fragment.app.FragmentActivity;

import com.android.step.R;
import com.android.step.client.LeftActivity;
import com.android.step.client.net.Client;
import com.android.step.model.Orientation;
import com.android.step.utils.Config;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class GPSFragment extends Fragment implements SensorEventListener {


    private LineChart lineChartX;
    private LineChart lineChartY;
    private LineChart lineChartZ;
    private SensorManager sensorManager;
    private Sensor sensor;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_visualization_gps, container, false);


        lineChartX = root.findViewById(R.id.chartLineX);
        lineChartY = root.findViewById(R.id.chartLineY);
        lineChartZ = root.findViewById(R.id.chartLineZ);

        initLineChart(lineChartX, Config.clientX);
        initLineChart(lineChartY, Config.clientY);
        initLineChart(lineChartZ, Config.clientZ);


        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        int z = (int) event.values[0];
        int x = (int) event.values[1];
        int y = (int) event.values[2];

        String jsonStr = new Gson().toJson(new Orientation(x, y, z));
        Client.getClient().send(jsonStr, getContext());
        setLineChartData(lineChartX, x, Config.clientX);
        setLineChartData(lineChartY, y, Config.clientY);
        setLineChartData(lineChartZ, z, Config.clientZ);
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

 /*       if (label.equals(Orientation.X)) {
            set.setColor(ColorTemplate.getHoloBlue());
        } else if (label.equals(Orientation.Y)) {
            set.setColor(getResources().getColor(R.color.colorAccent));
        }*/

        set.setColor(getResources().getColor(R.color.colorAccent));

//        set.setFillColor(Color.WHITE);
//        set.setFillAlpha(100);
        set.setDrawHorizontalHighlightIndicator(false);
        return set;
    }

    private void initLineChart(LineChart lineChart, String label) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(createSet(label));
//        dataSets.add(createSet("y"));
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
//        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
//        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
    }

    private void setLineChartData(LineChart lineChart, float client, String label) {
        LineData data = lineChart.getData();
        if (data != null) {
            ILineDataSet clientDataSet = data.getDataSetByIndex(0);
//            ILineDataSet setY = data.getDataSetByIndex(1);
            // set.addEntry(...); // can be called as well

            if (clientDataSet == null) {
                clientDataSet = createSet(label);
                data.addDataSet(clientDataSet);
            }

      /*      if (setY == null) {
                setY = createSet("y");
                data.addDataSet(setY);
            }*/

            data.addEntry(new Entry(clientDataSet.getEntryCount(), client), 0);
//            data.addEntry(new Entry(setY.getEntryCount(), clientX), 1);

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

}
