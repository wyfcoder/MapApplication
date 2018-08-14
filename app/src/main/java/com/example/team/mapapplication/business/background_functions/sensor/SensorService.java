package com.example.team.mapapplication.business.background_functions.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


import java.util.ArrayList;
import java.util.List;

public class SensorService implements SensorEventListener
{
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Context mContext;
    public double  lastX;
    double steps;
    private Sensor accerate;
    public List<Double> values;

    public SensorService(Context context)
    {
        mContext=context;
        values=new ArrayList<>();
        steps=0;
        lastX=0;
        initSensor();
    }

    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION)
        {
            float x = event.values[SensorManager.DATA_X];
            lastX = x;
        }
           else if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {
            double values1=Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2]);
            values.add(values1);
        }
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    public SensorData get()
    {
        mSensorManager.unregisterListener(this);
        SensorData sensorData=new SensorData();
        if(values.size()>0)
        {
            dealSteps();
            sensorData.steps=steps;
        }
        else
        {
            sensorData.steps=0;
        }
        sensorData.lastX=lastX;
        return sensorData;
    }

    public void restart()
    {
        if(values.size()!=0)
            values.clear();
        steps=0;
        lastX=0;
        initSensor();
    }

    private void dealSteps()
    {
        List<Integer> data=new ArrayList<>();
        for(int i=0;i<values.size();i++)  data.add(values.get(i).intValue());
        int t=0;
        for(int i=1;i<data.size();i++)
        {
            if(data.get(i-1)>data.get(i))
                t++;
        }
        
        if(t<5) steps=0;
        else if(5<t&&t<10) steps=0.5;
        else if(t<20&&t>10) steps=1;
        else if(t>20) steps=1.5;
    }

    private void initSensor()
    {
        mSensorManager= (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager!= null)
        {
            mSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        if(mSensor!=null)
        {
            mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_UI);
        }
        accerate=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this,accerate,SensorManager.SENSOR_DELAY_FASTEST);
    }

}
