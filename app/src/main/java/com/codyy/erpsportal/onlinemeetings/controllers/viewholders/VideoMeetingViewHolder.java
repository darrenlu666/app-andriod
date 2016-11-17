package com.codyy.erpsportal.onlinemeetings.controllers.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;
import com.codyy.erpsportal.onlinemeetings.models.entities.VideoMeetingEntity;

/**
 * Created by ldh on 2015/8/17.
 */
public class VideoMeetingViewHolder extends AbsViewHolder<VideoMeetingEntity>{

    ImageView mHeaderImageView;
    TextView mContentTextView;
    TextView mPersonNameTextView;
    TextView mTimeLabelTextView;
    TextView mTimeTextView;

    @Override
    public int obtainLayoutId() {
        return R.layout.item_videomeeting;
    }

    @Override
    public void mapFromView(View view) {
        mHeaderImageView = (ImageView)view.findViewById(R.id.iv_videoImage);
        mContentTextView =(TextView) view.findViewById(R.id.tv_content);
        mPersonNameTextView = (TextView) view.findViewById(R.id.tv_personName);
        mTimeTextView = (TextView) view.findViewById(R.id.tv_start_time);
        mTimeLabelTextView = (TextView) view.findViewById(R.id.tv_timelabel);
    }

    @Override
    public void setDataToView(VideoMeetingEntity data, Context context) {

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
