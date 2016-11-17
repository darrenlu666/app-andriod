package com.codyy.erpsportal.commons.models.entities;

import com.codyy.erpsportal.commons.models.Titles;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 2015/8/10.
 */
public class DeliveryRecommendSchedule extends DeliveryBase {

    /**
     * realEndTime : null
     * teacherName : 国家学校教师1
     * thumb : a9cfab72-8459-4ade-a66c-c0adac95398e.jpg
     * receiveSchool : null
     * realBeginTime : null
     * videos : null
     * classlevelName : 一年级
     * roomName : null
     * duration : 0
     * baseAreaId : null
     * teacherUserId : null
     * areaName : null
     * schoolId : null
     * id : e7ab6f39a4ca433fba4ef6feb968f690
     * schoolName : aa1
     * baseClassWorks : null
     * subjectName : 语文
     * watchCount : 397
     */
    private String realEndTime;
    private String teacherName;
    private String thumb;
    private String receiveSchool;
    private String realBeginTime;
    private String videos;
    private String classlevelName;
    private String roomName;
    private int duration;
    private String baseAreaId;
    private String teacherUserId;
    private String areaName;
    private String schoolId;
    private String id;
    private String schoolName;
    private String baseClassWorks;
    private String subjectName;
    private int watchCount;

    public void setRealEndTime(String realEndTime) {
        this.realEndTime = realEndTime;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setReceiveSchool(String receiveSchool) {
        this.receiveSchool = receiveSchool;
    }

    public void setRealBeginTime(String realBeginTime) {
        this.realBeginTime = realBeginTime;
    }

    public void setVideos(String videos) {
        this.videos = videos;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setBaseAreaId(String baseAreaId) {
        this.baseAreaId = baseAreaId;
    }

    public void setTeacherUserId(String teacherUserId) {
        this.teacherUserId = teacherUserId;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setBaseClassWorks(String baseClassWorks) {
        this.baseClassWorks = baseClassWorks;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    public String getRealEndTime() {
        return realEndTime;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getThumb() {
        return thumb;
    }

    public String getReceiveSchool() {
        return receiveSchool;
    }

    public String getRealBeginTime() {
        return realBeginTime;
    }

    public String getVideos() {
        return videos;
    }

    public String getClasslevelName() {
        return classlevelName;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getDuration() {
        return duration;
    }

    public String getBaseAreaId() {
        return baseAreaId;
    }

    public String getTeacherUserId() {
        return teacherUserId;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public String getId() {
        return id;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getBaseClassWorks() {
        return baseClassWorks;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getWatchCount() {
        return watchCount;
    }

    /**
     * @param object
     * @param deliveryBases
     */
    public static void getRecommendSchedule(JSONObject object, ArrayList<DeliveryBase> deliveryBases) {
        if ("success".equals(object.optString("result"))) {
            if (deliveryBases == null) {
                deliveryBases = new ArrayList<>();
            }
            JSONArray jsonArray = object.optJSONArray("data");
            if (jsonArray.length() > 0) {
                DeliveryBase deliveryBase = new DeliveryBase();
                deliveryBase.setType(DeliveryBase.TITLE_DIVIDE);
                deliveryBase.setTitle(Titles.sPagetitleSpeclassReplay);
                deliveryBase.setSpanSize(2);
                deliveryBases.add(deliveryBase);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    DeliveryRecommendSchedule deliveryRecommendSchedule = new DeliveryRecommendSchedule();
                    if (i == 0) {
                        deliveryRecommendSchedule.setSpanSize(2);
                        deliveryRecommendSchedule.setType(DeliveryBase.RECOMMEND_SCHEDULE_FIRST);
                    } else {
                        deliveryRecommendSchedule.setSpanSize(1);
                        deliveryRecommendSchedule.setType(DeliveryBase.RECOMMEND_SCHEDULE);
                    }
                    deliveryRecommendSchedule.setId(jsonObject.optString("id"));
                    deliveryRecommendSchedule.setClasslevelName(jsonObject.optString("classlevelName"));
                    deliveryRecommendSchedule.setTeacherName(jsonObject.optString("teacherName"));
                    deliveryRecommendSchedule.setSubjectName(jsonObject.optString("subjectName"));
                    deliveryRecommendSchedule.setSchoolName(jsonObject.optString("schoolName"));
                    deliveryRecommendSchedule.setWatchCount(jsonObject.optInt("watchCount"));
                    deliveryRecommendSchedule.setThumb(jsonObject.optString("thumb"));
                    deliveryBases.add(deliveryRecommendSchedule);
                    if (i == 0) {
                        DeliveryBase divide = new DeliveryBase();
                        divide.setType(DeliveryBase.DIVIDE_VIEW);
                        divide.setSpanSize(2);
                        deliveryBases.add(divide);
                    }
                }
                DeliveryBase divide = new DeliveryBase();
                divide.setType(DeliveryBase.DIVIDE_VIEW);
                divide.setSpanSize(2);
                deliveryBases.add(divide);
            }
        }
    }
}
