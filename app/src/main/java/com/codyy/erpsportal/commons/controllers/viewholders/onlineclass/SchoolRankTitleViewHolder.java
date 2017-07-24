package com.codyy.erpsportal.commons.controllers.viewholders.onlineclass;

import android.view.View;
import android.widget.RelativeLayout;

import com.codyy.erpsportal.R;
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

    public SchoolRankTitleViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_rank_school_title;
    }


    @Override
    public void setData(int position, BaseTitleItemBar data) {
        //do nothing .
        mCurrentPosition = position;
    }
}
