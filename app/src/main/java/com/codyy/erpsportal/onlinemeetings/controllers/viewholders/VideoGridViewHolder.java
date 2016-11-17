package com.codyy.erpsportal.onlinemeetings.controllers.viewholders;

import android.view.Gravity;
import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.widgets.onlinemeeting.CheckableTextView;
import com.codyy.erpsportal.commons.models.entities.VideoDetails;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.VideoMeetingPlayActivity;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by poe on 16-7-19.
 */
public class VideoGridViewHolder extends BaseRecyclerViewHolder<VideoDetails> {

    @Bind(R.id.checked_text_view)
    CheckableTextView mCheckedTextView;

    public VideoGridViewHolder(View itemView ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_video_gride_selector;
    }

    @Override
    public void setData(int position, VideoDetails data) {
        mCurrentPosition = position ;
        mData = data;
        //video name is the video index .
        mCheckedTextView.setGravity(Gravity.CENTER);
        mCheckedTextView.setText(String.valueOf(position+1));
        if(position == VideoMeetingPlayActivity.mCurrentIndex){
            mCheckedTextView.setChecked(true);
            mCheckedTextView.setBackgroundResource(R.drawable.bg_filter_rectangle_green);
            mCheckedTextView.setTextColor(mCheckedTextView.getResources().getColor(R.color.white));
        }else{
            mCheckedTextView.setChecked(false);
            mCheckedTextView.setBackgroundResource(R.drawable.bg_filter_rectangle_white);
            mCheckedTextView.setTextColor(mCheckedTextView.getResources().getColor(R.color.black));
        }
    }
}
