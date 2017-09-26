/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.models.entities;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;

/**
 * 小标题
 * Created by gujiajia on 2017/9/19.
 */

@LayoutId(R.layout.item_title_item_bar)
public class SubtitleFlesh implements Flesh {

    private String name;

    private boolean showMore;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowMore() {
        return showMore;
    }

    public void setShowMore(boolean showMore) {
        this.showMore = showMore;
    }

    @Override
    public boolean crossColumn() {
        return true;
    }
}
