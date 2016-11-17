package com.codyy.erpsportal.statistics.models.entities;

/**
 * 开课概况统计
 * Created by gujiajia on 2016/8/7.
 */
public class CourseProfile {
    /**
     * avgRate : 0
     * classroomCnt : 0
     * doClsroomCnt : 0
     * doClsroomRate : 0
     * downCnt : 0
     * downRate : 0
     * id : 504525871d804363b3c0f07ca3313dc3
     * name : 山东省
     * planCnt : 0
     * requiredCnt : null
     * type : AREA
     */

    /**
     * 平均开课数
     */
    private float avgRate;

    /**
     * 应开课教室数/应受邀教室数
     */
    private String classroomCnt;

    /**
     * 实开课教室数/实受邀教室数
     */
    private String doClsroomCnt;

    /**
     * 开课教室占比
     */
    private float doClsroomRate;

    /**
     * 实开课时数/实听课时数
     */
    private String downCnt;

    /**
     * 实开课时占比
     */
    private float downRate;

    /**
     * 区域/学校 ID
     */
    private String id;

    /**
     * 区域/学校 名称
     */
    private String name;

    /**
     * 计划课时数/计划受邀课时数
     */
    private String planCnt;

    /**
     * 区域类型
     */
    private String type;

    public float getAvgRate() {
        return avgRate;
    }

    public void setAvgRate(float avgRate) {
        this.avgRate = avgRate;
    }

    public String getClassroomCnt() {
        return classroomCnt;
    }

    public void setClassroomCnt(String classroomCnt) {
        this.classroomCnt = classroomCnt;
    }

    public String getDoClsroomCnt() {
        return doClsroomCnt;
    }

    public void setDoClsroomCnt(String doClsroomCnt) {
        this.doClsroomCnt = doClsroomCnt;
    }

    public float getDoClsroomRate() {
        return doClsroomRate;
    }

    public void setDoClsroomRate(float doClsroomRate) {
        this.doClsroomRate = doClsroomRate;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanCnt() {
        return planCnt;
    }

    public void setPlanCnt(String planCnt) {
        this.planCnt = planCnt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
