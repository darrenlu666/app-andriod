package com.codyy.erpsportal.commons.models.entities.filter;

/**
 * Created by yangxinwu on 2015/6/11.
 */
public class AreaItem {

    private  String areaName;
    private  String areaId;
    private  String selectArea;//已经选中的区域

    public String getSelectArea() {
        return selectArea;
    }

    public void setSelectArea(String selectArea) {
        this.selectArea = selectArea;
    }

    public AreaItem(String areaName, String areaId, String selectArea) {
        this.areaName = areaName;
        this.areaId = areaId;
        this.selectArea = selectArea;
    }


    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
}
