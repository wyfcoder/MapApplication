package com.example.team.mapapplication.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;

import org.litepal.crud.LitePalSupport;

/**
 * Created by Ellly on 2018/7/26.
 */

public class InputValueInfo extends LitePalSupport implements Parcelable{
    private LatLng latLng;
    private Double value;
    private String fileName;

    private Double latitude;
    private Double longitude;

    public InputValueInfo(){

    }

    protected InputValueInfo(Parcel in) {
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        if (in.readByte() == 0) {
            value = null;
        } else {
            value = in.readDouble();
        }
        fileName = in.readString();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
    }

    public static final Creator<InputValueInfo> CREATOR = new Creator<InputValueInfo>() {
        @Override
        public InputValueInfo createFromParcel(Parcel in) {
            return new InputValueInfo(in);
        }

        @Override
        public InputValueInfo[] newArray(int size) {
            return new InputValueInfo[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(latLng, flags);
        if (value == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(value);
        }
        dest.writeString(fileName);
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
    }
}
