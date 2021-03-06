package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
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
public class BNVideoControlView extends RelativeLayout implements AutoHide, Handler.Callback ,IFragmentMangerInterface{
    private String TAG = BNVideoControlView.class.getSimpleName();
    public static final int MSG_WHAT_NOTIFY_HIDE = 100;// hide thread notify main thread to hide title
    public static final int MSG_WHAT_AUTO_HIDE = 103;//hide the view
    public static final int ERROR_VIDEO_TOOL_SHORT = 201;//too short video less than 1000ms .
    public static final int MODE_LIVING = 1;//直播.
    public static final int MODE_RECORD = 0;//录播.

    @Bind(R.id.imgPlayOfVideoControl)
    ImageButton mPlayImageButton;
    @Bind(R.id.imgExpandOfVideoControl)
    ImageButton mExpandImageButton;
    @Bind(R.id.seekBarOfVideoControl)
    SeekBar mSeekBar;
    @Bind(R.id.txtTotalTimeOfVideoControl)
    TextView mTotalTextView;
    @Bind(R.id.txtPlayTimeOfVideoControl)
    TextView mCurrentTextView;
    private BnVideoView2.OnBNErrorListener mOnErrorListener;
    private BnVideoView2.OnBNDurationChangeListener mOnDurationChangeListener;
    private BnVideoView2.OnBNBufferUpdateListener mOnBufferUpdateListener;
    private BnVideoView2.OnBNCompleteListener mOnCompleteListener;
    private BnVideoView2.OnPlayingListener mOnPlayingListener;
    private BnVideoView2.OnSurfaceChangeListener mOnSurfaceChangeListener;
    private ExpandListener mExpandListener;
    private Context mContext;
    private PlaySate state = PlaySate.STOP;

    @Override
    public FragmentManager getNewFragmentManager() {
        return mFragmentManagerInterface==null?null:mFragmentManagerInterface.getNewFragmentManager();
    }

    public enum PlaySate {STOP, PLAY, PAUSE}

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
    private HandlerThread mHideHandlerThread = new HandlerThread("hide");//控制视图自动隐藏的子线程
    private Handler mHandlerHide;//线程hide的控制handler
    private Handler mHandler;//主线程的handler
    private boolean mSurfaceDestroy = false;//是否调用过surfaceDestroyed .
    private boolean mPaused = false;//是否突然被赞听过.
    private long mStartPlayTime = -1;
    private boolean mIsExpandable = true;//是否支持横竖屏 default：true
    private IFragmentMangerInterface mFragmentManagerInterface;
    private int mPlayMode = MODE_RECORD;//播放模式默认录播 ，如果设置为直播则没有开始/暂停 和 播放进度显示

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_WHAT_AUTO_HIDE://hide control view
                if (!isOnTouch) {
                    hideControl();
                }
                break;
            case ERROR_VIDEO_TOOL_SHORT://error too short video
                ToastUtil.showToast(getResources().getString(R.string.tips_video_too_short));
                break;
        }
        return false;
    }


    public BNVideoControlView(Context context) {
        this(context, null);
    }

    public BNVideoControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BNVideoControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BNVideoControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.BNVideoControlView, defStyle, 0);
        mPlayMode = ta.getInteger(R.styleable.BNVideoControlView_play_mode, MODE_RECORD);
        ta.recycle();

        View rootView = LayoutInflater.from(context).inflate(R.layout.video_control_view, this, true);
        ButterKnife.bind(rootView);
        updateMode();
        mContext = context;
        mHideHandlerThread.start();
        mHandler = new Handler(this);
        mHandlerHide = new Handler(mHideHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (MSG_WHAT_NOTIFY_HIDE == msg.what) {//通知Main UI hide .
                    mHandler.sendEmptyMessage(MSG_WHAT_AUTO_HIDE);
                }
            }
        };
        if (!this.isInEditMode()) {
            mWifiBroadCastUtil = new WiFiBroadCastUtils(new IFragmentMangerInterface() {
                @Override
                public FragmentManager getNewFragmentManager() {
                    return mFragmentManagerInterface ==null?null:mFragmentManagerInterface.getNewFragmentManager();
                }
            }, new WiFiBroadCastUtils.PlayStateListener() {
                @Override
                public void play() {
                    if (!mSurfaceDestroy) start();
                }

                @Override
                public void stop() {
                    BNVideoControlView.this.stop();
                    //网络问题引起的重播，应该从0开始
                    mLastPercent = 0;
                }
            });
        }
    }

    private void updateMode() {
        if (MODE_LIVING == mPlayMode) {
            mPlayImageButton.setVisibility(INVISIBLE);
            mSeekBar.setVisibility(INVISIBLE);
            mCurrentTextView.setVisibility(INVISIBLE);
            mTotalTextView.setVisibility(INVISIBLE);
        }
    }

    public int getPlayMode() {
        return mPlayMode;
    }

    public void setPlayMode(int playMode) {
        this.mPlayMode = playMode;
        updateMode();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Cog.e(TAG, "onFinishInflate()");
        this.setVisibility(GONE);

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
                touchControl();
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
                    Check3GUtil.instance().CheckNetType(BNVideoControlView.this, new Check3GUtil.OnWifiListener() {
                        @Override
                        public void onNetError() {

                        }

                        @Override
                        public void onContinue() {
                            resume();
                            mVideoView.seekTo(mDestPos);
                            mLastPercent = mCurrentPosition = mDestPos;
                        }
                    });
                }
                touchControl();
            }
        });


        mPlayImageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                touchControl();
                if (null != mVideoView) {
                    Cog.i(TAG,"playButton clicked !");
                    if (state == PlaySate.STOP || state == PlaySate.PAUSE) {
                        if (mIsLocal) {
                            //                            start();
                            if (state == PlaySate.STOP) {
                                start();
                            } else {
                                resume();
                            }
                        } else {
                            Check3GUtil.instance().CheckNetType(BNVideoControlView.this, new Check3GUtil.OnWifiListener() {
                                @Override
                                public void onNetError() {
                                    Cog.e(TAG,"nett error !");
                                }

                                @Override
                                public void onContinue() {
                                    if (state == PlaySate.STOP) {
                                        start();
                                    } else {
                                        resume();
                                    }

                                }
                            });
                        }
                    } else if (state == PlaySate.PLAY) {
                        pause();
                    }
                }
            }
        });

        mExpandImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsExpandable) {
                    touchControl();
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
        mHandlerHide.removeMessages(MSG_WHAT_NOTIFY_HIDE);
        mHandlerHide.sendEmptyMessageDelayed(MSG_WHAT_NOTIFY_HIDE, 3 * 1000);
    }

    @Override
    public void destroyView() {

    }

    @Override
    public void showControl() {
        Cog.e(TAG, "showControl()~");
        touchControl();
        if (getVisibility() == View.GONE) {

            this.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_in);
            this.startAnimation(anim);

            if (null != mDisplayListener) {
                mDisplayListener.show();
            }

            if (state == PlaySate.PAUSE) {
                setProgress(mLastPercent);
            } else if (state == PlaySate.PLAY) {
                setProgress(mCurrentPosition);
            } else if (state == PlaySate.STOP) {
                setProgress(mCurrentPosition);
            }
        }

    }

    @Override
    public void hideControl() {
        Cog.e(TAG, "hideControl");
        this.setVisibility(View.GONE);

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

        if (null != mHideHandlerThread && null != mHandler.getLooper()) {
            mHideHandlerThread.getLooper().quit();
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
        mCurrentTextView.setText(current + "/");
    }

    /**
     * bind BnVideoLayout2 , set listener .
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
                Cog.i(TAG, "pos : " + position);
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
                mLastPercent = 0;//恢复为0否则，在此点击播放按钮会被seek到上一次seek的位置 .
                long nowTime = System.currentTimeMillis();
                if ((nowTime - mStartPlayTime) < 1 * 1000) {
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

                if (null != mOnPlayingListener) {
                    mOnPlayingListener.onPlaying();
                }
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
                        Check3GUtil.instance().CheckNetType(BNVideoControlView.this, new Check3GUtil.OnWifiListener() {
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
            public void surfaceChanged(SurfaceHolder holder) {

            }

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
            Check3GUtil.instance().CheckNetType(BNVideoControlView.this, new Check3GUtil.OnWifiListener() {
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
            if (state == PlaySate.PAUSE) {
                resume();
                mVideoView.stop();
            } else if (state == PlaySate.PLAY) {
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
        state = PlaySate.STOP;
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
        state = PlaySate.PLAY;
        mPlayImageButton.setImageResource(R.drawable.poe_select_video_pause);
    }

    private void setStopState() {
        state = PlaySate.STOP;
        mPlayImageButton.setImageResource(R.drawable.poe_select_video_play);
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
        state = PlaySate.PAUSE;
        mPlayImageButton.setImageResource(R.drawable.poe_select_video_play);
    }

    private void resume() {
        Cog.e(TAG, "resume()~mLastPercent : " + mLastPercent);
        if (getPlayState() == PlaySate.PAUSE) {
            setPlaySate();
            mVideoView.resume();
        } else {
            start();
        }
    }

    public PlaySate getPlayState() {
        return state;
    }

    /**
     * @param total 单位ms
     */
    public void setDuration(final int total) {
        String sTotal = StringUtils.convertTime(convertTimeRound(total));
        mTotalTextView.setText(sTotal);
    }

    /**
     * 设置进度条
     *
     * @param currentPosition
     */
    public void setProgress(int currentPosition) {
        if (!isOnTouch && getVisibility() == View.VISIBLE) {
            String current = StringUtils.convertTime(convertTimeRound(currentPosition));
            setProgress(current, currentPosition);
        }
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
            mExpandImageButton.setVisibility(INVISIBLE);
        } else {
            mExpandImageButton.setVisibility(VISIBLE);
        }
    }

    public ExpandListener getExpandListener() {
        return mExpandListener;
    }

    public void setExpandListener(ExpandListener expandListener) {
        this.mExpandListener = expandListener;
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
