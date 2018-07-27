package com.example.team.mapapplication.base;

import android.content.Context;

/**
 * Created by Ellly on 2018/7/24.
 */

public class BasePresenter<V extends IBaseView> {
    protected V mView;
    protected Context mContext;

    public void attach(V view){
        mView = view;
        mContext = view.getContext();
    }

    public void detach(){
        mContext = null;
    }

    public static BasePresenter newInstance(){
        return new BasePresenter();
    }
}
