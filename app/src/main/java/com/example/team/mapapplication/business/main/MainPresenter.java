package com.example.team.mapapplication.business.main;

import android.os.Handler;
import android.view.View;

import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.ToastUtils;
import com.example.team.mapapplication.base.BasePresenter;
import com.example.team.mapapplication.engine.RepeatHandler;
import com.example.team.mapapplication.engine.LocateFinishHandler;

import java.util.List;
import java.util.NoSuchElementException;


public class MainPresenter extends BasePresenter<IMainView> {
    private static final boolean LOCATE_DEBUG = true;
    private MainViewModel mModel;
    private RepeatHandler mHandler;
    private LocateFinishHandler mLocateFinishedHandler;

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

    private void initRequiringHandlerIfNeed() {
        Runnable saveInfoRunnable = new Runnable() {
            @Override
            public void run() {
                mModel.saveInfo("10");
                mView.drawMarkerOverlay(mModel.getLatLng(), "10"); //暂时弃用了
                mView.refreshInfoList();
                mView.notifyWaitFinished();
                startPick();
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
        mModel.setModeStatus(MainViewModel.WIFI_MODE);
        mView.transferToWifiModeView();
        mModel.getInputValueInfos().clear();
        refreshInfoList();
    }

    public void transferToEditMode() {
        mModel.setModeStatus(MainViewModel.EDIT_MODE);
        mView.transferToEditModeView();
        mModel.getInputValueInfos().clear();
        refreshInfoList();
    }

    public void startPick() {
        mView.startLocate();
        mView.notifyWaitStart();
    }
}
