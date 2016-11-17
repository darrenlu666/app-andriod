package com.codyy.erpsportal.commons.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;

import com.codyy.erpsportal.commons.controllers.activities.LoadingUserTypeActivity;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;

/**
 * Created by gujiajia on 2016/4/20.
 */
public class Linker {

    public static void linkUserIcon(Activity activity, String userId) {
        if (activity == null) return;
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(activity, "未知用户！", Toast.LENGTH_SHORT).show();
        } else if (UserInfoKeeper.obtainUserInfo().getBaseUserId().equals(userId)) {
            MainActivity.start(activity, UserInfoKeeper.obtainUserInfo(), 2);
        } else {//2.访客
//            PublicUserActivity.start(activity, userId);
            LoadingUserTypeActivity.start(activity, userId);
        }
    }

}
