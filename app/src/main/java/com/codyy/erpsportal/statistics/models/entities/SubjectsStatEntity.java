package com.codyy.erpsportal.statistics.models.entities;

/**
 * 学科统计数据项
 * Created by gujiajia on 2016/8/11.
 */
public class SubjectsStatEntity {
    /**
     * areaName : 直属校
     * downCnt : 0
     * downRate : 0
     * planCnt : 0
     * requiredCnt : 0
     * subjectId : 6a3387092fad4540b05114e2a45dc80b
     * subjectName : 江苏语文
     */

    private String downCnt;
    private float downRate;
    private String planCnt;
    private String requiredCnt;
    private String subjectId;
    private String subjectName;

    public String getDownCnt() {
        return downCnt;
    }

    public void setDownCnt(String downCnt) {
        this.downCnt = downCnt;
    }

    public float getDownRate() {
        return downRate;
    }

    public void setDownRate(float downRate) {
        this.downRate = downRate;
    }

    public String getPlanCnt() {
        return planCnt;
    }

    public void setPlanCnt(String planCnt) {
        this.planCnt = planCnt;
    }

    public String getRequiredCnt() {
        return requiredCnt;
    }

    public void setRequiredCnt(String requiredCnt) {
        this.requiredCnt = requiredCnt;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
