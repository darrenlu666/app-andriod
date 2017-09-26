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
import com.codyy.erpsportal.commons.models.entities.HistoryCourseBigFlesh;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;

/**
 * 门户-直播课堂（海宁定制）-大课程回放 组持者
 * Created by gujiajia on 2017/9/21.
 */
@LayoutId(R.layout.item_customized_history_class)
public class HistoryCourseFleshBigVhr extends AbsSkeletonVhr<HistoryCourseBigFlesh> {

    @Bind(R.id.sdv)
    SimpleDraweeView mSDV;
    @Bind(R.id.tv_school)
    TextView mSchoolTextView;
    @Bind(R.id.tv_level_subject_teacher)
    TextView mLevelSTTextView;

    public HistoryCourseFleshBigVhr(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(HistoryCourseBigFlesh flesh) {
        ImageFetcher.getInstance(mSchoolTextView.getContext()).fetchSmall(mSDV,flesh.getThumb());
        mSchoolTextView.setText(flesh.getSchoolName());
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(flesh.getClasslevelName())) {
            sb.append(flesh.getClasslevelName()).append('/');
        }
        if (!TextUtils.isEmpty(flesh.getSubjectName())) {
            sb.append(flesh.getSubjectName()).append('/');
        }
        if (!TextUtils.isEmpty(flesh.getTeacherName())) {
            sb.append(flesh.getTeacherName()).append('/');
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        mLevelSTTextView.setText(sb.toString());
    }
}
