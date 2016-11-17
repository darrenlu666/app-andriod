package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONObject;

/**
 * 名校网络课堂和专递课堂的实时直播类
 * Created by ldh on 2015/9/14.
 */
public class LiveVideoDetail {


    /**
     * result : success
     * videoUrl :
     * msg : 未开始
     * classroomName : 主讲教室1
     */
    private String result;
    private String msg;
    private String classroomName;
    private String serverAddress;
    private String streamingServerType;
    private String dmsServerHost;
    private String videoUrl;

    public void setResult(String result) {
        this.result = result;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getStreamingServerType() {
        return streamingServerType;
    }

    public void setStreamingServerType(String streamingServerType) {
        this.streamingServerType = streamingServerType;
    }

    public String getDmsServerHost() {
        return dmsServerHost;
    }

    public void setDmsServerHost(String dmsServerHost) {
        this.dmsServerHost = dmsServerHost;
    }

    public String getResult() {
        return result;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getMsg() {
        return msg;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public static LiveVideoDetail parseObject(JSONObject jsonObject){
        LiveVideoDetail liveVideoDetail = new LiveVideoDetail();
        liveVideoDetail.setClassroomName(jsonObject.optString("classroomName"));
        liveVideoDetail.setVideoUrl(jsonObject.optString("videoUrl"));
        liveVideoDetail.setDmsServerHost(jsonObject.optString("dmsServerHost"));
        liveVideoDetail.setServerAddress(jsonObject.optString("serverAddress"));
        liveVideoDetail.setStreamingServerType(jsonObject.optString("streamingServerType"));
        return  liveVideoDetail;
    }
}
