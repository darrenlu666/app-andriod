package com.codyy.erpsportal.groups.controllers.viewholders;

import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.entities.filter.FilterEntity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 筛选-右边-单个item .
 * Created by poe on 16-1-25.
 */
public  class SimpleTextViewHolder extends BaseRecyclerViewHolder<FilterEntity> {

    @Bind(R.id.filter_item_view) RelativeLayout mContainer;
    @Bind(R.id.content) TextView mContentTextView;

    public SimpleTextViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_filter_simple_text;
    }

    @Override
    public void setData(int position , FilterEntity data) {
        mCurrentPosition    =   position ;
        mData  = data ;
        if (data.isCheck()) {
            mContentTextView.setTextColor(mContainer.getContext().getResources().getColor(R.color.green));
        } else {
            mContentTextView.setTextColor(Color.BLACK);
        }
        mContentTextView.setText(data.getName());
    }
}
