package com.codyy.erpsportal.commons.models.entities.mainpage;

/**
 * 天津首页下面的数据
 * Created by gujiajia on 2016/8/15.
 */
public class TianJinCoursesProfile {

    /**
     * message : 成功
     * result : success
     * studentCount : 1089000
     * teacherCount : 80000
     * schoolCount : 672
     * errorCode : 1
     * classroomCount : 772
     */

    private String message;
    private String result;
    private int studentCount;
    private int teacherCount;
    private int schoolCount;
    private String errorCode;
    private int classroomCount;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public int getTeacherCount() {
        return teacherCount;
    }

    public void setTeacherCount(int teacherCount) {
        this.teacherCount = teacherCount;
    }

    public int getSchoolCount() {
        return schoolCount;
    }

    public void setSchoolCount(int schoolCount) {
        this.schoolCount = schoolCount;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getClassroomCount() {
        return classroomCount;
    }

    public void setClassroomCount(int classroomCount) {
        this.classroomCount = classroomCount;
    }
}
