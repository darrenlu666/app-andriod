package com.codyy.erpsportal.classroom.models;

/**
 * 一条dms信息
 * Created by poe on 17-10-12.
 */

public class DMS {

    private String socketUrl;//websocket测速地址
    private String rtmpUrl;//dms服务器地址

    public String getSocketUrl() {
        return socketUrl;
    }

    public void setSocketUrl(String socketUrl) {
        this.socketUrl = socketUrl;
    }

    public String getRtmpUrl() {
        return rtmpUrl;
    }

    public void setRtmpUrl(String rtmpUrl) {
        this.rtmpUrl = rtmpUrl;
    }
}
