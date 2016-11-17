package com.codyy.erpsportal.commons.controllers.fragments;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;

import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;

/**
 * 集成下拉刷新与上拉更多的碎片
 * Created by gujiajia on 2015/12/22.
 */
public abstract class LoadMoreGridFragment<T, VH extends RecyclerViewHolder<T>> extends LoadMoreFragment<T, VH> {

    private GridLayoutManager mGridLayoutManager;
    private final static int LOAD_COUNT = 8;

    @Override
    protected LayoutManager createLayoutManager() {
        mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        return mGridLayoutManager;
    }

    /**
     * 改变列数
     *
     * @param spanCount
     */
    public void changeColumns(int spanCount) {
        mGridLayoutManager.setSpanCount(spanCount);
        mAdapter.updateSpanSizeLookup();
    }

    @Override
    public int obtainLoadCount() {
        return LOAD_COUNT;
    }
}
