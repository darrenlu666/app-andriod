package com.codyy.erpsportal.weibo.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.ClickTextView;
import com.codyy.erpsportal.commons.widgets.EmojiEditText;
import com.codyy.erpsportal.commons.widgets.EmojiView;
import com.codyy.erpsportal.weibo.models.entities.WeiBoListInfo;
import com.codyy.erpsportal.weibo.models.entities.WeiBoSearchPeople;
import com.codyy.url.URLConfig;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by kmdai on 16-3-2.
 */
public class WeiBoTurnActivity extends ToolbarActivity {
    public static final String TURN_DATA = "turn_data";
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTextView;
    @Bind(R.id.weibo_turn_image)
    SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.weibo_turn_count)
    ClickTextView mClickTextView;
    @Bind(R.id.weibo_new_scrollview)
    ScrollView mScrollView;
    @Bind(R.id.weibo_new_edittext)
    EmojiEditText mEditText;
    @Bind(R.id.weibo_new_emojiview)
    EmojiView mEmojiView;
    @Bind(R.id.weibo_turn_liner)
    LinearLayout mLinearLayout;
    private WeiBoListInfo mWeiBoListInfo;
    private RequestSender mRequestSender;
    private UserInfo mUserInfo;
    private ArrayList<WeiBoSearchPeople> mAtPeople;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeiBoListInfo = getIntent().getParcelableExtra(TURN_DATA);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mWeiBoListInfo != null) {
            if (mWeiBoListInfo.getOriginalMiblogBlog() != null) {
                WeiBoListInfo weiBoListInfo = mWeiBoListInfo.getOriginalMiblogBlog();
                initData(weiBoListInfo);
            } else {
                initData(mWeiBoListInfo);
            }
        }
    }

    public void initData(WeiBoListInfo info) {
        if (info.getImageList() != null && info.getImageList().size() > 0) {
            mSimpleDraweeView.setImageURI(Uri.parse(info.getImageList().get(0).getImage()));
        } else {
            mSimpleDraweeView.setImageURI(Uri.parse(info.getHeadPic()));
        }
        mClickTextView.setHtmlString(info.getBlogContent());
    }

    @Override
    protected int getLayoutView() {
        return R.layout.weibo_activity_turn;
    }

    @Override
    protected void initToolbar() {
        initToolbar(mToolbar);
        mTextView.setText("转发");
        mEmojiView.setEditText(mEditText, 150);
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mEmojiView.isShown()) {
                    hideKeyBoard(true, mEmojiView);
                }
            }
        });
        mRequestSender = new RequestSender(this);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        mAtPeople = new ArrayList<>();
    }


    public void sendEmoji(View view) {
        if (mEmojiView.isShown()) {
            hideKeyBoard(false, mEmojiView);
            return;
        }
        showKeyBoard(this, mEmojiView, mEditText);
    }

    public void sendAt(View view) {
        Intent intent = new Intent(this, WeiBoMyFriendActivity.class);
        intent.putExtra(WeiBoMyFriendActivity.TYPE_STYLE, WeiBoMyFriendActivity.TYPE_MY_FRIEND);
        startActivityForResult(intent, WeiBoNewActivity.REQUEST_AT);
    }

    public static void start(Context context, WeiBoListInfo weiBoListInfo) {
        Intent intent = new Intent(context, WeiBoTurnActivity.class);
        intent.putExtra(TURN_DATA, weiBoListInfo);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_weibo_new, menu);
        TextView textView = (TextView) menu.findItem(R.id.weibo_new_send).getActionView();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnWeiBo();
            }
        });
        return true;
    }

    /**
     * 转发微博
     */
    private void turnWeiBo() {
        if (TextUtils.isEmpty(mEditText.getText().toString())) {
            Snackbar.make(mEditText, "转发内容不能为空！", Snackbar.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> parm = new HashMap<>();
        parm.put("uuid", mUserInfo.getUuid());
        parm.put("weiBoContent", mEditText.getText().toString());
        parm.put("originalFlag", "N");
        if (mWeiBoListInfo.getOriginalMiblogBlog() != null) {
            parm.put("originalBlogId", mWeiBoListInfo.getOriginalBlogId());
        } else {
            parm.put("originalBlogId", mWeiBoListInfo.getMiblogId());
        }
        StringBuilder name = new StringBuilder("");
        for (WeiBoSearchPeople weiBoSearchPeople : mAtPeople) {
            name.append(weiBoSearchPeople.getRealName() + ",");
        }
        parm.put("realNameStr", name.toString());
        mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.WEIBO_SEND_DYNAMIC, parm, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ("success".equals(response.optString("result"))) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Snackbar.make(mScrollView, "转发失败！", Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) { }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case WeiBoNewActivity.REQUEST_AT:
                if (data != null) {
                    WeiBoSearchPeople weiBoSearchPeople = data.getParcelableExtra(WeiBoMyFriendActivity.RESULT_DATA);
                    if (weiBoSearchPeople != null) {
                        String str = "@" + weiBoSearchPeople.getRealName() + " ";
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.weibo_at)), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mEditText.getText().insert(mEditText.getSelectionStart(), spannableStringBuilder);
                        mAtPeople.add(weiBoSearchPeople);
                    }
                }
                break;
        }
    }

    public void showKeyBoard(Activity activity, EmojiView emojiView, EditText editText) {
        int emotionHeight = InputUtils.getKeyboardHeight(activity);
        InputUtils.hideSoftInputFromWindow(activity, editText);
        emojiView.getLayoutParams().height = emotionHeight;
        emojiView.setVisibility(View.VISIBLE);

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        在5.0有navigationbar的手机，高度高了一个statusBar
        int lockHeight = InputUtils.getAppContentHeight(activity) - mLinearLayout.getHeight();
//            lockHeight = lockHeight - statusBarHeight;
        lockContainerHeight(lockHeight);
    }

    private void lockContainerHeight(int paramInt) {
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) mScrollView.getLayoutParams();
        localLayoutParams.height = paramInt;
        localLayoutParams.weight = 0.0F;
    }

    public void hideKeyBoard(boolean showKeyBoard, EmojiView emojiView) {
        if (showKeyBoard) {
            LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) mScrollView.getLayoutParams();
            localLayoutParams.height = mEmojiView.getTop();
            localLayoutParams.weight = 0.0F;
            emojiView.setVisibility(View.GONE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            emojiView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    unlockContainerHeightDelayed();
                }
            }, 200L);
            InputUtils.showSoftInputFromWindow(this, mEditText);
        } else {
            emojiView.setVisibility(View.GONE);
            InputUtils.hideSoftInputFromWindow(this, mEditText);
            unlockContainerHeightDelayed();
        }
    }

    public void unlockContainerHeightDelayed() {
        ((LinearLayout.LayoutParams) mScrollView.getLayoutParams()).weight = 1.0F;
    }

    @Override
    protected void onDestroy() {
        mRequestSender.stop();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        UIUtils.addExitTranAnim(this);
    }
}
