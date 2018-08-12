package com.example.team.mapapplication.business.retrieve;

import com.example.team.mapapplication.base.BasePresenter;

/**
 * Created by Ellly on 2018/8/12.
 */

public class RetrievePresenter extends BasePresenter<IRetrieveView> {

    private RetrieveViewModel mViewModel;

    private RetrieveModel mModel = new RetrieveModel();

    @Override
    public void attach(IRetrieveView view) {
        super.attach(view);
        mViewModel = (RetrieveViewModel) mView.getModel();
    }

    public void requireData() {
        mViewModel.setDisplayInfos(mModel.getDisplayInfos());
    }
}
