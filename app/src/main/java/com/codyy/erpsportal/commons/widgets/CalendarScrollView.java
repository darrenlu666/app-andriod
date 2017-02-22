package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by kmdai on 16-6-24.
 */
public class CalendarScrollView extends FrameLayout {
    /**
     * 滑动时间
     */
    private static final int ANIMATION_DURATION = 500;
    private CalendarViewPager mViewPager;
    private View mBGView;
    private int mPagerHight;
    private TextView mTitleTV;
    private boolean mIsOpen;
    private OverScroller mScroller;
    private View mScrollView;
    private CalendarViewPager.OnDateChange mOnDateChange;
    private boolean mIsDragged;
    private float mStartY;
    private float mEndY;
    private int mCurrentYear;
    private int mCurrentMonth;
    private int mCurrentDay;

    /**
     * Sentinel value for no current active pointer.
     */
    private static final int INVALID_POINTER = -1;
    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private int mActivePointerId = INVALID_POINTER;

    public CalendarScrollView(Context context) {
        super(context);
        init(context, null);
    }

    public CalendarScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CalendarScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CalendarScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mBGView = LayoutInflater.from(context).inflate(R.layout.calendar_popuwindow, null);
        mBGView.setVisibility(GONE);
        mViewPager = (CalendarViewPager) mBGView.findViewById(R.id.popu_calendar);
        mTitleTV = (TextView) mBGView.findViewById(R.id.title_date);
        mScroller = new OverScroller(context);
        mPagerHight = UIUtils.dip2px(context, 250);
        mBGView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mPagerHight));
        addView(mBGView, 0);
        mTitleTV.setText(mViewPager.mYear + "-" + String.format(Locale.getDefault(), "%02d", mViewPager.mMonth));
        mViewPager.setOnDateChange(new CalendarViewPager.OnDateChange() {
            @Override
            public void onDateChange(int year, int month, int day) {
                mTitleTV.setText(year + "-" + String.format(Locale.getDefault(), "%02d", month));
                if (mOnDateChange != null) {
                    mOnDateChange.onDateChange(year, month, day);
                }
            }

            @Override
            public void onDateSelect(int year, int month, int day, int week) {
                if (mOnDateChange != null) {
                    mOnDateChange.onDateSelect(year, month, day, week);
                }
            }
        });
    }

    public boolean isOpen() {
        return mIsOpen;
    }

    public void setOndateChang(CalendarViewPager.OnDateChange ondateChang) {
        mOnDateChange = ondateChang;
    }

    @Override
    public void addView(View child) {
        if (getChildCount() > 1) {
            throw new IllegalStateException("This can host only one scroll view");
        }
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 1) {
            throw new IllegalStateException("This can host only one scroll view");
        }

        super.addView(child, index);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() > 1) {
            throw new IllegalStateException("This can host only one scroll view");
        }
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 1) {
            throw new IllegalStateException("This can host only one scroll view");
        }

        super.addView(child, index, params);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (!mScroller.isFinished()) {
//            return true;
//        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mIsOpen && inChild((int) event.getX(), (int) event.getY(), 1)) {
                    mIsDragged = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                break;
        }
        return mIsDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        //要点击在下面的view才停止动画，接收滑动事件
//        if (!inChild((int) event.getX(), (int) event.getY(), 1)) {
//            mIsDragged = false;
//            return false;
//        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                if (mScrollView == null) {
                    mScrollView = getChildAt(1);
                }
                mStartY = event.getY();
                mActivePointerId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                final int activePointerIndex = event.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1) {
                    break;
                }
                float y = event.getY(activePointerIndex);
                float dy = y - mStartY;
                mStartY = y;
                if (mScrollView.getTop() + dy <= 0) {
                    dy = -mScrollView.getTop();
                } else if (mScrollView.getTop() + dy > mPagerHight) {
                    dy = mPagerHight - mScrollView.getTop();
                }
                mScrollView.offsetTopAndBottom((int) dy);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mScrollView.getTop() > mPagerHight / 2) {
                    mIsOpen = true;
                    mScroller.startScroll(0, mScrollView.getTop(), 0, mPagerHight - mScrollView.getTop(), ANIMATION_DURATION);
                } else {
                    mIsOpen = false;
                    mScroller.startScroll(0, mScrollView.getTop(), 0, -mScrollView.getTop(), ANIMATION_DURATION);
                }
                postAnimation();
                mIsDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }
        return mIsDragged;
    }

    public void open() {
        if (!mScroller.isFinished()) {
            return;
        }
        if (mBGView.getVisibility() == GONE) {
            mBGView.setVisibility(VISIBLE);
        }
        if (mIsOpen) {
            mIsOpen = false;
            if (mScrollView != null) {
            }
            mScroller.startScroll(0, mScrollView.getTop(), 0, -mScrollView.getTop(), ANIMATION_DURATION);
        } else {
            mIsOpen = true;
            mScroller.startScroll(0, 0, 0, mPagerHight, ANIMATION_DURATION);
        }
        postAnimation();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
        super.measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        /**
         * 动画执行的时候，不要计算滑动view的大小和位置
         */
        if (!mScroller.isFinished() || mIsDragged) {
            View childView = getChildAt(0);
            int cWidth = childView.getMeasuredWidth();
            int cHeight = childView.getMeasuredHeight();
            MarginLayoutParams cParams = (MarginLayoutParams) childView.getLayoutParams();
            int cl = 0, ct = 0, cr = 0, cb = 0;
            cl = cParams.leftMargin;
            ct = cParams.topMargin;
            cr = cl + cWidth;
            cb = cHeight + ct;
            childView.layout(cl, ct, cr, cb);
            return;
        } else if (mIsOpen) {
            int cCount = getChildCount();
            int cWidth = 0;
            int cHeight = 0;
            MarginLayoutParams cParams = null;
            int mt = 0;
            /**
             * 遍历所有childView根据其宽和高，以及margin进行布局
             */
            for (int i = 0; i < cCount; i++) {
                View childView = getChildAt(i);
                cWidth = childView.getMeasuredWidth();
                cHeight = childView.getMeasuredHeight();
                cParams = (MarginLayoutParams) childView.getLayoutParams();
                int cl = 0, ct = 0, cr = 0, cb = 0;
                switch (i) {
                    case 0:
                        mt = cHeight;
                        cl = cParams.leftMargin;
                        ct = cParams.topMargin;
                        break;
                    case 1:
                        cl = cParams.leftMargin;
                        ct = cParams.topMargin + mt;
                        break;
                }
                cr = cl + cWidth;
                cb = cHeight + ct;
                childView.layout(cl, ct, cr, cb);
            }
        } else {
            super.onLayout(changed, left, top, right, bottom);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void postAnimation() {
        postInvalidateOnAnimation();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollView == null) {
                mScrollView = getChildAt(1);
            }
            mScrollView.offsetTopAndBottom(mScroller.getCurrY() - mScrollView.getTop());
            if (mScroller.isFinished()) {
                if (!mIsOpen) {
                    mBGView.setVisibility(GONE);
                }
            }
            invalidate();
        }
    }

    /**
     * 判断是否点击在孩子内
     *
     * @param x
     * @param y
     * @param index view位置
     * @return
     */
    private boolean inChild(int x, int y, int index) {
        if (getChildCount() > 1) {
            final int scrollY = getScrollY();
            final View child = getChildAt(index);
            return !(y < child.getTop() - scrollY
                    || y >= child.getBottom() - scrollY
                    || x < child.getLeft()
                    || x >= child.getRight());
        }
        return false;
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mCurrentYear = calendar.get(Calendar.YEAR);
        mCurrentMonth = calendar.get(Calendar.MONTH) + 1;
        mCurrentDay = calendar.get(Calendar.DAY_OF_MONTH);
        return mCurrentYear + "-" + String.format(Locale.getDefault(), "%02d", mCurrentMonth) + "-" + String.format(Locale.getDefault(), "%02d", mCurrentDay);
    }

    public int getYear() {
        return mCurrentYear;
    }

    public int getMonth() {
        return mCurrentMonth;
    }

    public int getDay() {
        return mCurrentDay;
    }

    public void reSetDate() {
        mViewPager.reSetDate();
    }

    public void setSelectDate(int year, int month, int day) {
        if (mViewPager != null) {
            mViewPager.setSelectDate(year, month, day);
        }
    }
}
