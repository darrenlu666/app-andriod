package com.codyy.erpsportal.commons.models.entities;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by yangxinwu on 2015/8/17.
 */
public class MeetDetail {


    /**
     * id : 09e581f2d4f4440589fef5a1fcfc792b
     * title : 国家发起的评课议课
     * mainTeacher : 021e6547050246e7bb0f6abe4ad77a91
     * startTime : 2015-09-15
     * totalScore : 10
     * averageScore : 0
     * viewCount : 11
     * subjectName : 数学
     * subjectPic : null
     * classLevelName : 一年级
     * scoreType : STAR
     */

    private String id;
    private String title;
    @SerializedName(value = "masterTeacherName", alternate = {"sponsorName"})
    private String mainTeacher;
    @SerializedName(value = "beginDate")
    private String startTime;
    private String totalScore;
    @SerializedName(value = "averageScore")
    private String averageScore;
    private String description;
    private int viewCount;//viewCount
    private String subjectName;
    private String subjectPic;
    private String classLevelName;
    private String scoreType;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMainTeacher(String mainTeacher) {
        this.mainTeacher = mainTeacher;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public void setAverageScore(String averageScore) {
        this.averageScore = averageScore;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setSubjectPic(String subjectPic) {
        this.subjectPic = subjectPic;
    }

    public void setClassLevelName(String classLevelName) {
        this.classLevelName = classLevelName;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMainTeacher() {
        return mainTeacher;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public String getAverageScore() {
        return averageScore;
    }

    public int getViewCount() {
        return viewCount;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectPic() {
        return subjectPic;
    }

    public String getClassLevelName() {
        return classLevelName;
    }

    public String getScoreType() {
        return scoreType;
    }

    public static MeetDetail parseJson(JSONObject jsonObject) {
        MeetDetail meetDetail = new MeetDetail();
        meetDetail.setId(jsonObject.optString("id"));
        meetDetail.setTitle(jsonObject.optString("title"));
        meetDetail.setMainTeacher(jsonObject.optString("mainTeacher"));
        meetDetail.setStartTime(jsonObject.optString("startTime"));
        meetDetail.setTotalScore(jsonObject.optString("totalScore"));
        meetDetail.setAverageScore(jsonObject.optString("averageScore").endsWith(".0") ? jsonObject.optString("averageScore").replace(".0", "") : jsonObject.optString("averageScore"));
        meetDetail.setViewCount(jsonObject.optInt("viewCount"));
        meetDetail.setSubjectName(jsonObject.optString("subjectName"));
        meetDetail.setSubjectPic(jsonObject.optString("subjectPic"));
        meetDetail.setClassLevelName(jsonObject.optString("classLevelName"));
        meetDetail.setScoreType(jsonObject.optString("scoreType"));
        return meetDetail;

    }
}
