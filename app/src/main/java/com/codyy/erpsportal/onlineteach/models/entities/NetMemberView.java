package com.codyy.erpsportal.onlineteach.models.entities;

/**
 * 网络授课－详情－主讲学校－接受端　.
 * Created by poe on 16-8-15.
 */
public class NetMemberView {
    private String baseUserId;
    private String enterTime;
    private String guestName;
    private String leaveTime;
    private String meetReceiveMemberId;
    private String meetReceivePlaceId;
    private String meetingId;
    private String memberType;
    private String realName;

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(String enterTime) {
        this.enterTime = enterTime;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }

    public String getMeetReceiveMemberId() {
        return meetReceiveMemberId;
    }

    public void setMeetReceiveMemberId(String meetReceiveMemberId) {
        this.meetReceiveMemberId = meetReceiveMemberId;
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

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
