package com.example.team.mapapplication.business.retrieve;

import com.example.team.mapapplication.base.BaseModel;
import com.example.team.mapapplication.bean.DataDisplayInfo;

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
        for (int i = 0; i < 25; i++){
            displayInfos.add(new DataDisplayInfo());
        }
        return displayInfos;
    }
}
