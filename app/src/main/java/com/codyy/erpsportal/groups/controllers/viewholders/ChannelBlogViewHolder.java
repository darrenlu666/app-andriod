package com.codyy.erpsportal.groups.controllers.viewholders;

import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.blog.BlogPost;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.facebook.drawee.view.SimpleDraweeView;
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
    @Bind(R.id.tv_desc)TextView mDescTextView;

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
        //head pic
        ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView,data.getBlogPicture());
        mTitleTextView.setText(Html.fromHtml(UIUtils.filterCharacter(data.getBlogTitle())));
        mDescTextView.setText(data.getBlogDesc());
    }
}
