package com.codyy.erpsportal.commons.models.entities.filter;


import com.codyy.erpsportal.commons.controllers.fragments.filters.LiveFilterFragment;

/**
 * Created by yangxinwu on 2015/6/12.
 */
public class LiveFilterRightItem {

    /**
     * 具体的属性值
     */
    private String value;

    /**
     * 筛选级别 隐藏标签 如：直属校
     */
    private String levelName;

    /**
     * 筛选Table标签的别名（显示出来的） 如：学校
     */
    private String alignName;


    /**
     * 除了tree用到的id
     */
    private String secondId;

    /**
     * 地区筛选id
     */
    private String id;


    /**
     *
     * @param value
     * @param levelName
     * @param alignName
     * @param id
     */
    public LiveFilterRightItem(String value, String levelName, String alignName, String id) {
        this.value = value;
        this.levelName = levelName;
        this.alignName = alignName;
        this.id = id;

        //直属校需要特殊化
        if(alignName.equals(LiveFilterFragment.STR_SCHOOL_DIRECT)){
            this.alignName = LiveFilterFragment.STR_SCHOOL;
        }

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlignName() {
        return alignName;
    }

    public void setAlignName(String alignName) {
        this.alignName = alignName;
    }

    public String getSecondId() {
        return secondId;
    }

    public void setSecondId(String secondId) {
        this.secondId = secondId;
    }
}
