package com.codyy.erpsportal.statistics.models.entities;

import android.text.TextUtils;

/**
 * 按学期统计项
 * Created by gujiajia on 2016/8/10.
 */
public class TermlyPropItem {

    /**
     * areaName : 直属校
     * baseAreaId : 580b6c458db349ee8a35ec70ec5bd201
     * baseTrimesterId : 1
     * classroomName : null
     * clsClassroomId : null
     * clsSchoolId : null
     * columnName : 市
     * directPlanCount : 0
     * directRatio : 0
     * directRealCount : 0
     * directSubtract : null
     * downFlag : false
     * planCount : 0
     * ratio : 0
     * ratioType : DIRECT
     * realCount : 0
     * schoolName : null
     * subtract : null
     * trimesterName : 2016上学期
     */

    private String areaName;
    private String schoolName;
    private String classroomName;

    private String baseAreaId;
    private String clsSchoolId;
    private String clsClassroomId;

    private String baseTrimesterId;

    private String columnName;
    private boolean downFlag;
    private int planCount;

    private float ratio;
    private float directRatio;
    private String ratioType;
    private String trimesterName;

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getBaseAreaId() {
        return baseAreaId;
    }

    public void setBaseAreaId(String baseAreaId) {
        this.baseAreaId = baseAreaId;
    }

    public String getBaseTrimesterId() {
        return baseTrimesterId;
    }

    public void setBaseTrimesterId(String baseTrimesterId) {
        this.baseTrimesterId = baseTrimesterId;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public String getClsClassroomId() {
        return clsClassroomId;
    }

    public String getClsSchoolId() {
        return clsSchoolId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isDownFlag() {
        return downFlag;
    }

    public void setDownFlag(boolean downFlag) {
        this.downFlag = downFlag;
    }

    public int getPlanCount() {
        return planCount;
    }

    public void setPlanCount(int planCount) {
        this.planCount = planCount;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public float getDirectRatio() {
        return directRatio;
    }

    public void setDirectRatio(float directRatio) {
        this.directRatio = directRatio;
    }

    public String getRatioType() {
        return ratioType;
    }

    public void setRatioType(String ratioType) {
        this.ratioType = ratioType;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getTrimesterName() {
        return trimesterName;
    }

    public void setTrimesterName(String trimesterName) {
        this.trimesterName = trimesterName;
    }

    public String getId() {
        if (!TextUtils.isEmpty(baseAreaId)) {
            return baseAreaId;
        } else if (!TextUtils.isEmpty(clsSchoolId)) {
            return clsSchoolId;
        } else {
            return clsClassroomId;
        }
    }

    public String getName() {
        if (!TextUtils.isEmpty(areaName)) {
            return areaName;
        } else if (!TextUtils.isEmpty(schoolName)) {
            return schoolName;
        } else {
            return classroomName;
        }
    }

    /**
     * 是否是直属校
     * @return 是否是直属校
     */
    public boolean isDirect() {
        return "DIRECT".equals(ratioType);
    }
}
