package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.support.design.widget.AppBarLayout.ScrollingViewBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.codyy.erpsportal.commons.utils.Cog;

/**
 * Created by gujiajia on 2015/9/10.
 */
public class MapBehavior extends ScrollingViewBehavior {

    private final static String TAG = "MapBehavior";

    public MapBehavior() {
        super();
    }

    public MapBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        Cog.d(TAG, "layoutDependsOn child=" + child + ",dependency=" + dependency);
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        Cog.d(TAG, "onMeasureChild child=" + child + "");
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        Cog.d(TAG, "onDependentViewChanged child=" + child + ",dependency=" + dependency);
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
