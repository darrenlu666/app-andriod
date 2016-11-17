package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.codyy.erpsportal.commons.utils.Cog;

/**
 * Created by gujiajia on 2015/9/10.
 */
public class AppBarLayoutBehavior extends AppBarLayout.Behavior {

    private final static String TAG = "AppBarLayoutBehavior";

    public AppBarLayoutBehavior() {
        super();
    }

    public AppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        Cog.d(TAG, "onStartNestedScroll");
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {
        Cog.d(TAG, "onNestedPreScroll");
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Cog.d(TAG, "onNestedScroll");
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target) {
        Cog.d(TAG, "onStopNestedScroll");
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        Cog.d(TAG, "onInterceptTouchEvent");
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        Cog.d(TAG, "onTouchEvent");
        return super.onTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {
        Cog.d(TAG, "onNestedFling");
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout abl, int layoutDirection) {
        Cog.d(TAG, "onLayoutChild");
        return super.onLayoutChild(parent, abl, layoutDirection);
    }

    @Override
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, AppBarLayout appBarLayout) {
        Cog.d(TAG, "onSaveInstanceState");
        return super.onSaveInstanceState(parent, appBarLayout);
    }

    @Override
    public void onRestoreInstanceState(CoordinatorLayout parent, AppBarLayout appBarLayout, Parcelable state) {
        Cog.d(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(parent, appBarLayout, state);
    }
}
