package com.example.team.mapapplication.business.main;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.example.team.mapapplication.base.BaseModel;
import com.example.team.mapapplication.bean.DataDisplayInfo;
import com.example.team.mapapplication.bean.InputValueInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by Ellly on 2018/7/24.
 */

/**
 * 用于保存、获取和上传各类MainActivity内的产生的数据
 */
public class MainViewModel extends BaseModel {

    public static final int EDIT_MODE = 1;
    public static final int WIFI_MODE = 0;
    public static final int DISPLAY_MODE = 2;

    private LatLng mLocInfo;
    private Boolean mIsFirstLoc;
    private float mRadius;
    private List<InputValueInfo> mInfos = new ArrayList<>();
    private boolean isDataWaiting = false;
    private boolean isToolbarHide = false;
    private int mModeStatus = 1;
    private int mScreenHeight;
    private String mDisplayFileName;
    private List<InputValueInfo> mDisplayData = new ArrayList<>();
    private boolean mPickStarted;
    private long mSavedTimeMills; // the time mills when the first click on back button takes place. wyy
    private double mLastX; // seems the location accuracy circle. wyy
    private int mMorphBtnHeight;
    private int mMorphBtnWidth;

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
                data.add(new LatLng(latitude, longitude));
            }
        }
        return data;

    }

    public void saveInfo(String info) {

        SPUtils.getInstance().put(TimeUtils.getNowString(), info);

        InputValueInfo e = new InputValueInfo();
        e.setLatLng(getLatLng());
        e.setValue(Double.valueOf(info));
        mInfos.add(e);
        Log.d("InputInfo", info);
    }

    public List<InputValueInfo> getInputValueInfos(){
        // mocking data wyy
        /*for (int i = 0; i < 20; i++){
            InputValueInfo e = new InputValueInfo();
            e.setValue(12.2 + i);
            e.setLatLng(new LatLng(34.1 + i, 108.3 + i));
            mInfos.add(e);
        }*/
        return mInfos;
    }

    public boolean isToolbarHide() {
        return isToolbarHide;
    }

    public void setToolbarHide(boolean toolbarHide) {
        isToolbarHide = toolbarHide;
    }

    public void setModeStatus(int modeStatus) {
        this.mModeStatus = modeStatus;
    }

    public int getModeStatus() {
        return mModeStatus;
    }

    public void setScreenHeight(int screenHeight) {
        this.mScreenHeight = screenHeight;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    public void setDisplayFileName(String displayFileName) {
        this.mDisplayFileName = displayFileName;
    }

    public String getDisplayFileName() {
        return mDisplayFileName;
    }

    /**
     * get data for display mode. wyy
     * @return data to display
     */
    public List<InputValueInfo> getDisplayData() {
        /* mocking data
        for (int i = 0; i < 20; i++) {
            InputValueInfo e = new InputValueInfo();
            e.setValue(12.2 + i);
            e.setLatLng(new LatLng(34.1 + i, 108.3 + i));
            mDisplayData.add(e);
        }*/

        mDisplayData = LitePal.where("fileName = ?", getDisplayFileName()).find(InputValueInfo.class);
        for (InputValueInfo ivi : mDisplayData){
            ivi.setLatLng(new LatLng(ivi.getLatitude(), ivi.getLongitude()));
        }
        return mDisplayData;
    }

    public void setDisplayData(List<InputValueInfo> mDisplayData) {
        this.mDisplayData = mDisplayData;
    }

    public void saveValuesToDB(String text) {
        // save the current data with the assigned file name. wyy

        List<InputValueInfo> data = getInputValueInfos();


        // another copy of the original data would not solve the problem of the failure on saving data. wyy
        // but on account of the stability I don't want to delete this right now until this problem is completely clear. wyy
        List<InputValueInfo> data_copy = new ArrayList<>();

        for (InputValueInfo d : data){
            InputValueInfo tmp = new InputValueInfo();
            tmp.setValue(d.getValue());
            tmp.setLatLng(d.getLatLng());
            data_copy.add(d);
        }


        if (isThisNameUsed(text)){
            LitePal.deleteAll(InputValueInfo.class, "fileName = ?", text);
        }
        for (InputValueInfo d : data_copy){
            d.assignBaseObjId(0);  // this line is the key I think. no matter how I copy the original data,
                                   // once I do the assignment of any value of the object and add it into the new list,
                                   // the baseObjIds of the original data will be written simultaneously into the new ones
                                   // even though I have never done anything about the ids.
                                   // thus by referring to the official doc I use this method to force the Id to be 0.
                                   // and it works, lucky. wyy
            d.setFileName(text);
            // accidentally find that LitePal seems not able to deal with parcelable class...
            d.setLatitude(d.getLatLng().latitude);
            d.setLongitude(d.getLatLng().longitude);
            Log.d("Data Info", "Data Info: \n" + d.getFileName() + "\n" + d.getValue() + "\n" + d.getLatLng());
            boolean result = d.save();
            Log.d("IS_SUCED", result + "   result");
        }

    }

    public boolean isThisNameUsed(String text) {
        List<InputValueInfo> preData = LitePal.where("fileName = ?", text).find(InputValueInfo.class);

        // delete the previous same-named data and re-save the new ones.
        return preData != null && preData.size() > 0;
    }

    public void saveDisplayToDB(String text) {
        // save the saving behavior as another table. wyy

        Calendar calendar = Calendar.getInstance();
        DataDisplayInfo di = new DataDisplayInfo();
        di.setFileName(text);
        di.setCalendar(calendar);
        di.setYear(calendar.get(Calendar.YEAR));
        di.setMonth(calendar.get(Calendar.MONTH ) + 1);
        di.setDay(calendar.get(Calendar.DAY_OF_MONTH));

        List<DataDisplayInfo> preData = LitePal.where("mFileName = ?", text).find(DataDisplayInfo.class);

        // determine whether the mFileName has been used. If not, then save it or it will be updated.
        if (preData == null || preData.size() <= 0){
            di.save();
        }else {
            di.updateAll("mFileName = ?", text);
        }
    }


    public void setPickStarted(boolean pickStarted) {
        this.mPickStarted = pickStarted;
    }

    public boolean isPickStarted() {
        return mPickStarted;
    }

    public long getSavedTimeMills() {
        return mSavedTimeMills;
    }

    public void setSavedTimeMills(long savedTimeMills) {
        this.mSavedTimeMills = savedTimeMills;
    }

    @Deprecated
    public String[] getNeedPermissions() {
        String[] permissions = {PermissionConstants.LOCATION, PermissionConstants.STORAGE, PermissionConstants.SENSORS, PermissionConstants.PHONE};
        /*boolean[] grants = new boolean[permissions.length];
        for (int i = 0; i < grants.length; i++){
            grants[i] = PermissionUtils.isGranted(permissions[i]);
        }
        List<String> needGrantPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++){
            if (!grants[i]){
                needGrantPermissions.add(permissions[i]);
            }
        }*/
        return permissions;
    }

    public void setLastX(double lastX) {
        this.mLastX = lastX;
    }

    public double getLastX() {
        return mLastX;
    }

    public void setMorphBtnHeight(int morphBtnHeight) {
        this.mMorphBtnHeight = morphBtnHeight;
    }

    public int getmMorphBtnHeight() {
        return mMorphBtnHeight;
    }

    public void setmMorphBtnHeight(int mMorphBtnHeight) {
        this.mMorphBtnHeight = mMorphBtnHeight;
    }

    public void setMorphBtnWidth(int morphBtnWidth) {
        this.mMorphBtnWidth = morphBtnWidth;
    }

    public int getMorphBtnWidth() {
        return mMorphBtnWidth;
    }
}
