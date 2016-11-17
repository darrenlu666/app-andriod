package com.codyy.erpsportal.commons.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

public class UserInfoKeeperService extends Service {
    private final static String TAG = "UserInfoKeeperService";

    private UserInfo mUserInfo;

    private LocalBinder mBinder;

    public UserInfoKeeperService() {
    }

    public class LocalBinder extends Binder {
        public UserInfoKeeperService getService(){
            return UserInfoKeeperService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }
}
