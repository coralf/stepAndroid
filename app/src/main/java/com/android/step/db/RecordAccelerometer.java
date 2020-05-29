package com.android.step.db;

import org.litepal.crud.LitePalSupport;

public class RecordAccelerometer extends LitePalSupport {
    private String date;
    private float gravityX;
    private float gravityY;
    private float gravityZ;
    private float linearAccelerationX;
    private float linearAccelerationZ;
    private float linearAccelerationY;
    private float x;
    private float y;
    private float z;
    private int recordCount;

    public RecordAccelerometer(String date, float x, float y, float z, int recordCount) {
        this.date = date;
        this.x = x;
        this.y = y;
        this.z = z;
        this.recordCount = recordCount;
    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public RecordAccelerometer(String date, float gravityX, float gravityY, float gravityZ, float linearAccelerationX, float linearAccelerationZ, float linearAccelerationY, int recordCount) {
        this.date = date;
        this.gravityX = gravityX;
        this.gravityY = gravityY;
        this.gravityZ = gravityZ;
        this.linearAccelerationX = linearAccelerationX;
        this.linearAccelerationZ = linearAccelerationZ;
        this.linearAccelerationY = linearAccelerationY;
        this.recordCount = recordCount;
    }

    public RecordAccelerometer(String date, float gravityX, float gravityY, float gravityZ, float linearAccelerationX, float linearAccelerationZ, float linearAccelerationY) {
        this.date = date;
        this.gravityX = gravityX;
        this.gravityY = gravityY;
        this.gravityZ = gravityZ;
        this.linearAccelerationX = linearAccelerationX;
        this.linearAccelerationZ = linearAccelerationZ;
        this.linearAccelerationY = linearAccelerationY;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getGravityX() {
        return gravityX;
    }

    public void setGravityX(float gravityX) {
        this.gravityX = gravityX;
    }

    public float getGravityY() {
        return gravityY;
    }

    public void setGravityY(float gravityY) {
        this.gravityY = gravityY;
    }

    public float getGravityZ() {
        return gravityZ;
    }

    public void setGravityZ(float gravityZ) {
        this.gravityZ = gravityZ;
    }

    public float getLinearAccelerationX() {
        return linearAccelerationX;
    }

    public void setLinearAccelerationX(float linearAccelerationX) {
        this.linearAccelerationX = linearAccelerationX;
    }

    public float getLinearAccelerationZ() {
        return linearAccelerationZ;
    }

    public void setLinearAccelerationZ(float linearAccelerationZ) {
        this.linearAccelerationZ = linearAccelerationZ;
    }

    public float getLinearAccelerationY() {
        return linearAccelerationY;
    }

    public void setLinearAccelerationY(float linearAccelerationY) {
        this.linearAccelerationY = linearAccelerationY;
    }
}
