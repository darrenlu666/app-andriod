package com.codyy.erpsportal.groups.controllers.viewholders;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 单个灰底黑字title（圈组成员）
 * Created by poe on 16-1-15.
 */
public class GroupMemberTitleViewHolder extends BaseRecyclerViewHolder<BaseTitleItemBar>{

    @Bind(R.id.tv_title)TextView mTitleTextView;

    public GroupMemberTitleViewHolder(View itemView ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_simple_black_text;
    }

    @Override
    public void setData(int position,BaseTitleItemBar data) {
        mCurrentPosition    =   position ;
        mData   =   data ;
        mTitleTextView.setText(data.getBaseTitle());
    }
}
