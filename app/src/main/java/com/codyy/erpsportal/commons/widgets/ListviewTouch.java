package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by kmdai on 2015/4/27.
 */
public class ListviewTouch extends ListView {
    public ListviewTouch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ListviewTouch(Context context) {
        super(context);
    }

    public ListviewTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//
//        return false;
//    }
}
