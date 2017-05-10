package com.codyy.erpsportal.commons.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * 锁屏监听广播接收器
 * Created by poe on 15-11-2.
 */
public class ScreenLockChangeReceiver extends BroadcastReceiver {
    public static final String TAG = "ScreenLockReceiver";
    private LocalBroadcastManager mLocalBroadcastManager;

    public ScreenLockChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //通知正在播放的视频组件
        if( null == mLocalBroadcastManager){
            mLocalBroadcastManager =   LocalBroadcastManager.getInstance(context);
        }
        String action = intent.getAction();
        Log.i(TAG,action);
        if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
            mLocalBroadcastManager.sendBroadcastSync(new Intent(ScreenBroadcastReceiver.ACTION_SCREEN_ON));
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
            mLocalBroadcastManager.sendBroadcastSync(new Intent(ScreenBroadcastReceiver.ACTION_SCREEN_OFF));
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
            mLocalBroadcastManager.sendBroadcastSync(new Intent(ScreenBroadcastReceiver.ACTION_SCREEN_USER_PRESENTER));
        }
    }
}
