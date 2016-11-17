package com.codyy.erpsportal.commons.models.entities;


import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangxinwu on 2015/6/30.
 */
public class ClassRoomItem {
    private String classRoom;
    private String setsuji;
    private String subject;
    private String grade;
    private String teacher;
    private String subjectPic;
    /**
     * 会议id，即mid
     */
    private String meetingId;

    private long startTime;

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

    private String status;

    public String getSubjectPic() {
        return subjectPic;
    }

    public void setSubjectPic(String subjectPic) {
        this.subjectPic = subjectPic;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSetsuji() {
        return setsuji;
    }

    public void setSetsuji(String setsuji) {
        this.setsuji = setsuji;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    //解析json
    public static ClassRoomItem parse(JSONObject jsonObject) {
        ClassRoomItem classRoomItem = new ClassRoomItem();
        classRoomItem.setGrade(StringUtils.replaceHtml(jsonObject.optString("classlevelName")).toString());
//        classRoomItem.setTeacher(StringUtils.replaceHtml(jsonObject.optString("directorName")).toString());
        classRoomItem.setTeacher(StringUtils.replaceHtml(jsonObject.optString("realName")).toString());
        classRoomItem.setSubject(StringUtils.replaceHtml(jsonObject.optString("subjectName")).toString());
        classRoomItem.setClassRoom(StringUtils.replaceHtml(jsonObject.optString("roomName")).toString());
        classRoomItem.setSubjectPic(jsonObject.optString("subjectPic"));
        classRoomItem.setSetsuji(jsonObject.optString("classSeq"));
        classRoomItem.setMeetingId(jsonObject.optString("scheduleDetailId"));
        classRoomItem.setStatus(jsonObject.optString("status"));
        classRoomItem.setStartTime(jsonObject.optLong("startTime"));
        return classRoomItem;
    }

    public static List<ClassRoomItem> parseList(JSONObject jsonObject) {
        List<ClassRoomItem> mList = new ArrayList<>();
        if ("success".equals(jsonObject.optString("result"))) {
            JSONArray mJsonArray = jsonObject.optJSONArray("list");
            if (null != mJsonArray && mJsonArray.length() > 0) {
                Cog.i("Json", "mJsonArray length ==>>" + mJsonArray.length() + "::" + mJsonArray);
                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject object = mJsonArray.optJSONObject(i);
                    mList.add(parse(object));
                }
            }
        }
        return mList;
    }


}
