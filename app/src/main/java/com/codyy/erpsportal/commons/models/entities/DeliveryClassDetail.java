package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.commons.utils.UriUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 2015/9/12.
 */
public class DeliveryClassDetail implements Parcelable {

    /**
     * id : 80fa5a6e6b5140bd9e7fc69897ba429d
     * classlevelName : 一年级
     * subjectName : 数学
     * roomName : 主讲教室1
     * teacherUserId : 904d257a1ba842b49cddeaad54b4273d
     * teacherName : guojiatea1
     * baseAreaId : da5f05d3cf624b318e6fbd859e57a5e2
     * areaName : 中国
     * schoolId : 7bc3aea6f5d4444285ab17527a6cb688
     * schoolName : 国家学校
     * realBeginTime : 2015-08-28 16:02
     * realEndTime : 1440792259410
     * duration : 2
     * watchCount : 39
     * position : 0
     * thumb : j.jpg
     */

    private String id;
    private String classlevelName;
    private String subjectName;
    private String roomName;
    private String teacherUserId;
    private String teacherName;
    private String baseAreaId;
    private String areaName;
    private String schoolId;
    private String schoolName;
    private String realBeginTime;
    private long realEndTime;
    private int duration;
    private int watchCount;
    private int position;
    private String thumb;
    private ArrayList<String> receiveSchool = new ArrayList<>();

    public ArrayList<String> getReceiveSchool() {
        return receiveSchool;
    }

    public void setReceiveSchool(ArrayList<String> receiveSchool) {
        this.receiveSchool = receiveSchool;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setTeacherUserId(String teacherUserId) {
        this.teacherUserId = teacherUserId;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setBaseAreaId(String baseAreaId) {
        this.baseAreaId = baseAreaId;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setRealBeginTime(String realBeginTime) {
        this.realBeginTime = realBeginTime;
    }

    public void setRealEndTime(long realEndTime) {
        this.realEndTime = realEndTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getId() {
        return id;
    }

    public String getClasslevelName() {
        return classlevelName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getTeacherUserId() {
        return teacherUserId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getBaseAreaId() {
        return baseAreaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getRealBeginTime() {
        return realBeginTime;
    }

    public long getRealEndTime() {
        return realEndTime;
    }

    public int getDuration() {
        return duration;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public int getPosition() {
        return position;
    }

    public String getThumb() {
        return thumb;
    }

    public static final DeliveryClassDetail getDelivery(JSONObject object) {
        DeliveryClassDetail deliveryClassDetail = new DeliveryClassDetail();
        deliveryClassDetail.setId(object.optString("id"));
        deliveryClassDetail.setClasslevelName(object.optString("classlevelName"));
        deliveryClassDetail.setSubjectName(object.optString("subjectName"));
        deliveryClassDetail.setRoomName(object.optString("roomName"));
        deliveryClassDetail.setTeacherUserId(object.optString("teacherUserId"));
        deliveryClassDetail.setTeacherName(object.optString("teacherName"));
        deliveryClassDetail.setBaseAreaId(object.optString("baseAreaId"));
        deliveryClassDetail.setAreaName(object.optString("areaName"));
        deliveryClassDetail.setSchoolId(object.optString("schoolId"));
        deliveryClassDetail.setSchoolName(object.optString("schoolName"));
        deliveryClassDetail.setRealBeginTime(object.optString("realBeginTime"));
        deliveryClassDetail.setRealEndTime(object.optInt("realEndTime"));
        deliveryClassDetail.setDuration(object.optInt("duration"));
        deliveryClassDetail.setWatchCount(object.optInt("watchCount"));
        deliveryClassDetail.setPosition(object.optInt("position"));
        deliveryClassDetail.setThumb(UriUtils.getSmall(object.optString("thumb")));
        JSONArray array = object.optJSONArray("receiveSchool");
        if (array != null) {
            ArrayList<String> strings = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object1 = array.optJSONObject(i);
                strings.add(object1.optString("schoolName"));
            }
            deliveryClassDetail.setReceiveSchool(strings);
        }
        return deliveryClassDetail;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.classlevelName);
        dest.writeString(this.subjectName);
        dest.writeString(this.roomName);
        dest.writeString(this.teacherUserId);
        dest.writeString(this.teacherName);
        dest.writeString(this.baseAreaId);
        dest.writeString(this.areaName);
        dest.writeString(this.schoolId);
        dest.writeString(this.schoolName);
        dest.writeString(this.realBeginTime);
        dest.writeLong(this.realEndTime);
        dest.writeInt(this.duration);
        dest.writeInt(this.watchCount);
        dest.writeInt(this.position);
        dest.writeString(this.thumb);
        dest.writeStringList(this.receiveSchool);
    }

    public DeliveryClassDetail() {
    }

    protected DeliveryClassDetail(Parcel in) {
        this.id = in.readString();
        this.classlevelName = in.readString();
        this.subjectName = in.readString();
        this.roomName = in.readString();
        this.teacherUserId = in.readString();
        this.teacherName = in.readString();
        this.baseAreaId = in.readString();
        this.areaName = in.readString();
        this.schoolId = in.readString();
        this.schoolName = in.readString();
        this.realBeginTime = in.readString();
        this.realEndTime = in.readLong();
        this.duration = in.readInt();
        this.watchCount = in.readInt();
        this.position = in.readInt();
        this.thumb = in.readString();
        this.receiveSchool = in.createStringArrayList();
    }

    public static final Parcelable.Creator<DeliveryClassDetail> CREATOR = new Parcelable.Creator<DeliveryClassDetail>() {
        public DeliveryClassDetail createFromParcel(Parcel source) {
            return new DeliveryClassDetail(source);
        }

        public DeliveryClassDetail[] newArray(int size) {
            return new DeliveryClassDetail[size];
        }
    };
}
