package com.codyy.erpsportal.county.controllers.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.TitleItemBar;
import com.codyy.erpsportal.county.controllers.activities.CountyClassDetailActivity;
import com.codyy.erpsportal.county.controllers.models.entities.CountyListItem;
import com.codyy.erpsportal.databinding.ItemContyListBinding;
import com.codyy.erpsportal.commons.models.entities.RefreshEntity;

import java.util.List;

/**
 * Created by kmdai on 16-6-6.
 */
public class CountyListAdapter extends RefreshBaseAdapter<RefreshEntity> {

    public CountyListAdapter(Context mContext) {
        super(mContext);
    }

    public CountyListAdapter(Context mContext, List<RefreshEntity> mDatas) {
        super(mContext, mDatas);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder getHolderView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        switch (viewType) {
            case CountyListItem.ITEM_TYPE_TITLE:
                TitleItemBar titleItemBar = new TitleItemBar(mContext);
                titleItemBar.setHasMore(false);
                titleItemBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(mContext, 40)));
                return new RecyclerView.ViewHolder(titleItemBar) {
                };
            case CountyListItem.ITEM_TYPE_COUNT:
                return new ContentHolder(ItemContyListBinding.inflate(inflater, parent, false));
        }
        return null;
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position, RefreshEntity entity) {
        switch (getItemViewType(position)) {
            case CountyListItem.ITEM_TYPE_TITLE:
                final CountyListItem countyListItem = (CountyListItem) entity;
                TitleItemBar titleItemBar = (TitleItemBar) holder.itemView;
                titleItemBar.setTitle(countyListItem.getSchoolName() + "(" + countyListItem.getMainClassRoomName() + ")");
                titleItemBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CountyClassDetailActivity.start(mContext,
                                countyListItem.getClsClassroomId(),
                                null,
                                countyListItem.getSchoolName() + "(" + countyListItem.getMainClassRoomName() + ")",
                                CountyClassDetailActivity.TYPE_MASTERCLASSROOM);
                    }
                });
                break;
            case CountyListItem.ITEM_TYPE_COUNT:
                ((ContentHolder) holder).setData(entity);
                break;
        }
    }

    class ContentHolder extends RecyclerView.ViewHolder {
        ItemContyListBinding mBinding;

        public ContentHolder(ItemContyListBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void setData(RefreshEntity entity) {
            mBinding.setItem((CountyListItem.ScheduleItem) entity);
            mBinding.executePendingBindings();
        }
    }
}
