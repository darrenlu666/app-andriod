package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;


public class AsyncTextView extends TextView implements AwareView {
    private DisplayListener listenerWeakReference;
    private Object holder;

    public AsyncTextView(Context context) {
        super(context);
    }

    public AsyncTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AsyncTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AsyncTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setHolder(Object object) {
        holder = object;
    }

    @Override
    public Object getHolder() {
        return holder;
    }

    @Override
    public void setText(String text) {
        super.setText(text);
    }

    @Override
    public DisplayListener getDisplayListener() {
        return listenerWeakReference;
    }

    @Override
    public void setDisplayListener(DisplayListener listener) {
        listenerWeakReference = listener;//new WeakReference<>(listener);
    }
}
