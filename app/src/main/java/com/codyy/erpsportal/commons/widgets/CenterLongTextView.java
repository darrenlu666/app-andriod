package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;

/**
 * 用于解决太长居中左边滑不过去也看不到
 * Created by gujiajia on 2017/1/18.
 */

public class CenterLongTextView extends HorizontalScrollView {

    private TextView mTextView;

    public CenterLongTextView(Context context) {
        super(context);
        init(context, null);
    }

    public CenterLongTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CenterLongTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    public CenterLongTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = inflate(context, R.layout.center_long_text_view, this);
        mTextView = (TextView) view.findViewById(R.id.text_view);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.text});
            mTextView.setText(ta.getString(0));
            ta.recycle();
        }
        setHorizontalScrollBarEnabled(false);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int textWidth = mTextView.getMeasuredWidth();
        int measuredWidth = getMeasuredWidth();

        Cog.d("CenterLongTextView", "onLayout textWidth=", textWidth, ",measuredWidth=", measuredWidth);
        if (measuredWidth > textWidth) {
            int left = (measuredWidth - textWidth) / 2;
            mTextView.layout(left, getPaddingTop(), left + textWidth, getPaddingTop() + mTextView.getMeasuredHeight() );
            Cog.d("CenterLongTextView", "mTextView left=", mTextView.getLeft(),
                    ", top=", mTextView.getTop(),
                    ", right=", mTextView.getRight(),
                    ", bottom=", mTextView.getBottom());
        } else {
            super.onLayout(changed, l, t, r, b);
        }
    }

    public CharSequence getText() {
        return mTextView.getText();
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setText(@StringRes int resId) {
        mTextView.setText(resId);
    }
}
