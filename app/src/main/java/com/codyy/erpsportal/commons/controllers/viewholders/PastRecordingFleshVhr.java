/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.viewholders;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;

/**
 * 往期录播组持者
 * Created by gujiajia on 2017/9/15.
 */
@LayoutId(R.layout.item_past_recording_flesh)
public class PastRecordingFleshVhr extends AbsSkeletonVhr<PastRecordingFlesh> {

    @Bind(R.id.dv_icon)
    SimpleDraweeView mIconDv;

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.tv_scope)
    TextView mScopeTv;

    @Bind(R.id.tv_school)
    TextView mSchoolTv;

    public PastRecordingFleshVhr(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(PastRecordingFlesh flesh) {
        mTitleTv.setText(flesh.getName());
        mScopeTv.setText(flesh.getScope());
        mSchoolTv.setText(flesh.getSchoolName());
    }
}
