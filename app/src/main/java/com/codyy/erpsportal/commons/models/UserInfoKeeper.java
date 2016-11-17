package com.codyy.erpsportal.commons.models;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.services.UserInfoKeeperService;

/**
 * 登陆信息保持器
 *
 * Created by gujiajia on 2015/7/30.
 */
public class UserInfoKeeper {

    private static UserInfoKeeper ourInstance = new UserInfoKeeper();

    private UserInfo userInfo;

    public static UserInfoKeeper getInstance() {
        return ourInstance;
    }

    private UserInfoKeeper() {
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * 清除用户登录信息
     */
    public void clearUserInfo() {
        userInfo = null;
    }

    /**
     * 登陆信息是否为空
     */
    public boolean isEmpty() {
        return userInfo == null;
    }

    public static UserInfo obtainUserInfo() {
        return getInstance().getUserInfo();
    }

    public static void obtainUserInfo(Context context) {
        Intent intent = new Intent(context, UserInfoKeeperService.class);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }
}
