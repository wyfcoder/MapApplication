package com.example.team.mapapplication.business.background_functions.location;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.example.team.mapapplication.business.background_functions.sensor.SensorData;
import com.example.team.mapapplication.business.background_functions.sensor.SensorService;
import com.example.team.mapapplication.business.background_functions.latlng.JingWei;
import com.example.team.mapapplication.business.background_functions.latlng.Math_ways;
import com.example.team.mapapplication.engine.RepeatHandler;

public class LocationService extends Service {

    private IntereactionForLocation mLocationInterface;
    private RepeatHandler mRequestHandler;

    public void setLocationInterface(IntereactionForLocation i){
        mLocationInterface = i;
    }

    public class ServiceBinder extends Binder{
        public LocationService getService(){
            return LocationService.this;
        }
    }



    private ServiceBinder mBinder = new ServiceBinder();

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
            if (isFirst) {
                latitude_value = latitude2;
                longitude_value = longitude2;
                longitude_last = longitude2;
                latitude_last = latitude2;
                distance = 0;
                walkDistance = 0;
                isFirst = false;
            }
            else
                {
                JingWei old = new JingWei();
                old.longitude_value = longitude_value;
                old.latitude_value = latitude_value;

                walkDistance = math_ways.stepToDistance(sensorData.steps);

                JingWei newO = math_ways.countJW(old, walkDistance, sensorData.lastX);

                old.latitude_value = latitude2;

                old.longitude_value = longitude2;

                distance = math_ways.translateToDistance(old, newO);

                if (re_times == 0)
                {
                    if (latitude_last == latitude2 && longitude_last == longitude2)
                    {
                        if(distance<35) {
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
                        if (distance <= 10||distance>200)
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
                    }
                }
                    else
                {
                    re_times--;
                    if(distance<10||distance>50)
                    {
                        re_times=0;
                        latitude_value=latitude2;
                        longitude_value=longitude2;
                    }
                    else
                    {
                        latitude_value = newO.latitude_value;
                        longitude_value = newO.longitude_value;
                    }
                    longitude_last=longitude2;
                    latitude_last=latitude2;
                }
                sensorService.restart();
            }
            lastX = sensorData.lastX;
            accurate = bdLocation.getRadius();


//            locationClient.stop();  // OK here is where all happens. I mute it and the notification stop flashing. wyy

            if (mLocationInterface != null){
                mLocationInterface.passLocation(new LatLng(latitude_value, longitude_value), accurate, lastX);
            }

//            sendBroadcast();  No need to communicate with the original "Main Activity". wyy
        }
    }


    /**
     * Thread.sleep() seems not reliable, at least on my phone MIX1 the 3-sec blank time does not exist. pity.
     * Thus I decide to replace it with handler. wyy
     */
    @Deprecated
    private class DataThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(3000);
                    locationClient.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private DataThread dataThread;

    public LocationService() {

    }


    public void onCreate() {
        super.onCreate();
        initOption();
        initClient();


        isFirst = true;
        re_times = 0;
        sensorService = new SensorService(getApplicationContext());
        initReceiver();
        dataThread = new DataThread();
        // here I deleted this line in order to multi-call the service to get latitude and longitude. wyy
    }

    /**
     * Considering that it seems there is no need to initiate the client every time the request for location comes.
     * Thus I have moved this code block to here to initiate it for only once

     * OK I must confess that I'm wrong - .- . I don't know why after I have done such change cited above the GPS notification will crazily flash in the notification bar.
     * Now the change is discarded. (Maybe I can find some information on the official Doc)

     * OK again I must again confess I'm wrong -. - . Here is not where the problem arises from.
     * this change will not disturb normal process. So it just comes back again.
     * wyy
     */

    private void initClient() {
        locationClient = new LocationClient(getApplicationContext());
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(myLocationListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //the deleted line above has been moved here

        Runnable requestLocation = new Runnable() {
            @Override
            public void run() {
                locationClient.start();
            }
        };

        mRequestHandler = new RepeatHandler(requestLocation);
        mRequestHandler.setRepeatOn(true);
        mRequestHandler.setMessageMark(109);
        mRequestHandler.setDelay(1000);
        mRequestHandler.start();

//        new Thread(dataThread).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public ServiceBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    private void initOption() {
        option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("all");
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);  // the doc says the span must be higher than 1000 so I just turn it to 1000  * _ * wyy
        option.disableCache(true);
        option.setIsNeedLocationDescribe(true);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        option.setEnableSimulateGps(false);
        option.setIgnoreKillProcess(true);
        option.setLocationNotify(true);
        option.setOpenGps(true);
        locationClient = new LocationClient(getApplicationContext());
        locationClient.setLocOption(option);
    }

    private void initReceiver() {
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