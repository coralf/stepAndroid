package com.android.step.server.ui.home;

import android.graphics.Color;
import android.graphics.Typeface;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "HomeFragment";

    private SensorManager sensorManager;
    private Sensor sensor;
    private LineChart lineStep;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        initView(root);

//
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//
//        Log.d(TAG, "onCreateView: " + sensor.getName());

        return root;
    }

    private void initView(View root) {
        lineStep = root.findViewById(R.id.line_step);
        lineStep.setViewPortOffsets(0, 0, 0, 0);
        lineStep.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // no description text
        lineStep.getDescription().setEnabled(false);

        // enable touch gestures
        lineStep.setTouchEnabled(true);

        // enable scaling and dragging
        lineStep.setDragEnabled(true);
        lineStep.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineStep.setPinchZoom(false);

        lineStep.setDrawGridBackground(false);
        lineStep.setMaxHighlightDistance(300);

        XAxis x = lineStep.getXAxis();
        x.setEnabled(false);

        YAxis y = lineStep.getAxisLeft();
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        lineStep.getAxisRight().setEnabled(false);


        lineStep.getLegend().setEnabled(false);

        lineStep.animateXY(2000, 2000);

        // don't forget to refresh the drawing
        lineStep.invalidate();
        setData(45, 43);
    }

    private void setData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * (range + 1)) + 20;
            values.add(new Entry(i, val));
        }

        LineDataSet set1;

        if (lineStep.getData() != null &&
                lineStep.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineStep.getData().getDataSetByIndex(0);
            set1.setValues(values);
            lineStep.getData().notifyDataChanged();
            lineStep.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.WHITE);
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.WHITE);
            set1.setFillColor(Color.WHITE);
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return lineStep.getAxisLeft().getAxisMinimum();
                }
            });

            // create a data object with the data sets
            LineData data = new LineData(set1);
//            data.setValueTypeface(tfLight);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            lineStep.setData(data);
        }
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