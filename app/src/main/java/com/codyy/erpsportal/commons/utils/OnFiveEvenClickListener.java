package com.codyy.erpsportal.commons.utils;

import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 五连点监听器
 * Created by gujiajia on 2016/3/25.
 */
public abstract class OnFiveEvenClickListener implements OnTouchListener {

    private long mLastClickTime;

    private int mClickCount;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == MotionEventCompat.getActionMasked(event)) {
            onTap();
            return true;
        }
        return false;
    }

    private void onTap() {
        long nowClickTile = System.currentTimeMillis();
        long interval = System.currentTimeMillis() - mLastClickTime;
        if (interval < 300L) {
            mClickCount++;
            if (mClickCount >= 5) {
                onFiveEvenClick();
                mClickCount = 0;
            }
        } else {
            mClickCount = 0;
        }
        mLastClickTime = nowClickTile;
    }

    public abstract void onFiveEvenClick();
}
