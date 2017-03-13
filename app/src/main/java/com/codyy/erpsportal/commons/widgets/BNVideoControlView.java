package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
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

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
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
public class BNVideoControlView extends RelativeLayout implements AutoHide, Handler.Callback {
    private String TAG = BNVideoControlView.class.getSimpleName();
    public static final int MSG_WHAT_NOTIFY_HIDE = 100;// hide thread notify main thread to hide title
    public static final int MSG_WHAT_AUTO_HIDE = 103;//hide the view
    public static final int ERROR_VIDEO_TOOL_SHORT = 201;//too short video less than 1000ms .
    @Bind(R.id.imgPlayOfVideoControl)    ImageButton mPlayImageButton;
    @Bind(R.id.imgExpandOfVideoControl)    ImageButton mExpandImageButton;
    @Bind(R.id.seekBarOfVideoControl)    SeekBar mSeekBar;
    @Bind(R.id.txtTotalTimeOfVideoControl)    TextView mTotalTextView;
    @Bind(R.id.txtPlayTimeOfVideoControl)    TextView mCurrentTextView;
    private BnVideoView2.OnBNErrorListener mOnErrorListener;
    private BnVideoView2.OnBNDurationChangeListener mOnDurationChangeListener;
    private BnVideoView2.OnBNBufferUpdateListener mOnBufferUpdateListener;
    private BnVideoView2.OnBNCompleteListener mOnCompleteListener;
    private BnVideoView2.OnPlayingListener mOnPlayingListener;
    private BnVideoView2.OnSurfaceChangeListener mOnSurfaceChangeListener;
    private ExpandListener mExpandListener;
    private Context mContext;
    private PlaySate state = PlaySate.STOP;

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
    private boolean mDestroy = false;//是否调用过surfaceDestroyed .
    private boolean mPaused = false ;//是否突然被赞听过.
    private long mStartPlayTime = -1;
    private boolean mIsExpandable = true ;//是否支持横竖屏 default：true
    private FragmentManager mFragmentManager ;

    public void setDisplayListener(DisplayListener mDisplayListener) {
        this.mDisplayListener = mDisplayListener;
    }

    public FragmentManager getFragmentManager() {
        return mFragmentManager;
    }

    public void setFragmentManager(FragmentManager mFragmentManager) {
        this.mFragmentManager = mFragmentManager;
    }

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

    /**
     * 设置进度条
     *
     * @param current
     * @param progress
     */
    private void setProgress(String current, int progress) {
        mCurrentTextView.setText(current + "/");
        mSeekBar.setMax(mTotal);
        mSeekBar.setProgress(progress);
    }

    /**
     * bind surfaceView , set listener .
     *
     * @param view
     */
    public void bindVideoView(@NonNull BnVideoView2 view ,FragmentManager manager) {
        this.mVideoView = view;
        this.mFragmentManager = manager;
        mVideoView.setVolume(100);
        initListener();
    }

    private void initListener() {
        mVideoView.setOnDurationChangeListener(new BnVideoView2.OnBNDurationChangeListener() {
            @Override
            public void onDurationUpdate(int duration) {
                Cog.e(TAG, " total : " + duration);
                mTotal = duration;
                setDuration(duration);
                if (null != mOnDurationChangeListener)
                    mOnDurationChangeListener.onDurationUpdate(duration);
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
                Cog.e(TAG, "onPlaying()~~~~~~~~~~~~~~~~mLastPercent" + mLastPercent);
                setPlaySate();
                if (Math.abs(mLastPercent - mCurrentPosition) >= 1000) {//防止拖动seekbar后两者差值在1000ms内引起的无限seek .
                    Cog.e(TAG, "onPlaying()~~~~~~~~~~~~~~~~seek:" + mLastPercent + " mcurrent:" + mCurrentPosition);
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
                if (mVideoView != null && !TextUtils.isEmpty(urlPath)) {
                    if (mIsLocal) {
                        start();
                    } else {
                        Check3GUtil.instance().CheckNetType(EApplication.instance(), new Check3GUtil.OnWifiListener() {
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
                mDestroy = true;
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
            Check3GUtil.instance().CheckNetType(mContext, new Check3GUtil.OnWifiListener() {
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
        mVideoView.stop();
    }

    public void initConfig() {
        Cog.e(TAG, "+initConfig " + mLastPercent                                                                                                                                                                                        );
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
     * 开始播放
     */
    public void start() {
        Cog.e(TAG, "+start " + urlPath + " mLastper:" + mLastPercent);
        if (mVideoView == null) return;//实测有null情况
        if (!TextUtils.isEmpty(urlPath)) {
            mVideoView.setUrl(urlPath, mURLType);
            mVideoView.play(BnVideoView2.BN_PLAY_DEFAULT);
            if (mLastPercent > 0) {
                if(mPaused){//paused by some action .
                    mPaused = false;
                    mVideoView.seekTo(mLastPercent);
                }else if(mDestroy){//surface Destroyed .
                    mDestroy = false;
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

    private void setPauseState() {
        //记录当前的位置，方便resume后seek .
        mLastPercent = mCurrentPosition;
        mCurrentPosition = 0;
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
        String sTotal = StringUtils.convertTime(total / 1000);
        mTotalTextView.setText(sTotal);
    }

    /**
     * 设置进度条
     *
     * @param currentPosition
     */
    public void setProgress(int currentPosition) {
        if (!isOnTouch && getVisibility() == View.VISIBLE) {
            String current = StringUtils.convertTime(currentPosition / 1000);
            setProgress(current, currentPosition);
        }
    }

    public BNVideoControlView(Context context) {
        this(context, null);
    }

    public BNVideoControlView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context);
    }

    public BNVideoControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BNVideoControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.video_control_view, this, true);
        ButterKnife.bind(rootView);
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
            mWifiBroadCastUtil = new WiFiBroadCastUtils(mContext, mFragmentManager,new WiFiBroadCastUtils.PlayStateListener() {
                @Override
                public void play() {
                    start();
                }

                @Override
                public void stop() {
                    BNVideoControlView.this.stop();
                }
            });
        }
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

                isOnTouch = false;
                //防止播放完成之后记录的位置得不到更新
                if (mLastPercent != mDestPos) {
                    mLastPercent = mDestPos;
                }
                if (mIsLocal) {
                    resume();
                    mVideoView.seekTo(mDestPos);
                } else {
                    Check3GUtil.instance().CheckNetType(mContext, new Check3GUtil.OnWifiListener() {
                        @Override
                        public void onNetError() {

                        }

                        @Override
                        public void onContinue() {
                            resume();
                            mVideoView.seekTo(mDestPos);
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
                    if (state == PlaySate.STOP || state == PlaySate.PAUSE) {
                        if (mIsLocal) {
                            //                            start();
                            if (state == PlaySate.STOP) {
                                start();
                            } else {
                                resume();
                            }
                        } else {
                            Check3GUtil.instance().CheckNetType(mContext, new Check3GUtil.OnWifiListener() {
                                @Override
                                public void onNetError() {

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
                if(mIsExpandable){
                    touchControl();
                    Activity activity = (Activity) getContext();
                    if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        UIUtils.setLandscape(activity);
                        if(null != mExpandListener) mExpandListener.expand();
                    } else if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        UIUtils.setPortrait(activity);
                        if(null != mExpandListener) mExpandListener.collapse();
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

    public boolean isPlaying() {
        return null != mVideoView && mVideoView.isPlaying();
    }


    public void setOnErrorListener(BnVideoView2.OnBNErrorListener errorListener) {
        mVideoView.setOnErrorListener(errorListener);
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
        if(!mIsExpandable){
            mExpandImageButton.setVisibility(INVISIBLE);
        }else{
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
    public interface ExpandListener{
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
