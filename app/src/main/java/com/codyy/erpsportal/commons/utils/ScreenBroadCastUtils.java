package com.codyy.erpsportal.commons.utils;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.commons.receivers.ScreenBroadcastReceiver;

/**
 * Created by caixingming on 2017/5/8.
 * 注册、解绑 屏幕相关的广播类
 */
public class ScreenBroadCastUtils {
    private static final String TAG = "ScreenBroadCastUtils";
    private LocalBroadcastManager mLocalBroadcastManager;
    private ScreenBroadcastReceiver myBroadcastReceive;
    private ScreenLockListener mScreenLockListener;

    /**
     * @param lockListener
     * 注册Lock通知广播
     * 解绑：：destroy()
     */
    public ScreenBroadCastUtils(ScreenLockListener lockListener) {
        this.mScreenLockListener =   lockListener;
        registerMyBroadCast();
    }

    //注册
    private void registerMyBroadCast(){
        //注册广播
        mLocalBroadcastManager  =   LocalBroadcastManager.getInstance(EApplication.instance());
        myBroadcastReceive = new ScreenBroadcastReceiver(new ScreenBroadcastReceiver.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                Log.i(TAG,"onScreenOn()");
            }

            @Override
            public void onScreenOff() {
                Log.i(TAG,"onScreenOff()");
                if(null != mScreenLockListener) mScreenLockListener.onScreenLock();
            }

            @Override
            public void onUserPresent() {
                Log.i(TAG,"onUserPresent()");
                if(null != mScreenLockListener) mScreenLockListener.onScreenOn();
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(ScreenBroadcastReceiver.ACTION_SCREEN_ON);
        filter.addAction(ScreenBroadcastReceiver.ACTION_SCREEN_OFF);
        filter.addAction(ScreenBroadcastReceiver.ACTION_SCREEN_USER_PRESENTER);
        mLocalBroadcastManager.registerReceiver(myBroadcastReceive,filter);
    }

    /**
     * 解除广播的绑定
     */
    public void destroy(){
        //解除广播接收器
        if(myBroadcastReceive != null && mLocalBroadcastManager != null){
            mLocalBroadcastManager.unregisterReceiver(myBroadcastReceive);
            mLocalBroadcastManager  = null;
            this.myBroadcastReceive = null;
        }
        this.mScreenLockListener =   null;
    }

    /**
     * 监听 控制状态并执行
     */
    public interface ScreenLockListener {

        /**
         *  打开锁屏
         */
        void onScreenOn();

        /**
         * 锁屏
         */
        void onScreenLock();

    }
}
