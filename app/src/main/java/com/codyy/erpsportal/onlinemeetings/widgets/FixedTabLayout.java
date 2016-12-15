package com.codyy.erpsportal.onlinemeetings.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;

import com.codyy.erpsportal.R;

/**
 * Created by poe on 12/14/16.
 */

public class FixedTabLayout extends TabLayout{

    public FixedTabLayout(Context context) {
        this(context,null);
    }

    public FixedTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FixedTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //get ａttr about average .
        boolean isAverageTabs = false;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlidingTabLayout);
            isAverageTabs = typedArray.getBoolean(R.styleable.SlidingTabLayout_isAverageTabs, false);
            typedArray.recycle();
        }

        if(isAverageTabs){
            setTabMode(TabLayout.MODE_FIXED);
        }
    }

    @Override
    public void addTab(@NonNull Tab tab) {
        super.addTab(tab);
        //如果当前tab的文字超过１５
        if(getTabCount()>2){

            int tabSize = 0;
            for(int i =0;i < getTabCount();i++){
               tabSize += getTabAt(i).getText().length();
            }

            if(tabSize > 15){
                setTabMode(TabLayout.MODE_SCROLLABLE);
            }
        }
    }
}
