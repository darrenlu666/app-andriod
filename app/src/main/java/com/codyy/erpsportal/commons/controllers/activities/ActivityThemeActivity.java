package com.codyy.erpsportal.commons.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.widgets.BlogComposeView;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.ChannelAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.HorizontalListViewAdapter2;
import com.codyy.erpsportal.commons.controllers.fragments.CustomCommentFragment;
import com.codyy.erpsportal.commons.controllers.fragments.DeliveryClassDetailFragment;
import com.codyy.erpsportal.onlinemeetings.controllers.fragments.VideoIntroductionFragment;
import com.codyy.erpsportal.commons.services.FileDownloadService;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.SystemUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.VideoDownloadUtils;
import com.codyy.erpsportal.commons.widgets.BNVideoControlView;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;
import com.codyy.erpsportal.commons.widgets.HorizontalListView;
import com.codyy.erpsportal.commons.widgets.SlidingTabLayout;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.DeliveryClassDetail;
import com.codyy.erpsportal.commons.models.entities.EvaluationScore;
import com.codyy.erpsportal.commons.models.entities.MeetDetail;
import com.codyy.erpsportal.commons.models.entities.ThemeVideo;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.resource.models.entities.ResourceDetails;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动主题Activity
 * 集体备课/互动听课/ 专递课堂课程回放
 * Created by yangxinwu on 2015/7/29.
 */
public class ActivityThemeActivity extends FragmentActivity implements CustomCommentFragment.IBlogComposeView{
    /**
     * 专递课堂
     */
    public static final int DELIVERY_CLASS = 0x0a1;
    /**
     * 集体备课
     */
    public static final int PREPARE_LESSON = 0x0a2;
    /***
     * 互动听课
     */
    public static final int INTERACT_LESSON = 0x0a3;
    /**
     * 评课议课
     */
    public static final int EVALUATION_LESSON = 0x0a4;
    /**
     * 名校网络课堂
     * LiveAppointmentDetail
     */
    public static final int LIVE_APPOINTMENT = 0x0a5;

    private final static String TAG = "ActivityThemeActivity";
    private TextView mTitleTv;
    private TextView mTvVideoCount;
    private HorizontalListView mListView;
    private HorizontalListViewAdapter2 mListViewAdapter;
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private ChannelAdapter mAdapter;
    private BnVideoView2 mVideoView;
    private FrameLayout mFrameLayout;
    private BNVideoControlView mVideoControl;
    private LinearLayout mVideoLayout;
    private RelativeLayout mRltTitle;
    private TextView mDownBtn;
    private BlogComposeView mComposeView;//表情输入框

    private UserInfo mUserInfo;
    private List<ThemeVideo> mVideoList = new ArrayList<>();
    private RequestSender mRequestSender;
    private Object mReqTag = new Object();
    private MeetDetail mMeetDetail;
    private int mType;
    private String mId;
    private EvaluationScore mScore;
    private ResourceDetails mResourceDetails;
    private String mResourceName;
    private int mVideoNumber = 0;
    /**
     * 点击次数
     */
    private int mViewCount;
    private View.OnClickListener mDownListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mResourceDetails != null && mResourceDetails.getId() != null) {
                mResourceDetails.setResourceName(mResourceName + (mVideoNumber + 1));
                VideoDownloadUtils.downloadVideo(mResourceDetails, mResourceDetails.getAttachPath(), mUserInfo.getBaseUserId());
            } else {
                ToastUtil.showToast(ActivityThemeActivity.this, "抱歉，没有视频！");
            }
        }
    };
    private boolean mHasVideo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitytheme);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        findViewById(R.id.activity_theme_text_down).setOnClickListener(mDownListener);
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mType = getIntent().getIntExtra("type", -1);
        mId = getIntent().getStringExtra("id");
        mViewCount = getIntent().getIntExtra("viewCount", 0);

        if (EVALUATION_LESSON == mType) {
            mScore = getIntent().getParcelableExtra("score");
        }

        mResourceDetails = new ResourceDetails();
        mResourceDetails.setId(mId);
        initView();
        loadData();
    }

    private void initView() {
        mRequestSender = new RequestSender(this);
        mTitleTv = (TextView) findViewById(R.id.title);
        mTvVideoCount = (TextView) findViewById(R.id.tv_video_count);
        mListView = (HorizontalListView) findViewById(R.id.hl_video);
        mViewPager = (ViewPager) findViewById(R.id.activity_thieme_viewpager);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mVideoView = (BnVideoView2) findViewById(R.id.video_view);
        mFrameLayout = (FrameLayout) findViewById(R.id.video_area);
        mVideoLayout = (LinearLayout) findViewById(R.id.activitytheme_videolayout);
        mRltTitle = (RelativeLayout) findViewById(R.id.rltControlTitle);
        mDownBtn = (TextView) findViewById(R.id.activity_theme_text_down1);
        mComposeView = (BlogComposeView) findViewById(R.id.compose);
        mDownBtn.setOnClickListener(mDownListener);
        mVideoControl = (BNVideoControlView) findViewById(R.id.videoControl);
        mVideoControl.bindVideoView(mVideoView, getSupportFragmentManager());
        mVideoControl.setDisplayListener(new BNVideoControlView.DisplayListener() {

            @Override
            public void show() {
                mRltTitle.setVisibility(View.VISIBLE);
            }

            @Override
            public void hide() {
                if (mHasVideo) {
                    mRltTitle.setVisibility(View.GONE);
                }
            }
        });
        mVideoControl.setOnCompleteListener(new BnVideoView2.OnBNCompleteListener() {
            @Override
            public void onComplete() {
                if (++mVideoNumber < mVideoList.size()) {
                    setSelectVideo(mVideoNumber);
                    mListViewAdapter.setSelectIndex(mVideoNumber);
                    mListViewAdapter.notifyDataSetChanged();
                    mVideoView.stop();
                    playVideo(mVideoList.get(mVideoNumber).getId());
                } else {
                    --mVideoNumber;
                }
            }
        });

        int tabWidth = (int) (SystemUtils.getScreenWidth(this) / 2.0);
        mSlidingTabLayout.setTabWidth(tabWidth);
        mAdapter = new ChannelAdapter(this, getSupportFragmentManager(), mViewPager);
        switch (mType) {
            case PREPARE_LESSON:
            case INTERACT_LESSON:
            case EVALUATION_LESSON:
                Bundle bundle1 = new Bundle();
                bundle1.putInt("type", mType);
                mAdapter.addTab(getResources().getString(R.string.activity_detail), VideoIntroductionFragment.class, bundle1);
                break;
            case DELIVERY_CLASS:
                mVideoLayout.setVisibility(View.GONE);
            case LIVE_APPOINTMENT:
                findViewById(R.id.activity_theme_text_down).setVisibility(View.GONE);
                Bundle bundle = new Bundle();
                bundle.putInt("type", mType);
                bundle.putString("id", mId);
                mAdapter.addTab("课程详情", DeliveryClassDetailFragment.class, bundle);
                break;
        }
        if (EVALUATION_LESSON == mType) {
            mAdapter.addTab( "查看评论", CustomCommentFragment.class, createBundle());
        } else {
            mAdapter.addTab( getResources().getString(R.string.newest_comment), CustomCommentFragment.class, createBundle());
        }
        mSlidingTabLayout.setViewPager(mViewPager);
        mListViewAdapter = new HorizontalListViewAdapter2(this, mVideoList);
        mListView.setAdapter(mListViewAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                isNext = false;
                mVideoControl.stop();
                mListViewAdapter.setSelectIndex(position);
                mListViewAdapter.notifyDataSetChanged();
                mVideoNumber = position;
                setSelectVideo(position);
                playVideo(mVideoList.get(position).getId());
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 0){
                    mComposeView.setVisibility(View.GONE);
                }else{
                    if (mType != EVALUATION_LESSON)
                        mComposeView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    mComposeView.setVisibility(View.GONE);
                }else{
                    if (mType != EVALUATION_LESSON)
                        mComposeView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setSelectVideo(int position) {
        mResourceDetails.setId(mVideoList.get(position).getId());
        mResourceDetails.setAttachPath(mVideoList.get(mVideoNumber).getDownloadUrl());
        mResourceDetails.setResourceName(mResourceName + mVideoNumber);
    }

    private void loadData() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        String url = "";
        switch (mType) {
            case PREPARE_LESSON:
                url = URLConfig.GET_PREPARE_DETAILS;
                params.put("preparationId", mId);
                break;
            case INTERACT_LESSON:
                url = URLConfig.GET_INTERAC_DETAIL;
                params.put("lectureId", mId);
                break;
            case EVALUATION_LESSON:
                url = URLConfig.GET_EVALUATION_DETAIL;
                params.put("evaluationId", mId);
                break;
            case DELIVERY_CLASS:
                url = URLConfig.GET_SCHEDULE_DETAIL;
                params.put("scheduleDetailId", mId);
                break;
            case LIVE_APPOINTMENT:
                url = URLConfig.GET_LIVE_APPOINTMENT_DETAIL;
                params.put("liveAppointmentId", mId);
                break;
        }

        mRequestSender.sendRequest(new RequestSender.RequestData(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, response.toString());
                switch (mType) {
                    case PREPARE_LESSON:
                    case INTERACT_LESSON:
                    case EVALUATION_LESSON:
                        reservationClass(response);
                        break;
                    case DELIVERY_CLASS:
                        if ("success".equals(response.optString("result"))) {
                            DeliveryClassDetail deliveryClassDetail = DeliveryClassDetail.getDelivery(response.optJSONObject("scheduleViewDetail"));
                            ((DeliveryClassDetailFragment) mAdapter.getFragmentAt(0)).setDeliveryClassDetail(deliveryClassDetail);
                            mResourceName = deliveryClassDetail.getSchoolName() + deliveryClassDetail.getSubjectName();
                            mTitleTv.setText(mResourceName);
                            mResourceDetails.setThumbPath(deliveryClassDetail.getThumb());
                            JSONObject object = response.optJSONObject("scheduleViewDetail");
                            JSONArray videosArray = object.optJSONArray("videos");
                            setVideo(videosArray, null);
                        }
                        break;
                    case LIVE_APPOINTMENT:
                        if ("success".equals(response.optString("result"))) {
                            DeliveryClassDetail deliveryClassDetail = DeliveryClassDetail.getDelivery(response.optJSONObject("liveAppointmentDetailView"));
                            ((DeliveryClassDetailFragment) mAdapter.getFragmentAt(0)).setDeliveryClassDetail(deliveryClassDetail);
                            mResourceName = deliveryClassDetail.getSchoolName() + deliveryClassDetail.getSubjectName();
                            mTitleTv.setText(mResourceName);
                            mResourceDetails.setThumbPath(deliveryClassDetail.getThumb());
                            JSONObject object = response.optJSONObject("liveAppointmentDetailView");
                            JSONArray videosArray = object.optJSONArray("videos");
                            setVideo(videosArray, null);
                        }
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.showToast(ActivityThemeActivity.this, getString(R.string.net_error));
            }
        }, mReqTag));
    }

    /**
     * @param response
     */
    private void reservationClass(JSONObject response) {
        String result = response.optString("result");
        if ("success".equals(result)) {
            switch (mType) {
                case PREPARE_LESSON:
                case INTERACT_LESSON:
                    mMeetDetail = new Gson().fromJson(response.optJSONObject("data").toString(), MeetDetail.class);
                    setVideo(null, response);
                    break;
                case EVALUATION_LESSON:
                    mMeetDetail = new Gson().fromJson(response.optJSONObject("evluationDetail").toString(), MeetDetail.class);
                    if (mMeetDetail != null) {
//                        mMeetDetail.setViewCount(mViewCount);
                        mMeetDetail.setStartTime(DateUtil.getDateStr(response.optJSONObject("evluationDetail").optLong("startDate"), DateUtil.DEF_FORMAT));
                    }
                    setVideo(null, response);
                    break;
            }
//            if (detailsJsonObject != null) {
//                mMeetDetail = MeetDetail.parseJson(detailsJsonObject);
//            }
            if (mMeetDetail != null) {
                VideoIntroductionFragment videoIntroductionFragment = (VideoIntroductionFragment) getSupportFragmentManager().findFragmentByTag(UIUtils.obtainFragmentTag(mViewPager.getId(), 0));
                if (videoIntroductionFragment != null) {
                    videoIntroductionFragment.setVideoDetails(mMeetDetail, mScore);
                }
                mResourceName = mMeetDetail.getTitle();
                mTitleTv.setText(mResourceName);//设置标题
                mResourceDetails.setThumbPath(mMeetDetail.getSubjectPic());
            }
        } else if ("error".equals(result)) {
        }
    }

    /**
     * 设置视频
     *
     * @param jsonArray
     */
    private void setVideo(JSONArray jsonArray, JSONObject jsonObject) {
        ArrayList<ThemeVideo> videos = null;
        if (mType != DELIVERY_CLASS && jsonObject != null) {
            videos = getVideo(jsonObject);
        } else {
            videos = ThemeVideo.parseJsonArray1(jsonArray);
        }
        if (videos != null) {
            mVideoList.addAll(videos);
        }
        if (mVideoList != null) {
            mTvVideoCount.setText("共" + mVideoList.size() + "段");
            mListViewAdapter.notifyDataSetChanged();
            if (mVideoList.size() <= 1) {
                if (mType != DELIVERY_CLASS && mType != LIVE_APPOINTMENT) {
                    mVideoLayout.setVisibility(View.GONE);
                    mDownBtn.setVisibility(View.VISIBLE);
                }
            } else {
                if (mType != DELIVERY_CLASS && mType != LIVE_APPOINTMENT) {
                    mDownBtn.setVisibility(View.GONE);
                    findViewById(R.id.activity_theme_text_down).setVisibility(View.VISIBLE);
                    mVideoLayout.setVisibility(View.VISIBLE);
                }
            }
            if (mVideoList.size() == 0) {
                mHasVideo = false;
                ToastUtil.showToast(ActivityThemeActivity.this, "暂无视频资源!");
                mDownBtn.setVisibility(View.GONE);
            } else {
                mHasVideo = true;
            }
            if (mVideoList.size() > 0) {
                mListViewAdapter.setSelectIndex(0);
                playVideo(mVideoList.get(mVideoNumber).getId());
                setSelectVideo(mVideoNumber);
            }
        }
    }

    private ArrayList<ThemeVideo> getVideo(JSONObject object) {
        switch (mType) {
            case PREPARE_LESSON:
            case INTERACT_LESSON: {
                JSONArray jsonArray = object.optJSONArray("meetVideoList");
                if (jsonArray != null && jsonArray.length() > 0) {
                    ArrayList<ThemeVideo> videos = new ArrayList<>(jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object1 = jsonArray.optJSONObject(i);
                        ThemeVideo themeVideo = new ThemeVideo();
                        themeVideo.setId(object1.optString("meetVideoId"));
                        themeVideo.setDownloadUrl(object1.optString("downloadPath"));
                        themeVideo.setFilePath(object1.optString("filePath"));
                        videos.add(themeVideo);
                    }
                    return videos;
                }
            }
            case EVALUATION_LESSON:
                JSONArray jsonArray = object.optJSONArray("videoIds");
                if (jsonArray != null && jsonArray.length() > 0) {
                    ArrayList<ThemeVideo> videos = new ArrayList<>(jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object1 = jsonArray.optJSONObject(i);
                        ThemeVideo themeVideo = new ThemeVideo();
                        themeVideo.setDownloadUrl(object1.optString("downloadUrl"));
                        themeVideo.setFilePath(object1.optString("filePath"));
                        videos.add(themeVideo);
                    }
                    return videos;
                }
                break;
        }

        return null;
    }

    private void playVideo(String id) {
        //1.判断是否已经下载 本视频
        if (FileDownloadService.hasMp4Downloaded(mUserInfo.getBaseUserId(), id)) {
            String video = FileDownloadService.getCachedMp4File(mUserInfo.getBaseUserId(), id);
            Cog.i("video:", video);
            mVideoControl.setVideoPath(video, BnVideoView2.BN_URL_TYPE_HTTP, true);
        } else {
            String filePath = mVideoList.get(mVideoNumber).getFilePath();
            mResourceDetails.setAttachPath(mVideoList.get(mVideoNumber).getDownloadUrl());
            if (filePath != null && !filePath.equals("null")) {
                mVideoControl.setVideoPath(filePath, BnVideoView2.BN_URL_TYPE_HTTP, false);
            } else {
                ToastUtil.showToast(ActivityThemeActivity.this, "找不到视频！");
            }
        }
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

    private Bundle createBundle() {
        Bundle args = new Bundle();
        args.putParcelable(Constants.USER_INFO, mUserInfo);
        args.putString("mid", mId);
        args.putInt("type", mType);
        switch (mType) {
            case PREPARE_LESSON:
            case INTERACT_LESSON:
                break;
            case EVALUATION_LESSON:
                args.putBoolean("isComment", false);
                break;
            case DELIVERY_CLASS:
            case LIVE_APPOINTMENT:
                break;
        }
        return args;
    }

    @Override
    protected void onDestroy() {
        mRequestSender.stop(mReqTag);
        mVideoControl.stop();
        super.onDestroy();
    }

    public static void start(Context context, int type, String id, int viewCount) {
        Intent intent = new Intent(context, ActivityThemeActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("id", id);
        intent.putExtra("viewCount", viewCount);
        context.startActivity(intent);
    }

    public static void start(Context context, int type, String id, int viewCount, EvaluationScore evaluationScore) {
        Intent intent = new Intent(context, ActivityThemeActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("id", id);
        intent.putExtra("viewCount", viewCount);
        intent.putExtra("score", evaluationScore);
        context.startActivity(intent);
    }

    @Override
    public BlogComposeView getBlogComposeView() {
        return mComposeView;
    }
}
