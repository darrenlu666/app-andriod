package com.codyy.erpsportal.groups.controllers.viewholders;

import android.graphics.drawable.Animatable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.blog.BlogPost;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UriUtils;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 频道页-圈组-item
 * Created by poe on 16-1-15.
 */
public class ChannelBlogViewHolder extends BaseRecyclerViewHolder<BlogPost> {

    public static final int ITEM_TYPE_RECOMMEND = 0x01 ;
    public static final int ITEM_TYPE_HOT = 0x02 ;
    public static final int ITEM_TYPE_ALL = 0x03;
    public static final int ITEM_TYPE_TOP = 0x04;

    @Bind(R.id.sdv_pic)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv_name)TextView mTitleTextView;
    @Bind(R.id.et_desc)TextView mDescTextView;

    public ChannelBlogViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_channel_blog_post;
    }

    @Override
    public void setData(int position,BlogPost data) {
        mCurrentPosition    =   position ;
        mData   =   data ;

        mTitleTextView.setText(Html.fromHtml(UIUtils.filterCharacter(data.getBlogTitle())));
        mDescTextView.setText(data.getBlogDesc());
        //head pic
//        ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView,data.getBlogPicture());
        mSimpleDraweeView.setController(Fresco.newDraweeControllerBuilder().setControllerListener(new ControllerListener<ImageInfo>() {
            @Override
            public void onSubmit(String id, Object callerContext) {

            }

            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {

            }

            @Override
            public void onIntermediateImageSet(String id, ImageInfo imageInfo) {

            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {

            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                mSimpleDraweeView.setImageResource(R.drawable.default_person_head);
            }

            @Override
            public void onRelease(String id) {

            }
        }).setUri(UriUtils.buildSmallImageUrl(data.getBlogPicture()))
          .build());

    }
}
