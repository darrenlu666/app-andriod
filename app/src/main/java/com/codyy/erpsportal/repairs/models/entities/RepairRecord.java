package com.codyy.erpsportal.repairs.models.entities;

/**
 * 报修记录
 * Created by gujiajia on 2017/3/21.
 */

public class RepairRecord {

    public RepairRecord(String id, String classroomSerial, String classroomName, String reporterName,
                        long reportTime, String content, int status) {
        this.id = id;
        this.classroomSerial = classroomSerial;
        this.classroomName = classroomName;
        this.reporterName = reporterName;
        this.reportTime = reportTime;
        this.content = content;
        this.status = status;
    }

    private String id;

    private String classroomSerial;

    private String classroomName;

    private String reporterName;

    private long reportTime;

    private String content;

    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassroomSerial() {
        return classroomSerial;
    }

    public void setClassroomSerial(String classroomSerial) {
        this.classroomSerial = classroomSerial;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public long getReportTime() {
        return reportTime;
    }

    public void setReportTime(long reportTime) {
        this.reportTime = reportTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
