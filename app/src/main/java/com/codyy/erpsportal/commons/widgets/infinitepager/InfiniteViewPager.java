package com.codyy.erpsportal.commons.widgets.infinitepager;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.LinePageIndicator;

import java.lang.reflect.Field;

/**
 * 无限滚viewpager，还带滑动降速
 * Created by gujiajia on 2016/5/17.
 */
public class InfiniteViewPager extends ViewPager implements Callback {

    private final static String TAG = "InfiniteViewPager";

    private CustomDurationScroller mScroller;

    private final static int MSG_SCROLL = 0x32;

    private Handler mHandler = new Handler(this);

    private LinePageIndicator mIndicator;

    private int mTouchSlop;

    public InfiniteViewPager(Context context) {
        super(context);
        postInitScroller();
    }

    public InfiniteViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        postInitScroller();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InfiniteViewPager);
        if (typedArray != null) {
            float factor = typedArray.getFloat(R.styleable.InfiniteViewPager_scrollDurationFactor, 1f);
            setScrollDurationFactor(factor);
            typedArray.recycle();
        }
    }

    private void postInitScroller() {
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        mTouchSlop = vc.getScaledTouchSlop();
        try {
            Class<?> clazz = ViewPager.class;
            Field scroller = clazz.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = clazz.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);
            mScroller = new CustomDurationScroller(getContext(), (Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setScrollDurationFactor(float factor){
        mScroller.setScrollFactor(factor);
    }

    private float mInitialMotionX;

    private float mInitialMotionY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Cog.d(TAG, "onTouchEvent ev=" + ev);
        int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialMotionX = ev.getX();
                mInitialMotionY = ev.getY();
            case MotionEvent.ACTION_MOVE:
                stopScrolling();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(ev.getY() - mInitialMotionY) < mTouchSlop
                        && Math.abs(ev.getX() - mInitialMotionX) < mTouchSlop)
                    performClick();
            case MotionEvent.ACTION_CANCEL:
                startToScroll();
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Cog.d(TAG, "onViewAttachedToWindow");
        startToScroll();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Cog.d(TAG, "onViewDetachedFromWindow");
        stopScrolling();
    }

    /**
     * 是否正在滚动
     */
    boolean mIsScrolling;

    /**
     * 开始自动滚
     */
    public void startToScroll() {
        if (!mIsScrolling) {
            sendScrollMsg();
            mIsScrolling = true;
        }
    }

    /**
     * 发送滚一下消息
     */
    private void sendScrollMsg() {
        mHandler.removeMessages(MSG_SCROLL);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_SCROLL), 4000);
    }

    /**
     * 停止自动滚
     */
    public void stopScrolling() {
        if (mIsScrolling) {
            mHandler.removeMessages(MSG_SCROLL);
            mIsScrolling = false;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MSG_SCROLL) {
            if (getChildCount() <= 1) {//项的数量没有两个或两个以上的
                stopScrolling();
                return true;
            }
            if (mIndicator != null) mIndicator.setCurrentItemSmooth(getCurrentItem() + 1);
            sendScrollMsg();
            return true;
        }
        return false;
    }

    public void setIndicator(LinePageIndicator indicator) {
        mIndicator = indicator;
    }

    public static class CustomDurationScroller extends Scroller {

        private float mScrollFactor = 1;

        public CustomDurationScroller(Context context) {
            super(context);
        }

        public CustomDurationScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public CustomDurationScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        public void setScrollFactor(float scrollFactor) {
            this.mScrollFactor = scrollFactor;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, (int)(duration * mScrollFactor));
        }
    }
}
