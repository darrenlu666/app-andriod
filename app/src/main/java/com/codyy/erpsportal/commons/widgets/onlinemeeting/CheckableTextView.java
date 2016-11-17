package com.codyy.erpsportal.commons.widgets.onlinemeeting;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.TextView;

/**
 * noting to do  .
 * Created by poe on 16-7-19.
 */
public class CheckableTextView extends TextView implements Checkable{
//    private static final String TAG = "CheckableTextView";
    /**check state default:false */
    private boolean mIsCheckState = false;

    public CheckableTextView(Context context) {
        super(context);
    }

    public CheckableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckableTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    public void setChecked(boolean checked) {
        if(mIsCheckState != checked){
            mIsCheckState = checked;
            //refresh UI
            invalidate();
        }
    }

    @Override
    public boolean isChecked() {
        return mIsCheckState;
    }

    @Override
    public void toggle() {
        setChecked(!mIsCheckState);
    }
}
