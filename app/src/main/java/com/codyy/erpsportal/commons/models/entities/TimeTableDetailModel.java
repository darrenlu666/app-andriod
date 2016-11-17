package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 2015/4/13.
 */
public class TimeTableDetailModel {
    /**
     * 教室类型
     */
    private String roomType;
    /**
     * 教室名字
     */
    private String roomName;
    /**
     * 总有多少周
     */
    private int totalWeek;
    /**
     * 第几周
     */
    private int weekSeq;
    /**
     * 课表列表
     */
    private List<TimeTableContent> scheduleDetails;

    /**
     * 是否有下一周
     */
    private String hasNext;
    /**
     * 是否有上一周
     */
    private String hasPre;

    private int receiveIndex;

    public int getReceiveIndex() {
        return receiveIndex;
    }

    public void setReceiveIndex(int receiveIndex) {
        this.receiveIndex = receiveIndex;
    }

    public String getHasNext() {
        return hasNext;
    }

    public void setHasNext(String hasNext) {
        this.hasNext = hasNext;
    }

    public String getHasPre() {
        return hasPre;
    }

    public void setHasPre(String hasPre) {
        this.hasPre = hasPre;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getTotalWeek() {
        return totalWeek;
    }

    public void setTotalWeek(int totalWeek) {
        this.totalWeek = totalWeek;
    }

    public int getWeekSeq() {
        return weekSeq;
    }

    public void setWeekSeq(int weekSeq) {
        this.weekSeq = weekSeq;
    }

    public List<TimeTableContent> getScheduleDetails() {
        return scheduleDetails;
    }

    public void setScheduleDetails(List<TimeTableContent> scheduleDetails) {
        this.scheduleDetails = scheduleDetails;
    }

    /**
     * 获取课表详情
     *
     * @param jsonObject
     */
    public static TimeTableDetailModel getTimeTableDetails(JSONObject jsonObject) {
        if ("success".equals(jsonObject.optString("result"))) {
            TimeTableDetailModel timeTableDetailModel = new TimeTableDetailModel();
            timeTableDetailModel.setRoomType(jsonObject.optString("roomType"));
            timeTableDetailModel.setRoomName(jsonObject.optString("roomName"));
            timeTableDetailModel.setTotalWeek(jsonObject.optInt("totalWeek"));
            timeTableDetailModel.setHasNext(jsonObject.optString("hasNext"));
            timeTableDetailModel.setHasPre(jsonObject.optString("hasPre"));
            timeTableDetailModel.setReceiveIndex(jsonObject.optInt("receiveIndex"));
            JSONArray jsonArray = jsonObject.optJSONArray("scheduleDetails");
            if (jsonArray != null) {
                List<TimeTableContent> contents = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    TimeTableContent timeTableContent = new TimeTableContent();
                    timeTableContent.setScheduleDetailId(jsonObject1.optString("scheduleDetailId"));
                    timeTableContent.setDaySeq(jsonObject1.optInt("daySeq"));
                    timeTableContent.setClassSeq(jsonObject1.optInt("classSeq"));
                    timeTableContent.setSubjectName(jsonObject1.optString("subjectName"));
                    contents.add(timeTableContent);
                }
                timeTableDetailModel.setScheduleDetails(contents);
            }
            timeTableDetailModel.setWeekSeq(jsonObject.optInt("weekSeq"));
            return timeTableDetailModel;
        }
        return null;
    }
}
