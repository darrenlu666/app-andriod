package com.codyy.erpsportal.schooltv.models;

import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;

/**
 * 校园电视台-往期视频
 * Created by poe on 17-3-14.
 */

public class SchoolVideo extends BaseTitleItemBar {
    private String programName;
    private String serverAddress;
    private String speaker;
    private long startTime;
    private String thumbPath;
    private String tvProgramDetailId;
    private String tvProgramId;
    private String videoPath;
    private int viewCnt;

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getTvProgramDetailId() {
        return tvProgramDetailId;
    }

    public void setTvProgramDetailId(String tvProgramDetailId) {
        this.tvProgramDetailId = tvProgramDetailId;
    }

    public String getTvProgramId() {
        return tvProgramId;
    }

    public void setTvProgramId(String tvProgramId) {
        this.tvProgramId = tvProgramId;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public int getViewCnt() {
        return viewCnt;
    }

    public void setViewCnt(int viewCnt) {
        this.viewCnt = viewCnt;
    }
}
