package com.codyy.erpsportal.commons.models.entities.mainpage;

/**
 * 台州首页地图下方数据统计.
 * Created by poe on 17-9-13.
 */

public class TaiZhouPanel {
    /**
     * areaName : 31812
     * netTeachCount : 14750
     * resourceCount : 37444
     * scheduleCount : 72437
     * shoolCount : 64767
     */

    private String areaName;
    private int netTeachCount;
    private int resourceCount;
    private int scheduleCount;
    private int shoolCount;

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getNetTeachCount() {
        return netTeachCount;
    }

    public void setNetTeachCount(int netTeachCount) {
        this.netTeachCount = netTeachCount;
    }

    public int getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(int resourceCount) {
        this.resourceCount = resourceCount;
    }

    public int getScheduleCount() {
        return scheduleCount;
    }

    public void setScheduleCount(int scheduleCount) {
        this.scheduleCount = scheduleCount;
    }

    public int getShoolCount() {
        return shoolCount;
    }

    public void setShoolCount(int shoolCount) {
        this.shoolCount = shoolCount;
    }
}
