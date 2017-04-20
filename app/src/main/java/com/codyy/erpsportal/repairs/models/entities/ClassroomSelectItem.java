package com.codyy.erpsportal.repairs.models.entities;

import android.os.Parcel;

/**
 * 供选择的教室列表
 * Created by gujiajia on 2017/4/14.
 */

public class ClassroomSelectItem implements android.os.Parcelable {

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj instanceof ClassroomSelectItem) {
            ClassroomSelectItem item = (ClassroomSelectItem) obj;
            if (clsClassroomId == null && item.clsClassroomId == null) {
                return true;
            }
            if (clsClassroomId != null && clsClassroomId.equals(item.clsClassroomId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.clsClassroomId);
        dest.writeString(this.roomName);
        dest.writeString(this.roomNo);
        dest.writeString(this.roomType);
        dest.writeString(this.skey);
    }

    public ClassroomSelectItem() {
    }

    protected ClassroomSelectItem(Parcel in) {
        this.clsClassroomId = in.readString();
        this.roomName = in.readString();
        this.roomNo = in.readString();
        this.roomType = in.readString();
        this.skey = in.readString();
    }

    public static final Creator<ClassroomSelectItem> CREATOR = new Creator<ClassroomSelectItem>() {
        @Override
        public ClassroomSelectItem createFromParcel(Parcel source) {
            return new ClassroomSelectItem(source);
        }

        @Override
        public ClassroomSelectItem[] newArray(int size) {
            return new ClassroomSelectItem[size];
        }
    };
}
