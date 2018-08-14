package com.example.team.mapapplication.business.main;

import android.view.View;

import com.baidu.mapapi.model.LatLng;
import com.example.team.mapapplication.base.IBaseMethodInterface;
import com.example.team.mapapplication.base.IBaseView;
import com.example.team.mapapplication.base.IBaseViewModel;

import java.util.List;

/**
 * Created by Ellly on 2018/7/24.
 */

public interface IMainView extends IBaseView, IBaseViewModel{
    public void animateToLocation();
    public void drawLoc(double latitude, double longitude, float radius, double lastX);

    void drawHeatMap(List<LatLng> dataList);

    void removeHeatMap();

    void refreshInfoList();

    void startLocate();

    void drawMarkerOverlay(LatLng latLng, String info);

    void notifyWaitStart();

    void notifyWaitFinished();

    void showDeletePopup(View itemView, int position);

    void hideFloatingViews();

    void showFloatingViews();

    void transferToWifiModeView();

    void showWifiViews();

    void hideWifiViews();

    void showEditViews();

    void hideEditViews();

    void transferToEditModeView();

    void transferToDisplayModeView();

    void selectWifi(List<String> names);

    void performSaveBtnCalled();

    void showMessageDialog(String s, IBaseMethodInterface iBaseMethodInterface);
}
