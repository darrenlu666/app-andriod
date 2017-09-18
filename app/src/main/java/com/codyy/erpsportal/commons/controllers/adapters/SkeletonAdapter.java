/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.adapters;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.codyy.erpsportal.commons.controllers.viewholders.AbsSkeletonVhr;
import com.codyy.erpsportal.commons.controllers.viewholders.VhrFactory;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.entities.Flesh;

import java.util.ArrayList;
import java.util.List;

/**
 * 频道页优课资源数据适配器
 */
public class SkeletonAdapter extends RecyclerView.Adapter<AbsSkeletonVhr<Flesh>> {

    private static final String TAG = "SkeletonAdapter";

    private final List<Flesh> mFleshes;

    private final VhrFactory mVhrFactory;

    public SkeletonAdapter(VhrFactory vhrFactory) {
        mFleshes = new ArrayList<>();
        mVhrFactory = vhrFactory;
    }

    public void setFleshes(List<Flesh> fleshes) {
        this.mFleshes.clear();
        this.mFleshes.addAll(fleshes);
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        gridLayoutManager.setSpanSizeLookup(new TitleSpanSizeLookup(mFleshes, gridLayoutManager.getSpanCount()));
    }



    @Override
    public AbsSkeletonVhr<Flesh> onCreateViewHolder(ViewGroup parent, int viewType) {
        return mVhrFactory.create(parent, viewType);
    }

    @Override
    public void onBindViewHolder(AbsSkeletonVhr<Flesh> holder, int position) {
        holder.bind(mFleshes.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        Flesh item = mFleshes.get(position);
        LayoutId layoutId = item.getClass().getAnnotation(LayoutId.class);
        return layoutId.value();
    }

    @Override
    public int getItemCount() {
        return mFleshes.size();
    }

    public void addItem(int index, Flesh item) {
        mFleshes.add(index, item);
    }

    public void addItem(Flesh item) {
        mFleshes.add(item);
    }

    public Flesh removeItem(int index) {
        return mFleshes.remove(index);
    }

    public Flesh getItemAt(int i) {
        return mFleshes.get(i);
    }

    public void removeItems(int i) {

    }

    public void clearItems() {

    }

    public static class TitleSpanSizeLookup extends GridLayoutManager.SpanSizeLookup{

        private List<Flesh> mItems;

        private int mSpanCount;

        public TitleSpanSizeLookup(List<Flesh> items, int spanCount) {
            mItems = items;
            mSpanCount = spanCount;
        }

        @Override
        public int getSpanSize(int position) {
            if (mItems.get(position).crossColumn()) {
                return mSpanCount;
            }
            return 1;
        }
    }
}
