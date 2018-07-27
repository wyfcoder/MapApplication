package com.example.team.mapapplication.bean;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by Ellly on 2018/7/26.
 */

public class InputValueInfo {
    private LatLng latLng;
    private Double value;

    public LatLng getLatLng() {
        return latLng;
    }

    public Double getValue() {
        return value;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
