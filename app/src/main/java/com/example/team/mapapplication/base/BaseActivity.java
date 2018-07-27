package com.example.team.mapapplication.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Ellly on 2018/7/24.
 */

public abstract class BaseActivity<V extends BasePresenter> extends AppCompatActivity{
    protected V mPresenter = createPresenter();

    protected abstract V createPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }
}
