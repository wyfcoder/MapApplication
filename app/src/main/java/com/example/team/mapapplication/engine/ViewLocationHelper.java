package com.example.team.mapapplication.engine;

import android.view.View;

/**
 * Created by Ellly on 2018/7/30.
 */

public class ViewLocationHelper {
    public static int[] getAbsoluteLocationOnScreen(View v){
        int[] locations = new int[2];
        v.getLocationOnScreen(locations);
        return locations;
    }
}
