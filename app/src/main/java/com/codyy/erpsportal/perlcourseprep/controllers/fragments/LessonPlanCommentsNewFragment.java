package com.codyy.erpsportal.perlcourseprep.controllers.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.common.EmotionInputDetector;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.DeleteCommentDialog;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.EmojiView;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.MoreRelies;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.presenters.IFragmentManagerProvider;
import com.codyy.erpsportal.commons.models.presenters.SendingDialogPresenter;
import com.codyy.erpsportal.perlcourseprep.controllers.adapters.PcpCommentsAdapter;
import com.codyy.erpsportal.perlcourseprep.controllers.adapters.PcpCommentsAdapter.HeaderUpdater;
import com.codyy.erpsportal.perlcourseprep.models.entities.CommentsHeader;
import com.codyy.erpsportal.rethink.models.entities.DeleteCommentEvent;
import com.codyy.erpsportal.rethink.models.entities.DeleteReplyEvent;
import com.codyy.erpsportal.rethink.models.entities.MoreCommentsEvent;
import com.codyy.erpsportal.rethink.models.entities.RethinkComment;
import com.codyy.erpsportal.rethink.models.entities.RethinkCommentBase;
import com.codyy.erpsportal.rethink.models.entities.RethinkReply;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 个人备课-评论列表
 */
public class LessonPlanCommentsNewFragment extends Fragment implements IFragmentManagerProvider, OnRefreshListener {

    private static final String TAG = "ResourceCommentsFragment";

    public static final String ARG_USER_INFO = "arg_user_info";

    public static final String ARG_LESSON_PLAN_ID = "lessonPlanId";

    public static final String ARG_RATE = "rate";

    private final static int LOAD_COUNT = 10;

    private int mTotal;

    private UserInfo mUserInfo;

    private String mLessonPlanId;

    private float mRate;

    private int mStart;

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

    private PcpCommentsAdapter mCommentsAdapter;

    private SendingDialogPresenter mSendingDialogPresenter;

    private RequestSender mRequestSender;

    private EmotionInputDetector mDetector;

    public static LessonPlanCommentsNewFragment newInstance(UserInfo userInfo,
                                                            String lessonPlanId,
                                                            float rate) {
        LessonPlanCommentsNewFragment fragment = new LessonPlanCommentsNewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_USER_INFO, userInfo);
        bundle.putString(ARG_LESSON_PLAN_ID, lessonPlanId);
        bundle.putFloat(ARG_RATE, rate);
        fragment.setArguments(bundle);
        return fragment;
    }

    public LessonPlanCommentsNewFragment() {
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
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        if (getArguments() != null) {
            mUserInfo = getArguments().getParcelable(ARG_USER_INFO);
            mLessonPlanId = getArguments().getString(ARG_LESSON_PLAN_ID);
            mRate = getArguments().getFloat(ARG_RATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments_with_detector, container, false);
        ButterKnife.bind(this, view);
        mEmojiView.setEditText(mCommentEt, 150);
        mDetector = EmotionInputDetector.with(getActivity())
                .setEmotionView(mEmojiView)
                .bindToContent(mContentFl)
                .bindToEditText(mCommentEt)
                .bindToEmotionButton(mEmojiIb)
                .bindToSend(mPublishBtn)
                .setOnSendClickListener(new EmotionInputDetector.OnSendClickListener() {
                    @Override
                    public void onSendClickListener(String content) {
                        publishComment(content);
                    }
                })
                .build();
        mRefreshLayout.setOnRefreshListener(this);
        mCommentsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mCommentsAdapter = new PcpCommentsAdapter();
        mCommentsRv.setAdapter(mCommentsAdapter);
        mCommentsAdapter.updateHeader(new HeaderUpdater() {
            @Override
            public void update(CommentsHeader header) {
                header.setRate(mRate);
            }
        });
        return view;
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

    public void onEvent(RethinkComment replyingComment) {
        startToInputReply(replyingComment);
    }

    /**
     * 正在回复的评论
     */
    private RethinkCommentBase mReplyingComment;

//    private String mReplyTo;

    public void startToInputReply(RethinkCommentBase comment) {
        mReplyingComment = comment;
        String replyTo = getString(R.string.rely_to, mReplyingComment.getUserRealName());
        mDetector.startToInputReply(replyTo);
    }


    public void onEvent(DeleteCommentEvent deleteCommentEvent) {
        showDeleteCommentDialog(deleteCommentEvent.getPosition());
    }

    public void onEvent(DeleteReplyEvent deleteReplyEvent) {
        showDeleteReplyDialog(deleteReplyEvent.getPosition());
    }

    /**
     * 评论类型
     */
    private final static int TYPE_COMMENT = 1;

    /**
     * 评论回复类型
     */
    private final static int TYPE_REPLY = 3;

    public void showDeleteReplyDialog(int position) {
        DeleteCommentDialog deleteCommentDialog = DeleteCommentDialog.newInstance(position, R.string.confirm_to_delete_reply);
        deleteCommentDialog.setOnOkClickListener(new DeleteCommentDialog.OnOkClickListener() {
            @Override
            public void onOkClickListener(int position) {
                switch (mCommentsAdapter.getItemViewType(position)) {
                    case TYPE_COMMENT:
                        RethinkComment rethinkComment = (RethinkComment) mCommentsAdapter.getItem(position);
                        deleteComment(rethinkComment.getId(), position);
                        break;
                    case TYPE_REPLY:
                        RethinkReply rethinkReply = (RethinkReply) mCommentsAdapter.getItem(position);
                        deleteComment(rethinkReply.getId(), position);
                        break;
                }
            }
        });
        deleteCommentDialog.show(getFragmentManager(), "delete_reply");
    }

    public void showDeleteCommentDialog(int position) {
        DeleteCommentDialog deleteCommentDialog = DeleteCommentDialog.newInstance(position, R.string.confirm_to_delete_comment);
        deleteCommentDialog.setOnOkClickListener(new DeleteCommentDialog.OnOkClickListener() {
            @Override
            public void onOkClickListener(int position) {
                switch (mCommentsAdapter.getItemViewType(position)) {
                    case TYPE_COMMENT:
                        RethinkComment rethinkComment = (RethinkComment) mCommentsAdapter.getItem(position);
                        deleteComment(rethinkComment.getId(), position);
                        break;
                    case TYPE_REPLY:
                        RethinkReply rethinkReply = (RethinkReply) mCommentsAdapter.getItem(position);
                        deleteComment(rethinkReply.getId(), position);
                        break;
                }
            }
        });
        deleteCommentDialog.show(getFragmentManager(), "delete_comment");
    }

    /**
     * 删除评论
     *
     * @param commentId
     * @param position
     */
    private void deleteComment(String commentId, final int position) {
        Map<String, String> map = new HashMap<>(2);
        map.put("uuid", mUserInfo.getUuid());
        map.put("commentId", commentId);
        mRequestSender.sendRequest(new RequestData(URLConfig.LESSON_PLAN_DELETE_COMMENT, map, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "getComments response", response);
                stopRefreshing();
                if ("success".equals(response.optString("result"))) {
//                    mCommentsAdapter.remove(position);
                    fetchComments(true);
                } else {
                    UIUtils.toast(getContext(), "删除评论失败。", Toast.LENGTH_SHORT);
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.d(TAG, "getComments error", error);
                UIUtils.toast(getContext(), "删除评论失败，请检查网络。", Toast.LENGTH_SHORT);
            }
        }));
    }

    public void onEvent(RethinkReply reply) {
        startToInputReply(reply);
    }

    public void onEvent(String resourceId) {
        getArguments().putString(ARG_LESSON_PLAN_ID, resourceId);
        fetchComments(true);
    }

    /**
     * 抓取个人备课评论
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
        params.put("lessonPlanId", mLessonPlanId);
        mIsLoadingMore = true;
        mRequestSender.sendRequest(new RequestData(URLConfig.LESSON_PLAN_COMMENTS, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "getComments response", response);
                mIsLoadingMore = false;
                stopRefreshing();
                if ("success".equals(response.optString("result"))) {
                    if (isRefresh) {
                        mCommentsAdapter.clear();
                    }
                    mTotal = response.optInt("total");
                    updateTotal();
                    JSONArray jsonArray = response.optJSONArray("list");
                    for (int i = 0; i < jsonArray.length(); i++) {
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
                        mCommentsAdapter.addComment(rethinkComment);
                    }
                    mCommentsAdapter.setHasMore(mTotal > mCommentsAdapter.getCommentCount());
                    mCommentsAdapter.notifyDataSetChanged();
                    if (mCommentsAdapter.getCommentCount() == 0) {
                        mEmptyTv.setVisibility(View.VISIBLE);
                    } else {
                        mEmptyTv.setVisibility(View.GONE);
                    }
                    mStart = mCommentsAdapter.getCommentCount();
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

    private void updateTotal() {
        mCommentsAdapter.updateHeader(new HeaderUpdater() {
            @Override
            public void update(CommentsHeader header) {
                header.setTotal(mTotal);
            }
        });
    }

    /**
     * 解析回复json数组并加入到对应回复中
     *
     * @param rethinkComment
     * @param repliesJsonArray
     */
    private void parseRepliesJsonArray(RethinkComment rethinkComment, JSONArray repliesJsonArray) {
        for (int j = 0; j < repliesJsonArray.length(); j++) {
            JSONObject replyJsonObj = repliesJsonArray.optJSONObject(j);
            RethinkReply rethinkReply = parseReplyJsonObject(replyJsonObj, rethinkComment);
            rethinkComment.addReply(rethinkReply);
        }
    }

    @NonNull
    private RethinkReply parseReplyJsonObject(JSONObject replyJsonObj, RethinkComment comment) {
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

    public void onMoreReplyClick(MoreRelies data) {
        final RethinkComment rethinkComment = data.getRethinkComment();
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("commId", rethinkComment.getId());
        int start = rethinkComment.getCurrentCount();
        params.put("start", start + "");
        params.put("end", start + 4 + "");
        mIsLoadingMore = true;
        mRequestSender.sendRequest(new RequestData(URLConfig.LESSON_PLAN_MORE_COMMENT, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onMoreReplyClick response=", response);
                mIsLoadingMore = false;
                if ("success".equals(response.optString("result"))) {
                    JSONArray repliesJa = response.optJSONArray("list");
                    rethinkComment.setTotalReplyCount(response.optInt("total"));
                    if (repliesJa.length() > 0) {
                        List<RethinkReply> replies = new ArrayList<>(repliesJa.length());
                        for (int j = 0; j < repliesJa.length(); j++) {
                            JSONObject replyJsonObj = repliesJa.optJSONObject(j);
                            RethinkReply rethinkReply = parseReplyJsonObject(replyJsonObj, rethinkComment);
                            replies.add(rethinkReply);
                        }
                        mCommentsAdapter.addReplies(rethinkComment, replies);
                    }
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.d(TAG, "onMoreReplyClick error=", error);
                mIsLoadingMore = false;
                UIUtils.toast(getContext(), "获取更多回复失败！", Toast.LENGTH_SHORT);
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


    /**
     * 发表评论
     */
    private void publishComment(String commentContent) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("lessonPlanId", getArguments().getString("lessonPlanId", ""));
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

        mRequestSender.sendRequest(new RequestData(URLConfig.LESSON_PLAN_ADD_COMMENT, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "publishComment response:", response);
                mDetector.hide();
                mSendingDialogPresenter.dismiss();
                if ("success".equals(response.optString("result"))) {
                    UIUtils.toast(getContext(), getString(R.string.publish_comment_successfully), Toast.LENGTH_SHORT);
                    clearReplyToAndClear();
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
                UIUtils.toast(getContext(), "评论个人备课失败。请检查网络。", Toast.LENGTH_SHORT);
            }
        }));
    }

    private void clearReplyToAndClear() {
        if (mReplyingComment != null) {
            mReplyingComment = null;//回复成功，清除被回复的评论
//            mReplyTo = null;
            mDetector.clearReplyTo();
        }
        mDetector.clear();
    }

    /**
     * 通知评论列表更新
     */
    private void notifyCommentsListToUpdate() {
        mRefreshLayout.setRefreshing(true);
        fetchComments(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed() && !isVisibleToUser) {
            mDetector.hide();
        }
    }

    @Override
    public FragmentManager obtainFragmentManager() {
        return getFragmentManager();
    }

}
