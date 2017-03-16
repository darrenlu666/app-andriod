package com.codyy.erpsportal.onlineteach.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.common.EmotionInputDetector;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.adapters.HorizontalListViewAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.DeleteCommentDialog;
import com.codyy.erpsportal.commons.services.FileDownloadService;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.VideoDownloadUtils;
import com.codyy.erpsportal.commons.widgets.BNVideoControlView;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;
import com.codyy.erpsportal.commons.widgets.EmojiView;
import com.codyy.erpsportal.commons.widgets.HorizontalListView;
import com.codyy.erpsportal.commons.models.entities.MoreRelies;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsDetailEntity;
import com.codyy.erpsportal.commons.models.entities.VideoDetails;
import com.codyy.erpsportal.commons.models.presenters.IFragmentManagerProvider;
import com.codyy.erpsportal.commons.models.presenters.SendingDialogPresenter;
import com.codyy.erpsportal.resource.models.entities.ResourceDetails;
import com.codyy.erpsportal.rethink.controllers.adapters.BaseCommentsAdapter;
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

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 2016-08-16 ＂网络授课＂
 */
public class NetTechVideoActivity extends BaseHttpActivity implements BnVideoView2.OnBNCompleteListener, Handler.Callback, IFragmentManagerProvider, SwipeRefreshLayout.OnRefreshListener {
    private final static String TAG = "NetTechVideoActivity";
    @Bind(R.id.fl_content)LinearLayout mContentFl;
    @Bind(R.id.btn_publish)Button mPublishBtn;
    @Bind(R.id.ib_emoji)ImageButton mEmojiIb;
    @Bind(R.id.et_comment)EditText mCommentEt;
    @Bind(R.id.emoji_view)EmojiView mEmojiView;
    @Bind(R.id.rl_comments)SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.rv_comments)RecyclerView mCommentsRv;
    @Bind(R.id.title) TextView mTitleTv;
    @Bind(R.id.activity_theme_text_down1) TextView mDownBtn;
    @Bind(R.id.tv_video_count) TextView mTvVideoCount;
    @Bind(R.id.tv_video_list) TextView mTvVideoList;
    @Bind(R.id.hl_video) HorizontalListView mListView;
    @Bind(R.id.video_view) BnVideoView2 mVideoView;
    @Bind(R.id.video_area) FrameLayout mFrameLayout;
    @Bind(R.id.videoControl) BNVideoControlView mVideoControl;
    @Bind(R.id.rltControlTitle) RelativeLayout rltTitle;

    private String mPreparationId;
    private PrepareLessonsDetailEntity mPrepareLessonsDetailEntity;
    private List<VideoDetails> videoDetailsList = new ArrayList<>();
    private HorizontalListViewAdapter mListViewAdapter;
    private int mCurIndex = 0;
    public Handler handler;
    private boolean isVideoPlayEnd = false;//视频是否已经播放结束
    private final static int LOAD_COUNT = 10;
    private int mTotal;
    private int mStart;
    private BaseCommentsAdapter mCommentsAdapter;
    private SendingDialogPresenter mSendingDialogPresenter;
    private EmotionInputDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        handler = new Handler(this);
        mPrepareLessonsDetailEntity = getIntent().getParcelableExtra(Constants.PREPARE_DATA);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_activity_video;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_NET_TEACH_VIDEO_DETAIL;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("lessonId", mPreparationId);
        return params;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG, "onResponse:" + response);
        String result = response.optString("result");
        if ("success".equals(result)) {
            int total = response.optInt("total");
            if (total == 0) {

            } else {
                JSONArray jsonArray = response.optJSONArray("videos");
                videoDetailsList.addAll(VideoDetails.parseJsonArray(jsonArray));
                if (videoDetailsList != null) {
                    if (videoDetailsList.size() >0){
                        mTitleTv.setText(videoDetailsList.get(0).getVideoName());
                        mListViewAdapter.setSelectIndex(0);
                        playVideo(videoDetailsList.get(0));
                        mListViewAdapter.notifyDataSetChanged();

                        if (videoDetailsList.size() == 1) {
                            mDownBtn.setVisibility(View.VISIBLE);
                            mTvVideoCount.setVisibility(View.GONE);
                            mTvVideoList.setVisibility(View.GONE);
                            mListView.setVisibility(View.GONE);
                            findViewById(R.id.btn_download).setVisibility(View.GONE);
                        }
                    }
                    mTvVideoCount.setText("共" + videoDetailsList.size() + "段");
                }
            }
        } else if ("error".equals(result)) {
            String message = response.optString("message");
            UIUtils.toast(NetTechVideoActivity.this, message, Toast.LENGTH_SHORT);
            finish();
        }
        //get first level comments .
        fetchComments(true);
    }

    @Override
    public void onFailure(VolleyError error) {
        Cog.e(TAG, "onErrorResponse:" + error);
        UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
        //get first level comments .
        fetchComments(true);
    }

    @Override
    public void init() {
        mPreparationId = getIntent().getStringExtra(Constants.PREPARATIONID);
        mSendingDialogPresenter = new SendingDialogPresenter(this);
        rltTitle.setVisibility(View.GONE);
        mVideoControl.bindVideoView(mVideoView,getSupportFragmentManager());
        mVideoControl.setOnCompleteListener(this);
        mVideoControl.setDisplayListener(new BNVideoControlView.DisplayListener() {

            @Override
            public void show() {
                rltTitle.setVisibility(View.VISIBLE);
            }

            @Override
            public void hide() {
                rltTitle.setVisibility(View.GONE);
            }

        });

        mListViewAdapter = new HorizontalListViewAdapter(this, videoDetailsList);
        mListView.setAdapter(mListViewAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurIndex = position;
                isVideoPlayEnd = true;
                stopPlayVideo();
                if (mCurIndex <= videoDetailsList.size() - 1) {
                    Cog.d("----------------------------", "----------播放下一集----------");
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                }
            }

        });

        mVideoControl.setOnErrorListener(new BnVideoView2.OnBNErrorListener() {
            @Override
            public void onError(int errorCode, String errorMsg) {
                if (-2 == errorCode || 0 == errorCode) {
                    //do your action .\
                    Cog.d("------------xxx----------------:", "onError()");
                    mCurIndex = mCurIndex + 1;
                    if (mCurIndex <= videoDetailsList.size() - 1) {
                        isVideoPlayEnd = true;
                    }
                }
            }
        });
        mEmojiView.setEditText(mCommentEt);
        mDetector = EmotionInputDetector.with(this)
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
        mCommentsRv.setLayoutManager(new LinearLayoutManager(this));
        mCommentsAdapter = new BaseCommentsAdapter();
        mCommentsRv.setAdapter(mCommentsAdapter);
        //get detail data .
        requestData(true);
    }

    @OnClick({R.id.activity_theme_text_down1,R.id.btn_download})
    void goDownload(){
        if (mCurIndex > (videoDetailsList.size() - 1)){
            mCurIndex = videoDetailsList.size() - 1;
        }
        VideoDetails videoDetails = videoDetailsList.get(mCurIndex);
        ResourceDetails resourceDetails = new ResourceDetails();
        resourceDetails.setId(videoDetails.getMeetVideoId());
        resourceDetails.setResourceName(videoDetails.getVideoName());
        resourceDetails.setThumbPath(videoDetails.getThumbPath());
        // TODO: 16-8-16 验证现在现在是否全部为绝对下载地址. ??
        VideoDownloadUtils.downloadVideo(resourceDetails, videoDetails.getFilePath(), mUserInfo.getBaseUserId());
    }

    @Override
    public void onBackPressed() {
        if (!mDetector.interceptBackPress()) {
            super.onBackPressed();
        }
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
        deleteCommentDialog.show(obtainFragmentManager(), "delete_reply");
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
        deleteCommentDialog.show(obtainFragmentManager(), "delete_comment");
    }

    /**
     * 删除评论
     *
     * @param commentId
     * @param position
     */
    private void deleteComment(String commentId, final int position) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put("uuid", mUserInfo.getUuid());
        map.put("commentId", commentId);
        requestData(URLConfig.DELETE_NET_TEACH_COMMENT, map, false,new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                Cog.d(TAG, "getComments response", response);
                stopRefreshing();
                if ("success".equals(response.optString("result"))) {
//                    mCommentsAdapter.remove(position);
                    fetchComments(true);
                } else {
                    UIUtils.toast(NetTechVideoActivity.this, "删除评论失败。", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onRequestFailure(VolleyError error) {
                stopRefreshing();
                Cog.d(TAG, "getComments error", error);
                UIUtils.toast(NetTechVideoActivity.this, "删除评论失败，请检查网络。", Toast.LENGTH_SHORT);
            }
        });
    }

    public void onEvent(RethinkReply reply) {
        startToInputReply(reply);
    }

    /**
     * 抓取个人备课评论
     */
    private void fetchComments(final boolean isRefresh) {
        HashMap<String, String> params = new HashMap<>();
        if (isRefresh) {
            mStart = 0;
        }
        int start = mStart;
        int end = start + LOAD_COUNT;
        params.put("start", "" + start);
        params.put("end", "" + (end - 1));
        params.put("uuid", mUserInfo.getUuid());
        params.put("meetingId", mPreparationId);
        mIsLoadingMore = true;
        requestData(URLConfig.GET_NET_TEACH_COMMENT_LIST,params,false,new IRequest(){
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                Cog.d(TAG, "getComments response", response);
                mIsLoadingMore = false;
                stopRefreshing();
                if (isRefresh) {
                    mCommentsAdapter.clear();
                }
                mTotal = response.optInt("total");
                JSONArray jsonArray = response.optJSONArray("comments");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject commentJsonObj = jsonArray.optJSONObject(i);
                    RethinkComment rethinkComment = new RethinkComment();
                    rethinkComment.setContent(commentJsonObj.optString("content"));
                    rethinkComment.setId(commentJsonObj.optString("meetCommentId"));
                    rethinkComment.setCreateTime(commentJsonObj.optString("createTime"));
                    rethinkComment.setTotalReplyCount(commentJsonObj.optInt("totalReplyCount"));
                    rethinkComment.setUserType(commentJsonObj.optString("userType"));
                    if (!commentJsonObj.isNull("childCommentList")) {
                        JSONArray repliesJsonArray = commentJsonObj.optJSONArray("childCommentList");
                        parseRepliesJsonArray(rethinkComment, repliesJsonArray);
                    }
                    rethinkComment.setUserIcon(UiMainUtils.getFullImagePath(commentJsonObj.optString("serverAddress"),commentJsonObj.optString("headPic")));
                    rethinkComment.setUserId(commentJsonObj.optString("baseUserId"));
                    rethinkComment.setUserRealName(commentJsonObj.optString("realName"));
                    rethinkComment.setMine(mUserInfo.getBaseUserId().equals(rethinkComment.getUserId()));
                    mCommentsAdapter.addComment(rethinkComment);
                }
                mCommentsAdapter.setHasMore(mTotal > mCommentsAdapter.getCommentCount());
                mCommentsAdapter.notifyDataSetChanged();
                mStart = mCommentsAdapter.getCommentCount();
            }

            @Override
            public void onRequestFailure(VolleyError error) {
                Cog.d(TAG, "getComments error", error);
                mIsLoadingMore = false;
                stopRefreshing();
                UIUtils.toast(NetTechVideoActivity.this, "获取评论失败，请检查网络。", Toast.LENGTH_SHORT);
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
        rethinkReply.setId(replyJsonObj.optString("meetCommentId"));
        rethinkReply.setCreateTime(replyJsonObj.optString("createTime"));
        rethinkReply.setReplyToName(replyJsonObj.optString("replyToRealName"));
        rethinkReply.setUserId(replyJsonObj.optString("userId"));
        rethinkReply.setUserRealName(replyJsonObj.optString("realName"));
        rethinkReply.setMine(mUserInfo.getBaseUserId().equals(rethinkReply.getUserId()));
        rethinkReply.setComment(comment);
        return rethinkReply;
    }

    public void onMoreReplyClick(MoreRelies data) {
        final RethinkComment rethinkComment = data.getRethinkComment();
        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("parentCommentId", rethinkComment.getId());
        int start = rethinkComment.getCurrentCount();
        params.put("start", start + "");
        params.put("end", start + 4 + "");
        requestData(URLConfig.GET_NET_TEACH_SECOND_COMMENT_LIST, params, false,new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                Cog.d(TAG, "onMoreReplyClick response=", response);
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

            @Override
            public void onRequestFailure(VolleyError error) {
                Cog.d(TAG, "onMoreReplyClick error=", error);
                UIUtils.toast(NetTechVideoActivity.this, "获取更多回复失败！", Toast.LENGTH_SHORT);
            }
        });
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
        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("meetingId", mPreparationId);
        params.put("comment", commentContent);

        mSendingDialogPresenter.show();
        if (mReplyingComment != null) {
            if (mReplyingComment instanceof RethinkReply) {
                RethinkReply reply = (RethinkReply) mReplyingComment;
                params.put("parentCommentId", reply.getParentId());
            } else {
                params.put("parentCommentId", mReplyingComment.getId());
            }
            params.put("replyToId", mReplyingComment.getUserId());
        }

        requestData(URLConfig.SEND_NET_TEACH_COMMENT, params,false, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                Cog.d(TAG, "publishComment response:", response);
                mDetector.hide();
                mSendingDialogPresenter.dismiss();
                if ("success".equals(response.optString("result"))) {
                    UIUtils.toast(NetTechVideoActivity.this, getString(R.string.publish_comment_successfully), Toast.LENGTH_SHORT);
                    clearReplyToAndClear();
                    notifyCommentsListToUpdate();
                } else {
                    UIUtils.toast(NetTechVideoActivity.this, response.optString("message"), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onRequestFailure(VolleyError error) {
                mSendingDialogPresenter.dismiss();
                Cog.d(TAG, "publishComment error:", error);
                UIUtils.toast(NetTechVideoActivity.this, "评论个人备课失败。请检查网络。", Toast.LENGTH_SHORT);
            }
        });
    }

    private void clearReplyToAndClear() {
        if (mReplyingComment != null) {
            mReplyingComment = null;//回复成功，清除被回复的评论
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
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * q
     * 播放视频
     * @param videoDetails
     */
    private void playVideo(VideoDetails videoDetails) {
        //1.判断是否已经下载 本视频
        if (FileDownloadService.hasMp4Downloaded(mUserInfo.getBaseUserId(), videoDetails.getMeetVideoId())) {
            String video = FileDownloadService.getCachedMp4File(mUserInfo.getBaseUserId(), videoDetails.getMeetVideoId());
            Cog.i("video:", video);
            mVideoControl.setVideoPath(video, BnVideoView2.BN_URL_TYPE_RTMP_HISTORY, true);
            return;
        }
        if (videoDetails == null) {
            Cog.i("video:videoDetails", "videoDetails");
            return;
        }
        //视频会议>>> rtmp点播
        //集体备课/互动听课>>> http点播
        mVideoControl.setVideoPath(videoDetails.getFilePath(), BnVideoView2.BN_URL_TYPE_HTTP, false);
    }

    private void stopPlayVideo() {
        Cog.d("----------------------------:", "stopPlayVideo()");
        mVideoControl.stop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams lparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            mFrameLayout.setLayoutParams(lparam);
        } else {
            int height = UIUtils.dip2px(this, 220);
            LinearLayout.LayoutParams lparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            mFrameLayout.setLayoutParams(lparam);
        }
    }

    public void onBackClick(View view) {
        finish();
        UIUtils.addExitTranAnim(this);
    }

    @Override
    protected void onPause() {
        mVideoControl.onPause();
        super.onPause();
    }

    @Override
    public void onComplete() {
        isVideoPlayEnd = true;
        mCurIndex = mCurIndex + 1;
        if (mCurIndex <= videoDetailsList.size() - 1) {
            Cog.d("----------------------------", "----------播放下一集----------");
            Message message = new Message();
            message.what = 0;
            handler.sendMessage(message);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                Cog.d("----------------------------", "------1----刷新集数--mCurIndex--" + mCurIndex + "------");
                mListViewAdapter.setSelectIndex(mCurIndex);
                Cog.d("----------------------------", "-----2-----刷新集数--mCurIndex--" + mCurIndex + "------");
                mListViewAdapter.notifyDataSetChanged();
                Cog.d("----------------------------", "------3----刷新集数--notifyDataSetChanged--" + mCurIndex + "------");
                mTitleTv.setText(videoDetailsList.get(mCurIndex).getVideoName());
                isVideoPlayEnd = false;
                playVideo(videoDetailsList.get(mCurIndex));
                break;
        }
        return false;
    }

    @Override
    public FragmentManager obtainFragmentManager() {
        return getSupportFragmentManager();
    }

    @Override
    protected void onDestroy() {
        stopPlayVideo();
        super.onDestroy();
    }

    public static void start(Activity from, String id) {
        Intent intent = new Intent(from, NetTechVideoActivity.class);
        intent.putExtra(Constants.PREPARATIONID, id);
        from.startActivity(intent);
        UIUtils.addEnterAnim(from);
    }
}
