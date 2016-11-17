package com.codyy.erpsportal.weibo.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.RefreshRecycleView;
import com.codyy.erpsportal.weibo.controllers.fragments.WeiBoUniversalFragment;

import butterknife.Bind;

public class WeiBoUniversalActivity extends ToolbarActivity {
    /**
     * 标题
     */
    public static final String KEY_TITLE = "key_title";
    /**
     * 找人
     */
    public static final String FIND_PEOPLE = "找人";
    /**
     * 粉丝
     */
    public static final String MY_FANS = "粉丝";
    /**
     * 关注
     */
    public static final String MY_FOLLOW = "我的关注";
    /**
     * 私信
     */
    public static final String MY_MSG = "私信";
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTitleTxt;
    /**
     * 搜索
     */
    @Bind(R.id.weibo_search_edittext)
    EditText mEditText;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.weibo_search_edittext_delete)
    ImageView mEditextDelete;
    @Bind(R.id.title_layout)
    RelativeLayout mTitleLayout;
    private String mKeyCache;
    private String mType;
    private WeiBoUniversalFragment mWeiBoUniversalFragment;
    private InputMethodManager mInputMethodManager;
    private int mTitleHight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (TextUtils.isEmpty(mEditText.getText()) && mType.equals(FIND_PEOPLE)) {
                    Snackbar.make(v, getResources().getString(R.string.weibo_search_people), Snackbar.LENGTH_SHORT).show();
                    return true;
                } else {
                    mWeiBoUniversalFragment.searchInPut(mEditText.getText().toString());
                    mKeyCache = mEditText.getText().toString();
                    InputUtils.hideSoftInputFromWindow(WeiBoUniversalActivity.this, mEditText);
                    return true;
                }
            }
            return false;
        }
    };

    private void init() {
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mWeiBoUniversalFragment = (WeiBoUniversalFragment) getSupportFragmentManager().findFragmentById(R.id.weibouniversalfragment);
//        mEditText.setOnKeyListener(mOnKeyListener);会调用两次监听回调
        mEditText.setOnEditorActionListener(mOnEditorActionListener);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (mEditextDelete.getVisibility() == View.GONE) {
                        mEditextDelete.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mEditextDelete.getVisibility() == View.VISIBLE) {
                        mEditextDelete.setVisibility(View.GONE);
                    }
                }
            }
        });
        mEditextDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.setText("");
            }
        });
        String type = getIntent().getExtras().getString(KEY_TITLE);
        if (null != type) {
            switch (type) {
                case FIND_PEOPLE:
                    mEditText.setFocusable(true);
                    mEditText.setFocusableInTouchMode(true);
                    mWeiBoUniversalFragment.setType(WeiBoUniversalFragment.TYPE_FIND_PEOPLE);
                    break;
                case MY_FOLLOW:
                    //清除edittext focus，键盘不弹出
                    mEditText.clearFocus();
                    mToolbar.setFocusable(true);
                    mToolbar.setFocusableInTouchMode(true);
                    mEditText.setFocusable(false);
                    mEditText.setFocusableInTouchMode(true);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    mWeiBoUniversalFragment.setType(WeiBoUniversalFragment.TYPE_MY_FOLLOW);
                    break;
                case MY_FANS:
                    //清除edittext focus，键盘不弹出
                    mEditText.clearFocus();
                    mToolbar.setFocusable(true);
                    mToolbar.setFocusableInTouchMode(true);
                    mEditText.setFocusable(false);
                    mEditText.setFocusableInTouchMode(true);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    mWeiBoUniversalFragment.setType(WeiBoUniversalFragment.TYPE_MY_FANS);
                    break;
                case MY_MSG:
                    //清除edittext focus，键盘不弹出
                    mEditText.clearFocus();
                    mToolbar.setFocusable(true);
                    mToolbar.setFocusableInTouchMode(true);
                    mEditText.setFocusable(false);
                    mEditText.setFocusableInTouchMode(true);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    mWeiBoUniversalFragment.setType(WeiBoUniversalFragment.TYPE_MY_MSG);
                    break;
            }
        }
        mWeiBoUniversalFragment.setOnTouch(new RefreshRecycleView.OnTouch() {
            @Override
            public boolean onTouch(MotionEvent event) {
                if (removeFocus()) {
                    mEditText.setText(mKeyCache);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_wei_bo_universal;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (removeFocus()) {
                return true;
            }
            if (MY_MSG.equals(getIntent().getExtras().getString(KEY_TITLE))) {
                setResult(RESULT_OK);
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onBack() {
        super.onBack();
        if (MY_MSG.equals(getIntent().getExtras().getString(KEY_TITLE))) {
            setResult(RESULT_OK);
        }
    }

    private boolean removeFocus() {
        if (mEditText.hasFocus() || mInputMethodManager.isActive(mEditText)) {
            mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            mEditText.clearFocus();
            //取消焦点
            mEditText.setFocusable(false);
            //设置点击时获取焦点
            mEditText.setFocusableInTouchMode(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

//        if (ev.getY() > mTitleLayout.) {
//            if (removeFocus()) {
//                mEditText.setText(mKeyCache);
//                return true;
//            }
//        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void initToolbar() {
        initToolbar(mToolbar);
        mType = getIntent().getExtras().getString(KEY_TITLE);
        mTitleTxt.setText(mType);
    }

    public static void start(Context context, Bundle bundle, int request) {
        Intent intent = new Intent(context, WeiBoUniversalActivity.class);
        intent.putExtras(bundle);
        ((Activity) context).startActivityForResult(intent, request);
        UIUtils.addEnterAnim((Activity) context);
    }

    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, WeiBoUniversalActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
//        NavUtils.navigateUpTo((Activity) context,intent);
        UIUtils.addEnterAnim((Activity) context);
    }

    @Override
    public void finish() {
        super.finish();
        UIUtils.addExitTranAnim(this);
    }
}
