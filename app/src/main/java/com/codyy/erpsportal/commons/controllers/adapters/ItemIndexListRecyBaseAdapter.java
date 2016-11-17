package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;

import java.util.List;

/**
 * 习题列表界面抽象adapter
 * Created by ldh on 2016/1/7.
 */
public abstract class ItemIndexListRecyBaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ItemIndexListRecyBaseAdapter.class.getSimpleName();
    protected List<T> mList;
    protected Context mContext;
    protected static final int TYPE_CONTENT = 0x001;
    protected static final int TYPE_TITLE = 0x002;

    public ItemIndexListRecyBaseAdapter(Context context, List<T> list) {
        mContext = context;
        mList = list;
    }

    public void setList(List<T> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return createViewHolder(LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), null), viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof RecyclerViewHolder) {
            RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
            T item = mList.get(position);
            viewHolder.setDataToView(item);
        }
    }

    /**
     * 获取布局文件
     *
     * @param viewType view类型
     * @return id
     */
    protected abstract
    @LayoutRes
    int getLayoutId(int viewType);

    /**
     * 创建viewholder
     *
     * @param view
     * @param viewType
     * @return
     */
    protected abstract RecyclerView.ViewHolder createViewHolder(View view, int viewType);

    /**
     * 获取布局类型
     *
     * @param position
     * @return
     */
    public abstract int getItemType(int position);

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getItemType(position);
    }

}
