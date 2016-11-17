package com.codyy.erpsportal.commons.common;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * 监听recyclerView滚动到底部
 * Created by poe on 16-1-19.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = "EndlessRecyclerOnScrollListener";

    private int mPreviousTotal = 0; // The total number of items in the dataset after the last load
    private boolean mLoading = false;
    //list到达 最后一个item的时候 触发加载
    private int mVisibleThreshold = 1;
    // The minimum amount of items to have below your current scroll position before loading more.
    int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;
    //默认第一页
    private int mCurrentPage = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if(null == mLinearLayoutManager ) return;
        mVisibleItemCount = recyclerView.getChildCount();
        mTotalItemCount = mLinearLayoutManager.getItemCount();
        mFirstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        //判断加载完成了...
        if (mLoading) {
            if(mPreviousTotal <= 0 ){
                mPreviousTotal = mTotalItemCount;
            }else if (mTotalItemCount > mPreviousTotal) {
                mLoading = false;
                mPreviousTotal = mTotalItemCount;
            }else if(mTotalItemCount < mPreviousTotal){
                //refresh .
                mCurrentPage = 1;
                mLoading = false;
                mPreviousTotal = mTotalItemCount ;
            }
        }
        //totalItemCount > visibleItemCount 超过一个页面才有加载更多
        if (!mLoading && mTotalItemCount > mVisibleItemCount && (mTotalItemCount - mVisibleItemCount) < (mFirstVisibleItem + mVisibleThreshold)) {
            // End has been reached
            // Do something
            mCurrentPage++;
            onLoadMore(mCurrentPage);
            mLoading = true;
        }
    }

    public int getVisibleThreshold() {
        return mVisibleThreshold;
    }

    public void setVisibleThreshold(int visibleThreshold) {
        this.mVisibleThreshold = visibleThreshold;
    }

    public int getCurrent_page() {
        return mCurrentPage;
    }

    public void setCurrent_page(int current_page) {
        this.mCurrentPage = current_page;
    }

    public abstract void onLoadMore(int current_page);
}