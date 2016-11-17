package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangxinwu on 2015/8/18.
 */
public class RemoteDirectorItem {

    /**
     * subjectPic : 20787737-2be9-4a19-80c1-270d870fe1fa.gif
     * classSeq : 2
     * appointmentId : c96ff9fcaae547b2909d2232390e6440
     * beginTime : null
     * speakerUserName : 王琦琦
     * schoolName : 金鸡湖/%小学
     * classlevelName : 一年级
     * clsClassroomId : 7368b8c930dd4b3d9f60d8169d38bc35
     * subjectName : 语文
     * status : null
     */
    private String subjectPic;
    private String classSeq;
    private String appointmentId;
    private String beginTime;
    private String speakerUserName;
    private String schoolName;
    private String classlevelName;
    private String clsClassroomId;
    private String clsClassRoomName;
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

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public void setSpeakerUserName(String speakerUserName) {
        this.speakerUserName = speakerUserName;
    }

    public String getClsClassRoomName() {
        return clsClassRoomName;
    }

    public void setClsClassRoomName(String clsClassRoomName) {
        this.clsClassRoomName = clsClassRoomName;
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


    public static RemoteDirectorItem parseJson(JSONObject jsonObject) {
        RemoteDirectorItem remoteDirectorItem = new RemoteDirectorItem();
        remoteDirectorItem.setAppointmentId(jsonObject.optString("appointmentId"));
        remoteDirectorItem.setSpeakerUserName(jsonObject.optString("speakerUserName"));
        remoteDirectorItem.setSubjectName(jsonObject.optString("subjectName"));
        remoteDirectorItem.setClasslevelName(jsonObject.optString("classlevelName"));
        remoteDirectorItem.setClassSeq(jsonObject.optString("classSeq"));
        remoteDirectorItem.setSchoolName(jsonObject.optString("schoolName"));
        remoteDirectorItem.setClsClassroomId(jsonObject.optString("clsClassroomId"));
        remoteDirectorItem.setSubjectPic(jsonObject.optString("subjectPic"));
        remoteDirectorItem.setBeginTime(jsonObject.optString("beginTime"));
        remoteDirectorItem.setStatus(jsonObject.optString("status"));
        remoteDirectorItem.setClsClassRoomName(jsonObject.optString("classRoomName"));
        return remoteDirectorItem;
    }

    public static List<RemoteDirectorItem> parseJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }
        List<RemoteDirectorItem> remoteDirectorItemList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            RemoteDirectorItem remoteDirectorItem = parseJson(jsonObject);
            remoteDirectorItemList.add(remoteDirectorItem);
        }
        return remoteDirectorItemList;
    }


}
