package com.codyy.erpsportal.commons.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ActivityThemeActivity;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.fragments.channels.BaseHttpFragment;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.comment.BaseComment;
import com.codyy.erpsportal.commons.models.entities.comment.BaseCommentParse;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.CommentUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.widgets.BlogComposeView;
import com.codyy.erpsportal.commons.widgets.EmojiconEditText;
import com.codyy.erpsportal.commons.widgets.MyDialog;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.erpsportal.commons.widgets.blog.CommentButton;
import com.codyy.erpsportal.groups.controllers.viewholders.CommentMoreViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.CommentViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.ReplyCommentViewHolder;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleRecyclerView;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;

/**
 * 评论页面
 * 单独的 首页-专递课堂-详情-评论
 * Created by poe on 2016/6/16.
 */
public class CustomCommentFragment extends BaseHttpFragment implements BlogComposeView.OnComposeOperationDelegate {
    public static final String TAG = "CustomCommentFragment";
    public final static int ITEM_TYPE_FIRST_LEVEL = 0x01;//一级评论
    public final static int ITEM_TYPE_FIRST_LEVEL_MORE = 0x02;//一级更多
    public final static int ITEM_TYPE_SECOND_LEVEL = 0x03;//二级评论回复
    public final static int ITEM_TYPE_SECOND_LEVEL_MORE = 0x04;//二级更多
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)RefreshLayout mRefreshLayout;
    @Bind(R.id.tv_comment_count)TextView mAllCommentCountTv;//全部评论数
    private BlogComposeView mComposeView;
    private BaseRecyclerAdapter<BaseTitleItemBar, BaseRecyclerViewHolder<BaseTitleItemBar>> mAdapter;
    private LinkedList<BaseTitleItemBar> mData = new LinkedList<>();
    private InputMethodManager mInputManager;
    private boolean mIsSecondReply = false;//是否回复某条评论 .
    private BaseComment mTempBlog; //被回复的对象.
    private int mCommentCount = 0;//1级评论总数
    private CommentButton mSendComment;
    private EmojiconEditText mInputEditText;
    private String mid;
    private int type;
    boolean isComment;
    private IBlogComposeView mBlogComposeViewInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mid = bundle.getString("mid");
        type = bundle.getInt("type");
        isComment = bundle.getBoolean("isComment", true);
        mBlogComposeViewInterface = (IBlogComposeView) getActivity();
        if (null != mBlogComposeViewInterface) {
            mComposeView = mBlogComposeViewInterface.getBlogComposeView();
        }
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_custom_comments;
    }

    @Override
    public String obtainAPI() {
        String url = "";
        switch (type) {
            case ActivityThemeActivity.PREPARE_LESSON://集体备课
            case ActivityThemeActivity.INTERACT_LESSON://互动听课
                url = URLConfig.GET_COMMENT_NETTEACH;
                break;
            case ActivityThemeActivity.EVALUATION_LESSON://评课议课
                url = URLConfig.GET_COMMENT_LSIT;
                mComposeView.setVisibility(View.GONE);
                break;
            case ActivityThemeActivity.DELIVERY_CLASS://专递课堂
                url = URLConfig.GET_SCHEDULE_COMMENTLIST;
                break;
            case ActivityThemeActivity.LIVE_APPOINTMENT://直录播课堂
                url = URLConfig.GET_LIVE_APPOINTMENT_COMMENT;
                break;
        }
        return url;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        int start = CommentUtils.getFirstCommentCount(mData);
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        switch (type) {
            case ActivityThemeActivity.PREPARE_LESSON:
                data.put("meetingId", mid);
                data.put("meetType", "PREPARE_LESSON");
                break;
            case ActivityThemeActivity.INTERACT_LESSON:
                data.put("meetingId", mid);
                data.put("meetType", "INTERACT_LESSON");
                break;
            case ActivityThemeActivity.EVALUATION_LESSON:
                data.put("evaluationId", mid);
                data.put("meetType", "EVALUATON");
                break;
            case ActivityThemeActivity.DELIVERY_CLASS:
                data.put("scheduleDetailId", mid);
                break;
            case ActivityThemeActivity.LIVE_APPOINTMENT:
                data.put("liveAppointmentId", mid);
                break;
        }
        data.put("start", "" + start);
        data.put("end", "" + (start != 0 ? (start + sPageCount - 1) : 3));
        return data;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG, response.toString());
        if (null == mRecyclerView) return;
        if(isRefreshing) mData.clear();
        mRecyclerView.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        if (null != response) {
            BaseCommentParse parse = new Gson().fromJson(response.toString(), BaseCommentParse.class);
            mCommentCount = parse.getTotal();
            if (null != parse && parse.getCommentList() != null) {
                List<BaseComment> commentList = parse.getCommentList();
                if(commentList.size()!= mCommentCount){
                    mCommentCount = commentList.size();
                }
                if (null != commentList && commentList.size() > 0) {

                    for (BaseComment bc : commentList) {
                        bc.setBaseViewHoldType(ITEM_TYPE_FIRST_LEVEL);
                        mData.add(bc);
                        //二级 ?
                        if (bc.getReplyList() != null && bc.getReplyList().size() > 0) {
                            //有二级数据
                            for (BaseComment replyComment : bc.getReplyList()) {
                                replyComment.setBaseViewHoldType(ITEM_TYPE_SECOND_LEVEL);
                                mData.add(replyComment);
                            }
                            //二级更多.不足4条则说明没有更多评论，否则尝试加载工多...
                            if (bc.getReplyList().size() >= 5) {
                                BaseComment second = new BaseComment();
                                second.setBaseViewHoldType(ITEM_TYPE_SECOND_LEVEL_MORE);
                                second.setParentCommentId(bc.getCommentId());
                                second.setCommentContent("更多");
                                second.setCommentId(CommentUtils.getSecondMoreId(bc.getCommentId()));
                                mData.add(second);
                            }
                        }
                    }

                    //1.3 评论数

                    mAllCommentCountTv.setText("(" + mCommentCount + ")");
                    if (commentList.size() < mCommentCount) {
                        BaseComment more = new BaseComment();
                        more.setBaseViewHoldType(ITEM_TYPE_FIRST_LEVEL_MORE);
                        more.setCommentContent("更多");
                        more.setCommentId(CommentUtils.getFirstMoreId(mid));
                        mData.add(more);
                    }
                    mAdapter.setData(mData);
                }
            }
        }
        hideMore();
    }

    @Override
    public void onFailure(Throwable error) {
        LogUtils.log(error);
        if (null == mRecyclerView) return;
        mRecyclerView.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        hideMore();
    }

    @Override
    public void onViewLoadCompleted() {
        super.onViewLoadCompleted();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mInputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputEditText = mComposeView.getEtText();
        mSendComment = mComposeView.getBtnSend();
        mComposeView.setOperationDelegate(this);
        setAdapter();
        UiMainUtils.setDrawableLeft(mSendComment, R.drawable.ic_message_count, mCommentCount > 9999 ? "9999" : (mCommentCount + ""));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新
                mRecyclerView.setRefreshing(true);
                requestData(true);
            }
        });
        // TODO: 16-4-6 检测点击事件 ，隐藏输入框
        mRefreshLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mComposeView.isKeyboardVisible() || mComposeView.isEmojiVisible()) {
                    mComposeView.hideEmojiOptAndKeyboard();
                }
                return false;
            }
        });
        //get data .
        mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    private void clickItem(BaseComment blogComment) {
        Cog.i(TAG, "clicked item ~");
        if (mUserInfo.getBaseUserId().equals(blogComment.getBaseUserId())) {
            //delete options .
            makeSureDeleteDialog(blogComment);
        } else {
            if (type == ActivityThemeActivity.EVALUATION_LESSON) {//评课议课不能回复
                return;
            }
            mTempBlog = blogComment;
            mInputEditText.setText("");
            mInputEditText.setHint("回复" + blogComment.getRealName() + ":");
            UiMainUtils.showKeyBoard(mInputManager);
            mInputEditText.requestFocus();
            mIsSecondReply = true;
        }
    }

    /**
     * 删除自己发表的评论
     *
     * @param blogComment 自己发表的评论
     */
    private void makeSureDeleteDialog(final BaseComment blogComment) {
        MyDialog dialog = MyDialog.newInstance("确定删除？", MyDialog.DIALOG_STYLE_TYPE_0, new MyDialog.OnclickListener() {
            @Override
            public void leftClick(MyDialog myDialog) {
                mInputEditText.setText("");
                myDialog.dismiss();
            }

            @Override
            public void rightClick(MyDialog myDialog) {
                sendDeleteComment(blogComment.getCommentId());
                myDialog.dismiss();
            }

            @Override
            public void dismiss() {

            }
        });
        dialog.show(getFragmentManager(), "delete");
    }

    private void setAdapter() {
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder<BaseTitleItemBar>>() {
            @Override
            public BaseRecyclerViewHolder createViewHolder(ViewGroup parent, int viewType) {
                BaseRecyclerViewHolder viewHolder = null;
                switch (viewType) {
                    case ITEM_TYPE_FIRST_LEVEL_MORE://加载更多...
                        viewHolder = new CommentMoreViewHolder(LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.item_blog_detail_more, parent, false));
                        break;
                    case ITEM_TYPE_FIRST_LEVEL:
                        viewHolder = new CommentViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_blog_comment, parent, false),
                                type == ActivityThemeActivity.EVALUATION_LESSON);//当前是评课议课时则隐藏回复
                        break;
                    case ITEM_TYPE_SECOND_LEVEL:
                        viewHolder = new ReplyCommentViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),
                                R.layout.item_blog_comment_child));
                        break;
                    case ITEM_TYPE_SECOND_LEVEL_MORE:
                        viewHolder = new CommentMoreViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(
                                parent.getContext(), R.layout.item_blog_comment_reply_more));
                        break;
                }
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
                return mData.get(position).getBaseViewHoldType();
            }
        });

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<BaseTitleItemBar>() {
            @Override
            public void onItemClicked(View v, int position, BaseTitleItemBar data) {
                BaseComment bc = (BaseComment) data;
                switch (mAdapter.getItemViewType(position)) {
                    case ITEM_TYPE_FIRST_LEVEL://一级
                        if (v.getId() == R.id.sdv_pic) {
                            if (!UserInfo.USER_TYPE_AREA_USER.equals(bc.getUserType()) && !UserInfo.USER_TYPE_SCHOOL_USER.equals(bc.getUserType())) {
                                //1.自己的信息跳转到首页-"我的"
                                if (bc.getBaseUserId().equals(mUserInfo.getBaseUserId())) {
                                    MainActivity.start(getActivity(), mUserInfo, 2);
                                } else {//2.访客
                                    PublicUserActivity.start(getActivity(), bc.getBaseUserId());
                                }
                            } else {
                                ToastUtil.showToast(EApplication.instance(), "无法查看管理员信息");
                            }
                        } else {
                            clickItem(bc);
                        }
                        break;
                    case ITEM_TYPE_SECOND_LEVEL://二级
                        clickItem(bc);
                        break;
                    case ITEM_TYPE_FIRST_LEVEL_MORE://一级更多
                        //load more ...
                        mData.remove(mData.size() - 1);
                        showMore();
                        requestData(false);
                        break;
                    case ITEM_TYPE_SECOND_LEVEL_MORE://二级更多
                        //load more ...
                        loadSecondCommentMore(bc.getParentCommentId());
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 显示更多加载的进度条
     **/
    private void showMore() {
        mAdapter.setHasMoreData(true);
        mAdapter.setRefreshing(true);
        mAdapter.notifyDataSetChanged();
    }

    private void hideMore() {
        mAdapter.setRefreshing(false);
        mAdapter.setHasMoreData(false);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 更多-二级评论
     *
     * @param blogCommentId 父类id
     */
    private void loadSecondCommentMore(final String blogCommentId) {
        int start = CommentUtils.getReplyCount(blogCommentId, mData);
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if (null != blogCommentId)
            data.put("commentId", blogCommentId);
        data.put("start", "" + start);
        data.put("end", "" + (start + sPageCount - 1));

        requestData(URLConfig.GET_BLOG_SECOND_COMMENT_LIST, data,false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                Cog.d(TAG, response.toString());
                if (null == mRecyclerView) return;
                if(isRefreshing) mData.clear();
                refreshSecondLevel(blogCommentId, response);
            }

            @Override
            public void onRequestFailure(Throwable error) {

            }
        });
    }

    /**
     * 发表评论
     *
     * @param comment 评论
     */
    private void sendComment(final String comment) {
        HashMap<String, String> data = new HashMap<>();
        data.put("uuid", mUserInfo.getUuid());
        String url = "";
        switch (type) {
            case ActivityThemeActivity.PREPARE_LESSON://集体备课
            case ActivityThemeActivity.INTERACT_LESSON://互动听课
                url = URLConfig.ADD_COMMENT_LECTURE;
                data.put("meetingId", mid);
                data.put("comment", comment);
                break;
            case ActivityThemeActivity.EVALUATION_LESSON://评课议课　
                url = URLConfig.ADD_COMMENT_EVA;
                data.put("evaluationId", mid);
                data.put("commentContent", comment);
                break;
            case ActivityThemeActivity.DELIVERY_CLASS://专递课堂
                url = URLConfig.ADD_SCHEDULE_COMMENT;
                data.put("scheduleDetailId", mid);
                data.put("commentContent", comment);
                break;
            case ActivityThemeActivity.LIVE_APPOINTMENT://名校网络课堂
                url = URLConfig.ADD_LIVE_APPOINTMENT_COMMENT;
                data.put("liveAppointmentId", mid);
                data.put("commentContent", comment);
                break;
        }

        requestData(url, data, false,new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                if (null != response && "success".equals(response.optString("result"))) {
                    Cog.d(TAG, response.toString());
                    String message = response.optString("message");
                    ToastUtil.showToast(TextUtils.isEmpty(message) ? "评论成功！" : message);
                    if (null == mRecyclerView) return;
                    BaseComment blogComment = new BaseComment();
                    blogComment.setCommentId(response.optString("meetCommentId"));
                    blogComment.setId(mid);
                    blogComment.setCommentContent(comment);
                    blogComment.setBaseUserId(mUserInfo.getBaseUserId());
                    blogComment.setCreateTime(response.optString("strCreatetime"));
                    blogComment.setHeadPic(mUserInfo.getHeadPic());
                    blogComment.setRealName(mUserInfo.getRealName());
                    blogComment.setBaseViewHoldType(ITEM_TYPE_FIRST_LEVEL);
                    blogComment.setHeadPic(mUserInfo.getHeadPic());

                    mData.add(0, blogComment);
                    mAdapter.setData(mData);
                    //更新comment数
                    mCommentCount++;
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {
                ToastUtil.showToast("评论失败！");
            }
        });
    }

    /**
     * 回复评论
     *
     * @param comment       评论
     * @param blogCommentId 被回复的评论id
     */
    private void sendReplyComment(final String blogCommentId, String replyToId, final String comment) {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        data.put("meetingId", mid);
        data.put("parentCommentId", blogCommentId);
        data.put("replyToId", replyToId);
        data.put("comment", comment);

        requestData(URLConfig.ADD_COMMENT_LECTURE, data,false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                if (null != response && "success".equals(response.optString("result"))) {
                    Cog.d(TAG, response.toString());
                    String message = response.optString("message");
                    ToastUtil.showToast(TextUtils.isEmpty(message) ? "评论成功！" : message);
                    if (null == mRecyclerView) return;
                    BaseComment blogComment = new BaseComment();
                    blogComment.setCommentId(response.optString("meetCommentId"));
                    blogComment.setId(mid);
                    blogComment.setCommentContent(comment);
                    blogComment.setBaseUserId(mUserInfo.getBaseUserId());
                    blogComment.setCreateTime(response.optString("strCreatetime"));
                    blogComment.setHeadPic(mUserInfo.getHeadPic());
                    blogComment.setRealName(mUserInfo.getRealName());
                    blogComment.setBaseViewHoldType(ITEM_TYPE_SECOND_LEVEL);
                    blogComment.setReplyName(mTempBlog.getRealName());
                    //在评论底部插入一条数据
                    int replyCount = CommentUtils.getReplyCount(blogCommentId, mData);
                    mData.add(CommentUtils.getPos(blogCommentId, mData) + replyCount, blogComment);
                    mAdapter.notifyItemInserted(CommentUtils.getPos(blogCommentId, mData));
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {
                ToastUtil.showToast("评论失败！");
            }
        });
    }

    /**
     * 回复评论
     *
     * @param blogCommentId 被回复的评论id
     */
    private void sendDeleteComment(final String blogCommentId) {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        data.put("commentId", blogCommentId);

        requestData(URLConfig.DELETE_COMMENT_LECTURE, data,false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                if (null != response && ("success".equals(response.optString("result")) || response.optBoolean("result"))) {
                    Cog.d(TAG, response.toString());
                    ToastUtil.showToast("删除成功！");
                    if (null == mRecyclerView) return;
                    //如果删除的是一级评论，则对应的二级评论也要干掉
                    if (ITEM_TYPE_FIRST_LEVEL == mData.get(CommentUtils.getPos(blogCommentId, mData) - 1).getBaseViewHoldType()) {
                        //迭代删除一级 及 一级以下的所有评论
                        CommentUtils.removeFirstLevel(blogCommentId, mData);
                        //更新comment数

                        mCommentCount--;
                        if (mCommentCount >= 0) {
                            mAllCommentCountTv.setText("(" + mCommentCount + ")");
                        }
                    } else {//二级item 则直接删除
                        mData.remove(CommentUtils.getPos(blogCommentId, mData) - 1);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    ToastUtil.showToast("删除失败！");
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {
                ToastUtil.showToast("删除失败！");
            }
        });
    }


    /**
     * 根据返回更新数据结构
     *
     * @param blogCommentId blog id .
     * @param response      response data .
     */
    private void refreshSecondLevel(String blogCommentId, JSONObject response) {
        BaseCommentParse parse = new Gson().fromJson(response.toString(), BaseCommentParse.class);
        if (null != parse && parse.getCommentList() != null && parse.getCommentList().size() > 0) {
            List<BaseComment> blogList = new ArrayList<>();
            int pos = CommentUtils.getPos(blogCommentId, mData);
            for (BaseComment bc : parse.getCommentList()) {
                bc.setBaseViewHoldType(ITEM_TYPE_SECOND_LEVEL);
                blogList.add(bc);
            }
            mData.addAll(pos, blogList);
            mAdapter.notifyItemRangeInserted(pos, blogList.size());
        } else {
            int pos = CommentUtils.getSecondMorePos(blogCommentId, mData);
            mData.remove(pos);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSendText(String text) {
        if (TextUtils.isEmpty(mInputEditText.getText().toString())) {
            ToastUtil.showToast("请输入评论内容!");
        } else {
            if (mIsSecondReply) {
                //发送二级评论
                if (null != mTempBlog) {
                    sendReplyComment(mTempBlog.getCommentId(), mTempBlog.getBaseUserId(), mInputEditText.getText().toString());
                    mIsSecondReply = false;//发送一次后恢复为正常回复框.
                    mInputEditText.setText("");
                    mInputEditText.setHint("我来说两句");
                }
            } else {
                //发送一级评论
                sendComment(mInputEditText.getText().toString());
                mInputEditText.setText("");
            }
            //隐藏键盘
            if (mComposeView.isKeyboardVisible()) {
                UiMainUtils.hideKeyBoard(mInputManager);
            }
        }
    }

    @Override
    public void onSoftWareKeyOpen() {
        mInputEditText.requestFocus();
        setSendState();
    }

    @Override
    public void onSoftWareKeyClose() {
        setSendState();
    }

    @Override
    public void onEmojiPanOpen() {
        setSendState();
    }

    @Override
    public void onEmojiPanClose() {
        setSendState();
    }

    private void setSendState() {
        UiMainUtils.setBlogSendText(mSendComment, "发送");
    }


    public interface IBlogComposeView {
        BlogComposeView getBlogComposeView();
    }
}
