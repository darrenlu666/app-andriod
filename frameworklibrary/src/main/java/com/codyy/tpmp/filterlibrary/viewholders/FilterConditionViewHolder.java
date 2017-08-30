package com.codyy.tpmp.filterlibrary.viewholders;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codyy.tpmp.filterlibrary.R;
import com.codyy.tpmp.filterlibrary.entities.FilterCell;
import com.codyy.tpmp.filterlibrary.entities.FilterModule;

/**
 * 筛选-右边-条件筛选item
 * Created by poe on 16-1-25.
 */
public  class FilterConditionViewHolder extends BaseRecyclerViewHolder<FilterModule> {

    RelativeLayout mContainer;
    TextView mTitleTextView;
    TextView mContentTextView;

    public FilterConditionViewHolder(View itemView) {
        super(itemView);
        bindView(itemView);
    }

    private void bindView(View itemView) {
        mContainer = (RelativeLayout) itemView.findViewById(R.id.filter_item_view);
        mTitleTextView = (TextView) itemView.findViewById(R.id.title);
        mContentTextView = (TextView) itemView.findViewById(R.id.content);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_filter_right;
    }

    @Override
    public void setData(int position , FilterModule data) {
        mCurrentPosition    =   position ;
        mData = data;
        FilterCell fe = data.getData();
        if (data.isSelected()) {
            mContentTextView.setTextColor(ContextCompat.getColor(mContainer.getContext(),R.color.main_color));
        } else {
            mContentTextView.setTextColor(Color.BLACK);
        }
        mTitleTextView.setText(fe.getLevelName());
        mContentTextView.setText(fe.getName());
    }
}
