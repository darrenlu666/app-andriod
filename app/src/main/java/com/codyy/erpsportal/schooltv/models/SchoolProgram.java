package com.codyy.erpsportal.schooltv.models;

import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;

/**
 * 校园电视台-节目单
 * Created by poe on 17-3-14.
 */

public class SchoolProgram extends BaseTitleItemBar {

    public static final int STATUS_INIT =   0;//un start init .
    public static final int STATUS_ON = 1;//is continue .
    public static final int STATUS_END = 2;//already end .

    public static final String TRANS_SUCCESS = "TRANS_SUCCESS";//视频转换成功
    public static final String TRANS_TRANSING = "TRANS_TRANSING";//视频转换中
    public static final String TRANS_PEDING = "TRANS_PEDING";//视频转换失败

    private String brief;//简介
    private long endTime;
    private String programName;
    private String speaker;
    private long startTime;
    private int status;
    private String streamUrl;
    private String statusStr;
    private String thumbPath;
    private String transFlag;
    private String tvProgramDetailId;
    private String videoName;
    private String videoPath;

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getTransFlag() {
        return transFlag;
    }

    public void setTransFlag(String transFlag) {
        this.transFlag = transFlag;
    }

    public String getTvProgramDetailId() {
        return tvProgramDetailId;
    }

    public void setTvProgramDetailId(String tvProgramDetailId) {
        this.tvProgramDetailId = tvProgramDetailId;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
}
