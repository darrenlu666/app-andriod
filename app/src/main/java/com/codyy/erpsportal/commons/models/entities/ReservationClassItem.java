package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 2015/8/19.
 */
public class ReservationClassItem {

    /**
     * subjectPic : a9cfab72-8459-4ade-a66c-c0adac95398e.jpg
     * classSeq :  第二节
     * appointmentId : 24784483c8f44a81bb0c6a0b52a6efbd
     * classroomName : null
     * beginTime : 2015-07-23
     * speakerUserName : qq
     * schoolName : 金鸡湖/%小学
     * classlevelName : 一年级
     * clsClassroomId : null
     * subjectName : 数学
     * status : INIT
     */
    private String subjectPic;
    private String classSeq;
    private String appointmentId;
    private String classroomName;
    private String beginTime;
    private String speakerUserName;
    private String schoolName;
    private String classlevelName;
    private String clsClassroomId;
    private String subjectName;
    private String status;

    public void setSubjectPic(String subjectPic) {
        this.subjectPic = subjectPic;
    }

    public void setClassSeq(String classSeq) {
        this.classSeq = classSeq;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public void setSpeakerUserName(String speakerUserName) {
        this.speakerUserName = speakerUserName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public void setClsClassroomId(String clsClassroomId) {
        this.clsClassroomId = clsClassroomId;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubjectPic() {
        return subjectPic;
    }

    public String getClassSeq() {
        return classSeq;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public String getSpeakerUserName() {
        return speakerUserName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getClasslevelName() {
        return classlevelName;
    }

    public String getClsClassroomId() {
        return clsClassroomId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getStatus() {
        return status;
    }

    /**
     * @param object
     * @param items
     */
    public static void getReservationClass(JSONObject object, ArrayList<ReservationClassItem> items) {
        if ("success".endsWith(object.optString("result"))) {
            JSONArray array = object.optJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.optJSONObject(i);
                ReservationClassItem reservationClassItem = new ReservationClassItem();
                reservationClassItem.setAppointmentId(jsonObject.optString("appointmentId"));
                reservationClassItem.setSpeakerUserName(jsonObject.optString("speakerUserName"));
                reservationClassItem.setSubjectName(jsonObject.optString("subjectName"));
                reservationClassItem.setClasslevelName(jsonObject.optString("classlevelName"));
                reservationClassItem.setClassSeq(jsonObject.optString("classSeq"));
                reservationClassItem.setSchoolName(jsonObject.optString("schoolName"));
                reservationClassItem.setClsClassroomId(jsonObject.optString("clsClassroomId"));
                reservationClassItem.setSubjectPic(jsonObject.optString("subjectPic"));
                reservationClassItem.setBeginTime(jsonObject.optString("beginTime"));
                reservationClassItem.setStatus(jsonObject.optString("status"));
                reservationClassItem.setClassroomName(jsonObject.optString("classroomName"));
                items.add(reservationClassItem);
            }
        }
    }
}
