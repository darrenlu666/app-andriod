package com.codyy.erpsportal.groups.controllers.viewholders;

import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.blog.GroupBlogPost;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 频道页-圈组-item
 * Created by poe on 16-1-15.
 */
public class GroupBlogViewHolder extends BaseRecyclerViewHolder<GroupBlogPost> {

    @Bind(R.id.sdv_pic)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv_name)TextView mTitleTextView;
    @Bind(R.id.et_desc)TextView mDescTextView;
    @Bind(R.id.tv_teacher_name)TextView mNameTextView;

    public GroupBlogViewHolder(View itemView ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_group_blog_post;
    }

    @Override
    public void setData(int position,GroupBlogPost data) {
        mCurrentPosition    =   position ;
        mData   =   data ;
        //head pic
        ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView,data.getBlogPicture());
        mTitleTextView.setText(Html.fromHtml(UIUtils.filterCharacter(data.getBlogTitle())));
        mNameTextView.setText(data.getRealName());
        mDescTextView.setText(data.getTextContent());
    }
}
