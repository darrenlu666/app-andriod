package com.codyy.erpsportal.rethink.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.DeleteCommentDialog;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.DeleteCommentDialog.OnOkClickListener;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.MoreRelies;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.models.presenters.IFragmentManagerProvider;
import com.codyy.erpsportal.commons.models.presenters.SendingDialogPresenter;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.WebViewUtils;
import com.codyy.erpsportal.commons.widgets.EmojiView;
import com.codyy.erpsportal.commons.widgets.TouchyWebView;
import com.codyy.erpsportal.rethink.controllers.adapters.RethinkCommentsAdapter;
import com.codyy.erpsportal.rethink.models.entities.CommentListHeader;
import com.codyy.erpsportal.rethink.models.entities.DeleteCommentEvent;
import com.codyy.erpsportal.rethink.models.entities.DeleteReplyEvent;
import com.codyy.erpsportal.rethink.models.entities.MoreCommentsEvent;
import com.codyy.erpsportal.rethink.models.entities.RethinkComment;
import com.codyy.erpsportal.rethink.models.entities.RethinkCommentBase;
import com.codyy.erpsportal.rethink.models.entities.RethinkDetails;
import com.codyy.erpsportal.rethink.models.entities.RethinkReply;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 教学反思详情
 * @author gujiajia
 */
public class RethinkDetailsActivity extends AppCompatActivity implements IFragmentManagerProvider, OnOkClickListener, OnRefreshListener {

    private final static String TAG = "RethinkDetailsActivity";

    private final static String EXTRA_RETHINK_ID = "com.codyy.erpsportal.RETHINK_ID";

    private final static int COMMENT_MAX_LENGTH = 150;

    private final static int LOAD_COUNT = 10;

    private int mTotal;

    /**
     * 显示内容UI
     */
    @Bind(R.id.sv_content)
    ScrollView mContentSv;

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.tv_teacher)
    TextView mTeacherTv;

    @Bind(R.id.tv_type)
    TextView mTypeTv;

    @Bind(R.id.tv_view_count)
    TextView mViewCountTv;

    @Bind(R.id.tv_create_time)
    TextView mCreateTimeTv;

    @Bind(R.id.tv_range)
    TextView mRangeTv;

    @Bind(R.id.content)
    TouchyWebView mContentWv;

    @Bind(R.id.toolbar_title)
    TextView mToolbarTitleTv;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.btn_recommend)
    Button mRecommendBtn;

    @Bind(R.id.et_comment)
    EditText mCommentEt;

    @Bind(R.id.btn_publish)
    Button mSwitchBtn;

    /**
     * 表情键盘弹出按钮
     */
    @Bind(R.id.ib_emoji)
    ImageButton mEmojiBtn;

    @Bind(R.id.emoji_view)
    EmojiView mEmojiView;

    @Bind(R.id.rl_comments)
    SwipeRefreshLayout mCommentsRl;

    /**
     * 评论列表区
     */
    @Bind(R.id.rv_comments)
    RecyclerView mCommentsRv;

    @Bind(R.id.fl_content)
    FrameLayout mContentFl;

    private String mRethinkId;

    private RequestSender mRequestSender;

    private RethinkDetails mRethinkDetails;

    private UserInfo mUserInfo;

    private RethinkCommentsAdapter mAdapter;

    private SendingDialogPresenter mSendingDialogPresenter;

    private LengthFilter mLengthFilter;

    private HeaderFilter mHeaderFilter;

    private int mStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rethink_details);
        mSendingDialogPresenter = new SendingDialogPresenter(this);
        ButterKnife.bind(this);
        mRethinkId = getIntent().getStringExtra(EXTRA_RETHINK_ID);
        initToolbar(mToolbar);
        mRequestSender = new RequestSender(this);
        initCommentTextFilter();

        mUserInfo = UserInfoKeeper.obtainUserInfo();

        if (mUserInfo.isManager() ) {
            mRecommendBtn.setVisibility(View.VISIBLE);
        }

        mCommentsRl.setEnabled(false);
        mCommentsRl.setOnRefreshListener(this);
        mEmojiView.setEditText(mCommentEt);
        initCommentsRecyclerView();
        fetchRethinkDetails();
        fetchRethinkComments(true);
        setListenerToRootView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(MoreCommentsEvent moreCommentsEvent) {
        fetchRethinkComments(false);
    }

    public void onEvent(MoreRelies moreRelies) {
        onMoreReplyClick(moreRelies);
    }

    public void onEvent(RethinkComment replyingComment) {
        startToInputReply(replyingComment);
    }

    public void onEvent(DeleteCommentEvent deleteCommentEvent) {
        showDeleteCommentDialog(deleteCommentEvent.getPosition());
    }

    public void onEvent(DeleteReplyEvent deleteReplyEvent) {
        showDeleteReplyDialog(deleteReplyEvent.getPosition());
    }

    public void onEvent(RethinkReply reply){
        startToInputReply(reply);
    }

    private void initCommentTextFilter() {
        mLengthFilter = new LengthFilter(COMMENT_MAX_LENGTH);
        mHeaderFilter = new HeaderFilter();
        mHeaderFilter.setOnHeaderChangingListener(new OnHeaderChangingListener() {
            @Override
            public void onTryingToChangeHeader() {
                mCommentEt.post(new Runnable() {
                    @Override
                    public void run() {
                        moveCommentEditTextSelectionToEnd();
                    }
                });
            }
        });
        mCommentEt.setFilters(new InputFilter[]{mLengthFilter, mHeaderFilter});
    }

    /**
     * 初始化评论列表
     */
    private void initCommentsRecyclerView() {
        mCommentsRv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RethinkCommentsAdapter();
        mCommentsRv.setAdapter(mAdapter);
    }

    /**
     * 抓取教学反思详情数据
     */
    private void fetchRethinkDetails() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("rethinkId", mRethinkId);
        Cog.d(TAG, URLConfig.RETHINK_DETAILS, params);
        mRequestSender.sendRequest(new RequestData(URLConfig.RETHINK_DETAILS, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "fetchRethinkDetails response=", response);
                if ("success".equals(response.optString("result"))) {
                    mRethinkDetails = RethinkDetails.JSON_PARSER.parse(response.optJSONObject("rethinkDetails"));
                    updateRecommendBtn();
                    updateContentViews();
                    updateCommentListHead();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "fetchRethinkDetails error=", error);
                UIUtils.toast(RethinkDetailsActivity.this, "获取教学反思详情失败，请检查网络。", Toast.LENGTH_SHORT);
            }
        }));
    }

    /**
     * 更新评论列表头
     */
    private void updateCommentListHead() {
        mAdapter.updateHeader(new HeaderUpdater() {
            @Override
            public void update(CommentListHeader header) {
                header.setTitle(mRethinkDetails.getTitle());
                header.setTime(mRethinkDetails.getCreateTime());
            }
        });
    }

    /**
     * 更新内容区域UI
     */
    private void updateContentViews() {
        mTitleTv.setText( mRethinkDetails.getTitle());
        mTeacherTv.setText( mRethinkDetails.getTeacherName());
        mTypeTv.setText( acquireTypeName(mRethinkDetails.getType()));
        mViewCountTv.setText( Integer.toString( mRethinkDetails.getViewCount()));
        mCreateTimeTv.setText( mRethinkDetails.getCreateTime());
        mRangeTv.setText(buildRange( mRethinkDetails));
        WebViewUtils.setContentToWebView(mContentWv, mRethinkDetails.getContent());
    }

    private String acquireTypeName(String type) {
        String typeName = null;
        switch (type) {
            case "CLASS":
                typeName = "课时反思";
                break;
            case "DAY":
                typeName = "日反思";
                break;
            case "WEEK":
                typeName = "周反思";
                break;
            case "MONTH":
                typeName = "月反思";
                break;
            case "SEMESTER_MIDDLE":
                typeName = "期中反思";
                break;
            case "SEMESTER_END":
                typeName = "期末反思";
                break;
            default:
                typeName = "未知类型";
        }
        return typeName;
    }

    /**
     * 创建教学反思范围字串
     * @return
     */
    private String buildRange(RethinkDetails rethinkDetails) {
        if (TextUtils.isEmpty(rethinkDetails.getSemesterName()))
            return "";
        StringBuilder sb = new StringBuilder();
        sb.append(rethinkDetails.getSemesterName());
        if (TextUtils.isEmpty(rethinkDetails.getClassLevelName()))
            return sb.toString();
        sb.append('/').append(rethinkDetails.getClassLevelName());

        if (TextUtils.isEmpty(rethinkDetails.getSubjectName()))
            return sb.toString();
        sb.append('/').append(rethinkDetails.getSubjectName());

        if (TextUtils.isEmpty(rethinkDetails.getVersionName()))
            return sb.toString();
        sb.append('/').append(rethinkDetails.getVersionName());

        if (TextUtils.isEmpty(rethinkDetails.getVolumeName()))
            return sb.toString();
        sb.append('/').append(rethinkDetails.getVolumeName());

        if (TextUtils.isEmpty(rethinkDetails.getChapterName()))
            return sb.toString();
        sb.append('/').append(rethinkDetails.getChapterName());

        if (TextUtils.isEmpty(rethinkDetails.getSectionName()))
            return sb.toString();
        sb.append('/').append(rethinkDetails.getSectionName());
        return sb.toString();
    }

    /**
     * 抓取教学反思评论
     */
    private void fetchRethinkComments(final boolean isRefresh) {
        Map<String, String> params = new HashMap<>();

        if (isRefresh) {
            mStart = 0;
        }
        int start = mStart;
        int end = start + LOAD_COUNT;
        params.put("start", "" + start);
        params.put("end", "" + (end - 1));

        params.put("uuid", mUserInfo.getUuid());
        params.put("id", mRethinkId);

        //http://10.1.80.22/rethink/getRethinkComments.do?id=f0bf5b1fd76f4cc9a9d62d06cccf7ee5&uuid=MOBILE:d0645d05c2074fb69a64a53b4a8b15d5&start=0&end=9
        mRequestSender.sendRequest(new RequestData(URLConfig.RETHINK_COMMENTS, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "getRethinkComments response", response);
                stopRefreshing();
                if ("success".equals(response.optString("result"))) {
                    if (isRefresh) {
                        mAdapter.clear();
                    }
                    mTotal = response.optInt("total");
                    updateCommentsCount();
                    JSONArray jsonArray = response.optJSONArray("list");
                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject commentJsonObj = jsonArray.optJSONObject(i);
                        RethinkComment rethinkComment = new RethinkComment();
                        rethinkComment.setContent(commentJsonObj.optString("content"));
                        rethinkComment.setId(commentJsonObj.optString("id"));
                        rethinkComment.setCreateTime(commentJsonObj.optString("createTime"));
                        rethinkComment.setTotalReplyCount(commentJsonObj.optInt("totalReplyCount"));
                        rethinkComment.setUserType(commentJsonObj.optString("userType"));

                        JSONArray repliesJsonArray = commentJsonObj.optJSONArray("replies");
                        parseRepliesJsonArray(rethinkComment, repliesJsonArray);
                        rethinkComment.setUserIcon(commentJsonObj.optString("userIcon"));
                        rethinkComment.setUserId(commentJsonObj.optString("baseUserId"));
                        rethinkComment.setUserRealName(commentJsonObj.optString("userRealName"));
                        rethinkComment.setMine(mUserInfo.getBaseUserId().equals(rethinkComment.getUserId()));
                        mAdapter.addComment(rethinkComment);
                    }
                    mAdapter.setHasMore(mTotal > mAdapter.getCommentCount());
                    mAdapter.notifyDataSetChanged();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "getRethinkComments error", error);
                stopRefreshing();
                UIUtils.toast(RethinkDetailsActivity.this, "获取教学反思评论失败，请检查网络。", Toast.LENGTH_SHORT);
            }
        }));
    }

    /**
     * 解析回复json数组并加入到对应回复中
     * @param rethinkComment
     * @param repliesJsonArray
     */
    private void parseRepliesJsonArray(RethinkComment rethinkComment, JSONArray repliesJsonArray) {
        for (int j=0; j < repliesJsonArray.length(); j++) {
            JSONObject replyJsonObj = repliesJsonArray.optJSONObject(j);
            RethinkReply rethinkReply = parseReplyJsonObject(replyJsonObj, rethinkComment);
            rethinkComment.addReply(rethinkReply);
        }
    }

    @NonNull
    private RethinkReply parseReplyJsonObject(JSONObject replyJsonObj , RethinkComment comment) {
        RethinkReply rethinkReply = new RethinkReply();
        rethinkReply.setReplyToUserId(replyJsonObj.optString("replyToUserId"));
        rethinkReply.setContent(replyJsonObj.optString("content"));
        rethinkReply.setId(replyJsonObj.optString("id"));
        rethinkReply.setCreateTime(replyJsonObj.optString("createTime"));
        rethinkReply.setReplyToName(replyJsonObj.optString("replyToName"));
        rethinkReply.setUserId(replyJsonObj.optString("userId"));
        rethinkReply.setUserIcon(replyJsonObj.optString("userIcon"));
        rethinkReply.setUserRealName(replyJsonObj.optString("userRealName"));
        rethinkReply.setMine(mUserInfo.getBaseUserId().equals(rethinkReply.getUserId()));
        rethinkReply.setComment(comment);
        return rethinkReply;
    }

    /**
     * 更新评论列表头
     */
    private void updateCommentsCount() {
        if (mContentSv.getVisibility() == View.VISIBLE) {
            mSwitchBtn.setText(Integer.toString(mTotal));
        }
        mAdapter.updateHeader(new HeaderUpdater() {
            @Override
            public void update(CommentListHeader header) {
                header.setCount(mTotal);
            }
        });
    }
    /**
     * 初始化toolbar
     * @param toolbar
     */
    protected void initToolbar(Toolbar toolbar) {
        if (toolbar == null)
            return;
        toolbar.setTitle("");
        toolbar.collapseActionView();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn_bg);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * 虚拟键盘是否开启
     */
    private boolean mVirtualKeyboardOpened = false;

    private int mVirtualKeyboardHeight;

    public void setListenerToRootView(){
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mVirtualKeyboardHeight = activityRootView.getRootView().getHeight() - activityRootView.getHeight()
                        - InputUtils.getStatusBarHeight(RethinkDetailsActivity.this);
                if (mVirtualKeyboardHeight > 100 ) { // 99%情况下高度差是键盘导致的。
                    //实际情况中，键盘弹出时输入框换行时高度变化也会跑这里，
                    //因为键盘弹出时高度差超过100了，而输入框高度变化时会触发全局布局监听器
                    if(!mVirtualKeyboardOpened){
                        onSoftKeyboardOpened();
                    }
                    mVirtualKeyboardOpened = true;
                }else if(mVirtualKeyboardOpened){
                    onSoftKeyboardClosed();
                    mVirtualKeyboardOpened = false;
                }
            }
        });
    }

    private void onSoftKeyboardOpened() {
        updateSwitchBtnToSend();
        mEmojiView.getLayoutParams().height = mVirtualKeyboardHeight;
        mEmojiView.setVisibility(View.GONE);
        mEmojiBtn.setVisibility(View.VISIBLE);
    }

    private void onSoftKeyboardClosed() {
        if (mEmojiView.getVisibility() == View.GONE) {//软键盘关闭了且表情键盘也消失了的话，说明退出了输入状态，可以隐藏表情按钮
            mEmojiBtn.setVisibility(View.GONE);
            if (mContentSv.getVisibility() == View.VISIBLE) {
                updateSwitchBtnToCommentsCount();
            } else {
                updateSwitchBtnToOriginal();
            }
        }
    }

    @OnClick(R.id.ib_emoji)
    public void onEmojiBtnClick(View view) {
        if (mEmojiView.getVisibility() == View.GONE) {
            closeVirtualKeyboard();
            mEmojiView.setVisibility(View.VISIBLE);
            lockContentLayoutHeight(mContentFl.getHeight());
        } else {
            hideEmojiView();
            openVirtualKeyboard();
        }
    }

    @OnClick(R.id.et_comment)
    public void onCommentEtClick() {
        if (mEmojiView.getVisibility() == View.VISIBLE) {
            hideEmojiView();
        }
        if (!mVirtualKeyboardOpened) {
            openVirtualKeyboard();
        }
    }

    private void hideEmojiView() {
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) mContentFl.getLayoutParams();
        localLayoutParams.height = mContentFl.getHeight() - mVirtualKeyboardHeight;
        localLayoutParams.weight = 0.0F;
        mEmojiView.setVisibility(View.GONE);
        mContentFl.postDelayed(new Runnable() {
            @Override
            public void run() {
                unlockContentLayoutHeight();
            }
        }, 200);
    }

    private void lockContentLayoutHeight(int height) {
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) mContentFl.getLayoutParams();
        localLayoutParams.height = height;
        localLayoutParams.weight = 0.0F;
    }

    public void unlockContentLayoutHeight() {
        ((LinearLayout.LayoutParams) mContentFl.getLayoutParams()).weight = 1.0F;
    }

    /**
     * 关闭虚拟键盘
     */
    private void closeVirtualKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void openVirtualKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(mCommentEt,InputMethodManager.SHOW_FORCED);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVirtualKeyboardOpened) {
            closeVirtualKeyboard();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                UIUtils.addExitTranAnim(this);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 更新推荐按钮
     */
    private void updateRecommendBtn(){
        if (mRethinkDetails.isRecommended()) {
            mRecommendBtn.setText(R.string.cancel_recommend);
        } else {
            mRecommendBtn.setText(R.string.recommend);
        }
        mRecommendBtn.setEnabled(true);
    }

    public void onMoreReplyClick(MoreRelies data) {
        final RethinkComment rethinkComment = data.getRethinkComment();
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("commId", rethinkComment.getId());
        int start = rethinkComment.getCurrentCount();
        params.put("start", start + "");
        params.put("end", start + 4 + "");
        mRequestSender.sendRequest(new RequestData(URLConfig.RETHINK_MORE_REPLIES, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onMoreReplyClick response=", response);
                if ("success".equals(response.optString("result"))) {
                    JSONArray repliesJa = response.optJSONArray("list");
                    rethinkComment.setTotalReplyCount(response.optInt("total"));
                    if (repliesJa.length() > 0) {
                        List<RethinkReply> replies = new ArrayList<>(repliesJa.length());
                        for (int j=0; j < repliesJa.length(); j++) {
                            JSONObject replyJsonObj = repliesJa.optJSONObject(j);
                            RethinkReply rethinkReply = parseReplyJsonObject(replyJsonObj, rethinkComment);
                            replies.add(rethinkReply);
                        }
                        mAdapter.addReplies(rethinkComment, replies);
                    }
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "onMoreReplyClick error=", error);
                UIUtils.toast(RethinkDetailsActivity.this, "获取更多回复失败！",Toast.LENGTH_SHORT);
            }
        }));
    }

    /**
     * 推荐按钮点击事件
     * @param view
     */
    @OnClick(R.id.btn_recommend)
    public void onRecommendClick(View view) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("rethinkId", mRethinkId);
        if (!mRethinkDetails.isRecommended()) {
            params.put("recommand", "Y");
        } else {
            params.put("recommand", "N");
        }
        mRequestSender.sendRequest(new RequestData(URLConfig.RECOMMEND_RETHINK, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onRecommendClick response=", response);
                if ("success".equals(response.optString("result"))) {
                    mRethinkDetails.setRecommended(!mRethinkDetails.isRecommended());
                    updateRecommendBtn();
                }
                UIUtils.toast(RethinkDetailsActivity.this, response.optString("message"), Toast.LENGTH_SHORT);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "onRecommendClick error=", error);
                if (!mRethinkDetails.isRecommended()) {
                    UIUtils.toast(RethinkDetailsActivity.this, getString(R.string.recommend_failed), Toast.LENGTH_SHORT);
                } else {
                    UIUtils.toast(RethinkDetailsActivity.this, getString(R.string.cancel_recommend_failed), Toast.LENGTH_SHORT);
                }
            }
        }));
    }

    /**
     * 右下角按钮点击事件，一般状态下功能为切换内容和评论列表显示
     * 当软键盘弹出时，功能变为发表评论
     *
     * @param view
     */
    @OnClick(R.id.btn_publish)
    public void onSwitchClick(View view) {
        if (mVirtualKeyboardOpened) {
            String commentContent = obtainCommentContent();
            if (commentContent == null) return;
            closeVirtualKeyboard();
            publishComment(commentContent);
        } else if (mEmojiView.getVisibility() == View.VISIBLE) {
            String commentContent = obtainCommentContent();
            if (commentContent == null) return;
            hideEmojiView();
            publishComment(commentContent);
        } else if(mContentSv.getVisibility() == View.INVISIBLE) {
            mContentSv.setVisibility(View.VISIBLE);
            mCommentsRv.setVisibility(View.INVISIBLE);
            mCommentsRl.setEnabled(false);
            updateSwitchBtnToCommentsCount();
            clearReplyTo();
        } else {
            mContentSv.setVisibility(View.INVISIBLE);
            mCommentsRv.setVisibility(View.VISIBLE);
            mCommentsRl.setEnabled(true);
            updateSwitchBtnToOriginal();
            clearReplyTo();
        }
    }

    /**
     * 发表评论
     */
    private void publishComment(String commentContent) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("rethinkId", mRethinkId);
        params.put("userId", mUserInfo.getBaseUserId());
        params.put("content", commentContent);

        mSendingDialogPresenter.show();

        if (mReplyingComment != null) {
            if (mReplyingComment instanceof RethinkReply) {
                RethinkReply reply = (RethinkReply) mReplyingComment;
                params.put("parentCommentId", reply.getParentId());
            } else {
                params.put("parentCommentId", mReplyingComment.getId());
            }
            params.put("replyToUserId", mReplyingComment.getUserId());
        }

        mRequestSender.sendRequest(new RequestData(URLConfig.ADD_RETHINK_COMMENT, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "publishComment response:", response);
                mSendingDialogPresenter.dismiss();
                if ("success".equals(response.optString("result"))) {
                    UIUtils.toast(RethinkDetailsActivity.this, getString(R.string.publish_comment_successfully), Toast.LENGTH_SHORT);
                    clearReplyToAndClear();

                    closeVirtualKeyboard();
                    notifyCommentsListToUpdate();
                } else {
                    UIUtils.toast(RethinkDetailsActivity.this, response.optString("message"), Toast.LENGTH_SHORT);
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "publishComment error:", error);
                mSendingDialogPresenter.dismiss();
                UIUtils.toast( RethinkDetailsActivity.this, "评论教学反思失败。请检查网络。", Toast.LENGTH_SHORT);
            }
        }));
    }

    /**
     * 从输入框中获取评论内容,在回复时要去除“回复某某某：”头
     * @return
     */
    @Nullable
    private String obtainCommentContent() {
        String commentContent = mCommentEt.getText().toString();

        if (!TextUtils.isEmpty(mReplyTo)) {
            if (commentContent.length() > mReplyTo.length()) {
                commentContent = commentContent.substring(mReplyTo.length());
            } else {
                return null;
            }
        }

        if (TextUtils.isEmpty(commentContent.trim())) {
            return null;
        }
        return commentContent;
    }

    /**
     * 通知评论列表更新
     */
    private void notifyCommentsListToUpdate() {
        mCommentsRl.setRefreshing(true);
        fetchRethinkComments(true);
    }

    private void clearReplyTo() {
        if (mReplyingComment != null) {
            mReplyingComment = null;//回复成功，清除被回复的评论
            mReplyTo = null;
            mLengthFilter.setMax(COMMENT_MAX_LENGTH);
            mHeaderFilter.setHeaderLength(0);
            mCommentEt.getText().clear();
        }
    }

    private void clearReplyToAndClear() {
        if (mReplyingComment != null) {
            mReplyingComment = null;//回复成功，清除被回复的评论
            mReplyTo = null;
            mLengthFilter.setMax(COMMENT_MAX_LENGTH);
            mHeaderFilter.setHeaderLength(0);
        }
        mCommentEt.getText().clear();
    }

    /**
     * 更新切换键，显示原文
     */
    private void updateSwitchBtnToOriginal() {
        mSwitchBtn.setText(R.string.original);
        mSwitchBtn.setCompoundDrawablePadding(0);
        mSwitchBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    /**
     * 更新切换键，显示评论数量
     */
    private void updateSwitchBtnToCommentsCount() {
        mSwitchBtn.setText(Integer.toString(mTotal));
        mSwitchBtn.setCompoundDrawablePadding(UIUtils.dip2px(this, 2));
        mSwitchBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_msg, 0, 0, 0);
    }

    /**
     * 更新切换键，显示发送，键盘弹出时。
     */
    private void updateSwitchBtnToSend() {
        mSwitchBtn.setText(R.string.publish);
        mSwitchBtn.setCompoundDrawablePadding(0);
        mSwitchBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    /**
     * 正在回复的教学反思
     */
    private RethinkCommentBase mReplyingComment;

    private String mReplyTo;

    public void startToInputReply(RethinkCommentBase comment) {
        mReplyingComment = comment;
        mReplyTo = getString(R.string.rely_to, mReplyingComment.getUserRealName());
        mHeaderFilter.setHeaderLength(0);
        mCommentEt.setText(mReplyTo);
        mLengthFilter.setMax(mReplyTo.length()+COMMENT_MAX_LENGTH);
        mHeaderFilter.setHeaderLength(mReplyTo.length());
        mCommentEt.requestFocus();
        moveCommentEditTextSelectionToEnd();
        showVirtualKeyboard();
    }

    private void moveCommentEditTextSelectionToEnd() {
        mCommentEt.setSelection( mCommentEt.length());
    }

    private void showVirtualKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(mCommentEt, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 启动
     * @param activity
     * @param rethinkId
     */
    public static void start(Activity activity, String rethinkId){
        Intent intent = new Intent(activity, RethinkDetailsActivity.class);
        intent.putExtra(EXTRA_RETHINK_ID, rethinkId);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    @Override
    public FragmentManager obtainFragmentManager() {
        return getSupportFragmentManager();
    }


    public void showDeleteReplyDialog(int position) {
        DeleteCommentDialog deleteCommentDialog = DeleteCommentDialog.newInstance(position, R.string.confirm_to_delete_reply);
        deleteCommentDialog.show(getSupportFragmentManager(), "delete_reply");
    }

    public void showDeleteCommentDialog(int position) {
        DeleteCommentDialog deleteCommentDialog = DeleteCommentDialog.newInstance(position, R.string.confirm_to_delete_comment);
        deleteCommentDialog.show(getSupportFragmentManager(), "delete_comment");
    }

    /**
     * 删除确认
     */
    @Override
    public void onOkClickListener(final int position) {
        Cog.d(TAG, "+onOkClickListener id=", position);
        final RethinkCommentBase commentBase = (RethinkCommentBase) mAdapter.getItem(position);

        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("commId", commentBase.getId());
        mRequestSender.sendRequest(new RequestData(URLConfig.DELETE_RETHINK_COMMENT, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "delete comment response=", response);
                if ("success".equals(response.optString("result"))){
                    String toastStr;
                    if (commentBase instanceof RethinkComment) {
                        toastStr = "删除评论成功！";
                    } else {
                        toastStr = "删除回复成功！";
                    }
                    mAdapter.remove(position);
                    ToastUtil.showToast(RethinkDetailsActivity.this, toastStr, Toast.LENGTH_SHORT);
                } else {
                    ToastUtil.showToast(RethinkDetailsActivity.this, response.optString("message"), Toast.LENGTH_SHORT);
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.e(TAG, "delete comment error=", error);
            }
        }));
    }

    @Override
    public void onRefresh() {
        fetchRethinkComments(true);
    }

    public void stopRefreshing() {
        mCommentsRl.post(new Runnable() {
            @Override
            public void run() {
                mCommentsRl.setRefreshing(false);
            }
        });
    }

    public interface HeaderUpdater{
        void update(CommentListHeader header);
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

    /**
     * 输入框保持头有“回复某某”的输入过滤器
     */
    public static class HeaderFilter implements InputFilter {

        private int mHeaderLength;

        private OnHeaderChangingListener mOnHeaderChangingListener;

        public HeaderFilter(){}

        public HeaderFilter(int headerLength) {
            mHeaderLength = headerLength;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (mHeaderLength == 0) return null;
            if(dstart < mHeaderLength) {
                if (mOnHeaderChangingListener != null) {
                    mOnHeaderChangingListener.onTryingToChangeHeader();
                }
                return dest.subSequence(dstart, dend);
            }
            return null;
        }

        public int getHeaderLength() {
            return mHeaderLength;
        }

        public void setHeaderLength(int headerLength) {
            mHeaderLength = headerLength;
        }

        public OnHeaderChangingListener getOnHeaderChangingListener() {
            return mOnHeaderChangingListener;
        }

        public void setOnHeaderChangingListener(OnHeaderChangingListener onHeaderChangingListener) {
            mOnHeaderChangingListener = onHeaderChangingListener;
        }
    }

    interface OnHeaderChangingListener {
        void onTryingToChangeHeader();
    }
}
