package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 2015/9/15.
 */
public class ScheduleLiveView {

    /**
     * scheduleDetailId : 7bcf4c0ef02e44c68b5e64333f6ce4f8
     * classroomId : 466b98937a2c4bcbb49e00846e6b92b4
     * serverAddress : rtmp://10.1.100.222:1938/pms
     * teacherName : 本藤莲<input/>
     * schoolName : 王刚学校
     * roomName : 主讲教室1
     * status : INIT
     * streamingServerType : DMC
     * dmsServerHost : http://10.1.0.1/dmc
     */

    private String scheduleDetailId;
    private String classroomId;
    private String serverAddress;
    private String teacherName;
    private String schoolName;
    private String roomName;
    private String status;
    private String streamingServerType;
    private String dmsServerHost;
    private String streamUrl;

    public void setScheduleDetailId(String scheduleDetailId) {
        this.scheduleDetailId = scheduleDetailId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStreamingServerType(String streamingServerType) {
        this.streamingServerType = streamingServerType;
    }

    public void setDmsServerHost(String dmsServerHost) {
        this.dmsServerHost = dmsServerHost;
    }

    public String getScheduleDetailId() {
        return scheduleDetailId;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getStatus() {
        return status;
    }

    public String getStreamingServerType() {
        return streamingServerType;
    }

    public String getDmsServerHost() {
        return dmsServerHost;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    /**
     * @param object
     * @return
     */
    public static ScheduleLiveView parseJson(JSONObject object) {
        ScheduleLiveView scheduleLiveView = new ScheduleLiveView();
        scheduleLiveView.setScheduleDetailId(object.optString("scheduleDetailId"));
        scheduleLiveView.setClassroomId(object.optString("classroomId"));
        scheduleLiveView.setServerAddress(object.optString("serverAddress"));
        scheduleLiveView.setTeacherName(object.optString("teacherName"));
        scheduleLiveView.setSchoolName(object.optString("schoolName"));
        scheduleLiveView.setRoomName(object.optString("roomName"));
        scheduleLiveView.setStatus(object.optString("status"));
        scheduleLiveView.setStreamUrl(object.optString("streamUrl"));
        scheduleLiveView.setStreamingServerType(object.optString("streamingServerType"));
        scheduleLiveView.setDmsServerHost(object.optString("dmsServerHost"));
        return scheduleLiveView;
    }

    public static List<ScheduleLiveView> parseJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) return null;
        List<ScheduleLiveView> scheduleLiveViews = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            scheduleLiveViews.add(parseJson(jsonObject));
        }
        return scheduleLiveViews;
    }
}
