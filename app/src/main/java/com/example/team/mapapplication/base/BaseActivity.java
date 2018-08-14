package com.example.team.mapapplication.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


import com.blankj.utilcode.util.ToastUtils;
import com.example.team.mapapplication.R;
import com.jaeger.library.StatusBarUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

/**
 * Created by Ellly on 2018/7/24.
 */

public abstract class BaseActivity<V extends BasePresenter> extends AppCompatActivity{
    protected V mPresenter = createPresenter();
    protected Toolbar mToolbar;

    protected abstract V createPresenter();

    protected abstract int getContentViewId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.sky_blue));
        mToolbar = getToolbar();
        if (mToolbar != null){
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getToolbarTitle());
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, getDrawerIcon()));
            actionBar.setSubtitle(getSubTitle());
        }
    }

    protected int getDrawerIcon() {
        return R.drawable.ic_drawer_actionbar;
    }

    protected CharSequence getSubTitle() {
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onHomeIndicatorClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onHomeIndicatorClicked() {
        ToastUtils.showShort("返回");
    }

    protected CharSequence getToolbarTitle(){
        return "MapApplication";
    }


    protected Toolbar getToolbar() {
        return findViewById(R.id.toolbar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
    }
}
