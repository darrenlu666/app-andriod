package com.codyy.erpsportal.onlinemeetings.controllers.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.onlinemeetings.models.entities.VideoMeetingEntity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ldh on 2015/8/17.
 * modified by poe on 2017/3/3 .
 */
public class VideoMeetingViewHolder extends BaseRecyclerViewHolder<VideoMeetingEntity> {

    @Bind(R.id.iv_videoImage) ImageView mHeaderImageView;
    @Bind(R.id.tv_content) TextView mContentTextView;
    @Bind(R.id.tv_personName) TextView mPersonNameTextView;
    @Bind(R.id.tv_start_time) TextView mTimeTextView;
    @Bind(R.id.tv_timelabel) TextView mTimeLabelTextView;

    public VideoMeetingViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_videomeeting;
    }

    @Override
    public void setData(int position, VideoMeetingEntity data) throws Throwable {
        if(data.getMeet_sate().equals("INIT")){
            mTimeLabelTextView.setText("预约开始时间：");
            mHeaderImageView.setBackgroundResource(R.drawable.unstart);
        }else if(data.getMeet_sate().equals("PROGRESS")){
            mTimeLabelTextView.setText("开始时间：");
            mHeaderImageView.setBackgroundResource(R.drawable.xxhdpi_assessmenting_);
        }else if(data.getMeet_sate().equals("END")){
            mTimeLabelTextView.setText("开始时间：");
            mHeaderImageView.setBackgroundResource(R.drawable.xxhdpi_end);
        }

        mContentTextView.setText(data.getTitle());
        mPersonNameTextView.setText(data.getSpeaker());
        mTimeTextView.setText(data.getBegin_time());
    }
}
