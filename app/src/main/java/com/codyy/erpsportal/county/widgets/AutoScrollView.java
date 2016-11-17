package com.codyy.erpsportal.county.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.OverScroller;

/**
 * Created by kmdai on 16-5-31.
 */
public class AutoScrollView extends FrameLayout {
    private int mLastMotionY;
    private int mActivePointerId;
    // 滚动阀值,x的move距离超出该值时才视为一个touch的move行为
    private int mTouchSlop;
    private boolean mIsBeingDragged;
    private int mHight;
    private static final int INVALID_POINTER = -1;
    private VelocityTracker mVelocityTracker;
    private OverScroller mScroller;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private float mStartY;

    public AutoScrollView(Context context) {
        super(context);
        initScrollView(context, null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initScrollView(context, attrs);

    }

    public AutoScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initScrollView(context, attrs);
    }

    public AutoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initScrollView(context, attrs);
    }

    private void initScrollView(Context context, AttributeSet attrs) {
        mScroller = new OverScroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    public void addView(View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("This can host only one scroll view");
        }

        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("This can host only one scroll view");
        }

        super.addView(child, index);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("This can host only one scroll view");
        }

        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("This can host only one scroll view");
        }

        super.addView(child, index, params);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
        * Shortcut the most recurring case: the user is in the dragging
        * state and he is moving his finger.  We want to intercept this
        * motion.
        */
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
            return true;
        }
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE: {
                 /*
                * Locally do absolute value. mLastMotionY is set to the y value
                * of the down event.
                */
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                }
                final int pointerIndex = ev.findPointerIndex(activePointerId);
                if (pointerIndex == -1) {
                    break;
                }
                final int y = (int) ev.getY(pointerIndex);
                final int yDiff = Math.abs(y - mLastMotionY);
                if (yDiff > mTouchSlop) {
                    mIsBeingDragged = true;
                    mLastMotionY = y;
                    initVelocityTrackerIfNotExists();
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                final int y = (int) ev.getY();
                if (!inChild((int) ev.getX(), y)) {
                    mIsBeingDragged = false;
                    recycleVelocityTracker();
                    break;
                }
                 /*
                 * Remember location of down touch.
                 * ACTION_DOWN always refers to pointer index 0.
                 */
                mLastMotionY = y;
                mActivePointerId = ev.getPointerId(0);

//                initOrResetVelocityTracker();
//                mVelocityTracker.addMovement(ev);
                /*
                * If being flinged and user touches the screen, initiate drag;
                * otherwise don't.  mScroller.isFinished should be false when
                * being flinged.
                */
                mIsBeingDragged = !mScroller.isFinished();
//                if (mIsBeingDragged && mScrollStrictSpan == null) {
//                    mScrollStrictSpan = StrictMode.enterCriticalSpan("ScrollView-scroll");
//                }
//                startNestedScroll(SCROLL_AXIS_VERTICAL);
//                mScroller.computeScrollOffset();
//                mScroller.abortAnimation();
//                requestDisallowInterceptTouchEvent(true);
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                /* Release the drag */
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                recycleVelocityTracker();
//                if (mScroller.springBack(mScrollX, mScrollY, 0, 0, 0, getScrollRange())) {
//                    postInvalidateOnAnimation();
//                }
//                stopNestedScroll();
                break;
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(ev);
        MotionEvent vtev = MotionEvent.obtain(ev);
//        if (actionMasked == MotionEvent.ACTION_DOWN) {
//            mNestedYOffset = 0;
//        }
//        vtev.offsetLocation(0, mNestedYOffset);
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                /*
             * If being flinged and user touches, stop the fling. isFinished
             * will be false if being flinged.
             */
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mIsBeingDragged = getChildCount() != 0;
                if (!mIsBeingDragged) {
                    return false;
                }

                // Remember where the motion event started
                mStartY = mLastMotionY = (int) ev.getY();
                mActivePointerId = ev.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1) {
                    break;
                }
                final float y = ev.getY(activePointerIndex);
                final int deltaY = (int) (mLastMotionY - y);

                if (!mIsBeingDragged) {
                    // 判断是否为一个move行为
                    if (Math.abs(deltaY) > mTouchSlop) {
                        mIsBeingDragged = true;
                    }
                }

                if (mIsBeingDragged) {
                    // Scroll to follow the motion event
                    mLastMotionY = (int) y;

                    float oldScrollY = getScrollY();
                    float scrollY = oldScrollY + deltaY;

                    // Clamp values if at the limits and record
                    final int left = 0;
                    final int range = getScrollRangeY();
                    // 防止滚动超出边界
                    if (scrollY > range) {
                        scrollY = range;
                    } else if (scrollY < left) {
                        scrollY = left;
                    }

                    // 替换了系统ScrollView的overScrollBy方法,
                    // 即不考虑overScroll情况直接scrollTo滚动了
                    scrollTo(getScrollX(), (int) (scrollY));
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    final int activePointerIndex1 = ev.findPointerIndex(mActivePointerId);
                    if (activePointerIndex1 == -1) {
//                        resetScroll(true);
                        break;
                    }
                    final float y1 = ev.getY(activePointerIndex1);
                    float dy = mStartY - y1;
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocity = (int) velocityTracker.getYVelocity(mActivePointerId);

                    if (getChildCount() > 0) {
                        // 速度超过某个阀值时才视为fling
                        if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
//                            fling(-initialVelocity);

                            if (initialVelocity < -2000) {
                                mScroller.startScroll(0, getScrollY(), 0, (mHight - getScrollY()), 500);
                            } else if (initialVelocity > 2000) {
                                mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 500);
                            } else if (Math.abs(dy) >= mHight / 3) {
                                if (dy > 0) {
                                    mScroller.startScroll(0, getScrollY(), 0, (mHight - getScrollY()), 500);
                                } else {
                                    mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 500);
                                }
                            } else {
                                if (getScrollY() > mHight - getScrollY()) {
                                    mScroller.startScroll(0, getScrollY(), 0, (mHight - getScrollY()), 500);
                                } else {
                                    mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 500);
                                }
                            }
                        } else {
                            resetScroll(false);
                        }
                    }
                    postAnimation();
                    endDrag();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                resetScroll(true);
                break;
        }
        vtev.recycle();
        return true;
    }

    /**
     * 恢复滑动位置
     */

    private void resetScroll(boolean post) {
        if (getScrollY() > mHight - getScrollY()) {
            mScroller.startScroll(0, getScrollY(), 0, (mHight - getScrollY()), 500);
        } else {
            mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 500);
        }
        if (post) {
            postAnimation();
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHight = h;
    }

    @Override
    public ViewGroup.LayoutParams getLayoutParams() {
        return super.getLayoutParams();
    }

    /**
     * 获取y轴向最大滚动范围
     *
     * @return
     */
    private int getScrollRangeY() {
        int scrollRange = 0;
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            scrollRange = Math.max(0, child.getHeight() - (getHeight() - getPaddingTop() - getPaddingBottom()));
        }
        return scrollRange;
    }

    private void endDrag() {
        mIsBeingDragged = false;

        recycleVelocityTracker();
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置子ｖｉｅｗ的高度为父view的两倍
     *
     * @param child
     * @param parentWidthMeasureSpec
     * @param widthUsed
     * @param parentHeightMeasureSpec
     * @param heightUsed
     */
    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        int height = MeasureSpec.getSize(parentHeightMeasureSpec);
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, MeasureSpec.makeMeasureSpec(height * 2, MeasureSpec.getMode(parentHeightMeasureSpec)), heightUsed);
    }


    /**
     * 惯性滑动
     *
     * @param velocityX The initial velocitX in the X direction. Positive numbers mean
     *                  that the finger/cursor is moving down the screen, which means
     *                  we want to scroll towards the top.
     */
    public void fling(int velocityX) {
        if (getChildCount() > 0) {

            mScroller.fling(getScrollX(), getScrollY(),
                    0, velocityX,
                    0, 0,
                    0, getScrollRangeY());

            invalidate();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void postAnimation() {
        postInvalidateOnAnimation();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();

            if (oldX != x || oldY != y) {
                scrollTo(x, y);
            }
            // Keep on drawing until the animation has finished.
            invalidate();
            return;
        }
    }

    private boolean inChild(int x, int y) {
        if (getChildCount() > 0) {
            final int scrollY = getScrollY();
            final View child = getChildAt(0);
            return !(y < child.getTop() - scrollY
                    || y >= child.getBottom() - scrollY
                    || x < child.getLeft()
                    || x >= child.getRight());
        }
        return false;
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }
}
