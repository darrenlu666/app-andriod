package com.codyy.erpsportal.groups.controllers.viewholders;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.filter.FilterEntity;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 筛选-左边子item
 * Created by poe on 16-1-25.
 */
public class FilterChoiceViewHolder extends BaseRecyclerViewHolder<FilterEntity> {

    @Bind(R.id.title) TextView mTextView;

    public FilterChoiceViewHolder(View itemView ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_live_left_filter;
    }

    @Override
    public void setData(int position , FilterEntity data) {
        mCurrentPosition    =   position;
        mData = data ;
        if(data != null ){
            if(data.isCheck()){
                mTextView.setTextColor(mTextView.getContext().getResources().getColor(R.color.green));
            }else{
                mTextView.setTextColor(mTextView.getContext().getResources().getColor(R.color.md_grey_700));
            }
            mTextView.setText(data.getName());
        }
    }
}
