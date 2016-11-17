package com.codyy.erpsportal.commons.controllers.viewholders;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;

import butterknife.Bind;

/**
 * Created by gujiajia on 2016/7/14.
 */
public class MainTitleBarViewHolder extends BindingRvHolder<String> {

    @Bind(R.id.tv_bar_title)
    TextView mBarTitleTv;

    public MainTitleBarViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataToView(String titleName) {
        mBarTitleTv.setText(titleName);
    }
}
