package com.codyy.erpsportal.commons.models.entities;

/**
 * 接收教室详情
 * Created by kmdai on 2015/4/16.
 */
public class ReceiveClassInfo {
    /**
     * 接受教室
     */
    public static final String TYPE_RECEIVE = "receive";
    /**
     * 本校听课学生
     */
    public static final String TYPE_STUDENT = "student";
    private String schoolName;
    private String studentNum;
    private String realName;
    private String contactPhone;
    private String type;

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}
