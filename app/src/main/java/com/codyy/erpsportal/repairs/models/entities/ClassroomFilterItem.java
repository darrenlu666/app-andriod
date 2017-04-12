package com.codyy.erpsportal.repairs.models.entities;

/**
 * 报修教室筛选项
 * Created by gujiajia on 2017/3/22.
 */

public class ClassroomFilterItem implements RepairFilterItem {

    /**
     * 教室id
     */
    private String clsClassroomId;

    /**
     * 教室编号
     */
    private String skey;

    /**
     * 教室名称
     */
    private String roomName;

    public ClassroomFilterItem(String clsClassroomId, String skey, String roomName) {
        this.clsClassroomId = clsClassroomId;
        this.skey = skey;
        this.roomName = roomName;
    }

    public String getClsClassroomId() {
        return clsClassroomId;
    }

    public void setClsClassroomId(String clsClassroomId) {
        this.clsClassroomId = clsClassroomId;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @Override
    public String content() {
        if (clsClassroomId == null) {
            return "全部";
        } else {
            return skey + "（" + roomName + "）";
        }
    }
}
