package com.android.step.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    public static String getNowDateString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String format = df.format(new Date());
        return format;
    }


    public static String getDateByNum(int year, int month, int day) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = df.parse(year + "-" + month + "-" + day);
            return df.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getDate(String s) {
        String[] split = s.split("-");
        return split[0] + "年" + split[1] + "月" + split[2] + "日";
    }

}
