package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;

/**
 * 标题栏
 * Created by gujiajia on 2015/8/4.
 */
public class TitleBar extends RelativeLayout {

    private final static int DEFAULT_HEIGHT_DP = 50;

    private int mDefaultHeight;

    private TextView mTitleTv;

    private Button mReturnBtn;

    /**
     * 标题，xml中android:text中的属性
     */
    private String mTitle;

    private boolean mIsTitleInCenter;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.title_bar, this, true);
        mDefaultHeight = UIUtils.dip2px(context, DEFAULT_HEIGHT_DP);
        if (attrs != null) {
            TypedArray defAttrs = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
            mTitle = defAttrs.getString(R.styleable.TitleBar_android_text);
            mIsTitleInCenter = defAttrs.getBoolean(R.styleable.TitleBar_isTitleInCenter, true);
            defAttrs.recycle();
        }

        mTitleTv = (TextView) findViewById(R.id.title);
        mReturnBtn = (Button) findViewById(R.id.btn_return);
        if (!mIsTitleInCenter) {
            LayoutParams layoutParams = (LayoutParams) mTitleTv.getLayoutParams();
            layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.btn_return);
            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.addRule(RelativeLayout.END_OF, R.id.btn_return);
            }
            mTitleTv.setMaxEms(12);
        }
        if (mTitle != null)
            mTitleTv.setText(mTitle);
        //添加默认Listener，退出当前Activity
        mReturnBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    activity.finish();
                    UIUtils.addExitTranAnim(activity);
                }
            }
        });
        setBackgroundResource(R.color.main_green);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = Math.min(mDefaultHeight, heightSize);
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = mDefaultHeight;
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setTitle(@StringRes int stringId) {
        mTitleTv.setText(stringId);
    }

    public void setTitle(CharSequence title) {
        mTitleTv.setText(title);
    }

    public void setText(CharSequence title) {
        mTitleTv.setText(title);
    }

    /**
     * 添加返回按钮回调，默认回调为退出Activity
     * @param listener
     */
    public void setOnReturnClickListener(OnClickListener listener) {
        mReturnBtn.setOnClickListener(listener);
    }
}
