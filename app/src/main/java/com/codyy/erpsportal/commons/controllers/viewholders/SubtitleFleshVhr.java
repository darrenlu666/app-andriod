/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.viewholders;

import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.entities.SubtitleFlesh;
import com.codyy.erpsportal.commons.widgets.TitleItemBar;

import butterknife.Bind;

/**
 * 小标题组持者
 * Created by gujiajia on 2017/9/19.
 */

@LayoutId(R.layout.item_title_item_bar)
public class SubtitleFleshVhr extends AbsSkeletonVhr<SubtitleFlesh> {

    @Bind(R.id.title_item_bar)
    TitleItemBar mTitleItemBar;

    public SubtitleFleshVhr(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(SubtitleFlesh flesh) {
        mTitleItemBar.setTitle( flesh.getName());
        mTitleItemBar.setHasMore( flesh.isShowMore());
    }
}
