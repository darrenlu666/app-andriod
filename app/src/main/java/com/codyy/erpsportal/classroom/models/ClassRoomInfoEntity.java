package com.codyy.erpsportal.classroom.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 专递课堂 直录播课堂
 * Created by ldh on 2016/6/29.
 */
public class ClassRoomInfoEntity {


    /**
     * list : [{"classPeriod":"测试内容hm8u","status":"测试内容6am5","teacherName":"测试内容3125","classId":"测试内容07qf","grade":"测试内容m530","schoolTime":"测试内容w9o9","mainSchoolName":"测试内容c6e3","subject":"测试内容3490"}]
     * total : 测试内容6669
     * errorCode : 测试内容48t2
     * classCount : 84572
     * result : 测试内容7ee8
     */

    private String total;
    private String errorCode;
    private int classCount;
    private String result;
    /**
     * classPeriod : 测试内容hm8u
     * status : 测试内容6am5
     * teacherName : 测试内容3125
     * classId : 测试内容07qf
     * grade : 测试内容m530
     * schoolTime : 测试内容w9o9
     * mainSchoolName : 测试内容c6e3
     * subject : 测试内容3490
     */

    private List<ListEntity> list;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getClassCount() {
        return classCount;
    }

    public void setClassCount(int classCount) {
        this.classCount = classCount;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<ListEntity> getList() {
        return list;
    }

    public void setList(List<ListEntity> list) {
        this.list = list;
    }

    public static class ListEntity {
        private String classPeriod;
        private String status;
        private String teacherName;
        private String classId;
        private String scheduleDetailId;
        private String grade;
        private long schoolTime;
        private long realBeginTime;
        private String mainSchoolName;
        private String subject;
        private String thumb;
        private String areaPath;
        private String roomName;//教室名字
        private boolean showClassRoomName;//是否显示教室名

        public String getAreaPath() {
            return areaPath;
        }

        public void setAreaPath(String areaPath) {
            this.areaPath = areaPath;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public boolean isShowClassRoomName() {
            return showClassRoomName;
        }

        public void setShowClassRoomName(boolean showClassRoomName) {
            this.showClassRoomName = showClassRoomName;
        }

        public long getRealBeginTime() {
            return realBeginTime;
        }

        public void setRealBeginTime(long realBeginTime) {
            this.realBeginTime = realBeginTime;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String getClassPeriod() {
            return classPeriod;
        }

        public void setClassPeriod(String classPeriod) {
            this.classPeriod = classPeriod;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getClassId() {
            return classId;
        }

        public void setClassId(String classId) {
            this.classId = classId;
        }

        public String getScheduleDetailId() {
            return scheduleDetailId;
        }

        public void setScheduleDetailId(String scheduleDetailId) {
            this.scheduleDetailId = scheduleDetailId;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public long getSchoolTime() {
            return schoolTime;
        }

        public void setSchoolTime(long schoolTime) {
            this.schoolTime = schoolTime;
        }

        public String getMainSchoolName() {
            return mainSchoolName;
        }

        public void setMainSchoolName(String mainSchoolName) {
            this.mainSchoolName = mainSchoolName;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }
    }

    public static ClassRoomInfoEntity parseResponse(JSONObject response) {
        ClassRoomInfoEntity classRoomInfoEntity = new ClassRoomInfoEntity();
        if (response.optString("result").equals("success")) {
            classRoomInfoEntity.setClassCount(response.isNull("classCount") ? 0 : response.optInt("classCount"));
            JSONArray jsonArray = response.optJSONArray("list");
            List<ListEntity> listEntities = new ArrayList<>();
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    ListEntity listEntity = new ListEntity();
                    listEntity.setClassPeriod((jsonObject.isNull("classPeriod") ? "" : jsonObject.optString("classPeriod")));
                    listEntity.setClassId((jsonObject.isNull("baseClasslevelId") ? "" : jsonObject.optString("baseClasslevelId")));
                    listEntity.setScheduleDetailId(jsonObject.isNull("scheduleDetailId") ? "" : jsonObject.optString("scheduleDetailId"));
                    listEntity.setGrade((jsonObject.isNull("classlevelName") ? "" : jsonObject.optString("classlevelName")));
                    listEntity.setMainSchoolName(jsonObject.isNull("schoolName") ? "" : jsonObject.optString("schoolName"));
                    listEntity.setRealBeginTime(jsonObject.isNull("realBeginTime")?0:jsonObject.optLong("realBeginTime"));
                    listEntity.setSchoolTime(jsonObject.isNull("startTime") ? 0 : jsonObject.optLong("startTime"));
                    listEntity.setStatus(jsonObject.isNull("status") ? "" : jsonObject.optString("status"));
                    listEntity.setSubject(jsonObject.isNull("subjectName") ? "" : jsonObject.optString("subjectName"));
                    listEntity.setTeacherName(jsonObject.isNull("realName") ? "" : jsonObject.optString("realName"));
                    listEntity.setThumb(jsonObject.isNull("thumb") ? "" : jsonObject.optString("thumb"));
                    listEntity.setRoomName(jsonObject.optString("roomName",""));
                    listEntity.setShowClassRoomName(jsonObject.optBoolean("showClassRoomName",false));
                    listEntity.setAreaPath(jsonObject.optString("areaPath",""));
                    listEntities.add(listEntity);
                }
                classRoomInfoEntity.setList(listEntities);
            }
        }
        return classRoomInfoEntity;
    }
}
