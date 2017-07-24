package com.codyy.erpsportal.commons.controllers.viewholders.customized;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.customized.HistoryClass;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页-专递课堂-课程回放item（sip）
 * Created by poe on 16-6-1.
 */

public class HistoryClassViewHolderSip extends BaseRecyclerViewHolder<HistoryClass> {

    @Bind(R.id.sdv)SimpleDraweeView mSDV;
    @Bind(R.id.tv_school)TextView mSchoolTextView;
    @Bind(R.id.tv_level_subject_teacher)TextView mLevelSTTextView;

    public HistoryClassViewHolderSip(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_customized_history_class;
    }

    @Override
    public void setData(int position, HistoryClass data) {
        mCurrentPosition = position;
        mData   =   data;
        if(null == data) return;
        // 16-6-1 set data content .
        ImageFetcher.getInstance(mSchoolTextView.getContext()).fetchSmall(mSDV,data.getThumb());
        mSchoolTextView.setText(data.getSchoolName());
        mLevelSTTextView.setText(data.getClasslevelName()+"/"+data.getSubjectName());
    }
}
