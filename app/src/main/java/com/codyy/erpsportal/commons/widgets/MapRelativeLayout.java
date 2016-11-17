package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout.DefaultBehavior;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;

/**
 * Created by gujiajia on 2015/9/9.
 */
@DefaultBehavior(MapBehavior.class)
public class MapRelativeLayout extends RelativeLayout {

    private final static String TAG = "MapRelativeLayout";

    private int mTitleBarHeight;

    private float mDensity;

    private int mParentHeight = 0;

    public MapRelativeLayout(Context context) {
        this(context, null);
    }

    public MapRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MapRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        mDensity = getResources().getDisplayMetrics().density;
        mTitleBarHeight = getResources().getDimensionPixelSize(R.dimen.title_height);
        Cog.d(TAG, "init mTitleBarHeight=" + mTitleBarHeight);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Cog.d(TAG, "onMeasure height=" + MeasureSpec.getMode( heightMeasureSpec) + "," + MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mParentHeight <= 0) {
            NestedScrollView parentView = (NestedScrollView) getParent();
            mParentHeight = parentView.getMeasuredHeight();
        }
        Cog.d(TAG, "onMeasure parentHeight=" + mParentHeight);
        RelativeLayout statPanel = (RelativeLayout) findViewById(R.id.stat_panel);
        WebView mapView = (WebView) findViewById(R.id.map_view);
        Cog.d(TAG, "statPanel height=" + statPanel.getMeasuredHeight());
        int totalHeight = 0;
        totalHeight = getChildMeasuredHeightByViewId(R.id.btn_slide_up)
                + getChildMeasuredHeightByViewId(R.id.area_name)
                + getChildMeasuredHeightByViewId(R.id.class_status)
                + getChildMeasuredHeightByViewId(R.id.lb_main_classroom_count)
                + (int)(40 * mDensity + 0.5f);
//        int bottomHeight = getChildMeasuredHeightByViewId(R.id.lb_planed_total) * 3
//                + (int)(100 * density)
//                + getChildMeasuredHeightByViewId(R.id.lb_classroom_semester);
//        totalHeight = statPanel.getMeasuredHeight() - bottomHeight;
        Cog.d(TAG, "statPanel need show part height=" + totalHeight);
        int mapHeight = mParentHeight - totalHeight - mTitleBarHeight;
        LayoutParams layoutParams = (LayoutParams) statPanel.getLayoutParams();
        layoutParams.topMargin = mapHeight;

        LayoutParams mapLayoutParams = (LayoutParams) mapView.getLayoutParams();
        mapLayoutParams.height = mapHeight;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(parentHeight + bottomHeight, MeasureSpec.EXACTLY));
    }

    private int getChildMeasuredHeightByViewId(@IdRes int viewId) {
        int height = findViewById(viewId).getMeasuredHeight();
//        Cog.d(TAG, "getChildMeasuredHeightByViewId viewId=" + viewId + ",height=" + height);
        return height;
    }

}
