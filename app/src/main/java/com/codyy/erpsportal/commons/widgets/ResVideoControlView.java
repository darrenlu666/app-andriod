package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.interfaces.IFragmentMangerInterface;
import com.codyy.erpsportal.commons.utils.Check3GUtil;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.WiFiBroadCastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 视频播放控制器
 * Created by poe on 2015/7/9.
 * <p/>
 * home按键后自动seek回原来的位置播放 只需要在onPause中调用本view中的onPause()
 * 无需在恢复后手动做任何操作！
 */
public class ResVideoControlView extends RelativeLayout implements AutoHide, Handler.Callback ,IFragmentMangerInterface{
    private String TAG = ResVideoControlView.class.getSimpleName();
    public static final int MSG_WHAT_AUTO_HIDE = 103;//hide the view
    private static final int HIDE_DELAY_MILLIS = 3000;
    public static final int ERROR_VIDEO_TOOL_SHORT = 201;//too short video less than 1000ms .

    @Bind(R.id.imgPlayOfVideoControl)
    ImageButton mPlayIb;

    @Bind(R.id.imgExpandOfVideoControl)
    ImageButton mExpandIb;

    @Bind(R.id.seekBarOfVideoControl)
    SeekBar mSeekBar;

    @Bind(R.id.txtTotalTimeOfVideoControl)
    TextView mTotalTv;

    @Bind(R.id.txtPlayTimeOfVideoControl)
    TextView mCurrentTv;

    @Bind(R.id.tv_clarity)
    TextView mClarityTv;

    private BnVideoView2.OnBNErrorListener mOnErrorListener;
    private BnVideoView2.OnBNDurationChangeListener mOnDurationChangeListener;
    private BnVideoView2.OnBNBufferUpdateListener mOnBufferUpdateListener;
    private BnVideoView2.OnBNCompleteListener mOnCompleteListener;
    private BnVideoView2.OnPlayingListener mOnPlayingListener;
    private BnVideoView2.OnSurfaceChangeListener mOnSurfaceChangeListener;
    private ExpandListener mExpandListener;
    private PlayState state = PlayState.STOP;

    private boolean mLandscape;

    private int mSwitchPos = -1;

    @Override
    public FragmentManager getNewFragmentManager() {
        return mFragmentManagerInterface==null?null:mFragmentManagerInterface.getNewFragmentManager();
    }

    public enum PlayState {STOP, PLAY, PAUSE}

    private BnVideoView2 mVideoView = null;
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
    private DisplayListener mDisplayListener;
    private WiFiBroadCastUtils mWifiBroadCastUtil;//Wi-Fi监测
    private boolean mIsLocal = false;
    private Handler mHandler;//主线程的handler
    private boolean mSurfaceDestroy = false;//是否调用过surfaceDestroyed .
    private boolean mPaused = false;//是否突然被赞听过.
    private long mStartPlayTime = -1;
    private boolean mIsExpandable = true;//是否支持横竖屏 default：true
    private IFragmentMangerInterface mFragmentManagerInterface;

    private boolean mIsComplete;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_WHAT_AUTO_HIDE://hide control view
                if (!isOnTouch && state == PlayState.PLAY) {
                    hideControl();
                }
                break;
            case ERROR_VIDEO_TOOL_SHORT://error too short video
                ToastUtil.showToast(getResources().getString(R.string.tips_video_too_short));
                break;
        }
        return false;
    }


    public ResVideoControlView(Context context) {
        this(context, null);
    }

    public ResVideoControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResVideoControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ResVideoControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.res_video_control_view, this, true);
        ButterKnife.bind(rootView);
        mHandler = new Handler(this);
        if (!this.isInEditMode()) {
            mWifiBroadCastUtil = new WiFiBroadCastUtils(new IFragmentMangerInterface() {
                @Override
                public FragmentManager getNewFragmentManager() {
                    return mFragmentManagerInterface ==null?
                            null :mFragmentManagerInterface.getNewFragmentManager();
                }
            }, new WiFiBroadCastUtils.PlayStateListener() {
                @Override
                public void play() {
                    if (!mSurfaceDestroy) start();
                }

                @Override
                public void stop() {
                    ResVideoControlView.this.stop();
                    //网络问题引起的重播，应该从0开始
                    mLastPercent = 0;
                }
            });
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Cog.e(TAG, "onFinishInflate()");
        setVisibility(GONE);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (null != mVideoView && !TextUtils.isEmpty(urlPath)) {
                    if (fromUser) {
                        mDestPos = progress;
                        Cog.e("progress::", "^^^^^^^^^" + progress + " Max:" + seekBar.getMax());
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Cog.e(TAG, "onStartrackingTouch()~");
                isOnTouch = true;
                pause();
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                Cog.e(TAG, "onStopTrackingTouch()~");
                isOnTouch = false;
                //update the play progress text .
                if(getVisibility() == VISIBLE){
                    setTextProgress(StringUtils.convertTime(convertTimeRound(mDestPos)));
                }

                if (mIsLocal) {
                    resume();
                    mVideoView.seekTo(mDestPos);
                    mLastPercent = mCurrentPosition = mDestPos;
                } else {
                    Check3GUtil.instance().CheckNetType(ResVideoControlView.this, new Check3GUtil.OnWifiListener() {
                        @Override
                        public void onNetError() { }

                        @Override
                        public void onContinue() {
                            resume();
                            mVideoView.seekTo(mDestPos);
                            mLastPercent = mCurrentPosition = mDestPos;
                        }
                    });
                }
            }
        });

        mPlayIb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != mVideoView) {
                    Cog.i(TAG,"playButton clicked !");
                    if (state == PlayState.STOP || state == PlayState.PAUSE) {
                        if (mIsLocal) {
                            if (state == PlayState.STOP) {
                                start();
                            } else {
                                resume();
                            }
                        } else {
                            Check3GUtil.instance().CheckNetType(ResVideoControlView.this, new Check3GUtil.OnWifiListener() {
                                @Override
                                public void onNetError() {
                                    Cog.e(TAG,"nett error !");
                                }

                                @Override
                                public void onContinue() {
                                    if (state == PlayState.STOP) {
                                        start();
                                    } else {
                                        resume();
                                    }
                                }
                            });
                        }
                    } else if (state == PlayState.PLAY) {
                        pause();
                    }
                }
            }
        });

        mExpandIb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsExpandable) {
                    allowHide();
                    Activity activity = (Activity) getContext();
                    if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        UIUtils.setLandscape(activity);
                        if (null != mExpandListener) mExpandListener.expand();
                    } else if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        UIUtils.setPortrait(activity);
                        if (null != mExpandListener) mExpandListener.collapse();
                    }
                }
            }
        });

        showControl();
    }


    //延时消失
    @Override
    public void touchControl() {
        Cog.e(TAG, " touchControl()~");
        mHandler.removeMessages(MSG_WHAT_AUTO_HIDE);
        mHandler.sendEmptyMessageDelayed(MSG_WHAT_AUTO_HIDE, HIDE_DELAY_MILLIS);
    }

    @Override
    public void destroyView() { }

    @Override
    public void showControl() {
        Cog.e(TAG, "showControl()~");
        if (getVisibility() == View.GONE) {
            setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.abc_fade_in);
            startAnimation(anim);

            if (null != mDisplayListener) {
                mDisplayListener.show();
            }

            if (state == PlayState.PAUSE) {
                setProgress(mLastPercent);
            } else if (state == PlayState.PLAY) {
                setProgress(mCurrentPosition);
                touchControl();
            } else if (state == PlayState.STOP) {
                setProgress(mCurrentPosition);
            }
        }
    }

    @Override
    public void hideControl() {
        Cog.e(TAG, "hideControl");
        setVisibility(View.GONE);

        if (null != mDisplayListener) {
            mDisplayListener.hide();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mVideoView) {
            mVideoView = null;
        }

        //解除广播接收器
        if (null != mWifiBroadCastUtil) {
            mWifiBroadCastUtil.destroy();
        }
    }

    public void setDisplayListener(DisplayListener mDisplayListener) {
        this.mDisplayListener = mDisplayListener;
    }

    /**
     * 设置进度条
     *
     * @param current
     * @param progress
     */
    private void setProgress(String current, int progress) {
        setTextProgress(current);
        mSeekBar.setMax(mTotal);
        mSeekBar.setProgress(progress);
    }

    private void setTextProgress(String current) {
        mCurrentTv.setText(current);
    }

    /**
     * bind BnVideoLayout2 , set listener.
     * you should invoked this method before {@link #setVideoPath(String, int, boolean)}
     * @param videoLayout2
     */
    public void bindVideoView(@NonNull BnVideoLayout2 videoLayout2, IFragmentMangerInterface manager) {
        Cog.d(TAG,"ifragment manager interface :"+manager);
        this.bindVideoView(videoLayout2.getVideoView(),manager);
        //setOnErrorListener will be instead by the bindVideoView method , so you need
        setOnPlayingListener(videoLayout2);
        setOnErrorListener(videoLayout2);
    }

    /**
     * bind surfaceView , set listener .
     * you should invoked this method before {@link #setVideoPath(String, int, boolean)}
     * @param view
     */
    public void bindVideoView(@NonNull BnVideoView2 view, IFragmentMangerInterface manager) {
        this.mVideoView = view;
        this.mFragmentManagerInterface = manager;
        mVideoView.setVolume(100);
        initListener();
    }

    private void initListener() {
        mVideoView.setOnErrorListener(new BnVideoView2.OnBNErrorListener() {
            @Override
            public void onError(int errorCode, String errorMsg) {
                Cog.e(TAG, "onError:errorCode=", errorCode, ",errorMsg=", errorMsg);
                //播放出错后，防止上一次拖动位置没有重置导致进度条不更新问题 .
                if (errorCode == -2 || 0 == errorCode) {
                    mLastPercent = 0;
                } else if (errorCode == -1) {//不支持硬解，改为软解
                    mLastPercent = 0;
                } else if (9 == errorCode) {//9 no playable stream .
                    mLastPercent = 0;
                }

                if(null != mOnErrorListener) mOnErrorListener.onError(errorCode,errorMsg);
            }
        });
        mVideoView.setOnDurationChangeListener(new BnVideoView2.OnBNDurationChangeListener() {
            @Override
            public void onDurationUpdate(int duration) {
                Cog.e(TAG, " total : " + duration);
                if (mTotal != duration) {
                    Cog.e(TAG, " total changed from : " + mTotal + " to :" + duration);
                    mTotal = duration;
                    setDuration(duration);
                    if (null != mOnDurationChangeListener)
                        mOnDurationChangeListener.onDurationUpdate(duration);
                }
            }
        });

        mVideoView.setOnBufferUpdateListener(new BnVideoView2.OnBNBufferUpdateListener() {
            @Override
            public void onBufferUpdate(int position) {
//                Cog.i(TAG, "pos : " + position);
                //判断上次拖动后是否出现了抖动，抖动频率低于700ms不做进度更新.
                if(position>mLastPercent){
                    setProgress(position);
                }else if(position == mTotal){
                    setProgress(position);
                }
                if (mCurrentPosition != position) {
                    mCurrentPosition = position;
                }

                if (null != mOnBufferUpdateListener)
                    mOnBufferUpdateListener.onBufferUpdate(position);
            }
        });

        mVideoView.setOnCompleteListener(new BnVideoView2.OnBNCompleteListener() {
            @Override
            public void onComplete() {
                Cog.e("----------dd------------------", "onComplete");
                stop();
                mIsComplete = true;
                mLastPercent = 0;//恢复为0否则，在此点击播放按钮会被seek到上一次seek的位置 .
                long nowTime = System.currentTimeMillis();
                if ((nowTime - mStartPlayTime) < 1000) {
                    mHandler.sendEmptyMessage(ERROR_VIDEO_TOOL_SHORT);
                }

                if (mOnCompleteListener != null) {
                    Cog.e(TAG, "---------------------------- onComplete involved !");
                    mOnCompleteListener.onComplete();
                }
            }
        });

        mVideoView.setOnPlayingListener(new BnVideoView2.OnPlayingListener() {
            @Override
            public void onPlaying() {
                Cog.e(TAG, "onPlaying()~~~~~~~~~~~~~~~~mLastPercent" + mLastPercent + " mcurrent:" + mCurrentPosition);
                setPlaySate();
                if (Math.abs(mLastPercent - mCurrentPosition) >= 1000) {//防止拖动seekbar后两者差值在1000ms内引起的无限seek .
                    Cog.e(TAG, "onPlaying()~~~~~~~~~~~~~~~~seek : mLastPercent " + mLastPercent + " mcurrent:" + mCurrentPosition);
                    mVideoView.seekTo(mLastPercent);
                    mCurrentPosition = mLastPercent;
                }

                if (mSwitchPos > 0 && !mIsComplete) {
                    mVideoView.seekTo(mSwitchPos);
                    mSwitchPos = -1;
                }

                if (null != mOnPlayingListener) {
                    mOnPlayingListener.onPlaying();
                }
                mIsComplete = false;
            }
        });

        mVideoView.setOnSurfaceChangeListener(new BnVideoView2.OnSurfaceChangeListener() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Cog.e(TAG, "surfaceCreated()~~~~~~~~~~~~~~~~mLastPercent" + mLastPercent);
                mSurfaceDestroy = false;
                if (mVideoView != null && !TextUtils.isEmpty(urlPath)) {
                    if (mIsLocal) {
                        start();
                    } else {
                        Check3GUtil.instance().CheckNetType(ResVideoControlView.this, new Check3GUtil.OnWifiListener() {
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

                if (null != mOnSurfaceChangeListener)
                    mOnSurfaceChangeListener.surfaceCreated(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Cog.e(TAG, "surfaceDestroyed()~~~~~~~~~~~~~~~~");
                mSurfaceDestroy = true;
                stop();
                mVideoView.close();
                if (null != mOnSurfaceChangeListener)
                    mOnSurfaceChangeListener.surfaceDestroyed(holder);
            }
        });

        //设置点击事件
        mVideoView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showControl();
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
            Check3GUtil.instance().CheckNetType(ResVideoControlView.this, new Check3GUtil.OnWifiListener() {
                @Override
                public void onNetError() { }

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
            if (state == PlayState.PAUSE) {
                resume();
                mVideoView.stop();
            } else if (state == PlayState.PLAY) {
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
        mPaused = true;
        int pos = mCurrentPosition;
        stop();
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
        if(null != mVideoView)
        mVideoView.stop();
    }

    public void initConfig() {
        Cog.e(TAG, "+initConfig " + mLastPercent);
        mLastPercent = 0;
        mCurrentPosition = 0;
        mTotal = 100;
        isOnTouch = false;
        showControl();
        setDuration(mTotal);//进度恢复
        setProgress(mCurrentPosition);//进度条恢复
        state = PlayState.STOP;
    }

    /**
     * be carefully : please make sure mDestroyView is not set true before invoked this method .
     * 开始播放
     */
    public void start() {
        Cog.e(TAG, "+start " + urlPath + " mLastper:" + mLastPercent);
        if (mVideoView == null) return;//实测有null情况
        if (!TextUtils.isEmpty(urlPath)) {
            mVideoView.setUrl(urlPath, mURLType);
            mVideoView.play(BnVideoView2.BN_PLAY_DEFAULT);
            if (mLastPercent > 0 ) {
                if (mPaused) {//paused by some action .
                    mPaused = false;
                    mVideoView.seekTo(mLastPercent);
                }
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
        state = PlayState.PLAY;
        touchControl();
        updatePlayBtnIcon();
    }

    private void setStopState() {
        state = PlayState.STOP;
        disallowHide();
        updatePlayBtnIcon();
    }

    /**
     * 记录暂停状态
     * 将当前的播放位置记录到mLastPercent.
     * 当前的播放位置值空.
     */
    private void setPauseState() {
        //记录当前的位置，方便resume后seek .
        mLastPercent = mCurrentPosition;
        //为什么要设置为0? 去除本行解决seek后2次重复seek的问题
//        mCurrentPosition = 0;
        state = PlayState.PAUSE;
        disallowHide();
        updatePlayBtnIcon();
    }

    /**
     * 根据状态更新播放按钮
     */
    private void updatePlayBtnIcon() {
        if (state == PlayState.PLAY) {
            if (mLandscape) {
                mPlayIb.setImageResource(R.drawable.ic_pause_fs);
            } else {
                mPlayIb.setImageResource(R.drawable.poe_select_video_pause);
            }
        } else {//state == PlayState.STOP || state == PlayState.PAUSE
            if (mLandscape) {
                mPlayIb.setImageResource(R.drawable.ic_play_fs);
            } else {
                mPlayIb.setImageResource(R.drawable.poe_select_video_play);
            }
        }
    }

    private void resume() {
        Cog.e(TAG, "resume()~mLastPercent : " + mLastPercent);
        if (getPlayState() == PlayState.PAUSE) {
            setPlaySate();
            mVideoView.resume();
        } else {
            start();
        }
    }

    public PlayState getPlayState() {
        return state;
    }

    /**
     * @param total 单位ms
     */
    public void setDuration(final int total) {
        String sTotal = StringUtils.convertTime(convertTimeRound(total));
        mTotalTv.setText(sTotal);
    }

    /**
     * 设置进度条
     *
     * @param currentPosition 当前进度
     */
    public void setProgress(int currentPosition) {
        if (!isOnTouch && getVisibility() == View.VISIBLE) {
            String current = StringUtils.convertTime(convertTimeRound(currentPosition));
            setProgress(current, currentPosition);
        }
    }

    /**
     * 切换流
     * @param playUrl 新流地址
     */
    public void switchClarity(String playUrl) {
        mSwitchPos = mCurrentPosition;
        setVideoPath( playUrl, BnVideoView2.BN_URL_TYPE_HTTP, false);
    }

    /**
     * 锁定显示状态，禁止自动隐藏播放条
     */
    public void disallowHide() {
        mHandler.removeMessages(MSG_WHAT_AUTO_HIDE);
    }

    /**
     * 允许自动显示播放条
     */
    public void allowHide() {
        if (state == PlayState.PLAY) {
            mHandler.removeMessages(MSG_WHAT_AUTO_HIDE);
            mHandler.sendEmptyMessageDelayed(MSG_WHAT_AUTO_HIDE, HIDE_DELAY_MILLIS);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Cog.d(TAG, "onConfigurationChanged LANDSCAPE");
            mLandscape = true;
            mExpandIb.setVisibility(INVISIBLE);
            mClarityTv.setVisibility(VISIBLE);
//            bottom2Center(mCurrentTv, false);
//            bottom2Center(mTotalTv, false);
            mSeekBar.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.sb_res_video_landscape));
            mSeekBar.setThumb(ContextCompat.getDrawable(getContext(), R.drawable.tb_position));
//            moveSbBetween(mCurrentTv, mTotalTv);
        } else {
            Cog.d(TAG, "onConfigurationChanged PORTRAIT");
            mLandscape = false;
            mExpandIb.setVisibility(VISIBLE);
            mClarityTv.setVisibility(INVISIBLE);
//            bottom2Center(mCurrentTv, true);
//            bottom2Center(mTotalTv, true);
            mSeekBar.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.po_seekbar));
            mSeekBar.setThumb(ContextCompat.getDrawable(getContext(), R.drawable.poe_circle_bg));
//            moveSbBetween(mPlayIb, mExpandIb);
        }
        updatePlayBtnIcon();
    }

    /**
     * 将进度条移到两个组件之间
     * @param leftView 左边的组件
     * @param rightView 右边的组件
     */
    private void moveSbBetween(View leftView, View rightView) {
        LayoutParams layoutParams = (LayoutParams) mSeekBar.getLayoutParams();
        layoutParams.addRule(RIGHT_OF, leftView.getId());
        layoutParams.addRule(LEFT_OF, rightView.getId());
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.addRule(END_OF, leftView.getId());
            layoutParams.addRule(START_OF, rightView.getId());
        }
        mSeekBar.setLayoutParams(layoutParams);
    }

    /**
     * 将组件从底部移到居中
     * @param view 组件
     * @param reverse 反向，将组件从居中移到底部
     */
    private void bottom2Center(View view, boolean reverse) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (reverse) {
            layoutParams.addRule(CENTER_VERTICAL, 0);
            layoutParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);
        } else {
            layoutParams.addRule(CENTER_VERTICAL, TRUE);
            layoutParams.addRule(ALIGN_PARENT_BOTTOM, 0);
        }
        view.setLayoutParams(layoutParams);
    }

    /**
     * 四舍五入传入的数据 ms -> s .
     * 59500 -> 60
     * 59300 -> 59
     * @param position
     * @return
     */
    private int convertTimeRound(int position){
        int now = Math.round((float)position / 1000);
        Cog.i(TAG,"convertTimeRound ~ progress : "+ now);
        return now;
    }

    public boolean isPlaying() {
        return null != mVideoView && mVideoView.isPlaying();
    }


    public void setOnErrorListener(BnVideoView2.OnBNErrorListener errorListener) {
        mOnErrorListener = errorListener;
    }

    public void setOnDurationChangeListener(BnVideoView2.OnBNDurationChangeListener durationChangeListener) {
        this.mOnDurationChangeListener = durationChangeListener;
    }

    public void setOnBufferUpdateListener(BnVideoView2.OnBNBufferUpdateListener bufferUpdateListener) {
        this.mOnBufferUpdateListener = bufferUpdateListener;
    }

    public void setOnCompleteListener(BnVideoView2.OnBNCompleteListener completeListener) {
        this.mOnCompleteListener = completeListener;
    }

    public void setOnPlayingListener(BnVideoView2.OnPlayingListener onPlayingListener) {
        this.mOnPlayingListener = onPlayingListener;
    }

    public void setOnSurfaceChangeListener(BnVideoView2.OnSurfaceChangeListener surfaceChangeListener) {
        this.mOnSurfaceChangeListener = surfaceChangeListener;
    }

    public boolean isExpandable() {
        return mIsExpandable;
    }

    public void setExpandable(boolean isExpandable) {
        this.mIsExpandable = isExpandable;
        if (!mIsExpandable) {
            mExpandIb.setVisibility(INVISIBLE);
        } else {
            mExpandIb.setVisibility(VISIBLE);
        }
    }

    public ExpandListener getExpandListener() {
        return mExpandListener;
    }

    public void setExpandListener(ExpandListener expandListener) {
        this.mExpandListener = expandListener;
    }

    public void setOnClarityClickListener(OnClickListener onClickListener) {
        mClarityTv.setOnClickListener(onClickListener);
    }

    /**
     * 监听本控件的隐藏状态
     */
    public interface DisplayListener {
        /**
         * control view show on screen
         */
        void show();

        /**
         * I am hide
         */
        void hide();
    }

    /**
     * 横竖屏监听
     */
    public interface ExpandListener {
        /**
         * 点击了 Expand 图标
         */
        void expand();

        /**
         * 回到竖屏
         */
        void collapse();
    }
}
