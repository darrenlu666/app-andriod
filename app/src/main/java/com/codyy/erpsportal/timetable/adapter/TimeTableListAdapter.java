package com.codyy.erpsportal.timetable.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.databinding.TimetableListItemBinding;
import com.codyy.erpsportal.commons.models.entities.TimeTableListContent;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by kmdai on 16-7-1.
 */
public class TimeTableListAdapter extends RefreshBaseAdapter<TimeTableListContent> {
    public TimeTableListAdapter(Context mContext) {
        super(mContext);
    }

    public TimeTableListAdapter(Context mContext, List<TimeTableListContent> mDatas) {
        super(mContext, mDatas);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder getHolderView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (viewType == TimeTableListContent.TYPE_ITEM) {
            return new TimeTableItemViewHolder(TimetableListItemBinding.inflate(inflater, parent, false));
        }
        return null;
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position, TimeTableListContent entity) {
        if (getItemViewType(position) == TimeTableListContent.TYPE_ITEM) {
            ((TimeTableItemViewHolder) holder).bindData(entity);
        }
    }

    class TimeTableItemViewHolder extends RecyclerView.ViewHolder {
        TimetableListItemBinding mTimetableListItemBinding;
        SimpleDraweeView mSimpleDraweeView;

        public TimeTableItemViewHolder(TimetableListItemBinding binding) {
            super(binding.getRoot());
            mSimpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.simledraweeview);
            mTimetableListItemBinding = binding;
        }

        public void bindData(TimeTableListContent entity) {
            mSimpleDraweeView.setImageURI(Uri.parse(entity.getHeadPic()));
            mTimetableListItemBinding.setItem(entity);
            mTimetableListItemBinding.executePendingBindings();
        }
    }
}
