package com.codyy.erpsportal.homework.models.entities;

/**
 * 作业列表基类
 * Created by ldh on 2015/12/28.
 */
public class WorkListBase {
    /**
     * workId : ************
     * workName : 高三化学第五章练习作业
     * workAssignTime : yyyy-MM-dd hh:mm
     * workSubject : 数学
     * workState : 待批阅
     */

    private String workId;
    private String workName;
    private String workAssignTime;
    private String workSubject;
    private String workState;
    private String subjectPic;
    private String readOverType;

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public void setWorkAssignTime(String workAssignTime) {
        this.workAssignTime = workAssignTime;
    }

    public void setWorkSubject(String workSubject) {
        this.workSubject = workSubject;
    }

    public void setWorkState(String workState) {
        this.workState = workState;
    }

    public String getWorkId() {
        return workId;
    }

    public String getWorkName() {
        return workName;
    }

    public String geworkAssignTime() {
        return workAssignTime;
    }

    public String getWorkSubject() {
        return workSubject;
    }


    public String getSubjectPic() {
        return subjectPic;
    }

    public void setSubjectPic(String subjectPic) {
        this.subjectPic = subjectPic;
    }

    public String getWorkState() {
        return workState;
    }

    public String getReadOverType() {
        return readOverType;
    }

    public void setReadOverType(String readOverType) {
        this.readOverType = readOverType;
    }

}
