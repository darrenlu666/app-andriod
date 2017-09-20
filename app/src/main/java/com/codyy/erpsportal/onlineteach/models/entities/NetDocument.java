package com.codyy.erpsportal.onlineteach.models.entities;


import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

/**
 * 网络授课－相关文档
 * Created by poe on 16-8-15.
 */
public class NetDocument extends BaseTitleItemBar {
    private String clsClassroomId;
    private String guestName;
    private String belongToType;
    private String createTime;
    private String documentName;
    private String documentPath;
    private String guestFlag;
    private String locked;
    private String meetDocumentId;
    private String meetingId;
    private String pageNum;
    private String serverAddress;
    private String serverResourceId;
    private String uploadUserId;

    public String getClsClassroomId() {
        return clsClassroomId;
    }

    public void setClsClassroomId(String clsClassroomId) {
        this.clsClassroomId = clsClassroomId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getBelongToType() {
        return belongToType;
    }

    public void setBelongToType(String belongToType) {
        this.belongToType = belongToType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getGuestFlag() {
        return guestFlag;
    }

    public void setGuestFlag(String guestFlag) {
        this.guestFlag = guestFlag;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getMeetDocumentId() {
        return meetDocumentId;
    }

    public void setMeetDocumentId(String meetDocumentId) {
        this.meetDocumentId = meetDocumentId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getPageNum() {
        return pageNum;
    }

    public void setPageNum(String pageNum) {
        this.pageNum = pageNum;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerResourceId() {
        return serverResourceId;
    }

    public void setServerResourceId(String serverResourceId) {
        this.serverResourceId = serverResourceId;
    }

    public String getUploadUserId() {
        return uploadUserId;
    }

    public void setUploadUserId(String uploadUserId) {
        this.uploadUserId = uploadUserId;
    }
}
