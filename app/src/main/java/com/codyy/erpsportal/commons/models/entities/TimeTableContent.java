package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 2015/4/8.
 */
public class TimeTableContent {
    /**
     * 天
     */
    private int daySeq;
    /**
     * 第几节课
     */
    private int classSeq;
    /**
     * 课名
     */
    private String subjectName;
    private String scheduleDetailId;
    private boolean isProgress;

    public int getDaySeq() {
        return daySeq;
    }

    public void setDaySeq(int daySeq) {
        this.daySeq = daySeq;
    }

    public int getClassSeq() {
        return classSeq;
    }

    public void setClassSeq(int classSeq) {
        this.classSeq = classSeq;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getScheduleDetailId() {
        return scheduleDetailId;
    }

    public void setScheduleDetailId(String scheduleDetailId) {
        this.scheduleDetailId = scheduleDetailId;
    }

    public boolean isProgress() {
        return isProgress;
    }

    public void setProgress(boolean progress) {
        isProgress = progress;
    }

    /**
     *
     */
    public static void getTimeTable(JSONObject object, ArrayList<TimeTableContent> contents) {
        if ("success".equals(object.optString("result"))) {
            JSONArray jsonArray = object.optJSONArray("scheduleDetails");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                TimeTableContent timeTableContent = new TimeTableContent();
                timeTableContent.setScheduleDetailId(jsonObject1.optString("scheduleDetailId"));
                timeTableContent.setDaySeq(jsonObject1.optInt("daySeq"));
                timeTableContent.setClassSeq(jsonObject1.optInt("classSeq"));
                timeTableContent.setSubjectName(jsonObject1.optString("subjectName"));
                contents.add(timeTableContent);
            }
        }
    }
}
