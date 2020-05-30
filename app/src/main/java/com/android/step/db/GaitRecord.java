package com.android.step.db;

import org.litepal.crud.LitePalSupport;

public class GaitRecord extends LitePalSupport {
    private String terminal;
    private String date;
    private Long time;
    private float gravity;
    private float accX;
    private float accY;
    private float accZ;
    private int count;
    private int stepCount;


    public GaitRecord(String terminal, String date, Long time, float accX, float accY, float accZ, int count, int stepCount) {
        this.terminal = terminal;
        this.date = date;
        this.time = time;
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
        this.count = count;
        this.stepCount = stepCount;
    }


    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public float getAccX() {
        return accX;
    }

    public void setAccX(float accX) {
        this.accX = accX;
    }

    public float getAccY() {
        return accY;
    }

    public void setAccY(float accY) {
        this.accY = accY;
    }

    public float getAccZ() {
        return accZ;
    }

    public void setAccZ(float accZ) {
        this.accZ = accZ;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public GaitRecord(String terminal, String date, Long time, float accX, float accY, float accZ, int count) {
        this.terminal = terminal;
        this.date = date;
        this.time = time;
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
        this.count = count;
    }
}
