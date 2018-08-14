package com.example.team.mapapplication.business.retrieve;

import com.example.team.mapapplication.base.BaseModel;
import com.example.team.mapapplication.bean.DataDisplayInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ellly on 2018/8/12.
 */

public class RetrieveViewModel extends BaseModel {

    private List<DataDisplayInfo> mDisplayInfos = new ArrayList<>();

    public List<DataDisplayInfo> getDisplayInfos() {
        return mDisplayInfos;
    }

    public void setDisplayInfos(List<DataDisplayInfo> displayInfos) {
        this.mDisplayInfos = displayInfos;
    }
}
