package com.codyy.erpsportal.groups.controllers.viewholders;

import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.widgets.MarqueeTextView;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 圈组空间-公告-item
 * Created by poe on 16-1-15.
 */
public class GroupAnnounceViewHolder extends BaseRecyclerViewHolder<BaseTitleItemBar> {

    @Bind(R.id.tv_content)MarqueeTextView mContentTextView;

    public GroupAnnounceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_group_space_announce;
    }

    @Override
    public void setData(int position, BaseTitleItemBar data) {
        mCurrentPosition    =   position;
        mData   =   data ;
        //head pic
        if(null != data && null != data.getBaseTitle()){
            mContentTextView.setText(data.getBaseTitle().trim());
        }
    }
}
