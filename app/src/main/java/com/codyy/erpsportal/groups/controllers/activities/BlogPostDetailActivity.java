package com.codyy.erpsportal.groups.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringDef;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.erpsportal.commons.widgets.blog.CommentButton;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.rethink.controllers.activities.SubjectMaterialPicturesActivity;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.utils.CommentUtils;
import com.codyy.erpsportal.commons.utils.HtmlUtils;
import com.codyy.erpsportal.commons.widgets.BnMediaPlayLayout;
import com.codyy.erpsportal.groups.controllers.viewholders.BlogCommentChildViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.CommentMoreViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.BlogCommentViewHolder;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;
import com.codyy.erpsportal.perlcourseprep.models.entities.SubjectMaterialPicture;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.blog.BlogDetail;
import com.codyy.erpsportal.commons.models.entities.blog.BlogDetailParse;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.WebViewUtils;
import com.codyy.erpsportal.commons.widgets.BlogComposeView;
import com.codyy.erpsportal.commons.widgets.EmojiconEditText;
import com.codyy.erpsportal.commons.widgets.MyDialog;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleRecyclerView;
import com.codyy.erpsportal.commons.models.entities.comment.BaseComment;
import com.codyy.erpsportal.commons.models.entities.comment.BaseCommentParse;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import butterknife.Bind;

/**
 * 首页-博文-博文详情
 * Created by poe on 16-1-18.
 */
public class BlogPostDetailActivity extends BaseHttpActivity implements BlogComposeView.OnComposeOperationDelegate{
    private final static String TAG = "BlogPostDetailActivity";
    /** 来自门户*/
    public final static String FROM_TYPE_SHARE = "SHARE";
    /** 来自圈组*/
    public final static String FROM_TYPE_GROUP = "GROUP";
    /** 来自班级/个人*/
    public final static String FROM_TYPE_PERSON = "PERSON";

    public final static String EXTRA_BLOG_ID = "com.blog.id";//
    public final static String EXTRA_FROM_TYPE = "com.blog.from";//
    public final static String EXTRA_GROUP_ID = "com.group.id";//
    @StringDef(value = {FROM_TYPE_GROUP,FROM_TYPE_PERSON,FROM_TYPE_SHARE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FromOption{}

    public final static int ITEM_TYPE_FIRST_LEVEL = 0x01;//一级评论
    public final static int ITEM_TYPE_FIRST_LEVEL_MORE = 0x02;//一级更多
    public final static int ITEM_TYPE_SECOND_LEVEL = 0x03;//二级评论回复
    public final static int ITEM_TYPE_SECOND_LEVEL_MORE = 0x04;//二级更多
    /**     * 显示评论     */
    public final static int ACTION_STATE_SHOW_COMMENT = 0x10;
    /**     * 显示原文     */
    public final static int ACTION_STATE_SHOW_BLOG = 0x11;
    /**     * send msg .     */
    public final static int ACTION_STATE_SEND_MSG = 0x12;

    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.web_view)WebView mWebView ;
    @Bind(R.id.tv_title)TextView mBlogTitleTv;
    @Bind(R.id.tv_creator)TextView mBlogCreatorTv;
    @Bind(R.id.tv_count)TextView mBlogReadCountTv;
    @Bind(R.id.tv_create_time)TextView mCreateTimeTv;
    @Bind(R.id.tv_comment_count)TextView mAllCommentCountTv;//全部评论数
    @Bind(R.id.rlt_layout_comment)RelativeLayout mCommentRlt;
    @Bind(R.id.simple_recycler_view)SimpleRecyclerView mRecyclerView;
    @Bind(R.id.compose) BlogComposeView mComposeView;
    @Bind(R.id.scroll_view)ScrollView mScrollView;
    @Bind(R.id.fl_content)FrameLayout mFrameLayout ;
    @Bind(R.id.tv_category)TextView mCategoryTextView;
    @Bind(R.id.bn_media_view)BnMediaPlayLayout mBnMediaPlayLayout;//视频播放组件
    @Bind(R.id.rb_star)RatingBar mRatingBar;//评分
    @Bind(R.id.tv_score)TextView mScoreTextView;
    @Bind(R.id.refresh_layout)    RefreshLayout mRefreshLayout;

    private String mBlogId ;
//    private Button mSendComment;
    private CommentButton mSendComment;
    private EmojiconEditText mInputEditText;
    private BaseRecyclerAdapter<BaseTitleItemBar , BaseRecyclerViewHolder<BaseComment>> mAdapter ;
    private LinkedList<BaseTitleItemBar> mData = new LinkedList<>();
    private InputMethodManager mInputManager ;
    private boolean mIsSecondReply = false ;//是否回复某条评论 .
    private BaseComment mTempBlog ; //被回复的对象.
    private int mCommentCount = 0 ;//1级评论总数
    private int mActionState = ACTION_STATE_SHOW_COMMENT ;//default : 显示原文 ...
    private List<String> mImageList = new ArrayList<>();//博文详情中的图片详情 !
    private String mFromType = FROM_TYPE_SHARE;//默认来自门户的详情 .
    private String mGroupId = "";

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_blog_post_detail;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_HOME_BLOG_POST_DETAIL;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if(null != mBlogId) data.put("blogId",mBlogId);
        data.put("categoryType", mFromType);
        if(mFromType.equals(FROM_TYPE_GROUP)){// from group
            data.put("groupId",mGroupId);
        }
        if(mFromType.equals(FROM_TYPE_SHARE)){// from door
            data.put("baseAreaId", UIUtils.filterNull(mUserInfo.getBaseAreaId()));
            data.put("schoolId", UIUtils.filterNull(mUserInfo.getSchoolId()));
        }
        return data;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG , response.toString());
        if(null == mAllCommentCountTv ) return;
        if(mRefreshLayout.isRefreshing())mRefreshLayout.setRefreshing(false);
//        if(isRefreshing) mData.clear();
        BlogDetail blogDetail = new Gson().fromJson(response.toString() , BlogDetail.class);
        mImageList = HtmlUtils.filterImages(blogDetail.getBlogContent());
        refreshUI(blogDetail);
        loadCommentMore();
    }

    private void refreshUI(BlogDetail blogDetail) {
        if(null != mAllCommentCountTv) {
            //1.详情
            if (null != blogDetail) {
                //1.1 title
                mBlogTitleTv.setText(Html.fromHtml(UIUtils.filterCharacter(blogDetail.getBlogTitle())));
                mBlogCreatorTv.setText(blogDetail.getCreatorName());
                mCategoryTextView.setText(TextUtils.isEmpty(blogDetail.getBlogCategory()) ? "未分类" : blogDetail.getBlogCategory());//分类
                if(blogDetail.getViewCount() != null){
                    mBlogReadCountTv.setText(String.valueOf(Integer.valueOf(blogDetail.getViewCount()) + 1));
                }
                //create time
                String time = DateUtil.getDateStr(Long.parseLong(blogDetail.getCreateTime()), "yyyy-MM-dd HH:mm");
                mCreateTimeTv.setText(time);
                //1.2 内容
                if (null != blogDetail.getBlogContent()) {
                    //加载内容...
                    if (mImageList != null && mImageList.size() > 0) {
                        WebViewUtils.setContentToWebView(mWebView, HtmlUtils.constructExecActionJs(blogDetail.getBlogContent()));
                    } else {
                        WebViewUtils.setContentToWebView(mWebView, HtmlUtils.constructWordBreakJs(blogDetail.getBlogContent()));
                    }
                }
                //平均分
                mScoreTextView.setText(blogDetail.getShowAverage()+"分");
                mRatingBar.setMax(10);
                mRatingBar.setRating(blogDetail.getRatingAverage()/2);
            }
        }
    }

    @Override
    public void onFailure(Throwable error) {
        if(null == mAllCommentCountTv ) return;
        ToastUtil.showToast(error.getMessage());
        LogUtils.log(error);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    public void init() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        UiMainUtils.setNavigationTintColor(this,R.color.main_green);
        mBlogId = getIntent().getStringExtra(EXTRA_BLOG_ID);
        mGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        mFromType = getIntent().getStringExtra(EXTRA_FROM_TYPE);
        mTitleTextView.setText(getString(R.string.blog_detail_title));
        initToolbar(mToolBar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mComposeView.isKeyboardVisible()){
                    UiMainUtils.hideKeyBoard(mInputManager);
                }
                finish();
                UIUtils.addExitTranAnim(BlogPostDetailActivity.this);
            }
        });
        //webView !
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JsInteraction(),"control");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mData.clear();
                loadCommentMore();
            }
        });

        mInputManager   = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputEditText  =   mComposeView.getEtText();
        mSendComment    =   mComposeView.getBtnSend() ;
        mComposeView.setOperationDelegate(this);

        setAdapter();
        UiMainUtils.setDrawableLeft(mSendComment , R.drawable.ic_message_count ,mCommentCount>9999?"9999":(mCommentCount+""));
        // 检测点击事件 ，隐藏输入框
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mComposeView.isKeyboardVisible() || mComposeView.isEmojiVisible()){
                    mComposeView.hideEmojiOptAndKeyboard();
                }
                return false;
            }
        });

        mCommentRlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mComposeView.isKeyboardVisible() || mComposeView.isEmojiVisible()){
                    mComposeView.hideEmojiOptAndKeyboard();
                }
            }
        });

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(mComposeView.isKeyboardVisible() || mComposeView.isEmojiVisible()){
                    mComposeView.hideEmojiOptAndKeyboard();
                }
                return false;
            }
        });
    }

    public class JsInteraction {
        @JavascriptInterface
        public void showImage(int position ,String url) {
            List<SubjectMaterialPicture> subjectMaterialPictures = new ArrayList<>();
            for(int i=0 ;i < mImageList.size() ; i++){
                SubjectMaterialPicture smp = new SubjectMaterialPicture();
                smp.setId(""+i);
                smp.setUrl(mImageList.get(i));
                subjectMaterialPictures.add(smp);
            }
            SubjectMaterialPicturesActivity.start(BlogPostDetailActivity.this,subjectMaterialPictures,position);
        }
    }

    private void clickItem(BaseComment blogComment,View view) {
        if(view.getId() == R.id.tv_reply){
            mTempBlog   =   blogComment;
            mInputEditText.setText("");
            mInputEditText.setHint("回复"+blogComment.getRealName()+":");
            UiMainUtils.showKeyBoard(mInputManager);
            mInputEditText.requestFocus();
            mIsSecondReply = true ;
        }else{

            if(mUserInfo.getBaseUserId().equals(blogComment.getBaseUserId())){
                //delete options .
                makeSureDeleteDialog(blogComment);
            }else{
                //隐藏键盘
                if(mComposeView.isKeyboardVisible() || mComposeView.isEmojiVisible()){
                    mComposeView.hideEmojiOptAndKeyboard();
                }
            }
        }
    }

    private void setAdapter() {
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder<BaseComment>>() {
            @Override
            public BaseRecyclerViewHolder createViewHolder(ViewGroup parent, int viewType) {
                BaseRecyclerViewHolder viewHolder = null;
                switch (viewType){
                    case ITEM_TYPE_FIRST_LEVEL:
                        viewHolder = new BlogCommentViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_blog_comment,parent ,false));
                        break;
                    case ITEM_TYPE_FIRST_LEVEL_MORE://加载更多...
                    viewHolder  =   new CommentMoreViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_blog_detail_more,parent,false));
                    break;
                    case ITEM_TYPE_SECOND_LEVEL:
                        viewHolder  =   new BlogCommentChildViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(
                                parent.getContext(),R.layout.item_blog_comment_child));
                        break;
                    case ITEM_TYPE_SECOND_LEVEL_MORE:
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
                    case ITEM_TYPE_FIRST_LEVEL://一级
                        if(v.getId() == R.id.sdv_pic){
                            if(!UserInfo.USER_TYPE_AREA_USER.equals(bc.getUserType()) && !UserInfo.USER_TYPE_SCHOOL_USER.equals(bc.getUserType())){
                                //1.自己的信息跳转到首页-"我的"
                                if(bc.getBaseUserId().equals(mUserInfo.getBaseUserId())){
                                    MainActivity.start(BlogPostDetailActivity.this, mUserInfo , 2);
                                }else{//2.访客
                                    PublicUserActivity.start(BlogPostDetailActivity.this, bc.getBaseUserId());
                                }
                            }else{
                                ToastUtil.showToast(EApplication.instance(),"无法查看管理员信息");
                            }
                        }else {
                            clickItem((BaseComment)data,v);
                        }
                        break;
                    case ITEM_TYPE_SECOND_LEVEL://二级
                        clickItem(bc,v);
                        break;
                    case ITEM_TYPE_FIRST_LEVEL_MORE://一级更多
                        //load more ...
                        mData.remove(mData.size()-1);
                        showMore();
                        loadCommentMore();
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

    private void showMore(){
        mAdapter.setHasMoreData(true);
        mAdapter.setRefreshing(true);
        mAdapter.notifyDataSetChanged();
    }

    private void hideMore() {
        mAdapter.setHasMoreData(false);
        mAdapter.setRefreshing(false);
        mAdapter.notifyDataSetChanged();
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
        dialog.show(getSupportFragmentManager(),"delete");
    }

    /**
     * 更多-一级评论
     */
    private void loadCommentMore() {
        int start = CommentUtils.getFirstCommentCount(mData);
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if(null != mBlogId) data.put("blogId",mBlogId);
        data.put("start",""+start);
        data.put("end",""+(start+sPageCount-1));

        requestData(URLConfig.GET_BLOG_COMMENT_LIST, data,false, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                Cog.d(TAG , response.toString());
                if(null == mRecyclerView ) return;
                if (null !=mRefreshLayout&&mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                hideMore();
                BlogDetailParse blogDetailParse = new Gson().fromJson(response.toString() , BlogDetailParse.class);
                if(null != blogDetailParse){
                    mCommentCount = blogDetailParse.getTotal();
                    mAllCommentCountTv.setText("(" + mCommentCount + ")");
                    UiMainUtils.setDrawableLeft(mSendComment, R.drawable.ic_message_count, mCommentCount > 9999 ? "9999" : (mCommentCount + ""));

                    List<BaseComment> commentList = blogDetailParse.getCommentList();
                    if(null != commentList && commentList.size() > 0 ){
                        for(BaseComment bc : commentList){
                            bc.setBaseViewHoldType(ITEM_TYPE_FIRST_LEVEL);
                            mData.add(bc);
                            //二级 ?
                            if(bc.getReplyList()!=null && bc.getReplyList().size() >0 ){
                                //有二级数据
                                for(BaseComment replyComment : bc.getReplyList()){
                                    replyComment.setBaseViewHoldType(ITEM_TYPE_SECOND_LEVEL);
                                    mData.add(replyComment);
                                }
                                //二级更多.不足4条则说明没有更多评论，否则尝试加载工多...
                                if(bc.getReplyList().size()>=5){
                                    BaseComment second = new BaseComment();
                                    second.setBaseViewHoldType(ITEM_TYPE_SECOND_LEVEL_MORE);
                                    second.setParentCommentId(bc.getCommentId());
                                    second.setCommentContent("更多");
                                    second.setCommentId(CommentUtils.getSecondMoreId(bc.getCommentId()));
                                    mData.add(second);
                                }
                            }
                        }

                        if(CommentUtils.getFirstCommentCount(mData) < mCommentCount){
                            BaseComment more = new BaseComment();
                            more.setBaseViewHoldType(ITEM_TYPE_FIRST_LEVEL_MORE);
                            more.setCommentContent("更多");
                            more.setCommentId(CommentUtils.getFirstMoreId(mBlogId));
                            mData.add(more);
                        }
                        mAdapter.setData(mData);
                    }
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * 更多-二级评论
     * @param blogCommentId 父类id
     */
    private void loadSecondCommentMore(final String blogCommentId){
        int start = CommentUtils.getReplyCount(blogCommentId , mData);
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if(null != blogCommentId){
            data.put("commentId",blogCommentId);
        }else{
            data.put("commentId","");
        }
        data.put("start",""+start);
        data.put("end",""+(start+sPageCount-1));

        requestData(URLConfig.GET_BLOG_SECOND_COMMENT_LIST, data, false,new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                Cog.d(TAG , response.toString());
                if(null == mRecyclerView ) return;
                refreshSecondLevel(blogCommentId , response);
            }

            @Override
            public void onRequestFailure(Throwable error) {

            }
        });
    }

    /**
     * 发表一级评论
     * @param comment
     */
    private void sendComment(final String comment){
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if(null != mBlogId) data.put("blogId",mBlogId);
        data.put("commentContent",comment);

        requestData(URLConfig.POST_BLOG_COMMENT, data, false,new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                if(null != response && "success".equals(response.optString("result"))){
                    Cog.d(TAG , response.toString());
                    String message = response.optString("message");
                    ToastUtil.showToast(message == null ? "评论成功！":message);
                    if(null == mRecyclerView ) return;
                    BaseComment blogComment = new BaseComment();
                    blogComment.setCommentId(response.optString("blogCommentId"));
                    blogComment.setId(mBlogId);
                    blogComment.setCommentContent(comment);
                    blogComment.setBaseUserId(mUserInfo.getBaseUserId());
                    blogComment.setCreateTime(""+System.currentTimeMillis());
                    blogComment.setHeadPic(mUserInfo.getHeadPic());
                    blogComment.setRealName(mUserInfo.getRealName());
                    blogComment.setBaseViewHoldType(ITEM_TYPE_FIRST_LEVEL);
                    blogComment.setHeadPic(mUserInfo.getHeadPic());

                    mData.add(0,blogComment);
                    mAdapter.setData(mData);
                    //更新comment数
                    mCommentCount++;
                    mAllCommentCountTv.setText("(" + mCommentCount + ")");
                    UiMainUtils.setDrawableLeft(mSendComment , R.drawable.ic_message_count ,mCommentCount>9999?"9999":(mCommentCount+""));
                    if(mCommentRlt.getVisibility() == View.GONE || mCommentRlt.getVisibility() == View.INVISIBLE){
                        showCommentView();
                    }
                }else{
                    ToastUtil.showToast("评论失败！");
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {
                ToastUtil.showToast("评论失败！");
            }
        });
    }

    /**
     * 发送
     * @param comment
     * @return
     */
    private String filterEndEmoji(String comment) {
        if(null != comment && comment.length()>=145){
            comment = comment.substring(0,comment.lastIndexOf("["));
        }
        return comment;
    }

    /**
     * 过滤最后一个
     * @param comment  comment
     * @param blogCommentId 被回复的评论id
     */
    private void sendReplyComment(final String blogCommentId , final String comment){
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if(null != mBlogId) data.put("blogId",mBlogId);
        data.put("parentCommentId",blogCommentId);
        data.put("commentContent",comment);

        requestData(URLConfig.POST_BLOG_COMMENT_REPLY, data,false, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                if(null != response && "success".equals(response.optString("result"))){
                    Cog.d(TAG , response.toString());
                    String message = response.optString("message");
                    ToastUtil.showToast(message == null ? "评论成功！":message);
                    if(null == mRecyclerView ) return;
                    String commentContent = response.optString("commentContent");
                    BaseComment blogComment = new BaseComment();
                    blogComment.setCommentId(response.optString("blogCommentId"));
                    blogComment.setParentCommentId(response.optString("parentCommentId"));
                    blogComment.setId(mBlogId);
                    blogComment.setCommentContent(commentContent);
                    blogComment.setBaseUserId(mUserInfo.getBaseUserId());
                    blogComment.setCreateTime(""+System.currentTimeMillis());
                    blogComment.setHeadPic(mUserInfo.getHeadPic());
                    blogComment.setRealName(response.optString("realName"));
                    blogComment.setBaseViewHoldType(ITEM_TYPE_SECOND_LEVEL);
                    blogComment.setReplyName(mTempBlog.getRealName());
                    //在评论底部插入一条数据
                    int replyCount = CommentUtils.getReplyCount(blogCommentId , mData);
                    mData.add(CommentUtils.getPos(blogCommentId,mData)+replyCount,blogComment);
                    mAdapter.notifyItemInserted(CommentUtils.getPos(blogCommentId,mData)+replyCount);
                }else{
                    ToastUtil.showToast("回复失败！");
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
        if(null != mBlogId) data.put("blogId",mBlogId);
        data.put("blogCommentId",blogCommentId);

        requestData(URLConfig.DELETE_BLOG_COMMENT, data,false, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                if(null != response && ("success".equals(response.optString("result"))|| response.optBoolean("result"))){
                    Cog.d(TAG , response.toString());
                    ToastUtil.showToast("删除成功！");
                    if(null == mRecyclerView ) return;
                    //如果删除的是一级评论，则对应的二级评论也要干掉
                    if(ITEM_TYPE_FIRST_LEVEL == mData.get(CommentUtils.getPos(blogCommentId,mData)-1).getBaseViewHoldType()){
                    //迭代删除一级 及 一级以下的所有评论
                        CommentUtils.removeFirstLevel(blogCommentId,mData);
                        //更新comment数
                        mCommentCount--;
                        if(mCommentCount>=0){
                            mAllCommentCountTv.setText("(" + mCommentCount + ")");
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
     *根据返回更新数据结构
     * @param blogCommentId
     * @param response
     */
    private void refreshSecondLevel(String blogCommentId, JSONObject response) {
        BaseCommentParse parse = new Gson().fromJson(response.toString() , BaseCommentParse.class);
        if(null != parse && parse.getCommentList() != null && parse.getCommentList().size()>0){
            List<BaseComment> blogList = new ArrayList<>();
            int morePos = CommentUtils.getSecondMorePos(blogCommentId , mData);
            for(BaseComment bc : parse.getCommentList()){
                bc.setBaseViewHoldType(ITEM_TYPE_SECOND_LEVEL);
                blogList.add(bc);
            }
            mData.addAll(morePos,blogList);
            mAdapter.notifyItemRangeInserted(morePos,blogList.size());
            //判断是否已经完成
            if(CommentUtils.getReplyCount(blogCommentId,mData)<=parse.getTotal()){
                mData.remove(CommentUtils.getSecondMorePos(blogCommentId , mData));
                mAdapter.notifyDataSetChanged();
            }

        }else{
            int pos = CommentUtils.getSecondMorePos(blogCommentId , mData);
            mData.remove(pos);
            mAdapter.notifyDataSetChanged();
        }

    }

    void clickSend(){
        Cog.d(TAG , "showComment !~ ");
        switch (mActionState){
            case ACTION_STATE_SHOW_COMMENT://显示评论
                showCommentView();
                break;
            case ACTION_STATE_SHOW_BLOG://显示原文
                hideCommentView();
                break;
            case ACTION_STATE_SEND_MSG://发送
                if(TextUtils.isEmpty(mInputEditText.getText().toString())){
                    ToastUtil.showToast("请输入评论内容!");
                }else{
                    if(mIsSecondReply){
                        //发送二级评论
                        if(null != mTempBlog){
                            sendReplyComment(mTempBlog.getCommentId(),filterEndEmoji(mInputEditText.getText().toString()));
                            mIsSecondReply = false ;//发送一次后恢复为正常回复框.
                            mInputEditText.setText("");
                            mInputEditText.setHint("我来说两句");
                        }
                    }else {
                        //发送一级评论
                        sendComment(filterEndEmoji(mInputEditText.getText().toString()));
                        mInputEditText.setText("");
                    }

                    //隐藏键盘
                    if(mComposeView.isKeyboardVisible()) {
                        UiMainUtils.hideKeyBoard(mInputManager);
                    }
                }
                break ;
        }
    }

    /**
     * 隐藏评论列表
     */
    private void hideCommentView() {
        if(mCommentRlt.getVisibility() == View.VISIBLE){
            mCommentRlt.setVisibility(View.INVISIBLE);
            mCommentRlt.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_to_right));
        }
        setSendState();
    }

    /**
     * 展示评论列表
     */
    private void showCommentView() {
        if(mCommentRlt.getVisibility() != View.VISIBLE){
            mCommentRlt.setVisibility(View.VISIBLE);
            mCommentRlt.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right));
        }
        setSendState();
    }

    /**
     * 圈组博文专用 .
     */
    public static void startFromGroup(Context context ,String blogId ,String groupId){
        Intent intent = new Intent(context , BlogPostDetailActivity.class);
        intent.putExtra(EXTRA_BLOG_ID , blogId);
        intent.putExtra(EXTRA_GROUP_ID , groupId);
        intent.putExtra(EXTRA_FROM_TYPE , FROM_TYPE_GROUP);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }
    /**
     * @param context context
     * @param blogId blogId
     * @param from 参考{@link FromOption}
     */
    public static void start(Context context ,String blogId , @FromOption String from){
        Intent intent = new Intent(context , BlogPostDetailActivity.class);
        intent.putExtra(EXTRA_BLOG_ID , blogId);
        intent.putExtra(EXTRA_FROM_TYPE , from);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }

    private void setSendState() {
        if(mComposeView.isKeyboardVisible()){
            mActionState = ACTION_STATE_SEND_MSG ;
            UiMainUtils.setBlogSendText(mSendComment , "发送");
        }else{
            if(mComposeView.isEmojiVisible()){
                //如果表情栏存在
                mActionState = ACTION_STATE_SEND_MSG ;
                UiMainUtils.setBlogSendText(mSendComment , "发送");
            }else{
                if(mCommentRlt.getVisibility() == View.VISIBLE){
                    mActionState = ACTION_STATE_SHOW_BLOG ;
                    UiMainUtils.setBlogSendText(mSendComment , "原文");
                }else{
                    mActionState = ACTION_STATE_SHOW_COMMENT ;
                    UiMainUtils.setDrawableLeft(mSendComment , R.drawable.ic_message_count ,mCommentCount>9999?"9999":(mCommentCount+""));
                }
            }
        }
    }

    @Override
    public void onSendText(String text) {
        clickSend();
    }

    @Override
    public void onSoftWareKeyOpen() {
        Cog.i(TAG," onSoftWareKeyOpen ()");
        mInputEditText.requestFocus();
        setSendState();
    }

    @Override
    public void onSoftWareKeyClose() {
        Cog.i(TAG," onSoftWareKeyClose ()");
        setSendState();
    }

    @Override
    public void onEmojiPanOpen() {
        Cog.i(TAG," onEmojiPanOpen ()");
        setSendState();
    }

    @Override
    public void onEmojiPanClose() {
        Cog.i(TAG," onEmojiPanClose ()");
        setSendState();
    }
}
