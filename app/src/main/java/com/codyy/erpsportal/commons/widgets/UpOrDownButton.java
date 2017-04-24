package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;

/**
 * 用于列表顶部的升序降序控件
 * Created by yangxinwu on 2015/7/29.
 */
public class UpOrDownButton extends RelativeLayout implements View.OnClickListener {
    private TextView mTextView;
    private boolean isUp = false;
    private boolean mIsChecked = false;

    private OnStateChangedListener mStateChangedListener;

    public UpOrDownButton(Context context) {
        super(context);
    }

    public UpOrDownButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UpOrDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UpOrDownButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.up_or_down_button_view, this, true);
        mTextView = (TextView) findViewById(R.id.tv_title);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.text});
            String title = ta.getString(0);
            if (!TextUtils.isEmpty(title)) {
                mTextView.setText(title);
            }
            ta.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnClickListener(this);
    }

    /**
     * 设置当前控件的标题
     */
    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    /**
     * 设置当前控件的标题
     *
     * @param stringId
     */
    public void setText(@StringRes int stringId) {
        mTextView.setText(stringId);
    }

    /**
     * 选中当前控件做的操作
     */
    public void setChecked() {
        Drawable icon;
        if (isUp) {
            icon = ContextCompat.getDrawable(mTextView.getContext(),R.drawable.ic_green_arrow_up);
            isUp = false;
        } else {
            icon = ContextCompat.getDrawable(mTextView.getContext(),R.drawable.ic_green_arrow_down);
            isUp = true;
        }
        mTextView.setTextColor(UiMainUtils.getColor(R.color.main_green));
        mTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
    }

    /**
     * 返回当前是升序还是降序
     *
     * @return true 升序  false 降序
     */
    public boolean getCurUpOrDown() {
        return isUp;
    }

    public boolean isUp() {
        return isUp;
    }

    /**
     * 还原为最初控件的样子
     */
    public void setInitView() {
        Drawable icon = ContextCompat.getDrawable(mTextView.getContext(),R.drawable.ic_grey_arrow_down);//getResources().getDrawable(R.drawable.img_down);
        mTextView.setTextColor(UiMainUtils.getColor(R.color.gray));
        isUp = false;
        mTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
    }

    public void setChecked(boolean isChecked) {
        this.mIsChecked = isChecked;
        if(isChecked()) {
            mTextView.setTextColor(getResources().getColor(R.color.main_green));
        } else {
            mTextView.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    public void setUp(boolean isUp) {
        this.isUp = isUp;
        if (isUp()) {
            mTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_up, 0);
        } else {
            mTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.img_down, 0);
        }
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void onClick(View v) {
        if ( !isChecked()) {
            setChecked(true);
            if (mStateChangedListener != null) {
                mStateChangedListener.onSelected(isUp());
            }
        } else {
            setUp( !isUp());
            if (mStateChangedListener != null) {
                if (isUp()) {
                    mStateChangedListener.onUp();
                } else {
                    mStateChangedListener.onDown();
                }
            }
        }
    }

    public OnStateChangedListener getStateChangedListener() {
        return mStateChangedListener;
    }

    public void setStateChangedListener(OnStateChangedListener stateChangedListener) {
        this.mStateChangedListener = stateChangedListener;
    }

    public interface OnStateChangedListener {
        void onSelected(boolean isUp);
        void onUp();
        void onDown();
    }

}
