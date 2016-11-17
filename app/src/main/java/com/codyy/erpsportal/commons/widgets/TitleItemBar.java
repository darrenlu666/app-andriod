package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;

/**
 * 右边有更多的小标题
 * Created by gujiajia on 2015/9/7.
 */
public class TitleItemBar extends RelativeLayout {

    private final static String TAG = "TitleItemBar";

    private OnMoreClickListener mOnMoreClickListener;

    private Button mMoreBtn;

    private TextView mTitleTv;

    private boolean mHasMore;

    public TitleItemBar(Context context) {
        super(context);
        init(null);
    }

    public TitleItemBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TitleItemBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TitleItemBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.title_item_bar, this, true);
        /**
         * add by kmdai
         * 设置头部空白
         */
        setPadding(0, UIUtils.dip2px(getContext(), 15), 0, 0);
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mMoreBtn = (Button) findViewById(R.id.btn_more);
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TitleItemBar);
            String title = ta.getString(R.styleable.TitleItemBar_android_text);
            mHasMore = ta.getBoolean(R.styleable.TitleItemBar_hasMore, true);
            if (!TextUtils.isEmpty(title)) {
                mTitleTv.setText(title);
            }
            ta.recycle();
        }

        if (mHasMore) {
            mMoreBtn.setVisibility(View.VISIBLE);
        } else {
            mMoreBtn.setVisibility(View.GONE);
        }
        mMoreBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnMoreClickListener != null) {
                    mOnMoreClickListener.onMoreClickListener(TitleItemBar.this);
                }
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Cog.d(TAG, "onMeasure widthMeasureSpec=" + MeasureSpec.toString(widthMeasureSpec)
//                + ",heightMeasureSpec=" + MeasureSpec.toString(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Cog.d(TAG, "onMeasureFinish widthMeasureSpec=" + MeasureSpec.toString(getMeasuredWidth())
//                + ",heightMeasureSpec=" + MeasureSpec.toString(getMeasuredHeight()));
    }



    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

    public void setTitle(@StringRes int stringId) {
        mTitleTv.setText(stringId);
    }

    public boolean hasMore() {
        return mHasMore;
    }

    public void setHasMore(boolean hasMore) {
        if (mHasMore != hasMore) {
            mHasMore = hasMore;
            if (mMoreBtn != null) mMoreBtn.setVisibility(hasMore() ? View.VISIBLE : View.GONE);
        }
    }

    public OnMoreClickListener getOnMoreClickListener() {
        return mOnMoreClickListener;
    }

    public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener) {
        if (onMoreClickListener != null) {
            setHasMore(true);
        } else {
            setHasMore(false);
        }
        mOnMoreClickListener = onMoreClickListener;
    }

    public interface OnMoreClickListener {
        void onMoreClickListener(View view);
    }

}
