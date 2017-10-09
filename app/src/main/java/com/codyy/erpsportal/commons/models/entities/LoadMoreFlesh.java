/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.models.entities;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;

/**
 * 加载更多
 * Created by gujiajia on 2017/9/28.
 */
@LayoutId(R.layout.flesh_load_more)
public class LoadMoreFlesh implements Flesh {
    @Override
    public boolean crossColumn() {
        return true;
    }
}
