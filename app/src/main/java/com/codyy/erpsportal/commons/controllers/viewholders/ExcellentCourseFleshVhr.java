/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.viewholders;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;

/**
 * 精品课程组持者
 * Created by gujiajia on 2017/9/15.
 */
@LayoutId(R.layout.item_excellent_course_flesh)
public class ExcellentCourseFleshVhr extends AbsSkeletonVhr<ExcellentCourseFlesh> {

    @Bind(R.id.dv_icon)
    SimpleDraweeView mIconDv;

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.tv_scope)
    TextView mScopeTv;

    @Bind(R.id.tv_school)
    TextView mSchoolTv;

    public ExcellentCourseFleshVhr(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(ExcellentCourseFlesh excellentCourse) {
        if (!TextUtils.isEmpty(excellentCourse.getTitle())) {
            mTitleTv.setText(excellentCourse.getTitle());
        } else {
            mTitleTv.setText(excellentCourse.getRealName());
        }
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(excellentCourse.getClasslevelName())) {
            sb.append(excellentCourse.getClasslevelName()).append('/');
        }
        if (!TextUtils.isEmpty(excellentCourse.getSubjectName())) {
            sb.append(excellentCourse.getSubjectName()).append('/');
        }
        if (!TextUtils.isEmpty(excellentCourse.getVersionName())) {
            sb.append(excellentCourse.getVersionName()).append('/');
        }
        if (!TextUtils.isEmpty(excellentCourse.getVolumeName())) {
            sb.append(excellentCourse.getVolumeName()).append('/');
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        mScopeTv.setText(sb.toString());
        mSchoolTv.setText(excellentCourse.getSchoolName());
        ImageFetcher.getInstance(mIconDv)
                .fetchSmall(mIconDv, excellentCourse.getThumb());
    }
}
