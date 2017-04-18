package com.codyy.erpsportal.commons.common;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codyy.erpsportal.commons.utils.Cog;

import java.util.Objects;

/**
 * 监听recyclerView滚动到底部
 * Created by poe on 16-1-19.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = "EndlessRecyclerOnScrollListener";
    /**
     * 两次加载更多的最小间隔时间
     **/
    private static final long MIN_LOAD_PERIOD = 1 * 1000;
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
        if (null == mLinearLayoutManager) return;
        mVisibleItemCount = recyclerView.getChildCount();
        mTotalItemCount = mLinearLayoutManager.getItemCount();
        mFirstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();


        if (mPreviousTotal <= 0) {
            mPreviousTotal = mTotalItemCount;
        }
        //判断加载完成了...
//        if (mLoading) {
             if (mTotalItemCount > mPreviousTotal) {
                Cog.i(TAG, "mTotalItemCount > mPreviousTotal set loading false !");
                Cog.i(TAG, "mVisibleItemCount: " + mVisibleItemCount + " mTotalItemCount: " + mTotalItemCount + " mFirstVisibleItem: " + mFirstVisibleItem);
                mLoading = false;
                mPreviousTotal = mTotalItemCount;
            } else if (mTotalItemCount < mPreviousTotal) {
                Cog.i(TAG, "refresh set loading false !");
                Cog.i(TAG, "mVisibleItemCount: " + mVisibleItemCount + " mTotalItemCount: " + mTotalItemCount + " mFirstVisibleItem: " + mFirstVisibleItem);
                //refresh .
                mCurrentPage = 1;
                mLoading = false;
                mPreviousTotal = mTotalItemCount;
            }
//        }
        Cog.i(TAG, "mPreviousTotal: " + mPreviousTotal + "mVisibleItemCount: " + mVisibleItemCount + " mTotalItemCount: " + mTotalItemCount + " mFirstVisibleItem: " + mFirstVisibleItem + " mLoading :" + mLoading);
        if (!mLoading && mTotalItemCount > mVisibleItemCount && (mFirstVisibleItem + mVisibleThreshold) > (mTotalItemCount - mVisibleItemCount)) {
            // End has been reached
            Cog.i(TAG, "mLoading state : " + mLoading);
            // Do something
            mLoading = true;
            mCurrentPage++;
            Cog.i(TAG, "reach end pageIndex: " + mCurrentPage);
            onLoadMore(mCurrentPage);
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
     *
     * @param loading default: false 触底加载 true: 不加载
     */
    public void setLoading(boolean loading) {
        mLoading = loading;
    }

    /**
     * 初始化
     */
    public void initState(){
        Cog.i(TAG," initState ~~!!!!!!!!!!!!!!!!!");
        mLoading = false;
        mCurrentPage = 1;
        mPreviousTotal = mTotalItemCount = 0;
    }
    public  void loadMoreFailed(){
        Cog.i(TAG," loadMoreFailed ~~!!!!!!!!!!!!!!!!!");
        if(mCurrentPage > 1){
            mCurrentPage -- ;
        }
        setLoading(false);
    };

    public abstract void onLoadMore(int current_page);
}