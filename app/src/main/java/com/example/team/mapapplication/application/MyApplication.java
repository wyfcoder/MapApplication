package com.example.team.mapapplication.application;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

import org.litepal.LitePal;

/**
 * Created by Ellly on 2018/7/23.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        LitePal.initialize(this);
    }
}
