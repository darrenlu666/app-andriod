/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.viewholders;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.entities.EmptyFlesh;

import butterknife.Bind;

/**
 * 暂时没有相关内容组持者
 * Created by gujiajia on 2017/9/20.
 */
@LayoutId(R.layout.item_emtry_note)
public class EmptyFleshVhr extends AbsSkeletonVhr<EmptyFlesh> {

    @Bind(R.id.tv_empty)
    TextView mEmptyTv;

    public EmptyFleshVhr(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(EmptyFlesh flesh) {
        if (flesh.getText() != null) {
            mEmptyTv.setText(flesh.getText());
        }
    }
}
