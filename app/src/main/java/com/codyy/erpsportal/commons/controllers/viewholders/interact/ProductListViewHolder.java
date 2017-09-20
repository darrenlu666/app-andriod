package com.codyy.erpsportal.commons.controllers.viewholders.interact;

import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 集体备课/-详情-备课成果/相关文档
 * Created by poe on 16-6-23.
 */
public class ProductListViewHolder extends BaseRecyclerViewHolder<Object> {

    @Bind(R.id.tv_resource_name) TextView mSchoolTextView;

    public ProductListViewHolder(View itemView ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_resource_prepare_lseeons;
    }

    @Override
    public void setData(int position, Object data) {
        mCurrentPosition = position;
        mData   =   data;
        mSchoolTextView.setText((String) data);
    }
}
