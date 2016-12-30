package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;

/**
 * 自动补全TextView
 * Created by gujiajia on 2016/8/3.
 */
public class InstantAutoCompleteTv extends AutoCompleteTextView {
    public InstantAutoCompleteTv(Context context) {
        super(context);
    }

    public InstantAutoCompleteTv(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InstantAutoCompleteTv(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public InstantAutoCompleteTv(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused && getAdapter() != null) {
            performFiltering("", KeyEvent.KEYCODE_UNKNOWN);
            showDropDown();
        }
    }
}
