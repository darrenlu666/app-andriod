package com.codyy.erpsportal.repairs.models.entities;

/**
 * 有报修时间的学校
 * Created by gujiajia on 2017/3/20.
 */

public class RepairSchool {

    public RepairSchool(){
        super();
    }

    public RepairSchool(String schoolId, String schoolName, String areaName, int malfunctionCount, int handledCount) {
        this.schoolId = schoolId;
        this.schoolName = schoolName;
        this.areaName = areaName;
        this.malfunctionCount = malfunctionCount;
        this.handledCount = handledCount;
    }

    private String schoolId;

    private String schoolName;

    private String areaName;

    /**
     * 问题总数
     */
    private int malfunctionCount;

    /**
     * 已处理问题数
     */
    private int handledCount;

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getMalfunctionCount() {
        return malfunctionCount;
    }

    public void setMalfunctionCount(int malfunctionCount) {
        this.malfunctionCount = malfunctionCount;
    }

    public int getHandledCount() {
        return handledCount;
    }

    public void setHandledCount(int handledCount) {
        this.handledCount = handledCount;
    }
}
