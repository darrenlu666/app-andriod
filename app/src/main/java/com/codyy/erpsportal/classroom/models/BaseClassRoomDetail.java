package com.codyy.erpsportal.classroom.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 专递课堂 实时直播 往期录播详情实体类基类
 * Created by ldh on 2016/7/6.
 */
public class BaseClassRoomDetail implements Parcelable {
    public String errorCode;
    public String result;
    public String area;
    public String classPeriod;
    public String classTime;
    public String grade;
    public String schoolName;
    public String teacher;
    public String subject;
    public List<ReceiveInfoEntity> receiveInfoList;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.subject);
        parcel.writeString(this.teacher);
        parcel.writeString(this.schoolName);
        parcel.writeString(this.grade);
        parcel.writeString(this.classTime);
        parcel.writeString(this.classPeriod);
        parcel.writeString(this.area);
        parcel.writeString(this.result);
        parcel.writeString(this.errorCode);
        parcel.writeList(this.receiveInfoList);
    }

    public static final Parcelable.Creator<BaseClassRoomDetail> CREATOR = new Parcelable.Creator<BaseClassRoomDetail>() {
        public BaseClassRoomDetail createFromParcel(Parcel source) {
            return new BaseClassRoomDetail(source);
        }

        public BaseClassRoomDetail[] newArray(int size) {
            return new BaseClassRoomDetail[size];
        }
    };

    public BaseClassRoomDetail(Parcel in) {
        this.subject = in.readString();
        this.teacher = in.readString();
        this.schoolName = in.readString();
        this.grade = in.readString();
        this.classTime = in.readString();
        this.classPeriod = in.readString();
        this.area = in.readString();
        this.result = in.readString();
        this.errorCode = in.readString();
    }

    public BaseClassRoomDetail() {

    }

    public static class ReceiveInfoEntity {
        private String receiveName;
        private String receiveUrl;
        private String stream;
        private String roomName;//新增教室名字
        private boolean showClassRoomName;//是否有多间教室.

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public boolean isShowClassRoomName() {
            return showClassRoomName;
        }

        public void setShowClassRoomName(boolean showClassRoomName) {
            this.showClassRoomName = showClassRoomName;
        }

        public String getStream() {
            // 降低分辨率 720p ．　
            return stream;
        }

        public void setStream(String stream) {
            this.stream = stream;
        }

        public String getReceiveName() {
            return receiveName;
        }

        public void setReceiveName(String receiveName) {
            this.receiveName = receiveName;
        }

        public String getReceiveUrl() {
            return receiveUrl;
        }

        public void setReceiveUrl(String receiveUrl) {
            this.receiveUrl = receiveUrl;
        }
    }

    public List<ReceiveInfoEntity> getReceiveInfoList() {
        return receiveInfoList;
    }

    public void setReceiveInfoList(List<ReceiveInfoEntity> receiveInfoList) {
        this.receiveInfoList = receiveInfoList;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getClassPeriod() {
        return classPeriod;
    }

    public void setClassPeriod(String classPeriod) {
        this.classPeriod = classPeriod;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

}
