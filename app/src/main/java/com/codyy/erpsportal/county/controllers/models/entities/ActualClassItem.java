package com.codyy.erpsportal.county.controllers.models.entities;

import android.os.Parcel;

import com.codyy.erpsportal.commons.models.entities.RefreshEntity;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 16-10-21.
 */

public class ActualClassItem extends RefreshEntity {


    private String cancelReason;
    private String cancelType;
    private String classStatus;
    private int classSeq;
    private String classlevelName;
    private int daySeq;
    private String realName;
    private String roomName;
    private String subjectName;
    private int weekSeq;

    public String getCancelReason() {
        return cancelReason;
    }

    public int getWeekSeq() {
        return weekSeq;
    }

    public void setWeekSeq(int weekSeq) {
        this.weekSeq = weekSeq;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getCancelType() {
        return cancelType;
    }

    public void setCancelType(String cancelType) {
        this.cancelType = cancelType;
    }

    public int getClassSeq() {
        return classSeq;
    }

    public void setClassSeq(int classSeq) {
        this.classSeq = classSeq;
    }

    public String getClassStatus() {
        return classStatus;
    }

    public void setClassStatus(String classStatus) {
        this.classStatus = classStatus;
    }

    public String getClasslevelName() {
        return classlevelName;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public int getDaySeq() {
        return daySeq;
    }

    public void setDaySeq(int daySeq) {
        this.daySeq = daySeq;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public ActualClassItem() {
    }

    public static List<ActualClassItem> getActualClassItemList(JSONArray array) {
        if (array != null) {
            ArrayList<ActualClassItem> actualClassItems = new ArrayList<>(array.length());
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                ActualClassItem actualClassItem = gson.fromJson(array.optString(i), ActualClassItem.class);
                actualClassItem.setmHolderType(REFRESH_TYPE_LASTVIEW + 1);
                actualClassItems.add(actualClassItem);
            }
            return actualClassItems;
        }
        return new ArrayList<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.cancelReason);
        dest.writeString(this.cancelType);
        dest.writeString(this.classStatus);
        dest.writeInt(this.classSeq);
        dest.writeString(this.classlevelName);
        dest.writeInt(this.daySeq);
        dest.writeString(this.realName);
        dest.writeString(this.roomName);
        dest.writeString(this.subjectName);
        dest.writeInt(this.weekSeq);
    }

    protected ActualClassItem(Parcel in) {
        super(in);
        this.cancelReason = in.readString();
        this.cancelType = in.readString();
        this.classStatus = in.readString();
        this.classSeq = in.readInt();
        this.classlevelName = in.readString();
        this.daySeq = in.readInt();
        this.realName = in.readString();
        this.roomName = in.readString();
        this.subjectName = in.readString();
        this.weekSeq = in.readInt();
    }

    public static final Creator<ActualClassItem> CREATOR = new Creator<ActualClassItem>() {
        @Override
        public ActualClassItem createFromParcel(Parcel source) {
            return new ActualClassItem(source);
        }

        @Override
        public ActualClassItem[] newArray(int size) {
            return new ActualClassItem[size];
        }
    };
}
