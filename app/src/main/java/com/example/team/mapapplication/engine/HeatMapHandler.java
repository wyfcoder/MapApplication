package com.example.team.mapapplication.engine;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Ellly on 2018/7/24.
 */

/**
 * 这是一个循环执行mDrawRunnable事件的Handler
 * 只需设置事件Runnable发送Message即可。
 * 通常需要设置停止之后需要的额外操作mStopRunnable
 */
public class HeatMapHandler extends Handler {
    private static final long DELAY = 1000;
    public static int DRAW_HEAT_MAP_MESSAGE_MARK = 123;
    public boolean STOP_MARK = true;

    private Runnable mDrawRunnable;
    private Runnable mStopRunnable;

    public HeatMapHandler(Runnable r) {
        super();
        mDrawRunnable = r;
    }

    public void setStopRunnable(Runnable rr){
        mStopRunnable = rr;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == DRAW_HEAT_MAP_MESSAGE_MARK && STOP_MARK){
            post(mDrawRunnable);
//            sendEmptyMessageDelayed(DRAW_HEAT_MAP_MESSAGE_MARK, DELAY);//若只是画一次的话，只需注释掉这一行
        }else if(!STOP_MARK){
            post(mStopRunnable);
        }
    }

    public void stop() {
        STOP_MARK = false;
        sendEmptyMessage(DRAW_HEAT_MAP_MESSAGE_MARK);
    }
}
