package com.android.step.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;

import com.android.step.R;
import com.android.step.utils.Config;
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

    private final String label1;
    private String label;
    private LineChart lineChart;
    protected Typeface tfLight;
    private int color;
    private int color1;


    public LineView(LineChart lineChart, String label, String label1, int lineColor, int lineColor1) {
        this.lineChart = lineChart;
        this.label = label;
        this.label1 = label1;
        this.color = lineColor;
        this.color1 = lineColor1;
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


        LineData data1 = new LineData();
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
        chart.setData(data1);

    }


    public void setLineChartData(float value, float value1) {
        LineData data = this.lineChart.getData();
        if (data != null) {
            ILineDataSet dataSet = data.getDataSetByIndex(0);
            ILineDataSet dataSet1 = data.getDataSetByIndex(1);
            if (dataSet == null) {
                dataSet = createSet(this.label);
                data.addDataSet(dataSet);
            }

            if (dataSet1 == null) {
                dataSet1 = createSet(this.label1);
                data.addDataSet(dataSet1);
            }

            data.addEntry(new Entry(dataSet.getEntryCount(), value), 0);
            data.addEntry(new Entry(dataSet1.getEntryCount(), value1), 1);
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
        if (label.equals(Config.ACC_X_S)) {
            set.setColor(this.color);
        } else {
            set.setColor(this.color1);
        }
        set.setDrawHorizontalHighlightIndicator(false);
        return set;
    }


}
