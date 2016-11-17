package com.codyy.erpsportal.commons.services;

/**
 * Created by gujiajia on 2016/7/22.
 */
public class CacheInfo {
    private String fileName;
    private String downloadUrl;
    private String size;
    private String userId;
    private String resId;

    public CacheInfo(String fileName, String downloadUrl, String size, String userId, String resId) {
        this.fileName = fileName;
        this.downloadUrl = downloadUrl;
        this.size = size;
        this.userId = userId;
        this.resId = resId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }
}
