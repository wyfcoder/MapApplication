package com.example.team.mapapplication.business.background_functions.location;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.team.mapapplication.business.background_functions.latlng.JingWei;
import com.example.team.mapapplication.business.background_functions.latlng.Math_ways;
import com.example.team.mapapplication.business.background_functions.sensor.SensorData;
import com.example.team.mapapplication.business.background_functions.sensor.SensorService;

public class LocationService2 extends Service
{

    private SensorService sensorService;//传感器的数据

    private LocationClient locationClient = null;

    private LocationClientOption option;

    private double latitude_value;
    private double longitude_value;
    public double latitude_last;
    public double longitude_last;
    private double distance;
    private double walkDistance;
    private double lastX;
    private boolean isFirst;
    private double accurate;

    private int re_times;
    private int frequeence;
    private ReLocationBroadcastReceive reLocationBroadcastReceive;
    private MyLocationListener myLocationListener = new MyLocationListener();


    private class MyLocationListener extends BDAbstractLocationListener {
        //更新出新的地理信息
        public void onReceiveLocation(BDLocation bdLocation)
        {
            double latitude2 = bdLocation.getLatitude();
            double longitude2 = bdLocation.getLongitude();
            Math_ways math_ways = new Math_ways();
            lastX = sensorService.lastX;
            SensorData sensorData = sensorService.get();

            if (isFirst)
            {
                latitude_value = latitude2;
                longitude_value = longitude2;
                longitude_last = longitude2;
                latitude_last = latitude2;
                distance = 0;
                walkDistance = 0;
                frequeence=0;
                isFirst = false;
            }
            else
                {
                JingWei old = new JingWei();

                old.longitude_value = longitude_value;
                old.latitude_value = latitude_value;

                walkDistance = math_ways.stepToDistance(sensorData.steps,getApplicationContext());

                JingWei newO = math_ways.countJW(old, walkDistance, sensorData.lastX);

                old.latitude_value = latitude2;

                old.longitude_value = longitude2;

                distance = math_ways.translateToDistance(old, newO);

                if (re_times == 0)
                {
                    if (latitude_last == latitude2 && longitude_last == longitude2)
                    {
                        frequeence++;
                        if(distance<35)
                        {
                            longitude_value = newO.longitude_value;
                            latitude_value = newO.latitude_value;
                        }
                        else
                        {
                            longitude_value=longitude_last;
                            latitude_value=latitude_last;
                        }
                    }
                    else
                    {
                            if (distance <= 25||distance>200||frequeence<=5)
                            {
                                longitude_value = newO.longitude_value;
                                latitude_value = newO.latitude_value;
                            }
                            else
                            {
                                longitude_value = longitude2;
                                latitude_value = latitude2;
                            }
                        if (distance <= 300)
                        {
                            longitude_last = longitude2;
                            latitude_last = latitude2;
                        }
                        frequeence=0;
                    }
                }
                    else
                {
                    re_times--;
                    latitude_value = newO.latitude_value;
                    longitude_value = newO.longitude_value;
                    longitude_last=longitude2;
                    latitude_last=latitude2;
                }
                sensorService.restart();
            }
            lastX = sensorData.lastX;
            accurate = bdLocation.getRadius();
            locationClient.stop();
            sendBroadcast();
        }
    }

    private class DataThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(500);
                    locationClient = new LocationClient(getApplicationContext());
                    locationClient.setLocOption(option);
                    locationClient.registerLocationListener(myLocationListener);
                    locationClient.start();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private DataThread dataThread;

    public LocationService2()
    {

    }


    public void onCreate()
    {
        super.onCreate();
        initOption();
        isFirst = true;
        re_times = 0;
        sensorService = new SensorService(getApplicationContext());
        initReceiver();
        dataThread = new DataThread();
        new Thread(dataThread).start();
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void initOption() {
        option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("all");
        option.setCoorType("bd09ll");
        option.setScanSpan(500);
        option.disableCache(true);
        option.setIsNeedLocationDescribe(true);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        option.setEnableSimulateGps(false);
        option.setIgnoreKillProcess(false);
        option.setLocationNotify(true);
        option.setOpenGps(true);
        locationClient = new LocationClient(getApplicationContext());
        locationClient.setLocOption(option);
    }

    private void initReceiver()
    {
        reLocationBroadcastReceive = new ReLocationBroadcastReceive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.reLocation");
        registerReceiver(reLocationBroadcastReceive, intentFilter);
    }

    private void sendBroadcast()
    {
        Intent intent = new Intent("com.LocationInformation");
        intent.putExtra("latitude", latitude_value);
        intent.putExtra("longitude", longitude_value);
        intent.putExtra("accurate", accurate);
        intent.putExtra("distance", distance);
        intent.putExtra("walkDistance", walkDistance);
        intent.putExtra("lastX", lastX);
        intent.putExtra("longitude_old", longitude_last);
        intent.putExtra("latitude_old", latitude_last);
        sendBroadcast(intent);
    }

    class ReLocationBroadcastReceive extends BroadcastReceiver
    {
        public void onReceive(Context context, Intent intent)
        {
            re_times = 10;
            latitude_last=latitude_value = intent.getDoubleExtra("latitude", latitude_value);
            longitude_last=longitude_value = intent.getDoubleExtra("longitude", longitude_value);
        }
    }
}