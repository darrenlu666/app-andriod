package com.codyy.erpsportal.commons.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.codyy.erpsportal.R;

/**
 *
 * Created by gujiajia on 2015/10/28.
 */
public class RefreshLayout extends SwipeRefreshLayout implements AppBarLayout.OnOffsetChangedListener {

    private AppBarLayout mAppBarLayout;

    private int mAppBarLayoutId;

    public RefreshLayout(Context context) {
        super(context);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RefreshLayout);
            mAppBarLayoutId = ta.getResourceId(0, 0);
            ta.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getContext() instanceof Activity) {
            mAppBarLayout = (AppBarLayout) ((Activity) getContext()).findViewById(mAppBarLayoutId);
            if(null != mAppBarLayout){
                mAppBarLayout.addOnOffsetChangedListener(this);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if(null != mAppBarLayout){
            mAppBarLayout.removeOnOffsetChangedListener(this);
        }
        mAppBarLayout = null;
        super.onDetachedFromWindow();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        this.setEnabled(i == 0);
    }
}
