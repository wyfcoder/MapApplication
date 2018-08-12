package com.example.team.mapapplication.bean;

import com.baidu.mapapi.model.LatLng;

import org.litepal.crud.LitePalSupport;

/**
 * Created by Ellly on 2018/7/26.
 */

public class InputValueInfo extends LitePalSupport{
    private LatLng latLng;
    private Double value;
    private String fileName;

    private Double latitude;
    private Double longitude;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
