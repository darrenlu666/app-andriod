package com.codyy.erpsportal.classroom.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.classroom.fragment.ClassDetailFragment;
import com.codyy.erpsportal.classroom.fragment.ClassRoomCommentFragment;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.classroom.models.ClassRoomDetail;
import com.codyy.erpsportal.classroom.models.RecordRoomDetail;
import com.codyy.erpsportal.commons.utils.AutoHideUtils;
import com.codyy.erpsportal.commons.utils.Check3GUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.WiFiBroadCastUtils;
import com.codyy.erpsportal.commons.widgets.BNVideoControlView;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout2;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;
import com.codyy.erpsportal.exam.controllers.activities.media.adapters.MMBaseRecyclerViewAdapter;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 专递课堂 直录播课堂的 直播 录播详情界面Activity
 * Created by ldh on 2016/6/29.
 */
public class ClassRoomDetailActivity extends AppCompatActivity implements View.OnClickListener, ClassRoomCommentFragment.SoftInputOpenListener {
    private static final String TAG = ClassRoomDetailActivity.class.getSimpleName();

    @Bind(R.id.tv_class_detail)
    TextView mTvClassDetail;

    @Bind(R.id.tv_latest_comment)
    TextView mTvLatestComment;

    @Bind(R.id.view_fst_fragment_line)
    View mViewFstFragmentLine;

    @Bind(R.id.view_sec_fragment_line)
    View mViewSecFragmentLine;

    @Bind(R.id.vp_class_detail)
    ViewPager mViewPager;

    RelativeLayout mRlVideoList;
    TextView mTitleTv;

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
     * 当前播放的视频片段index
     */
    private int mCurrentPlayIndex;

    private LinearLayout mTabLine;

    private LinearLayout mTabLineBottom;

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
    private String mRequestUrl;

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
        initViewStub();
        handleData();
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

    /**
     * 分别为实时直播和往期录播加载不同的view
     */
    private void initViews() {
        mTabLine = (LinearLayout) findViewById(R.id.ll_title);
        mTabLineBottom = (LinearLayout) findViewById(R.id.ll_line);
        mFlRecordVideo = (FrameLayout) findViewById(R.id.fl_record_video);
        if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_LIVE) || mFrom.equals(ClassRoomContants.TYPE_LIVE_LIVE)) {
            initLiveVideo();
            if ("INIT".equals(mStatus)) {
                handleRequestFailure();
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
        mVideoTitleLl = (RelativeLayout) findViewById(R.id.rl_video_title);
        mAutoHide = new AutoHideUtils(mVideoTitleLl);
        mVideoLayout = (BnVideoLayout2) findViewById(R.id.bnVideoViewOfLiveVideoLayout);
        mVideoLayout.setVolume(100);
        mVideoLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mAutoHide.showControl();
                return false;
            }
        });
        mVideoLayout.setOnTipsTouchListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutoHide.showControl();
            }
        });
        mVideoLayout.setOnSurfaceChangeListener(new BnVideoView2.OnSurfaceChangeListener() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                check3GWifi();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (null != mVideoLayout)
                    mVideoLayout.stop();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder) {

            }
        });
        mAutoHide.showControl();
        check3GWifi();
        registerWiFiListener();
        ImageView mIvFullScreen = (ImageView) findViewById(R.id.iv_full_screen);
        mIvFullScreen.setOnClickListener(this);
    }

    /**
     * 初始化视频相关组件
     */
    private void initRecordVideo() {
        BnVideoView2 mVideoView = (BnVideoView2) findViewById(R.id.video_view);
        mVideoControl = (BNVideoControlView) findViewById(R.id.videoControl);
        mVideoControl.bindVideoView(mVideoView, getSupportFragmentManager());
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
        mVideoListIndexRcv.setLayoutManager(new LinearLayoutManager(ClassRoomDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void hideVideoList() {
        mRlVideoList = (RelativeLayout) findViewById(R.id.rl_videolist);
        if (mRlVideoList != null) {
            mRlVideoList.setVisibility(View.GONE);
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

    private void handleData() {
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

        RequestSender requestSender = new RequestSender(this);
        requestSender.sendRequest(new RequestSender.RequestData(mRequestUrl,
                mParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ("success".equals(response.optString("result"))) {
                    if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_LIVE) || mFrom.equals(ClassRoomContants.TYPE_LIVE_LIVE)) {
                        mClassRoomDetail = ClassRoomDetail.parseResult(response);
                        mTitleTv.setText(mClassRoomDetail.getSchoolName());
                    } else {
                        mRecordRoomDetail = RecordRoomDetail.parseResult(response);
                        mTitleTv.setText(mRecordRoomDetail.getSchoolName());
                    }
                    initViews();
                    addViewPager();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_LIVE) || mFrom.equals(ClassRoomContants.TYPE_LIVE_LIVE)) {
                    handleRequestFailure();
                }
            }
        }));
    }

    private void addViewPager() {
        List<Fragment> mFragmentList = new ArrayList<>();
        if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_LIVE) || mFrom.equals(ClassRoomContants.TYPE_LIVE_LIVE)) {
            mFragmentList.add(ClassDetailFragment.newInstance(mFrom, mClassRoomDetail.getArea(), mClassRoomDetail.getClassPeriod(), mClassRoomDetail.getClassTime(),
                    mClassRoomDetail.getGrade(), mClassRoomDetail.getSchoolName(), mClassRoomDetail.getTeacher(), mClassRoomDetail.getSubject(), getReceiveNameList()));
        } else {
            mFragmentList.add(ClassDetailFragment.newInstance(mFrom, mRecordRoomDetail.getArea(), mRecordRoomDetail.getClassPeriod(), mRecordRoomDetail.getClassTime(),
                    mRecordRoomDetail.getGrade(), mRecordRoomDetail.getSchoolName(), mRecordRoomDetail.getTeacher(), mRecordRoomDetail.getSubject(), getReceiveNameList(),
                    mRecordRoomDetail.getTimeLength(), mRecordRoomDetail.getPlayCount()));
        }

        //添加最新评论fragment
        ClassRoomCommentFragment classRoomCommentFragment;
        mFragmentList.add(classRoomCommentFragment = ClassRoomCommentFragment.newInstance(UserInfoKeeper.obtainUserInfo(), mScheduleDetailId, mFrom));
        classRoomCommentFragment.setOnSoftInputOpenListener(this);
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mTvClassDetail.setTextColor(getResources().getColor(R.color.green));
                        mViewFstFragmentLine.setBackgroundColor(0xff1bac22);
                        mTvLatestComment.setTextColor(getResources().getColor(R.color.personal_text_color));
                        mViewSecFragmentLine.setBackgroundDrawable(null);
                        break;
                    case 1:
                        mTvLatestComment.setTextColor(getResources().getColor(R.color.green));
                        mViewSecFragmentLine.setBackgroundColor(0xff1bac22);
                        mTvClassDetail.setTextColor(getResources().getColor(R.color.personal_text_color));
                        mViewFstFragmentLine.setBackgroundDrawable(null);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.tv_class_detail, R.id.tv_latest_comment})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_class_detail:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_latest_comment:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.iv_back:
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
                setScreenLandScape();
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
        Activity activity = ClassRoomDetailActivity.this;
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

    public static void startActivity(Context context, String scheduleDetailId, String from) {
        Intent intent = new Intent(context, ClassRoomDetailActivity.class);
        intent.putExtra(ClassRoomContants.EXTRA_SCHEDULE_DETAIL_ID, scheduleDetailId);
        intent.putExtra(ClassRoomContants.FROM_WHERE_MODEL, from);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String scheduleDetailId, String from, String status) {
        Intent intent = new Intent(context, ClassRoomDetailActivity.class);
        intent.putExtra(ClassRoomContants.EXTRA_SCHEDULE_DETAIL_ID, scheduleDetailId);
        intent.putExtra(ClassRoomContants.FROM_WHERE_MODEL, from);
        intent.putExtra(ClassRoomContants.EXTRA_LIVE_STATUS, status);
        context.startActivity(intent);
    }

    /**
     * @return 获取辅课堂的直播地址集合
     */
    private ArrayList<String> getReceiveNameList() {
        ArrayList<String> nameList = new ArrayList<>();
        if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_LIVE) || mFrom.equals(ClassRoomContants.TYPE_LIVE_LIVE)) {
            for (int i = 0; i < mClassRoomDetail.getReceiveInfoList().size(); i++) {
                nameList.add(mClassRoomDetail.getReceiveInfoList().get(i).getReceiveName());
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
        if (mVideoFailureTv == null)
            mVideoFailureTv = (TextView) findViewById(R.id.tv_un_start_tip);
        mVideoFailureTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void open() {
        mTabLine.setVisibility(View.GONE);
        mTabLineBottom.setVisibility(View.GONE);
    }

    @Override
    public void close() {
        mTabLine.setVisibility(View.VISIBLE);
        mTabLineBottom.setVisibility(View.VISIBLE);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        ViewPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.fragmentList = list;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
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
                startPlay();
            }
        });
    }

    private void startPlay() {
        if (!mClassRoomDetail.getMainUrl().equals("") && !mVideoLayout.isPlaying()) {
            mVideoLayout.setUrl(mClassRoomDetail.getMainUrl() + "/" + mClassRoomDetail.getStream(), BnVideoView2.BN_URL_TYPE_RTMP_LIVE);
            mVideoLayout.play(BnVideoView2.BN_PLAY_DEFAULT);
        }
    }

    private void registerWiFiListener() {
        WiFiBroadCastUtils wfb = new WiFiBroadCastUtils(this, getSupportFragmentManager(), new WiFiBroadCastUtils.PlayStateListener() {
            @Override
            public void play() {
                if (!mVideoLayout.isPlaying()) {
                    mVideoLayout.play(BnVideoView2.BN_PLAY_DEFAULT);
                }
            }

            @Override
            public void stop() {
                if (mVideoLayout.isPlaying()) {
                    mVideoLayout.stop();
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
    }
}
