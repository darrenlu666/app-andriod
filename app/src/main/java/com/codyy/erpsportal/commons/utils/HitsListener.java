package com.codyy.erpsportal.commons.utils;

import android.view.View;

/**
 * 防止连点点击事件
 * Created by gujiajia on 2017/5/17.
 */

public abstract class HitsListener implements View.OnClickListener {

    private long lastTime;

    @Override
    public void onClick(View v) {
        long currTime = System.currentTimeMillis();
        if (currTime - lastTime > 300L) {
            onHit(v);
        }
        lastTime = currTime;
    }

    abstract void onHit(View v);
}
