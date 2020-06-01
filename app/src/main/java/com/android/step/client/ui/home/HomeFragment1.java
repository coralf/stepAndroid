package com.android.step.client.ui.home;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
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
import androidx.fragment.app.Fragment;
import com.android.step.R;
import com.github.mikephil.charting.charts.LineChart;


public class HomeFragment1 extends Fragment implements View.OnClickListener {

    private static final String TAG = "HomeFragment";

    private SensorManager sensorManager;
    private Sensor sensor;
    private LineChart lineStep;
    private TextView txToZero;
    private Button btnStartRecord;
    private Button btnEndRecord;
    private TextView txStepCount;
    private static final int BATCH_LATENCY_0 = 0; // no batching


    public static final String CARD_INTRO = "intro";
    public static final String CARD_REGISTER_DETECTOR = "register_detector";
    public static final String CARD_REGISTER_COUNTER = "register_counter";
    public static final String CARD_BATCHING_DESCRIPTION = "register_batching_description";
    public static final String CARD_COUNTING = "counting";
    public static final String CARD_EXPLANATION = "explanation";
    public static final String CARD_NOBATCHSUPPORT = "error";

    public static final int ACTION_REGISTER_DETECT_NOBATCHING = 10;
    public static final int ACTION_REGISTER_DETECT_BATCHING_5s = 11;
    public static final int ACTION_REGISTER_DETECT_BATCHING_10s = 12;
    public static final int ACTION_REGISTER_COUNT_NOBATCHING = 21;
    public static final int ACTION_REGISTER_COUNT_BATCHING_5s = 22;
    public static final int ACTION_REGISTER_COUNT_BATCHING_10s = 23;
    public static final int ACTION_UNREGISTER = 1;
    private static final int ACTION_BATCHING_DESCRIPTION_DISMISS = 2;
    private static final int ACTION_EXPLANATION_DISMISS = 3;

    public static final int STATE_OTHER = 0;
    public static final int STATE_COUNTER = 1;
    public static final int STATE_DETECTOR = 2;

    private static final String BUNDLE_STATE = "state";
    private static final String BUNDLE_LATENCY = "latency";
    private static final String BUNDLE_STEPS = "steps";

    private static final int BATCH_LATENCY_10s = 10000000;
    private static final int BATCH_LATENCY_5s = 5000000;

    private static final int EVENT_QUEUE_LENGTH = 10;
    private float[] mEventDelays = new float[EVENT_QUEUE_LENGTH];

    private int mEventLength = 0;
    private int mEventData = 0;

    private int mSteps = 0;
    private int mCounterSteps = 0;
    private int mPreviousCounterSteps = 0;
    private int mState = STATE_OTHER;
    private int mMaxDelay = 0;


    private final SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {

                mSteps += event.values.length;

            } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

                if (mCounterSteps < 1) {
                    mCounterSteps = (int) event.values[0];
                }
                mSteps = (int) event.values[0] - mCounterSteps;
                mSteps = mSteps + mPreviousCounterSteps;
            }
            txStepCount.setText(mSteps + "");
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    }

    private void resetCounter() {
        mSteps = 0;
        mCounterSteps = 0;
        mEventLength = 0;
        mEventDelays = new float[EVENT_QUEUE_LENGTH];
        mPreviousCounterSteps = 0;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            resetCounter();
            mSteps = savedInstanceState.getInt(BUNDLE_STEPS);
            mState = savedInstanceState.getInt(BUNDLE_STATE);
            mMaxDelay = savedInstanceState.getInt(BUNDLE_LATENCY);
            if (mState == STATE_DETECTOR) {
                registerEventListener(mMaxDelay, Sensor.TYPE_STEP_DETECTOR);
            } else if (mState == STATE_COUNTER) {
                mPreviousCounterSteps = mSteps;
                registerEventListener(mMaxDelay, Sensor.TYPE_STEP_COUNTER);
            }
        }
    }

    public Object getSystemService(String service) {
        return getContext().getSystemService(service);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_LATENCY, mMaxDelay);
        outState.putInt(BUNDLE_STATE, mState);
        outState.putInt(BUNDLE_STEPS, mSteps);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(Activity.SENSOR_SERVICE);
        sensorManager.unregisterListener(mListener);
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

    private void clickEndRecord() {
        unregisterListeners();
        btnEndRecord.setVisibility(View.GONE);
        btnStartRecord.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), "采集完成", Toast.LENGTH_LONG).show();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                int mins = Integer.parseInt(msg.obj.toString());
                if (mins != -1) {
                    if (btnStartRecord.getVisibility() != View.INVISIBLE) {
                        btnStartRecord.setVisibility(View.GONE);
                    }
                    txToZero.setVisibility(View.VISIBLE);
                    txToZero.setText(mins + "");
                    txStepCount.setText(mSteps + "");
                } else {
                    txToZero.setVisibility(View.INVISIBLE);
                    btnEndRecord.setVisibility(View.VISIBLE);
                    registerEventListener(BATCH_LATENCY_0, Sensor.TYPE_STEP_COUNTER);
                }
            }
            // 要做的事情
            super.handleMessage(msg);
        }
    };

    private void clickStartRecoed() {
        new Thread(() -> {
            int mins = 5;
            while (true) {
                try {
                    if (mins < -1) {
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


    private void unregisterListeners() {
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(Activity.SENSOR_SERVICE);
        sensorManager.unregisterListener(mListener);
        Log.i(TAG, "Sensor listener unregistered.");

    }


    private void registerEventListener(int maxdelay, int sensorType) {

        mMaxDelay = maxdelay;
        if (sensorType == Sensor.TYPE_STEP_COUNTER) {
            mState = STATE_COUNTER;
            mCounterSteps = 0;
            Log.i(TAG, "Event listener for step counter sensor registered with a max delay of "
                    + mMaxDelay);
        } else {
            mState = STATE_DETECTOR;
            Log.i(TAG, "Event listener for step detector sensor registered with a max delay of "
                    + mMaxDelay);
        }

        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(Activity.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);

        final boolean batchMode = sensorManager.registerListener(
                mListener, sensor, SensorManager.SENSOR_DELAY_NORMAL, maxdelay);
    }


}