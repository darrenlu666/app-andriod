package com.codyy.erpsportal.repairs.models.entities;

/**
 * 有报修记录的学校
 * Created by gujiajia on 2017/3/20.
 */

public class RepairSchool {

    public RepairSchool(){
        super();
    }

    public RepairSchool(String schoolId, String schoolName, String areaName, int allCount, int dealCount) {
        this.schoolId = schoolId;
        this.schoolName = schoolName;
        this.areaName = areaName;
        this.allCount = allCount;
        this.dealCount = dealCount;
    }

    /**
     * 学校id
     */
    private String schoolId;

    /**
     * 学校名称
     */
    private String schoolName;

    /**
     * 学校所在区域名称，取最下级
     */
    private String areaName;

    /**
     * 问题总数
     */
    private int allCount;

    /**
     * 已处理问题数
     */
    private int dealCount;

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

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public int getDealCount() {
        return dealCount;
    }

    public void setDealCount(int dealCount) {
        this.dealCount = dealCount;
    }
}
