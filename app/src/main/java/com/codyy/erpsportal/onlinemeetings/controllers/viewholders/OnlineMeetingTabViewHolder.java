package com.codyy.erpsportal.onlinemeetings.controllers.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;
import com.codyy.erpsportal.onlinemeetings.models.entities.TabInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 申请发言 viewHolder
 * Created by poe on 15-7-21.
 */
public class OnlineMeetingTabViewHolder extends AbsViewHolder<TabInfo> {

    @Bind(R.id.tv_tab_indicator) TextView mTitleTextView;
    @Bind(R.id.img_tab_indicator)ImageView mIconImageVie;

    public OnlineMeetingTabViewHolder(View view) {
        super(view);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.tab_indicator_online;
    }

    @Override
    public void mapFromView(View view) {
        //do inflate by butterknife in construct function of super .
        ButterKnife.bind(this, view);
    }

    @Override
    public void setDataToView(TabInfo data, Context context) {
        mTitleTextView.setText(data.getTabTitle());
        mIconImageVie.setImageResource(data.getTabIcon());
    }
}
