package com.codyy.erpsportal.statistics.models.entities;

import java.util.List;

/**
 * 开课概况统计结果
 * Created by gujiajia on 2016/8/7.
 */
public class CourseProfilesResult {

    private String result;

    private String areaLevel;

    private List<CourseProfile> dataList;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAreaLevel() {
        return areaLevel;
    }

    public void setAreaLevel(String areaLevel) {
        this.areaLevel = areaLevel;
    }

    public List<CourseProfile> getDataList() {
        return dataList;
    }

    public void setDataList(List<CourseProfile> dataList) {
        this.dataList = dataList;
    }
}
