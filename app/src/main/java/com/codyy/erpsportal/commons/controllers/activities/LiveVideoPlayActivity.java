package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.Classroom;
import com.codyy.erpsportal.commons.models.entities.LiveClassListModel;
import com.codyy.erpsportal.commons.models.entities.LiveVideoDetail;
import com.codyy.erpsportal.commons.models.entities.SchoolNetClassListModel;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.parsers.ClassTourClassroomParser;
import com.codyy.erpsportal.commons.utils.AutoHideUtils;
import com.codyy.erpsportal.commons.utils.Check3GUtil;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.WiFiBroadCastUtils;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout2;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 直播详细、视频播放页面
 * Created by caixingming on 2015/4/24.
 */
public class LiveVideoPlayActivity extends AppCompatActivity {

    private String TAG = LiveVideoPlayActivity.class.getSimpleName();
    public static final int PLAY_STYLE_LIVE = 0x001;//从专递课堂跳转
    public static final int PLAY_STYLE_SCHOOLNET = 0x002;//从直录播课堂跳转
    /**
     * 从哪里跳转过来
     */
    private static int mStartFrom;

    private BnVideoLayout2 mVideoLayout;
//    private BNPlayerFactory mBnPlayerFactory;

    private String mId;//schedule id

    private String mTitle;//标题

    private String mUrl;

    private TextView mTextTitle;

    private RelativeLayout mRelativeLayout;

    private AutoHideUtils mAutoHide;

    private List<String> videoList;
    //增加网络监测
    private WiFiBroadCastUtils wfb;


    /**
     * 辅课堂播放流地址
     */
    private String mAssociateVideoUrl = "";
    /**
     * 辅课堂
     */
    private Classroom mAssociateClassroom;
    /**
     * 主课堂
     */
    private Classroom mMainClassroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_live_video_play);
        init();
        //禁止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void init() {
        mId = getIntent().getStringExtra("id");
        mTitle = getIntent().getStringExtra("title");
        mUrl = getIntent().getStringExtra("url");

        mTextTitle = (TextView) findViewById(R.id.txtTitleOfLiveVideoPlay);
        mVideoLayout = (BnVideoLayout2) findViewById(R.id.bnVideoViewOfLiveVideoLayout);
        mVideoLayout.setVolume(100);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeHeadOfLiveVideoPlay);
        String formatTitle = getString(mStartFrom == PLAY_STYLE_LIVE ? R.string.title_live_class:R.string.title_schoollive_class);
        mTextTitle.setText(String.format(formatTitle, mTitle));
        mAutoHide = new AutoHideUtils(mRelativeLayout);
        mVideoLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mAutoHide.showControl();
                return false;
            }
        });

        mVideoLayout.setOnSurfaceChangeListener(new BnVideoView2.OnSurfaceChangeListener() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                check3GWifi();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Cog.e(TAG, "surfaceDestroyed()~~~~~~~~~~~~~~~~");

            }
        });

        mAutoHide.showControl();
        videoList = new ArrayList<>();

        check3GWifi();
        registerWiFiListener();
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

    private void registerWiFiListener() {
        wfb = new WiFiBroadCastUtils(this, getSupportFragmentManager(),new WiFiBroadCastUtils.PlayStateListener() {
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

    private void startPlay() {
        if(!mUrl.equals("") && !mVideoLayout.isPlaying()){
            Cog.i("video:", mUrl);
            mVideoLayout.setUrl(mUrl,BnVideoView2.BN_URL_TYPE_RTMP_LIVE);
            mVideoLayout.play(BnVideoView2.BN_PLAY_DEFAULT);
        }
    }

    private void loadReceivingClassroomStreams() {
        Map<String, String> params = new HashMap<>();
        params.put("id", mId);
        params.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());

        String url = "";
        if (mStartFrom == PLAY_STYLE_LIVE) {//专递课堂
            url = URLConfig.SPECIAL_DELIVERY_CLASSROOM_VIDEOS;
        } else if(mStartFrom == PLAY_STYLE_SCHOOLNET){//直录播课堂
            url = URLConfig.SCHOOL_NET_CLASSROOM_VIDEOS;
        }

        //requestQueue.add(npr);
        RequestSender requestSender = new RequestSender(this);
        requestSender.sendRequest(new RequestSender.RequestData(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadReceivingClassroomStreams:", response);
                if ("success".equals(response.optString("result"))) {
                    JSONArray jsonArray = response.optJSONArray("watchPath");
                    List<Classroom> classroomList = new ClassTourClassroomParser().parseArray(jsonArray);
                    if (classroomList != null && classroomList.size() > 0) {

                        //获取主课堂信息放于mMainClassroom
                        for(int i = 0;i < classroomList.size();i++){
                            if(classroomList.get(i).getType().equals("main")){
                                mMainClassroom = classroomList.get(i);
                            }
                        }

                        //获取辅课堂（List）地址，并放于videoList中
                        for(int i = 0;i<classroomList.size();i++){
                            if(classroomList.get(i).getType().equals("receive")){
                                mAssociateClassroom = classroomList.get(i);
                                if(classroomList.get(i).getStreamingServerType().equals("DMC")){
                                    RequestQueue queue = Volley.newRequestQueue(LiveVideoPlayActivity.this);
                                    StringRequest stringRequest = new StringRequest(classroomList.get(i).getDmsServerHost() + "?method=play&stream=class_" + classroomList.get(i).getClassRoomId() + "_u_" + classroomList.get(i).getId(), new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            String result = response.substring(response.indexOf(":") + 2,response.length() - 2);
                                            mAssociateVideoUrl = result + "/class_" + mMainClassroom.getClassRoomId() + "_u_" + mAssociateClassroom.getId() + "_" + mAssociateClassroom.getClassRoomId();
                                            videoList.add(mAssociateVideoUrl);
//                                            setVolumes();
//                                            playWithAudioMix();
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            ToastUtil.showToast(LiveVideoPlayActivity.this, error.toString());
                                        }
                                    });
                                    queue.add(stringRequest);
                                }else{
                                    mAssociateVideoUrl = mAssociateClassroom.getVideoUrl() + "/class_" + mMainClassroom.getClassRoomId() + "_u_" + mAssociateClassroom.getId() + "_" + mAssociateClassroom.getClassRoomId();
                                    videoList.add(mAssociateVideoUrl);
//                                    setVolumes();
//                                    playWithAudioMix();
                                }
                            }
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.e(TAG, "loadReceivingClassroomStreams onErrorResponse:", error);
            }
        }));
    }

    public void onBackClick(View view) {
        this.finish();
        overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
    }

    /**
     * 专递课堂的参数传递
     * @param activity
     * @param mClass
     * @param playUrl
     */
    public static void start(Activity activity, LiveClassListModel mClass ,String playUrl) {
        Intent intent = new Intent(activity, LiveVideoPlayActivity.class);
        intent.putExtra("url", playUrl);
        intent.putExtra("title", mClass.getSchool());
        intent.putExtra("id", mClass.getId());
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);

        mStartFrom = PLAY_STYLE_LIVE;
    }

    /**
     * 直录播课堂的参数传递
     * @param activity
     * @param mClass
     * @param playUrl
     */
    public static void start(Activity activity, SchoolNetClassListModel mClass , String playUrl) {
        Intent intent = new Intent(activity, LiveVideoPlayActivity.class);
        intent.putExtra("url", playUrl);
        intent.putExtra("title", mClass.getSchoolName());
        intent.putExtra("id", mClass.getAppointmentId());
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);

        mStartFrom = PLAY_STYLE_SCHOOLNET;
    }

    /**
     * 点击名校网络课堂的实时直播接口
     */
    public static void startVideoFromSchoolNet(Activity activity,LiveVideoDetail mClass,String playUrl,String appointmentId){
        Intent intent = new Intent(activity,LiveVideoPlayActivity.class);
        intent.putExtra("url",playUrl);
        intent.putExtra("title",mClass.getClassroomName());
        intent.putExtra("id",appointmentId);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);

        mStartFrom = PLAY_STYLE_SCHOOLNET;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoLayout != null) {
            mVideoLayout.stop();
        }
    }

    @Override
    protected void onDestroy() {
        //解除广播接收器
        if (null != wfb) {
            wfb.destroy();
        }
        super.onDestroy();
        //销毁hide handler线程
        if(null != mAutoHide){
            mAutoHide.destroyView();
        }
    }

}
