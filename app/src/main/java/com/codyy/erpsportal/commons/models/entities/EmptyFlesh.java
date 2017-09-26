/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.models.entities;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;

/**
 * 暂时没有相关内容
 * Created by gujiajia on 2017/9/20.
 */
@LayoutId(R.layout.item_emtry_note)
public class EmptyFlesh implements Flesh {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean crossColumn() {
        return true;
    }
}
