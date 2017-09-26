/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.models.entities;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;

/**
 * 门户-直播课堂（海宁定制）-课程回放
 * Created by gujiajia on 2017/9/20.
 */
@LayoutId(R.layout.item_customized_history_class)
public class HistoryCourseBigFlesh extends HistoryCourseFlesh {

    public HistoryCourseBigFlesh() {}

    public HistoryCourseBigFlesh(HistoryCourseFlesh historyCourseFlesh) {
        setId( historyCourseFlesh.getId());
        setAreaName( historyCourseFlesh.getAreaName());
        setBaseAreaId( historyCourseFlesh.getBaseAreaId());
        setClasslevelName( historyCourseFlesh.getClasslevelName());
        setDuration( historyCourseFlesh.getDuration());
        setPosition( historyCourseFlesh.getPosition());
        setRealBeginTime( historyCourseFlesh.getRealBeginTime());
        setRealEndTime( historyCourseFlesh.getRealEndTime());
        setReceiveSchool( historyCourseFlesh.getReceiveSchool());
        setRoomName( historyCourseFlesh.getRoomName());
        setSchoolId( historyCourseFlesh.getSchoolId());
        setSchoolName( historyCourseFlesh.getSchoolName());
        setServerAddress( historyCourseFlesh.getServerAddress());
        setSubjectName( historyCourseFlesh.getSubjectName());
        setTeacherName( historyCourseFlesh.getTeacherName());
        setTeacherUserId( historyCourseFlesh.getTeacherUserId());
        setThumb( historyCourseFlesh.getThumb());
        setVideos( historyCourseFlesh.getVideos());
        setWatchCount( historyCourseFlesh.getWatchCount());
    }

    @Override
    public boolean crossColumn() {
        return true;
    }
}
