package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.classroom.models.ClassRoomDetail;
import com.codyy.erpsportal.commons.models.entities.Classroom;
import com.codyy.erpsportal.commons.models.entities.Classroom.VideoUrlCallback;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.NormalGetRequest;
import com.codyy.erpsportal.commons.models.network.RequestManager;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.parsers.ClassTourClassroomParser;
import com.codyy.erpsportal.commons.utils.AutoHideUtils;
import com.codyy.erpsportal.commons.utils.Check3GUtil;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout2;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 直播详细、视频播放页面
 * Created by caixingming on 2015/4/24.
 * <p/>
 * 多个视频播放、主辅课堂页面
 */
public class LiveVideoListPlayActivity extends FragmentActivity {

    private String TAG = LiveVideoListPlayActivity.class.getSimpleName();

    private static final String EXTRA_ID =   "id";

    private static final String EXTRA_CLASSROOM = "video";

    private static final String EXTRA_USER_INFO = "user_info";

    private static final String EXTRA_TYPE = "type";

    private ViewPager mViewPager;

    private UserInfo mUserInfo;

    /**
     * 上一个界面传来的课堂
     */
    private Classroom mParentClassroom;

    private List<Classroom> mClassroomList = new ArrayList<>();

    private String mType;

    /**
     * 约课id
     */
    private String mId;// getClassWatchAll.do?scheduleDetailId = mId

    private ClassroomsPagerAdapter mClassroomPagerAdapter;

    private ClassTourClassroomParser mParser;

    public static boolean mIsPlayable = false;

    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_bnvideo_viewpager);
        mRequestQueue = RequestManager.getRequestQueue();
        mParser = new ClassTourClassroomParser();
        init();
        //禁止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        showPrompt();
    }

    public void init() {
        mIsPlayable =   false;//init state
        mId = getIntent().getStringExtra(EXTRA_ID);
        mUserInfo = getIntent().getParcelableExtra(EXTRA_USER_INFO);
        mParentClassroom = getIntent().getParcelableExtra(EXTRA_CLASSROOM);
        mType = getIntent().getStringExtra(EXTRA_TYPE);
        mViewPager = (ViewPager) findViewById(R.id.live_class_viewpager);
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Cog.d(TAG, "onPageScrolled position=" + position);
            }

            @Override
            public void onPageSelected(int position) {
                Cog.d(TAG, "onPageSelected position=" + position);
                playCurrent();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        loadData();
    }

    private void updateViewPager() {
        if (null != mClassroomPagerAdapter && mClassroomList != null) {
            mViewPager.setAdapter(mClassroomPagerAdapter);
        } else {
            mClassroomPagerAdapter = new ClassroomsPagerAdapter(this);
            mViewPager.setAdapter(mClassroomPagerAdapter);
        }

        Check3GUtil.instance().CheckNetType(this, new Check3GUtil.OnWifiListener() {
            @Override
            public void onNetError() {
            }

            @Override
            public void onContinue() {
                mIsPlayable = true;
                playCurrent();
            }
        });
    }

    private void playCurrent() {
        int current = mViewPager.getCurrentItem();
        final BnVideoLayout2 videoLayout = mClassroomPagerAdapter.getBnVideoLayout( current);
        final Classroom classroom = mClassroomList.get( current);
        if (!TextUtils.isEmpty(classroom.getVideoUrl())) {
            playVideo(videoLayout , classroom.getVideoUrl());
            return;
        }
        if (classroom.isMain()) {//如果是主课堂还是原来的方式
            classroom.fetchMainVideoUrl(mRequestQueue, new VideoUrlCallback() {
                @Override
                public void onUrlFetched(String url) {
                    videoLayout.setUrl(url,BnVideoView2.BN_URL_TYPE_RTMP_LIVE);
                    videoLayout.play(BnVideoView2.BN_PLAY_DEFAULT);
                }
                @Override
                public void onError() {
                    videoLayout.onError(-2,TAG);
                }
            });
        } else {//
            if ("DMC".equals(classroom.getStreamingServerType())) {
                NormalGetRequest request = new NormalGetRequest(classroom.getDmsServerHost() + "?method=play&stream=class_" + classroom.getClassRoomId() + "_u_" + classroom.getId(), new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Cog.d(TAG, "fetchMainVideoUrl response=", response);
                        String result = response.optString("result");
                        String videoUrl = result + "/class_" + mParser.mMainClassroomId + "_u_" + classroom.getId() + "_" + classroom.getClassRoomId();
                        Cog.d(TAG, "dmc type videoUrl=", videoUrl);
                        classroom.setVideoUrl(videoUrl);
                        playVideo(videoLayout, videoUrl);
                    }
                }, new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Cog.d(TAG, "fetchMainVideoUrl error=", error);
                        videoLayout.onError(-2,null == error ?error.getMessage():TAG);
                    }
                });
                mRequestQueue.add(request);
            } else {
                String videoUrl = classroom.getPmsServerHost() + "/class_" + mParser.mMainClassroomId + "_u_" + classroom.getId() + "_" + classroom.getClassRoomId();
                Cog.d(TAG, "pms type videoUrl=", videoUrl);
                classroom.setVideoUrl(videoUrl);
                playVideo(videoLayout , videoUrl);
            }
        }
    }

    private void playVideo(BnVideoLayout2 videoLayout, String url) {
        videoLayout.setUrl(url,BnVideoView2.BN_URL_TYPE_RTMP_LIVE);
        videoLayout.play(BnVideoView2.BN_PLAY_DEFAULT);
    }

    /**
     * 显示提示
     */
    private void showPrompt() {
        SharedPreferences sharedPreferences = getSharedPreferences("hint", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("tourHint", true)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("tourHint", false);
            editor.apply();
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
            View view = getLayoutInflater().inflate(R.layout.navigation_layout, null);
            TextView promptTv = (TextView) view.findViewById(R.id.tv_prompt);
            promptTv.setText(R.string.slide_to_switch_video);
            ImageView imageView = (ImageView) view.findViewById(R.id.navigation_image);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.na_anim);
            imageView.startAnimation(animation);
            dialog.setContentView(view);
            dialog.show();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        }
    }

    private void loadData() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        String url;
        if (ClassTourNewActivity.TYPE_SPECIAL_DELIVERY_CLASSROOM.equals(mType)) {//专递课堂
            url = URLConfig.SPECIAL_DELIVERY_CLASSROOM_VIDEOS;
        } else {//直录播课堂
            url = URLConfig.SCHOOL_NET_CLASSROOM_VIDEOS;
        }
        params.put("id", mId);

        //requestQueue.add(npr);
        RequestSender requestSender = new RequestSender(LiveVideoListPlayActivity.this);
        requestSender.sendRequest(new RequestSender.RequestData(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if ("success".equals(response.optString("result"))) {
                    JSONArray jsonArray = response.optJSONArray("watchPath");
                    List<Classroom> classroomList = mParser.parseArray(jsonArray);
                    if (classroomList != null && classroomList.size() > 0) {
                        mClassroomList.clear();
                        mClassroomList.addAll(classroomList);
                        updateViewPager();
                    } else {
                        UIUtils.toast(LiveVideoListPlayActivity.this, "无法获取视频地址！", Toast.LENGTH_SHORT);
                        finish();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.e(TAG, "onErrorResponse:" + error);
//                UiUtils.toast(LiveVideoListPlayActivity.this, R.string.net_error, Toast.LENGTH_SHORT);
                mClassroomList.clear();
                if (mParentClassroom != null) {
                    mClassroomList.add(mParentClassroom);
                    updateViewPager();
                }
//                finish();
            }
        }));
    }

    public static void start(Activity activity, Classroom classroom, UserInfo userInfo, String type) {
        Intent intent = new Intent(activity, LiveVideoListPlayActivity.class);
        intent.putExtra(EXTRA_ID, classroom.getId());
        intent.putExtra(EXTRA_USER_INFO, userInfo);
        intent.putExtra(EXTRA_CLASSROOM, classroom);
        intent.putExtra(EXTRA_TYPE, type);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    public static void start(Activity activity, ClassRoomDetail classRoomDetail,UserInfo userInfo,String type){
        Intent intent = new Intent(activity, LiveVideoListPlayActivity.class);
        intent.putExtra(EXTRA_USER_INFO, userInfo);
        intent.putExtra(EXTRA_CLASSROOM, classRoomDetail);
        intent.putExtra(EXTRA_TYPE, type);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

//    private BNPlayerFactory mBnPlayerFactory = new BNPlayerFactory();

    private class ClassroomsPagerAdapter extends PagerAdapter {

        private LayoutInflater mLayoutInflater;

        private SparseArray<BnVideoLayout2> mBnVideoLayouts = new SparseArray<>();

        public ClassroomsPagerAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mClassroomList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final Classroom classroom = mClassroomList.get(position);
            View view = mLayoutInflater.inflate(R.layout.activity_live_video_play, container, false);
            TextView mTitleTv = (TextView) view.findViewById(R.id.txtTitleOfLiveVideoPlay);
            final BnVideoLayout2 videoLayout = (BnVideoLayout2) view.findViewById(R.id.bnVideoViewOfLiveVideoLayout);
            videoLayout.setVolume(100);
            videoLayout.setOnSurfaceChangeListener(new BnVideoView2.OnSurfaceChangeListener() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    Cog.i(TAG, "surfaceCreated()!!!!!!!!!!!!!!!!!!~~~~~~~~~~~~~~~~");

                    //监测视频是否在fragment消失时被销毁?，如果没有则手动销毁一次
                    if (videoLayout != null && videoLayout.isPlaying()) {
                        Cog.i(TAG, "mVideoLayout is playing .............~~~~~~~~~~~~~~~");
                        videoLayout.stop();
                    }

//                    videoLayout.setUrl(classroom.getVideoUrl(),BnVideoView2.BN_URL_TYPE_RTMP_LIVE);
//                    videoLayout.play(BnVideoView2.BN_PLAY_TYPE_1);

                    //延迟2s执行视频恢复等待 stop销毁动作结束
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mIsPlayable) {
                                Cog.d(TAG, "startPlay mUrl=" + classroom.getVideoUrl());
                                if (TextUtils.isEmpty(classroom.getVideoUrl())) {
                                    Cog.e(TAG, "startPlay mUrl is NULL!");
                                    return;
                                }

                                videoLayout.setUrl(classroom.getVideoUrl(),BnVideoView2.BN_URL_TYPE_RTMP_LIVE);
                                videoLayout.play(BnVideoView2.BN_PLAY_DEFAULT);
                            }
                        }
                    }, 2 * 1000);
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    Cog.i(TAG, "surfaceDestroyed()!!!!!!!!!!!!!!!!!!~~~~~~~~~~~~~~~~");
                    videoLayout.stop();
                }
            });

            RelativeLayout mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativeHeadOfLiveVideoPlay);
            view.findViewById(R.id.imgBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            String formatTitle = getString(R.string.title_live_class);
            mTitleTv.setText(String.format(formatTitle, classroom.getSchoolName()));

            final AutoHideUtils autoHide = new AutoHideUtils(mRelativeLayout);
            videoLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    autoHide.showControl();
                    return false;
                }
            });
            autoHide.showControl();
            mBnVideoLayouts.append(position, videoLayout);
            container.addView(view);
            return view;
        }

        public BnVideoLayout2 getBnVideoLayout(int position) {
            return mBnVideoLayouts.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
            mBnVideoLayouts.delete(position);
        }
    }
}
