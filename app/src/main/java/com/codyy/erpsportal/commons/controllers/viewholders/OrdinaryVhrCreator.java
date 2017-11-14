/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.viewholders;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

import java.lang.reflect.Constructor;

/**
 * 一般的组持者创建者
 * Created by gujiajia on 2017/9/15.
 */

public class OrdinaryVhrCreator<VH extends ViewHolder> extends AbsVhrCreator<VH> {

    private Constructor<VH> mConstructor;

    private @LayoutRes int mLayoutId;

    public OrdinaryVhrCreator(Class<VH> vhClass, @LayoutRes int layoutId) {
        try {
            mConstructor = vhClass.getConstructor(View.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (mConstructor == null) {
            throw new RuntimeException("No constructor with argument View!");
        }
        mLayoutId = layoutId;
    }

    @Override
    protected int obtainLayoutId() {
        return mLayoutId;
    }

    @Override
    protected VH doCreate(View view) {
        VH viewHolder = null;
        try {
            viewHolder = mConstructor.newInstance(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return viewHolder;
    }
}
