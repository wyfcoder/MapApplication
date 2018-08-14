package com.example.team.mapapplication.business.acquireinfo;

import com.blankj.utilcode.util.SPUtils;
import com.example.team.mapapplication.base.BasePresenter;

/**
 * Created by Ellly on 2018/8/13.
 */

public class AcquirePresenter extends BasePresenter<IAcquireView> {
    private AcquireModel mModel = new AcquireModel();

    public void saveGender(String selected_gender) {
        AcquireModel.saveGender(selected_gender);
    }

    public void saveHeight(String text) {
        AcquireModel.saveHeight(text);
    }
}
