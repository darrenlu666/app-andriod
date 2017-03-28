package com.codyy.erpsportal.repairs.models.entities;

import android.support.annotation.StringRes;

import com.codyy.erpsportal.R;

/**
 * 报修详情
 * Created by gujiajia on 2017/3/24.
 */

public class RepairDetails {

    private String id;

    private String serial;

    private String classroomSerial;

    private String classroomName;

    private String description;

    private String[] photos;

    private String categories;

    private String reporter;

    private String phone;

    private long reportTime;

    private int status;

    private String handlerName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getReportTime() {
        return reportTime;
    }

    public void setReportTime(long reportTime) {
        this.reportTime = reportTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public @StringRes int statusStr() {
        switch (status) {
            case 1:
                return R.string.status_await_handle;
            case 2:
                return R.string.status_handling;
            case 3:
                return R.string.status_handled;
            case 4:
                return R.string.status_accepted;
        }
        return android.R.string.unknownName;
    }
}
