package com.codyy.erpsportal.commons.models.entities.interact;

import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;

/**
 * 互动听课-录制权限
 * Created by poe on 16-6-23.
 */
public class RecordPriority extends BaseTitleItemBar{

    private boolean     isRecorder; //是否有录制权限 true ：有 false：没有
    private String      recorderSchool;//录制学校名
    private String      recorderName;//录制人名字

    public boolean isRecorder() {
        return isRecorder;
    }

    public void setRecorder(boolean recorder) {
        isRecorder = recorder;
    }

    public String getRecorderSchool() {
        return recorderSchool;
    }

    public void setRecorderSchool(String recorderSchool) {
        this.recorderSchool = recorderSchool;
    }

    public String getRecorderName() {
        return recorderName;
    }

    public void setRecorderName(String recorderName) {
        this.recorderName = recorderName;
    }
}
