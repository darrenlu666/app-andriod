package com.codyy.erpsportal.classroom.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.fragment.ClassDetailFragment;
import com.codyy.erpsportal.classroom.fragment.ClassRoomCommentFragment;
import com.codyy.erpsportal.classroom.fragment.PeopleTreeFragment;
import com.codyy.erpsportal.classroom.models.BaseClassRoomDetail;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.classroom.models.ClassRoomDetail;
import com.codyy.erpsportal.classroom.models.RecordRoomDetail;
import com.codyy.erpsportal.classroom.utils.DMSUtils;
import com.codyy.erpsportal.commons.controllers.activities.ClassTourNewActivity;
import com.codyy.erpsportal.commons.controllers.activities.ClassTourPagerActivity;
import com.codyy.erpsportal.commons.interfaces.IFragmentMangerInterface;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.TourClassroom;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.AutoHideUtils;
import com.codyy.erpsportal.commons.utils.Check3GUtil;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DeviceUtils;
import com.codyy.erpsportal.commons.utils.ScreenBroadCastUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.WiFiBroadCastUtils;
import com.codyy.erpsportal.commons.widgets.BNVideoControlView;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout2;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;
import com.codyy.erpsportal.exam.controllers.activities.media.adapters.MMBaseRecyclerViewAdapter;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 专递课堂 -直播
 * Created by ldh on 2016/6/29.
 */
public class CustomLiveDetailActivity extends AppCompatActivity implements View.OnClickListener, PeopleTreeFragment.ISyncCount, IFragmentMangerInterface, ClassRoomCommentFragment.SoftInputOpenListener {
    private static final String TAG = CustomLiveDetailActivity.class.getSimpleName();
    private static final int MSG_UPDATE_WATCH_COUNT = 0x001;//update the count of people mount sea .
    private static final int MAX_COUNT = 10 * 10000;
    private static final int UPDATE_TIME_DELAY = 30 * 1000;//更新观看人数.
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    RelativeLayout mRlVideoList;
    TextView mTitleTv;
    private UserInfo mUserInfo;//当前用户信息
    /**
     * 往期录播布局
     */
    private FrameLayout mFlRecordVideo;
    /**
     * 标题栏
     */
    private RelativeLayout mVideoTitleLl;
    /**
     * 显示隐藏类
     */
    private AutoHideUtils mAutoHide;
    /**
     * 顶部包括返回按钮和学校名称的条
     */
    private LinearLayout mTopLine;
    /**
     * 实时直播播放器
     */
    private BnVideoLayout2 mVideoLayout;
    /**
     * 往期录播播放器的控制器
     */
    private BNVideoControlView mVideoControl;
    /**
     * 实时直播未开始的提示
     */
    private TextView mVideoFailureTv;
    /**
     * 横竖平按钮
     **/
    private ImageView mIvFullScreen;
    /**
     * 当前播放的视频片段index
     */
    private int mCurrentPlayIndex;
    /**
     * 视频列表适配器
     */
    private ListAdapter mVideoListAdapter;
    /**
     * 实时直播详情实体类
     */
    private ClassRoomDetail mClassRoomDetail;
    /**
     * 往期录播详情实体类
     */
    private RecordRoomDetail mRecordRoomDetail;
    /**
     * 是否是横屏
     */
    private boolean mIsExpanable;
    private String mScheduleDetailId;//课程id
    private String mFrom;//专递课堂/直录播课堂
    private String mStatus;//进行中 or 未开始
    private String mSubject;//传递的自定义学科字段
    private String mRequestUrl;
    private RequestSender mRequestSender;
    private int mPeopleCount = 0;
    private WiFiBroadCastUtils mWifiUtil;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_UPDATE_WATCH_COUNT:
                    try {
                        int count = (int) msg.obj;
                        Cog.i(TAG, "update success now count : " + count);
                        if (count <= 0) count = 1;
                        mPeopleCount = count;
                        View tabView = mTabLayout.getTabAt(2).getCustomView();
                        TextView tv = (TextView) tabView.findViewById(R.id.tv_tab_item);
                        if (count < MAX_COUNT) {
                            tv.setText(count + "人观看");
                        } else if (count % 10000 == 0) {
                            tv.setText(count / 10000 + "万人观看");
                        } else if (count % 10000 > 0) {
                            tv.setText(count / 10000 + "万+人观看");
                        }
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        Cog.e(TAG, e == null ? "cast error !" : e.getMessage());
                    }
                    break;
            }
        }
    };

    private ScreenBroadCastUtils mScreenBroadCastUtils;
    private void registerScreenReceiver() {
        mScreenBroadCastUtils = new ScreenBroadCastUtils(CustomLiveDetailActivity.this ,new ScreenBroadCastUtils.ScreenLockListener() {
            @Override
            public void onScreenOn() {
                Log.i(TAG,"onScreenOn()");
                startLivePlay();
            }

            @Override
            public void onScreenLock() {
                Log.i(TAG,"onScreenLock()");
                stopLivePlay();
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_detail);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mScheduleDetailId = getIntent().getExtras().getString(ClassRoomContants.EXTRA_SCHEDULE_DETAIL_ID);
        mFrom = getIntent().getExtras().getString(ClassRoomContants.FROM_WHERE_MODEL);
        mStatus = getIntent().getExtras().getString(ClassRoomContants.EXTRA_LIVE_STATUS);
        mSubject = getIntent().getExtras().getString(ClassRoomContants.EXTRA_LIVE_SUBJECT);
        mUserInfo = getIntent().getParcelableExtra(Constants.USER_INFO);
        if (null == mUserInfo) mUserInfo = UserInfoKeeper.obtainUserInfo();
        initViewStub();
        requestData();
        //禁止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 分别为实时直播和往期录播加载不同的布局
     */
    private void initViewStub() {
        ViewStub mViewStub;
        if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_LIVE) || mFrom.equals(ClassRoomContants.TYPE_LIVE_LIVE)) {
            mViewStub = (ViewStub) findViewById(R.id.vs_class);
        } else {
            mViewStub = (ViewStub) findViewById(R.id.vs_record);
        }
        mViewStub.inflate();
    }

    private Runnable mHeartJump = new Runnable() {
        @Override
        public void run() {
            HashMap<String, String> params = new HashMap<>();
            params.put("clsScheduleDetailId", mScheduleDetailId);
            params.put("uuid", mUserInfo.getUuid());
            mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.GET_CUSTOMER_LIVING_WATCH_COUNT, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    if ("success".equals(jsonObject.optString("result"))) {
                        int count = jsonObject.optInt("count");
                        //send message to update count number .
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_WATCH_COUNT, count));
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(Throwable volleyError) {
                    Cog.e(TAG, (volleyError == null || volleyError.getMessage() == null) ? "error" : volleyError.getMessage());
                }
            }));

            mHandler.postDelayed(mHeartJump, UPDATE_TIME_DELAY);
        }
    };

    /**
     * 分别为实时直播和往期录播加载不同的view
     * invoked after  {@link #requestData()}
     */
    private void initViews() {
        if (isFinishing()) {
            return;
        }
        mFlRecordVideo = (FrameLayout) findViewById(R.id.fl_record_video);
        if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_LIVE) || mFrom.equals(ClassRoomContants.TYPE_LIVE_LIVE)) {
            initLiveVideo();
            if ("INIT".equals(mStatus)) {
                mIvFullScreen.setVisibility(View.GONE);
                handleRequestFailure();
            } else {
                check3GWifi();
                registerWiFiListener();
                registerScreenReceiver();
                mAutoHide.showControl();
                mIvFullScreen.setVisibility(View.VISIBLE);
            }
        } else {
            initRecordVideo();
            initVideoIndexList();
            if (mRecordRoomDetail.getVideoInfoList().size() >= 1) {
                playHistoryVideo(mRecordRoomDetail.getVideoInfoList().get(0).getVideoUrl());
            }
            if (mRecordRoomDetail.getVideoInfoList().size() < 2) {//当无数据时，视频列表隐藏//当视频个数为1时，也需要隐藏
                hideVideoList();
            }
        }
    }

    /**
     * 初始化直播视频的相关东西
     */
    private void initLiveVideo() {
        if (isFinishing()) {
            return;
        }
        mVideoTitleLl = (RelativeLayout) findViewById(R.id.rl_video_title);
        mVideoLayout = (BnVideoLayout2) findViewById(R.id.bnVideoViewOfLiveVideoLayout);
        mVideoLayout.setVolume(100);
        mVideoLayout.getVideoView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (null != mAutoHide) mAutoHide.showControl();
                return true;
            }
        });
        mVideoLayout.setTextClickListener(new BnVideoLayout2.ITextClickListener() {
            @Override
            public void onClick(View v) {
                mAutoHide.showControl();
            }
        });
        mVideoLayout.setOnSurfaceChangeListener(new BnVideoView2.OnSurfaceChangeListener() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Cog.i(TAG, " surfaceCreated ~");
                mSurfaceDestroyed = false;
                check3GWifi();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Cog.i(TAG, " surfaceDestroyed ~");
                mSurfaceDestroyed = true;
                //如果已经在了，立刻清除
                mHandler.removeCallbacks(mPlayLiveRunnable);
                if (null != mVideoLayout)
                    mVideoLayout.stop();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder) {

            }
        });

        mIvFullScreen = (ImageView) findViewById(R.id.iv_full_screen);
        mIvFullScreen.setOnClickListener(this);
        mAutoHide = new AutoHideUtils(mIvFullScreen);
    }

    /**
     * 初始化视频相关组件
     */
    private void initRecordVideo() {
        BnVideoView2 mVideoView = (BnVideoView2) findViewById(R.id.video_view);
        mVideoControl = (BNVideoControlView) findViewById(R.id.videoControl);
        mVideoControl.bindVideoView(mVideoView, new IFragmentMangerInterface() {
            @Override
            public FragmentManager getNewFragmentManager() {
                return getSupportFragmentManager();
            }
        });
        mVideoControl.setDisplayListener(new BNVideoControlView.DisplayListener() {
            @Override
            public void show() {
                mTopLine.setVisibility(View.VISIBLE);
            }

            @Override
            public void hide() {
                mTopLine.setVisibility(View.GONE);
            }
        });
        mVideoControl.setOnCompleteListener(new BnVideoView2.OnBNCompleteListener() {
            @Override
            public void onComplete() {
                if (mRecordRoomDetail != null && mRecordRoomDetail.getVideoInfoList() != null) {
                    if (mCurrentPlayIndex < mRecordRoomDetail.getVideoInfoList().size() - 1) {
                        mCurrentPlayIndex = mCurrentPlayIndex + 1;
                        mVideoListAdapter.setPosition(mCurrentPlayIndex);
                        playHistoryVideo(mRecordRoomDetail.getVideoInfoList().get(mCurrentPlayIndex).getVideoUrl());
                    }
                }
            }
        });
    }

    /**
     * 初始化视频列表
     */
    private void initVideoIndexList() {
        TextView mVideoCountTv = (TextView) findViewById(R.id.tv_video_list_count);
        mVideoCountTv.setText(getString(R.string.class_video_list_count, mRecordRoomDetail.getVideoInfoList().size()));
        RecyclerView mVideoListIndexRcv = (RecyclerView) findViewById(R.id.recycler_view);
        mVideoListAdapter = new ListAdapter(mRecordRoomDetail.getVideoInfoList(), this);
        mVideoListAdapter.setOnInViewClickListener(R.id.btn_class_video_list, new MMBaseRecyclerViewAdapter.onInternalClickListener<RecordRoomDetail.VideoListInfo>() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, RecordRoomDetail.VideoListInfo values) {
                mCurrentPlayIndex = position;
                mVideoListAdapter.setPosition(position);
                playHistoryVideo(mRecordRoomDetail.getVideoInfoList().get(position).getVideoUrl());
            }

            @Override
            public void OnLongClickListener(View parentV, View v, Integer position, RecordRoomDetail.VideoListInfo values) {

            }
        });
        mVideoListIndexRcv.setAdapter(mVideoListAdapter);
        mVideoListIndexRcv.setLayoutManager(new LinearLayoutManager(CustomLiveDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void hideVideoList() {
        mRlVideoList = (RelativeLayout) findViewById(R.id.rl_videolist);
        if (mRlVideoList != null) {
            mRlVideoList.setVisibility(View.GONE);
        }
    }

    @Override
    public void open() {
        hideVideoList();
    }

    @Override
    public void close() {
        showVideoList();
    }

    private void showVideoList(){
        mRlVideoList = (RelativeLayout) findViewById(R.id.rl_videolist);
        if (mRlVideoList != null && mRecordRoomDetail.getVideoInfoList().size() > 1) {
            mRlVideoList.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 播放指定的视频资源
     *
     * @param url 点播视频的资源地址
     */
    private void playHistoryVideo(String url) {
        mVideoControl.setVideoPath(url, BnVideoView2.BN_URL_TYPE_HTTP, false);
    }

    /**
     *
     */
    private void requestData() {
        if (TextUtils.isEmpty(mFrom)) return;
        Map<String, String> mParams = new HashMap<>();
        mParams.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        mParams.put("scheduleDetailId", mScheduleDetailId);
        switch (mFrom) {
            case ClassRoomContants.TYPE_CUSTOM_LIVE://专递课堂－直播
                mRequestUrl = URLConfig.NEW_LIVE_ONLINE_DETAIL;
                break;
            case ClassRoomContants.TYPE_CUSTOM_RECORD://专递课堂－录播
                mRequestUrl = URLConfig.NEW_LIVE_RECORD_DETAIL;
                break;
            case ClassRoomContants.TYPE_LIVE_LIVE://直录播－直播
                mRequestUrl = URLConfig.NEW_RECORD_DETAIL＿LIVE;
                break;
            case ClassRoomContants.TYPE_LIVE_RECORD://直录播－录播
                mRequestUrl = URLConfig.NEW_RECORD_DETAIL_RECORD;
                break;
        }

        mTopLine = (LinearLayout) findViewById(R.id.ll_top_title);
        ImageView mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        mTitleTv = (TextView) findViewById(R.id.tv_school_name);
        mTitleTv.setVisibility(View.INVISIBLE);

        mRequestSender = new RequestSender(this);
        mRequestSender.sendRequest(new RequestSender.RequestData(mRequestUrl,
                mParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ("success".equals(response.optString("result"))) {
                    if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_LIVE) || mFrom.equals(ClassRoomContants.TYPE_LIVE_LIVE)) {
                        ClassRoomDetail.parseResult(response, new ClassRoomDetail.ISocketTestCallBack() {
                            @Override
                            public void onComplete(ClassRoomDetail data) {
                                mClassRoomDetail = data;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mClassRoomDetail.setSubject(mSubject);
                                        //  2017/12/18 添加教室名称(如有多间教室)v5.3.9
                                        StringBuffer mainStr = new StringBuffer(mClassRoomDetail.getSchoolName());
                                        if(mClassRoomDetail.isShowClassRoomName()
                                                &&!TextUtils.isEmpty(mClassRoomDetail.getRoomName())){
                                            if(!TextUtils.isEmpty(mainStr))mainStr.append("_");
                                            mainStr.append(mClassRoomDetail.getRoomName());
                                        }
                                        mTitleTv.setText(mainStr.toString());
                                        initViews();
                                        addViewPager();
                                    }
                                });
                            }
                        });

                    } else {
                        mRecordRoomDetail = RecordRoomDetail.parseResult(response);
                        mTitleTv.setText(mRecordRoomDetail.getSchoolName());
                        initViews();
                        addViewPager();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_LIVE) || mFrom.equals(ClassRoomContants.TYPE_LIVE_LIVE)) {
                    handleRequestFailure();
                }
            }
        }));
    }

    private void addViewPager() {
        if (isFinishing()) {
            return;
        }
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.main_green));
        mTabLayout.setSelectedTabIndicatorHeight((int) (getResources().getDimension(R.dimen.tab_layout_select_indicator_height)));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        List<Fragment> mFragmentList = new ArrayList<>();
        if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_LIVE) || mFrom.equals(ClassRoomContants.TYPE_LIVE_LIVE)) {
            mTabLayout.addTab(mTabLayout.newTab().setText("课程详情"));
            StringBuffer mainStr = new StringBuffer(mClassRoomDetail.getSchoolName());
            if(mClassRoomDetail.isShowClassRoomName()
                    &&!TextUtils.isEmpty(mClassRoomDetail.getRoomName())){
                if(!TextUtils.isEmpty(mainStr))mainStr.append("_");
                mainStr.append(mClassRoomDetail.getRoomName());
            }
            mFragmentList.add(ClassDetailFragment.newInstance(mFrom, mClassRoomDetail.getArea(), mClassRoomDetail.getClassPeriod(), mClassRoomDetail.getClassTime(),
                    mClassRoomDetail.getGrade(), mainStr.toString(), mClassRoomDetail.getTeacher(), mClassRoomDetail.getSubject(), getReceiveNameList()));
        } else {
            mTabLayout.addTab(mTabLayout.newTab().setText("课程详情"));
            mFragmentList.add(ClassDetailFragment.newInstance(mFrom, mRecordRoomDetail.getArea(), mRecordRoomDetail.getClassPeriod(), mRecordRoomDetail.getClassTime(),
                    mRecordRoomDetail.getGrade(), mRecordRoomDetail.getSchoolName(), mRecordRoomDetail.getTeacher(), mRecordRoomDetail.getSubject(), getReceiveNameList(),
                    mRecordRoomDetail.getTimeLength(), mRecordRoomDetail.getPlayCount()));
        }
        //添加最新评论fragment
        mTabLayout.addTab(mTabLayout.newTab().setText("最新评论"));
        ClassRoomCommentFragment classRoomCommentFragment = ClassRoomCommentFragment.newInstance(mUserInfo, mScheduleDetailId, mFrom);
        classRoomCommentFragment.setOnSoftInputOpenListener(this);
        mFragmentList.add(classRoomCommentFragment);
        //v5.3.3 共xxx人观看
        mTabLayout.addTab(mTabLayout.newTab().setText("0人观看"));
        mFragmentList.add(PeopleTreeFragment.newInstance(mUserInfo, mScheduleDetailId));

        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                mFragmentList, new String[]{getString(R.string.class_detail), getString(R.string.newest_comment)});
        mViewPager.setAdapter(mViewPagerAdapter);
        //tips : if you do not set this attr , default only one fragment will be obtained when go to background .
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        Cog.i(TAG, "init heart jump!");
        //start pooling .
        mHandler.removeCallbacks(mHeartJump);
        mHandler.post(mHeartJump);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                DeviceUtils.hideSoftKeyboard(mViewPager);
                if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_LIVE) || mFrom.equals(ClassRoomContants.TYPE_LIVE_LIVE)) {//直播：全屏状态下点击返回按钮时，返回竖屏
                    if (mIsExpanable) {
                        switchWindowOrientation();
                    } else {
                        finish();
                    }
                } else {//录播：全屏状态下点击返回按钮时，返回竖屏
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        int height = UIUtils.dip2px(this, 180f);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
                        mFlRecordVideo.setLayoutParams(params);
                        mTitleTv.setVisibility(View.INVISIBLE);
                    } else {
                        finish();
                    }
                }
                break;
            case R.id.iv_full_screen:
                if(UserInfo.USER_TYPE_PARENT.equals(mUserInfo.getUserType())){
                    //跳转到实时轮训页面
                    TourClassroom classroom = new TourClassroom();
                    classroom.setSchoolName(mClassRoomDetail.getSchoolName());
                    classroom.setType("main");
                    classroom.setId(mScheduleDetailId);
                    classroom.setVideoUrl(mClassRoomDetail.getMainUrl() + "/" + mClassRoomDetail.getStream());

                    ClassTourPagerActivity.start(this, classroom, mUserInfo, ClassTourNewActivity.TYPE_SPECIAL_DELIVERY_CLASSROOM);
                }else{
                    setScreenLandScape();
                }
                break;
        }
    }


    /**
     * 横竖屏切换
     */
    private void setScreenLandScape() {
        if (UserInfoKeeper.obtainUserInfo().getUserType().equals(UserInfo.USER_TYPE_PARENT)) {//当是家长时，跳转到四分屏界面
            LiveVideoListPlayNewActivity.start(this, mClassRoomDetail, UserInfoKeeper.obtainUserInfo());
        } else {
            switchWindowOrientation();
        }
    }

    /**
     * 屏幕方向切换
     */
    private void switchWindowOrientation() {
        Activity activity = CustomLiveDetailActivity.this;
        if (!mIsExpanable) {
            UIUtils.setLandscape(activity);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            mVideoLayout.setLayoutParams(params);
            mVideoTitleLl.setLayoutParams(params);
            mIsExpanable = !mIsExpanable;
            mTitleTv.setVisibility(View.VISIBLE);
        } else {
            UIUtils.setPortrait(activity);
            int height = UIUtils.dip2px(this, 180f);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height);
            mVideoLayout.setLayoutParams(params);
            mVideoTitleLl.setLayoutParams(params);
            mIsExpanable = !mIsExpanable;
            mTitleTv.setVisibility(View.INVISIBLE);
        }
    }

    public static void startActivity(Context context, UserInfo userInfo, String scheduleDetailId, String from, String subject) {
        Intent intent = new Intent(context, CustomLiveDetailActivity.class);
        intent.putExtra(ClassRoomContants.EXTRA_SCHEDULE_DETAIL_ID, scheduleDetailId);
        intent.putExtra(ClassRoomContants.FROM_WHERE_MODEL, from);
        intent.putExtra(ClassRoomContants.EXTRA_LIVE_SUBJECT, subject);
        intent.putExtra(Constants.USER_INFO, userInfo);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, UserInfo userInfo, String scheduleDetailId, String from, String status, String subject) {
        Intent intent = new Intent(context, CustomLiveDetailActivity.class);
        intent.putExtra(ClassRoomContants.EXTRA_SCHEDULE_DETAIL_ID, scheduleDetailId);
        intent.putExtra(ClassRoomContants.FROM_WHERE_MODEL, from);
        intent.putExtra(ClassRoomContants.EXTRA_LIVE_STATUS, status);
        intent.putExtra(ClassRoomContants.EXTRA_LIVE_SUBJECT, subject);
        intent.putExtra(Constants.USER_INFO, userInfo);
        context.startActivity(intent);
    }


    /**
     * @return 获取辅课堂的直播地址集合
     */
    private ArrayList<String> getReceiveNameList() {
        ArrayList<String> nameList = new ArrayList<>();
        if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_LIVE) || mFrom.equals(ClassRoomContants.TYPE_LIVE_LIVE)) {
            for (int i = 0; i < mClassRoomDetail.getReceiveInfoList().size(); i++) {
                BaseClassRoomDetail.ReceiveInfoEntity entity = mClassRoomDetail.getReceiveInfoList().get(i);
                StringBuffer str = new StringBuffer(entity.getReceiveName());
                if(entity.isShowClassRoomName()
                        &&!TextUtils.isEmpty(entity.getRoomName())){
                    if(!TextUtils.isEmpty(str)) str.append("_");
                    str.append(entity.getRoomName());
                }
                nameList.add(str.toString());
            }
        } else {
            for (int i = 0; i < mRecordRoomDetail.getReceiveInfoList().size(); i++) {
                nameList.add(mRecordRoomDetail.getReceiveInfoList().get(i).getReceiveName());
            }
        }
        return nameList;
    }

    /**
     * 当请求数据返回error时，做如下处理
     */
    private void handleRequestFailure() {
        if (isFinishing()) {
            return;
        }
        if (mVideoFailureTv == null) {
            mVideoFailureTv = (TextView) findViewById(R.id.tv_un_start_tip);
            mVideoFailureTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // do nothing .
                    if (null != mAutoHide) mAutoHide.showControl();
                }
            });
        }
        mVideoFailureTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void sync(int currentCount) {
        Cog.i(TAG, "sync heart jump!");
        if (mPeopleCount < currentCount) {
            mHandler.removeCallbacks(mHeartJump);
            mHandler.post(mHeartJump);
        }
    }

    @Override
    public FragmentManager getNewFragmentManager() {
        return getSupportFragmentManager();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;
        private String[] titles;

        ViewPagerAdapter(FragmentManager fm, List<Fragment> list, String[] titles) {
            super(fm);
            this.fragmentList = list;
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position < 0 || position >= titles.length) return "";
            return titles[position];
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    class ListAdapter extends MMBaseRecyclerViewAdapter<RecordRoomDetail.VideoListInfo> {

        private int mPosition = 0;

        ListAdapter(List<RecordRoomDetail.VideoListInfo> list, Context context) {
            super(list, context);
        }

        public void setPosition(int position) {
            this.mPosition = position;
            notifyDataSetChanged();
        }

        public int getPosition() {
            return mPosition;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            final View view = LayoutInflater.from(context).inflate(R.layout.item_class_video_list, parent, false);
            return new NormalRecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            if (mPosition == position) {
                ((NormalRecyclerViewHolder) holder).indexBtn.setTextColor(getResources().getColor(R.color.main_color));
            } else {
                ((NormalRecyclerViewHolder) holder).indexBtn.setTextColor(getResources().getColor(R.color.exam_normal_color));
            }
            ((NormalRecyclerViewHolder) holder).indexBtn.setText(position + 1 + "");
        }

        class NormalRecyclerViewHolder extends RecyclerView.ViewHolder {
            Button indexBtn;

            NormalRecyclerViewHolder(View view) {
                super(view);
                indexBtn = (Button) view.findViewById(R.id.btn_class_video_list);
            }
        }
    }

    //检测是否为3G网络
    private void check3GWifi() {
        Check3GUtil.instance().CheckNetType(this, new Check3GUtil.OnWifiListener() {
            @Override
            public void onNetError() {
            }

            @Override
            public void onContinue() {
                mHandler.removeCallbacks(mPlayLiveRunnable);
                //如果视图销毁了 ， 停止播放 .
                if (!mSurfaceDestroyed) {
                    mHandler.post(mPlayLiveRunnable);
                }
            }
        });
    }

    /**
     * 是否继续播放标记位 ： default : false 可以继续播放直播  true: 视图已经销毁，阻止继续播放{@link #mPlayLiveRunnable}
     */
    private boolean mSurfaceDestroyed = false;
    /**
     * 开始播放视频
     * 因为网络检测的延迟此处播放应当在应用onDestroy时候被释放
     */
    private Runnable mPlayLiveRunnable = new Runnable() {
        @Override
        public void run() {
            //view 销毁后立刻退出
            if (mSurfaceDestroyed) return;
            if (isFinishing()) return;
            /*if (!mClassRoomDetail.getMainUrl().equals("") && !mVideoLayout.isPlaying()) {
                mVideoLayout.setUrl(mClassRoomDetail.getMainUrl() + "/" + mClassRoomDetail.getStream(), BnVideoView2.BN_URL_TYPE_RTMP_LIVE);
                mVideoLayout.play(BnVideoView2.BN_PLAY_DEFAULT);
            }*/
            startLivePlay();
        }
    };


    private void registerWiFiListener() {
        mWifiUtil = new WiFiBroadCastUtils(this, new WiFiBroadCastUtils.PlayStateListener() {
            @Override
            public void play() {
                startLivePlay();
            }

            @Override
            public void stop() {
                stopLivePlay();
            }
        });
    }

    private void stopLivePlay() {
        if (mVideoLayout.isPlaying()) {
            mVideoLayout.stop();
        }
    }

    private void startLivePlay() {
        if (!mVideoLayout.isPlaying()) {
            if (TextUtils.isEmpty(mVideoLayout.getVideoView().getUrl())) {
                mVideoLayout.setUrl(mClassRoomDetail.getMainUrl() + "/" + mClassRoomDetail.getStream(), BnVideoView2.BN_URL_TYPE_RTMP_LIVE);
            }
            mVideoLayout.play(BnVideoView2.BN_PLAY_DEFAULT);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(isFinishing()) return;
        if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_RECORD) || mFrom.equals(ClassRoomContants.TYPE_LIVE_RECORD)) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                int height = UIUtils.dip2px(this, 180f);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
                mFlRecordVideo.setLayoutParams(params);
                mTitleTv.setVisibility(View.INVISIBLE);
            } else {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                mFlRecordVideo.setLayoutParams(params);
                mTitleTv.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoControl != null)
            mVideoControl.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stop net request .
        /*DMSUtils.exitLiving(mRequestSender, mScheduleDetailId, mUserInfo.getUuid(), new DMSUtils.ILeave() {
            @Override
            public void onComplete() {
                if (null != mRequestSender) mRequestSender.stop();
            }
        });*/
        if (null != mRequestSender) mRequestSender.stop();
        if (null != mWifiUtil) mWifiUtil.destroy();
        if(null != mScreenBroadCastUtils) mScreenBroadCastUtils.destroy(CustomLiveDetailActivity.this );
        mHandler.removeCallbacks(mPlayLiveRunnable);
        mHandler.removeCallbacks(mHeartJump);
        mVideoLayout = null;
    }
}
