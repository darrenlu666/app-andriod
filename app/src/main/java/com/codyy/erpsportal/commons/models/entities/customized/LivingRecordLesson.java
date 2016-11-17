package com.codyy.erpsportal.commons.models.entities.customized;

import android.text.TextUtils;

import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;

import java.io.Serializable;

/**
 * 直录播课堂
 * Created by poe on 16-6-2.
 */

public class LivingRecordLesson extends BaseTitleItemBar implements Serializable{
    private String id;
    private String classlevelName;
    private String subjectName;
    private String schoolName;
    private String thumb;
    private String teacherName;
    private String serverAddress;
    private String watchCount;
    private String session;//节次 “一” ～ “八”

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClasslevelName() {
        return classlevelName;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getWatchCount() {
        if(TextUtils.isEmpty(watchCount)){
            return  0;
        }
        return Integer.valueOf(watchCount);
    }

    public void setWatchCount(String watchCount) {
        this.watchCount = watchCount;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
