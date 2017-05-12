package com.codyy.erpsportal.groups.controllers.viewholders;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 单个TextView .
 * Created by poe on 16-1-25.
 */
public  class SingleTextViewHolder extends BaseRecyclerViewHolder<String> {

    @Bind(R.id.content) TextView mContentTextView;

    public SingleTextViewHolder(View itemView ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_filter_simple_text;
    }

    @Override
    public void setData(int position , String data) {
        mCurrentPosition    =   position ;
        mData  = data ;
        mContentTextView.setText(data);
    }

}
