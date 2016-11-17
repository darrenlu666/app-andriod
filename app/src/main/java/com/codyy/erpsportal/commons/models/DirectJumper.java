package com.codyy.erpsportal.commons.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.UIUtils;

/**
 * 跳转者
 * Created by gujiajia on 2015/8/5.
 */
public class DirectJumper implements Jumpable {

    private Class<? extends Activity> clazz;

    private Bundle bundle;

    public DirectJumper(Class<? extends Activity> _class) {
        this.clazz = _class;
    }

    @Override
    public void jump(Context context) {
        UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
        Intent it = new Intent(context, clazz);
        it.putExtra(Constants.USER_INFO, userInfo);
        context.startActivity(it);
        UIUtils.addEnterAnim((Activity) context);
    }
}
