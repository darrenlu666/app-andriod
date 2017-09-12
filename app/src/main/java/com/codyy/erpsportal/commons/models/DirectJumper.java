package com.codyy.erpsportal.commons.models;

import android.app.Activity;
import android.content.Intent;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

/**
 * 跳转者
 * Created by gujiajia on 2015/8/5.
 */
public class DirectJumper extends BearJumper {

    public DirectJumper(Class<? extends Activity> _class) {
        super(_class);
    }

    @Override
    protected void bearData(Intent intent) {
        UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
        intent.putExtra(Constants.USER_INFO, userInfo);
    }
}
