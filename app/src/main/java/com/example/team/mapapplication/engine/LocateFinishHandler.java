package com.example.team.mapapplication.engine;

import android.os.Handler;
import android.os.Message;

import com.example.team.mapapplication.business.main.MainActivity;

/**
 * Created by Ellly on 2018/7/27.
 */
public class LocateFinishHandler extends Handler {
    private Runnable mRunnable;

    public LocateFinishHandler(Runnable r) {
        super();
        mRunnable = r;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == MainActivity.LOCATE_FINISHED){
            post(mRunnable);
        }
    }
}
