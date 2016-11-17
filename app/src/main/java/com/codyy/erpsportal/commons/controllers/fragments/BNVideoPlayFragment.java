package com.codyy.erpsportal.commons.controllers.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.LiveVideoListPlayActivity;
import com.codyy.erpsportal.commons.models.entities.PVideo;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.AutoHideUtils;
import com.codyy.erpsportal.commons.utils.WiFiBroadCastUtils;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout;
import com.codyy.erpsportal.commons.widgets.BnVideoView;

/**
 * 视频播放Fragment用于有多个辅课堂的场景 .
 * Created by caixingming on 2015/5/28.
 */
public class BNVideoPlayFragment extends Fragment {

    private static String TAG = "BNVideoPlayFragment";
    private static final String ARG_VIDEO_TITLE = "title";
    private static final String ARG_VIDEO_URL = "url";
    private TextView textTitle;//视频标题
    private BnVideoLayout mVideoLayout;//视频组件
    private String mUrl;//视频播放地址
    private String mTitle;//传递过来的标题
    private RelativeLayout mRelativeLayout;//header 容器
    private AutoHideUtils mAutoHide;//自动隐藏
    private WiFiBroadCastUtils wfb; //增加网络监测

    public static BNVideoPlayFragment newInstance(PVideo video) {
        Cog.d(TAG,"newInstance()!~", video.getVideoName());
        BNVideoPlayFragment f = new BNVideoPlayFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_TITLE, video.getVideoName());
        args.putString(ARG_VIDEO_URL, video.getVideoPath());
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_VIDEO_TITLE);
            mUrl = getArguments().getString(ARG_VIDEO_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_live_video_play, container, false);
        textTitle = (TextView) view.findViewById(R.id.txtTitleOfLiveVideoPlay);
        mVideoLayout = (BnVideoLayout) view.findViewById(R.id.bnVideoViewOfLiveVideoLayout);
        mVideoLayout.setVolume(100);
        mVideoLayout.setPlayingListener(new BnVideoView.OnPlayingListener() {

            @Override
            public void onPlaying() {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Cog.i(TAG, "surfaceCreated()!!!!!!!!!!!!!!!!!!~~~~~~~~~~~~~~~~");

                //监测视频是否在fragment消失时被销毁?，如果没有则手动销毁一次
                if (mVideoLayout != null && mVideoLayout.isPlaying()) {
                    Cog.i(TAG, "mVideoLayout is playing .............~~~~~~~~~~~~~~~");
                    mVideoLayout.stop();
                }

                //延迟2s执行视频恢复等待 stop销毁动作结束
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isVisible() && getUserVisibleHint()) {
                            check3GWifi();
                        }
                    }
                }, 2 * 1000);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Cog.i(TAG, "surfaceDestroyed()!!!!!!!!!!!!!!!!!!~~~~~~~~~~~~~~~~");
                mVideoLayout.stop();
            }
        });

        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativeHeadOfLiveVideoPlay);
        //back btn
        view.findViewById(R.id.imgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        String format_title = getString(R.string.title_live_class);
        textTitle.setText(String.format(format_title, mTitle));

        //自动隐藏视频标题实现
        mAutoHide = new AutoHideUtils(mRelativeLayout);
        mVideoLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mAutoHide.showControl();
                return false;
            }
        });
        mAutoHide.showControl();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //注册wifi监听
        registerWiFiListener();
    }


    //检测是否为3G网络
    public void check3GWifi() {
        Cog.d(TAG,"check3GWifi()~");
        if(LiveVideoListPlayActivity.mIsPlayable){
            startPlay();
        }
    }

    private void registerWiFiListener() {
        wfb = new WiFiBroadCastUtils(getActivity(),getActivity().getSupportFragmentManager(), new WiFiBroadCastUtils.PlayStateListener() {
            @Override
            public void play() {
                if (!mVideoLayout.isPlaying()) {
                    mVideoLayout.setUrl(mUrl);
                    mVideoLayout.play();
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
    public void onResume() {
        super.onResume();
        Cog.i(TAG,"onResume()!!!!!!!!!!!!!!!!!!~~~~~~~~~~~~~~~~");
        if (isVisible() && getUserVisibleHint()) {
            check3GWifi();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        stopPlay();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisible()) {
            if (isVisibleToUser) {
                check3GWifi();
            } else {
                stopPlay();
            }
        }
    }

    private void startPlay() {
        Cog.d(TAG, "startPlay mUrl=" + mUrl);
        if (TextUtils.isEmpty(mUrl)) {
            Cog.e(TAG, "startPlay mUrl is NULL!");
            return;
        }

        mVideoLayout.setUrl(mUrl);
        mVideoLayout.play();
    }

    private void stopPlay() {
        if (!TextUtils.isEmpty(mUrl) && mVideoLayout.isPlaying()) {
            mVideoLayout.stop();
        }
    }

    @Override
    public void onDestroy() {
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
