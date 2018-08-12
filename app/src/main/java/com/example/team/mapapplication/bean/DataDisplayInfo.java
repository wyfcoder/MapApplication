package com.example.team.mapapplication.bean;

import org.litepal.crud.LitePalSupport;

import java.util.Calendar;

/**
 * Created by Ellly on 2018/8/12.
 */

public class DataDisplayInfo extends LitePalSupport{
    private String mFileName;
    private Calendar mCalendar;

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
}
