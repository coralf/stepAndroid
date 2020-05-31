package com.android.step.server.ui.config;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.step.R;
import com.android.step.db.GaitRecord;
import com.android.step.db.Step;
import com.android.step.server.ui.OnceGaitRecodActivity;
import com.android.step.utils.TimeUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
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
        View.OnClickListener, CalendarView.OnCalendarLongClickListener {
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
        mCalendarView.setOnCalendarLongClickListener(this);
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

        barChart.setMaxVisibleValueCount(60);

        barChart.setPinchZoom(false);

        barChart.setDrawGridBackground(false);

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
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
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
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
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


    private void selectCurrentDay(int year, int month, int day) {

        String compDate = TimeUtils.getDateByNum(year, month, day);
        List<GaitRecord> gaitRecords = LitePal.where("date=?", compDate).order("count").find(GaitRecord.class);

        List<GaitRecord> currentGaitRecords = new ArrayList<>();
        for (GaitRecord gaitRecord : gaitRecords) {
            if (!sameCount(currentGaitRecords, gaitRecord.getCount())) {
                currentGaitRecords.add(gaitRecord);
            }
        }


//
//        List<String> items = new ArrayList<>();
//        for (int i = 1; i <= 50; i++) {
//            items.add("第" + i + "次采集记录");
//        }

        String[] countArr = new String[currentGaitRecords.size()];

        for (int i = 0; i < currentGaitRecords.size(); i++) {
            countArr[i] = "第" + currentGaitRecords.get(i).getCount() + "次步态采集记录";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("步态记录：" + year + "年" + month + "月" + day + "日");
        builder.setItems(countArr, (dialog, index) -> {
//            toOnceGaitActivity(currentGaitRecords.get(index));
            selectAlertDialog(currentGaitRecords.get(index));
        });
        builder.create().show();
    }

    private void selectAlertDialog(GaitRecord gaitRecord) {
        String[] strArr = {"查询", "删除"};
        new AlertDialog.Builder(getActivity())
                .setTitle("步态记录：" + TimeUtils.getDate(gaitRecord.getDate()))
                .setItems(strArr, (dialog, index) -> {
                    if (strArr[0].equals(strArr[index])) {
                        toOnceGaitActivity(gaitRecord);
                    } else if (strArr[1].equals(strArr[index])) {
                        confirmDelete(gaitRecord);
                    }
                })
                .setCancelable(true)
                .create().show();
    }

    private void confirmDelete(GaitRecord gaitRecord) {
        new AlertDialog.Builder(getContext())
                .setTitle("删除确认")
                .setMessage("确定要删除" + TimeUtils.getDate(gaitRecord.getDate()) + "第" + gaitRecord.getCount() + "次的步测记录吗？")
                .setPositiveButton("是", (dialog, which) -> {
                    int record = LitePal.deleteAll("GaitRecord", "date=? and count=?", gaitRecord.getDate(), gaitRecord.getCount() + "");
                    Toast.makeText(getContext(), "删除步态记录" + record + "条成功", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                })
                .setNegativeButton("否", null)
                .show();
    }

    private boolean sameCount(List<GaitRecord> currentGaitRecords, int count) {
        for (GaitRecord gaitRecord : currentGaitRecords) {
            if (gaitRecord.getCount() == count) {
                return true;
            }
        }
        return false;
    }

    private void toOnceGaitActivity(GaitRecord s) {
        Intent intent = new Intent(this.getActivity(), OnceGaitRecodActivity.class);
        intent.putExtra("count", s.getCount());
        intent.putExtra("date", s.getDate());
        startActivity(intent);
    }

    private void resetBarData(int year, int month, int day) {
        mTxBarTitle.setText(month + "月步态统计");
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
        List<Step> stepList = LitePal.where("year = ? and month = ?", year + "", month + "").find(Step.class);
        return stepList;
    }

    private List<Step> testGetCurrentMonthStepData(int year, int month, int day) throws ParseException {
        List<Step> stepList = LitePal.where("year = ? and month = ?", year + "", month + "").find(Step.class);
        Log.d(TAG, "setLineData: stepList len:" + stepList.size());
        for (Step step : stepList) {
            Log.d(TAG, "getCurrentMonthStepData: " + step.toString());
        }
        return stepList;
    }


    @Override
    public void onCalendarLongClickOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarLongClick(Calendar calendar) {
        selectCurrentDay(calendar.getYear(), calendar.getMonth(), calendar.getDay());

    }
}