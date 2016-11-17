package com.codyy.erpsportal.homework.models.entities;

/**
 * 作业信息
 * Created by ldh on 2016/6/12.
 */
public class WorkInfoEntity {
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getReader() {
        return reader;
    }

    public void setReader(String reader) {
        this.reader = reader;
    }

    public String getSubmitPerson() {
        return submitPerson;
    }

    public void setSubmitPerson(String submitPerson) {
        this.submitPerson = submitPerson;
    }

    public String getNotSubmitPerson() {
        return notSubmitPerson;
    }

    public void setNotSubmitPerson(String notSubmitPerson) {
        this.notSubmitPerson = notSubmitPerson;
    }

    public String getAssignTime() {
        return assignTime;
    }

    public void setAssignTime(String assignTime) {
        this.assignTime = assignTime;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    private String subject;
    private String reader;
    private String submitPerson;
    private String notSubmitPerson;
    private String assignTime;
    private String deadline;
}
