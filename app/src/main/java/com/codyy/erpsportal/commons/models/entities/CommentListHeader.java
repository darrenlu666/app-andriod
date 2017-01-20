package com.codyy.erpsportal.commons.models.entities;


import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

/**
 * 评论头
 * Created by gujiajia on 2017/1/17.
 */

public abstract class CommentListHeader {

    /**
     * 获取布局id
     * @return 布局id
     */
    @LayoutRes
    public abstract int obtainLayoutId();

    public abstract ViewHolder createViewHolder(ViewGroup parent);

    public abstract void update(ViewHolder holder);
}
