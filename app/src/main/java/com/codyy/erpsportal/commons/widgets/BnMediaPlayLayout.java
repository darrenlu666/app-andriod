package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.codyy.bennu.sdk.BNMediaPlayer;
import com.codyy.bennu.sdk.impl.BNAudioMixer;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.interfaces.IFragmentMangerInterface;
import com.codyy.erpsportal.commons.utils.Check3GUtil;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.WiFiBroadCastUtils;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 博文-播放器
 * 支持普通点播视频播放
 * 支持不显示视频的普通音频播放
 * 自带状态设置!
 * created by poe 2016/06/14.
 */
public class BnMediaPlayLayout extends RelativeLayout implements BnVideoView2.OnPlayingListener, BnVideoView2.OnBNErrorListener , Handler.Callback,IFragmentMangerInterface {
    private static final String TAG = BnMediaPlayLayout.class.getSimpleName();
    public static final int MEDIA_TYPE_VIDEO = 0x001;//视频点播
    public static final int MEDIA_TYPE_AUDIO = 0x002;//普通音频文件播放

    @Bind(R.id.imgPlayOfVideoControl)ImageButton mPlayImageButton;
    @Bind(R.id.imgExpandOfVideoControl)ImageButton mExpandImageButton;
    @Bind(R.id.seekBarOfVideoControl)SeekBar mSeekBar;
    @Bind(R.id.txtTotalTimeOfVideoControl)TextView mTotalTextView;

    @Bind(R.id.hintText) TextView mHintTv;
    private BnVideoView2.OnBNErrorListener mOnErrorListener;
    private BnVideoView2.OnPlayingListener mOnPlayingListener;
    @Bind(R.id.bnVideoView) BnVideoView2 mVideoView = null;
    private int mTotal = 100;
    /**
     * 记录上次播放的位置，但是End时候需要清空
     * 拖动seek的位置
     */
    private int mLastPercent = -1;
    private String urlPath;
    private int mURLType = BnVideoView2.BN_URL_TYPE_HTTP;//default :http play
    private boolean isOnTouch = false;//判断 seek是否手动滑动了
    private int mDestPos = 0;
    private int mCurrentPosition = 0;
    private BNVideoControlView.DisplayListener mDisplayListener;
    private WiFiBroadCastUtils mWifiBroadCastUtil;//Wi-Fi监测
    private boolean mIsLocal = false;
    private Handler mHandler;//主线程的handler
    private boolean mDestroy = false;//是否调用过surfaceDestroyed .
    private long mStartPlayTime = -1;
    private boolean mIsExpandable = true ;//是否支持横竖屏 default：true
    private BNVideoControlView.PlaySate state = BNVideoControlView.PlaySate.STOP;
    private IFragmentMangerInterface mFragmentManagerInterface ;

    private int obtainLayoutId(){
        return  R.layout.bn_media_layout ;
    }

    public BnMediaPlayLayout(Context context) {
        this(context, null);
    }

    public BnMediaPlayLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BnMediaPlayLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BnMediaPlayLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs){
        View rootView = LayoutInflater.from(getContext()).inflate(obtainLayoutId(), this, true);
        ButterKnife.bind(rootView);
        if (!this.isInEditMode()) {
            mWifiBroadCastUtil = new WiFiBroadCastUtils(mFragmentManagerInterface, new WiFiBroadCastUtils.PlayStateListener() {
                @Override
                public void play() {
                    start();
                }

                @Override
                public void stop() {
                    BnMediaPlayLayout.this.stop();
                }
            });
        }
    }

    public void setmFragmentManagerInterface(IFragmentMangerInterface mFragmentManager) {
        this.mFragmentManagerInterface = mFragmentManager;
    }

    /**
     * 获取播放的实体类.
     * @return
     */
    public BNMediaPlayer getPlayer() {
        if(null != mVideoView)
            return  mVideoView.getPlayer();
        return null;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Cog.e(TAG, "onFinishInflate()");
        mVideoView.setOnPlayingListener(this);
        mVideoView.setOnErrorListener(this);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (null != mVideoView && !TextUtils.isEmpty(urlPath)) {
                    if (fromUser) {
                        mDestPos = progress;
                        Cog.e("progress Changed by hand :", "^^^^^^^^^" + progress + " Max:" + seekBar.getMax());
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Cog.e(TAG, "onStartrackingTouch()~");
                isOnTouch = true;
//                pause();
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                Cog.i(TAG, "onStopTrackingTouch ()~" + mDestPos);
                isOnTouch = false;
                //防止播放完成之后记录的位置得不到更新
                if (mLastPercent != mDestPos) {
                    mLastPercent = mDestPos;
                }
                if (mIsLocal) {
                    Cog.i(TAG, "seek to ()~" + mDestPos);
                    resume();
                    mVideoView.seekTo(mDestPos);
                } else {
                    Check3GUtil.instance().CheckNetType(BnMediaPlayLayout.this, new Check3GUtil.OnWifiListener() {
                        @Override
                        public void onNetError() {
                        }

                        @Override
                        public void onContinue() {
                            Cog.i(TAG, "seek to ()~" + mDestPos);
                            resume();
                            mVideoView.seekTo(mDestPos);
                        }
                    });
                }
            }
        });

        mPlayImageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != mVideoView) {
                    if (state == BNVideoControlView.PlaySate.STOP || state == BNVideoControlView.PlaySate.PAUSE) {
                        if (mIsLocal) {
                            if (state == BNVideoControlView.PlaySate.STOP) {
                                start();
                            } else {
                                resume();
                            }
                        } else {
                            Check3GUtil.instance().CheckNetType(BnMediaPlayLayout.this, new Check3GUtil.OnWifiListener() {
                                @Override
                                public void onNetError() {
                                }
                                @Override
                                public void onContinue() {
                                    if (state == BNVideoControlView.PlaySate.STOP) {
                                        start();
                                    } else {
                                        resume();
                                    }
                                }
                            });
                        }
                    } else if (state == BNVideoControlView.PlaySate.PLAY) {
                        pause();
                    }
                }
            }
        });
        //设置音量
        mVideoView.setVolume(100);
    }

    @Override
    public void onPlaying() {
        post(new Runnable() {
            @Override
            public void run() {
                mHintTv.setVisibility(INVISIBLE);
                mVideoView.requestFocus();
            }
        });
        if (mOnPlayingListener != null) mOnPlayingListener.onPlaying();
    }

    @Override
    public void onError(int errorCode , String errorMsg) {
        if (errorCode == -2 || 0 == errorCode) {
            post(new Runnable() {
                @Override
                public void run() {
                    mHintTv.setText(R.string.txt_video_meeting_no_input_stream);
                    mHintTv.setVisibility(VISIBLE);
                }
            });
        } else if( errorCode == -1){//不支持硬解，改为软解
            mVideoView.setEncodeType(BnVideoView2.BN_ENCODE_SOFTWARE);
        }

        if(null != mOnErrorListener) {
            mOnErrorListener.onError(errorCode,errorMsg);
        }
    }

    public BnVideoView2 getVideoView() {
        return mVideoView;
    }

    /**
     * @param videoUrl
     * @param urlType
     */
    public void setUrl(String videoUrl,int urlType) {
        if (mVideoView.setUrl(videoUrl,urlType)) {
            post(new Runnable() {
                @Override
                public void run() {
                    mHintTv.setText(R.string.loading);
                    mHintTv.setVisibility(VISIBLE);
                }
            });
        }
    }

    public boolean isPlaying() {
        return mVideoView.isPlaying();
    }

    public boolean isUrlEmpty() {
        return mVideoView.isUrlEmpty();
    }

    /**
     * @param playType 0:play() 1:playWithChat() 2:audio mix
     */
    public void play(int playType) {
        mVideoView.play(playType);
    }

    public void setVolume(int i) {
        mVideoView.setVolume(i);
    }

    /**
     * 设置混音器
     * @param mixer
     */
    public void setAudioMixer(BNAudioMixer mixer){
        mVideoView.setAudioMixer(mixer);
    }

    /**
     * 开始之后调用...
     * @param timeOut
     */
    public void setTimeOut(int timeOut){
        mVideoView.setTimeOut(timeOut);
    }


    public void setPlayingListener(BnVideoView2.OnPlayingListener onPlayingListener) {
        this.mOnPlayingListener = onPlayingListener;
    }

    public boolean isPaused() {
        return mVideoView.isPause();
    }

    public void close(){
        Cog.e(TAG,"close ~");
        mVideoView.close();
    }

    public void setEncodeType(int encodeType){
        mVideoView.setEncodeType(encodeType);
    }

    public SurfaceHolder getSurfaceHold() {
        return mVideoView.getSurfaceHold();
    }

    public void setOnErrorListener(BnVideoView2.OnBNErrorListener errorListener) {
        this.mOnErrorListener = errorListener;
    }

    public void setOnDurationChangeListener(BnVideoView2.OnBNDurationChangeListener durationChangeListener) {
        mVideoView.setOnDurationChangeListener(durationChangeListener);
    }

    public void setOnBufferUpdateListener(BnVideoView2.OnBNBufferUpdateListener bufferUpdateListener) {
        mVideoView.setOnBufferUpdateListener(bufferUpdateListener);
    }

    public void setOnCompleteListener(BnVideoView2.OnBNCompleteListener completeListener) {
        mVideoView.setOnCompleteListener(completeListener);
    }

    public void setOnPlayingListener(BnVideoView2.OnPlayingListener onPlayingListener) {
        this.mOnPlayingListener = onPlayingListener;
    }

    public void setOnSurfaceChangeListener(BnVideoView2.OnSurfaceChangeListener surfaceChangeListener) {
        mVideoView.setOnSurfaceChangeListener(surfaceChangeListener);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case BNVideoControlView.MSG_WHAT_AUTO_HIDE://hide control view
                if (!isOnTouch) {
//                    hideControl();
                }
                break;
            case BNVideoControlView.ERROR_VIDEO_TOOL_SHORT://error too short video
                ToastUtil.showToast(getResources().getString(R.string.tips_video_too_short));
                break;
        }
        return false;
    }

    /**
     * 设置进度条
     *
     * @param current
     * @param progress
     */
    private void setProgress(String current, int progress) {
        mSeekBar.setMax(mTotal);
        mSeekBar.setProgress(progress);
    }

    public void initListener() {
        mVideoView.setOnDurationChangeListener(new BnVideoView2.OnBNDurationChangeListener() {
            @Override
            public void onDurationUpdate(int duration) {
                Cog.e(TAG, " total : " + duration);
                mTotal = duration;
                setDuration(duration);
            }
        });
        mVideoView.setOnBufferUpdateListener(new BnVideoView2.OnBNBufferUpdateListener() {
            @Override
            public void onBufferUpdate(int position) {
                Cog.i(TAG, "pos : " + position);
                if (position != mCurrentPosition) {
                    mCurrentPosition = position;
                    setProgress(position);
                }
            }
        });

        mVideoView.setOnCompleteListener(new BnVideoView2.OnBNCompleteListener() {
            @Override
            public void onComplete() {
                Cog.e("----------dd------------------", "onComplete");
                stop();
                mLastPercent = 0;//恢复为0否则，在此点击播放按钮会被seek到上一次seek的位置 .
                long nowTime = System.currentTimeMillis();
                if ((nowTime - mStartPlayTime) < 1 * 1000) {
                    mHandler.sendEmptyMessage(BNVideoControlView.ERROR_VIDEO_TOOL_SHORT);
                }
            }
        });

        mVideoView.setOnSurfaceChangeListener(new BnVideoView2.OnSurfaceChangeListener() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Cog.e(TAG, "surfaceCreated()~~~~~~~~~~~~~~~~mLastPercent" + mLastPercent);
                if (mVideoView != null && !TextUtils.isEmpty(urlPath)) {
                    if (mIsLocal) {
                        start();
                    } else {
                        Check3GUtil.instance().CheckNetType(BnMediaPlayLayout.this, new Check3GUtil.OnWifiListener() {
                            @Override
                            public void onContinue() {
                                start();
                            }

                            @Override
                            public void onNetError() {
                                // has no network...

                            }
                        });
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Cog.e(TAG, "surfaceDestroyed()~~~~~~~~~~~~~~~~");
                mDestroy = true;
                stop();
                mVideoView.close();
            }
        });
        //设置点击事件
        mVideoView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mVideoView.isPause()){
                    resume();
                }else if(mVideoView.isPlaying()){
                    pause();
                }else{
                    start();
                }
                return false;
            }
        });
    }

    /**
     * 设置播放路径 并播放
     *
     * @param path
     * @param urlType 视频地址类型 {@link BnVideoView2}
     * @param isLocal 是否是本地文件
     */
    public void setVideoPath(String path, int urlType, boolean isLocal) {
        initConfig();
        initListener();
        this.mURLType = urlType;
        this.mIsLocal = isLocal;
        Cog.d("url", "check it : " + path);
        if (null != urlPath && this.urlPath.equals(path)) {
            Cog.e(TAG, "the same url is give :" + path);
        } else {
            this.urlPath = path;
            tryEndLastVideo();
        }

        if (!isLocal) {
            Check3GUtil.instance().CheckNetType(BnMediaPlayLayout.this, new Check3GUtil.OnWifiListener() {
                @Override
                public void onNetError() {
                }

                @Override
                public void onContinue() {
                    start();
                }
            });
        } else {
            start();
        }

        mStartPlayTime = System.currentTimeMillis();
    }

    private void tryEndLastVideo() {
        if (null != mVideoView) {
            if (state == BNVideoControlView.PlaySate.PAUSE) {
                resume();
                mVideoView.stop();
            } else if (state == BNVideoControlView.PlaySate.PLAY) {
                stop();
            }
        }
    }


    /**
     * 按住home键执行暂停操作，记住当前位置 等待seek .
     * HOME - >
     */
    public void onPause() {
        Cog.e(TAG, "+onPause start... " + mLastPercent);
        int pos = mCurrentPosition;
        stop();
        initConfig();
        mLastPercent = pos;
        Cog.e(TAG, "+onPause end... " + mLastPercent);
    }

    /**
     * 停止播放
     */
    public void stop() {
        Cog.d("----------------------------:", "stop()");
        Cog.e(TAG, "+stop " + mLastPercent);
        setStopState();
        mVideoView.stop();
    }

    public void initConfig() {
        Cog.e(TAG, "+initConfig " + mLastPercent);
        mLastPercent = 0;
        mCurrentPosition = 0;
        mTotal = 100;
        isOnTouch = false;
//        showControl();
        setDuration(mTotal);//进度恢复
        setProgress(mCurrentPosition);//进度条恢复
        state = BNVideoControlView.PlaySate.STOP;
    }

    /**
     * 开始播放
     */
    public void start() {
        Cog.e(TAG, "+start " + urlPath + " mLastper:" + mLastPercent);
        if (!TextUtils.isEmpty(urlPath)) {
            mVideoView.setUrl(urlPath, mURLType);
            mVideoView.play(BnVideoView2.BN_PLAY_DEFAULT);
            if (mLastPercent > 0 && mDestroy) {
                mDestroy = false;
                mVideoView.seekTo(mLastPercent);
            }
            setPlaySate();
        }

        if (null != mDisplayListener) {
            mDisplayListener.show();
        }
    }

    /**
     * 页面失去焦点后 强制暂停
     */
    private void pause() {
        Cog.e(TAG, "pause()~");
        if (mVideoView.isPlaying()) {
            setPauseState();
            mVideoView.pause();
        }
    }

    private void setPlaySate() {
        state = BNVideoControlView.PlaySate.PLAY;
        mPlayImageButton.setImageResource(R.drawable.poe_select_video_pause);
    }

    private void setStopState() {
        state = BNVideoControlView.PlaySate.STOP;
        mPlayImageButton.setImageResource(R.drawable.poe_select_video_play);
    }

    private void setPauseState() {
        //记录当前的位置，方便resume后seek .
        mLastPercent = mCurrentPosition;
        mCurrentPosition = 0;
        state = BNVideoControlView.PlaySate.PAUSE;
        mPlayImageButton.setImageResource(R.drawable.poe_select_video_play);
    }

    private void resume() {
        Cog.e(TAG, "resume()~mLastPercent : " + mLastPercent);
        if (getPlayState() == BNVideoControlView.PlaySate.PAUSE) {
            setPlaySate();
            mVideoView.resume();
        } else {
            start();
        }
    }

    public BNVideoControlView.PlaySate getPlayState() {
        return state;
    }

    /**
     * @param total 单位ms
     */
    public void setDuration(final int total) {
        String sTotal = StringUtils.convertTime(total / 1000);
        mTotalTextView.setText(sTotal);
    }

    /**
     * 设置进度条
     * @param currentPosition
     */
    public void setProgress(int currentPosition) {
        if (!isOnTouch && getVisibility() == View.VISIBLE) {
            String current = StringUtils.convertTime(currentPosition / 1000);
            setProgress(current, currentPosition);
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        //解除广播接收器
        if (null != mWifiBroadCastUtil) {
            mWifiBroadCastUtil.destroy();
        }
        //注销实体类引用
        if (null != mVideoView) {
            mVideoView = null;
        }
    }

    @Override
    public FragmentManager getNewFragmentManager() {
        return mFragmentManagerInterface.getNewFragmentManager();
    }
}
