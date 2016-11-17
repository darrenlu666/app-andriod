package com.codyy.erpsportal.exam.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eachann on 2016/1/27.
 */
public class ExamClass implements Parcelable {
    private String classRoomId;
    private String classRoomName;

    public String getClassRoomId() {
        return classRoomId;
    }

    public void setClassRoomId(String classRoomId) {
        this.classRoomId = classRoomId;
    }

    public String getClassRoomName() {
        return classRoomName;
    }

    public void setClassRoomName(String classRoomName) {
        this.classRoomName = classRoomName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.classRoomId);
        dest.writeString(this.classRoomName);
    }

    public ExamClass() {
    }

    protected ExamClass(Parcel in) {
        this.classRoomId = in.readString();
        this.classRoomName = in.readString();
    }

    public static final Parcelable.Creator<ExamClass> CREATOR = new Parcelable.Creator<ExamClass>() {
        public ExamClass createFromParcel(Parcel source) {
            return new ExamClass(source);
        }

        public ExamClass[] newArray(int size) {
            return new ExamClass[size];
        }
    };
}
