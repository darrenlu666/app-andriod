package com.codyy.erpsportal.commons.controllers.viewholders.homepage;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.customized.HistoryClass;
import com.codyy.erpsportal.commons.models.entities.mainpage.GroupLive;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页-集团校－直播item
 * Created by poe on 16-6-1.
 */

public class MainLiveViewHolder extends BaseRecyclerViewHolder<GroupLive> {


    @Bind(R.id.sdv)SimpleDraweeView mSDV;
    @Bind(R.id.tv_school)TextView mSchoolTextView;
    @Bind(R.id.tv_level_subject_teacher)TextView mLevelSTTextView;

    public MainLiveViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_customized_history_class;
    }

    @Override
    public void setData(int position, GroupLive data) {
        if(null == data) return;
        // 16-6-1 set data content .
        ImageFetcher.getInstance(mSchoolTextView.getContext()).fetchSmall(mSDV,data.getImagePath());
        mSchoolTextView.setText(data.getRealName());
        StringBuilder sb = new StringBuilder();
        if(!TextUtils.isEmpty(data.getBaseClasslevelName())) sb.append(data.getBaseClasslevelName());
        if(!TextUtils.isEmpty(data.getBaseSubjectName())) sb.append("/"+data.getBaseSubjectName());
        mLevelSTTextView.setText(sb.toString());
    }
}
