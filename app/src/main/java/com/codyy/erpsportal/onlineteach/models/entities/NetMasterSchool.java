package com.codyy.erpsportal.onlineteach.models.entities;

import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;

import java.util.List;

/**
 * 网络授课－主讲学校
 * Created by poe on 16-8-15.
 */
public class NetMasterSchool extends BaseTitleItemBar {
    private String areaIdPath;
    private String areaPath;
    private String baseAreaId;
    private String clsClassroomId;
    private String clsSchoolId;
    private String joinMemberIds;
    private String joinMemberNames;
    private String mainMemberId;
    private String mainMemberName;
    private String meetReceivePlaceId;
    private String meetingId;
    private String placeType;
    private String receiveMemberId;
    private String receiveMemberIds;
    private String receiveMemberName;
    private String receiveMemberNames;
    private String roomName;
    private String schoolName;
    private boolean isSelfSchool;//是否是本校
    private List<NetMemberView> receiveMemberViewList;

    public boolean isSelfSchool() {
        return isSelfSchool;
    }

    public void setSelfSchool(boolean selfSchool) {
        isSelfSchool = selfSchool;
    }

    public String getAreaIdPath() {
        return areaIdPath;
    }

    public void setAreaIdPath(String areaIdPath) {
        this.areaIdPath = areaIdPath;
    }

    public String getAreaPath() {
        return areaPath;
    }

    public void setAreaPath(String areaPath) {
        this.areaPath = areaPath;
    }

    public String getBaseAreaId() {
        return baseAreaId;
    }

    public void setBaseAreaId(String baseAreaId) {
        this.baseAreaId = baseAreaId;
    }

    public String getClsClassroomId() {
        return clsClassroomId;
    }

    public void setClsClassroomId(String clsClassroomId) {
        this.clsClassroomId = clsClassroomId;
    }

    public String getClsSchoolId() {
        return clsSchoolId;
    }

    public void setClsSchoolId(String clsSchoolId) {
        this.clsSchoolId = clsSchoolId;
    }

    public String getJoinMemberIds() {
        return joinMemberIds;
    }

    public void setJoinMemberIds(String joinMemberIds) {
        this.joinMemberIds = joinMemberIds;
    }

    public String getJoinMemberNames() {
        return joinMemberNames;
    }

    public void setJoinMemberNames(String joinMemberNames) {
        this.joinMemberNames = joinMemberNames;
    }

    public String getMainMemberId() {
        return mainMemberId;
    }

    public void setMainMemberId(String mainMemberId) {
        this.mainMemberId = mainMemberId;
    }

    public String getMainMemberName() {
        return mainMemberName;
    }

    public void setMainMemberName(String mainMemberName) {
        this.mainMemberName = mainMemberName;
    }

    public String getMeetReceivePlaceId() {
        return meetReceivePlaceId;
    }

    public void setMeetReceivePlaceId(String meetReceivePlaceId) {
        this.meetReceivePlaceId = meetReceivePlaceId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public String getReceiveMemberId() {
        return receiveMemberId;
    }

    public void setReceiveMemberId(String receiveMemberId) {
        this.receiveMemberId = receiveMemberId;
    }

    public String getReceiveMemberIds() {
        return receiveMemberIds;
    }

    public void setReceiveMemberIds(String receiveMemberIds) {
        this.receiveMemberIds = receiveMemberIds;
    }

    public String getReceiveMemberName() {
        return receiveMemberName;
    }

    public void setReceiveMemberName(String receiveMemberName) {
        this.receiveMemberName = receiveMemberName;
    }

    public String getReceiveMemberNames() {
        return receiveMemberNames;
    }

    public void setReceiveMemberNames(String receiveMemberNames) {
        this.receiveMemberNames = receiveMemberNames;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public List<NetMemberView> getReceiveMemberViewList() {
        return receiveMemberViewList;
    }

    public void setReceiveMemberViewList(List<NetMemberView> receiveMemberViewList) {
        this.receiveMemberViewList = receiveMemberViewList;
    }
}
