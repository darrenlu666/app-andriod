package com.codyy.erpsportal.homework.models.entities.task;

/**
 * 本地保存习题答案的实体类
 * Created by ldh on 2015/12/29.
 */
public class TaskAnswer  {

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskItemId() {
        return taskItemId;
    }

    public void setTaskItemId(String taskItemId) {
        this.taskItemId = taskItemId;
    }

    public String getTaskItemType() {
        return taskItemType;
    }

    public void setTaskItemType(String taskItemType) {
        this.taskItemType = taskItemType;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public String getStudentTextAnswer() {
        return studentTextAnswer;
    }

    public void setStudentTextAnswer(String studentTextAnswer) {
        this.studentTextAnswer = studentTextAnswer;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceLocalPath() {
        return resourceLocalPath;
    }

    public void setResourceLocalPath(String resourceLocalPath) {
        this.resourceLocalPath = resourceLocalPath;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    private String taskId;
    private String taskItemId;
    private String taskItemType;
    private String studentAnswer;
    private String studentTextAnswer;
    private String resourceName;
    private String resourceId;
    private String resourceLocalPath;
    private String resourceType;
    private long time;
}
