package com.codyy.erpsportal.commons.models.personal;

/**
 * 二维码信息实体类.
 * Created by poe on 16/05/17.
 */

public class ShareApp {

    private String appOS;//android or iOS appOS
    private String appPad;//
    private String appPhoneUrl;//android download url .
    private String applicationName;//互动学习平台
    private String download_url;//ios download url .
    private String latestVersion;

    public String getAppOs() {
        return appOS;
    }

    public void setAppOs(String appOs) {
        this.appOS = appOs;
    }

    public String getAppPad() {
        return appPad;
    }

    public void setAppPad(String appPad) {
        this.appPad = appPad;
    }

    public String getAppPhoneUrl() {
        return appPhoneUrl;
    }

    public void setAppPhoneUrl(String appPhoneUrl) {
        this.appPhoneUrl = appPhoneUrl;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }
}
