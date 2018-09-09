package com.example.team.mapapplication.engine;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Ellly on 2018/9/1.
 */

public class ScreenShotHelper {
    public abstract class ScreenShotListener{
        abstract void afterScreenShot(Bitmap bitmap);
        void beforeScreenShot(){

        }
    }

    public ScreenShotHelper(){}

    public ScreenShotHelper setDefaultListener(){
        mListener = new ScreenShotListener() {
            @Override
            void afterScreenShot(Bitmap bitmap) {
                saveScreenShot(bitmap);
            }
        };
        return this;
    }

    private ScreenShotListener mListener;

    public void shotScreen(BaiduMap map){
        if (mListener != null){
            mListener.beforeScreenShot();
        }
        map.snapshot(new BaiduMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                if (mListener != null){
                    mListener.afterScreenShot(bitmap);
                }
            }
        });
    }

    @Deprecated
    public Bitmap shotScreen(Activity activity){

        if (mListener != null){
            mListener.beforeScreenShot();
        }

        View dView = activity.getWindow().getDecorView();
//        dView.setDrawingCacheEnabled(true);
//        dView.buildDrawingCache();
//        Bitmap bitmap = dView.getDrawingCache();
        Bitmap bitmap = getBitmapFromView(dView);

        if (mListener != null){
            mListener.afterScreenShot(bitmap);
        }
        return bitmap;
    }

    public static Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache(true);
        return bitmap;
    }


    public void saveScreenShot(Bitmap bitmap) {
        if (bitmap != null) {
            try {
                // 获取内置SD卡路径
                String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                // 图片文件路径
                String filePath = sdCardPath + File.separator + "screenshot.png";
                File file = new File(filePath);
                FileOutputStream os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
                Log.d("a7888", "存储完成");
                ToastUtils.showLong("存储完成: " + filePath);
            } catch (Exception e) {
            }
        }
    }
}
