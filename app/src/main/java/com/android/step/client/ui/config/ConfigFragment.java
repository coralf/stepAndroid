package com.android.step.client.ui.config;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.step.R;
import com.android.step.db.Step;
import com.android.step.server.ui.config.DayFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import org.litepal.LitePal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ConfigFragment extends Fragment implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener,
        View.OnClickListener {
    TextView mTextMonthDay;

    TextView mTextYear;

    TextView mTextLunar;

    TextView mTextCurrentDay;

    CalendarView mCalendarView;

    RelativeLayout mRelativeTool;

    TextView mTxBarTitle;


    private int mYear;
    CalendarLayout mCalendarLayout;


    private BarChart barChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_config, container, false);

        initView(root);
        initData(root);
        return root;
    }


    public void initView(View root) {
        mTextMonthDay = root.findViewById(R.id.tv_month_day);
        mTextYear = root.findViewById(R.id.tv_year);
        mTextLunar = root.findViewById(R.id.tv_lunar);
        mRelativeTool = root.findViewById(R.id.rl_tool);
        mCalendarView = root.findViewById(R.id.calendarView);
        mTextCurrentDay = root.findViewById(R.id.tv_current_day);
        mCalendarLayout = root.findViewById(R.id.calendarLayout);
        mTxBarTitle = root.findViewById(R.id.tx_bar_title);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        root.findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToCurrent();
            }
        });
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
//        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
        setTextCurrentDay(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay());
        initLineView(root);

    }

    public int getDayStep(int year, int month, int day) {
        List<Step> steps = LitePal.where("year=? and month=? and day = ?", year + "", month + "", day + "").find(Step.class);
        if (steps.size() > 0) {
            Step step = steps.get(0);
            return step.getStepCount();
        }
        return 0;
    }


    private void initLineView(View root) {
        barChart = root.findViewById(R.id.barChart);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        barChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        barChart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);

        barChart.getLegend().setEnabled(false);
        ValueFormatter xAxisFormatter = new DayFormatter();
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(xAxisFormatter);


        setLineData(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay());
    }


    private void setLineData(int year, int month, int day) {
        try {
            List<Step> currentMonthStepData = getCurrentMonthStepData(year, month, day);

            ArrayList<BarEntry> values = new ArrayList<>();
            for (Step step : currentMonthStepData) {
                Log.d(TAG, "setLineData: day:" + step.getDay());
                values.add(new BarEntry(step.getDay(),
                        step.getStepCount()));
            }

            BarDataSet set1;
            if (barChart.getData() != null &&
                    barChart.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
//                set1.setLabel(mCalendarView.getCurMonth() + "月运动记录");
                set1.setValues(values);
                barChart.getData().notifyDataChanged();
                barChart.notifyDataSetChanged();

            } else {
                set1 = new BarDataSet(values, "");
                set1.setDrawIcons(false);
                ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);
                BarData data = new BarData(dataSets);
                data.setValueTextSize(10f);
                data.setBarWidth(0.9f);
                barChart.setData(data);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }


    protected void initData(View root) {
        List<Step> stepList = getAllSteps();
        Map<String, Calendar> map = new HashMap<>();

        for (Step step : stepList) {
            int year = step.getYear();
            int month = step.getMonth();
            int day = step.getDay();
            String stepCount = step.getStepCount() + "";
            map.put(getSchemeCalendar(year, month, day, R.color.colorAccentA, stepCount).toString(),
                    getSchemeCalendar(year, month, day, R.color.colorAccentA, stepCount));
        }
        mCalendarView.setSchemeDate(map);
        setStepData();
    }

    private List<Step> getAllSteps() {
        return LitePal.findAll(Step.class);
    }


    public void setStepData() {
        List<Step> stepList = LitePal.findAll(Step.class);
        long currentMillis = System.currentTimeMillis();
        if (stepList.size() < 1) {
            List<Step> stepData = new ArrayList<>();

            Random random = new Random(1);
            for (int i = 0; i < 100; i++) {
                Date now = new Date(currentMillis);
                currentMillis -= 24 * 60 * 60 * 1000;
                new Random(1);
                Step step = new Step();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String formatDate = sdf.format(now);
                step.setDate(formatDate);
//                step.setDate(now.getTime() + "");
                int stepCount = random.nextInt(20000) + 500;
                String[] splitDate = formatDate.split("-");
                step.setYear(Integer.parseInt(splitDate[0]));
                step.setMonth(Integer.parseInt(splitDate[1]));
                step.setDay(Integer.parseInt(splitDate[2]));
                step.setStepCount(stepCount);
                step.save();
            }
        }

    }


    @Override
    public void onClick(View v) {


    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);
        calendar.setScheme(text);
        return calendar;
    }


    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mYear = calendar.getYear();
        int month = calendar.getMonth();
        int day = calendar.getDay();
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(month + "月" + day + "日");
        mTextYear.setText(String.valueOf(mYear));
        mTextLunar.setText(calendar.getLunar());


        setTextCurrentDay(mYear, month, day);
        resetBarData(mYear, month, day);

    }

    private void resetBarData(int year, int month, int day) {
        mTxBarTitle.setText(month + "月运动统计");
        setLineData(year, month, day);
        barChart.invalidate();

    }

    public void setTextCurrentDay(int year, int month, int day) {
        int step = getDayStep(year, month, day);
        mTextCurrentDay.setText((step + "步"));
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }


    private List<Step> getCurrentMonthStepData(int year, int month, int day) throws ParseException {
//        String dateStr = currentYear + "-" + month + "-" + day;
//        Log.d(TAG, "getCurrentMonthStepData: dateStr" + dateStr);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date parse = sdf.parse(dateStr);
//        String formatDate = sdf.format(parse);
//        List<Step> stepList = LitePal.where("datetime(date) >= datetime('" + formatDate + "','start of month','+0 month','-0 day') and \n" +
//                "datetime(date) < datetime('" + formatDate + "','start of month','+1 month','0 day')").find(Step.class);
        List<Step> stepList = LitePal.where("year = ? and month = ?", year + "", month + "").find(Step.class);
//        Log.d(TAG, "setLineData: stepList len:" + stepList.size());
//        for (Step step : stepList) {
//            Log.d(TAG, "getCurrentMonthStepData: " + step.toString());
//        }
        return stepList;
    }


}