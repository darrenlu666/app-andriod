package com.codyy.erpsportal.commons.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.codyy.erpsportal.commons.utils.Cog;

/**
 * 锁屏监听广播接收器
 * Created by poe on 15-11-2.
 */
public class ScreenBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "ScreenBroadcastReceiver";

    private ScreenStateListener mScreenStateListener;

    public ScreenBroadcastReceiver(ScreenStateListener screenStateListener) {
        mScreenStateListener = screenStateListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,intent.getAction()+"");
        if (isInitialStickyBroadcast()) {//miui8锁屏十分钟会偷偷断开wifi，还会发个粘性广播坑你
            Cog.d(TAG, "isInitialStickyBroadcast = ", isInitialStickyBroadcast());
            return;
        }
        String action = intent.getAction();
        Log.i(TAG,action);
        if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
            if(null != mScreenStateListener) mScreenStateListener.onScreenOn();
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
            if(null != mScreenStateListener) mScreenStateListener.onScreenOff();
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
            if(null != mScreenStateListener) mScreenStateListener.onUserPresent();
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
