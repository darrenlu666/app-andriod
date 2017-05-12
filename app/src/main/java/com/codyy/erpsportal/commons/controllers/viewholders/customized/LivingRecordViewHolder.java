package com.codyy.erpsportal.commons.controllers.viewholders.customized;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.EmumIndex;
import com.codyy.erpsportal.commons.models.entities.customized.LivingRecordLesson;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页-专递课堂-正在直播item
 * Created by poe on 16-6-1.
 */

public class LivingRecordViewHolder extends BaseRecyclerViewHolder<LivingRecordLesson> {
    /**
     * 正在直播
     */
    public static final int ITEM_TYPE_LIVING =0x001;
    /**
     * 推荐课程
     */
    public static final int ITEM_TYPE_RECOMMEND =0x002;
    @Bind(R.id.tv_period)TextView mPeriodTextView;
    @Bind(R.id.tv_name)TextView mTitleTextView;
    @Bind(R.id.tv_level_subject_teacher)TextView mLevelSTTextView;//年纪/学科/老师

    public LivingRecordViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_channel_live_record;
    }

    @Override
    public void setData(int position, LivingRecordLesson data) {
        mCurrentPosition    =   position;
        mData   =   data;
        if(null == data) return;
        mPeriodTextView.setText(TextUtils.isEmpty(data.getSession())?"一": EmumIndex.getIndex(Integer.valueOf(data.getSession())));
        mTitleTextView.setText(data.getSchoolName());
        mLevelSTTextView.setText(data.getClasslevelName()+"/"+data.getSubjectName()+"/"+data.getTeacherName());
    }
}
