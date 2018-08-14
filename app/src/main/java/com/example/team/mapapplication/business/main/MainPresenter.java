package com.example.team.mapapplication.business.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.team.mapapplication.base.BasePresenter;
import com.example.team.mapapplication.base.IBaseMethodInterface;
import com.example.team.mapapplication.bean.InputValueInfo;
import com.example.team.mapapplication.business.acquireinfo.AcquireModel;
import com.example.team.mapapplication.business.background_functions.signal.SaveDataService;
import com.example.team.mapapplication.engine.RepeatHandler;
import com.example.team.mapapplication.engine.LocateFinishHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.example.team.mapapplication.business.main.MainViewModel.WIFI_MODE;


public class MainPresenter extends BasePresenter<IMainView> {
    private static final boolean LOCATE_DEBUG = true;
    private MainViewModel mModel;
    private RepeatHandler mHandler;
    private LocateFinishHandler mLocateFinishedHandler;

    private RepeatHandler mGetStrengthHandler;

    //for test
    private LocateFinishHandler mRepeatingLocationRequiringHandler;

    @Override
    public void attach(IMainView view) {
        super.attach(view);
        mModel = (MainViewModel) view.getModel();
    }

    /**
     * 移动到定位位置
     */
    public void animateToLoc() {

        mView.animateToLocation();
    }

    /**
     * 每次调用此方法时都会获取当前已保存的经纬度
     * 并画出定位小蓝点
     */
    public void setLocationData() {
        LatLng latLng = mModel.getLatLng();
        float radius = mModel.getRadius();
        mView.drawLoc(latLng.latitude, latLng.longitude, radius);
    }

    public void drawHeatMap() {
        // 大宗数据下异步加载热图，设置绘图核心Runnable
        mHandler = new RepeatHandler(new Runnable() {
            @Override
            public void run() {

                try {
                    List<LatLng> dataList = mModel.getHeatMapData();
                    mView.drawHeatMap(dataList);
                } catch (NoSuchElementException e){
                    ToastUtils.showShort("请保存至少一组数据");
                    return;
                } catch (IllegalArgumentException e){
                    ToastUtils.showShort("请确认经纬度是否正确");
                    return;
                }

                ToastUtils.showShort("HeatMapFinished");
            }
        });
        //结束后会运行此Runnable
        mHandler.setStopRunnable(new Runnable() {
            @Override
            public void run() {
                mView.removeHeatMap();
            }
        });
        //开启循环。
        mHandler.setMessageMark(108);
        mHandler.start();
    }

    public void removeHeatMap() {
        if (mHandler != null){
            mHandler.stop();
        }
    }


    /*TODO: 这里如果多次连续输入值的话
            会因为定位速度不够快的缘故而导致后来的某个数据不会被记录到
            考虑最终的定位效果决定是否在获取定位中间禁止再次输入数据
            */
    public void saveInfo(final String info) {
        Runnable saveInfoRunnable = new Runnable() {
            @Override
            public void run() {
                mModel.saveInfo(info);
                mView.drawMarkerOverlay(mModel.getLatLng(), info); //暂时弃用了
                mView.refreshInfoList();
                mView.notifyWaitFinished();
                mLocateFinishedHandler = null;
            }
        };
        mLocateFinishedHandler = new LocateFinishHandler(saveInfoRunnable);
//        mView.startLocate();  // Considering that currently the location service is always on so here I just assume that the location request has finished.
                                // the delay interval serves to imitate the data writing process(usually it is thought time-consumed hhh). wyy
        mLocateFinishedHandler.sendEmptyMessageDelayed(MainActivity.LOCATE_FINISHED, 500);
        mView.notifyWaitStart();
    }

    public void refreshInfoList(){
        mView.notifyWaitStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mView.refreshInfoList();
                mView.notifyWaitFinished();
            }
        }, 500);

    }

    public void sendLocateFinishedMessage() {
        if (mLocateFinishedHandler != null){
            mLocateFinishedHandler.sendEmptyMessage(MainActivity.LOCATE_FINISHED);
        }
        if (LOCATE_DEBUG){
            if (mRepeatingLocationRequiringHandler == null){
                initRequiringHandlerIfNeed();
            }
            mRepeatingLocationRequiringHandler.sendEmptyMessageDelayed(MainActivity.LOCATE_FINISHED, 7000);
        }
    }

    @Deprecated
    private void initRequiringHandlerIfNeed() {
        Runnable saveInfoRunnable = new Runnable() {
            @Override
            public void run() {
                mModel.saveInfo("10");
                mView.drawMarkerOverlay(mModel.getLatLng(), "10"); //暂时弃用了
                mView.refreshInfoList();
                mView.notifyWaitFinished();
                ToastUtils.showShort("定位结束开始下一次定位");
            }
        };
        mRepeatingLocationRequiringHandler = new LocateFinishHandler(saveInfoRunnable);

    }

    public void deleteThisItem(View itemView, int viewType, int position) {
        mView.showDeletePopup(itemView, position);
    }

    public void showFloatingViews() {
        mModel.setToolbarHide(false);
        mView.showFloatingViews();
    }

    public void hideFloatingViews() {
        mModel.setToolbarHide(true);
        mView.hideFloatingViews();
    }

    public void animateFloatingViews() {
        ToastUtils.showShort("MapView Clicked");
        if (mModel.isToolbarHide()){
            showFloatingViews();
        }else {
            hideFloatingViews();
        }
    }

    public void transferToWifiMode() {
        bindSignalServiceIfNeeded();
        mModel.setModeStatus(WIFI_MODE);
        mView.transferToWifiModeView();
        mModel.getInputValueInfos().clear();
        refreshInfoList();
    }

    public void transferToEditMode() {
//        unBindSignalServiceIfNeeded(); //in case of unexpected crash triggered by frequently switching signal service. wyy
        mModel.setModeStatus(MainViewModel.EDIT_MODE);
        mView.transferToEditModeView();
        mModel.getInputValueInfos().clear();
        refreshInfoList();
    }

    public void transferToDisplayMode() {
        mModel.setModeStatus(MainViewModel.DISPLAY_MODE);
        mModel.getInputValueInfos().addAll(mModel.getDisplayData());
        refreshInfoList();
        mView.transferToDisplayModeView();
    }


    private SaveDataService mSaveDataService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSaveDataService = ((SaveDataService.SignalServiceBinder) service).getService();
            ToastUtils.showShort("Service Connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mSaveDataService = null;
        }
    };



    public void selectWifi() {
        List<String> names = mSaveDataService.getWifiNames();
        mView.selectWifi(names);
    }

    public void startPick(final CharSequence selectedSignalName) {
//        mView.startLocate();
//        mView.notifyWaitStart();

        mModel.setPickStarted(true);

        Runnable getStrengthRunnable = new Runnable() {
            @Override
            public void run() {
                if (mSaveDataService != null){
                    int strength = mSaveDataService.getStrength((String) selectedSignalName);
                    ToastUtils.showShort("Strength: " + strength);
                    saveInfo(strength + "");
                }else {
                    Log.d("Signal", "Service Unprepared");
                }
            }
        };
        mGetStrengthHandler = new RepeatHandler(getStrengthRunnable);
        mGetStrengthHandler.setRepeatOn(true);
        mGetStrengthHandler.setDelay(3000);
        mGetStrengthHandler.setMessageMark(201);
        mGetStrengthHandler.start();
    }


    public void bindSignalServiceIfNeeded() {
        if (mSaveDataService == null){
            mContext.bindService(new Intent(mContext, SaveDataService.class), mConnection, Context.BIND_AUTO_CREATE);
        }
    }


    public void unBindSignalServiceIfNeeded() {
        if (mSaveDataService != null){
            mContext.unbindService(mConnection);
        }
    }

    /**
     * save the current values of the list and the description (display) to the DB
     * @param text file's name
     */
    public int saveValuesToDB(String text) {
        if (mModel.getInputValueInfos().size() <= 0){
            ToastUtils.showShort("当前数据为空");
            return -1;
        }else if ("".equals(text)){
            ToastUtils.showShort("当前文件名为空");
            return -2;
        }
        mModel.saveValuesToDB(text);
        mModel.saveDisplayToDB(text);
        ToastUtils.showShort("存储完成");
        return 0;
    }

    /**
     * set to unbind service when pick's end.
     * now deprecated.
     */
    @Deprecated
    public void endPick() {
        unBindSignalServiceIfNeeded();
    }

    /**
     * it starts in edit mode so the only needed is to determine whether it's in Wifi mode. wyy
     * @param state saved mode status.
     */
    public void switchMode(int state) {
        if (state == WIFI_MODE){
            transferToWifiMode();
        }
    }

    /**
     * restore data list when app re-launched. wyy
     * @param infos saved data list.
     */
    public void restoreDataList(List<InputValueInfo> infos) {
        mModel.getInputValueInfos().addAll(infos);
    }

    @Deprecated
    public void askIfNeedToSave(IBaseMethodInterface iBaseMethodInterface) {
        mView.showMessageDialog("是否", iBaseMethodInterface);
    }

    public void dealSavedInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null){ // seems unnecessary though. -.- wyy
            int state = savedInstanceState.getInt("state", -1);
            List<InputValueInfo> infos = savedInstanceState.getParcelableArrayList("data_list");
            switchMode(state);
            restoreDataList(infos);
        }
    }

    public static void askForPermissions() {


        String[] needGrantPermissions = {PermissionConstants.LOCATION, PermissionConstants.STORAGE, PermissionConstants.SENSORS, PermissionConstants.PHONE};


        if (needGrantPermissions.length > 0) {
            PermissionUtils p = PermissionUtils.permission(needGrantPermissions).callback(new PermissionUtils.SimpleCallback() {
                @Override
                public void onGranted() {
                }

                @Override
                public void onDenied() {
                    ToastUtils.showShort("可以在手机设置内手动授予权限");
                }
            });
            p.request();
        }
    }

    public void saveGender(String text) {
        AcquireModel.saveGender(text);
    }

    public void saveHeight(String s) {
        AcquireModel.saveHeight(s);
    }
}
