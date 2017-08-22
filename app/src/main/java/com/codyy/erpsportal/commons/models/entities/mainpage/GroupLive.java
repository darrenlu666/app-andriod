package com.codyy.erpsportal.commons.models.entities.mainpage;

import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

/**
 * 集团校－直播类
 * Created by poe on 17-8-22.
 */

public class GroupLive extends BaseTitleItemBar{


    /**
     * baseClasslevelId :
     * baseClasslevelName : 国家学校一年级11
     * baseSubjectId :
     * baseSubjectName : 语文22222212
     * courseId : 58fec67cf65a4f29b05a127dfeb3291d
     * imagePath : http://china.dev.com:8080/mobile/images/courseLive.png
     * realName : 语文老师
     * schoolName : 国家学校
     * serverAddress :
     * status : PROGRESS
     * thumb :
     * type : live
     */

    private String baseClasslevelId;
    private String baseClasslevelName;
    private String baseSubjectId;
    private String baseSubjectName;
    private String courseId;
    private String imagePath;
    private String realName;
    private String schoolName;
    private String serverAddress;
    private String status;
    private String thumb;
    private String type;

    public String getBaseClasslevelId() {
        return baseClasslevelId;
    }

    public void setBaseClasslevelId(String baseClasslevelId) {
        this.baseClasslevelId = baseClasslevelId;
    }

    public String getBaseClasslevelName() {
        return baseClasslevelName;
    }

    public void setBaseClasslevelName(String baseClasslevelName) {
        this.baseClasslevelName = baseClasslevelName;
    }

    public String getBaseSubjectId() {
        return baseSubjectId;
    }

    public void setBaseSubjectId(String baseSubjectId) {
        this.baseSubjectId = baseSubjectId;
    }

    public String getBaseSubjectName() {
        return baseSubjectName;
    }

    public void setBaseSubjectName(String baseSubjectName) {
        this.baseSubjectName = baseSubjectName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
