package com.codyy.erpsportal.homework.models.entities.task;

/**
 * 学生答题时间记录
 * Created by ldh on 2016/3/7.
 */
public class AnswerTimeLog {
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public long getEstimateEndTime() {
        return estimateEndTime;
    }

    public void setEstimateEndTime(long estimateEndTime) {
        this.estimateEndTime = estimateEndTime;
    }

    public long getRealEndTime() {
        return realEndTime;
    }

    public void setRealEndTime(long realEndTime) {
        this.realEndTime = realEndTime;
    }

    public String getExamResultId() {
        return examResultId;
    }

    public void setExamResultId(String examResultId) {
        this.examResultId = examResultId;
    }


    private String studentId;
    private String taskId;
    private long estimateEndTime;
    private long realEndTime;
    private String examResultId;
}
