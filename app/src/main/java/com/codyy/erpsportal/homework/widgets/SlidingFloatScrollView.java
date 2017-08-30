package com.codyy.erpsportal.homework.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * 按题批阅界面的上滑隐藏题目，悬停学生列表
 * Created by ldh on 2016/6/16.
 */
public class SlidingFloatScrollView extends ScrollView {

    public SlidingFloatScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingFloatScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SlidingFloatScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollListener != null) {
            onScrollListener.onScroll(t);
        }
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        requestDisallowInterceptTouchEvent(true);
        super.setOnTouchListener(l);
    }

    public interface OnScrollListener {
        void onScroll(int t);
    }

    private OnScrollListener onScrollListener;

    public void setOnScrollListener(OnScrollListener listener) {
        onScrollListener = listener;
    }
}
