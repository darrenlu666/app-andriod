package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;


/**
 * Created by gujiajia on 2015/4/28.
 */
public class EmptyView extends RelativeLayout {
    private final static String TAG = "EmptyView";

    private boolean mLoading;

    private TextView mEmptyTv;

    private ProgressBar mLoadingBar;

    private OnReloadClickListener mOnReloadClickListener;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.empty_view, this, true);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.EmptyView);
        if (typedArray != null) {
            mLoading = typedArray.getBoolean(R.styleable.EmptyView_isLoading, false);
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mEmptyTv = (TextView) findViewById(R.id.tv_empty);
        mLoadingBar = (ProgressBar) findViewById(R.id.pro_loading);
        Drawable drawable = getResources().getDrawable(R.drawable.progress_loading);
        mLoadingBar.setIndeterminateDrawable(drawable);
        SpannableString spStr = getSpannableString(R.string.no_data_for_now);
        mEmptyTv.setText(spStr);
        mEmptyTv.setHighlightColor(Color.TRANSPARENT);
        mEmptyTv.setMovementMethod(LinkMovementMethod.getInstance());
        if (mLoading) {
            mEmptyTv.setVisibility(View.GONE);
            mLoadingBar.setVisibility(View.VISIBLE);
        } else {
            mEmptyTv.setVisibility(View.VISIBLE);
            mLoadingBar.setVisibility(View.GONE);
        }
    }

    @NonNull
    private SpannableString getSpannableString(int str) {
        ClickableSpan mMyClickableSpan = new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                Cog.d(TAG, "updateDrawState");
                ds.setColor(getResources().getColor(R.color.green));
            }

            @Override
            public void onClick(View widget) {
                if (mOnReloadClickListener != null)
                    mOnReloadClickListener.onReloadClick();
            }
        };

        SpannableString spStr = new SpannableString(getContext().getString(str));
        spStr.setSpan(mMyClickableSpan, 0, spStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spStr;
    }

    public void setOnReloadClickListener(OnReloadClickListener onReloadClickListener) {
        this.mOnReloadClickListener = onReloadClickListener;
    }

    public void setLoading(boolean isLoading) {
        mLoading = isLoading;
        if (mLoading) {
            mEmptyTv.setVisibility(View.GONE);
            mLoadingBar.setVisibility(View.VISIBLE);
        } else {
            mEmptyTv.setVisibility(View.VISIBLE);
            mLoadingBar.setVisibility(View.GONE);
        }
    }

    public void setText(int title){
        if(null != mEmptyTv) {
            SpannableString spStr = getSpannableString(title);
            mEmptyTv.setText(spStr);
        }
    }

    public boolean isLoading() {
        return mLoading;
    }

    public interface OnReloadClickListener {
        void onReloadClick();
    }
}
