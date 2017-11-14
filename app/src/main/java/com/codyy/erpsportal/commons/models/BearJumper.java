/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.UIUtils;

/**
 * 背负数据的跳转者
 * Created by gujiajia on 2017/9/12.
 */

public abstract class  BearJumper implements Jumpable {

    private Class<? extends Activity> clazz;

    public BearJumper(Class<? extends Activity> _class) {
        this.clazz = _class;
    }

    @Override
    public void jump(Context context) {
        Intent it = new Intent(context, clazz);
        UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
        it.putExtra(Constants.USER_INFO, userInfo);
        bearData(it);
        context.startActivity(it);
        UIUtils.addEnterAnim((Activity) context);
    }

    protected abstract void bearData(Intent intent);
}
