/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.codyy.erpsportal.commons.models.entities.Flesh;

import butterknife.ButterKnife;

/**
 * Created by gujiajia on 2017/9/14.
 */

public abstract class AbsSkeletonVhr<T extends Flesh> extends RecyclerView.ViewHolder {
    public AbsSkeletonVhr(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public abstract void bind(T flesh);
}
