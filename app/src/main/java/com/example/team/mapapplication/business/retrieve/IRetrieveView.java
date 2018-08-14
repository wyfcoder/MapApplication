package com.example.team.mapapplication.business.retrieve;

import com.example.team.mapapplication.base.IBaseView;
import com.example.team.mapapplication.base.IBaseViewModel;

/**
 * Created by Ellly on 2018/8/12.
 */

public interface IRetrieveView extends IBaseView, IBaseViewModel{
    void notifyDataSetChanged();
}
