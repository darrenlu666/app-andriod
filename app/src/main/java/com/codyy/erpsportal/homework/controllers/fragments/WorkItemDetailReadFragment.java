package com.codyy.erpsportal.homework.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codyy.erpsportal.homework.controllers.activities.WorkItemDetailActivity;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.homework.widgets.PressBar;

/**
 * 按习题批阅界面 习题内容fragment
 * Created by ldh on 2016/2/16.
 */
public class WorkItemDetailReadFragment extends TaskFragment{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemInfoClass = getArguments().getParcelable(WorkItemDetailActivity.ARG_ITEM_INFO);//题目信息
        if (mItemInfoClass != null) {
            /**
             * 添加题干
             */
            if (mItemInfoClass.getWorkItemType().equals(TYPE_TEXT)) {
                addContent(mItemInfoClass.getTextQestion());
            } else {
                addContent(mItemInfoClass.getWorkContent());
            }
            /**
             * 添加音视频
             */
            if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_VIDEO_STRING)) {//加载视频
                addVideoThumbnail(mItemInfoClass.getWorkItemResourceUrl(), FROM_NET, "", false);
            } else if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_AUDIO_STRING)) {//加载音频
                addServiceItemAudioBar(mItemInfoClass.getWorkItemResourceUrl());
            }
        }
    }
}
