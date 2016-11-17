package com.codyy.erpsportal.commons.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 锁屏监听广播接收器
 * Created by poe on 15-11-2.
 */
public class ScreenBroadcastReceiver extends BroadcastReceiver {

    private ScreenStateListener mScreenStateListener;


    public ScreenBroadcastReceiver( ScreenStateListener screenStateListener) {
        mScreenStateListener = screenStateListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
            mScreenStateListener.onScreenOn();
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
            mScreenStateListener.onScreenOff();
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
            mScreenStateListener.onUserPresent();
        }
    }


    public interface ScreenStateListener {// 返回给调用者屏幕状态信息

        /**
         * open screen .
         */
        void onScreenOn();

        /**
         * lock screen .
         */
        void onScreenOff();

        /**
         * unlock screen after open screen .
         */
        void onUserPresent();
    }
}
