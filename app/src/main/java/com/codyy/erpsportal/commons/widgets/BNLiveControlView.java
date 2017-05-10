package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codyy.bennu.sdk.impl.BNAudioMixer;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.interfaces.IFragmentMangerInterface;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Check3GUtil;
import com.codyy.erpsportal.commons.utils.ScreenBroadCastUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.WeakHandler;
import com.codyy.erpsportal.commons.widgets.BnVideoView2.OnSurfaceChangeListener;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 直播浮动菜单
 * Created by caixingming on 2015/7/9.
 * <p/>
 * home按键后自动seek回原来的位置播放
 * # the Host Activity Must implements the interface {@link IFragmentMangerInterface}
 */
public class BNLiveControlView extends RelativeLayout implements AutoHide, OnSurfaceChangeListener{
    private String TAG = "BNLiveControlView";
    /**
     * 通知隐藏
     */
    private static final int MSG_NOTIFY_HIDE_VIEW = 100;
    /**
     * 设置标题
     */
    private static final int MSG_SET_TITLE = 101;
    /**
     * 闪现
     */
    private static final int MSG_RESOLVE_DISPLAY = 102;
    /**
     * 隐藏control view
     */
    private static final int MSG_HIDE_VIEW = 103;

    @Bind(R.id.imgExpandOfVideoControl)
    ImageButton btnExpand;
    @Bind(R.id.tv_first_line)
    TextView mTitleText;
    /**
     * 一秒单位
     */
    private static final int SECOND = 1000;
    /**
     * 自动隐藏的事件间隔
     */
    public static final int HIDE_PERIOD = 3 * SECOND;
    private BnVideoLayout2 mVideoView = null;
    private String urlPath;
    private DisplayListener mDisplayListener;
    private ExpandListener mExpandListener;
    private BnVideoView2.OnBNErrorListener mOnErrorListener;
    private BnVideoView2.OnBNDurationChangeListener mOnDurationChangeListener;
    private BnVideoView2.OnBNBufferUpdateListener mOnBufferUpdateListener;
    private BnVideoView2.OnBNCompleteListener mOnCompleteListener;
    private BnVideoView2.OnPlayingListener mOnPlayingListener;
    private OnSurfaceChangeListener mOnSurfaceChangeListener;
    private boolean isOnTouch = false;
    private boolean isExpanding = false;
    /**
     * 线程hide的控制handler
     */
    private Handler mHandlerHide;
    private BNLiveHandler mHandler = new BNLiveHandler(this);
    private BNAudioMixer mAudioMixer;
    /**
     * 尝试重连的次数 < 2
     */
    private int mRetryCount = 0;
    /**
     * 最大时间间隔 第一次 ：10 第二次：20 ...40 ..... 60 * 1000
     */
    public static final long MAX_WAIT_TIME = 60 * SECOND;

    private IFragmentMangerInterface mIFragmentManagerInterface;


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Cog.e(TAG, "surfaceCreated");
        if (mVideoView != null && !mVideoView.isPlaying()) {
            //防止横竖屏引起多次start问题
            if (!isExpanding) {
                start(true);
            }
        }
        if (null != mOnSurfaceChangeListener) {
            mOnSurfaceChangeListener.surfaceCreated(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder) {
        Cog.e(TAG, "surfaceChanged~");
        if (null != mVideoView) {
//            mVideoView.setReceiveVideo(true);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Cog.e(TAG, "surfaceDestroyed~");
        mVideoView.stop();
        mVideoView.close();
//        mVideoView.setReceiveVideo(false);
        mRetryCount = 0;
        if (null != mOnSurfaceChangeListener) {
            mOnSurfaceChangeListener.surfaceDestroyed(holder);
        }
    }


    public BNLiveControlView(Context context) {
        super(context, null, R.attr.videoControlViewStyle);
        init(context);
    }

    public BNLiveControlView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.videoControlViewStyle);
        init(context);
    }

    public BNLiveControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BNLiveControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(final Context context) {
        LayoutInflater.from(context).inflate(R.layout.video_control_live_layout, this, true);
        HandlerThread hideThread = new HandlerThread("hide" + TAG);
        hideThread.start();
        mHandlerHide = new Handler(hideThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mHandler.sendEmptyMessage(MSG_HIDE_VIEW);
            }
        };
        registerScreenReceiver();
        if(context instanceof IFragmentMangerInterface){
            mIFragmentManagerInterface = (IFragmentMangerInterface) context;
        }else{
            throw new RuntimeException("you should implements IFragmentManagerInterface");
        }
    }

    private ScreenBroadCastUtils mScreenBroadCastUtils;
    private void registerScreenReceiver() {
        mScreenBroadCastUtils = new ScreenBroadCastUtils(new ScreenBroadCastUtils.ScreenLockListener() {
            @Override
            public void onScreenOn() {
                Log.i(TAG,"onScreenOn()");
                if (!TextUtils.isEmpty(urlPath)) {
                    start(true);
                }
            }

            @Override
            public void onScreenLock() {
                Log.i(TAG,"onScreenLock()");
                stop();
            }
        });
    }

    public void setIsExpanding(boolean isExpanding) {
        this.isExpanding = isExpanding;
    }

    public void setDisplayListener(DisplayListener mDisplayListener) {
        this.mDisplayListener = mDisplayListener;
    }

    /**
     * 是否开始播放视频
     */
    public boolean isPlaying = false;

    /**
     * 绑定播放的组件 /设置 编解码格式和演讲者
     *
     * @param view
     * @param title
     * @param encodeType 0:hardware encode  1:software encode .
     */
    public void bindVideoView(BnVideoLayout2 view, String title, int encodeType) {

        if (view == null) {
            throw new NullPointerException("VideoView is NULL~!!");
        }
        this.mVideoView = view;
        mVideoView.setEncodeType(encodeType);
        mVideoView.setVolume(100);
        mTitleText.setText(title);
        mVideoView.setOnErrorListener(new BnVideoView2.OnBNErrorListener() {
            @Override
            public void onError(int errorCode, String errorMsg) {
                if (0 == errorCode && mRetryCount < 30) {
                    Cog.e(TAG, "error 0 : 当前网络异常，尝试重连 " + mRetryCount + " : " + urlPath);
                    if (mVideoView.isPlaying()) {
                        mVideoView.stop();
                    }
                    start(true);
                    mRetryCount++;
                }

                if (1 == errorCode) {//不支持硬解　
                    Cog.e(TAG, "部支持硬解，尝试软解！！！");
                    start(false);
                }

                if (null != mOnErrorListener) {
                    mOnErrorListener.onError(errorCode, errorMsg);
                }
            }
        });
        //设置点击事件
        mVideoView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // 点击显示，再次点击消失 .
                if (getVisibility() == View.GONE) {
                    showControl();
                } else {
                    hideControl();
                }

                if (!isPlaying && null != mDisplayListener) {
                    mDisplayListener.show();
                }

                return false;
            }
        });
        mVideoView.setPlayingListener(new BnVideoView2.OnPlayingListener() {
            @Override
            public void onPlaying() {
                Cog.e(TAG, "onPlaying () ~ ");
                isPlaying = true;
                if (null != mOnPlayingListener) {
                    mOnPlayingListener.onPlaying();
                }
            }
        });
        mVideoView.setOnSurfaceChangeListener(this);
    }

    public void setAudioMixer(BNAudioMixer mAudioMixer) {
        this.mAudioMixer = mAudioMixer;
    }

    /**
     * 设置播放路径 并播放
     *
     * @param path
     */
    public void setVideoPath(String path, final BNAudioMixer mixer) {
        Cog.d("url", "check it : " + path);
        this.urlPath = path;
        mAudioMixer = mixer;
        Check3GUtil.instance().CheckNetType(mIFragmentManagerInterface, new Check3GUtil.OnWifiListener() {
            @Override
            public void onNetError() {
                ToastUtil.showToast(EApplication.instance(), "网络异常！");
            }

            @Override
            public void onContinue() {
                start(true);
            }
        });
    }

    public void stop() {
        if (null != mVideoView && mVideoView.isPlaying()) {
            mVideoView.stop();
            mRetryCount = 0;
        }
    }

    /**
     * 开始播放
     */
    public void start(boolean isHardWare) {
        if (!TextUtils.isEmpty(urlPath) && null != mVideoView) {
            mVideoView.setUrl(urlPath, BnVideoView2.BN_URL_TYPE_RTMP_LIVE);
            mVideoView.setVolume(100);
            if (isHardWare) {
                mVideoView.setEncodeType(BnVideoView2.BN_ENCODE_HARDWARE);
            } else {
                mVideoView.setEncodeType(BnVideoView2.BN_ENCODE_SOFTWARE);
            }
            mVideoView.play(BnVideoView2.BN_PLAY_WITH_CHAT);
            //take effect after play init which tested by poe .
            mVideoView.setTimeOut(15);
            if (mAudioMixer != null) {
                mVideoView.setAudioMixer(mAudioMixer);
            } else {
                Log.w(TAG, "mAudioMixer == null");
            }
        }

        if (null != mDisplayListener) {
            mDisplayListener.show();
        }
    }

    public void startResume() {
        if (!TextUtils.isEmpty(urlPath)) {
            if (mVideoView.isPlaying()) {
                mVideoView.setReceiveVideo(true);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Cog.e(TAG, "onFinishInflate()");
        this.setVisibility(GONE);
        ButterKnife.bind(this);

        btnExpand.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                touchControl();
                int screenHeight = getResources().getDisplayMetrics().heightPixels;

                if (getHeight() < screenHeight / 2) {
                    isExpanding = true;
                    if (null != mExpandListener) {
                        mExpandListener.expand();
                    }
                } else {
                    isExpanding = false;
                    if (null != mExpandListener) {
                        mExpandListener.collapse();
                    }
                }
            }
        });
    }


    //延时消失
    @Override
    public void touchControl() {
        Cog.e(TAG, " touchControl()~");
        mHandlerHide.removeMessages(MSG_NOTIFY_HIDE_VIEW);
        mHandlerHide.sendEmptyMessageDelayed(MSG_NOTIFY_HIDE_VIEW, HIDE_PERIOD);
    }

    @Override
    public void destroyView() {
        if (null != mVideoView) {
            mVideoView.stop();
            mVideoView.close();
        }
    }

    @Override
    public void showControl() {
        Cog.e(TAG, "showControl()~");
        touchControl();
        if (getVisibility() == View.GONE) {

            this.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(EApplication.instance(), R.anim.abc_fade_in);
            this.startAnimation(anim);

            if (null != mDisplayListener) {
                mDisplayListener.show();
            }
        }
    }

    @Override
    public void hideControl() {
        Cog.e(TAG, "hideControl");
        this.setVisibility(View.GONE);
        this.startAnimation(AnimationUtils.loadAnimation(EApplication.instance(), R.anim.abc_fade_out));

        if (null != mDisplayListener) {
            mDisplayListener.hide();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mScreenBroadCastUtils) {
            mScreenBroadCastUtils.destroy();
        }
    }


    public void setOnExpandListener(ExpandListener expandListener) {
        this.mExpandListener = expandListener;
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

    public void setOnSurfaceChangeListener(OnSurfaceChangeListener surfaceChangeListener) {
        this.mOnSurfaceChangeListener = surfaceChangeListener;
    }


    private void hideView() {
        if (!isOnTouch) {
            hideControl();
        }
    }

    private void setTitle(String title) {
        if (getVisibility() == View.VISIBLE) {
            mTitleText.setText(title);
        }
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

    private static class BNLiveHandler extends WeakHandler<BNLiveControlView> {

        public BNLiveHandler(BNLiveControlView owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (getOwner() == null) return;
            Bundle bd = (Bundle) msg.obj;
            switch (msg.what) {
                case MSG_SET_TITLE://set the title
                    String title = bd.getString("title");
                    getOwner().setTitle(title);
                    break;
                case MSG_RESOLVE_DISPLAY:
                    //do nothing ...
                    break;
                case MSG_HIDE_VIEW://hide control view
                    getOwner().hideView();
                    break;
            }
        }
    }
}
