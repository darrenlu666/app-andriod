package com.codyy.erpsportal.classroom.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 非区域用户的往期录播列表实体类
 * Created by ldh on 2016/7/5.
 */
public class NoAreaRecordedDetail {

    /**
     * errorCode : 测试内容5i4m
     * list : [{"classRoomId":"测试内容4x6y","grade":"测试内容66tn","mainSchoolName":"测试内容9pe9","mainTeacher":"测试内容pa71","playCount":"测试内容cs31","startTime":"测试内容4l72","subject":"测试内容i5m6","thumbnailUrl":"测试内容ms7c"}]
     * result : 测试内容y14m
     * total : 85182
     */

    private String errorCode;
    private String result;
    private int total;
    /**
     * classRoomId : 测试内容4x6y
     * grade : 测试内容66tn
     * mainSchoolName : 测试内容9pe9
     * mainTeacher : 测试内容pa71
     * playCount : 测试内容cs31
     * startTime : 测试内容4l72
     * subject : 测试内容i5m6
     * thumbnailUrl : 测试内容ms7c
     */

    private List<ListEntity> list;

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ListEntity> getList() {
        return list;
    }

    public void setList(List<ListEntity> list) {
        this.list = list;
    }

    public static class ListEntity {
        private String classRoomId;
        private String scheduleDetailId;
        private String grade;
        private String mainSchoolName;
        private String mainTeacher;
        private String playCount;
        private long startTime;
        private String subject;
        private String thumbnailUrl;
        private String videoDeleteFlag;

        public String getClassRoomId() {
            return classRoomId;
        }

        public String getScheduleDetailId() {
            return scheduleDetailId;
        }

        public void setScheduleDetailId(String scheduleDetailId) {
            this.scheduleDetailId = scheduleDetailId;
        }

        public String getVideoDeleteFlag() {
            return videoDeleteFlag;
        }

        public void setVideoDeleteFlag(String videoDeleteFlag) {
            this.videoDeleteFlag = videoDeleteFlag;
        }

        public void setClassRoomId(String classRoomId) {
            this.classRoomId = classRoomId;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getMainSchoolName() {
            return mainSchoolName;
        }

        public void setMainSchoolName(String mainSchoolName) {
            this.mainSchoolName = mainSchoolName;
        }

        public String getMainTeacher() {
            return mainTeacher;
        }

        public void setMainTeacher(String mainTeacher) {
            this.mainTeacher = mainTeacher;
        }

        public String getPlayCount() {
            return playCount;
        }

        public void setPlayCount(String playCount) {
            this.playCount = playCount;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }
    }

    public static List<ListEntity> parseResponse(JSONObject response) {
        List<ListEntity> list = new ArrayList<>();
        if ("success".equals(response.optString("result"))) {
            JSONArray jsonArray = response.optJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                ListEntity listEntity = new ListEntity();
                JSONObject object = jsonArray.optJSONObject(i);
                listEntity.setClassRoomId(object.isNull("baseClasslevelId") ? "" : object.optString("baseClasslevelId"));
                listEntity.setScheduleDetailId(object.isNull("scheduleDetailId") ? "" : object.optString("scheduleDetailId"));
                listEntity.setGrade(object.isNull("classlevelName") ? "" : object.optString("classlevelName"));
                listEntity.setMainSchoolName(object.isNull("schoolName") ? "" : object.optString("schoolName"));
                listEntity.setMainTeacher(object.isNull("realName") ? "" : object.optString("realName"));
                listEntity.setPlayCount(object.isNull("showCount") ? "0" : object.optString("showCount"));
                listEntity.setStartTime(object.isNull("realBeginTime") ? 0 : object.optLong("realBeginTime"));
                listEntity.setSubject(object.isNull("subjectName") ? "" : object.optString("subjectName"));
                listEntity.setThumbnailUrl(object.isNull("thumb") ? "" : object.optString("thumb"));
                listEntity.setVideoDeleteFlag(object.isNull("videoDeleteFlag") ? "N" : object.optString("videoDeleteFlag"));
                list.add(listEntity);
            }
        }
        return list;
    }

    public static List<ListEntity> parseSchoolResponse(JSONObject response) {
        List<ListEntity> list = new ArrayList<>();
        if ("success".equals(response.optString("result"))) {
            JSONArray jsonArray = response.optJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                ListEntity listEntity = new ListEntity();
                JSONObject object = jsonArray.optJSONObject(i);
                listEntity.setClassRoomId(object.isNull("baseClasslevelId") ? "" : object.optString("baseClasslevelId"));
                listEntity.setScheduleDetailId(object.isNull("scheduleDetailId") ? "" : object.optString("scheduleDetailId"));
                listEntity.setGrade(object.isNull("classlevelName") ? "" : object.optString("classlevelName"));
                listEntity.setMainSchoolName(object.isNull("schoolName") ? "" : object.optString("schoolName"));
                listEntity.setMainTeacher(object.isNull("realName") ? "" : object.optString("realName"));
                listEntity.setPlayCount(object.isNull("showCount") ? "0" : object.optString("showCount"));
                listEntity.setStartTime(object.isNull("startTime") ? 0 : object.optLong("startTime"));
                listEntity.setSubject(object.isNull("subjectName") ? "" : object.optString("subjectName"));
                listEntity.setThumbnailUrl(object.isNull("thumb") ? "" : object.optString("thumb"));
                listEntity.setVideoDeleteFlag(object.isNull("videoDeleteFlag") ? "N" : object.optString("videoDeleteFlag"));
                list.add(listEntity);
            }
        }
        return list;
    }
}
