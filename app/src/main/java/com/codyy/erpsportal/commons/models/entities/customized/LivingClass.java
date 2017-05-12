package com.codyy.erpsportal.commons.models.entities.customized;

import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 首页-专递课堂-正在直播
 * Created by poe on 16-6-1.
 */
public class LivingClass extends BaseTitleItemBar implements Serializable {

    private String classlevelName;//年级
    private String id ;//直播id
    private String  schoolName;//学校名
    private String subjectName;//评课主题
    private String subjectPic;//学科图片
    private String teacherName;//教师名
    @SerializedName("realBeginTime")
    private String startTime;//开始时间
    private String liveType;//无用数据

    public String getClasslevelName() {
        return classlevelName;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectPic() {
        return subjectPic;
    }

    public void setSubjectPic(String subjectPic) {
        this.subjectPic = subjectPic;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getLiveType() {
        return liveType;
    }

    public void setLiveType(String liveType) {
        this.liveType = liveType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
