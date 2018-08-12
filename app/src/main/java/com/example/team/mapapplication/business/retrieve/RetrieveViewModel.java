package com.example.team.mapapplication.business.retrieve;

import com.example.team.mapapplication.base.BaseModel;
import com.example.team.mapapplication.bean.DataDisplayInfo;
import com.example.team.mapapplication.bean.InputValueInfo;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ellly on 2018/8/12.
 */

public class RetrieveViewModel extends BaseModel {

    private List<DataDisplayInfo> mDisplayInfos = new ArrayList<>();

    public List<DataDisplayInfo> getDisplayInfos() {

        mDisplayInfos = LitePal.findAll(DataDisplayInfo.class);

        return mDisplayInfos;
    }

    public void setDisplayInfos(List<DataDisplayInfo> displayInfos) {
        this.mDisplayInfos = displayInfos;
    }
}
