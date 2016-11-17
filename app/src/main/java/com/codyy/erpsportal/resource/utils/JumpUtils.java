package com.codyy.erpsportal.resource.utils;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import com.codyy.erpsportal.commons.controllers.activities.LoginActivity;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.resource.controllers.activities.DocumentContentActivity;
import com.codyy.erpsportal.resource.controllers.activities.ImageDetailsActivity;
import com.codyy.erpsportal.resource.controllers.activities.VideoDetailsActivity;
import com.codyy.erpsportal.resource.models.entities.Document;
import com.codyy.erpsportal.resource.models.entities.Image;

/**
 * Created by gujiajia on 2016/6/15.
 */
public class JumpUtils {

    /**
     * 添加点击后进入资源详情监听器
     * @param v 被添加监听的view
     * @param videoId 视频资源id
     */
    public static void addGotoVideoDetailsClickListener(View v, final String videoId) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) v.getContext();
                UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
                if (userInfo == null) {
                    LoginActivity.startClearTask(activity);
                    return;
                }
                VideoDetailsActivity.start(activity, userInfo, videoId);
            }
        });
    }

    public static void addGotoImageDetailsClickListener(View v, Image image) {
        addGotoImageDetailsClickListener(v, image.getId());
    }

    public static void addGotoImageDetailsClickListener(View v, final String imageId) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) v.getContext();
                UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
                if (userInfo == null) {
                    LoginActivity.startClearTask(activity);
                    return;
                }
                ImageDetailsActivity.start(activity, userInfo, imageId);
            }
        });
    }

    public static void addGotoDocumentDetailsClickListener(View v, final Document document) {
        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) v.getContext();
                UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
                if (userInfo == null) {
                    LoginActivity.startClearTask(activity);
                    return;
                }
                DocumentContentActivity.start(activity, userInfo, document);
            }
        });
    }
}
