package com.codyy.erpsportal.repairs.models.entities;

/**
 * 报修教室筛选项
 * Created by gujiajia on 2017/3/22.
 */

public class ClassroomFilterItem implements RepairFilterItem {

    private String classroomId;

    private String classroomNo;

    private String classroomName;

    public ClassroomFilterItem(String classroomId, String classroomNo, String classroomName) {
        this.classroomId = classroomId;
        this.classroomNo = classroomNo;
        this.classroomName = classroomName;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public String getClassroomNo() {
        return classroomNo;
    }

    public void setClassroomNo(String classroomNo) {
        this.classroomNo = classroomNo;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    @Override
    public String content() {
        if (classroomId == null) {
            return "全部";
        } else {
            return classroomNo + "（" + classroomName + "）";
        }
    }
}
