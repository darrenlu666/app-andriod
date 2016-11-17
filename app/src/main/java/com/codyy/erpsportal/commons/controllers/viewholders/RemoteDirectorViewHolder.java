package com.codyy.erpsportal.commons.controllers.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.models.entities.RemoteDirectorItem;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.widgets.AvatarView;


/**
 * Created by yangxinwu on 2015/8/3.
 */
public class RemoteDirectorViewHolder extends AbsViewHolder<RemoteDirectorItem> {
    AvatarView headerImage;
    TextView mTvHolderOne;
    TextView mTvHolderTwo;
    TextView mTvHolderThr;
    RelativeLayout mRlStartTime;
    TextView mTvInRemoteClass;
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
        mRlStartTime = (RelativeLayout) view.findViewById(R.id.rl_start_time);
        mTvHour = (TextView) mRlStartTime.findViewById(R.id.tv_hour);
        mTvMinite = (TextView) mRlStartTime.findViewById(R.id.tv_minite);
        mTvInRemoteClass = (TextView) view.findViewById(R.id.tv_in_remote_living_class);
    }

    @Override
    public void setDataToView(RemoteDirectorItem data, Context context) {

        mTvHolderOne.setText(data.getSpeakerUserName());
        mTvHolderTwo.setText(data.getClasslevelName() + "/" + data.getSubjectName() + "/" + data.getClassSeq());
        mTvHolderThr.setText(data.getClasslevelName());
        if (data.getStatus().equals("")) {//显示进入
            mTvInRemoteClass.setVisibility(View.VISIBLE);
            mRlStartTime.setVisibility(View.GONE);
        } else {
            mTvInRemoteClass.setVisibility(View.GONE);
            mRlStartTime.setVisibility(View.VISIBLE);
            String[] time = data.getBeginTime().split(":");
            mTvHour.setText(time[0]);
            mTvMinite.setText(time[1]);
        }

        headerImage.setAvatarUrl(URLConfig.IMAGE_URL + StringUtils.replaceSmallPic(data.getSubjectPic()));
    }
}
