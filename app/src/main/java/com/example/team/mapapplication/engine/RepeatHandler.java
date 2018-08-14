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
public class RepeatHandler extends Handler {
    private static long DELAY = 1000;
    public int DRAW_HEAT_MAP_MESSAGE_MARK = 123;
    public boolean STOP_MARK = true;

    private Runnable mDrawRunnable;
    private Runnable mStopRunnable;
    private boolean REPEAT_ON = false;

    public RepeatHandler(Runnable r) {
        super();
        mDrawRunnable = r;
    }

    public void setStopRunnable(Runnable rr){
        mStopRunnable = rr;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == getMessageMark() && STOP_MARK){
            post(mDrawRunnable);
            if (isRepeatOn()){
                sendEmptyMessageDelayed(getMessageMark(), DELAY);//若只是画一次的话，只需把
            }
        }else if(!STOP_MARK){
            if (mStopRunnable != null){
                post(mStopRunnable);
            }
        }
    }

    public void setRepeatOn(boolean on){
        REPEAT_ON = on;
    }

    private boolean isRepeatOn() {
        return REPEAT_ON;
    }

    public void setMessageMark(int mark){
        DRAW_HEAT_MAP_MESSAGE_MARK = mark;
    }

    private int getMessageMark() {
        return DRAW_HEAT_MAP_MESSAGE_MARK;
    }

    public void stop() {
        STOP_MARK = false;
        sendEmptyMessage(getMessageMark());
    }

    public void start(){
        sendEmptyMessage(getMessageMark());
    }

    public void setDelay(long delay){
        DELAY = delay;
    }
}
