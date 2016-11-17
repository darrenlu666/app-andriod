package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;

/**
 * Created by poe on 16-3-13.
 */
public class MyBottomSheet extends BottomSheetDialog{

    private BottomSheetBehavior mBottomSheetBehavior;


    public MyBottomSheet(@NonNull Context context) {
        super(context);
    }

    public MyBottomSheet(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
    }

    protected MyBottomSheet(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
//        mBottomSheetBehavior    =   BottomSheetBehavior.from(view);
    }

    public BottomSheetBehavior getBottomSheetBehavior() {
        return mBottomSheetBehavior;
    }
}
