package com.codyy.erpsportal.resource.controllers.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator.LayoutId;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.codyy.erpsportal.resource.utils.DraweeViewUtils;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 频道页资源项item
 * Created by gujiajia on 2015/8/6.
 */
@LayoutId(R.layout.item_resource)
public class ResourceViewHolder extends RecyclerView.ViewHolder {

    private View mContentView;

    private TextView mTitleTv;

    private SimpleDraweeView mIconDv;

    public ResourceViewHolder(View itemView) {
        super(itemView);
        mContentView = itemView;
        mTitleTv = (TextView) mContentView.findViewById(R.id.title);
        mIconDv = (SimpleDraweeView) mContentView.findViewById(R.id.res_icon);
    }

    public void bindItem(Resource resource) {
        mTitleTv.setText(resource.getTitle());
        DraweeViewUtils.setPlaceHolderByResType(mIconDv, resource.getType());
        ImageFetcher.getInstance(mIconDv.getContext()).fetchSmall(mIconDv, resource.getIconUrl());
        Resource.addGotoResDetailsClickListener(mContentView, resource);
    }
}
