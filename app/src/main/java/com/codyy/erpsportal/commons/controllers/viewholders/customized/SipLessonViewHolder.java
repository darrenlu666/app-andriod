package com.codyy.erpsportal.commons.controllers.viewholders.customized;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.customized.HistoryClass;
import com.codyy.erpsportal.commons.models.entities.customized.SipLesson;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页-专递课堂-课程回放item
 * Created by poe on 16-6-1.
 */

public class SipLessonViewHolder extends BaseRecyclerViewHolder<SipLesson> {

    @Bind(R.id.sdv)SimpleDraweeView mSDV;
    @Bind(R.id.tv_school)TextView mSchoolTextView;
    @Bind(R.id.tv_level_subject_teacher)TextView mLevelSTTextView;

    public SipLessonViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_customized_history_class;
    }

    @Override
    public void setData(int position, SipLesson data) {
        mCurrentPosition = position;
        mData   =   data;
        if(null == data) return;
        // 16-6-1 set data content .
        ImageFetcher.getInstance(mSchoolTextView.getContext()).fetchSmall(mSDV,data.getThumb());
        mSchoolTextView.setText(data.getSchoolName());
        StringBuilder sb = new StringBuilder();
        if(!TextUtils.isEmpty(data.getClasslevelName())) sb.append(data.getClasslevelName());
        if(!TextUtils.isEmpty(data.getSubjectName())) sb.append("/"+data.getSubjectName());
        if(!TextUtils.isEmpty(data.getTeacherName())) sb.append("/"+data.getTeacherName());
        mLevelSTTextView.setText(sb.toString());
    }
}
