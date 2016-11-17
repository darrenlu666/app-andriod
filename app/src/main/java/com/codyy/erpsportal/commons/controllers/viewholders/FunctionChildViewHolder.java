package com.codyy.erpsportal.commons.controllers.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.AppInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by poe on 15-7-21.
 */
public class FunctionChildViewHolder extends AbsViewHolder<AppInfo>{

    @Bind(R.id.tv_function_item) TextView mTitleTextView;
    @Bind(R.id.img_function_item)ImageView mIconImageVie;


    public FunctionChildViewHolder(View view) {
        super(view);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_frag_function_child;
    }

    @Override
    public void mapFromView(View view) {
        //do inflate by butterknife in construct function of super .
        ButterKnife.bind(this,view);
        mTitleTextView.setTextColor(EApplication.instance().getResources().getColor(R.color.md_green_A700));
    }

    @Override
    public void setDataToView(AppInfo data, Context context) {

        mTitleTextView.setText(data.getAppName());
        mIconImageVie.setImageResource(data.getIcon());

    }
}
