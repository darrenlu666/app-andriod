package com.codyy.erpsportal.commons.models.entities.customized;

import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;

import java.io.Serializable;

/**
 * 首页-专递课堂-课程回放
 * Created by poe on 16-6-1.
 */
public class HistoryClass extends BaseTitleItemBar implements Serializable {
    private String id;//课程回放id
    private String areaName;
    private String baseAreaId;
    private String classlevelName;
    private int duration;
    private int position;
    private long realBeginTime;
    private long realEndTime;
    private String receiveSchool;
    private String roomName;
    private String schoolId;
    private String schoolName;
    private String serverAddress;
    private String subjectName;
    private String teacherName;
    private String teacherUserId;
    private String thumb;
    private String videos;
    private int watchCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getClasslevelName() {
        return classlevelName;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getRealBeginTime() {
        return realBeginTime;
    }

    public void setRealBeginTime(long realBeginTime) {
        this.realBeginTime = realBeginTime;
    }

    public long getRealEndTime() {
        return realEndTime;
    }

    public void setRealEndTime(long realEndTime) {
        this.realEndTime = realEndTime;
    }

    public String getReceiveSchool() {
        return receiveSchool;
    }

    public void setReceiveSchool(String receiveSchool) {
        this.receiveSchool = receiveSchool;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherUserId() {
        return teacherUserId;
    }

    public void setTeacherUserId(String teacherUserId) {
        this.teacherUserId = teacherUserId;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getVideos() {
        return videos;
    }

    public void setVideos(String videos) {
        this.videos = videos;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }
}
