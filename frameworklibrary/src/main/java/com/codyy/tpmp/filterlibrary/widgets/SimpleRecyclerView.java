package com.codyy.tpmp.filterlibrary.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 实现了刷新状态设置，
 * 解决刷新触摸Crash .
 * setRefresh(true)禁止触摸
 * Created by poe on 16-2-16.
 */
public class SimpleRecyclerView extends RecyclerView {
    /**
     * 是否正在刷新中...
     * true :禁止touch
     * false：恢复正常
     */
    private boolean mIsRefreshing = false;

    public SimpleRecyclerView(Context context) {
        super(context);
        init();
    }

    public SimpleRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    /**
     * 是否在刷新中...
     *
     * @param mIsRefreshing
     */
    public void setRefreshing(boolean mIsRefreshing) {
        this.mIsRefreshing = mIsRefreshing;
    }

    public void init() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mIsRefreshing;
            }
        });
    }

}
