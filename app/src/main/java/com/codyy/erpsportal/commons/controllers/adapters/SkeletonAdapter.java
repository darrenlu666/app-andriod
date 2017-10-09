/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.adapters;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codyy.erpsportal.commons.controllers.viewholders.AbsSkeletonVhr;
import com.codyy.erpsportal.commons.controllers.viewholders.LoadMoreFleshVhr;
import com.codyy.erpsportal.commons.controllers.viewholders.VhrFactory;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.entities.Flesh;
import com.codyy.erpsportal.commons.models.entities.LoadMoreFlesh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 骨架数据适配器
 */
public class SkeletonAdapter extends RecyclerView.Adapter<AbsSkeletonVhr<Flesh>> {

    private static final String TAG = "SkeletonAdapter";

    private final List<Flesh> mFleshes;

    private final VhrFactory mVhrFactory;

    private OnFleshStabbedListener mOnFleshStabbedListener;


    private int mVisibleThreshold = 2;

    private int mLastVisibleItem;

    /**
     * 已绑定到RecyclerView的项目个数。从LayoutManager中获取。
     */
    private int mTotalItemCount;

    private boolean mFetchingMore;

    private boolean mFetchMoreEnabled = false;

    private LoadMoreFlesh mLoadMoreFlesh;

    private OnFetchMoreListener mOnFetchMoreListener;

    public SkeletonAdapter(VhrFactory vhrFactory) {
        mFleshes = new ArrayList<>();
        mVhrFactory = vhrFactory;
    }

    /**
     *
     * @param vhrFactory 组持者工厂
     * @param onFetchMoreListener 加载更多监听
     */
    public SkeletonAdapter(VhrFactory vhrFactory, OnFetchMoreListener onFetchMoreListener) {
        this(vhrFactory);
        if (onFetchMoreListener != null) {
            mFetchMoreEnabled = true;
            mOnFetchMoreListener = onFetchMoreListener;
            mVhrFactory.addViewHolder(LoadMoreFleshVhr.class);
            mLoadMoreFlesh = new LoadMoreFlesh();
        }
    }

    public void setOnFleshStabbedListener(OnFleshStabbedListener onFleshStabbedListener) {
        mOnFleshStabbedListener = onFleshStabbedListener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new TitleSpanSizeLookup(mFleshes, gridLayoutManager.getSpanCount()));
        }
        if (mFetchMoreEnabled)
            addOnScrollListenerToRecyclerView(recyclerView);
    }

    /**
     * 添加滑动监听
     * @param recyclerView 回收器组件
     */
    private void addOnScrollListenerToRecyclerView(RecyclerView recyclerView) {
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mFetchMoreEnabled) return;
                mTotalItemCount = linearLayoutManager.getItemCount();
                mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!mFetchingMore && mTotalItemCount <= (mLastVisibleItem + mVisibleThreshold)) {
                    if (mOnFetchMoreListener != null) {
                        addItem(mLoadMoreFlesh);
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                notifyItemInserted(getItemCount() - 1);
                            }
                        });
                        mFetchingMore = true;
                        mOnFetchMoreListener.onLoadMore();
                    }
                    mFetchingMore = true;
                }
            }
        });
    }

    @Override
    public AbsSkeletonVhr<Flesh> onCreateViewHolder(ViewGroup parent, int viewType) {
        return mVhrFactory.create(parent, viewType);
    }

    @Override
    public void onBindViewHolder(final AbsSkeletonVhr<Flesh> holder, int position) {
        holder.bind(mFleshes.get(position));
        if (mOnFleshStabbedListener != null) {
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Flesh flesh = mFleshes.get(holder.getAdapterPosition());
                    mOnFleshStabbedListener.onStabbed(flesh);
                }
            });
        }
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
        mFleshes.remove(i);
    }

    /**
     * 删除加载更多项
     */
    public void removeFetching() {
        if (mFleshes.size() > 0 && mFleshes.remove(mFleshes.size() - 1) != mLoadMoreFlesh) {
            throw new IllegalStateException("Last item is not fetching more item,please check!");
        }
    }

    public void clearItems() {
        mFleshes.clear();
    }

    public void setFleshes(List<? extends Flesh> fleshes) {
        this.mFleshes.clear();
        this.mFleshes.addAll(fleshes);
        notifyDataSetChanged();
    }

    public void addItems(Collection<? extends Flesh> fleshes) {
        mFleshes.addAll(fleshes);
    }

    public void disableFetchMore() {
        if (mFetchMoreEnabled) {
            mFetchMoreEnabled = false;
        }
    }

    public void enableFetchMore() {
        if (!mFetchMoreEnabled) {
            mFetchMoreEnabled = true;
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
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

    public void setFetchingMore(boolean isFetchingMore) {
        mFetchingMore = isFetchingMore;
    }

    /**
     * 被点击事件
     */
    public interface OnFleshStabbedListener{
        void onStabbed(Flesh flesh);
    }

    public interface OnFetchMoreListener {
        void onLoadMore();
    }
}
