package com.codyy.erpsportal.onlineteach.controllers.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.widgets.blog.CommentButton;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.channels.BaseHttpFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.interact.FirstCommentViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.interact.ReplyCommentViewHolder;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.CommentUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.BlogComposeView;
import com.codyy.erpsportal.commons.widgets.EmojiconEditText;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.MyDialog;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleHorizonDivider;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleRecyclerView;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.erpsportal.groups.controllers.viewholders.CommentMoreViewHolder;
import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;
import com.codyy.erpsportal.commons.models.entities.comment.BaseComment;
import com.codyy.erpsportal.commons.models.entities.comment.BaseCommentParse;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import butterknife.Bind;

/**
 * 基础评论类【专递课堂-详情】
 * Created by poe on 16-7-4.
 */
public class CommentsFragment extends BaseHttpFragment implements BlogComposeView.OnComposeOperationDelegate{
    private final static String TAG = "CommentsFragment";
    private static final int ITEM_VIEW_HOLDER_TYPE_COMMENT = 0x011;//一级评论
    private static final int ITEM_VIEW_HOLDER_TYPE_COMMENT_MORE = 0x012;//一级评论MORE
    private static final int ITEM_VIEW_HOLDER_TYPE_COMMENT_REPLY = 0x013;//二级评论
    private static final int ITEM_VIEW_HOLDER_TYPE_COMMENT_REPLY_MORE = 0x014;//二级评论MORE

    @Bind(R.id.empty_view)EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)RefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    @Bind(R.id.compose)BlogComposeView mComposeView; //输入框

    private BaseRecyclerAdapter<BaseTitleItemBar , BaseRecyclerViewHolder> mAdapter ;
    private EmojiconEditText mInputEditText;
    private CommentButton mSendComment;
    private InputMethodManager mInputManager ;
    private LinkedList<BaseTitleItemBar> mData  = new LinkedList<>();
    private String mId;//对ing的id
    private BaseComment mTempBlog ; //被回复的对象.
    private boolean mIsSecondReply = false ;//是否回复某条评论 .
    private int mCommentCount = 0 ;//1级评论总数

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_base_comment_view;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_EVALUATION_COMMENT_LIST;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        data.put("uuid", mUserInfo.getUuid());
        data.put("evaluationId", mId);
        data.put("start", String.valueOf(mData.size()));
        data.put("end", String.valueOf(mData.size()+sPageCount));
        return data;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG ,response.toString());
        if(null == mRecyclerView ) return;
        if(isRefreshing) mData.clear();
        mRecyclerView.setEnabled(true);
        mRecyclerView.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mEmptyView.setLoading(false);
        //隐藏加载
        hideMore();
        //parse
        BaseCommentParse parse = new Gson().fromJson(response.toString() , BaseCommentParse.class);
        if(null != parse && "success".equals(parse.getResult())){
            int total = parse.getTotal();
            List<BaseComment> list =  parse.getCommentList();
            for(BaseComment ec : list){
                ec.setBaseViewHoldType(ITEM_VIEW_HOLDER_TYPE_COMMENT);
                mData.add(ec);
                //添加二级评论数据
                List<BaseComment> evaluates = ec.getReplyList();
                if(evaluates != null && evaluates.size() >0){
                    for(BaseComment cc :evaluates){
                        cc.setParentCommentId(ec.getCommentId());
                        cc.setBaseViewHoldType(ITEM_VIEW_HOLDER_TYPE_COMMENT_REPLY);
                        mData.add(cc);
                    }
                }

                //二级更多...
//                        if(evaluates.size()>=5){
                BaseComment second = new BaseComment();
                second.setBaseViewHoldType(ITEM_VIEW_HOLDER_TYPE_COMMENT_REPLY_MORE);
                second.setParentCommentId(ec.getCommentId());
                second.setCommentContent("更多");
                second.setCommentId(CommentUtils.getSecondMoreId(ec.getCommentId()));
                mData.add(second);
//                        }
            }
            //notify data set changed .
            if((mData.size()-1)<total){// show more text .
                BaseComment second = new BaseComment();
                second.setBaseViewHoldType(ITEM_VIEW_HOLDER_TYPE_COMMENT_MORE);
                second.setCommentContent("更多");
                second.setCommentId(CommentUtils.getSecondMoreId(mId));
                mData.add(second);
            }
            mAdapter.setData(mData);

        }else{
            //获取结果失败
            ToastUtil.showToast(parse.getMessage());
        }
    }

    @Override
    public void onFailure(Throwable error) {
        if(null == mRecyclerView ) return;
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        if(mData.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onViewLoadCompleted() {
        super.onViewLoadCompleted();
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                mEmptyView.setLoading(true);
                requestData(true);
            }
        });
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        mRecyclerView.addItemDecoration(new SimpleHorizonDivider(divider));
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(true);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mInputEditText  =   mComposeView.getEtText();
        mSendComment    =   mComposeView.getBtnSend() ;
        mComposeView.setOperationDelegate(this);
        mInputManager   = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //set adapter
        setAdapter();
        mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    private void setAdapter() {
        mAdapter    = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder>() {
            @Override
            public BaseRecyclerViewHolder createViewHolder(ViewGroup parent, int viewType) {
                BaseRecyclerViewHolder viewHolder = null ;
                switch (viewType){
                    case ITEM_VIEW_HOLDER_TYPE_COMMENT://一级评论
                        viewHolder = new FirstCommentViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_blog_comment,parent,false));
                        break;
                    case ITEM_VIEW_HOLDER_TYPE_COMMENT_MORE://一级更多
                        viewHolder  =   new CommentMoreViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(
                                parent.getContext(),R.layout.item_blog_detail_more));
                        break;
                    case ITEM_VIEW_HOLDER_TYPE_COMMENT_REPLY://二级评论
                        viewHolder = new ReplyCommentViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(
                                parent.getContext(), R.layout.item_blog_comment_child));
                        break;
                    case ITEM_VIEW_HOLDER_TYPE_COMMENT_REPLY_MORE://二级更多
                        viewHolder  =   new CommentMoreViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(
                                parent.getContext(),R.layout.item_blog_comment_reply_more));
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
                switch (mAdapter.getItemViewType(position)){
                    case ITEM_VIEW_HOLDER_TYPE_COMMENT://一级
                        if(v.getId() == R.id.sdv_pic){
                            //1.自己的信息跳转到首页-"我的"
                            if(bc.getBaseUserId().equals(mUserInfo.getBaseUserId())){
                                MainActivity.start(getActivity(), mUserInfo , 2);
                            }else{//2.访客
                                PublicUserActivity.start(getActivity(), bc.getBaseUserId());
                            }
                        }else{
                            clickItem(bc);
                        }
                        break;
                    case ITEM_VIEW_HOLDER_TYPE_COMMENT_REPLY://二级
                        clickItem(bc);
                        break;
                    case ITEM_VIEW_HOLDER_TYPE_COMMENT_MORE://一级更多
                        //load more ...
                        mData.remove(mData.size()-1);
                        showMore();
                        requestData(false);
                        break;
                    case ITEM_VIEW_HOLDER_TYPE_COMMENT_REPLY_MORE://二级更多
                        //load more ...
                        getReplyComment(bc.getParentCommentId());
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showMore(){
        mAdapter.setRefreshing(true);
        mAdapter.setHasMoreData(true);
        mAdapter.notifyDataSetChanged();
    }

    private void hideMore() {
        mAdapter.setRefreshing(false);
        mAdapter.setHasMoreData(false);
        mAdapter.notifyDataSetChanged();
    }

    private void clickItem(BaseComment blogComment) {
        if(mUserInfo.getBaseUserId().equals(blogComment.getBaseUserId())){
            //delete options .
            makeSureDeleteDialog(blogComment);
        }else if(View.VISIBLE == mComposeView.getVisibility()){
            mTempBlog   =   blogComment;
            mInputEditText.setText("");
            mInputEditText.setHint("回复"+blogComment.getRealName()+":");
            UiMainUtils.showKeyBoard(mInputManager);
            mInputEditText.requestFocus();
            mIsSecondReply = true ;
        }
    }

    /**
     * 删除自己发表的评论
     * @param blogComment
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
                //delete the item .
                myDialog.dismiss();
            }

            @Override
            public void dismiss() {

            }
        });
        dialog.show(getFragmentManager(),"delete");
    }

    /**
     * 发表评论
     * @param comment
     */
    private void sendComment(final String comment){
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if(null != mId) data.put("evaluationId",mId);
        data.put("commentContent",comment);
        requestData(URLConfig.SEND_COMMENT, data,false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                Cog.d(TAG , response.toString());
                if(null != response && "success".equals(response.optString("result"))){
                    String message = response.optString("message");
                    ToastUtil.showToast(message == null ? "评论成功！":message);
                    if(null == mRecyclerView ) return;
                    BaseComment blogComment = new BaseComment();
                    blogComment.setCommentId(response.optString("blogCommentId"));
                    blogComment.setId(mId);
                    blogComment.setCommentContent(comment);
                    blogComment.setBaseUserId(mUserInfo.getBaseUserId());
                    blogComment.setCreateTime(""+System.currentTimeMillis());
                    blogComment.setHeadPic(mUserInfo.getHeadPic());
                    blogComment.setRealName(mUserInfo.getRealName());
                    blogComment.setBaseViewHoldType(ITEM_VIEW_HOLDER_TYPE_COMMENT);
                    blogComment.setHeadPic(mUserInfo.getHeadPic());
                    mData.add(1,blogComment);
                    mAdapter.setData(mData);
                    //更新comment数
                    mCommentCount++;
//                    mAllCommentCountTv.setText("(" + mCommentCount + ")");
//                    mSendComment.setText(mCommentCount>99?"99+":(mCommentCount+""));
                    UiMainUtils.setDrawableLeft(mSendComment , R.drawable.ic_message_count ,mCommentCount>9999?"9999":(mCommentCount+""));
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {
                ToastUtil.showToast("评论失败！");
            }
        });
    }

    /**
     * 删除评论
     * @param blogCommentId 被删除的评论id
     */
    private void sendDeleteComment(final String blogCommentId ){
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if(null != mId)
            data.put("evaluationId",mId);
        data.put("blogCommentId",blogCommentId);

        requestData(URLConfig.DELETE_BLOG_COMMENT, data,false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                Cog.d(TAG , response.toString());
                if(null != response && ("success".equals(response.optString("result"))|| response.optBoolean("result"))){
                    ToastUtil.showToast("删除成功！");
                    if(null == mRecyclerView ) return;
                    //如果删除的是一级评论，则对应的二级评论也要干掉
                    if(ITEM_VIEW_HOLDER_TYPE_COMMENT == mData.get(CommentUtils.getPos(blogCommentId,mData)-1).getBaseViewHoldType()){
                        //迭代删除一级 及 一级以下的所有评论
                        CommentUtils.removeFirstLevel(blogCommentId,mData);
                        //更新comment数
                        mCommentCount--;
                        if(mCommentCount>=0){
//                            mAllCommentCountTv.setText("(" + mCommentCount + ")");
//                            mSendComment.setText(mCommentCount>99?"99+":(mCommentCount+""));
                            UiMainUtils.setDrawableLeft(mSendComment , R.drawable.ic_message_count ,mCommentCount>9999?"9999":(mCommentCount+""));
                        }
                    }else{//二级item 则直接删除
                        mData.remove(CommentUtils.getPos(blogCommentId,mData)-1);
                    }
                    mAdapter.notifyDataSetChanged();
                }else{
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
     * 获取二级回复评论
     */
    private void getReplyComment(final String commentId) {
        int start = CommentUtils.getReplyCount(commentId , mData);
        HashMap<String, String> data = new HashMap<>();
        data.put("uuid", mUserInfo.getUuid());
        if(null != commentId){
            data.put("commentId", commentId);
        }
        data.put("start", String.valueOf(start));
        data.put("end", String.valueOf(mData.size()+sPageCount-1));
        requestData(URLConfig.GET_EVALUATION_COMMENT_LIST, data, false,new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                Cog.d(TAG ,response.toString());
                BaseCommentParse parse = new Gson().fromJson(response.toString() , BaseCommentParse.class);
                if(null != parse && "success".equals(parse.getResult())){
                    int total = parse.getTotal();
                    LinkedList<BaseTitleItemBar> list =  new LinkedList<>();
                    // TODO: 16-7-1 计算二级评论的插入位置 .
                    for(int i = 0 ;i <parse.getCommentList().size();i++){
                        BaseComment bc = parse.getCommentList().get(i);
                        bc.setBaseViewHoldType(ITEM_VIEW_HOLDER_TYPE_COMMENT_REPLY);
                        list.add(bc);
                    }
                    int pos = CommentUtils.getSecondMorePos(commentId , mData);
                    mData.addAll(pos,list);
                    mAdapter.notifyDataSetChanged();
                }else{
                    //获取结果失败
                    ToastUtil.showToast(parse.getMessage());
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {

            }
        });
    }

    /**
     * 回复评论
     * @param comment
     * @param blogCommentId 被回复的评论id
     */
    private void sendReplyComment(final String blogCommentId , final String comment){
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if(null != mId)
            data.put("evaluationId",mId);
        data.put("parentCommentId",blogCommentId);
        data.put("commentContent",comment);

        requestData(URLConfig.POST_BLOG_COMMENT_REPLY, data, false,new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                Cog.d(TAG , response.toString());
                if(null != response && "success".equals(response.optString("result"))){
                    String message = response.optString("message");
                    ToastUtil.showToast(message == null ? "评论成功！":message);
                    if(null == mRecyclerView ) return;
                    BaseComment blogComment = new BaseComment();
                    blogComment.setCommentId(response.optString("blogCommentId"));
                    blogComment.setId(mId);
                    blogComment.setCommentContent(comment);
                    blogComment.setBaseUserId(mUserInfo.getBaseUserId());
                    blogComment.setCreateTime(""+System.currentTimeMillis());
                    blogComment.setHeadPic(mUserInfo.getHeadPic());
                    blogComment.setRealName(mUserInfo.getRealName());
                    blogComment.setBaseViewHoldType(ITEM_VIEW_HOLDER_TYPE_COMMENT_REPLY);
                    blogComment.setReplyName(mTempBlog.getRealName());
                    //在评论底部插入一条数据
                    int replyCount = CommentUtils.getReplyCount(blogCommentId , mData);
                    mData.add(CommentUtils.getPos(blogCommentId,mData)+replyCount,blogComment);
                    mAdapter.notifyItemInserted(CommentUtils.getPos(blogCommentId,mData));
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {
                ToastUtil.showToast("评论失败！");
            }
        });
    }

    void clickSend(){
        Cog.d(TAG , "show Comment !~ ");
        if(TextUtils.isEmpty(mInputEditText.getText().toString())){
            ToastUtil.showToast("请输入评论内容!");
        }else{
            if(mIsSecondReply){
                //发送二级评论
                if(null != mTempBlog){
                    sendReplyComment(mTempBlog.getCommentId(),mInputEditText.getText().toString());
                    mIsSecondReply = false ;//发送一次后恢复为正常回复框.
                    mInputEditText.setText("");
                    mInputEditText.setHint("我来说两句");
                }
            }else {
                //发送一级评论
                sendComment(mInputEditText.getText().toString());
                mInputEditText.setText("");
            }
            //隐藏键盘
            if(mComposeView.isKeyboardVisible()) {
                UiMainUtils.hideKeyBoard(mInputManager);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mComposeView.isKeyboardVisible()){
            UiMainUtils.hideKeyBoard(mInputManager);
        }
    }

    @Override
    public void onSendText(String text) {
        clickSend();
    }

    @Override
    public void onSoftWareKeyOpen() {

    }

    @Override
    public void onSoftWareKeyClose() {

    }

    @Override
    public void onEmojiPanOpen() {

    }

    @Override
    public void onEmojiPanClose() {

    }
}
