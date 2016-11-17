package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldh on 2015/9/10.
 */
public class SchoolNetClassListModel {

    /**
     * contactPhone : null
     * areaTitle : null
     * schoolName : web前端学校
     * status : null
     * speakerUserName : webqdtea
     * beginTime : null
     * subjectName : 语文
     * areaName : null
     * classlevelName : 一年级
     * clsClassroomId : b8f8a8b6f7a64ceaab961ac58c1961fc
     * skey : null
     * classSeq :  第五节
     * appointmentId : e2a1b458ee054a37a368ecd71e6187e0
     * subjectPic : null
     * classroomName : 主讲教室1
     * transFlag:N
     */
    private String contactPhone; //联系方式
    private String areaTitle;//
    private String schoolName;
    private String status;
    private String speakerUserName;
    private String beginTime;
    private String subjectName;
    private String areaName;
    private String classlevelName;
    private String clsClassroomId;
    private String skey;
    private String classSeq;
    private String appointmentId;
    private String subjectPic;
    private String classroomName;
    private String transFlag;
    private String roomType;
    private String mainClassId;

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void setAreaTitle(String areaTitle) {
        this.areaTitle = areaTitle;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSpeakerUserName(String speakerUserName) {
        this.speakerUserName = speakerUserName;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public void setClsClassroomId(String clsClassroomId) {
        this.clsClassroomId = clsClassroomId;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public void setClassSeq(String classSeq) {
        this.classSeq = classSeq;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setSubjectPic(String subjectPic) {
        this.subjectPic = subjectPic;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public void setTransFlag(String transFlag){
        this.transFlag = transFlag;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getAreaTitle() {
        return areaTitle;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getStatus() {
        return status;
    }

    public String getSpeakerUserName() {
        return speakerUserName;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getClasslevelName() {
        return classlevelName;
    }

    public String getClsClassroomId() {
        return clsClassroomId;
    }

    public String getSkey() {
        return skey;
    }

    public String getClassSeq() {
        return classSeq;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getSubjectPic() {
        return subjectPic;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public String getTransFlag(){
        return transFlag;
    }

    public String getMainClassId() {
        return mainClassId;
    }

    public void setMainClassId(String mainClassId) {
        this.mainClassId = mainClassId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }


    public static List<SchoolNetClassListModel> parseObject(JSONObject jsonObject){
        List<SchoolNetClassListModel> list = new ArrayList<>();

        JSONArray jsonArray = jsonObject.optJSONArray("list");

        for(int i = 0;i < jsonArray.length(); i++){
            SchoolNetClassListModel schoolNetClassListModel = new SchoolNetClassListModel();
            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
            schoolNetClassListModel.setAppointmentId(jsonObject1.optString("appointmentId"));
            schoolNetClassListModel.setSpeakerUserName(jsonObject1.optString("speakerUserName"));
            schoolNetClassListModel.setSubjectName(jsonObject1.optString("subjectName"));
            schoolNetClassListModel.setClasslevelName(jsonObject1.optString("classlevelName"));
            schoolNetClassListModel.setClassSeq(jsonObject1.optString("classSeq"));
            schoolNetClassListModel.setSchoolName(jsonObject1.optString("schoolName"));
            schoolNetClassListModel.setClsClassroomId(jsonObject1.optString("clsClassroomId"));
            schoolNetClassListModel.setSubjectPic(jsonObject1.optString("subjectPic"));
            if (!jsonObject1.isNull("beginTime")) {
                schoolNetClassListModel.setBeginTime(jsonObject1.optString("beginTime"));
            }
            schoolNetClassListModel.setStatus(jsonObject1.optString("status"));
            schoolNetClassListModel.setClassroomName(jsonObject1.optString("classroomName"));
            schoolNetClassListModel.setContactPhone(jsonObject1.optString("contactPhone"));
            schoolNetClassListModel.setAreaName(jsonObject1.optString("areaName"));
            schoolNetClassListModel.setSkey(jsonObject1.optString("skey"));
            schoolNetClassListModel.setAreaTitle(jsonObject1.optString("areaTitle"));
            schoolNetClassListModel.setTransFlag(jsonObject1.optString("transFlag"));
            schoolNetClassListModel.setRoomType(jsonObject1.optString("roomType"));
            schoolNetClassListModel.setMainClassId(jsonObject1.optString("mainClassId"));

            list.add(schoolNetClassListModel);
        }

        return list;
    }

}
