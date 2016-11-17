package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义Viewpager实现禁止滑动
 * Created by poe on 15-9-8.
 */
public class CodyyViewPager extends ViewPager{

    private boolean mIsPagingEnabled = true;

    public CodyyViewPager(Context context) {
        super(context);
    }

    public CodyyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.mIsPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.mIsPagingEnabled && super.onInterceptTouchEvent(event);
    }

    /**
     * 是否禁止滑动
     * @param b true:可以滑动 (default) false:禁止滑动
     */
    public void setPagingEnabled(boolean b) {
        this.mIsPagingEnabled = b;
    }

}
