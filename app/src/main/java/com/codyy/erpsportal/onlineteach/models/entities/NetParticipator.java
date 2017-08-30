package com.codyy.erpsportal.onlineteach.models.entities;


import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

import java.util.ArrayList;

/**
 * 网络授课－参与机构
 * Created by poe on 16-8-15.
 */
public class NetParticipator extends BaseTitleItemBar {
    private String classroom;//分会场教室名称
    private String schoolName;//学校名称
    private ArrayList<String>  teacher;//听课教师
    private ArrayList<String> student;//听课学生
    private boolean selfSchool;//是否是本校　

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public ArrayList<String> getTeacher() {
        return teacher;
    }

    public void setTeacher(ArrayList<String> teacher) {
        this.teacher = teacher;
    }

    public ArrayList<String> getStudent() {
        return student;
    }

    public void setStudent(ArrayList<String> student) {
        this.student = student;
    }

    public boolean isSelfSchool() {
        return selfSchool;
    }

    public void setSelfSchool(boolean selfSchool) {
        this.selfSchool = selfSchool;
    }
}
