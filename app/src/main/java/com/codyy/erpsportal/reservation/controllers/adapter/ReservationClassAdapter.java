package com.codyy.erpsportal.reservation.controllers.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.databinding.ItemReservationClassBinding;
import com.codyy.erpsportal.reservation.controllers.activities.ReservationClassDetailActivity;
import com.codyy.erpsportal.reservation.models.entities.ReservationClassItem;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by kmdai on 16-6-13.
 */
public class ReservationClassAdapter extends RefreshBaseAdapter<ReservationClassItem> {
    public ReservationClassAdapter(Context mContext) {
        super(mContext);
    }

    public ReservationClassAdapter(Context mContext, List<ReservationClassItem> mDatas) {
        super(mContext, mDatas);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder getHolderView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        switch (viewType) {
            case ReservationClassItem.TYPE_CONTENT:
                return new ItemHolder(ItemReservationClassBinding.inflate(inflater, parent, false));
        }
        return null;
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position, ReservationClassItem entity) {
        switch (getItemViewType(position)) {
            case ReservationClassItem.TYPE_CONTENT:
                ((ItemHolder) holder).setData(entity);
                break;
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        ItemReservationClassBinding mBinding;
        SimpleDraweeView mSimpleDraweeView;

        public ItemHolder(ItemReservationClassBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mSimpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.item_reservation_class_simpledrawee);
        }

        public void setData(ReservationClassItem entity) {
            mSimpleDraweeView.setImageURI(Uri.parse(entity.getHeadPic()));
            mBinding.setItem(entity);
            mBinding.executePendingBindings();
        }
    }
}
