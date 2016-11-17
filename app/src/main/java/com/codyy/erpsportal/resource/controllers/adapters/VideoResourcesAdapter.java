package com.codyy.erpsportal.resource.controllers.adapters;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.resource.controllers.viewholders.VideoViewHolder;
import com.codyy.erpsportal.resource.models.entities.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujiajia on 2015/12/21.
 */
public class VideoResourcesAdapter extends RecyclerView.Adapter {

    private ViewHolderCreator<?> mViewHolderCreator;

    private final static int TYPE_VIDEO = 1;

    private final static int TYPE_LAST = 0;

    private List<Video> mList;

    private int mVisibleThreshold = 2;

    private int mLastVisibleItem;

    /**
     * 已绑定到RecyclerView的项目个数。从LayoutManager中获取。
     */
    private int mTotalItemCount;

    private boolean mLoading;

    private boolean mLoadMoreEnabled = true;

    private OnLoadMoreListener mOnLoadMoreListener;

    private RecyclerView mRecyclerView;

    public VideoResourcesAdapter(RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
        mRecyclerView = recyclerView;
        mViewHolderCreator = new EasyVhrCreator<>(VideoViewHolder.class);
        mOnLoadMoreListener = onLoadMoreListener;
        if (updateSpanSizeLookup()) {
            addOnScrollListenerToRecyclerView(recyclerView);
        } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            addOnScrollListenerToRecyclerView(recyclerView);
        }
    }

    /**
     *
     * @return whether updated
     */
    public boolean updateSpanSizeLookup() {
        if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
            final int spanCount = gridLayoutManager.getSpanCount();
            gridLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (mList.get(position) == null) {
                        return spanCount;
                    }
                    return 1;
                }
            });
            return true;
        } else {
            return false;
        }
    }

    /**
     * 添加滑动监听
     * @param recyclerView 回收者组件
     */
    private void addOnScrollListenerToRecyclerView(RecyclerView recyclerView) {
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mLoadMoreEnabled) return;
                mTotalItemCount = linearLayoutManager.getItemCount();
                mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!mLoading && mTotalItemCount <= (mLastVisibleItem + mVisibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        addItem(null);
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                notifyItemInserted(getItemCount() - 1);
                            }
                        });
                        mLoading = true;
                        mOnLoadMoreListener.onLoadMore();
                    }
                    mLoading = true;
                }
            }
        });
    }

    public void setLoading(boolean isLoading) {
        mLoading = isLoading;
    }

    public void setData(List<Video> infoList) {
        mList = infoList;
    }

    public void addData(List<Video> infoList) {
        if (mList != null) {
            mList.addAll(infoList);
        } else {
            setData(infoList);
        }
    }

    public void addItem(Video info) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.add(info);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_VIDEO) {
            return mViewHolderCreator.createViewHolder(parent);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_last, parent, false);
            return new LastItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof RecyclerViewHolder) {
            RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
            viewHolder.setDataToView(mList, position);
        } else {
            LastItemHolder itemHolder = (LastItemHolder) holder;
            itemHolder.setDataToView(mLoadMoreEnabled);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Video video = mList.get(position);
        if (video == null) return TYPE_LAST;
        return TYPE_VIDEO;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void disableLoadMore() {
        if (mLoadMoreEnabled) {
            mLoadMoreEnabled = false;
        }
    }

    public void enableLoadMore() {
        if (!mLoadMoreEnabled) {
            mLoadMoreEnabled = true;
        }
    }

    public void removeItem(int i) {
        mList.remove(i);
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    /**
     * 最后一项，用于显示没有更多或加载数据ProgressBar
     */
    private static class LastItemHolder extends ViewHolder {

        private TextView mEmptyTv;

        private ProgressBar mLoadingPb;

        public LastItemHolder(View itemView) {
            super(itemView);
            mapFromView(itemView);
        }

        public void mapFromView(View view) {
            mEmptyTv = (TextView) view.findViewById(R.id.tv_empty);
            mLoadingPb = (ProgressBar) view.findViewById(R.id.pb_loading);
        }

        public void setDataToView(boolean loadMoreEnabled) {
            if (loadMoreEnabled) {
                mEmptyTv.setVisibility(View.GONE);
                mLoadingPb.setVisibility(View.VISIBLE);
            } else {
                mEmptyTv.setVisibility(View.VISIBLE);
                mLoadingPb.setVisibility(View.GONE);
            }
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
