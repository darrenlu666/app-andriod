package com.codyy.erpsportal.county.controllers.models.entities;

import java.util.List;

/**
 * Created by kmdai on 16-8-13.
 */
public class CountyDetialFilter {

    /**
     * result : success
     * currentWeek : 11
     * totalWeek : 22
     * classroomList : [{"classId":"5f5e6fa85d534052b015e8554c954965","className":"接收教室1","scheduleDetailId":"","schoolName":"王刚学校","weekSeq":""}]
     */

    private String result;
    private int currentWeek;
    private int totalWeek;
    /**
     * classId : 5f5e6fa85d534052b015e8554c954965
     * className : 接收教室1
     * scheduleDetailId :
     * schoolName : 王刚学校
     * weekSeq :
     */

    private List<ClassroomListBean> classroomList;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCurrentWeek() {
        return currentWeek;
    }

    public void setCurrentWeek(int currentWeek) {
        this.currentWeek = currentWeek;
    }

    public int getTotalWeek() {
        return totalWeek;
    }

    public void setTotalWeek(int totalWeek) {
        this.totalWeek = totalWeek;
    }

    public List<ClassroomListBean> getClassroomList() {
        return classroomList;
    }

    public void setClassroomList(List<ClassroomListBean> classroomList) {
        this.classroomList = classroomList;
    }

    public static class ClassroomListBean {
        private String classId;
        private String className;
        private String scheduleDetailId;
        private String schoolName;
        private String weekSeq;
        private boolean isCheck;

        public String getClassId() {
            return classId;
        }

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }

        public void setClassId(String classId) {
            this.classId = classId;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getScheduleDetailId() {
            return scheduleDetailId;
        }

        public void setScheduleDetailId(String scheduleDetailId) {
            this.scheduleDetailId = scheduleDetailId;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getWeekSeq() {
            return weekSeq;
        }

        public void setWeekSeq(String weekSeq) {
            this.weekSeq = weekSeq;
        }
    }
}
