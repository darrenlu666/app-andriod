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
import com.codyy.erpsportal.commons.models.entities.Classroom;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.AutoHideUtils;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout;
import com.codyy.erpsportal.commons.widgets.BnVideoView;

/**
 * 课堂巡视单个视频页
 * Created by gujiajia on 2015/10/24.
 */
public class ClassroomVideoFragment extends Fragment {

    private static String TAG = "BNVideoPlayFragment";
    private TextView mTitleTv;
    private BnVideoLayout mVideoLayout;
//    private BNPlayerFactory mBnPlayerFactory;
    private Classroom mClassroom;
    private RelativeLayout mRelativeLayout;
    private AutoHideUtils mAutoHide;

    public static ClassroomVideoFragment newInstance(Classroom classroom) {
        Cog.d(TAG,"newInstance()!~", classroom.getSchoolName());
        ClassroomVideoFragment f = new ClassroomVideoFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable("classroom", classroom);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mClassroom = getArguments().getParcelable("classroom");
        }
//        mBnPlayerFactory = new BNPlayerFactory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_live_video_play, container, false);
        mTitleTv = (TextView) view.findViewById(R.id.txtTitleOfLiveVideoPlay);
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
        mTitleTv.setText(String.format(format_title, mClassroom.getSchoolName()));

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

    //检测是否为3G网络
    public void check3GWifi() {
        Cog.d(TAG, "check3GWifi()~");
        if(LiveVideoListPlayActivity.mIsPlayable){
            startPlay();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Cog.i(TAG, "onResume()!!!!!!!!!!!!!!!!!!~~~~~~~~~~~~~~~~");
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
    public void onDestroy() {
        super.onDestroy();
        //销毁hide handler线程
        if(null != mAutoHide){
            mAutoHide.destroyView();
        }
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
        Cog.d(TAG, "startPlay mUrl=" + mClassroom.getVideoUrl());
        if (TextUtils.isEmpty(mClassroom.getVideoUrl())) {
            Cog.e(TAG, "startPlay mUrl is NULL!");
            return;
        }

        mVideoLayout.setUrl(mClassroom.getVideoUrl());
        mVideoLayout.play();
    }

    private void stopPlay() {
        if (!TextUtils.isEmpty(mClassroom.getVideoUrl()) && mVideoLayout.isPlaying()) {
            mVideoLayout.stop();
        }
    }
}
