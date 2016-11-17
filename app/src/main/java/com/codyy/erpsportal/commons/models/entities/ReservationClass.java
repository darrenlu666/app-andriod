package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 2015/8/19.
 */
public class ReservationClass implements Parcelable{

    /**
     * duration : 0
     * subjectCoverNum : 42
     * liveAppointmentId : null
     * clsSchoolId : 3cb3d57d972f4711806ad4a6af354be4
     * areaName : 苏州
     * appointmentNum : 46
     * classlevelCoverNum : 42
     * classroomName : 主讲教室
     * skey : 40531809
     * areaTitle : 中国-江苏-苏州
     * schoolName : 背风坡小学
     * clsClassroomId : 60d274a8a9e7497dbb47ef272c8c94b5
     */
    private int subjectCoverNum;
    private String clsSchoolId;
    private String areaName;
    private int appointmentNum;
    private int classlevelCoverNum;
    private String classroomName;
    private String skey;
    private String areaTitle;
    private String schoolName;
    private String clsClassroomId;


    public void setSubjectCoverNum(int subjectCoverNum) {
        this.subjectCoverNum = subjectCoverNum;
    }


    public void setClsSchoolId(String clsSchoolId) {
        this.clsSchoolId = clsSchoolId;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setAppointmentNum(int appointmentNum) {
        this.appointmentNum = appointmentNum;
    }

    public void setClasslevelCoverNum(int classlevelCoverNum) {
        this.classlevelCoverNum = classlevelCoverNum;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public void setAreaTitle(String areaTitle) {
        this.areaTitle = areaTitle;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setClsClassroomId(String clsClassroomId) {
        this.clsClassroomId = clsClassroomId;
    }

    public int getSubjectCoverNum() {
        return subjectCoverNum;
    }

    public String getClsSchoolId() {
        return clsSchoolId;
    }

    public String getAreaName() {
        return areaName;
    }

    public int getAppointmentNum() {
        return appointmentNum;
    }

    public int getClasslevelCoverNum() {
        return classlevelCoverNum;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public String getSkey() {
        return skey;
    }

    public String getAreaTitle() {
        return areaTitle;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getClsClassroomId() {
        return clsClassroomId;
    }

    /**
     * @param object
     * @param reservationClasses
     */
    public static void getReservationList(JSONObject object, ArrayList<ReservationClass> reservationClasses) {
        if ("success".endsWith(object.optString("result"))) {
            JSONArray jsonArray = object.optJSONArray("classroomlist");
            for (int i = 0; i < jsonArray.length(); i++) {
                ReservationClass reservationClass = new ReservationClass();
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                reservationClass.setClsSchoolId(jsonObject.optString("clsSchoolId"));
                reservationClass.setClsClassroomId(jsonObject.optString("clsClassroomId"));
                reservationClass.setSchoolName(jsonObject.optString("schoolName"));
                reservationClass.setClassroomName(jsonObject.optString("classroomName"));
                reservationClass.setAreaName(jsonObject.optString("areaName"));
                reservationClass.setClasslevelCoverNum(jsonObject.optInt("classlevelCoverNum"));
                reservationClass.setSubjectCoverNum(jsonObject.optInt("subjectCoverNum"));
                reservationClass.setAppointmentNum(jsonObject.optInt("appointmentNum"));
                reservationClass.setSkey(jsonObject.optString("skey"));
                reservationClass.setAreaTitle(jsonObject.optString("areaTitle"));
                reservationClasses.add(reservationClass);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.subjectCoverNum);
        dest.writeString(this.clsSchoolId);
        dest.writeString(this.areaName);
        dest.writeInt(this.appointmentNum);
        dest.writeInt(this.classlevelCoverNum);
        dest.writeString(this.classroomName);
        dest.writeString(this.skey);
        dest.writeString(this.areaTitle);
        dest.writeString(this.schoolName);
        dest.writeString(this.clsClassroomId);
    }

    public ReservationClass() {
    }

    protected ReservationClass(Parcel in) {
        this.subjectCoverNum = in.readInt();
        this.clsSchoolId = in.readString();
        this.areaName = in.readString();
        this.appointmentNum = in.readInt();
        this.classlevelCoverNum = in.readInt();
        this.classroomName = in.readString();
        this.skey = in.readString();
        this.areaTitle = in.readString();
        this.schoolName = in.readString();
        this.clsClassroomId = in.readString();
    }

    public static final Creator<ReservationClass> CREATOR = new Creator<ReservationClass>() {
        public ReservationClass createFromParcel(Parcel source) {
            return new ReservationClass(source);
        }

        public ReservationClass[] newArray(int size) {
            return new ReservationClass[size];
        }
    };
}
