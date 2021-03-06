package com.codyy.erpsportal.repairs.controllers.adapters;

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
import com.codyy.erpsportal.repairs.controllers.viewholders.InquiryReplyVh;
import com.codyy.erpsportal.repairs.controllers.viewholders.InquiryVh;
import com.codyy.erpsportal.repairs.models.entities.InquiryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 有加载更多的RecyclerView的适配器
 * Created by gujiajia on 2015/12/21.
 */
public class InquiriesAdapter extends RecyclerView.Adapter {

    private final static int TYPE_INQUIRY = 0;

    private final static int TYPE_REPLY = 1;

    private final static int TYPE_LAST = -1;

    protected List<InquiryItem> mList;

    private int mVisibleThreshold = 2;

    private int mLastVisibleItem;

    /**
     * 已绑定到RecyclerView的项目个数。从LayoutManager中获取。
     */
    private int mTotalItemCount;

    private boolean mLoading;

    protected boolean mLoadMoreEnabled = true;

    private OnLoadMoreListener mOnLoadMoreListener;

    private SparseArray<AbsVhrCreator<?>> mVhCreators;

    private RecyclerView mRecyclerView;

    protected OnItemClickListener<InquiryItem> mOnItemClickListener;

    public InquiriesAdapter(RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
        mVhCreators = new SparseArray<>();
        mVhCreators.put(TYPE_INQUIRY, new EasyVhrCreator<>(InquiryVh.class));
        mVhCreators.put(TYPE_REPLY, new EasyVhrCreator<>(InquiryReplyVh.class));
        mRecyclerView = recyclerView;
        mOnLoadMoreListener = onLoadMoreListener;
        if (updateSpanSizeLookup()) {
            addOnScrollListenerToRecyclerView(recyclerView);
        } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            addOnScrollListenerToRecyclerView(recyclerView);
        }
        mRecyclerView.setAdapter(this);
    }

    /**
     * 更新跨度抬头
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
     * @param recyclerView 回收器组件
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

    public void setData(List<InquiryItem> infoList) {
        mList = infoList;
    }

    /**
     * 增加一个列表的数据项
     * @param infoList 数据项列表
     */
    public void addData(List<InquiryItem> infoList) {
        if (mList != null) {
            mList.addAll(infoList);
        } else {
            setData(infoList);
        }
    }

    /**
     * 添加项
     * @param info 项
     */
    public void addItem(InquiryItem info) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.add(info);
    }

    /**
     * 根据位置获取项数据
     * @param position 位置
     * @return 项数据
     */
    public InquiryItem obtainItem(int position) {
        if (mList == null || position < 0 || position >= mList.size()) {
            return null;
        }
        return mList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_INQUIRY) {
            return mVhCreators.get(TYPE_INQUIRY).createViewHolder(parent);
        } else if (viewType == TYPE_REPLY) {
            return mVhCreators.get(TYPE_REPLY).createViewHolder(parent);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_last, parent, false);
            return new LastItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder instanceof RecyclerViewHolder) {
            RecyclerViewHolder<InquiryItem> viewHolder = (RecyclerViewHolder<InquiryItem>) holder;
            viewHolder.setDataToView(mList, position);
            addItemClickListener(holder);
        } else {
            LastItemHolder itemHolder = (LastItemHolder) holder;
            itemHolder.setDataToView(mLoadMoreEnabled);
        }
    }

    /**
     * 添加项点击事件
     * @param holder 组持者
     */
    protected void addItemClickListener(final ViewHolder holder) {
        if (mOnItemClickListener != null) {//如果有项点击回调，添加项点击事件来回调
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(position, mList.get(position));
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = mList.get(position);
        if (item == null) {
            return TYPE_LAST;
        } else {
            InquiryItem inquiryItem = (InquiryItem) item;
            if (inquiryItem.isReply()) {
                return TYPE_REPLY;
            } else {
                return TYPE_INQUIRY;
            }
        }
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
        if (mList != null) {
            mList.remove(i);
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void setOnItemClickListener(OnItemClickListener<InquiryItem> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * 最后一项，用于显示没有更多或加载数据ProgressBar
     */
    protected static class LastItemHolder extends ViewHolder {

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

    public interface OnItemClickListener<DataT>{
        void onItemClick(int position, DataT item);
    }
}
