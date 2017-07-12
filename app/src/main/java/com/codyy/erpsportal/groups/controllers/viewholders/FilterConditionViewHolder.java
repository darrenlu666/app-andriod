package com.codyy.erpsportal.groups.controllers.viewholders;

import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.filter.FilterEntity;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 筛选-右边-条件筛选item
 * Created by poe on 16-1-25.
 */
public  class FilterConditionViewHolder extends BaseRecyclerViewHolder<FilterEntity> {

    @Bind(R.id.filter_item_view) RelativeLayout mContainer;
    @Bind(R.id.title) TextView mTitleTextView;
    @Bind(R.id.content) TextView mContentTextView;

    public FilterConditionViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_live_right_filter;
    }

    /*@OnClick(R.id.filter_item_view)
    void onClick(){
        if(null != mOnClickListener){
            mOnClickListener.onItemClicked(mCurrentPosition , mData );
        }
    }*/

    @Override
    public void setData(int position , FilterEntity data) {
        mCurrentPosition    =   position ;
        mData = data;
        if (data.isCheck()) {
            mContentTextView.setTextColor(mContainer.getContext().getResources().getColor(R.color.green));
        } else {
            mContentTextView.setTextColor(Color.BLACK);
        }
        mTitleTextView.setText(data.getLevelName());
        mContentTextView.setText(data.getName());
    }
}
