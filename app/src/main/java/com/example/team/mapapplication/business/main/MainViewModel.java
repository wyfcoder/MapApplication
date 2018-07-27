package com.example.team.mapapplication.business.main;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.example.team.mapapplication.base.BaseModel;
import com.example.team.mapapplication.bean.InputValueInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by Ellly on 2018/7/24.
 */

public class MainViewModel extends BaseModel {
    private LatLng mLocInfo;
    private Boolean mIsFirstLoc;
    private float mRadius;
    private List<InputValueInfo> mInfos = new ArrayList<>();
    private boolean isDataWaiting = false;

    public void setLatLng(LatLng latLng){
        mLocInfo = latLng;
    }

    public LatLng getLatLng(){
        return mLocInfo;
    }

    public boolean isFirstLoc(){
        return mIsFirstLoc;
    }

    public void setRadius(float radius) {
        this.mRadius = radius;
    }

    public float getRadius() {
        return mRadius;
    }

    /**
     * 判定是否正在获得定位数据
     * @return
     */
    public boolean getIsDataWaiting() {
        return isDataWaiting;
    }

    public void setDataWaiting(boolean isDataWaiting) {
        this.isDataWaiting = isDataWaiting;
    }

    /**
     * 核心需要更改的地方
     * @return 目前所获得的打点情况
     */
    public List<LatLng> getHeatMapData() {
        /*List<LatLng> randomList = new ArrayList<LatLng>();
        Random r = new Random();
        for (int i = 0; i < 500; i++) {
            // 116.220000,39.780000 116.570000,40.150000
            int rlat = r.nextInt(370000);
            int rlng = r.nextInt(370000);
            int lat = (int)getLatLng().latitudeE6 + rlat;
            int lng = (int)getLatLng().longitudeE6 + rlng;
            LatLng ll = new LatLng(lat / 1E6, lng / 1E6);
            randomList.add(ll);
        }
        return randomList;*/
        Random r = new Random();
        List<LatLng> data = new ArrayList<>();
        List<InputValueInfo> infos = getInputValueInfos();
        Comparator<InputValueInfo> comparator = new Comparator<InputValueInfo>() {
            @Override
            public int compare(InputValueInfo o1, InputValueInfo o2) {
                return o1.getValue() > o2.getValue() ? 1 : 0;
            }
        };
        double maxValue = Collections.max(infos, comparator).getValue();
        double minValue = Collections.min(infos, comparator).getValue();
        double minus = maxValue - minValue;


        for (InputValueInfo i : infos){
            int repeatNum = (int)(i.getValue() / maxValue * 10);
            LatLng latLng = i.getLatLng();
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            for (int j = 0; j < repeatNum; j++){
                data.add(new LatLng(latitude + r.nextDouble() * 10, longitude + r.nextDouble() * 10));
            }
        }
        return data;

    }

    public void saveInfo(String info) {

        InputValueInfo e = new InputValueInfo();
        e.setLatLng(getLatLng());
        e.setValue(Double.valueOf(info));
        mInfos.add(e);
        Log.d("InputInfo", info);
    }

    public List<InputValueInfo> getInputValueInfos(){
        for (int i = 0; i < 20; i++){
            InputValueInfo e = new InputValueInfo();
            e.setValue(12.2 + i);
            e.setLatLng(new LatLng(34.1 + i, 108.3 + i));
            mInfos.add(e);
        }
        return mInfos;
    }

}
