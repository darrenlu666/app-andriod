package com.codyy.erpsportal.commons.models.entities.mainpage;

import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONObject;

/**
 * 首页（资源） 直播课堂 实体项
 * Created by gujiajia on 2016/8/4.
 */
public class MainResClassroom {

    /**
     * 专递课堂
     */
    public final static String TYPE_ONLINE_CLASS = "schedule";

    /**
     * 名校网络课堂
     */
    public final static String TYPE_LIVE = "liveAppointment ";

    private String id;
    private String type;
    private String schoolName;
    private String gradeName;
    private String subjectName;
    private String teacherName;
    private String subjectIcon;
    private long realBeginTime;
    private int classSeq;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getSubjectIcon() {
        return subjectIcon;
    }

    public void setSubjectIcon(String subjectIcon) {
        this.subjectIcon = subjectIcon;
    }

    public long getRealBeginTime() {
        return realBeginTime;
    }

    public void setRealBeginTime(long realBeginTime) {
        this.realBeginTime = realBeginTime;
    }

    public int getClassSeq() {
        return classSeq;
    }

    public void setClassSeq(int classSeq) {
        this.classSeq = classSeq;
    }

    public final static JsonParser<MainResClassroom> PARSER = new JsonParser<MainResClassroom>() {
        @Override
        public MainResClassroom parse(JSONObject jsonObject) {
            MainResClassroom mainResClassroom = new MainResClassroom();
            mainResClassroom.setId(jsonObject.optString("scheduleDetailId"));
            mainResClassroom.setType(jsonObject.optString("liveType"));
            mainResClassroom.setSchoolName(jsonObject.optString("schoolName"));
            mainResClassroom.setGradeName(jsonObject.optString("classlevelName"));
            mainResClassroom.setSubjectName(jsonObject.optString("subjectName"));
            mainResClassroom.setTeacherName(jsonObject.optString("teacherName"));
            mainResClassroom.setSubjectIcon(optStrOrNull(jsonObject,"subjectPic"));
            mainResClassroom.setRealBeginTime(jsonObject.optLong("realBeginTime"));
            mainResClassroom.setClassSeq(jsonObject.optInt("classSeq"));
            return mainResClassroom;
        }
    };
}
