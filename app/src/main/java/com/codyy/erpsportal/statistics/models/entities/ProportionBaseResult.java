package com.codyy.erpsportal.statistics.models.entities;

import java.util.List;

/**
 * 开课比分析基础接口结果
 * Created by gujiajia on 2016/8/10.
 */
public class ProportionBaseResult {

    private boolean result;

    private List<TermEntity> trimesterList;

    private List<AreaInfo> areaList;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<TermEntity> getTrimesterList() {
        return trimesterList;
    }

    public void setTrimesterList(List<TermEntity> trimesterList) {
        this.trimesterList = trimesterList;
    }

    public List<AreaInfo> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<AreaInfo> areaList) {
        this.areaList = areaList;
    }
}
