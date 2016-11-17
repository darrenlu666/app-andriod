package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TabWidget;

/**
 * Created by gujiajia on 2015/4/9.
 */
public class MyTabWidget extends TabWidget {

    private OnTabClickListener mOnTabClickListener;

    public MyTabWidget(Context context) {
        super(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyTabWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyTabWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyTabWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        child.setOnClickListener(new TabClickListener(getTabCount() - 1));
    }

    private class TabClickListener implements OnClickListener{
        private int index;

        public TabClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            if (mOnTabClickListener != null) mOnTabClickListener.onTabClick(index);
        }
    }

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.mOnTabClickListener = onTabClickListener;
    }

    public interface OnTabClickListener{
        void onTabClick(int index);
    }
}
