package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import com.codyy.bennu.sdk.BNMediaPlayer;
import com.codyy.bennu.sdk.impl.BNAudioMixer;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;

/**
 * used by poe
 * 独立为BnVideoControlView服务
 */
public class BnVideoView2 extends SurfaceView implements SurfaceHolder.Callback,Handler.Callback{
    private final static String TAG = BnVideoView2.class.getSimpleName();
    /**
     * http点播
     */
    public static final int BN_URL_TYPE_HTTP = 0;
    /**
     * rtmp 点播
     */
    public static final int BN_URL_TYPE_RTMP_HISTORY = 1;
    /**
     * rtmp 直播
     */
    public static final int BN_URL_TYPE_RTMP_LIVE = 2;
    /**
     * 普通点播播放
     */
    public static final int BN_PLAY_DEFAULT = 0;
    /**
     * 直播播放(视频会议)
     */
    public static final int BN_PLAY_WITH_CHAT = 1;
    /**
     * hardware encode .
     */
    public static final int BN_ENCODE_HARDWARE = 0 ;
    /**
     * software encode .
    */
    public static final int BN_ENCODE_SOFTWARE = 1;

    public static final int MSG_BN_ON_ERROR = 0x001;//error
    public static final int MSG_BN_ON_PLAYING = 0x002;// onplaying ...
    public static final int MSG_BN_ON_COMPLETE = 0x003;//end stream
    public static final int MSG_BN_ON_BUFFER_UPDATE = 0x004;//position update ...
    public static final int MSG_BN_ON_DURATION_UPDATE = 0x005;//duration changed !
    public static final String ARG_EXTRA_DURATION = "duration";//总时长
    public static final String ARG_EXTRA_POSITION = "position";//当前位置
    public static final String ARG_EXTRA_ERROR_CODE = "error_code";//错误码
    public static final String ARG_EXTRA_ERROR_MSG = "error_msg";//错误原因

    private Handler mHandler ;//Main Thread Handler
    private OnBNErrorListener mOnErrorListener;
    private OnBNDurationChangeListener mOnDurationChangeListener;
    private OnBNBufferUpdateListener mOnBufferUpdateListener;
    private OnBNCompleteListener mOnCompleteListener;
    private OnPlayingListener mOnPlayingListener;
    private OnSurfaceChangeListener mOnSurfaceChangeListener;

    private Surface mSurface;
    private SurfaceHolder mSurfaceHold;//用于预览相机内容
    private BNMediaPlayer mPlayer;
    private String mUrl;
    private boolean isPlaying;
    /**
     * 暂停状态
     */
    private boolean isPause = false ;
    private boolean mShouldDrawError;
    private static final Object lock = new Object();
    private int mEncodeType = BN_ENCODE_HARDWARE;//视频播放类型 0:hardware 1:software
    /**
     * url type 0: http play model 1:rtmp play modle 2:rtmp chat model
     */
    private int mURLType = BN_URL_TYPE_HTTP;//默认http点播

    /**
     * play type 0: play() 1:playWithChat() 2.playWithAudioMix()
     */
    private int mPlayType = BN_PLAY_DEFAULT;//默认直接播放 play（）

    private int mVolume;

    private boolean mIsReceiveVideo = true;//是否接收视频

    private boolean mIsSurfaceNeedBinding = true;//是否需要调用setSurface() . default: true 依赖此flag来判断是否应该反复调用mPlayer.setSurface();


    public BnVideoView2(Context context) {
        super(context);
        init(null, 0);
    }

    public BnVideoView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BnVideoView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void init(AttributeSet attrs, int defStyle) {
        if(!isInEditMode()){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BnVideoView, defStyle, 0);
            mShouldDrawError = typedArray.getBoolean(R.styleable.BnVideoView_shouldDrawError, true);
            typedArray.recycle();

            mSurfaceHold = getHolder();
            mSurfaceHold.addCallback(this);
            mHandler = new Handler(this);
        }
    }


    /**
     * 获取播放的实体类.
     * @return
     */
    public BNMediaPlayer getPlayer() {
        return mPlayer;
    }

    /**
     *  在play之前调用 or 在play完成后调用 .
      * @param bReceived
     */
    public void setReceiveVideo(boolean bReceived)
    {
        Log.d(TAG, "+setReceiveVideo in viewer______IN:" + bReceived + "cur = " + mIsReceiveVideo);
        if (mIsReceiveVideo == bReceived)
            return;

        mIsReceiveVideo = bReceived;
        if (mPlayer != null) {
            Log.d(TAG, "+setReceiveVideo in viewer:" + bReceived);
            mPlayer.setReceiveVideo(bReceived, mSurface);
        }
        else
            Log.d(TAG, "+setReceiveVideo in viewer: but player == null");
    }


    /**
     * @param url address
     * @param urlType 0:1:2 http / rtmp play /rtmp chat
     * @return
     */
    public boolean setUrl(@NonNull String url ,int urlType) {
        this.mURLType   =   urlType;
        Cog.d(TAG, "+setUrl: this=" + getObjectId() + ",url=" + url);
        if (mUrl == null) {
            this.mUrl = url;
            return true;
        } else if (!mUrl.equals(url)) {
            stop();
            this.mUrl = url;
            return true;
        }
        Cog.d(TAG, "-setUrl:" + url);
        return false;
    }

    public String getUrl() {
        return mUrl;
    }

    public SurfaceHolder getSurfaceHold() {
        return mSurfaceHold;
    }

    public boolean isUrlEmpty() {
        return TextUtils.isEmpty(mUrl);
    }

    private boolean delayPlayingAfterSurfaceObtained;

    public void play(int playType) {
        this.mPlayType    =   playType;
        Cog.d(TAG, "+play:" + getObjectId());
        if (isPlaying()){
            return;
        }
        mPlayer = BNMediaPlayer.createPlayer();
        //一个新的player实例需要绑定一次#setSurface()
        mIsSurfaceNeedBinding = true;
        initListener();
        if (TextUtils.isEmpty(mUrl))
            throw new IllegalStateException("Please call setUrl firstly.");
        playNow();
        Cog.d(TAG, "-play:" + getObjectId());
    }

    /**
     * 获取播放类型 0:play() 1:playWithChat() 2:audio mix
     * @return
     */
    public int getPlayType(){
        return mPlayType;
    }

    /**
     * this is a hide method , you should not invoked this method for some player leak error .
     */
    private void playNow() {
        Cog.d(TAG, "+playNow");
        if (isPlaying())
            return;
        if (mSurface != null) {
            setSurfaceAndPlay();
        } else {
            delayPlayingAfterSurfaceObtained = true;
        }
        Cog.d(TAG, "-playNow");

    }

    /**
     * pause the video
     */
    public void pause() {
        setIsPause(true);
        if (null != mPlayer) {
            Cog.d(TAG, "+pause（）:" + getObjectId());
            mPlayer.pause();
        }
    }

    public void seekTo(int position) {
        if (null != mPlayer) {
            mPlayer.seekTo(position);
        }
    }

    public void resume() {
        if (isPause && mPlayer != null) {
            setIsPause(false);
            mPlayer.play();
        }
    }

    /**
     * set error listener
     * @param errorListener
     */
    public void setOnErrorListener(OnBNErrorListener errorListener) {
        this.mOnErrorListener = errorListener;
    }

    /**
     * set buffer update listener
     *
     * @param mBufferUpdateListener
     */
    public void setOnBufferUpdateListener(OnBNBufferUpdateListener mBufferUpdateListener) {
        this.mOnBufferUpdateListener = mBufferUpdateListener;
    }

    /**
     * set complete listener
     * you needn't to stop video play ,it will be auto stop by BNVideoView
     *
     * @param mCompleteListener
     */
    public void setOnCompleteListener(OnBNCompleteListener mCompleteListener) {
        this.mOnCompleteListener = mCompleteListener;
    }

    public void setOnPlayingListener(OnPlayingListener playingListener) {
        this.mOnPlayingListener = playingListener;
    }

    public void setOnSurfaceChangeListener(OnSurfaceChangeListener surfaceChangeListener) {
        this.mOnSurfaceChangeListener = surfaceChangeListener;
    }

    public void setOnDurationChangeListener(OnBNDurationChangeListener durationChangeListener) {
        this.mOnDurationChangeListener = durationChangeListener;
    }

    /**
     * 设置视频 编码格式
     * {@link BnVideoView2#BN_ENCODE_HARDWARE BnVideoView2#BN_ENCODE_SOFTWARE}
     * @param mEncodeType 0:hardware 1:software
     */
    public void setEncodeType(int mEncodeType) {
        this.mEncodeType = mEncodeType;
    }

    public void setSurfaceAndPlay() {
        synchronized (lock) {
            Cog.d(TAG, "+setSurfaceAndPlay" + getObjectId());
            setVolume(mVolume);
            //initional ratio
            mPlayer.setForceAspectRatio(true);
            if (mUrl.startsWith("rtmp") && mURLType == BnVideoView2.BN_URL_TYPE_RTMP_LIVE) {
                mPlayer.setUri(mUrl + " live=1");
            } else {
                mPlayer.setUri(mUrl);
            }
            //防止无效播放地址->横竖屏->后多次调用引起的libc->gui crash .
            if(mIsSurfaceNeedBinding){
                mIsSurfaceNeedBinding = false;
                Cog.e(TAG,"setSurface method invoked ~~~~~~~~~~~~~!!!!!!!");
                mPlayer.setSurface(mSurface);
            }
            if (mEncodeType == BN_ENCODE_HARDWARE) {
                mPlayer.setDecodeType(true);
            } else {
                mPlayer.setDecodeType(false);
            }

            switch (mPlayType){
                case BN_PLAY_DEFAULT://点播
                    mPlayer.play();
                    break;
                case  BN_PLAY_WITH_CHAT:// rtmp 点播
                    mPlayer.playWithChat();
                    break;
                  default:
                      throw new IllegalStateException("don't support with audio mix play ~ BN_PLAY_TYPE_3");
            }

            setIsPlaying(true);
            Cog.d(TAG, "-setSurfaceAndPlay" + getObjectId());
        }
    }

    public void stop() {
        if (isPlaying() || isPause) {
            delayPlayingAfterSurfaceObtained = false;
            Cog.d(TAG, "+stop:Stop:" + getObjectId());
            mPlayer.stop();
            Cog.d(TAG, "-stop:Stop:" + getObjectId());
            setIsPlaying(false);
            setIsPause(false);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        ViewGroup.LayoutParams param = getLayoutParams();
        Cog.e(TAG, "surfaceCreated () -----------width: " + param.width + ", height :" + param.height);
        synchronized (lock) {
            if (null != mOnSurfaceChangeListener) {
                mOnSurfaceChangeListener.surfaceCreated(holder);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Cog.e(TAG, "surfaceChanged () -----------width: " + width + ", height :" + height);
        Cog.d(TAG, "surfaceChanged:" + getObjectId());
        mSurface = holder.getSurface();
        if (delayPlayingAfterSurfaceObtained) {
            //防止横竖平　surfaceChange 引起多次播放 .
            delayPlayingAfterSurfaceObtained = false;
            setSurfaceAndPlay();
        }
        if (null != mOnSurfaceChangeListener) {
            mOnSurfaceChangeListener.surfaceChanged(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Cog.d(TAG, "surfaceDestroyed！");
        //surface 销毁后需要重新绑定.
        mIsSurfaceNeedBinding = true;
        if (null != mOnSurfaceChangeListener) {
            mOnSurfaceChangeListener.surfaceDestroyed(holder);
        }
    }
    private void initListener(){
        if(null != mPlayer){
            //onError
            mPlayer.setErrorListener(new BNMediaPlayer.ErrorListener() {
                @Override
                public void error(BNMediaPlayer player, int errorCode, String errorMsg) {
                    Cog.e(TAG, "onError=" + errorCode+" , "+errorMsg);
                    if (errorCode == -2 ||errorCode == 0) {
                        //在错误出现后总会调用stop方法，此处确保setSurface会被调用？可能不需要?但是暂停是不销毁的，防止暂停后画面不更新此处必须添加！
                        mIsSurfaceNeedBinding = true;
                        stop();
                    }
                    Bundle bd = new Bundle();
                    bd.putInt(ARG_EXTRA_ERROR_CODE , errorCode);
                    bd.putString(ARG_EXTRA_ERROR_MSG , errorMsg);
                    mHandler.obtainMessage(MSG_BN_ON_ERROR , bd).sendToTarget();
                }
            });

            //onClose
            mPlayer.setEndOfStreamListener(new BNMediaPlayer.EndOfStreamListener() {
                @Override
                public void endOfStream(BNMediaPlayer player) {
                    Cog.e(TAG, "+onClose @" + getObjectId());
                    mIsSurfaceNeedBinding = true;
                    mHandler.sendEmptyMessage(MSG_BN_ON_COMPLETE);
                }
            });

            //onPlaying 状态变化 .
            mPlayer.setStateChangedListener(new BNMediaPlayer.StateChangedListener() {
                @Override
                public void stateChanged(BNMediaPlayer player, BNMediaPlayer.State state) {
                    if (BNMediaPlayer.State.PLAYING == state) {
                        Cog.d(TAG, "+onPlaying @" + getObjectId());
                        mPlayer.setVolume(mVolume);
                        mHandler.sendEmptyMessage(MSG_BN_ON_PLAYING);
                    }
                }
            });

            // get the max duration .
            mPlayer.setDurationChangedListener(new BNMediaPlayer.DurationChangedListener() {
                @Override
                public void durationChanged(BNMediaPlayer player, long duration) {
                    Cog.e(TAG,"duration changed : " + duration);
                    Bundle bd = new Bundle();
                    bd.putInt(ARG_EXTRA_DURATION , (int) duration);
                    mHandler.obtainMessage(MSG_BN_ON_DURATION_UPDATE , bd ).sendToTarget();
                }
            });

            //buffer update listener .
            mPlayer.setPositionUpdatedListener(new BNMediaPlayer.PositionUpdatedListener() {
                @Override
                public void positionUpdated(BNMediaPlayer player, long position) {

                   /* if (null != mBufferUpdateListener) {
                        mBufferUpdateListener.onBufferUpdate((int) position, mDuration);
                    }*/
                    Bundle bd = new Bundle();
                    bd.putInt(ARG_EXTRA_POSITION , (int)position);
                    mHandler.obtainMessage(MSG_BN_ON_BUFFER_UPDATE , bd).sendToTarget();
                }
            });
        }
    }

    public void setVolume(int volume) {
        this.mVolume = volume;
        if (mPlayer != null) {
            mPlayer.setVolume(mVolume);
        }
    }

    /**
     * 需要在play之后设置
     * 设置混音器
     * @param mixer
     */
    public void setAudioMixer(BNAudioMixer mixer){
        if(null == mixer) return;
        if(mPlayer != null){
            mPlayer.setAudioMixer(mixer);
        }
    }

    /**
     * 单位 : s
     * @param timeOut
     */
    public void setTimeOut(int timeOut){
        if(null != mPlayer){
            mPlayer.setTimeOut(timeOut);
        }
    }



    private String getObjectId() {
        return Integer.toHexString(System.identityHashCode(this));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Cog.d(TAG, "+onDraw!");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Cog.d(TAG, "onDetachedFromWindow()!");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Cog.d(TAG, "onAttachedToWindow!");
    }

    public void setURLType(int mURLType) {
        this.mURLType = mURLType;
    }

    public int getURLType() {
        return mURLType;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPause(){
        return  isPause ;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public void setIsPause(boolean pause) {
        isPause = pause;
    }

    public void close (){
        Cog.e(TAG,"close ~method");
        if(null != mPlayer){
            Cog.e(TAG,"close ~mPlayer~");
            mPlayer.stop();
            setIsPlaying(false);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        Bundle bd = (Bundle) msg.obj;
        switch (msg.what) {
            case MSG_BN_ON_ERROR:// error throw by bennue .
                int errorCode = bd.getInt(ARG_EXTRA_ERROR_CODE);
                String errorMsg = bd.getString(ARG_EXTRA_ERROR_MSG);
                if(null != mOnErrorListener){
                    mOnErrorListener.onError(errorCode , errorMsg);
                }
                break;
            case MSG_BN_ON_DURATION_UPDATE://duration changed .
                int duration = bd.getInt(ARG_EXTRA_DURATION);
                if(null != mOnDurationChangeListener){
                    mOnDurationChangeListener.onDurationUpdate(duration);
                }
                break;
            case MSG_BN_ON_PLAYING://on plaing ...used with #bindView(BNVideoView..)
                if(null != mOnPlayingListener){
                    mOnPlayingListener.onPlaying();
                }
                break;
            case MSG_BN_ON_BUFFER_UPDATE:// buffer update ...
                int pos = bd.getInt(ARG_EXTRA_POSITION);
                if(null != mOnBufferUpdateListener){
                    mOnBufferUpdateListener.onBufferUpdate(pos);
                }
                break;
            case MSG_BN_ON_COMPLETE://on complete ...
                if(null != mOnCompleteListener){
                    mOnCompleteListener.onComplete();
                }
                break;
        }
        return false;
    }

    public interface OnBNErrorListener {
        /**
         * 错误回调
         * -1 :视频管道错误
         * -2 :视频信号未连接
         * 待续...
         *
         * @param errorCode
         */
        void onError(int errorCode , String errorMsg);
    }


    /**
     * 播放完成监听
     */
    public interface OnBNCompleteListener {
        /**
         * 播放完成
         */
        void onComplete();
    }

    /**
     * 播放进度更新
     */
    public interface OnBNBufferUpdateListener {

        /**
         * 播放进度更新
         *
         * @param position 播放位置
         */
        void onBufferUpdate(int position);

    }

    public interface OnBNDurationChangeListener{
        /**
         * 流的最大长度变更通知
         * @param duration
         */
        void onDurationUpdate(int duration);
    }

    /**
     * 完成缓冲加载，进行播放时回调
     */
    public interface OnPlayingListener {
        /**
         * prepared to play the url
         */
        void onPlaying();
    }

    /**
     * surface change listener
     */
    public interface OnSurfaceChangeListener {
        /**
         * @param holder
         */
        void surfaceCreated(SurfaceHolder holder);
        /**
         * @param holder
         */
        void surfaceChanged(SurfaceHolder holder);
        /**
         * surface destroyed
         */
        void surfaceDestroyed(SurfaceHolder holder);
    }
}
