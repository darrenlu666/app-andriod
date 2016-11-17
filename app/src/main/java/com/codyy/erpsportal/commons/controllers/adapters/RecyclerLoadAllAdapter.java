package com.codyy.erpsportal.commons.controllers.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eachann on 2016/01/13.
 */
public class RecyclerLoadAllAdapter<T, VH extends RecyclerViewHolder<T>> extends RecyclerView.Adapter {


    private List<T> mList;


    private ViewHolderCreator<VH> mViewHolderCreator;

    public RecyclerLoadAllAdapter(RecyclerView recyclerView,ViewHolderCreator<VH> viewHolderCreator) {
        mViewHolderCreator = viewHolderCreator;
    }

    public void setData(List<T> infoList) {
        mList = infoList;
    }

    public void addData(List<T> infoList) {
        if (mList != null) {
            mList.addAll(infoList);
        } else {
            setData(infoList);
        }
    }

    public void addItem(T info) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.add(info);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return mViewHolderCreator.createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof RecyclerViewHolder) {
            VH viewHolder = (VH) holder;
            T item = mList.get(position);
            viewHolder.setDataToView(item);
        }
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    public void removeItem(int i) {
        mList.remove(i);
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

}
