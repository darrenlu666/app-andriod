package com.codyy.erpsportal.repairs.controllers.adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator;
import com.codyy.erpsportal.repairs.controllers.viewholders.CheckedTextViewHolder;
import com.codyy.erpsportal.repairs.models.entities.RepairFilterItem;

import java.util.List;

/**
 * 选中文本适配器
 * Created by gujiajia on 2017/3/22.
 */

public class CheckedTextAdapter extends Adapter<CheckedTextViewHolder> {

    private OnItemClickListener mOnItemClickListener;

    private int mSelectedPosition = 0;

    private List<? extends RepairFilterItem> mItemList;

    private EasyVhrCreator<CheckedTextViewHolder> mEasyVhrCreator;

    public CheckedTextAdapter() {
        mEasyVhrCreator = new EasyVhrCreator<>(CheckedTextViewHolder.class);
    }

    public CheckedTextAdapter(List<? extends RepairFilterItem> itemList) {
        this();
        mItemList = itemList;
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public CheckedTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mEasyVhrCreator.createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(final CheckedTextViewHolder holder, int position) {
        final RepairFilterItem item = mItemList.get(position);
        holder.setDataToView(item, mSelectedPosition == position);
        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickPosition = holder.getAdapterPosition();
                int originalSelectedPos = mSelectedPosition;
                mSelectedPosition = clickPosition;
                notifyItemChanged( originalSelectedPos);
                notifyItemChanged( mSelectedPosition);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(item, mSelectedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList == null? 0: mItemList.size();
    }

    public List<? extends RepairFilterItem> getItemList() {
        return mItemList;
    }

    public void setItemList(List<? extends RepairFilterItem> itemList) {
        mItemList = itemList;
    }

    public interface OnItemClickListener {
        <E extends RepairFilterItem> void onItemClick(E item, int position);
    }
}
