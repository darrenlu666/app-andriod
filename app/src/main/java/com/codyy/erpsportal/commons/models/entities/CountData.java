package com.codyy.erpsportal.commons.models.entities;


import com.codyy.erpsportal.commons.utils.StringUtils;

import org.json.JSONObject;

/**
 * 监管首页数据集
 * Created by gujiajia on 2015/4/20.
 */
public class CountData {

    private String areaName;

    /**
     * 主讲课堂数
     */
    private int masterClassroomCount;

    /**
     * 接收课堂数
     */
    private int receiveClassroomCount;

    /**
     * 计划总课时数
     */
    private int planScheduleCount;

    /**
     * 受益学生数
     */
    private int studentBenefitCount;

    /**
     * 授课老师数
     */
    private int teachTeacherCount;

    /**
     * 周课时数
     */
    private int weekScheduleCount;

    /**
     * 应授课时数
     */
    private int weekPlanScheduleCount;

    /**
     * 已授课时数
     */
    private int weekScheduleOverCount;

    public int getMasterClassroomCount() {
        return masterClassroomCount;
    }

    public void setMasterClassroomCount(int masterClassroomCount) {
        this.masterClassroomCount = masterClassroomCount;
    }

    public int getReceiveClassroomCount() {
        return receiveClassroomCount;
    }

    public void setReceiveClassroomCount(int receiveClassroomCount) {
        this.receiveClassroomCount = receiveClassroomCount;
    }

    public int getPlanScheduleCount() {
        return planScheduleCount;
    }

    public void setPlanScheduleCount(int planScheduleCount) {
        this.planScheduleCount = planScheduleCount;
    }

    public int getStudentBenefitCount() {
        return studentBenefitCount;
    }

    public void setStudentBenefitCount(int studentBenefitCount) {
        this.studentBenefitCount = studentBenefitCount;
    }

    public int getTeachTeacherCount() {
        return teachTeacherCount;
    }

    public void setTeachTeacherCount(int teachTeacherCount) {
        this.teachTeacherCount = teachTeacherCount;
    }

    public int getWeekScheduleCount() {
        return weekScheduleCount;
    }

    public void setWeekScheduleCount(int weekScheduleCount) {
        this.weekScheduleCount = weekScheduleCount;
    }

    public int getWeekPlanScheduleCount() {
        return weekPlanScheduleCount;
    }

    public void setWeekPlanScheduleCount(int weekPlanScheduleCount) {
        this.weekPlanScheduleCount = weekPlanScheduleCount;
    }

    public int getWeekScheduleOverCount() {
        return weekScheduleOverCount;
    }

    public void setWeekScheduleOverCount(int weekScheduleOverCount) {
        this.weekScheduleOverCount = weekScheduleOverCount;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setAreaName(JSONObject jsonObject, String key) {
        setAreaName(StringUtils.replaceHtml(jsonObject.optString(key)));
    }

    public static CountData parseJson(JSONObject jsonObject) {
        CountData countData = new CountData();
        countData.setAreaName(jsonObject, "areaName");
        JSONObject countDataJson = jsonObject.optJSONObject("countData");
        countData.setMasterClassroomCount(countDataJson.optInt("masterClassroomCount"));
        countData.setReceiveClassroomCount(countDataJson.optInt("receiveClassroomCount"));
        countData.setPlanScheduleCount(countDataJson.optInt("planScheduleCount"));
        countData.setStudentBenefitCount(countDataJson.optInt("studentBenefitCount"));
        countData.setTeachTeacherCount(countDataJson.optInt("teachTeacherCount"));
        countData.setWeekScheduleCount(countDataJson.optInt("weekScheduleCount"));
        countData.setWeekPlanScheduleCount(countDataJson.optInt("weekPlanScheduleCount"));
        countData.setWeekScheduleOverCount(countDataJson.optInt("weekScheduleOverCount"));
        return countData;
    }
}
