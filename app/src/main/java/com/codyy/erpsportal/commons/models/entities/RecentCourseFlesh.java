/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.models.entities;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;

/**
 * 近期课程
 * Created by gujiajia on 2017/9/19.
 */
@LayoutId(R.layout.item_channel_interact_live)
public class RecentCourseFlesh implements Flesh {


    /**
     * beginTime : 1578103200000
     * canView : false
     * classlevelName : 一年级
     * id : 6b4f9297f42643c7a91f96346adef9b4
     * realBeginTime :
     * scheduleDate : 2020-01-04
     * schoolName : 琦琦学校
     * status : INIT
     * subjectName : 语文
     * subjectPic :
     * teacherName : 老红军2
     */

    private String beginTime;
    private boolean canView;
    private String classlevelName;
    private String id;
    private String realBeginTime;
    private String scheduleDate;
    private String schoolName;
    private String status;
    private String subjectName;
    private String subjectPic;
    private String teacherName;

    @Override
    public boolean crossColumn() {
        return true;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public boolean isCanView() {
        return canView;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }

    public String getClasslevelName() {
        return classlevelName;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRealBeginTime() {
        return realBeginTime;
    }

    public void setRealBeginTime(String realBeginTime) {
        this.realBeginTime = realBeginTime;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
