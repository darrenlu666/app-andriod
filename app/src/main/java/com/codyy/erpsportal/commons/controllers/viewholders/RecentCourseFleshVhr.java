/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.viewholders;

import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.entities.RecentCourseFlesh;
import com.codyy.erpsportal.commons.utils.DateUtil;

import butterknife.Bind;

/**
 * 专递课堂（海宁定制）近期课程组持者
 * Created by gujiajia on 2017/9/19.
 */
@LayoutId(R.layout.item_channel_interact_live)
public class RecentCourseFleshVhr extends AbsSkeletonVhr<RecentCourseFlesh> {

    @Bind(R.id.tv_start_time)
    TextView mStartTimeTv;
    @Bind(R.id.tv_name)
    TextView mNameTv;
    @Bind(R.id.tv_level_subject_teacher)
    TextView mLevelSubjectTeacherTv;
    @Bind(R.id.tv_state)
    TextView mStateTv;
    @Bind(R.id.rlt_container)
    RelativeLayout mContainerRlt;

    public RecentCourseFleshVhr(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(RecentCourseFlesh recentCourse) {
        String timeStr = TextUtils.isEmpty(recentCourse.getRealBeginTime())?
                recentCourse.getBeginTime():
                recentCourse.getRealBeginTime();
        mStartTimeTv.setText(DateUtil.getDateStr(Long.parseLong(timeStr),"HH:mm"));
        mNameTv.setText(recentCourse.getSchoolName());
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(recentCourse.getClasslevelName())) {
            sb.append(recentCourse.getClasslevelName()).append('/');
        }
        if (!TextUtils.isEmpty(recentCourse.getSubjectName())) {
            sb.append(recentCourse.getSubjectName()).append('/');
        }
        if (!TextUtils.isEmpty(recentCourse.getTeacherName())) {
            sb.append(recentCourse.getTeacherName()).append('/');
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        mLevelSubjectTeacherTv.setText(sb.toString());
    }
}
