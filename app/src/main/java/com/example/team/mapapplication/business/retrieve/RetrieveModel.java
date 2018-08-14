package com.example.team.mapapplication.business.retrieve;

import com.example.team.mapapplication.base.BaseModel;
import com.example.team.mapapplication.bean.DataDisplayInfo;
import com.example.team.mapapplication.bean.InputValueInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ellly on 2018/8/12.
 */

public class RetrieveModel extends BaseModel {

    private List<DataDisplayInfo> displayInfos = new ArrayList<>();

    /**
     * aimed to require data list from database or online
     * @return data list
     */
    public List<DataDisplayInfo> getDisplayInfos() {

        displayInfos = LitePal.findAll(DataDisplayInfo.class);

        return displayInfos;
    }

    public void deleteThisDataList(String fileName) {
        LitePal.deleteAll(InputValueInfo.class, "fileName = ?", fileName);
    }

    public void deleteThisDisplayInfo(String fileName) {
        LitePal.deleteAll(DataDisplayInfo.class, "mFileName = ?", fileName);
    }
}
