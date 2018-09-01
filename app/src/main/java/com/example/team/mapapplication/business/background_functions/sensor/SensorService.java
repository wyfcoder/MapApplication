package com.example.team.mapapplication.business.background_functions.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


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
        List<Integer> data2=new ArrayList<>();
        int size=values.size();
        int aData;
        int length;
        steps=0;

        for(int i=0;i<size;i++)  data.add(values.get(i).intValue());

        boolean isUp;


        if(data.get(0)>=data.get(1)) isUp=true;
        else isUp=false;

        length=1;
        aData=data.get(1);

        for(int k=2;k<size;k++)
        {
            if(data.get(k)>aData)
            {
                if(!isUp)
                {
                    data2.add(length);
                    isUp=true;
                    length=1;
                }
                else
                {
                    length++;
                }
                aData=data.get(k);
            }
            else if(data.get(k)<aData)
            {
                if(isUp)
                {
                    data2.add(length);
                    isUp=false;
                    length=1;
                }
                else
                {
                    length++;
                }
                aData=data.get(k);
            }
            else
            {
                length++;
            }
        }


        //  person  can walk 0~5 steps each secon. wyf
        double min=size*0.2;
        size=data2.size();
        for(int i=0;i<size-2;)
        {
            if((data2.get(i+1)+data2.get(i+2))>min) steps++;
            i+=2;
        }

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
