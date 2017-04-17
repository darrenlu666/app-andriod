package com.codyy.erpsportal.repairs.models.entities;

/**
 * 供选择的教室列表
 * Created by gujiajia on 2017/4/14.
 */

public class ClassroomSelectItem {

    /**
     * clsClassroomId : 35aba59ef1554e609879a3d71ff2233c
     * roomName : 主讲教室1
     * roomNo : 1234
     * roomType : MASTER
     * skey : 25640616
     */

    private String clsClassroomId;
    private String roomName;
    private String roomNo;
    private String roomType;
    private String skey;

    public String getClsClassroomId() {
        return clsClassroomId;
    }

    public void setClsClassroomId(String clsClassroomId) {
        this.clsClassroomId = clsClassroomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }
}
