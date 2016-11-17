package com.codyy.erpsportal.resource.controllers.viewholders;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator.LayoutId;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.resource.models.entities.Video;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;

/**
 * Created by gujiajia on 2016/6/14.
 */
@LayoutId(R.layout.item_resource_video)
public class VideoViewHolder extends BindingRvHolder<Video> {

    @Bind(R.id.title)
    TextView mNameTv;

    @Bind(R.id.res_icon)
    SimpleDraweeView mIconDv;

    @Bind(R.id.tv_view_count)
    TextView mViewCountTv;

    public VideoViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataToView(final Video video) {
        mNameTv.setText(video.getName());
        ImageFetcher.getInstance(mIconDv).fetchSmall(mIconDv, video.getThumbUrl());
        mViewCountTv.setText(video.getViewCount() + "");
//        JumpUtils.addGotoVideoDetailsClickListener(itemView, video.getId());
    }
}
