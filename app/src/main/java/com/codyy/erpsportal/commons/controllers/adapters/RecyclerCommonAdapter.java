package com.codyy.erpsportal.commons.controllers.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;

import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerCommonViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;

/**
 * 需要统一信息来变化所有item的适配器
 * Created by gujiajia on 2015/12/21.
 */
public class RecyclerCommonAdapter<T, VH extends RecyclerViewHolder<T>, INFO> extends RecyclerAdapter <T, VH> {

    private INFO mInfo;

    public RecyclerCommonAdapter(RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener, ViewHolderCreator<VH> viewHolderCreator) {
        super(recyclerView, onLoadMoreListener, viewHolderCreator);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof RecyclerCommonViewHolder) {
            RecyclerCommonViewHolder<T> viewHolder = (RecyclerCommonViewHolder<T>) holder;
            viewHolder.setDataToView(mList, position, mInfo);
        } else if (holder instanceof RecyclerViewHolder){
            RecyclerViewHolder<T> viewHolder = (RecyclerViewHolder<T>) holder;
            viewHolder.setDataToView(mList, position);
        } else {
            LastItemHolder itemHolder = (LastItemHolder) holder;
            itemHolder.setDataToView(mLoadMoreEnabled);
        }
    }

    public INFO getInfo() {
        return mInfo;
    }

    public void setInfo(INFO info) {
        mInfo = info;
    }
}
