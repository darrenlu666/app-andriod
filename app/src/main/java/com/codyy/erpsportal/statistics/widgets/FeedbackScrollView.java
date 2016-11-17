package com.codyy.erpsportal.statistics.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.codyy.erpsportal.commons.utils.Cog;

/**
 * Created by gujiajia on 2016/5/23.
 */
public class FeedbackScrollView extends HorizontalScrollView {

    private final static String TAG = "FeedbackScrollView";

    OnScrollChangeListener mOnScrollChangeListener;

    public FeedbackScrollView(Context context) {
        super(context);
    }

    public FeedbackScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeedbackScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public FeedbackScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    boolean mScrollByUser = true;

    public void scrollX(int x) {
        Cog.d(TAG, "scrollX this=" + this + ",x=" + x);
        mScrollByUser = false;
        scrollTo(x, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Cog.d(TAG, "onTouchEvent ev=" + ev);
        mScrollByUser = true;
        return false;
    }

    public void handleTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //Log.d("FeedbackScrollView", "onScrollChanged this=" + this + ",l = " + l);
        if (mOnScrollChangeListener != null && mScrollByUser) {
            mOnScrollChangeListener.onScrollChange(this, l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        mOnScrollChangeListener = onScrollChangeListener;
    }

    public interface OnScrollChangeListener {
        void onScrollChange(View view, int l, int t, int oldl, int oldt);
    }
}
