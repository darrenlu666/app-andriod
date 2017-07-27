package com.codyy.erpsportal.commons.controllers.viewholders.onlineclass;

import android.view.View;
import android.widget.RelativeLayout;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.channels.SipCustomizedFragment;
import com.codyy.erpsportal.commons.models.entities.onlineclass.SipNetResearch;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.widgets.AspectRatioImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 学校排名-title（专递课堂)(sip)
 * Created by poe on 17-7-24.
 */
public class SchoolRankTitleViewHolder extends BaseRecyclerViewHolder<BaseTitleItemBar> {
    //没有数据提示
    @Bind(R.id.rlt_no_data)
    RelativeLayout mNoDataRlt;

    public SchoolRankTitleViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_rank_school_title;
    }


    @Override
    public void setData(int position, BaseTitleItemBar data) {
        //do nothing .
        mCurrentPosition = position;
        if(SipCustomizedFragment.TYPE_ITEM_VIEW_HOLDER_RANK_SCHOOL_HEADER == data.getBaseViewHoldType()){
            mNoDataRlt.setVisibility(View.GONE);
        }else if(SipCustomizedFragment.TYPE_ITEM_VIEW_HOLDER_RANK_SCHOOL_HEADER_NO_DATA == data.getBaseViewHoldType()){
            mNoDataRlt.setVisibility(View.VISIBLE);
        }
    }
}
