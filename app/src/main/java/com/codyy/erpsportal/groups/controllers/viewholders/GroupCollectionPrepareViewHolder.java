package com.codyy.erpsportal.groups.controllers.viewholders;

import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.groups.models.entities.GroupPrepareLesson;
import com.facebook.drawee.view.SimpleDraweeView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 圈组空间-集体备课-item
 * Created by poe on 16-1-15.
 */
public class GroupCollectionPrepareViewHolder extends BaseRecyclerViewHolder<GroupPrepareLesson> {

    @Bind(R.id.sdv_pic)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv_name)TextView mTitleTextView;
    @Bind(R.id.et_desc)TextView mDescTextView;
    @Bind(R.id.tv_teacher_name)TextView mNameTextView;

    public GroupCollectionPrepareViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_group_blog_post;
    }

    @Override
    public void setData(int position,GroupPrepareLesson data) {
        mCurrentPosition    =    position ;
        mData   =   data ;
        //head pic
        ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView,data.getHeadPic());
        mNameTextView.setText(data.getRealName());
        mTitleTextView.setText(data.getMeetingTitle());
        mDescTextView.setText(data.getMeetingDescription());
    }
}
