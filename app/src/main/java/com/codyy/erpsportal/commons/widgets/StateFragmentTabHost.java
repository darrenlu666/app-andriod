package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;

import com.codyy.erpsportal.commons.utils.Cog;

/**
 * 防止Can not perform this action after onSaveInstanceState的FragmentTabHost
 * Created by gujiajia on 2016/10/24.
 */

public class StateFragmentTabHost extends FragmentTabHost {
    public StateFragmentTabHost(Context context) {
        super(context);
    }

    public StateFragmentTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        try {
            super.onAttachedToWindow();
        } catch (IllegalStateException e) {
            Cog.e("StateFragmentTabHost", e.getMessage());
        }
    }
}
