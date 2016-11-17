package com.codyy.erpsportal.commons.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;

/**
 * 表情输入检测
 */
public class EmotionInputDetector {

    private static final String SHARE_PREFERENCE_NAME = "com.codyy.emotioninputdetector";
    private static final String SHARE_PREFERENCE_TAG = "soft_input_height";
    private final static int COMMENT_MAX_LENGTH = 150;
    private Activity mActivity;
    private InputMethodManager mInputManager;
    private SharedPreferences sp;
    private View mEmotionLayout;
    private EditText mEditText;
    private View mContentView;
    private Button mSendButton;
    private int mKeyboardDefaultHeight;

    private LengthFilter mLengthFilter;

//    private HeaderFilter mHeaderFilter;

    private EmotionInputDetector() {
    }

    public static EmotionInputDetector with(Activity activity) {
        EmotionInputDetector emotionInputDetector = new EmotionInputDetector();
        emotionInputDetector.mActivity = activity;
        emotionInputDetector.mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        emotionInputDetector.sp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        emotionInputDetector.mKeyboardDefaultHeight = UIUtils.dip2px(activity, 300);
        return emotionInputDetector;
    }

    /**
     * 绑定到内容视图
     *
     * @param contentView
     * @return
     */
    public EmotionInputDetector bindToContent(View contentView) {
        mContentView = contentView;
        return this;
    }

    /**
     * 绑定到发送/发表按钮
     *
     * @param send
     * @return
     */
    public EmotionInputDetector bindToSend(Button send) {
        mSendButton = send;
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSendClickListener != null) {
                    String content = mEditText.getText().toString();
                    if (!TextUtils.isEmpty(content.trim())) {
                        mOnSendClickListener.onSendClickListener(content);
                    }
                }
            }
        });
        return this;
    }

    private OnSendClickListener mOnSendClickListener;

    /**
     * 发送/发表按钮事件监听
     *
     * @param listener
     */
    public EmotionInputDetector setOnSendClickListener(OnSendClickListener listener) {
        mOnSendClickListener = listener;
        return this;
    }

    public interface OnSendClickListener {
        /**
         * 在这里发送评论请求，成功了得调用{@link EmotionInputDetector#hide()}
         * @param content
         */
        void onSendClickListener(String content);
    }

    /**
     * 绑定到文本框视图
     *
     * @param editText
     * @return
     */
    public EmotionInputDetector bindToEditText(EditText editText) {
        mEditText = editText;
        mEditText.requestFocus();
        mLengthFilter = new LengthFilter(COMMENT_MAX_LENGTH);
//        mHeaderFilter = new HeaderFilter();
//        mHeaderFilter.setOnHeaderChangingListener(new OnHeaderChangingListener() {
//            @Override
//            public void onTryingToChangeHeader() {
//                mEditText.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        moveCommentEditTextSelectionToEnd();
//                    }
//                });
//            }
//        });
        mEditText.setFilters(new InputFilter[]{mLengthFilter});
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mEmotionLayout.isShown()) {
                    lockContentHeight();
                    hideEmotionLayout(true);

                            unlockContentHeightDelayed();

                }
                return false;
            }
        });
        return this;
    }

//    private void moveCommentEditTextSelectionToEnd() {
//        mEditText.setSelection(mEditText.length());
//    }

    /**
     * 绑定到表情切换按钮
     *
     * @param emotionButton
     * @return
     */
    public EmotionInputDetector bindToEmotionButton(View emotionButton) {
        emotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmotionLayout.isShown()) {
                    lockContentHeight();
                    hideEmotionLayout(true);
                    unlockContentHeightDelayed();
                } else {
                    if (isSoftInputShown()) {
                        lockContentHeight();
                        showEmotionLayout();
                        unlockContentHeightDelayed();
                    } else {
                        showEmotionLayout();
                    }
                }
            }
        });
        return this;
    }

    /**
     * 设置表情View
     *
     * @param emotionView
     * @return
     */
    public EmotionInputDetector setEmotionView(View emotionView) {
        mEmotionLayout = emotionView;
        return this;
    }

    public EmotionInputDetector build() {
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        hideSoftInput();
        return this;
    }

    public boolean interceptBackPress() {
        // TODO: 15/11/2 change this method's name
        if (mEmotionLayout.isShown()) {
            hideEmotionLayout(false);
            return true;
        }
        return false;
    }

    public void showEmotionLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = sp.getInt(SHARE_PREFERENCE_TAG, mKeyboardDefaultHeight);
        }
        hideSoftInput();
        mEmotionLayout.getLayoutParams().height = softInputHeight;
        mEmotionLayout.setVisibility(View.VISIBLE);
    }

    public void hideEmotionLayout(boolean showSoftInput) {
        if (mEmotionLayout.isShown()) {
            mEmotionLayout.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            }
        }
    }

    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = mContentView.getHeight();
        params.weight = 0.0F;
    }

    private void unlockContentHeightDelayed() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }

    public void showSoftInput() {
        mEditText.requestFocus();
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(mEditText, 0);
            }
        });
    }

    public void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    public boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    public boolean isEmotionLayoutShown() {
        return mEmotionLayout.isShown();
    }

    /**
     * 隐藏软键盘和表情键盘
     */
    public void hide() {
        if (isSoftInputShown()) {
            hideSoftInput();
        }
        if (isEmotionLayoutShown()) {
            hideEmotionLayout(false);
        }
    }

    /**
     * 开始输入回复内容
     *
     * @param replyTo
     */
    public void startToInputReply(String replyTo) {
//        mHeaderFilter.setHeaderLength(0);
//        mEditText.setText(replyTo);
//        mLengthFilter.setMax(replyTo.length() + COMMENT_MAX_LENGTH);
//        mHeaderFilter.setHeaderLength(replyTo.length());
//        moveCommentEditTextSelectionToEnd();
        mEditText.getText().clear();
        mEditText.setHint(replyTo);
        showSoftInput();
    }

    /**
     * 清空回复人
     */
    public void clearReplyTo() {
        mEditText.setHint(R.string.say_something);
//        mLengthFilter.setMax(COMMENT_MAX_LENGTH);
//        mHeaderFilter.setHeaderLength(0);
    }

    /**
     * 清空回复内容
     */
    public void clear() {
        mEditText.getText().clear();
        hide();
    }

    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }
        if (softInputHeight < 0) {
            Log.w("EmotionInputDetector", "Warning: value of softInputHeight is below zero!");
        }
        if (softInputHeight > 0) {
            sp.edit().putInt(SHARE_PREFERENCE_TAG, softInputHeight).apply();
        }
        return softInputHeight;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /**
     * 输入最长限制，因为输入框有时会已“回复xxx：”开头，所有最长限制会有所变化
     */
    public static class LengthFilter implements InputFilter {
        private int mMax;

        public LengthFilter(int max) {
            mMax = max;
        }

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                   int dstart, int dend) {
            int keep = mMax - (dest.length() - (dend - dstart));
            if (keep <= 0) {
                return "";
            } else if (keep >= end - start) {
                return null; // keep original
            } else {
                keep += start;
                if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                    --keep;
                    if (keep == start) {
                        return "";
                    }
                }
                return source.subSequence(start, keep);
            }
        }

        /**
         * @return the maximum length enforced by this input filter
         */
        public int getMax() {
            return mMax;
        }

        public void setMax(int max) {
            this.mMax = max;
        }
    }

//    /**
//     * 输入框保持头有“回复某某”的输入过滤器
//     */
//    public static class HeaderFilter implements InputFilter {
//
//        private int mHeaderLength;
//
//        private OnHeaderChangingListener mOnHeaderChangingListener;
//
//        public HeaderFilter() {
//        }
//
//        public HeaderFilter(int headerLength) {
//            mHeaderLength = headerLength;
//        }
//
//        @Override
//        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//            if (mHeaderLength == 0) return null;
//            if (dstart < mHeaderLength) {
//                if (mOnHeaderChangingListener != null) {
//                    mOnHeaderChangingListener.onTryingToChangeHeader();
//                }
//                return dest.subSequence(dstart, dend);
//            }
//            return null;
//        }
//
//        public int getHeaderLength() {
//            return mHeaderLength;
//        }
//
//        public void setHeaderLength(int headerLength) {
//            mHeaderLength = headerLength;
//        }
//
//        public OnHeaderChangingListener getOnHeaderChangingListener() {
//            return mOnHeaderChangingListener;
//        }
//
//        public void setOnHeaderChangingListener(OnHeaderChangingListener onHeaderChangingListener) {
//            mOnHeaderChangingListener = onHeaderChangingListener;
//        }
//    }
//
//    interface OnHeaderChangingListener {
//        void onTryingToChangeHeader();
//    }
}
