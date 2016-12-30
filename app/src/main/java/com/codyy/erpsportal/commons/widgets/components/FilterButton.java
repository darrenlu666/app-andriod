package com.codyy.erpsportal.commons.widgets.components;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.Button;

import com.codyy.erpsportal.R;

/**
 * 筛选按钮
 * 配合 MixBtnInLeftOfTitleBar 风格使用
 * Created by gujiajia on 2016/12/30.
 */
public class FilterButton extends Button{

    private boolean mIsFiltering;

    public FilterButton(Context context) {
        super(context);
        init();
    }

    public FilterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FilterButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public FilterButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        float  scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        int padding = (int)(8 * scaledDensity + 0.5f);
        ViewCompat.setBackground(this, null);
        setCompoundDrawablesWithIntrinsicBounds(0, 0 , R.drawable.filter_bg_selector, 0);
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0 , R.drawable.filter_bg_selector, 0);
        }
        setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        setPadding(padding, padding, padding, padding);
    }

    public void toggle() {
        setFiltering(!mIsFiltering);
    }

    public boolean isFiltering() {
        return mIsFiltering;
    }

    public void setFiltering(boolean filtering) {
        if (mIsFiltering == filtering) return;
        mIsFiltering = filtering;
        if (mIsFiltering) {
            setText(R.string.confirm_filter);
            setBackgroundResource(R.drawable.bg_btn_aqua);
            setCompoundDrawables(null, null, null, null);
        } else {
            setText("");
            setBackgroundResource(0);
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter, 0);
        }
    }
}
