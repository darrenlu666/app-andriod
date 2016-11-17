package com.codyy.erpsportal.county.controllers.models.entities;

import java.util.List;

/**
 * Created by kmdai on 16-8-10.
 */
public class CountyListItemDetail {

    /**
     * actualTimes : 0
     * classSeq : 2
     * daySeq : 7
     * baseUserId : 4b87458af1fc4a939f66b07b5e29f9d0
     * classlevel : 一年级
     * classlevelId : 31efba2782214d0eb5736055aae04da0
     * classroomId : 70f4199a01df401dbd5c6781ca0601be
     * planTimes : 157
     * receiveClassRoomList : [{"classRoom":"接收教室1","clsClassroomId":"5f5e6fa85d534052b015e8554c954965","schoolName":"王刚学校","studentCount":100,"teacherName":"wgschooltea1"},{"classRoom":"主讲教室1","clsClassroomId":"dfaab96e7b14465da63b17cc0b1a1a50","schoolName":"王刚学校","studentCount":100,"teacherName":"wgschooltea1"}]
     * strScheduleDate : 2016-08-07
     * result : success
     * roomName : 主讲教室3
     * rowCount : 2
     * scheduleDetailId : null
     * scheduleId : 6895fd337a7f4b2ca8e6e5ee60480c35
     * schoolName : 王刚学校
     * status : null
     * subject : 数学
     * subjectId : 382c748435cf4600a3871f9b4bab84cf
     * surveillance : null
     * teacherName : wJwTea
     * weekTimes : 29
     */

    private int actualTimes;
    private int classSeq;
    private int daySeq;
    private String baseUserId;
    private String classlevel;
    private String classlevelId;
    private String classroomId;
    private int planTimes;
    private String strScheduleDate;
    private String result;
    private String roomName;
    private int rowCount;
    private String scheduleDetailId;
    private String scheduleId;
    private String schoolName;
    private String status;
    private String subject;
    private String subjectId;
    private String teacherName;
    private int weekTimes;
    private String dayStr;
    private String classStr;

    public String getDayStr() {
        return dayStr;
    }

    public void setDayStr(String dayStr) {
        this.dayStr = dayStr;
    }

    public String getClassStr() {
        return classStr;
    }

    public void setClassStr(String classStr) {
        this.classStr = classStr;
    }

    /**
     * classRoom : 接收教室1
     * clsClassroomId : 5f5e6fa85d534052b015e8554c954965
     * schoolName : 王刚学校
     * studentCount : 100
     * teacherName : wgschooltea1
     */

    private List<ReceiveClassRoomListBean> receiveClassRoomList;

    public int getActualTimes() {
        return actualTimes;
    }

    public void setActualTimes(int actualTimes) {
        this.actualTimes = actualTimes;
    }

    public int getClassSeq() {
        return classSeq;
    }

    public void setClassSeq(int classSeq) {
        this.classSeq = classSeq;
    }

    public int getDaySeq() {
        return daySeq;
    }

    public void setDaySeq(int daySeq) {
        this.daySeq = daySeq;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getClasslevel() {
        return classlevel;
    }

    public void setClasslevel(String classlevel) {
        this.classlevel = classlevel;
    }

    public String getClasslevelId() {
        return classlevelId;
    }

    public void setClasslevelId(String classlevelId) {
        this.classlevelId = classlevelId;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public int getPlanTimes() {
        return planTimes;
    }

    public void setPlanTimes(int planTimes) {
        this.planTimes = planTimes;
    }

    public String getStrScheduleDate() {
        return strScheduleDate;
    }

    public void setStrScheduleDate(String strScheduleDate) {
        this.strScheduleDate = strScheduleDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public String getScheduleDetailId() {
        return scheduleDetailId;
    }

    public void setScheduleDetailId(String scheduleDetailId) {
        this.scheduleDetailId = scheduleDetailId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
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

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getWeekTimes() {
        return weekTimes;
    }

    public void setWeekTimes(int weekTimes) {
        this.weekTimes = weekTimes;
    }

    public List<ReceiveClassRoomListBean> getReceiveClassRoomList() {
        return receiveClassRoomList;
    }

    public void setReceiveClassRoomList(List<ReceiveClassRoomListBean> receiveClassRoomList) {
        this.receiveClassRoomList = receiveClassRoomList;
    }

    public static class ReceiveClassRoomListBean {
        private String classRoom;
        private String clsClassroomId;
        private String schoolName;
        private int studentCount;
        private String teacherName;

        public String getClassRoom() {
            return classRoom;
        }

        public void setClassRoom(String classRoom) {
            this.classRoom = classRoom;
        }

        public String getClsClassroomId() {
            return clsClassroomId;
        }

        public void setClsClassroomId(String clsClassroomId) {
            this.clsClassroomId = clsClassroomId;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public int getStudentCount() {
            return studentCount;
        }

        public void setStudentCount(int studentCount) {
            this.studentCount = studentCount;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }
    }

    public static String getNumberStr(int number) {
        String str[] = {"一", "二", "三", "四", "五", "六", "七", "八"};
        if (number - 1 >= 0 && number <= str.length) {
            return str[number - 1];
        }
        return "";
    }

    public static String getDayStr(int number) {
        String str[] = {"一", "二", "三", "四", "五", "六", "日"};
        if (number - 1 >= 0 && number <= str.length) {
            return str[number - 1];
        }
        return "";
    }
}
