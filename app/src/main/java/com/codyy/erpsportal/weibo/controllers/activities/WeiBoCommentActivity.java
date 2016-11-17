package com.codyy.erpsportal.weibo.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.WeiBoPopuDialog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.weibo.controllers.adapters.WeiBoCommentAdapter;
import com.codyy.erpsportal.weibo.models.entities.WeiBoComment;
import com.codyy.erpsportal.commons.widgets.EmojiEditText;
import com.codyy.erpsportal.commons.widgets.EmojiView;
import com.codyy.erpsportal.commons.widgets.RefreshRecycleView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class WeiBoCommentActivity extends ToolbarActivity implements WeiBoCommentAdapter.OnGetMoreComment, RefreshRecycleView.OnStateChangeLstener {
    /**
     * 微博id
     */
    public static final String WEIBI_ID = "weibo_id";
    public final static int GET_COMMENT = 0x001;
    public final static int GET_CHILDREN_COMMENT = 0x002;
    public final static int GET_COMMENT_FIRST = 0x003;
    public final static int GET_MORE_CHILDREN_COMMENT = 5;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTextTitle;
    RequestSender mRequestSender;
    @Bind(R.id.weibo_comment_swiprefresh)
    RefreshRecycleView mRecyclerView;
    @Bind(R.id.weibo_comment_inputcomment)
    EmojiEditText mEditText;
    @Bind(R.id.weibo_comment_rootview)
    LinearLayout mRootView;
    @Bind(R.id.private_emojiview)
    EmojiView mEmojiView;
    UserInfo mUserInfo;
    String mWeiBoId;
    private ArrayList<WeiBoComment> mWeiBoComments;
    private WeiBoCommentAdapter mWeiBoCommentAdapter;
    private int mStart = 0;
    private int mCount = 9;
    private int mEnd = mCount + mStart;
    private int mParentSize;
    private boolean mIsLoading;
    private WeiBoComment mWeiBoComment;
    private LinearLayoutManager mLinearLayoutManager;
    private int mType;
    private String mUrlCommentList;
    private Integer mHash = this.hashCode();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestSender = new RequestSender(this);
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mWeiBoId = getIntent().getStringExtra(WEIBI_ID);
        mType = getIntent().getIntExtra(WeiBoActivity.TYPE, WeiBoActivity.TYPE_PERSONAL);
        switch (mType) {
            case WeiBoActivity.TYPE_GROUP:
            case WeiBoActivity.TYPE_GROUP_MANAGER:
            case WeiBoActivity.TYPE_GROUP_VISITOR:
                mUrlCommentList = URLConfig.WEIBO_GET_COMMENTLIST_GROUP;
                break;
            case WeiBoActivity.TYPE_PERSONAL:
            case WeiBoActivity.TYPE_PERSNOAL_MY:
            case WeiBoActivity.TYPE_VISITOR:
                mUrlCommentList = URLConfig.WEIBO_GET_COMMENTLIST;
                break;

        }
        mWeiBoComments = new ArrayList<>();
        mWeiBoCommentAdapter = new WeiBoCommentAdapter(this, mWeiBoComments);
        mWeiBoCommentAdapter.setGetMoreComment(this);
        mEmojiView.setEditText(mEditText, 150);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setColorSchemeColors(getResources().getColor(R.color.refresh_scheme_main), getResources().getColor(R.color.refresh_scheme_pink), getResources().getColor(R.color.refresh_scheme_blue));
        mRecyclerView.setOnStateChangeLstener(this);
        mRecyclerView.setAdapter(mWeiBoCommentAdapter);
        mStart = 0;
        mEnd = mCount + mStart;
        mWeiBoComments.clear();
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mEmojiView.isShown()) {
                    hideKeyBoard(true, mEmojiView);
                }
            }
        });
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mEmojiView.isShown() || mEditText.hasFocus()) {
                    InputUtils.hideSoftInputFromWindow(WeiBoCommentActivity.this, mEditText);
                    hideKeyBoard(false, mEmojiView);
                    return true;
                }
                return false;
            }
        });
        loadData(GET_COMMENT_FIRST);
    }

    private void loadData(int msg) {
        Map<String, String> parm = new HashMap<>();
        parm.put("uuid", mUserInfo.getUuid());
        parm.put("start", String.valueOf(mStart));
        parm.put("end", String.valueOf(mEnd));
        parm.put("childCommentSize", String.valueOf(GET_MORE_CHILDREN_COMMENT));
        switch (mType) {
            case WeiBoActivity.TYPE_GROUP:
            case WeiBoActivity.TYPE_GROUP_VISITOR:
            case WeiBoActivity.TYPE_GROUP_MANAGER:
                parm.put("groupMiblogId", mWeiBoId);
                break;
            case WeiBoActivity.TYPE_PERSONAL:
            case WeiBoActivity.TYPE_VISITOR:
            case WeiBoActivity.TYPE_PERSNOAL_MY:
                parm.put("weiBoId", mWeiBoId);
                break;

        }
        httpConnect(mUrlCommentList, parm, msg);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_wei_bo_comment;
    }

    @Override
    protected void initToolbar() {
        initToolbar(mToolbar);
        mTextTitle.setText("评论");
    }

    public static void start(Context context, String weiboId, int type) {
        Intent intent = new Intent(context, WeiBoCommentActivity.class);
        intent.putExtra(WeiBoActivity.TYPE, type);
        intent.putExtra(WEIBI_ID, weiboId);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }

    private void httpConnect(String url, Map<String, String> parm, final int msg) {
        mRequestSender.sendRequest(new RequestSender.RequestData(url, parm, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (isFinishing()) {
                    return;
                }
                if (mRecyclerView != null && mRecyclerView.isRefreshing()) {
                    mRecyclerView.setRefreshing(false);
                }
                switch (msg) {
                    case GET_COMMENT_FIRST:
                        mWeiBoComments.clear();
                        mParentSize = response.optJSONArray("commentList").length();
                        ArrayList<WeiBoComment> weiBoComments = WeiBoComment.getData(response);
                        if (weiBoComments != null) {
                            mWeiBoComments.addAll(weiBoComments);
                            mWeiBoCommentAdapter.notifyDataSetChanged();
                        }
                        if (mWeiBoComments.size() < mEnd) {
                            mRecyclerView.setAdapterLastState(RefreshRecycleView.STATE_NO_MORE);
                            if (mWeiBoComments.size() < mCount + 1) {
                                mWeiBoCommentAdapter.setLastVisibility(View.GONE);
                            }
                        }
                        break;
                    case GET_COMMENT:
                        mParentSize = response.optJSONArray("commentList").length();
                        ArrayList<WeiBoComment> weiBoComments1 = WeiBoComment.getData(response);
                        if (weiBoComments1 != null) {
                            mWeiBoCommentAdapter.addListDatas(weiBoComments1);
                        }
                        if (mWeiBoComments.size() < mEnd) {
                            mRecyclerView.setAdapterLastState(RefreshRecycleView.STATE_NO_MORE);
                        } else {
                            mRecyclerView.setAdapterLastState(RefreshRecycleView.STATE_UP_LOADEMORE);
                        }
                        break;
                    case GET_CHILDREN_COMMENT:
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (isFinishing()) {
                    return;
                }
                if (mRecyclerView != null && mRecyclerView.isRefreshing()) {
                    mRecyclerView.setRefreshing(false);
                }
            }
        }, mHash));
    }

    @Override
    protected void onDestroy() {
        mRequestSender.stop(mHash);
        super.onDestroy();
    }

    @Override
    public void getMoreComment(final WeiBoComment weiBoComment, final int position) {
        if (!mIsLoading) {
            mIsLoading = true;
            String url = "";
            int start = weiBoComment.getChildrenSize();
            int end = start + GET_MORE_CHILDREN_COMMENT;
            Map<String, String> parm = new HashMap<>();
            parm.put("uuid", mUserInfo.getUuid());
            parm.put("start", String.valueOf(start));
            parm.put("end", String.valueOf(end));
            switch (mType) {
                case WeiBoActivity.TYPE_GROUP:
                case WeiBoActivity.TYPE_GROUP_VISITOR:
                case WeiBoActivity.TYPE_GROUP_MANAGER:
                    parm.put("groupParentCommentId", weiBoComment.getGroupMiblogId());
                    url = URLConfig.WEIBO_GET_CHILD_COMMENT_GROUP;
                    break;
                case WeiBoActivity.TYPE_PERSONAL:
                case WeiBoActivity.TYPE_VISITOR:
                case WeiBoActivity.TYPE_PERSNOAL_MY:
                    parm.put("commentId", weiBoComment.getMiblogCommentId());
                    url = URLConfig.WEIBO_GET_CHILD_COMMENT;
                    break;

            }
            mRequestSender.sendRequest(new RequestSender.RequestData(url, parm, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mIsLoading = false;
                    if (mRecyclerView.isRefreshing()) {
                        mRecyclerView.setRefreshing(false);
                    }
                    if ("success".equals(response.optString("result"))) {
                        int index = mWeiBoComments.indexOf(weiBoComment);
                        int size = response.optInt("total");
                        JSONArray array = response.optJSONArray("commentList");
                        for (int i = 0; i < array.length(); i++) {
                            WeiBoComment comment = WeiBoComment.getWeiBoComment(array.optJSONObject(i), WeiBoComment.TYPE_CHILD);
                            mWeiBoComments.add(index++, comment);
                        }
                        mWeiBoCommentAdapter.notifyItemRangeInserted(position, array.length());
                        if (weiBoComment.getChildrenSize() + array.length() >= size) {
                            int a = mWeiBoComments.indexOf(weiBoComment);
                            mWeiBoComments.remove(weiBoComment);
                            mWeiBoCommentAdapter.notifyItemRemoved(a);
                        } else {
                            weiBoComment.setChildrenSize(weiBoComment.getChildrenSize() + array.length());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }, mHash));
        }
    }

    @Override
    public void onItemClick(final WeiBoComment weiBoComment) {
        if (mUserInfo.getBaseUserId().equals(weiBoComment.getBaseUserId())) {
            InputUtils.hideSoftInputFromWindow(this, mEditText);
            if (mEmojiView.isShown()) {
                hideKeyBoard(false, mEmojiView);
            }
            WeiBoPopuDialog weiBoPopuDialog = WeiBoPopuDialog.newInstence(WeiBoPopuDialog.TYPE_PERSONAL_COMMENT);
            weiBoPopuDialog.setOnItemClick(new WeiBoPopuDialog.OnItemClick() {
                boolean flag = false;

                @Override
                public void onDeletecleck() {
                    Map<String, String> parm = new HashMap<>();
                    parm.put("uuid", mUserInfo.getUuid());
                    String url = "";
                    switch (mType) {
                        case WeiBoActivity.TYPE_GROUP:
                        case WeiBoActivity.TYPE_GROUP_VISITOR:
                        case WeiBoActivity.TYPE_GROUP_MANAGER:
                            url = URLConfig.WEIBO_DELETE_COMMENT_GROUP;
                            parm.put("groupMiblogCommentId", weiBoComment.getGroupMiblogCommentId());
                            break;
                        case WeiBoActivity.TYPE_PERSONAL:
                        case WeiBoActivity.TYPE_VISITOR:
                        case WeiBoActivity.TYPE_PERSNOAL_MY:
                            url = URLConfig.WEIBO_DELETE_COMMENT;
                            parm.put("commentId", weiBoComment.getMiblogCommentId());
                            break;
                    }
                    mRequestSender.sendRequest(new RequestSender.RequestData(url, parm, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            if (isFinishing()) {
                                return;
                            }
                            if ("success".equals(response.optString("result"))) {
                                if (WeiBoComment.TYPE_CHILD == weiBoComment.getmHolderType()) {
                                    mWeiBoCommentAdapter.ramoveData(weiBoComment);
                                } else {
                                    int index = mWeiBoComments.indexOf(weiBoComment);
                                    int size = 1;
                                    while (index < mWeiBoComments.size() - 1) {
                                        if (mWeiBoComments.get(index + 1).getmHolderType() != WeiBoComment.TYPE_PARENT) {
                                            mWeiBoComments.remove(index + 1);
                                            size++;
                                        } else {
                                            break;
                                        }
                                    }
                                    mWeiBoComments.remove(index);
                                    if (mWeiBoComments.size() == 0) {
                                        mWeiBoCommentAdapter.notifyDataSetChanged();
                                    } else {
                                        mWeiBoCommentAdapter.notifyItemRangeRemoved(index, size);
                                    }
                                }
                                Snackbar.make(mRecyclerView, response.optString("message"), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }, mHash));
                }

                @Override
                public void onMsgClick() {
                    flag = true;

                }

                @Override
                public void onFlowClick() {

                }

                @Override
                public void onDismiss() {
                    if (flag) {
                        mEditText.setHint("回复" + weiBoComment.getRealName());
                        InputUtils.showSoftInputFromWindow(WeiBoCommentActivity.this, mEditText);
                        mWeiBoComment = weiBoComment;
                    }
                    flag = false;
                }
            });
            weiBoPopuDialog.show(getSupportFragmentManager(), this.getClass().getName() + "onItemClick");
        } else {
            mEditText.setHint("回复" + weiBoComment.getRealName());
            InputUtils.showSoftInputFromWindow(this, mEditText);
            mWeiBoComment = weiBoComment;
        }
    }

    public void sendMessage(final View view) {
        if (!TextUtils.isEmpty(mEditText.getText())) {
            Map<String, String> parm = new HashMap<>();
            parm.put("uuid", mUserInfo.getUuid());
            String url = "";
            String commentIdName = "";
            String commentId = "";
            switch (mType) {
                case WeiBoActivity.TYPE_GROUP:
                case WeiBoActivity.TYPE_GROUP_VISITOR:
                case WeiBoActivity.TYPE_GROUP_MANAGER:
                    url = URLConfig.WEIBO_ADD_COMMENT_GROUP;
                    parm.put("groupMiblogId", mWeiBoId);
                    commentIdName = "parentCommentId";
                    if (mWeiBoComment != null) {
                        commentId = mWeiBoComment.getGroupMiblogCommentId();
                    }
                    parm.put("commentContent", mEditText.getText().toString());
                    break;
                case WeiBoActivity.TYPE_PERSONAL:
                case WeiBoActivity.TYPE_VISITOR:
                case WeiBoActivity.TYPE_PERSNOAL_MY:
                    url = URLConfig.WEIBO_ADD_COMMENT;
                    parm.put("weiBoId", mWeiBoId);
                    commentIdName = "commentId";
                    if (mWeiBoComment != null) {
                        commentId = mWeiBoComment.getMiblogCommentId();
                    }
                    parm.put("comment", mEditText.getText().toString());
                    break;
            }
            if (mWeiBoComment != null) {
                if (mWeiBoComment.getmHolderType() == WeiBoComment.TYPE_PARENT) {
                    parm.put(commentIdName, commentId);
                } else {
                    parm.put(commentIdName, mWeiBoComment.getParentCommentId());
                }
                parm.put("replyToUserId", mWeiBoComment.getBaseUserId());
            }
            mRequestSender.sendRequest(new RequestSender.RequestData(url, parm, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    if (isFinishing()) {
                        return;
                    }
                    if ("success".equals(response.optString("result")) && !WeiBoCommentActivity.this.isFinishing()) {
                        mEditText.setText("");
                        mEditText.setHint("我来说两句");
                        if (mWeiBoComment != null) {
                            InputUtils.hideSoftInputFromWindow(WeiBoCommentActivity.this, mEditText);
                            Snackbar.make(view, response.optString("message"), Snackbar.LENGTH_SHORT).show();
                            JSONObject object = null;
                            switch (mType) {
                                case WeiBoActivity.TYPE_GROUP:
                                case WeiBoActivity.TYPE_GROUP_VISITOR:
                                case WeiBoActivity.TYPE_GROUP_MANAGER:
                                    object = response.optJSONObject("groupMiblogComment");
                                    break;
                                case WeiBoActivity.TYPE_PERSONAL:
                                case WeiBoActivity.TYPE_VISITOR:
                                case WeiBoActivity.TYPE_PERSNOAL_MY:
                                    object = response.optJSONObject("miblogComment");
                                    break;
                            }
                            WeiBoComment weiBoComment = WeiBoComment.getWeiBoComment(object, WeiBoComment.TYPE_CHILD);
                            weiBoComment.setRealName(mUserInfo.getRealName());
                            weiBoComment.setHeadPic(mUserInfo.getHeadPic());
                            if (mWeiBoComment.getmHolderType() == WeiBoComment.TYPE_CHILD) {
                                for (WeiBoComment weiBoComment1 : mWeiBoComments) {
                                    if (mWeiBoComment.getParentCommentId() != null && mWeiBoComment.getParentCommentId().equals(weiBoComment1.getMiblogCommentId())) {
                                        int index = mWeiBoComments.indexOf(weiBoComment1);
                                        mWeiBoCommentAdapter.addData(weiBoComment, index + 1);
                                        break;
                                    }
                                }
                            } else {
                                int index = mWeiBoComments.indexOf(mWeiBoComment);
                                mWeiBoCommentAdapter.addData(weiBoComment, index + 1);
                            }
                        } else {
                            InputUtils.hideSoftInputFromWindow(WeiBoCommentActivity.this, mEditText);
                            Snackbar.make(view, response.optString("message"), Snackbar.LENGTH_SHORT).show();
                            JSONObject object = null;
                            switch (mType) {
                                case WeiBoActivity.TYPE_GROUP:
                                case WeiBoActivity.TYPE_GROUP_MANAGER:
                                case WeiBoActivity.TYPE_GROUP_VISITOR:
                                    object = response.optJSONObject("groupMiblogComment");
                                    break;
                                case WeiBoActivity.TYPE_PERSONAL:
                                case WeiBoActivity.TYPE_VISITOR:
                                case WeiBoActivity.TYPE_PERSNOAL_MY:
                                    object = response.optJSONObject("miblogComment");
                                    break;
                            }
                            WeiBoComment weiBoComment = WeiBoComment.getWeiBoComment(object, WeiBoComment.TYPE_PARENT);
                            weiBoComment.setRealName(mUserInfo.getRealName());
                            weiBoComment.setHeadPic(mUserInfo.getHeadPic());
                            mWeiBoCommentAdapter.addData(weiBoComment, 0);
                            mLinearLayoutManager.scrollToPosition(0);
                        }
                        mWeiBoComment = null;
                    } else {
                        Snackbar.make(view, response.optString("message"), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (isFinishing()) {
                        return;
                    }
                    if (!WeiBoCommentActivity.this.isFinishing()) {
                        Snackbar.make(view, getString(R.string.net_error), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }, mHash));
        } else {
            Snackbar.make(view, "评论不能为空！", Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * 发送表情
     *
     * @param view
     */
    public void sendEmoji(View view) {
        if (mEmojiView.getVisibility() == View.VISIBLE) {
            hideKeyBoard(true, mEmojiView);
        } else {
            showKeyBoard(this, mEmojiView, mEditText);
        }
    }

    @Override
    public void onRefresh() {
        mStart = 0;
        mEnd = mCount + mStart;
        loadData(GET_COMMENT_FIRST);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mEditText.hasFocus() || mEmojiView.isShown()) {
                mEditText.setFocusable(false);
                mWeiBoComment = null;
                mEditText.setHint("我来说两句");
                if (mEmojiView.isShown()) {
                    hideKeyBoard(false, mEmojiView);
                }
                mEditText.setFocusableInTouchMode(true);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onBottom() {
        if (mParentSize >= mEnd) {
            mStart = mParentSize;
            mEnd = mStart + mCount;
            loadData(GET_COMMENT);
            return true;
        } else {
            mRecyclerView.setAdapterLastState(RefreshRecycleView.STATE_NO_MORE);
        }
        return false;
    }

    public void showKeyBoard(Activity activity, EmojiView emojiView, EditText editText) {
        int emotionHeight = InputUtils.getKeyboardHeight(activity);
        InputUtils.hideSoftInputFromWindow(activity, editText);
        emojiView.getLayoutParams().height = emotionHeight;
        emojiView.setVisibility(View.VISIBLE);

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        在5.0有navigationbar的手机，高度高了一个statusBar
        int lockHeight = InputUtils.getAppContentHeight(activity);
//            lockHeight = lockHeight - statusBarHeight;
        lockContainerHeight(lockHeight);
    }

    private void lockContainerHeight(int paramInt) {
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) mRootView.getLayoutParams();
        localLayoutParams.height = paramInt;
        localLayoutParams.weight = 0.0F;
    }

    public void hideKeyBoard(boolean showKeyBoard, EmojiView emojiView) {
        if (showKeyBoard) {
            LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) mRootView.getLayoutParams();
            localLayoutParams.height = mEmojiView.getTop() - mToolbar.getHeight();
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
        ((LinearLayout.LayoutParams) mRootView.getLayoutParams()).weight = 1.0F;
    }

    @Override
    public void finish() {
        super.finish();
        UIUtils.addExitTranAnim(this);
    }
}
