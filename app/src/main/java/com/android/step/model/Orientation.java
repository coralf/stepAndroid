package com.android.step.model;

public class Orientation {

    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";


    private int x;
    private int y;
    private int z;


    public Orientation() {
    }

    public Orientation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }


    @Override
    public String toString() {
        return "Orientation{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
