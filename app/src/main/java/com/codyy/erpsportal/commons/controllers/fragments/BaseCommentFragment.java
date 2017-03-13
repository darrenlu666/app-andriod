package com.codyy.erpsportal.commons.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ActivityThemeActivity;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.channels.BaseHttpFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;
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
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleRecyclerView;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.erpsportal.commons.widgets.blog.CommentButton;
import com.codyy.erpsportal.groups.controllers.fragments.SimpleRecyclerDelegate;
import com.codyy.erpsportal.groups.controllers.viewholders.CommentMoreViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.CommentViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.ReplyCommentViewHolder;
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
 * Created by poe on 2017/03/0                                                                             7.
 */
// TODO: 3/7/17 １．支持自定义URL
// TODO: 3/7/17 2. 实现{@link SimpleRecyclerDelegate}
// TODO: 3/7/17 3. api接口　与　params 请求拆分　＞＞　．　
public abstract class BaseCommentFragment<T extends BaseComment> extends BaseHttpFragment implements BlogComposeView.OnComposeOperationDelegate {
    public static final String TAG = "BaseCommentFragment";
    public final static int ITEM_TYPE_FIRST_LEVEL = 0x01;//一级评论
    public final static int ITEM_TYPE_FIRST_LEVEL_MORE = 0x02;//一级更多
    public final static int ITEM_TYPE_SECOND_LEVEL = 0x03;//二级评论回复
    public final static int ITEM_TYPE_SECOND_LEVEL_MORE = 0x04;//二级更多

    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)RefreshLayout mRefreshLayout;
    @Bind(R.id.tv_comment_count)TextView mAllCommentCountTv;//全部评论数

    private BaseRecyclerAdapter<T, BaseRecyclerViewHolder<T>> mAdapter;
    protected LinkedList<T> mData = new LinkedList<>();
    private InputMethodManager mInputManager;
    private boolean mIsSecondReply = false;//是否回复某条评论 .
    private BaseComment mTempBlog; //被回复的对象.
    private int mCommentCount = 0;//1级评论总数
    private CommentButton mSendComment;
    private EmojiconEditText mInputEditText;

    /** 获取评论接口　**/
    public abstract BlogComposeView getComposeView();
    /** 获取评论类的代理 **/
    public abstract BaseCommentDelegate<T> getBaseCommentDelegate();

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_custom_comments;
    }

    @Override
    public String obtainAPI() {
        if(null == getBaseCommentDelegate() )
            throw  new IllegalAccessError("the activity must impements {@link BaseCommentDelegate}");
        return getBaseCommentDelegate().getParentDelegate().obtainAPI();
    }

    @Override
    public HashMap<String, String> getParam() {
        if(null == getBaseCommentDelegate() )
            throw  new IllegalAccessError("the activity must impements {@link BaseCommentDelegate}");
        return getBaseCommentDelegate().getParentDelegate().getParams();
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG, response.toString());
        if(null == getBaseCommentDelegate() )
            throw  new IllegalAccessError("the activity must impements {@link BaseCommentDelegate}");
        if (null == mRecyclerView) return;
        if(isRefreshing) mData.clear();
        mRecyclerView.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        //parse data
        getBaseCommentDelegate().getParentDelegate().parseData(response, mData);
        //判断是否有更多放到接口实现中去处理.
        //1.3 评论数
        mAllCommentCountTv.setText("(" + mCommentCount + ")");
        mAdapter.setData(mData);
        hideMore();
    }

    @Override
    public void onFailure(VolleyError error) {
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
        mInputEditText = getComposeView().getEtText();
        mSendComment = getComposeView().getBtnSend();
        getComposeView().setOperationDelegate(this);
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
                if (getComposeView().isKeyboardVisible() || getComposeView().isEmojiVisible()) {
                    getComposeView().hideEmojiOptAndKeyboard();
                }
                return false;
            }
        });
        //get data .
        mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

   /* private void clickItem(BaseComment blogComment) {
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
    }*/

    /**
     * 删除自己发表的评论
     *
     * @param blogComment 自己发表的评论
     */
    public void makeSureDeleteDialog(final BaseComment blogComment) {
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
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder<T>>() {
            @Override
            public BaseRecyclerViewHolder createViewHolder(ViewGroup parent, int viewType) {
                BaseRecyclerViewHolder viewHolder = null;
                switch (viewType) {
                    case ITEM_TYPE_FIRST_LEVEL_MORE://加载更多...
                        viewHolder = new CommentMoreViewHolder(LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.item_blog_detail_more, parent, false));
                        break;
                    case ITEM_TYPE_FIRST_LEVEL:
//                        viewHolder = new CommentViewHolder(LayoutInflater.from(parent.getContext())
//                                .inflate(R.layout.item_blog_comment, parent, false),
//                                type == ActivityThemeActivity.EVALUATION_LESSON);//当前是评课议课时则隐藏回复
                        getBaseCommentDelegate().getParentDelegate().getViewHolder(parent);
                        break;
                    case ITEM_TYPE_SECOND_LEVEL:
                        /*viewHolder = new ReplyCommentViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),
                                R.layout.item_blog_comment_child));*/
                        viewHolder = getBaseCommentDelegate().getChildrenDelegate().getViewHolder(parent);
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

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<T>() {
            @Override
            public void onItemClicked(View v, int position, T data) {
                BaseComment bc = data;
                switch (mAdapter.getItemViewType(position)) {
                    case ITEM_TYPE_FIRST_LEVEL://一级
                        /*if (v.getId() == R.id.sdv_pic) {
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
                        }*/
                        getBaseCommentDelegate().getParentDelegate().OnItemClicked(v,position,data);
                        break;
                    case ITEM_TYPE_SECOND_LEVEL://二级
                        getBaseCommentDelegate().getChildrenDelegate().OnItemClicked(v,position,data);
//                        clickItem(bc);
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
//        int start = getReplyCount(blogCommentId, mData);
        HashMap<String, String> data = getBaseCommentDelegate().getChildrenDelegate().getParams();

        requestData(getBaseCommentDelegate().getChildrenDelegate().obtainAPI(), data,false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                Cog.d(TAG, response.toString());
                if (null == mRecyclerView) return;

                refreshSecondLevel(blogCommentId, response);

            }

            @Override
            public void onRequestFailure(VolleyError error) {

            }
        });
    }

    /**
     * 发表评论
     *
     * @param comment 评论
     */
    private void sendComment(final String comment) {
        requestData(getBaseCommentDelegate().sendCommentDelegate().obtainAPI(), getBaseCommentDelegate().sendCommentDelegate().getParams(), false,new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                /*if (null != response && "success".equals(response.optString("result"))) {
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

                    mData.add(0, (T)blogComment);
                    mAdapter.setData(mData);
                    //更新comment数
                    mCommentCount++;
                }*/
                getBaseCommentDelegate().sendCommentDelegate().parseData(response,mData);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRequestFailure(VolleyError error) {
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
        /*HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        data.put("meetingId", mid);
        data.put("parentCommentId", blogCommentId);
        data.put("replyToId", replyToId);
        data.put("comment", comment);*/
        String url =getBaseCommentDelegate().sendReplyDelegate().obtainAPI();//URLConfig.ADD_COMMENT_LECTURE;
        HashMap<String, String> data = getBaseCommentDelegate().sendReplyDelegate().getParams();
        requestData(url,data,false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                /*if (null != response && "success".equals(response.optString("result"))) {
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
                    int replyCount = getReplyCount(blogCommentId, mData);
                    mData.add(getPos(blogCommentId, mData) + replyCount,(T)blogComment);
                }*/
                getBaseCommentDelegate().sendReplyDelegate().parseData(response,mData);
                mAdapter.notifyItemInserted(getPos(blogCommentId, mData));
            }

            @Override
            public void onRequestFailure(VolleyError error) {
                ToastUtil.showToast("评论失败！");
            }
        });
    }

    /**
     * 回复评论
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
                    if (ITEM_TYPE_FIRST_LEVEL == mData.get(getPos(blogCommentId, mData) - 1).getBaseViewHoldType()) {
                        //迭代删除一级 及 一级以下的所有评论
                        removeFirstLevel(blogCommentId, mData);
                        //更新comment数
                        mCommentCount--;
                        if (mCommentCount >= 0) {
                            mAllCommentCountTv.setText("(" + mCommentCount + ")");
                        }
                    } else {//二级item 则直接删除
                        mData.remove(getPos(blogCommentId, mData) - 1);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    ToastUtil.showToast("删除失败！");
                }
            }

            @Override
            public void onRequestFailure(VolleyError error) {
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
            List<T> blogList = new ArrayList<>();
            int pos = getPos(blogCommentId, mData);
            for (BaseComment bc : parse.getCommentList()) {
                bc.setBaseViewHoldType(ITEM_TYPE_SECOND_LEVEL);
                blogList.add((T)bc);
            }
            mData.addAll(pos, blogList);
            mAdapter.notifyItemRangeInserted(pos, blogList.size());
        } else {
            int pos = getSecondMorePos(blogCommentId, mData);
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
            if (getComposeView().isKeyboardVisible()) {
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

    /** 获取一级评论数 **/
    public int getFirstCommentCount(LinkedList<T> data){
        int count = 0 ;
        for(int i = 0 ; i < data.size() ; i++){
            String parentId = null;
            String content = "";
            if(data.get(i) instanceof BaseComment){
                parentId = data.get(i).getParentCommentId();
                content = data.get(i).getCommentContent();
            }
            if(TextUtils.isEmpty(parentId)){
                if(!"更多".equals(content)){
                    count++;
                }
            }
        }
        return  count ;
    }

    /** 当前的评论的二级回复总数 **/
    public int getReplyCount(String commentId , List<T> data){
        int count = 0 ;
        for(int i = 0 ; i < data.size() ; i++){
            if(data.get(i) instanceof BaseComment){
                BaseComment ec = data.get(i);
                if(null != data.get(i)&&null != ec.getParentCommentId() &&ec.getParentCommentId().equals(commentId)){
                    if(!"更多".equals(ec.getCommentContent()) ){
                        count++;
                    }
                }
            }

        }
        return  count ;
    }

    public int getPos(String blogCommentId , List<T> dataList) {
        int pos = -1 ;
        for(int i = 0 ; i < dataList.size() ; i++){
            if(dataList.get(i) instanceof BaseComment){
                BaseComment bc = (BaseComment) dataList.get(i);
                //find the position to insert .
                if(bc.getCommentId().equals(blogCommentId)){
                    pos = i+1 ;//下一个位置开始添加
                    if("更多".equals(bc.getCommentContent()) && pos >0 ){
                        pos -- ;
                        break;
                    }
                }
            }
        }
        return pos;
    }

    /** 删除一级评论 **/
    public void removeFirstLevel(String blogCommentId ,List<T> dataList) {
        List<BaseComment> deleteList = new ArrayList<>();
        for(BaseTitleItemBar blog : dataList){
            if(blog instanceof BaseComment){
                BaseComment bc = (BaseComment) blog;
                if(bc.getCommentId().equals(blogCommentId) || (bc.getParentCommentId()!=null && bc.getParentCommentId().equals(blogCommentId))){
                    deleteList.add(bc);
                }
            }
        }

        if(deleteList.size()>0){
            dataList.removeAll(deleteList);
        }
    }

    /**
     * 根据评论的id 获取二级评论的更多的列表.
     * @param blogCommentId
     * @return
     */
    public int getSecondMorePos(String blogCommentId ,LinkedList<T> data) {
        int pos = -1 ;
        for(int i = 0 ; i < data.size() ; i++){
            String id = "";
            //find the position to insert .
            if(data.get(i) instanceof BaseComment){
                id =  ((BaseComment) data.get(i)).getCommentId();
            }
            if(id.equals(getSecondMoreId(blogCommentId))){
                pos = i ;
                break;
            }
        }
        return pos;
    }

    /**
     * 制造一级评论的更多 id
     * @return
     */
    public String getFirstMoreId(String blogId){
        return "m"+blogId ;
    }

    /**
     * 制造二级评论的更多 id
     * @param blogCommentId
     * @return
     */
    @NonNull
    public String getSecondMoreId(String blogCommentId) {
        return "m"+blogCommentId;
    }
}
