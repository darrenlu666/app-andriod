/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.viewholders;

import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.entities.LoadMoreFlesh;

/**
 * Created by gujiajia on 2017/9/29.
 */
@LayoutId(R.layout.flesh_load_more)
public class LoadMoreFleshVhr extends AbsSkeletonVhr<LoadMoreFlesh> {
    public LoadMoreFleshVhr(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(LoadMoreFlesh flesh) {

    }
}
