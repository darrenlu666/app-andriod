package com.codyy.erpsportal.resource.controllers.adapters;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsVhrCreator;
import com.codyy.erpsportal.resource.controllers.viewholders.AudioViewHolder;
import com.codyy.erpsportal.resource.controllers.viewholders.DocumentViewHolder;
import com.codyy.erpsportal.resource.controllers.viewholders.ImageViewHolder;
import com.codyy.erpsportal.resource.controllers.viewholders.VideoViewHolder;
import com.codyy.erpsportal.resource.models.entities.Audio;
import com.codyy.erpsportal.resource.models.entities.Document;
import com.codyy.erpsportal.resource.models.entities.Image;
import com.codyy.erpsportal.resource.models.entities.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujiajia on 2015/12/21.
 */
public class ResourcesAdapter<VH extends RecyclerViewHolder<?>> extends RecyclerView.Adapter {

    private SparseArray<AbsVhrCreator<?>> mSparseArray;

    private final static int TYPE_VIDEO = 1;

    private final static int TYPE_AUDIO = 2;

    private final static int TYPE_DOCUMENT = 3;

    private final static int TYPE_IMAGE = 4;

    private final static int TYPE_ALBUM = 5;

    private final static int TYPE_LAST = 0;

    private List<Object> mList;

    private int mVisibleThreshold = 2;

    private int mLastVisibleItem;

    /**
     * 已绑定到RecyclerView的项目个数。从LayoutManager中获取。
     */
    private int mTotalItemCount;

    private boolean mLoading;

    private boolean mLoadMoreEnabled = true;

    private OnLoadMoreListener mOnLoadMoreListener;

    private OnItemClickListener mOnItemClickListener;

    private RecyclerView mRecyclerView;

    public ResourcesAdapter(RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
        mRecyclerView = recyclerView;
        mSparseArray = new SparseArray<>();
        addTypes();
        mOnLoadMoreListener = onLoadMoreListener;
        if (updateSpanSizeLookup()) {
            addOnScrollListenerToRecyclerView();
        } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            addOnScrollListenerToRecyclerView();
        }
    }

    private void addTypes() {
        mSparseArray.put(TYPE_VIDEO, new EasyVhrCreator<>(VideoViewHolder.class));
        mSparseArray.put(TYPE_DOCUMENT, new EasyVhrCreator<>(DocumentViewHolder.class));
        mSparseArray.put(TYPE_IMAGE, new EasyVhrCreator<>(ImageViewHolder.class));
        mSparseArray.put(TYPE_AUDIO, new EasyVhrCreator<>(AudioViewHolder.class));
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
                    Object item = mList.get(position);
                    if (item == null || item instanceof Audio) {
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
     */
    private void addOnScrollListenerToRecyclerView() {
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    public void setData(List<Object> infoList) {
        mList = infoList;
    }

    public List<Object> getData() {
        return mList;
    }

    public void addData(List<Object> infoList) {
        if (mList != null) {
            mList.addAll(infoList);
        } else {
            setData(infoList);
        }
    }

    public void addItem(Object info) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.add(info);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != TYPE_LAST) {
            return mSparseArray.get(viewType).createViewHolder(parent);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_last, parent, false);
            return new LastItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof RecyclerViewHolder) {
            RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
            addItemViewClickListener(holder.itemView, position);
            viewHolder.setDataToView(mList, position);
        } else {
            LastItemHolder itemHolder = (LastItemHolder) holder;
            itemHolder.setDataToView(mLoadMoreEnabled);
        }
    }

    private void addItemViewClickListener(View itemView, final int position) {
        if (mOnItemClickListener != null) {
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object object = mList.get(position);
        if (object == null) return TYPE_LAST;
        if (object instanceof Video){
            return TYPE_VIDEO;
        } else if (object instanceof Audio) {
            return TYPE_AUDIO;
        } else if (object instanceof Document) {
            return TYPE_DOCUMENT;
        } else if (object instanceof Image) {
            return TYPE_IMAGE;
        } else {
            return TYPE_ALBUM;
        }
    }

    public Object getItem(int position) {
        return mList.get(position);
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

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
