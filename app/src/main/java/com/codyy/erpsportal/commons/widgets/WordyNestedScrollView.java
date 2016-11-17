package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 监听滑动的NestedScrollView
 * Created by gujiajia on 2015/9/18.
 */
public class WordyNestedScrollView extends NestedScrollView {
    private OnScrollListener mOnScrollListener;

    public WordyNestedScrollView(Context context) {
        super(context);
    }

    public WordyNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WordyNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(x, y,oldX,oldY);
        }
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev) & notifyActionUp(ev);
    }

    private boolean notifyActionUp(MotionEvent ev) {
        if ((ev.getAction() & MotionEventCompat.ACTION_MASK) == MotionEvent.ACTION_UP
                && mOnScrollListener != null) {
            mOnScrollListener.onActionUp(ev);
        }
        return true;
    }

    public interface OnScrollListener {
        void onScroll(int x, int y,int oldX,int oldY);
        void onActionUp(MotionEvent ev);
    }
}
