package com.codyy.erpsportal.resource.controllers.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.DeleteCommentDialog;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.DeleteCommentDialog.OnOkClickListener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.commons.utils.NetworkUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.EmojiView;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.presenters.IFragmentManagerProvider;
import com.codyy.erpsportal.commons.models.presenters.SendingDialogPresenter;
import com.codyy.erpsportal.resource.controllers.activities.DocumentCommentActivity;
import com.codyy.erpsportal.resource.controllers.adapters.ResourceCommentsAdapter;
import com.codyy.erpsportal.resource.models.entities.Comment;
import com.codyy.erpsportal.resource.models.entities.CommentBase;
import com.codyy.erpsportal.resource.models.entities.DeleteCommentEvent;
import com.codyy.erpsportal.resource.models.entities.DeleteReplyEvent;
import com.codyy.erpsportal.resource.models.entities.MoreCommentsEvent;
import com.codyy.erpsportal.resource.models.entities.MoreRelies;
import com.codyy.erpsportal.resource.models.entities.Reply;

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
 * 资源评论片
 * Created by gujiajia
 */
public class ResCommentsFragment extends Fragment implements IFragmentManagerProvider, OnRefreshListener {

    private static final String TAG = "ResCommentsFragment";

    public static final String ARG_USER_INFO = "arg_user_info";

    public static final String ARG_RESOURCE_ID = "arg_video_resource_id";

    private final static int COMMENT_MAX_LENGTH = 150;

    private final static int LOAD_COUNT = 10;

    private int mTotal;

    private UserInfo mUserInfo;

    private String mResourceId;

    private int mStart;

    /**
     * 虚拟键盘是否开启
     */
    private boolean mVirtualKeyboardOpened = false;

    private int mVirtualKeyboardHeight;

    @Bind(R.id.fl_content)
    protected FrameLayout mContentFl;

    @Bind(R.id.btn_publish)
    protected Button mPublishBtn;

    @Bind(R.id.ib_emoji)
    protected ImageButton mEmojiIb;

    @Bind(R.id.et_comment)
    protected EditText mCommentEt;

    @Bind(R.id.emoji_view)
    protected EmojiView mEmojiView;

    @Bind(R.id.rl_comments)
    protected SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.rv_comments)
    protected RecyclerView mCommentsRv;

    @Bind(R.id.tv_empty)
    protected TextView mEmptyTv;

    private ResourceCommentsAdapter mCommentsAdapter;

    private SendingDialogPresenter mSendingDialogPresenter;

    private RequestSender mRequestSender;

    private InputMethodManager mInputManager;

    public static ResCommentsFragment newInstance() {
        ResCommentsFragment fragment = new ResCommentsFragment();
        return fragment;
    }

    public ResCommentsFragment() {
        mSendingDialogPresenter = new SendingDialogPresenter(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mRequestSender = new RequestSender(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserInfo = getArguments().getParcelable(ARG_USER_INFO);
            mResourceId = getArguments().getString(ARG_RESOURCE_ID);
        } else {
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                mUserInfo = intent.getParcelableExtra(Extra.USER_INFO);
                mResourceId = intent.getStringExtra(DocumentCommentActivity.EXTRA_RESOURCE_ID);
            }
        }
        mInputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_res_comments, container, false);
        ButterKnife.bind(this, view);
        mEmojiView.setEditText(mCommentEt);
//        mCommentEt.addTextChangedListener(mTextWatcher);
        setListenerToRootView();
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mCommentsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mCommentsAdapter = new ResourceCommentsAdapter();
        mCommentsRv.setAdapter(mCommentsAdapter);
        initCommentTextFilter();
        restoreDialogStatus(savedInstanceState);
        return view;
    }

    private void restoreDialogStatus(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            DeleteCommentDialog deleteCommentDialog = (DeleteCommentDialog) getFragmentManager()
                    .findFragmentByTag("delete_comment");
            if (deleteCommentDialog != null) {
                deleteCommentDialog.setOnOkClickListener(mOnDeleteCommentConfirm);
            }
        }
    }

    public void setListenerToRootView(){
        final View activityRootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mVirtualKeyboardHeight = activityRootView.getRootView().getHeight() - activityRootView.getHeight()
                        - InputUtils.getStatusBarHeight(getActivity());
                if (mVirtualKeyboardHeight > 180 ) { // 99.9999%情况下高度差是键盘导致的。
                    //实际情况中，键盘弹出时输入框换行时高度变化也会跑这里，
                    //因为键盘弹出时高度差超过100了，而输入框高度变化时会触发全局布局监听器
                    if(!mVirtualKeyboardOpened){
                        onSoftKeyboardOpened();
                    }
                    mVirtualKeyboardOpened = true;
                }else if(mVirtualKeyboardOpened){
                    mVirtualKeyboardOpened = false;
                }
            }
        });
    }

    private void onSoftKeyboardOpened() {
        mEmojiView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mVirtualKeyboardHeight > 0) {
                    LayoutParams layoutParams = mEmojiView.getLayoutParams();
                    layoutParams.height = mVirtualKeyboardHeight;
                }
                mEmojiView.setVisibility(View.GONE);
                mEmojiIb.setClickable(true);
            }
        }, 200);
    }

    private void initCommentTextFilter() {
        LengthFilter lengthFilter = new LengthFilter(COMMENT_MAX_LENGTH);
        mCommentEt.setFilters(new InputFilter[]{lengthFilter});
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchComments(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private boolean mIsLoadingMore;

    /**
     * 一级更多评论被点击
     * @param moreCommentsEvent
     */
    public void onEvent(MoreCommentsEvent moreCommentsEvent) {
        if (!mIsLoadingMore) {
            fetchComments(false);
        }
    }

    public void onEvent(MoreRelies moreRelies) {
        if (!mIsLoadingMore) {
            onMoreReplyClick(moreRelies);
        }
    }

    public void onEvent(Comment replyingComment) {
        startToInputReply(replyingComment);
    }

    /**
     * 正在回复的评论
     */
    private CommentBase mReplyingComment;

    public void startToInputReply(CommentBase comment) {
        mReplyingComment = comment;
        String replyTo = getString(R.string.rely_to, mReplyingComment.getUserRealName());
        mCommentEt.getText().clear();
        mCommentEt.setHint(replyTo);
        mCommentEt.requestFocus();
        mInputManager.showSoftInput(mCommentEt, InputMethodManager.SHOW_IMPLICIT);
    }

    public void onEvent(DeleteCommentEvent deleteCommentEvent) {
        showDeleteCommentDialog(deleteCommentEvent.getPosition());
    }

    public void onEvent(DeleteReplyEvent deleteReplyEvent) {
        showDeleteReplyDialog(deleteReplyEvent.getPosition());
    }

    public void showDeleteReplyDialog(int position) {
        DeleteCommentDialog deleteCommentDialog = DeleteCommentDialog.newInstance(position, R.string.confirm_to_delete_reply);
        deleteCommentDialog.setOnOkClickListener(mOnDeleteCommentConfirm);
        deleteCommentDialog.show(getFragmentManager(), "delete_comment");
    }

    public void showDeleteCommentDialog(int position) {
        DeleteCommentDialog deleteCommentDialog = DeleteCommentDialog.newInstance(position, R.string.confirm_to_delete_comment);
        deleteCommentDialog.setOnOkClickListener(mOnDeleteCommentConfirm);
        deleteCommentDialog.show(getFragmentManager(), "delete_comment");
    }

    private OnOkClickListener mOnDeleteCommentConfirm = new OnOkClickListener() {
        @Override
        public void onOkClickListener(final int position) {
            Cog.d(TAG, "+onOkClickListener id=", position);
            final CommentBase commentBase = (CommentBase) mCommentsAdapter.getItem(position);
            if (commentBase == null) return;

            Map<String, String> params = new HashMap<>();
            params.put("uuid", mUserInfo.getUuid());
            params.put("resourceCommentId", commentBase.getId());
            mRequestSender.sendRequest(new RequestData(URLConfig.DELETE_RES_COMMENT, params, new Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Cog.d(TAG, "delete comment response=", response);
                    if ("success".equals(response.optString("result"))){
                        String toastStr;
                        if (commentBase instanceof Comment) {
                            toastStr = "删除评论成功！";
                        } else {
                            toastStr = "删除回复成功！";
                        }
                        mCommentsAdapter.remove(position);
                        ToastUtil.showToast(getContext(), toastStr, Toast.LENGTH_SHORT);
                    } else {
                        ToastUtil.showToast(getContext(), response.optString("message"), Toast.LENGTH_SHORT);
                    }
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Cog.e(TAG, "delete comment error=", error);
                }
            }));
        }
    };

    public void onEvent(Reply reply){
        startToInputReply(reply);
    }

    public void onEvent(String resourceId) {
        mResourceId = resourceId;
        getArguments().putString(ARG_RESOURCE_ID, resourceId);
        fetchComments(true);
    }

    /**
     * 抓取评论
     */
    private void fetchComments(final boolean isRefresh) {
        Map<String, String> params = new HashMap<>();

        if (isRefresh) {
            mStart = 0;
        }
        int start = mStart;
        int end = start + LOAD_COUNT;
        params.put("start", "" + start);
        params.put("end", "" + (end - 1));

        params.put("uuid", mUserInfo.getUuid());
        params.put("resourceId", mResourceId);

        mIsLoadingMore = true;
        mRequestSender.sendRequest(new RequestData(URLConfig.RESOURCE_COMMENTS, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "getComments response", response);
                mIsLoadingMore = false;
                stopRefreshing();
                if ("success".equals(response.optString("result"))) {
                    mTotal = response.optInt("total");
                    if (mTotal == 0) {
                        mCommentsAdapter.clear();
                        mCommentsAdapter.notifyDataSetChanged();
                        mEmptyTv.setVisibility(View.VISIBLE);
                    } else {
                        if (isRefresh) {
                            mCommentsAdapter.clear();
                        }
                        mEmptyTv.setVisibility(View.GONE);
                        JSONArray jsonArray = response.optJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject commentJsonObj = jsonArray.optJSONObject(i);
                            Comment comment = new Comment();
                            comment.setContent(commentJsonObj.optString("commentContent"));
                            comment.setId(commentJsonObj.optString("resourceCommentId"));
                            comment.setCreateTime(commentJsonObj.optLong("createTime"));
                            comment.setTotalReplyCount(commentJsonObj.optInt("childrenCommentSize"));
                            comment.setUserType(commentJsonObj.optString("userType"));

                            JSONArray repliesJsonArray = commentJsonObj.optJSONArray("childrenCommentList");
                            parseRepliesJsonArray(comment, repliesJsonArray);//解析子评论

                            comment.setUserIcon(commentJsonObj.optString("headPicUrl"));
                            comment.setUserId(commentJsonObj.optString("commentUserId"));
                            comment.setUserRealName(commentJsonObj.optString("userName"));
                            comment.setMine(mUserInfo.getBaseUserId().equals(comment.getUserId()));
                            mCommentsAdapter.addComment(comment);
                        }
                        mCommentsAdapter.setHasMore(mTotal > mCommentsAdapter.getCommentCount());
                        mCommentsAdapter.notifyDataSetChanged();
                        mStart = mCommentsAdapter.getCommentCount();
                    }
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.d(TAG, "getComments error", error);
                mIsLoadingMore = false;
                stopRefreshing();
                UIUtils.toast(getContext(), "获取评论失败，请检查网络。", Toast.LENGTH_SHORT);
            }
        }));
    }

    /**
     * 解析回复json数组并加入到对应回复中
     * @param rethinkComment
     * @param repliesJsonArray
     */
    private void parseRepliesJsonArray(Comment rethinkComment, JSONArray repliesJsonArray) {
        for (int j=0; j < repliesJsonArray.length(); j++) {
            JSONObject replyJsonObj = repliesJsonArray.optJSONObject(j);
            Reply rethinkReply = parseReplyJsonObject(replyJsonObj, rethinkComment);
            rethinkComment.addReply(rethinkReply);
        }
    }

    @NonNull
    private Reply parseReplyJsonObject(JSONObject replyJsonObj , Comment comment) {
        Reply reply = new Reply();
        reply.setReplyToUserId(replyJsonObj.optString("replyToUserId"));
        reply.setContent(replyJsonObj.optString("commentContent"));
        reply.setId(replyJsonObj.optString("resourceCommentId"));
        reply.setCreateTime(replyJsonObj.optLong("createTime"));
        reply.setReplyToName(replyJsonObj.optString("replyToUserName"));
        reply.setUserId(replyJsonObj.optString("commentUserId"));
        reply.setUserIcon(replyJsonObj.optString("headPicUrl"));
        reply.setUserRealName(replyJsonObj.optString("userName"));
        reply.setMine(mUserInfo.getBaseUserId().equals(reply.getUserId()));
        reply.setComment(comment);
        return reply;
    }

    public void onMoreReplyClick(MoreRelies data) {
        final Comment comment = data.getRethinkComment();
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("parentCommentId", comment.getId());
        int start = comment.getCurrentCount();
        params.put("start", start + "");
        params.put("end", start + 4 + "");
        mIsLoadingMore = true;
        mRequestSender.sendRequest(new RequestData(URLConfig.RESOURCE_RELIES, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onMoreReplyClick response=", response);
                mIsLoadingMore = false;
                if ("success".equals(response.optString("result"))) {
                    JSONArray repliesJa = response.optJSONArray("data");
                    comment.setTotalReplyCount(response.optInt("total"));
                    if (repliesJa != null && repliesJa.length() > 0) {
                        List<Reply> replies = new ArrayList<>(repliesJa.length());
                        for (int j=0; j < repliesJa.length(); j++) {
                            JSONObject replyJsonObj = repliesJa.optJSONObject(j);
                            Reply rethinkReply = parseReplyJsonObject(replyJsonObj, comment);
                            replies.add(rethinkReply);
                        }
                        mCommentsAdapter.addReplies(comment, replies);
                    }
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.d(TAG, "onMoreReplyClick error=", error);
                mIsLoadingMore = false;
                UIUtils.toast(getContext(), "获取更多回复失败！",Toast.LENGTH_SHORT);
            }
        }));
    }

    @Override
    public void onRefresh() {
        fetchComments(true);
    }

    public void stopRefreshing() {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void onLoadMoreClick() {
        fetchComments(false);
    }

    @OnClick(R.id.btn_publish)
    public void onPublishBtnClick(View view) {
        if (!NetworkUtils.isConnected()) {
            UIUtils.toast(R.string.please_connect_internet, Toast.LENGTH_SHORT);
            return;
        }
        String commentContent = obtainCommentContent();
        if (commentContent == null) {
            Toast.makeText(getContext(), R.string.please_type_in_comment_content, Toast.LENGTH_SHORT).show();
            return;
        }
        publishComment(commentContent);
    }

    @Override
    public void onPause() {
        super.onPause();
        closeVirtualKeyboard();
    }

    /**
     * 发表评论
     */
    private void publishComment(String commentContent) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("resourceId", mResourceId);
        params.put("commentUserId", mUserInfo.getBaseUserId());
        params.put("commentContent", commentContent);

        mSendingDialogPresenter.show();

        if (mReplyingComment != null) {
            if (mReplyingComment instanceof Reply) {
                Reply reply = (Reply) mReplyingComment;
                params.put("parentCommentId", reply.getParentId());
            } else {
                params.put("parentCommentId", mReplyingComment.getId());
            }
            params.put("replyToUserId", mReplyingComment.getUserId());
        }

        mRequestSender.sendRequest(new RequestData(URLConfig.COMMENT_RESOURCE, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "publishComment response:", response);
                mSendingDialogPresenter.dismiss();
                if ("success".equals(response.optString("result"))) {
                    UIUtils.toast(getContext(), getString(R.string.publish_comment_successfully), Toast.LENGTH_SHORT);
                    clearReplyToAndClear();

                    closeVirtualKeyboard();
                    hideEmojiKeyboard();
                    notifyCommentsListToUpdate();
                } else {
                    UIUtils.toast(getContext(), response.optString("message"), Toast.LENGTH_SHORT);
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.d(TAG, "publishComment error:", error);
                mSendingDialogPresenter.dismiss();
                UIUtils.toast( getContext(), "评论教学反思失败。请检查网络。", Toast.LENGTH_SHORT);
            }
        }));
    }

    private void clearReplyToAndClear() {
        if (mReplyingComment != null) {
            mReplyingComment = null;//回复成功，清除被回复的评论
        }
        mCommentEt.setHint(R.string.say_something);
        mCommentEt.getText().clear();
    }

    /**
     * 通知评论列表更新
     */
    private void notifyCommentsListToUpdate() {
        mRefreshLayout.setRefreshing(true);
        fetchComments(true);
    }

    /**
     * 从输入框中获取评论内容,在回复时要去除“回复某某某：”头
     * @return
     */
    @Nullable
    private String obtainCommentContent() {
        String commentContent = mCommentEt.getText().toString();
        if (TextUtils.isEmpty(commentContent.trim())) {
            return null;
        }
        return commentContent;
    }

    @OnClick(R.id.et_comment)
    public void onCommentEtClick(View view) {
        if (mEmojiView.getVisibility() == View.VISIBLE) {
            hideEmojiView();
        }
        if (!mVirtualKeyboardOpened) {
            openVirtualKeyboard();
        }
    }

    @OnClick(R.id.ib_emoji)
    public void onEmojiBtnClick(View view) {
        if (mEmojiView.getVisibility() == View.GONE) {
            if (mVirtualKeyboardOpened) {
                mEmojiIb.setClickable(false);//防止连点
                closeVirtualKeyboard();
                lockContentLayoutHeight(mContentFl.getHeight());
                postUnlockContentLayoutHeight(true);
            }
            mEmojiView.setVisibility(View.VISIBLE);
            mCommentEt.requestFocus();
        } else {
            mEmojiIb.setClickable(false);//防止连点
            hideEmojiView();
            openVirtualKeyboard();
        }
    }

    private void lockContentLayoutHeight(int height) {
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) mContentFl.getLayoutParams();
        localLayoutParams.height = height;
        localLayoutParams.weight = 0.0F;
    }

    private void hideEmojiView() {
//        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) mContentFl.getLayoutParams();
//        localLayoutParams.height = mContentFl.getHeight() - mVirtualKeyboardHeight;
//        localLayoutParams.weight = 0.0F;
        lockContentLayoutHeight(mContentFl.getHeight() - mVirtualKeyboardHeight);
        postUnlockContentLayoutHeight(false);
    }

    private void postUnlockContentLayoutHeight(final boolean needSetBtnClickable) {
        mContentFl.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentFl.getLayoutParams()).weight = 1.0F;
                if (needSetBtnClickable) mEmojiIb.setClickable(true);//重新使表情切换的按钮能点击
            }
        }, 200);
    }

    /**
     * 关闭虚拟键盘
     */
    private void closeVirtualKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            mInputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void openVirtualKeyboard() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (mInputManager != null) {
            mInputManager.showSoftInput(mCommentEt,InputMethodManager.SHOW_FORCED);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed() && !isVisibleToUser) {
            if (mVirtualKeyboardOpened) {
                closeVirtualKeyboard();
            }
            if (mEmojiView.getVisibility() == View.VISIBLE) {
                mEmojiView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public FragmentManager obtainFragmentManager() {
        return getFragmentManager();
    }

    /**
     * 关闭表情键盘，用来返回被点时处理
     * @return
     */
    public boolean hideEmojiKeyboard() {
        if (mEmojiView.getVisibility() == View.VISIBLE) {
            LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) mContentFl.getLayoutParams();
            localLayoutParams.height = 0;
            localLayoutParams.weight = 1f;
            mEmojiView.post(new Runnable() {
                @Override
                public void run() {
                    mEmojiView.setVisibility(View.GONE);
                }
            });
            return true;
        }
        return false;
    }

}
