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

    private long endTime;
    private String programName;
    private long startTime;
    private int status;
    private String statusStr;
    private String thumbPath;
    private String transFlag;
    private String tvProgramDetailId;

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
}
