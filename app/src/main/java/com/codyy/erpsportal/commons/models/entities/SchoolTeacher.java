package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kmdai on 2015/4/20.
 */
public class SchoolTeacher extends BaseTitleItemBar implements Parcelable {
    private String schoolName;
    private String invitedStatus;
    private String isSelf;
    private String teachers;

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getInvitedStatus() {
        return invitedStatus;
    }

    public void setInvitedStatus(String invitedStatus) {
        this.invitedStatus = invitedStatus;
    }

    public String getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(String isSelf) {
        this.isSelf = isSelf;
    }

    public String getTeachers() {
        return teachers;
    }

    public void setTeachers(String teachers) {
        this.teachers = teachers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.schoolName);
        dest.writeString(this.invitedStatus);
        dest.writeString(this.isSelf);
        dest.writeString(this.teachers);
    }

    public SchoolTeacher() {
    }

    private SchoolTeacher(Parcel in) {
        this.schoolName = in.readString();
        this.invitedStatus = in.readString();
        this.isSelf = in.readString();
        this.teachers = in.readString();
    }

    public static final Creator<SchoolTeacher> CREATOR = new Creator<SchoolTeacher>() {
        public SchoolTeacher createFromParcel(Parcel source) {
            return new SchoolTeacher(source);
        }

        public SchoolTeacher[] newArray(int size) {
            return new SchoolTeacher[size];
        }
    };
}
