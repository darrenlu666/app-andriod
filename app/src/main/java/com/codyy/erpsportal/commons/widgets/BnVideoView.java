package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.codyy.bennu.sdk.BNMediaPlayer;
import com.codyy.bennu.sdk.impl.BNClassroomInfo;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;

/**
 *
 */
public class BnVideoView extends SurfaceView implements SurfaceHolder.Callback {

    private final static String TAG = "BnVideoView";

    /**
     * add by poe start
     **/
    private SurfaceHolder mSurfaceHolder;

    private OnBNErrorListener mErrorListener;
    private OnBNBufferUpdateListener mBufferUpdateListener;
    private OnBNCompleteListener mCompleteListener;
    private OnPlayingListener mPlayingListener;

    private Surface mSurface;

    private BNMediaPlayer mPlayer;

    private String mUrl;

    private boolean isPlaying;

    private Paint mTextPaint;

    private boolean mShouldDrawError;

    private int mDuration = 0;

    public BnVideoView(Context context) {
        super(context);
        init(null, 0);
    }

    public BnVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BnVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BnVideoView, defStyle, 0);
        mShouldDrawError = typedArray.getBoolean(R.styleable.BnVideoView_shouldDrawError, true);
        typedArray.recycle();
        mSurfaceHolder = getHolder();

        getHolder().addCallback(this);

        initPaint();
    }

    private void initPaint() {
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(getResources().getDimension(R.dimen.txt_size_middle));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public SurfaceHolder getMSurfaceHolder() {
        return mSurfaceHolder;
    }

    public boolean setUrl(@NonNull String url) {
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

    public boolean isUrlEmpty() {
        return TextUtils.isEmpty(mUrl);
    }

    private boolean delayPlayingAfterSurfaceObtained;

    public void play() {
        Cog.d(TAG, "+play:" + getObjectId());
        if (isPlaying()) return;
//        if (mPlayer == null) {
            mPlayer = BNMediaPlayer.createPlayer();
            initListener();
//            mPlayer.Init(this);
//        }else{
//            mPlayer.Init(this);
//        }

        if (TextUtils.isEmpty(mUrl)) throw new IllegalStateException("Please call setUrl firstly.");
        mPlayer.setUri(mUrl + " live=1");
        mPlayer.setVolume(mVolume);
        playNow();

        Cog.d(TAG, "-play:" + getObjectId());
    }

    public void playNow(){
        Cog.d(TAG, "+playNow");
        if (isPlaying()) return;
        if (mSurface != null) {
            setSurfaceAndPlay();
        } else {
            delayPlayingAfterSurfaceObtained = true;
        }
        Cog.d(TAG, "-playNow");
    }

    public void playSingle() {
        Cog.d(TAG, "+playSingle:" + getObjectId());

        if (mPlayer == null ) {
            Cog.d(TAG, "+playSingle:+init()" + getObjectId());
            mPlayer = BNMediaPlayer.createPlayer();
            initListener();
//            mPlayer.setType(BNPlayerImpl.KEY_DECODER_TYPE, BNPlayerImpl.LIVE_HARDWARE);
//            mPlayer.Init(this);
        }

        if (TextUtils.isEmpty(mUrl)) throw new IllegalStateException("Please call setUrl firstly.");
        mPlayer.setUri(mUrl);
        if (mSurface != null) {
            setSurfaceAndPlay();
        } else {
            delayPlayingAfterSurfaceObtained = true;
        }

        Cog.d(TAG, "-playSingle:" + getObjectId());
    }

    /**
     * pause the video
     */
    public void pause(){

        if(isPlaying() && null!= mPlayer){
            Cog.d(TAG, "+pause（）:" + getObjectId());
            mPlayer.pause();
        }
    }

    public void seekTo(int position){

        if(null != mPlayer ){
            mPlayer.seekTo(position);
        }
    }

    public void resume() {
        if (!isPlaying() && mPlayer != null) {
//            mPlayer.ResumePlay();
            setSurfaceAndPlay();
        }
    }

    /**
     * set error listener
     * @param errorListener
     */
    public void setErrorListener(OnBNErrorListener errorListener) {
        this.mErrorListener = errorListener;
    }

    /**
     * set buffer update listener
     * @param mBufferUpdateListener
     */
    public void setBufferUpdateListener(OnBNBufferUpdateListener mBufferUpdateListener) {
        this.mBufferUpdateListener = mBufferUpdateListener;
    }

    /**
     * set complete listener
     * you needn't to stop video play ,it will be auto stop by BNVideoView
     * @param mCompleteListener
     */
    public void setCompleteListener(OnBNCompleteListener mCompleteListener) {
        this.mCompleteListener = mCompleteListener;
    }

    public void setPlayingListener(OnPlayingListener playingListener) {
        this.mPlayingListener = playingListener;
    }

    public void setSurfaceAndPlay() {
        setVolume(mVolume);
        Cog.d(TAG, "+setSurfaceAndPlay" + getObjectId());
        //全屏...
//        mPlayer.SetForceAspectRatio(true);
        mPlayer.setSurface(mSurface);
//        mPlayer.SetType(BNPlayerImpl.KEY_DECODER_TYPE, BNPlayerImpl.LIVE_HARDWARE);
        mPlayer.play();
        setIsPlaying(true);
        Cog.d(TAG, "-setSurfaceAndPlay" + getObjectId());
    }

    public void stop() {
        Cog.d(TAG, "+stop:" + getObjectId());
        if (isPlaying()) {
            delayPlayingAfterSurfaceObtained = false;
            Cog.d(TAG, "+stop:Stop:" + getObjectId());
            mPlayer.stop();
            Cog.d(TAG, "-stop:Stop:" + getObjectId());
            //mPlayer.UnInit();
            setIsPlaying(false);
        }
        Cog.d(TAG, "-stop:" + getObjectId());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(null != mPlayingListener ){
            mPlayingListener.surfaceCreated(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Cog.d(TAG, "surfaceChanged:" + getObjectId());
        mSurface = holder.getSurface();
        if (delayPlayingAfterSurfaceObtained) {
            setSurfaceAndPlay();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Cog.d(TAG, "surfaceDestroyed！");
        if(null != mPlayingListener ){
            mPlayingListener.surfaceDestroyed(holder);
        }
        if (isPlaying()) {
            stop();
        }
    }

    private void initListener(){
        if(null != mPlayer){
            //onError
            mPlayer.setErrorListener(new BNMediaPlayer.ErrorListener() {
                @Override
                public void error(BNMediaPlayer player, int errorCode, String errorMsg) {
                    Cog.e(TAG, "onError=" + errorCode);
                    if(null != mErrorListener){
                        mErrorListener.onError(errorCode);
                    }
                    if (errorCode == 0&& mShouldDrawError) {
                    }
                }
            });

            //onClose
            mPlayer.setEndOfStreamListener(new BNMediaPlayer.EndOfStreamListener() {
                @Override
                public void endOfStream(BNMediaPlayer player) {
                    Cog.d(TAG, "+onClose @" + getObjectId());
                    if (null != mCompleteListener) {
                        mCompleteListener.onComplete();
                    }
                    //播放结束
                    stop();
                }
            });

            //onPlaying 状态变化 .
            mPlayer.setStateChangedListener(new BNMediaPlayer.StateChangedListener() {
                @Override
                public void stateChanged(BNMediaPlayer player, BNMediaPlayer.State state) {
                    if (BNMediaPlayer.State.PLAYING == state) {
                        Cog.d(TAG, "+onPlaying @" + getObjectId());
                        //set mute
                        mPlayer.setVolume(mVolume);
                        if (null != mPlayingListener) {
                            mPlayingListener.onPlaying();
                        }
                    }
                }
            });

            // get the max duration .
            mPlayer.setDurationChangedListener(new BNMediaPlayer.DurationChangedListener() {
                @Override
                public void durationChanged(BNMediaPlayer player, long duration) {
                    mDuration   = (int) duration;
                }
            });

            //buffer update listener .
            mPlayer.setPositionUpdatedListener(new BNMediaPlayer.PositionUpdatedListener() {
                @Override
                public void positionUpdated(BNMediaPlayer player, long position) {
                    if( null != mBufferUpdateListener){
                        mBufferUpdateListener.onBufferUpdate((int)position, mDuration);
                    }
                }
            });
        }
    }

    private int mVolume;

    public void setVolume(int volume) {
        this.mVolume = volume;
        if (mPlayer != null) {
            mPlayer.setVolume(mVolume);
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

    /**
     * @not use normally .
     * 设置辅课堂流并播放。
     * @param uris
     */
    private void setAssistUris(String... uris) {
        BNClassroomInfo classroomInfo = new BNClassroomInfo();
        classroomInfo.setAssistClassroomNum(uris.length);
        for(int i=0 ; i< uris.length ; i++){
            classroomInfo.setAssistClassroomUri(i,uris[i]);
        }

        mPlayer.playWithAudioMix(classroomInfo);
        /*for (int i = 0; i < uris.length; i++) {
            mPlayer.setAssistUri(i, uris[i]);
        }*/
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public void close (){
        if(null != mPlayer){
            mPlayer.stop();
        }
    }
    public interface OnBNErrorListener {
        /**
         * 错误回调
         *  -1 :视频管道错误
         *  -2 :视频信号未连接
         *  待续...
         * @param errorCode
         */
        void onError(int errorCode);
    }


    /**
     * 播放完成监听
     */
    public interface OnBNCompleteListener{
        /**
         * 播放完成
         */
        void onComplete();
    }

    /**
     * 播放进度更新
     */
    public interface OnBNBufferUpdateListener{

        /**
         * 播放进度更新
         * @param position  播放位置
         * @param total     总播放时间
         */
        void onBufferUpdate(int position, int total);

    }

    /**
     * 完成缓冲加载，进行播放时回调
     */
    public interface OnPlayingListener {

        /**
         * prepared to play the url
         */
        void onPlaying();

        /**
         * surface crated
         * @param holder
         */
        void surfaceCreated(SurfaceHolder holder);

        /**
         * surface destroyed
         * @param holder
         */
        void surfaceDestroyed(SurfaceHolder holder);
    }

    public boolean isShouldDrawError() {
        return mShouldDrawError;
    }

    public void setShouldDrawError(boolean shouldDrawError) {
        this.mShouldDrawError = shouldDrawError;
    }
}
