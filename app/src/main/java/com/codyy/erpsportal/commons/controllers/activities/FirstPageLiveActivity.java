package com.codyy.erpsportal.commons.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.interfaces.IFragmentMangerInterface;
import com.codyy.erpsportal.commons.models.entities.ScheduleLiveView;
import com.codyy.erpsportal.commons.utils.AutoHideUtils;
import com.codyy.erpsportal.commons.utils.Check3GUtil;
import com.codyy.erpsportal.commons.utils.LiveViewUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.WiFiBroadCastUtils;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout2;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页直播页面
 * Created by kmdai on 2015/9/15.
 */
public class FirstPageLiveActivity extends AppCompatActivity implements LiveViewUtils.OnUrlResultListener,IFragmentMangerInterface {
    public static final String INTENT_ID = "url";
    public static final String INTENT_LIVE_TYPE = "livetype";

    @Bind(R.id.bnVideoViewOfLiveVideoLayout)
    BnVideoLayout2 mBnVideoLayout;

    @Bind(R.id.txtTitleOfLiveVideoPlay)
    TextView mTextTitle;

    private String mID;
    private String mLiveType;

    LiveViewUtils mLiveViewUtils;

    //    private BNPlayerFactory mBnPlayerFactory;

    @Bind(R.id.relativeHeadOfLiveVideoPlay)
    RelativeLayout mRelativeLayout;

    private AutoHideUtils mAutoHide;
    //增加网络监测
    private WiFiBroadCastUtils wfb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_video_play);
        ButterKnife.bind(this);
        mID = getIntent().getStringExtra(INTENT_ID);
        mLiveType = getIntent().getStringExtra(INTENT_LIVE_TYPE);
        mLiveViewUtils = new LiveViewUtils(this);
        mLiveViewUtils.setOnUrlResultListener(this);
        mLiveViewUtils.playLiveVideo(mID, mLiveType);
        //禁止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mAutoHide = new AutoHideUtils(mRelativeLayout);
        mBnVideoLayout.setVolume(100);
        mBnVideoLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mAutoHide.showControl();
                return false;
            }
        });
        mAutoHide.showControl();
        registerWiFiListener();
    }

    /**
     * 返回
     *
     * @param view
     */
    public void onBackClick(View view) {
        finish();
    }

    private List<ScheduleLiveView> mScheduleLiveViews;

    @Override
    public void onStreamFetched(List<ScheduleLiveView> scheduleLiveViews) {
        mScheduleLiveViews = scheduleLiveViews;
        System.out.println("--------" + mScheduleLiveViews.get(0).getStreamUrl());
        if (!TextUtils.isEmpty(mScheduleLiveViews.get(0).getStreamUrl())) {//检查主课视频流是否正确获取
            check3GWifi();
        } else {//检查主课视频流没有，退出播放
            ToastUtil.showToast(this, "获取主课堂直播流失败！");
            finish();
        }
    }

    @Override
    public void onMainClassroomLoaded(ScheduleLiveView scheduleLiveView) {
        mTextTitle.setText(scheduleLiveView.getSchoolName() + " " + scheduleLiveView.getRoomName());
    }

    public static void startActivity(Context mContext, String id, String liveType) {
        Intent intent = new Intent(mContext, FirstPageLiveActivity.class);
        intent.putExtra(INTENT_ID, id);
        intent.putExtra(INTENT_LIVE_TYPE, liveType);
        mContext.startActivity(intent);
    }

    private void registerWiFiListener() {

        wfb = new WiFiBroadCastUtils(this, new WiFiBroadCastUtils.PlayStateListener() {
            @Override
            public void play() {
                if (!mBnVideoLayout.isPlaying()) {
                    mBnVideoLayout.play(0);
                }
            }

            @Override
            public void stop() {
                if (mBnVideoLayout.isPlaying()) {
                    mBnVideoLayout.stop();
                }
            }
        });
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

    /**
     * 开始播放
     */
    private void startPlay() {
        if (mDestroyed) return;
        mBnVideoLayout.setUrl(mScheduleLiveViews.get(0).getStreamUrl(), BnVideoView2.BN_URL_TYPE_RTMP_LIVE);
        mBnVideoLayout.play(0);
        //        checkAndSetAudios();
    }

    /**
     * 如果有辅课堂，设置辅课堂流用于混音
     */
    @Deprecated
    public void checkAndSetAudios() {
        if (mScheduleLiveViews != null && mScheduleLiveViews.size() > 1) {
            String[] audioStreams = new String[mScheduleLiveViews.size() - 1];
            for (int i = 0; i < audioStreams.length; i++) {
                audioStreams[i] = mScheduleLiveViews.get(i + 1).getStreamUrl();
            }
//            mBnVideoLayout.playWithAssistUris(audioStreams);
        } else {
            mBnVideoLayout.play(BnVideoView2.BN_PLAY_DEFAULT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mScheduleLiveViews != null && mScheduleLiveViews.size() > 0 && !TextUtils.isEmpty(mScheduleLiveViews.get(0).getStreamUrl())) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (mBnVideoLayout != null && !mBnVideoLayout.isPlaying()) {
                        //                        mVideoLayout.playNow();
                        check3GWifi();
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBnVideoLayout != null && mBnVideoLayout.isPlaying()) {
            mBnVideoLayout.stop();
        }
    }

    boolean mDestroyed;

    @Override
    protected void onDestroy() {
        //解除广播接收器
        if (null != wfb) {
            wfb.destroy();
        }
        super.onDestroy();
        mDestroyed = true;
        mBnVideoLayout.stop();
        //销毁hide handler线程
        if(null != mAutoHide){
            mAutoHide.destroyView();
        }
    }

    @Override
    public FragmentManager getNewFragmentManager() {
        return getSupportFragmentManager();
    }
}
