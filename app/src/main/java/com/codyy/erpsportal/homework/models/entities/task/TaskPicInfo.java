package com.codyy.erpsportal.homework.models.entities.task;

/**
 * 答题图片类
 * Created by ldh on 2016/2/26.
 */
public class TaskPicInfo {

    private String taskItemId;
    private String taskItemType;
    private String imageName;
    private String imageUrl;
    private String imageLocalPath;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageLocalPath() {
        return imageLocalPath;
    }

    public void setImageLocalPath(String imageLocalPath) {
        this.imageLocalPath = imageLocalPath;
    }
}
