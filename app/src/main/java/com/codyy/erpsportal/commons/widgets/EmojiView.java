package com.codyy.erpsportal.commons.widgets;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 16-1-29.
 */
public class EmojiView extends LinearLayout {
    private ViewPager mViewPager;
    private List<Keyboard> mKeyboards;
    private Context mContext;
    private LayoutInflater mInflater;
    private EditText mEditText;
    private int mMaxLength;
    private final LayoutTransition transitioner = new LayoutTransition();//键盘和表情切换

    public EmojiView(Context context) {
        super(context);
        init(context, null);
    }

    public EmojiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EmojiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EmojiView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 表情和文本框绑定
     *
     * @param editText
     */
    public void setEditText(EditText editText) {
        mEditText = editText;
    }

    /**
     * 表情和文本框绑定
     *
     * @param editText
     */
    public void setEditText(EditText editText, int maxlength) {
        mEditText = editText;
        mMaxLength = maxlength;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (this.isInEditMode()) {
            return;
        }
        mViewPager.setAdapter(new EmojiAdapter());
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mKeyboards = new ArrayList<>();
        Keyboard keyboard0 = new Keyboard(context, R.xml.emojicon);
        Keyboard keyboard1 = new Keyboard(context, R.xml.emojicon1);
        Keyboard keyboard2 = new Keyboard(context, R.xml.emojicon2);
        mKeyboards.add(keyboard0);
        mKeyboards.add(keyboard1);
        mKeyboards.add(keyboard2);
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER);
        mViewPager = new ViewPager(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = (float) 1.0;
        mViewPager.setLayoutParams(layoutParams);
        this.addView(mViewPager);
        final RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        radioGroup.setOrientation(RadioGroup.HORIZONTAL);
        this.addView(radioGroup);
        for (int i = 0; i < mKeyboards.size(); i++) {
            RadioButton radioButton = new RadioButton(context);
            RadioGroup.LayoutParams layoutParams1 = new RadioGroup.LayoutParams(UIUtils.dip2px(context, 5), UIUtils.dip2px(context, 5));
            layoutParams1.setMargins(UIUtils.dip2px(context, 10), 0, 0, 0);
            radioButton.setLayoutParams(layoutParams1);
            radioButton.setButtonDrawable(null);
            radioButton.setId(i);
            radioButton.setBackgroundResource(R.drawable.select);
            radioGroup.addView(radioButton);
        }
        radioGroup.check(0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                radioGroup.check(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class EmojiAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mKeyboards.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            KeyboardView keyboardView = (KeyboardView) mInflater.inflate(R.layout.keyboardview, container, false);
            keyboardView.setKeyboard(mKeyboards.get(position));
            keyboardView.setPreviewEnabled(false);
            keyboardView.setEnabled(true);
            container.addView(keyboardView);
            keyboardView.setOnKeyboardActionListener(listener);
            return keyboardView;
        }
    }

    KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {

            if (mEditText != null) {
                Editable editable = mEditText.getText();
                InputUtils.VerticalImageSpan[] span = editable.getSpans(0, editable.length(), InputUtils.VerticalImageSpan.class);
                int start = mEditText.getSelectionStart();
                if (primaryCode == Keyboard.KEYCODE_DELETE) {
                    if (start > 0) {
                        for (int i = 0; i < span.length; i++) {
                            int a = editable.getSpanEnd(span[i]);
                            if (a == start) {
                                editable.delete(editable.getSpanStart(span[i]), start);
                                return;
                            }
                        }
                        editable.delete(start - 1, start);
                    }
                }
            }
        }

        @Override
        public void onText(CharSequence text) {
            if (mEditText != null) {
                if (mMaxLength > 0) {
                    if (mMaxLength - mEditText.getText().length() >= text.length()) {
                        InputUtils.setEmojiSpanEdit(mEditText, text.toString(), (int) mEditText.getTextSize() + 10);
                    } else {
                        return;
                    }
                } else {
                    InputUtils.setEmojiSpanEdit(mEditText, text.toString(), (int) mEditText.getTextSize() + 10);
                }
            }
        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };
}
