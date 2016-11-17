package com.codyy.erpsportal.commons.controllers.viewholders.customized;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.entities.customized.LivingClass;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页-专递课堂-正在直播item
 * Created by poe on 16-6-1.
 */

public class LivingClassViewHolder extends BaseRecyclerViewHolder<LivingClass> {
    /**
     * 正在直播
     */
    public static final int ITEM_TYPE_LIVING =0x001;
    /**
     * 未开始 直播
     */
    public static final int ITEM_TYPE_LIVING_PREPARE =0x002;

    @Bind(R.id.tv_name)TextView mTitleTextView;
    @Bind(R.id.tv_level_subject_teacher)TextView mLevelSTTextView;//年纪/学科/老师
    @Bind(R.id.tv_state)TextView mStateTextView;//直播状态
    @Bind(R.id.tv_start_time)TextView mStartTimeTextView;//开始时间

    public LivingClassViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_channel_interact_live;
    }

    @Override
    public void setData(int position, LivingClass data) {
        mCurrentPosition    =   position;
        mData   =   data;
        if(null == data) return;
        mTitleTextView.setText(data.getSchoolName());
        mLevelSTTextView.setText(data.getClasslevelName()+"/"+data.getSubjectName()+"/"+data.getTeacherName());
//        mStartTimeTextView.setText(DateUtil.getDateStr(data.getStartTime(),"HH:mm"));
        mStartTimeTextView.setText(data.getStartTime());
    }
}
