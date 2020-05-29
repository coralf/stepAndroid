package com.android.step.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;

import com.android.step.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import static org.litepal.LitePalApplication.getContext;

public class LineView {

    private String label;
    private LineChart lineChart;
    protected Typeface tfLight;
    private int color;


    public LineView(LineChart lineChart, String label, int lineColor) {
        this.lineChart = lineChart;
        this.label = label;
        this.color = lineColor;
        tfLight = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Light.ttf");
        initLineChart(lineChart);
    }

    private void initLineChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.animateXY(2000, 2000);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);
        chart.setBackgroundColor(Color.WHITE);
        LineData data = new LineData();
        data.setValueTextColor(Color.argb(255, 255, 255, 255));
        data.setDrawValues(false);

        XAxis xl = chart.getXAxis();
        xl.setTypeface(tfLight);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        chart.setData(data);

    }


    public void setLineChartData(float value) {
        LineData data = this.lineChart.getData();
        if (data != null) {
            ILineDataSet dataSet = data.getDataSetByIndex(0);
            if (dataSet == null) {
                dataSet = createSet(this.label);
                data.addDataSet(dataSet);
            }
            data.addEntry(new Entry(dataSet.getEntryCount(), value), 0);
            data.notifyDataChanged();
            this.lineChart.notifyDataSetChanged();
            this.lineChart.setVisibleXRangeMaximum(100);
            this.lineChart.moveViewToX(data.getEntryCount());
        }
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
        set.setColor(this.color);
        set.setDrawHorizontalHighlightIndicator(false);
        return set;
    }


    private void initLineChart2(LineChart lineChart, String label) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(createSet(label));
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
        xl.setTypeface(tfLight);
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
    }


}
