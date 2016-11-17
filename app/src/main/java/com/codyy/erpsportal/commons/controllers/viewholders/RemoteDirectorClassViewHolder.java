package com.codyy.erpsportal.commons.controllers.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.ClassRoomItem;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.widgets.AvatarView;


/**
 * Created by yangxinwu on 2015/8/3.
 */
public class RemoteDirectorClassViewHolder extends AbsViewHolder<ClassRoomItem> {
    AvatarView headerImage;
    TextView mTvHolderOne;
    TextView mTvHolderTwo;
    TextView mTvHolderThr;
    RelativeLayout mRlTime;
    TextView mTvHour;
    TextView mTvMinite;


    @Override
    public int obtainLayoutId() {
        return R.layout.item_remote_director;
    }

    @Override
    public void mapFromView(View view) {
        headerImage = (AvatarView) view.findViewById(R.id.iv_head);
        mTvHolderOne = (TextView) view.findViewById(R.id.tv_holder_1);
        mTvHolderTwo = (TextView) view.findViewById(R.id.tv_holder_2);
        mTvHolderThr = (TextView) view.findViewById(R.id.tv_holder_3);
        mRlTime = (RelativeLayout) view.findViewById(R.id.rl_start_time);
        mTvHour = (TextView) view.findViewById(R.id.tv_hour);
        mTvMinite = (TextView) view.findViewById(R.id.tv_minite);
    }

    @Override
    public void setDataToView(ClassRoomItem data, Context context) {

        mTvHolderOne.setText(data.getTeacher());
        mTvHolderTwo.setText(data.getGrade() + "/" + data.getSubject() + "/" + data.getSetsuji());
        mTvHolderThr.setText(data.getClassRoom());
        headerImage.setAvatarUrl(StringUtils.replaceSmallPic(StringUtils.replace(data.getSubjectPic())));
        if (data.getStatus().equals("")) {
            mRlTime.setVisibility(View.GONE);
        } else {
            mRlTime.setVisibility(View.VISIBLE);
        }

    }


}
