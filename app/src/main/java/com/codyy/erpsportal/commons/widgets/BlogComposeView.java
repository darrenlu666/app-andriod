package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.EmojiViewPagerAdapter;
import com.codyy.erpsportal.commons.models.entities.Emojicon;
import com.codyy.erpsportal.commons.models.entities.People;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DeviceUtils;
import com.codyy.erpsportal.commons.utils.SoftKeyboardStateHelper;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.widgets.blog.CommentButton;
import com.viewpagerindicator.CirclePageIndicator;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义输入框，带表情(博文详情)
 */
public class BlogComposeView extends LinearLayout implements View.OnClickListener, SoftKeyboardStateHelper.SoftKeyboardStateListener, EmojiViewPagerAdapter.OnClickEmojiListener {
    public static final String TAG = "BlogComposeView";
    private static final int MAX_INPUT_SIZE = 150;
    private ImageView mIvEmoji;
    private Animation mShowAnim, mDismissAnim;
    private CommentButton mBtnSend;
    private EmojiconEditText mEtText;
    private OnComposeOperationDelegate mDelegate;
    private SoftKeyboardStateHelper mKeyboardHelper;
    private ViewPager mViewPager;
    private EmojiViewPagerAdapter mPagerAdapter;
    private int mCurrentKeyboardHeigh;
    private View mLyEmoji;
    private boolean mIsKeyboardVisible;
    private boolean mIsEmojiVisible;
    private boolean mNeedShowEmojiOnKeyboardClosed;

    public CommentButton getBtnSend() {
        return mBtnSend;
    }

    public EmojiconEditText getEtText() {
        return mEtText;
    }

    /**
     * 软键盘是否显示了
     *
     * @return
     */
    public boolean isKeyboardVisible() {
        return mIsKeyboardVisible;
    }

    /**
     * 表情框是否显示
     *
     * @return
     */
    public boolean isEmojiVisible() {
        return mIsEmojiVisible;
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                mBtnSend.setTextColor(UiMainUtils.getColor(R.color.white));
                mBtnSend.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.btn_send_enabled));
            } else {
                mBtnSend.setTextColor(UiMainUtils.getColor(R.color.gray));
                mBtnSend.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.btn_send_disable));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void showSendButton() {
        mShowAnim.reset();
        mBtnSend.clearAnimation();
        mBtnSend.startAnimation(mShowAnim);
    }

    private void dismissSendButton() {
        mBtnSend.clearAnimation();
        mBtnSend.startAnimation(mDismissAnim);
    }

    public BlogComposeView(Context context) {
        this(context, null);
    }

    public BlogComposeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private BlogComposeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private BlogComposeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private void init(Context context) {
        inflate(context, R.layout.view_input, this);

        mShowAnim = AnimationUtils.loadAnimation(context, R.anim.chat_show_send_button);
        mShowAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mBtnSend.setTextColor(getResources().getColor(R.color.white));
                mBtnSend.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_send_enabled));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mDismissAnim = AnimationUtils.loadAnimation(context, R.anim.chat_dismiss_send_button);
        mDismissAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBtnSend.setTextColor(getResources().getColor(R.color.gray));
                mBtnSend.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_send_disable));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mIvEmoji = (ImageView) findViewById(R.id.iv_emoji);
        mIvEmoji.setOnClickListener(this);
        mEtText = (EmojiconEditText) findViewById(R.id.et_text);
        mEtText.addTextChangedListener(mTextWatcher);
        mBtnSend = (CommentButton) findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);
//        mEtText.setFocusable(false);
        mLyEmoji = findViewById(R.id.ly_emoji);
        // TODO: 16-10-18 表情况在４．２．２以下需要处理高度.
//        if(Build.VERSION.SDK_INT<19){


        if (isInEditMode()) {
            return;
        }
        mKeyboardHelper = new SoftKeyboardStateHelper(((Activity) getContext()).getWindow()
                .getDecorView());
        mKeyboardHelper.addSoftKeyboardStateListener(this);


        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        /*LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(EApplication.instance().getResources().getDisplayMetrics().widthPixels, UIUtils.dip2px(EApplication.instance(),180));
        mViewPager.setLayoutParams(param2);*/
        int emojiHeight = caculateEmojiPanelHeight();

        Emojicon[] emojis = People.DATA;
        List<List<Emojicon>> pagers = new ArrayList<>();
        List<Emojicon> es = null;
        int size = 0;
        boolean justAdd = false;
        for (Emojicon ej : emojis) {
            if (size == 0) {
                es = new ArrayList<>();
            }
            if (size == 27) {
                es.add(new Emojicon(""));
            } else {
                es.add(ej);
            }
            size++;
            if (size == 28) {
                pagers.add(es);
                size = 0;
                justAdd = true;
            } else {
                justAdd = false;
            }
        }
        if (!justAdd && es != null) {
            int exSize = 28 - es.size();
            for (int i = 0; i < exSize; i++) {
                es.add(new Emojicon(""));
            }
            pagers.add(es);
        }

        mPagerAdapter = new EmojiViewPagerAdapter(getContext(), pagers,
                emojiHeight, this);
        mViewPager.setAdapter(mPagerAdapter);

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
    }


    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.btn_send) {
            if (mDelegate != null) {
                String encodeMsg = URLEncoder.encode(mEtText.getText().toString());
                mDelegate.onSendText(encodeMsg);
                clearText();
            }
        } else if (id == R.id.iv_emoji) {//点击表情
            if (mLyEmoji.getVisibility() == View.GONE) {
                mNeedShowEmojiOnKeyboardClosed = true;
                tryShowEmojiPanel();
            } else {
                tryHideEmojiPanel();
            }
        }
    }


    private int caculateEmojiPanelHeight() {
        mCurrentKeyboardHeigh = EApplication.getSoftKeyboardHeight();
        if (mCurrentKeyboardHeigh == 0 ) {
            mCurrentKeyboardHeigh = (int) DeviceUtils.dpToPixel(200);
        }

        int emojiPanelHeight = (int) (mCurrentKeyboardHeigh - DeviceUtils.dpToPixel(20));
        int emojiHeight = emojiPanelHeight / 4;
        Cog.i(TAG,"caculateEmojiPanelHeight() :: emojiPanelHeight : "+emojiPanelHeight +" mCurrentKeyboardHeigh:"+mCurrentKeyboardHeigh);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, emojiPanelHeight);
        /*LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(EApplication.instance().getResources().getDisplayMetrics().widthPixels, mCurrentKeyboardHeigh);
        mLyEmoji.setLayoutParams(param);*/
        mViewPager.setLayoutParams(lp);
        if (mPagerAdapter != null) {
            mPagerAdapter.setEmojiHeight(emojiHeight);
        }
        return emojiHeight;
    }

    private void tryShowEmojiPanel() {
        if (mIsKeyboardVisible) {
            hideKeyboard();
        } else {
            showEmojiPanel();
        }
        /*showEmojiPanel();
        hideKeyboard();*/
    }

    private void tryHideEmojiPanel() {
        if (!mIsKeyboardVisible) {
            showKeyboard();
        } else {
            hideEmojiPanel();
        }
    }

    private void showEmojiPanel() {
        mNeedShowEmojiOnKeyboardClosed = false;
        mLyEmoji.setVisibility(View.VISIBLE);
//        mIvEmoji.setImageResource(R.drawable.ic_laugh);
        mIsEmojiVisible = true;
        if (null != mDelegate) mDelegate.onEmojiPanOpen();
    }

    private void hideEmojiPanel() {
        if (mLyEmoji.getVisibility() == View.VISIBLE) {
            mLyEmoji.setVisibility(View.GONE);
//            mIvEmoji.setImageResource(R.drawable.ic_laugh);
            mIsEmojiVisible = false;
            if (null != mDelegate) mDelegate.onEmojiPanClose();
        }
    }

    public void hideEmojiOptAndKeyboard() {
        hideEmojiPanel();
        DeviceUtils.hideSoftKeyboard(mEtText);
    }


    private void hideKeyboard() {
        DeviceUtils.hideSoftKeyboard(mEtText);
    }

    private void showKeyboard() {
        mEtText.requestFocus();
        DeviceUtils.showSoftKeyboard(mEtText);
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        int realKeyboardHeight = keyboardHeightInPx
                - DeviceUtils.getStatusBarHeight();
        Cog.i(TAG,"keyH:"+keyboardHeightInPx +" status:"+DeviceUtils.getStatusBarHeight()+" realKeyBoard :"+realKeyboardHeight);

        EApplication.setSoftKeyboardHeight(realKeyboardHeight);
        if (mCurrentKeyboardHeigh != realKeyboardHeight) {
            caculateEmojiPanelHeight();
        }

        mIsKeyboardVisible = true;
        hideEmojiPanel();
        if (null != mDelegate) {
            mDelegate.onSoftWareKeyOpen();
        }
    }

    @Override
    public void onSoftKeyboardClosed() {
        mIsKeyboardVisible = false;
        if (mNeedShowEmojiOnKeyboardClosed) {
            showEmojiPanel();
        }

        if (null != mDelegate) {
            mDelegate.onSoftWareKeyClose();
        }
    }


    public static void input(EditText editText, Emojicon emojicon) {
        if (editText == null || emojicon == null) {
            return;
        }
        Cog.d("xxxx", URLEncoder.encode(emojicon.getEmoji()));
        String emojiconStr = getEmojiForChinese(URLEncoder.encode(emojicon.getEmoji()));
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start < 0) {
            editText.append(emojiconStr);
        } else {
            int nowLength = editText.getText().toString().length();
            if((nowLength+emojiconStr.length())<=MAX_INPUT_SIZE){
                editText.getText().replace(Math.min(start, end), Math.max(start, end),
                        emojiconStr, 0, emojiconStr.length());
            }
        }
    }

    public static void backspace(EditText editText) {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        editText.dispatchKeyEvent(event);
    }


    @Override
    public void onDelete() {
        backspace(mEtText);
    }

    public void setOperationDelegate(OnComposeOperationDelegate delegate) {
        mDelegate = delegate;
    }

    public void clearText() {
        if (mEtText != null) {
            mEtText.setText("");
        }
    }

    @Override
    public void onEmojiClick(Emojicon emoji) {
        input(mEtText, emoji);
    }

    public static boolean containsAny(String str, String searchChars) {

        return str.contains(searchChars);
    }


    private static String getEmojiForChinese(String encodeMsg1) {
        String encodeMsg = encodeMsg1;
        switch (encodeMsg) {
            case "%F0%9F%98%95":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%95", "[示爱]");
                break;
            case "%F0%9F%98%98":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%98", "[大哭]");
                break;
            case "%F0%9F%98%84":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%84", "[傲慢]");
                break;
            case "%F0%9F%98%83":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%83", "[白眼]");
                break;
            case "%F0%9F%98%80":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%80", "[便便]");
                break;
            case "%F0%9F%98%8A":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%8A", "[鄙视]");
                break;
            case "%E2%98%BA":
                encodeMsg = encodeMsg.replaceAll("%E2%98%BA", "[擦汗]");
                break;
            case "%F0%9F%98%89":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%89", "[菜刀]");
                break;
            case "%F0%9F%98%8D":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%8D", "[呲牙]");
                break;
            case "%F0%9F%98%9A":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%9A", "[得意]");
                break;
            case "%F0%9F%98%97":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%97", "[发怒]");
                break;
            case "%F0%9F%98%99":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%99", "[尴尬]");
                break;
            case "%F0%9F%98%9C":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%9C", "[害羞]");
                break;
            case "%F0%9F%98%9D":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%9D", "[汗]");
                break;
            case "%F0%9F%98%9B":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%9B", "[憨笑]");
                break;
            case "%F0%9F%98%B3":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%B3", "[花]");
                break;
            case "%F0%9F%98%81":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%81", "[惊恐]");
                break;
            case "%F0%9F%98%94":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%94", "[惊讶]");
                break;
            case "%F0%9F%98%8C":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%8C", "[可爱]");
                break;
            case "%F0%9F%98%92":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%92", "[抠鼻]");
                break;
            case "%F0%9F%98%9E":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%9E", "[耍酷]");
                break;
            case "%F0%9F%98%A3":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%A3", "[流泪]");
                break;
            case "%F0%9F%98%A2":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%A2", "[难过]");
                break;
            case "%F0%9F%98%82":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%82", "[撇嘴]");
                break;
            case "%F0%9F%98%AD":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%AD", "[敲打]");
                break;
            case "%F0%9F%98%AA":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%AA", "[亲亲]");
                break;
            case "%F0%9F%98%A5":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%A5", "[色]");
                break;
            case "%F0%9F%98%B0":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%B0", "[胜利]");
                break;
            case "%F0%9F%98%93":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%93", "[衰]");
                break;
            case "%F0%9F%98%A9":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%A9", "[耍酷]");
                break;
            case "%F0%9F%98%AB":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%AB", "[睡]");
                break;
            case "%F0%9F%98%A8":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%A8", "[微笑]");
                break;
            case "%F0%9F%98%B1":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%B1", "[偷笑]");
                break;
            case "%F0%9F%98%A0":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%A0", "[吐]");
                break;
            case "%F0%9F%98%A1":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%A1", "[委屈]");
                break;
            case "%F0%9F%98%A4":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%A4", "[微笑]");
                break;
            case "%F0%9F%98%96":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%96", "[心]");
                break;
            case "%F0%9F%98%86":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%86", "[心裂]");
                break;
            case "%F0%9F%98%8B":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%8B", "[嘘]");
                break;
            case "%F0%9F%98%B7":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%B7", "[阴险]");
                break;
            case "%F0%9F%98%8E":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%8E", "[疑问]");
                break;
            case "%F0%9F%98%B4":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%B4", "[再见]");
                break;
            case "%F0%9F%98%B5":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%B5", "[炸弹]");
                break;
            case "%F0%9F%98%B2":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%B2", "[抓狂]");
                break;
            case "%F0%9F%98%9F":
                encodeMsg = encodeMsg.replaceAll("%F0%9F%98%9F", "[猪头]");
                break;
            default:
                break;

        }
        return encodeMsg;
    }

    public interface OnComposeOperationDelegate {
        /**
         * 发送评论
         *
         * @param text
         */
        void onSendText(String text);

        /**
         * 打开软键盘
         */
        void onSoftWareKeyOpen();

        /**
         * 关闭软键盘
         */
        void onSoftWareKeyClose();

        /**
         * 显示表情键盘
         */
        void onEmojiPanOpen();

        /**
         * 隐藏表情键盘
         */
        void onEmojiPanClose();
    }


}

