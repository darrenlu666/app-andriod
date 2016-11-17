package com.codyy.erpsportal.commons.utils;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.widgets.AutoHide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 帮助控件View自动隐藏
 * care： you should must invoke {@link #destroyView()} #onDestroy()
 * Created by caixingming on 2015/5/8.
 */
public class AutoHideUtils implements AutoHide {
    private static String TAG = "AutoHideUtils";
    /**
     * 隐藏
     */
    private static final int ACTION_HIDE_VIEW = 101;
    private static final int ACTION_SHOW_VIEW = 102;

    private Handler mThreadHandler ;
    private AutoHideHandler mHandlerUI =  new AutoHideHandler(this);
    private List<View> mViews;

    public AutoHideUtils(View... views) {
        this.mViews = new ArrayList<>(Arrays.asList(views));
        if(views != null && views.length > 0){
            HandlerThread hideThread = new HandlerThread(TAG);
            hideThread.start();
            mThreadHandler = new Handler(hideThread.getLooper());
        }
    }

    private Runnable runHide = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (mHandlerUI != null)
                mHandlerUI.sendEmptyMessage(101);
        }
    };

    @Override
    public void hideControl() {
        if (mViews == null || mViews.size() == 0) {
            return;
        }
        for (View view : mViews) {
            view.setVisibility(View.GONE);
            view.startAnimation(AnimationUtils.loadAnimation(EApplication.instance(), R.anim.abc_fade_out));
        }
    }


    @Override
    public void showControl() {
        if (mViews == null || mViews.size() == 0) {
            return;
        }

        for (View view: mViews) {
            if (view.getVisibility() == View.GONE) {
                view.setVisibility(View.VISIBLE);
                Animation anim = AnimationUtils.loadAnimation(EApplication.instance(), R.anim.abc_fade_in);
                view.startAnimation(anim);
            }
        }
        touchControl();
    }

    @Override
    public void touchControl() {
        if(null != mThreadHandler){
            mThreadHandler.removeCallbacks(runHide);
            mThreadHandler.post(runHide);
        }
    }

    @Override
    public void destroyView() {
        if (mThreadHandler != null) {
            //停止线程
            mThreadHandler.removeCallbacks(runHide);
            //remove the force reference
            mViews = null;
        }

        if (null != mThreadHandler) {
            mThreadHandler.getLooper().quit();
        }

        mHandlerUI = null ;
    }

    private static class AutoHideHandler extends WeakHandler<AutoHideUtils>{

        public AutoHideHandler(AutoHideUtils owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(null == getOwner()) return;
            switch (msg.what) {
                case ACTION_HIDE_VIEW:
                    getOwner().hideControl();
                    break;
                default:
                    //do nothing .
                    break;
            }
        }
    }
}
