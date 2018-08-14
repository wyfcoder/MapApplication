package com.example.team.mapapplication.business.background_functions.location;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by Ellly on 2018/8/11.
 */

public interface IntereactionForLocation {
    void passLocation(LatLng latLng, double radius, double lastX);
}
