package com.codyy.erpsportal.homework.controllers.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.ScreenUtils;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.TaskReadByItemActivity;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.TaskReadDao;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.services.WeiBoMediaService;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.HtmlUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.WebViewUtils;
import com.codyy.erpsportal.exam.controllers.activities.media.adapters.MMBaseRecyclerViewAdapter;
import com.codyy.erpsportal.exam.utils.MediaCheck;
import com.codyy.erpsportal.homework.controllers.fragments.WorkItemDetailFragment;
import com.codyy.erpsportal.homework.controllers.fragments.WorkItemDetailReadFragment;
import com.codyy.erpsportal.homework.models.entities.ItemInfoClass;
import com.codyy.erpsportal.homework.models.entities.student.StudentAnswerListByItem;
import com.codyy.erpsportal.homework.utils.WorkUtils;
import com.codyy.erpsportal.homework.widgets.AudioBar;
import com.codyy.erpsportal.homework.widgets.PressBar;
import com.codyy.erpsportal.homework.widgets.SlidingFloatScrollView;
import com.codyy.erpsportal.perlcourseprep.models.entities.SubjectMaterialPicture;
import com.codyy.erpsportal.rethink.controllers.activities.SubjectMaterialPicturesActivity;
import com.codyy.url.URLConfig;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * 按习题批阅activity
 * Created by ldh on 2016/2/16.
 */
public class WorkReadByItemActivity extends TaskReadByItemActivity implements View.OnClickListener, PressBar.RecordListener {
    private static final String TAG = WorkReadByItemActivity.class.getSimpleName();
    public static final String EXTRA_WORK_ID = "EXTRA_WORK_ID";
    public static final String EXTRA_CLASS_ID = "EXTRA_CLASS_ID";

    private LinearLayout mStuListContainer;//学生列表布局
    private TextView mUnReadItemNumTv;//待批阅数量
    private LinearLayout mAnswerLinearLayout;//学生答案布局，用于动态添加webView
    private RatingBar mDifficultyRatingBar;//难易度
    private LinearLayout mAnalysisLinearLayout;//习题解析布局，用于动态添加webView
    private TextView mKnowlegetTv;//知识点
    private EditText mCommentEditText;//评论框
    private Button mCommitBtn;//提交
    private View mAndioAnswerView;//学生答案音频
    private View mVideoAnswerView;//学生答案视频
    private View mVideoAnalysisView;//习题解析视频

    private ListAdapter mStudentListAdapter = null;//学生列表adapter
    private int mUnReadStuNum = 0;//该题未批阅学生人数
    private int mUnReadItemNum = 0;//该套作业待批阅数
    private String mClassId;//班级id
    private List<StudentAnswerListByItem> mStuAnswerList;//某道题的学生答案集合
    private Map<String, String> mStuPicRequestParams;
    private MediaMetadataRetrieverTask mMediaRetrieverTask;//获取视频帧画面的Task

    /**
     * 用于上滑后显示悬停的学生列表
     */
    @Bind(R.id.rcl_floating_stu_list)
    RecyclerView mFloatingStuList;

    @Bind(R.id.ll_floating_stu_list)
    LinearLayout mFloatingStuListLayout;

    /**
     * 原学生列表区域的view
     */
    @Bind(R.id.ll_floating_view)
    LinearLayout mFloatingView;

    @Bind(R.id.view_division_line)
    View mDivisionLine;

    @Bind(R.id.relative_item_info)
    RelativeLayout mTopLayout;

    @Bind(R.id.toolbar_title)
    TextView mTitleTv;//标题

    /**
     * 是否处于悬停状态
     */
    private boolean isFloating;


    protected TextView mUnReadStuNumTv;//待批阅人数
    protected RecyclerView mStudentListRecyclerView;//学生头像列表

    /**
     * 语音按钮，点击弹出“按住说话”栏
     */
    @Bind(R.id.iv_audio_record)
    ImageView mAudioIv;

    /**
     * 是否显示“按住说话”栏
     */
    private boolean isCanRecord;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mWorkId = getIntent().getStringExtra(EXTRA_WORK_ID);
        mClassId = getIntent().getStringExtra(EXTRA_CLASS_ID);
        super.onCreate(savedInstanceState);
        initView();
        mCommitBtn.setOnClickListener(this);//添加提交按钮监听事件
        mCommentEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});//最多500个字符
        mCommentEditText.setOnClickListener(this);
        mStudentListAdapter = new ListAdapter(new ArrayList<StudentAnswerListByItem>(), this);
        mStudentListAdapter.setOnInViewClickListener(R.id.layout_user_icon, new MMBaseRecyclerViewAdapter.onInternalClickListener<StudentAnswerListByItem>() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, StudentAnswerListByItem values) {
                if (mStudentListAdapter.getPosition() != position) {
                    //焦点指向当前学生
                    mStudentListAdapter.setPosition(position);
                    //当前学生id
                    mCurrentStuId = mStuAnswerList.get(position).getStudentId();
                    //获取当前学生答案
                    loadStudentData(position);
                    //获取当前评论
                    addHistoryAudioInfo();
                }
            }

            @Override
            public void OnLongClickListener(View parentV, View v, Integer position, StudentAnswerListByItem values) {

            }
        });
        initFloatingRecyclerStuList();
        initRecyclerStuList();

        Intent intent = new Intent(this, WeiBoMediaService.class);
        this.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mStuAnswerInfoSfsv.setOnScrollListener(this);
    }

    /**
     * 初始化学生列表
     */
    private void initRecyclerStuList() {
        mStudentListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mStudentListAdapter.setHeaderInvisible(true);
        mStudentListRecyclerView.setAdapter(mStudentListAdapter);
        mStudentListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    /**
     * 初始化学生列表浮层
     */
    private void initFloatingRecyclerStuList() {
        mFloatingStuList.setItemAnimator(new DefaultItemAnimator());
        mStudentListAdapter.setHeaderInvisible(false);
        mFloatingStuList.setAdapter(mStudentListAdapter);
        mFloatingStuList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }


    /**
     * 设置自定义标题名
     *
     * @param stringId 标题名的字串资源id
     */
    protected void setCustomTitle(@StringRes int stringId) {
        mTitleTv.setText(stringId);
    }

    /**
     * 设置自定义标题名
     *
     * @param title 标题名
     */
    protected void setCustomTitle(CharSequence title) {
        mTitleTv.setText(title);
    }

    /**
     * 隐藏学生列表浮层
     */
    protected void hideFloatingStuList() {
        if (isFloating) {
            initRecyclerStuList();
            mFloatingStuListLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 显示学生列表浮层
     */
    private void showFloatingStuList() {
        if (!isFloating) {
            initFloatingRecyclerStuList();
            mFloatingStuListLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onScroll(int t) {
        if (t >= mItemInfoViewPager.getHeight() + mStuListContainer.getHeight()) {
            showFloatingStuList();
            isFloating = true;
        } else {
            hideFloatingStuList();
            isFloating = false;
        }
    }


    private void initView() {
        mStuListContainer = (LinearLayout) findViewById(R.id.container_stu_list);
        mStudentListRecyclerView = (RecyclerView) findViewById(R.id.recycler_stu_list);//学生头像列表
        mUnReadItemNumTv = (TextView) findViewById(R.id.tv_work_un_read_item_number);//待批阅数量
        mUnReadStuNumTv = (TextView) findViewById(R.id.tv_un_read_number);//待批阅人数
        mStuAnswerInfoSfsv = (SlidingFloatScrollView) findViewById(R.id.sfsv_stu_answer_info);
        mAnswerLinearLayout = (LinearLayout) findViewById(R.id.ll_answer_webview);//学生答案布局，用于动态添加webView
        mDifficultyRatingBar = (RatingBar) findViewById(R.id.rb_item_difficulty);//难易度
        mAnalysisLinearLayout = (LinearLayout) findViewById(R.id.ll_analysis_webview);//习题解析布局，用于动态添加webView
        mKnowlegetTv = (TextView) findViewById(R.id.tv_item_knowlege);//知识点
        mCommentEditText = (EditText) findViewById(R.id.et_comment);//评论框
        mCommitBtn = (Button) findViewById(R.id.btn_commit);//提交
        mAndioAnswerView = findViewById(R.id.layout_audio_answer);//学生答案音频
        mVideoAnswerView = findViewById(R.id.fl_video_view_answer);//学生答案视频
        mVideoAnalysisView = findViewById(R.id.fl_video_view_analysis);//习题解析视频
        mAudioIv.setOnClickListener(this);
    }

    private void loadStudentData(int position) {
        mAnswerLinearLayout.removeAllViews();
        //学生答案
        WebView mStuAnswerWebView = new WebView(this);
        mStuAnswerWebView.setBackgroundColor(Color.TRANSPARENT);
        WebViewUtils.setContentToWebView(mStuAnswerWebView, mItemInfoList.get(mCurrentItemIndex).getWorkItemType().equals(TaskFragment.TYPE_TEXT) ?
                mStuAnswerList.get(position).getTextAnswer() : mStuAnswerList.get(position).getStudentAnswer());
        mStuAnswerWebView.getSettings().setBlockNetworkImage(false);
        mAnswerLinearLayout.addView(mStuAnswerWebView, 0);
        if (mStuAnswerList.get(position).getResrouceUrl().contains(WorkItemDetailFragment.TYPE_VIDEO_STRING)) {
            mAndioAnswerView.setVisibility(View.GONE);
            mVideoAnswerView.setVisibility(View.VISIBLE);
            setVideo(R.id.sd_video_task_view_answer, R.id.iv_controller_answer, mStuAnswerList.get(position).getResrouceUrl());
        } else if (mStuAnswerList.get(position).getResrouceUrl().contains(WorkItemDetailFragment.TYPE_AUDIO_STRING)) {
            mAndioAnswerView.setVisibility(View.VISIBLE);
            mVideoAnswerView.setVisibility(View.GONE);
            setAudio(R.id.iv_audio_bar_answer, R.id.container_audio_answer, R.id.tv_audio_time_answer,
                    mStuAnswerList.get(position).getResrouceUrl());
        } else {
            mAndioAnswerView.setVisibility(View.GONE);
            mVideoAnswerView.setVisibility(View.GONE);
        }
    }

    private void setContentToWebView(WebView webView, String html) {
        List<String> mImageList = HtmlUtils.filterImages(html);
        if (mImageList != null && mImageList.size() > 0) {
            webView.addJavascriptInterface(new JsInteraction(mImageList), "control");
            webView.getSettings().setJavaScriptEnabled(true);
            WebViewUtils.setContentToWebView(webView, HtmlUtils.constructExecActionJs(html));
        } else {
            WebViewUtils.setContentToWebView(webView, html);
        }
    }

    public class JsInteraction {
        private List<String> imageList = new ArrayList<>();

        public JsInteraction(List<String> imageList) {
            this.imageList = imageList;
        }

        @JavascriptInterface
        public void showImage(int position, String url) {
            List<SubjectMaterialPicture> subjectMaterialPictures = new ArrayList<>();
            for (int i = 0; i < imageList.size(); i++) {
                SubjectMaterialPicture smp = new SubjectMaterialPicture();
                smp.setId("" + i);
                smp.setName("" + i);
                smp.setUrl(imageList.get(i));
                subjectMaterialPictures.add(smp);
            }
            SubjectMaterialPicturesActivity.start(WorkReadByItemActivity.this, subjectMaterialPictures, position);
        }
    }

    @Override
    protected void onViewBound() {
        super.onViewBound();
        setCustomTitle(getResources().getString(R.string.work_title_read_by_item));

        //当滑动题目时，更新页面中的所有相应信息。是否【答对】，多少题待批阅，多少人待批改，学生列表（未批），当前学生的答案等信息
        ViewPager.OnPageChangeListener mViewPagerChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mTaskReadDao.insert("", mCurrentStuId, mWorkId, mCurrentWorkItemId, "", mCommentEditText.getText().toString());
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentItemIndex = position;
                mCurrentWorkItemId = mItemInfoList.get(mCurrentItemIndex).getWorkItemId();
                //加载学生头像
                loadStuPic();
                reset();
                //重置评论框内容
                resetCommentEt(mCurrentWorkItemId);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mItemInfoViewPager.addOnPageChangeListener(mViewPagerChangeListener);
    }

    @Override
    protected void addFragments(JSONObject response) {
        Cog.d(TAG, response);
        if (response.optString("result").equals("success")) {
            mItemInfoList = ItemInfoClass.parseResponse(response);
            getUnReadSubjectiveItemList(mItemInfoList);//获取出还有学生未批阅的主观题
        }
    }

    private void resetCommentEt(String workItemId) {
        List<TaskReadDao.TaskItemReadInfo> readInfos = mTaskReadDao.query(mCurrentStuId, mWorkId);
        for (int i = 0; i < readInfos.size(); i++) {
            if (workItemId.equals(readInfos.get(i).getTaskItemId())) {
                mCommentEditText.setText(readInfos.get(i).getTaskItemReadComment());
                break;
            }
        }
    }

    /**
     * 获取出还有学生未批阅的主观题
     *
     * @param itemInfoList
     * @return
     */
    private int readSubjectiveItemCount = 0;//按题批阅中已批阅的题目个数
    private int subjectiveItemCount = 0;//按题批阅中

    private void getUnReadSubjectiveItemList(List<ItemInfoClass> itemInfoList) {
        final List<ItemInfoClass> newItemInfoList = new ArrayList<>();
        subjectiveItemCount = 0;
        readSubjectiveItemCount = 0;
        for (int i = 0; i < itemInfoList.size(); i++) {
            final ItemInfoClass itemInfoClass = itemInfoList.get(i);
            if (WorkUtils.isSubjective(itemInfoClass.getWorkItemType())) {//首先应该满足是主观题
                subjectiveItemCount++;
                setParams(itemInfoClass.getWorkItemId(), itemInfoClass.getWorkItemType());
                RequestSender requestSender = new RequestSender(this);
                requestSender.sendRequest(new RequestSender.RequestData(URLConfig.GET_HOMEWORK_STU_ANSWER_BY_ITEM, mStuPicRequestParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.optString("result").equals("success") && mToolbar != null) {
                            mStuAnswerList = selectUnReadStu(StudentAnswerListByItem.parseResponse(response));//获取该题的答题学生，并挑选出未批阅的学生
                            if (mStuAnswerList.size() > 0) {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(WorkItemDetailActivity.ARG_ITEM_INFO, itemInfoClass);
                                addFragment("", WorkItemDetailReadFragment.class, bundle);
                                newItemInfoList.add(itemInfoClass);
                                mItemInfoList = newItemInfoList;
                                mUnReadItemNum = mUnReadItemNum + 1;//开始时，未批阅数为题目总数
                                mCurrentWorkItemId = mItemInfoList.get(0).getWorkItemId();//获取第一题的workItemId
                                if (mUnReadItemNum == 1) {//加载第一题的学生列表 获取第一题的评价，若有的话
                                    loadStuPic();
                                    resetCommentEt(mCurrentWorkItemId);
                                }
                            } else {
                                judgeHaveSubjectiveCount();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(Throwable error) {
                        Log.e(TAG, "数据获取失败！");
                    }
                }));
            }
        }
        if (subjectiveItemCount == 0) {
            finish();
            ToastUtil.showToast(WorkReadByItemActivity.this, getString(R.string.work_read_by_item_no_read_count));
        }
    }

    private void judgeHaveSubjectiveCount() {
        readSubjectiveItemCount++;
        if (readSubjectiveItemCount == subjectiveItemCount) {//当已批阅的题目数==主观题数量时，退出界面
            finish();
            ToastUtil.showToast(WorkReadByItemActivity.this, getString(R.string.work_read_by_item_no_read_count));
        }
    }

    //加载学生头像及其答案
    private void loadStuPic() {
        setParams(mItemInfoList.get(mCurrentItemIndex).getWorkItemType());
        RequestSender requestSender = new RequestSender(this);
        requestSender.sendRequest(new RequestSender.RequestData(URLConfig.GET_HOMEWORK_STU_ANSWER_BY_ITEM, mStuPicRequestParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadData:", URLConfig.GET_HOMEWORK_STU_ANSWER_BY_ITEM, mStuPicRequestParams);
                if (response.optString("result").equals("success") && mToolbar != null) {
                    mStuAnswerList = selectUnReadStu(StudentAnswerListByItem.parseResponse(response));
                    if (mStuAnswerList.size() > 0) {
                        addStuInfo();
                        mStuListContainer.setVisibility(View.VISIBLE);
                        mStudentListAdapter.setPosition(0);
                        mCurrentStuId = mStuAnswerList.get(0).getStudentId();//先默认第一个
                        //加载题目答案信息
                        loadItemAnswerInfo();
                        addHistoryAudioInfo();
                    } else {
                        mStuListContainer.setVisibility(View.GONE);
                        mItemInfoViewPager.setCurrentItem(mCurrentItemIndex + 1);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Log.e(TAG, "数据获取失败！");
            }
        }));
    }

    /**
     * 按题批阅界面添加学生列表及其答案
     */
    private void addStuInfo() {
        mStudentListAdapter.setList(mStuAnswerList);
    }

    //加载题目相关信息（难易度，知识点，习题解析），以及学生答案，是否答对,**题待批改
    private void loadItemAnswerInfo() {
        mDifficultyRatingBar.setRating(TaskFragment.getRarRating(mItemInfoList.get(mCurrentItemIndex).getDifficulty()));
        mDifficultyRatingBar.setIsIndicator(true);
        mAnalysisLinearLayout.removeAllViews();
        WebView mAnalysisWebView = new WebView(this);
        mAnalysisWebView.setBackgroundColor(Color.TRANSPARENT); // 设置背景色
        mAnalysisWebView.getSettings().setBlockNetworkImage(false);
        WebViewUtils.setContentToWebView(mAnalysisWebView, mItemInfoList.get(mCurrentItemIndex).getItemAnalysis());
        mAnalysisLinearLayout.addView(mAnalysisWebView, 0);
        mKnowlegetTv.setText(mItemInfoList.get(mCurrentItemIndex).getKnowledgePoint());
        //添加习题解析音视频(后得知习题解析无音频，只可能存在视频)
        if (mItemInfoList.get(mCurrentItemIndex).getItemAnalysisResourceId().contains(WorkItemDetailFragment.TYPE_VIDEO_STRING)) {//添加视频
            mVideoAnalysisView.setVisibility(View.VISIBLE);
            setVideo(R.id.sd_video_task_view_analysis, R.id.iv_controller_analysis, mItemInfoList.get(mCurrentItemIndex).getItemAnalysisResourceId());
        } else {//无音视频
            mVideoAnalysisView.setVisibility(View.GONE);
        }
        if (mStuAnswerList != null) {
            loadStudentData(0);
        }
        //设置待批阅题目数量
        setUnReadItemNum();
        //设置待批阅人数数量
        for (int i = 0; i < mStuAnswerList.size(); i++) {
            if (!mStuAnswerList.get(i).getIsRead()) {
                mUnReadStuNum = mUnReadStuNum + 1;
            }
        }
        setUnReadStuNum();
    }

    //设置习题解析视频
    private void setVideo(int sdResId, int imgResId, final String playUrl) {
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) findViewById(sdResId);
        ImageView controller = (ImageView) findViewById(imgResId);
        mMediaRetrieverTask = new MediaMetadataRetrieverTask(simpleDraweeView, controller, ScreenUtils.getScreenWidth(this), UIUtils.dip2px(this, 230f));
        mMediaRetrieverTask.execute(playUrl);
        controller.setVisibility(View.VISIBLE);
        controller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlayingAudio();
                VideoViewActivity.startActivity(WorkReadByItemActivity.this, playUrl, WorkItemDetailFragment.FROM_NET, "");
            }
        });
    }

    private void stopPlayingAudio() {
        if (mMediaBinder != null) {
            mMediaBinder.stopTask();
        }
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
        }
    }

    @Override
    public void RecordEnd(String recordFilePath, int duration) {
        LinearLayout mAudioCommentLayout = (LinearLayout) findViewById(R.id.ll_audio_comment_list);
        mAudioCommentLayout.addView(new AudioBar(this));
    }

    /**
     * 获取视频任意一帧作为缩略图
     */
    class MediaMetadataRetrieverTask extends AsyncTask<String, Integer, Bitmap> {
        private SimpleDraweeView simpleDraweeView;
        private ImageView imageView;

        MediaMetadataRetrieverTask(SimpleDraweeView simpleDraweeView, ImageView imageView, int screenWidth, int height) {
            super();
            this.simpleDraweeView = simpleDraweeView;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            MediaCheck check = new MediaCheck();
            Bitmap bitmap = null;
            if (check.isUsable(params[0])) {
                Drawable drawable = getResources().getDrawable(R.drawable.video_can_play);
                BitmapDrawable bd = (BitmapDrawable) drawable;
                bitmap = bd.getBitmap();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (isCancelled()) return;
            if (bitmap == null) {
                Drawable drawable = getResources().getDrawable(R.drawable.video_not_play);
                BitmapDrawable bd = (BitmapDrawable) drawable;
                bitmap = bd.getBitmap();
                simpleDraweeView.setImageBitmap(bitmap);
                imageView.setVisibility(View.GONE);
            } else {
                simpleDraweeView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    //设置音频
    private AnimationDrawable mAnimationDrawable;//音频播放条动画
    private WeiBoMediaService.MediaBinder mMediaBinder;
    private WeiBoMediaService mWeiBoMediaService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaBinder = (WeiBoMediaService.MediaBinder) service;
            mWeiBoMediaService = ((WeiBoMediaService.MediaBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mWeiBoMediaService = null;
        }
    };

    /**
     * 按题批阅中的音频播放
     *
     * @param barResId      绿色播放条资源
     * @param autoItemResId 布局资源
     * @param timeResId     显示时间资源
     * @param playUrl       播放地址
     */
    private void setAudio(int barResId, int autoItemResId, final int timeResId, final String playUrl) {
        ImageView audioBar = (ImageView) findViewById(barResId);
        mAnimationDrawable = (AnimationDrawable) audioBar.getBackground();
        LinearLayout audioItem = (LinearLayout) findViewById(autoItemResId);
        final TextView timeTv = (TextView) findViewById(timeResId);
        audioItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaBinder != null) {
                    mMediaBinder.playTask(playUrl + "?phoneType=android", mAnimationDrawable, timeTv);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaBinder != null) {
            mMediaBinder.stop();
        }
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
        }
    }

    @Override
    protected void onDestroy() {
        this.unbindService(mServiceConnection);
        if (mMediaRetrieverTask != null) {
            mMediaRetrieverTask.cancel(true);
        }
        super.onDestroy();
    }

    //设置待批阅题目数量
    private void setUnReadItemNum() {
        SpannableStringBuilder unReadItemNumSpan = new SpannableStringBuilder(String.valueOf(mUnReadItemNum) + " " + getResources().getString(R.string.work_un_read_item_number));
        unReadItemNumSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 0, unReadItemNumSpan.toString().indexOf("题"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        unReadItemNumSpan.setSpan(new AbsoluteSizeSpan(12, true), 0, unReadItemNumSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mUnReadItemNumTv.setText(unReadItemNumSpan);
    }

    //设置待批阅人数数量
    private void setUnReadStuNum() {
        SpannableStringBuilder unReadStuNumSpan = new SpannableStringBuilder(String.valueOf(mUnReadStuNum) + " " + getResources().getString(R.string.work_un_read_number));
        unReadStuNumSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 0, unReadStuNumSpan.toString().indexOf("人"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        unReadStuNumSpan.setSpan(new AbsoluteSizeSpan(12, true), 0, unReadStuNumSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mUnReadStuNumTv.setText(unReadStuNumSpan);
    }

    //当滑动题目时，重置相关控件的信息
    private void reset() {
        //老师点评置空
        mCommentEditText.setText("");
        //重置该题未批阅人数
        mUnReadStuNum = 0;
    }

    /**
     * 根据题型的不同，添加请求参数params
     *
     * @param itemType
     */
    private void setParams(String itemType) {
        mStuPicRequestParams = new HashMap<>();
        mStuPicRequestParams.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
        mStuPicRequestParams.put("workId", mWorkId);
        mStuPicRequestParams.put("baseClassId", mClassId);
        if (itemType.equals(TaskFragment.TYPE_TEXT)) {
            mStuPicRequestParams.put("type", "text");
        } else {
            mStuPicRequestParams.put("questionId", mItemInfoList.get(mCurrentItemIndex).getWorkItemId());
        }
    }

    /**
     * 根据题型的不同，添加请求参数params
     *
     * @param itemType
     * @param itemId
     */
    private void setParams(String itemId, String itemType) {
        mStuPicRequestParams = new HashMap<>();
        mStuPicRequestParams.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
        mStuPicRequestParams.put("workId", mWorkId);
        mStuPicRequestParams.put("baseClassId", mClassId);
        if (itemType.equals(TaskFragment.TYPE_TEXT)) {
            mStuPicRequestParams.put("type", "text");
        } else {
            mStuPicRequestParams.put("questionId", itemId);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_comment://评论框
                //hideAudioBar();
                break;
            case R.id.iv_audio_record:
                //showOrHideAudioBar();
                break;
            case R.id.btn_commit://点击提交
                submitClick();
        }
    }

    private void submitClick() {
        RequestSender requestSender = new RequestSender(this);
        Map<String, String> params = new HashMap<>();
        params.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
        params.put("workId", mWorkId);
        params.put("studentId", mCurrentStuId);
        params.put("workItemId", mCurrentWorkItemId);
        params.put("readContent", mCommentEditText.getText().toString());
        params.put("isTextType", mItemInfoList.get(mCurrentItemIndex).getWorkItemType().equals(TaskFragment.TYPE_TEXT) ? "true" : "false");
        requestSender.sendRequest(new RequestSender.RequestData(URLConfig.SUBMIT_READ_INFO, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.optString("result").equals("success")) {
                    ToastUtil.showToast(WorkReadByItemActivity.this, getResources().getString(R.string.work_read_submit_success));
                    //切换学生头像至下一个
                    if (mStudentListAdapter.getPosition() + 1 != mStuAnswerList.size()) {//不是指向最后一个头像
                        mCurrentStuId = mStuAnswerList.get(mStudentListAdapter.getPosition() + 1).getStudentId();
                        mStuAnswerList.remove(mStudentListAdapter.getPosition());
                        mStudentListAdapter.remove(mStudentListAdapter.getPosition());
                        loadStudentData(mStudentListAdapter.getPosition());
                    } else {//焦点为最后一个头像，删除后获取第一个头像内容
                        mStuAnswerList.remove(mStudentListAdapter.getPosition());
                        mStudentListAdapter.remove(mStudentListAdapter.getPosition());
                        if (mStuAnswerList.size() > 0) {
                            mStudentListAdapter.setPosition(0);
                            mCurrentStuId = mStuAnswerList.get(0).getStudentId();
                            loadStudentData(0);
                        }
                    }
                    mUnReadStuNum = mUnReadStuNum - 1;//该题未批阅人数-1
                    mCommentEditText.setText("");//评论框清空
                    if (mUnReadStuNum == 0) {//表明该题已经批阅完成
                        mUnReadItemNum = mUnReadItemNum - 1;//未批题数-1
                        setUnReadItemNum();
                        //将学生列表隐藏
                        mStuListContainer.setVisibility(View.GONE);
                        mItemInfoViewPager.setCurrentItem(mCurrentItemIndex + 1);
                    }
                    setUnReadStuNum();
                    if (mUnReadItemNum == 0) {//表明所有题都批完
                        finish();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Log.e(TAG, "数据获取失败！");
            }
        }));
    }

/*    private void showOrHideAudioBar() {
        if (isCanRecord) {
            hideAudioBar();
        } else {
            showAudioBar();
        }
    }*/

    /*private void showAudioBar() {
        mAudioIv.setBackgroundResource(R.drawable.audio_record_select);
        mPressBar.setVisibility(View.VISIBLE);
        isCanRecord = true;
    }*/

    /*private void hideAudioBar() {
        mAudioIv.setBackgroundResource(R.drawable.audio_record_unselect);
        mPressBar.setVisibility(View.GONE);
        isCanRecord = false;
    }*/


    class ListAdapter extends MMBaseRecyclerViewAdapter<StudentAnswerListByItem> {

        private int mPosition = 0;
        private boolean isShowHeader = true;

        ListAdapter(List<StudentAnswerListByItem> list, Context context) {
            super(list, context);
        }

        public void setPosition(int position) {
            this.mPosition = position;
            notifyDataSetChanged();
        }

        public void setHeaderInvisible(boolean isShowHeader) {
            this.isShowHeader = isShowHeader;
            notifyDataSetChanged();
        }

        public int getPosition() {
            return mPosition;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            final View view = LayoutInflater.from(context).inflate(R.layout.item_student_list_work_read, parent, false);
            return new NormalRecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            if (mPosition == position) {
                ((NormalRecyclerViewHolder) holder).nameTv.setTextColor(getResources().getColor(R.color.main_color));
            } else {
                ((NormalRecyclerViewHolder) holder).nameTv.setTextColor(getResources().getColor(R.color.exam_normal_color));
            }
            ((NormalRecyclerViewHolder) holder).nameTv.setText(list.get(position).getStudentName());
            ((NormalRecyclerViewHolder) holder).picSd.setImageURI(Uri.parse(list.get(position).getHeadPicStr()));

        }

        public class NormalRecyclerViewHolder extends RecyclerView.ViewHolder {
            TextView nameTv;
            SimpleDraweeView picSd;

            public NormalRecyclerViewHolder(View view) {
                super(view);
                nameTv = (TextView) view.findViewById(R.id.tv_student_name);
                picSd = (SimpleDraweeView) view.findViewById(R.id.useritem_icon);
                if (!isShowHeader) {
                    picSd.setVisibility(View.GONE);
                } else {
                    picSd.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected String getUrl() {
        return URLConfig.GET_HOMEWORK_QUESTION;
    }

    @Override
    protected void addParams(Map<String, String> param) {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        addParam("workId", mWorkId);
    }

    private List<StudentAnswerListByItem> selectUnReadStu(List<StudentAnswerListByItem> studentAnswerListByItemList) {
        List<StudentAnswerListByItem> newList = new ArrayList<>();
        for (int i = 0; i < studentAnswerListByItemList.size(); i++) {
            StudentAnswerListByItem studentAnswerListByItem = studentAnswerListByItemList.get(i);
            if (!studentAnswerListByItem.getIsRead()) {
                newList.add(studentAnswerListByItem);
            }
        }
        return newList;
    }

    public static void startActivity(Context context, String workId, String classId) {
        Intent intent = new Intent(context, WorkReadByItemActivity.class);
        intent.putExtra(EXTRA_WORK_ID, workId);
        intent.putExtra(EXTRA_CLASS_ID, classId);
        context.startActivity(intent);
    }
}

