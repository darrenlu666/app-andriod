package com.codyy.erpsportal.onlineteach.models.entities;

import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;
import com.google.gson.annotations.SerializedName;

/**
 * 网络授课
 * Created by poe on 16-6-21.
 */
public class NetTeach extends BaseTitleItemBar {
    private String average;//	评分（平均分）	number
    @SerializedName("meetingId")
    private String lessonId;// 课程id	string
    private String score ;//	总分	number
    @SerializedName("beginTime")
    private long startTime;//	开始时间	string
    private String status;//	状态	string   INIT：未开时/PROGRESS：进行中 /END：已结束
    @SerializedName("baseSubjectName")
    private String subject;//	学科名称	string
    @SerializedName("mainSpeakerRealName")
    private String teacher;//	教师名称	string
    @SerializedName("meetingTitle")
    private String title;//	课程标题	string

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
