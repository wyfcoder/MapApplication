package com.example.team.mapapplication.bean;

import org.litepal.crud.LitePalSupport;

import java.util.Calendar;

/**
 * Created by Ellly on 2018/8/12.
 */

public class DataDisplayInfo extends LitePalSupport{
    private String mFileName;
    private Calendar mCalendar;

    private int year;
    private int month;
    private int day;

    public DataDisplayInfo(){
        mFileName = "test_name";
        mCalendar = Calendar.getInstance();
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public void setCalendar(Calendar calendar) {
        this.mCalendar = calendar;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
