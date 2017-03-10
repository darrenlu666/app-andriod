package com.codyy.erpsportal.resource.controllers.viewholders;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.resource.models.entities.Image;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;

/**
 * 图片资源项
 * Created by gujiajia on 2016/6/14.
 */
@LayoutId(R.layout.item_resource_image)
public class ImageViewHolder extends BindingRvHolder<Image> {

    @Bind(R.id.res_icon)
    SimpleDraweeView mThumbDv;

    @Bind(R.id.title)
    TextView mNameTv;

    @Bind(R.id.tv_view_count)
    TextView mViewCountTv;

    public ImageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataToView(Image data) {
        ImageFetcher.getInstance(mThumbDv).fetchSmall(mThumbDv, data.getThumbUrl());
        mNameTv.setText(data.getName());
        mViewCountTv.setText(data.getViewCount() + "");
    }
}
