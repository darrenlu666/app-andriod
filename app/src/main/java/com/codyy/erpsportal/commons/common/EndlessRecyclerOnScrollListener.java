package com.codyy.erpsportal.commons.common;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codyy.erpsportal.commons.utils.Cog;

/**
 * 监听recyclerView滚动到底部
 * Created by poe on 16-1-19.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = "EndlessRecyclerOnScrollListener";

    private int mPreviousTotal = 0; // The total number of items in the dataset after the last load
    private boolean mLoading = false;//是否有数据变化，只有当有新的数据变化发生时才会置为false.
    //list到达 最后一个item的时候 触发加载,居最后一个item为N个item时触发加载数据.
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
        //(mFirstVisibleItem + mVisibleThreshold)>(mTotalItemCount - mVisibleItemCount) 离底部最后一个时触发.
        if (!mLoading && mTotalItemCount > mVisibleItemCount && (mFirstVisibleItem + mVisibleThreshold)>(mTotalItemCount - mVisibleItemCount)) {
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

    public boolean isLoading() {
        return mLoading;
    }

    /**
     * 是否可以继续调用onLoadMore
     * @param loading default: false 触底加载 true: 不加载
     */
    public void setLoading(boolean loading) {
        mLoading = loading;
    }

    public abstract void onLoadMore(int current_page);
}