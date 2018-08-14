package com.example.team.mapapplication.business.acquireinfo;

import com.blankj.utilcode.util.SPUtils;
import com.example.team.mapapplication.base.BaseModel;

/**
 * Created by Ellly on 2018/8/13.
 */

public class AcquireModel extends BaseModel {

    public static void saveGender(String selected_gender) {
        SPUtils.getInstance("body_info").put("gender", selected_gender);
    }

    public static String getGender(){
        return SPUtils.getInstance("body_info").getString("gender");
    }

    public static void saveHeight(String text) {
        SPUtils.getInstance("body_info").put("height", text);
    }

    public static double getHeight(){
        String h = SPUtils.getInstance("body_info").getString("height");
        return Double.valueOf(h);
    }

    public static boolean isFirstEnterApp() {
        return SPUtils.getInstance("app_info").getBoolean("is_first_in", true);
    }

    public static void cancelIsFirstEnterApp() {
        SPUtils.getInstance("app_info").put("is_first_in", false);
    }
}
