package com.codyy.erpsportal.commons.models.entities.customized;

import java.util.List;

/**
 * 专递课堂　园区－学段
 * Created by poe on 24/07/17.
 */

public class SipSemesterLesson {

    private String semesterId;//
    private String semesterName;//小学
    private List<SipLesson> scheduleList;//学校集合

    public String getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public List<SipLesson> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<SipLesson> scheduleList) {
        this.scheduleList = scheduleList;
    }
}
