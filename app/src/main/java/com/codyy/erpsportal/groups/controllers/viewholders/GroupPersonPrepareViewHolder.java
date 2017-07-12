package com.codyy.erpsportal.groups.controllers.viewholders;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.groups.models.entities.GroupPersonPrepare;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 圈组空间-个人备课-item
 * Created by poe on 16-1-15.
 */
public class GroupPersonPrepareViewHolder extends BaseRecyclerViewHolder<GroupPersonPrepare> {

    @Bind(R.id.sdv_pic)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv_name)TextView mTitleTextView;
    @Bind(R.id.et_desc)TextView mDescTextView;
    @Bind(R.id.rb_star)    RatingBar mRbStar;

    public GroupPersonPrepareViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_group_prepare_lession;
    }

    @Override
    public void setData(int position,GroupPersonPrepare data) {
        mCurrentPosition    =   position ;
        mData   =   data ;
        //head pic
        ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView,data.getHeadPic());
        mTitleTextView.setText(data.getTitle());
        mDescTextView.setText(data.getRealName());
        mRbStar.setProgress(Integer.valueOf(data.getAverageScore()));
    }
}
