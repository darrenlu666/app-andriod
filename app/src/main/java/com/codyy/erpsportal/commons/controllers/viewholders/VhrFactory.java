/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.viewholders;

import android.util.SparseArray;
import android.view.ViewGroup;

import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.entities.Flesh;

/**
 * Created by gujiajia on 2017/9/14.
 */

public class VhrFactory {

    private SparseArray<OrdinaryVhrCreator<? extends AbsSkeletonVhr>> mCreators = new SparseArray<>();

    public void addViewHolder(Class<? extends AbsSkeletonVhr> vhrClass) {
        LayoutId layoutIdAnn = vhrClass.getAnnotation(LayoutId.class);
        int layoutId = layoutIdAnn.value();
        mCreators.put(layoutId, new OrdinaryVhrCreator<>(vhrClass, layoutId));
    }

    public AbsSkeletonVhr<Flesh> create(ViewGroup parent, int viewType) {
        return mCreators.get(viewType).createViewHolder(parent);
    }
}
