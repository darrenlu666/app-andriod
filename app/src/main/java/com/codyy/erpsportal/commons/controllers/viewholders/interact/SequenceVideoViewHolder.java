package com.codyy.erpsportal.commons.controllers.viewholders.interact;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.entities.AssessmentDetails;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 互动听课-详情-视频列表ViewHolder
 * Created by poe on 16-6-23.
 */
public class SequenceVideoViewHolder extends BaseRecyclerViewHolder<AssessmentDetails.VideoId>{

    @Bind(R.id.tv_video_count) TextView mVideoNameTv;
    @Bind(R.id.ll_video_count_view)RelativeLayout mBackground;

    public SequenceVideoViewHolder(View itemView ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.horizontal_list_item_video_view;
    }

    /*@OnClick(R.id.ll_video_count_view)
    void onClick(){
        if(null != mOnClickListener) mOnClickListener.onItemClicked(mCurrentPosition ,mData);
    }*/

    @Override
    public void setData(int position, AssessmentDetails.VideoId data) {
        mCurrentPosition = position;
        mData   =   data;
        mVideoNameTv.setText(String.valueOf(position + 1));
        //颜色
       /* if (position == EvaluationActivity.sVideoIndex) {
            mVideoNameTv.setTextColor(mVideoNameTv.getContext().getResources().getColor(R.color.video_text_selected));
            mBackground.setBackgroundResource(R.drawable.video_count_bg_selected);
        } else {
            mVideoNameTv.setTextColor(mVideoNameTv.getContext().getResources().getColor(R.color.video_text_unselected));
            mBackground.setBackgroundResource(R.drawable.video_count_bg_unselected);
        }*/
    }
}
