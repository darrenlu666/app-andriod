package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.AutoHideUtils;
import com.codyy.erpsportal.commons.utils.Check3GUtil;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.WiFiBroadCastUtils;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout2;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;
import com.codyy.erpsportal.commons.widgets.BnVideoView2.OnBNBufferUpdateListener;
import com.codyy.erpsportal.commons.widgets.BnVideoView2.OnBNCompleteListener;
import com.codyy.erpsportal.commons.widgets.BnVideoView2.OnPlayingListener;

public class HttpVideoPlayerActivity extends AppCompatActivity {

    private final static String TAG = "HttpVideoPlayerActivity";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_URL = "url";

    private String mTitle;
    private String mUrl;
    private TextView mTextTitle;
    private BnVideoLayout2 mVideoLayout;
    private RelativeLayout mRelativeLayout;
    private AutoHideUtils mAutoHide;
    private WiFiBroadCastUtils mWiFiBroadCastUtils;
    private ImageButton mPlayIb;
    private SeekBar mPlayingSb;
    private TextView mCurrentTv;
    private TextView mDurationTv;
    private RelativeLayout mControlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_http_video_player);
        init();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void init() {
        mTitle = getIntent().getStringExtra(EXTRA_TITLE);
        mUrl = getIntent().getStringExtra(EXTRA_URL);

        mTextTitle = (TextView) findViewById(R.id.tv_video_title);
        mTextTitle.setText(mTitle);
        mVideoLayout = (BnVideoLayout2) findViewById(R.id.video_layout);
        mVideoLayout.setVolume(100);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl_video_title);
        mControlView = (RelativeLayout) findViewById(R.id.control_view);
        mAutoHide = new AutoHideUtils(mRelativeLayout, mControlView);
        mVideoLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mAutoHide.showControl();
                return false;
            }
        });
        mAutoHide.showControl();

        mPlayIb = (ImageButton)findViewById(R.id.ib_play);
        mPlayIb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mComplete) {
                    mComplete = false;
                    startPlay();
                    mPlayIb.setImageResource(R.drawable.poe_select_video_pause);
                    return;
                }
                if (mVideoLayout.isPlaying()) {
                    if (mVideoLayout.isPaused()) {
                        mPlayIb.setImageResource(R.drawable.poe_select_video_pause);
                        mVideoLayout.resume();
                    } else {
                        mPlayIb.setImageResource(R.drawable.poe_select_video_play);
                        mVideoLayout.pause();
                    }
                }

            }
        });
        mPlayingSb = (SeekBar) findViewById(R.id.sb_playing);
        mCurrentTv = (TextView) findViewById(R.id.tv_current);
        mDurationTv = (TextView) findViewById(R.id.tv_duration);
        mVideoLayout.setOnPlayingListener(new OnPlayingListener() {
            @Override
            public void onPlaying() {
                mPlayIb.setImageResource(R.drawable.poe_select_video_pause);
            }
        });

        mVideoLayout.setOnDurationChangeListener(new BnVideoView2.OnBNDurationChangeListener() {
            @Override
            public void onDurationUpdate(int duration) {
                Cog.e(TAG,"onDurationUpdate total=", duration );
                mDuration = duration;
                setDuration(duration);
            }
        });

        mVideoLayout.setOnBufferUpdateListener(new OnBNBufferUpdateListener() {
            @Override
            public void onBufferUpdate(int position) {
                Cog.d(TAG, "onBufferUpdate position=", position);
                if(position != mCurrentPosition){
                    mCurrentPosition = position;
                    String current = StringUtils.convertTime(mCurrentPosition / 1000);
                    mCurrentTv.setText(current + "/");
                    mPlayingSb.setMax(mDuration);
                    mPlayingSb.setProgress(mCurrentPosition);
                }
            }
        });

        mVideoLayout.setOnCompleteListener(new OnBNCompleteListener() {
            @Override
            public void onComplete() {
                Cog.d(TAG, "onComplete");
                mComplete = true;
                mPlayIb.setImageResource(R.drawable.poe_select_video_play);
            }
        });

        check3GWifi();
        registerWiFiListener();
    }

    private int mDuration;

    private int mCurrentPosition;

    private boolean mComplete;

    /**
     *
     * @param total 单位ms
     */
    public void setDuration(final int total) {
        String duration = StringUtils.convertTime(total / 1000);
        mDurationTv.setText(duration);
    }

    //检测是否为3G网络
    private void check3GWifi() {
        Check3GUtil.instance().CheckNetType(this, new Check3GUtil.OnWifiListener() {
            @Override
            public void onNetError() {
                Cog.e(TAG, "check3GWifi onNetError");
            }

            @Override
            public void onContinue() {
                startPlay();
            }
        });

    }

    private void registerWiFiListener() {
        mWiFiBroadCastUtils = new WiFiBroadCastUtils(this,getSupportFragmentManager(), new WiFiBroadCastUtils.PlayStateListener() {
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
        if(!TextUtils.isEmpty(mUrl)){
            Cog.i(TAG, "startPlay url=", mUrl);
            mVideoLayout.setUrl(mUrl,BnVideoView2.BN_URL_TYPE_HTTP);
            mVideoLayout.play(BnVideoView2.BN_PLAY_DEFAULT);
        }
    }

    public void onBackClick(View view) {
        this.finish();
        UIUtils.addExitTranAnim(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mUrl)) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (mVideoLayout != null && !mVideoLayout.isPlaying()) {
                        check3GWifi();
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoLayout != null && mVideoLayout.isPlaying()) {
            mVideoLayout.stop();
        }
    }

    @Override
    protected void onDestroy() {
        //解除广播接收器
        if (null != mWiFiBroadCastUtils) {
            mWiFiBroadCastUtils.destroy();
        }
        super.onDestroy();
        //销毁hide handler线程
        if(null != mAutoHide){
            mAutoHide.destroyView();
        }
    }

    public static void start(Activity activity, String name, String videoUrl) {
        Intent intent = new Intent(activity, HttpVideoPlayerActivity.class);
        intent.putExtra(EXTRA_TITLE, name);
        intent.putExtra(EXTRA_URL, videoUrl);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }
}
