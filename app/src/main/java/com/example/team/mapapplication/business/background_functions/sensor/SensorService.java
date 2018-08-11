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
    private Sensor stepDetector;
    private Sensor countSensor;
    private Sensor accerate;
    private int pre_steps;
    private boolean isFirst;
    private int mode;
    public List<Double> values;
    long times;

    public SensorService(Context context)
    {
        mContext=context;
        values=new ArrayList<>();
        times=0;
        steps=0;
        lastX=0;
        isFirst=true;
        mSensorManager= (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager!= null)
        {
            mSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        //判断是否有方向传感器
        if(mSensor!=null)
        {
            //注册监听器
            mSensorManager.registerListener(this,mSensor, SensorManager.SENSOR_DELAY_UI);
        }
        stepDetector=mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);//获取单次计步传感器
        countSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(stepDetector!=null)
        {
            mSensorManager.registerListener(this,stepDetector, SensorManager.SENSOR_DELAY_UI);
            mode=1;
        }
        else if(countSensor!=null)
        {
            mSensorManager.registerListener(this,countSensor, SensorManager.SENSOR_DELAY_UI);
            mode=2;
        }
        else
        {
            accerate=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this,accerate, SensorManager.SENSOR_DELAY_UI);
            mode=3;
        }
    }
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION)
        {
            float x = event.values[SensorManager.DATA_X];
            lastX = x;
        }
        else if(event.sensor.getType()== Sensor.TYPE_STEP_DETECTOR)
        {
            if(isFirst)
            {
                pre_steps=(int) event.values[0];
                isFirst=false;
            }
            else
                steps=event.values[0]-pre_steps;
        }
        else if(event.sensor.getType()== Sensor.TYPE_ACCELEROMETER)
        {
            double values1= Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2]);
            if(times>10)
            values.add(values1);
            else
                times++;
        }
        else if(event.sensor.getType()== Sensor.TYPE_STEP_COUNTER)
        {
            if(isFirst)
            {
                steps=event.values[0];
                isFirst=false;
            }
            else
            {
                steps+=event.values[0];
            }
        }
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }
    public SensorData get()
    {
        mSensorManager.unregisterListener(this);
        SensorData sensorData=new SensorData();
        if(mode==3&&values.size()>0)
        {
            dealSteps();
            sensorData.steps=steps*1.5;
        }
        else  sensorData.steps=steps;
        sensorData.lastX=lastX;;
        return sensorData;
    }

    public void restart()
    {
        if(values.size()!=0)
            values.clear();
        times=0;
        steps=0;
        lastX=0;
        isFirst=true;
        mSensorManager= (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager!= null)
        {
            mSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        //判断是否有方向传感器
        if(mSensor!=null)
        {
            //注册监听器
            mSensorManager.registerListener(this,mSensor, SensorManager.SENSOR_DELAY_UI);
        }
        switch (mode)
        {
            case 1:
                stepDetector=mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
                mSensorManager.registerListener(this,stepDetector, SensorManager.SENSOR_DELAY_UI);
                break;
            case 2:
                countSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
                mSensorManager.registerListener(this,countSensor, SensorManager.SENSOR_DELAY_UI);
                break;
            case 3:
                accerate=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mSensorManager.registerListener(this,accerate, SensorManager.SENSOR_DELAY_UI);
                break;
        }
    }
    private void dealSteps()
    {
        List<Double> datas=new ArrayList<>();
        double data=values.get(0);
        datas.add(values.get(0));
        for(int i=1;i<values.size();i++)
        {
            if(Math.abs(values.get(i)-data)>0.1*data)
            {
                datas.add(values.get(i));
                data=values.get(i);
            }
        }
        double average=0;
        for(int i=0;i<datas.size();i++)
        {
            average+=datas.get(i);
        }
         average=average/(0.8*datas.size());
        int f=0;
        for(int i=0;i<datas.size();i++)
        {
            if(datas.get(i)>=average)
            {
                if(f>=2)
                {
                    steps++;
                    f=0;
                }
                else
                    f++;
            }
            else
            {
                f++;
            }
        }
    }

}
