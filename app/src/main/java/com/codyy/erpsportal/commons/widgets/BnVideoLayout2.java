package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.codyy.bennu.sdk.BNMediaPlayer;
import com.codyy.bennu.sdk.impl.BNAudioMixer;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;

/**
 * 增加编码控制 .
 * created by poe .
 */
public class BnVideoLayout2 extends FrameLayout implements BnVideoView2.OnPlayingListener, BnVideoView2.OnBNErrorListener  {

    private static final String TAG = "BnVideoLayout2";
    private BnVideoView2 mBnVideoView;
    private TextView mHintTv;
    private BnVideoView2.OnBNErrorListener mOnErrorListener;
    private BnVideoView2.OnPlayingListener mOnPlayingListener;
    private ITextClickListener mTextClickListener;

    public BnVideoLayout2(Context context) {
        this(context, null);
    }

    public BnVideoLayout2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BnVideoLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BnVideoLayout2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs){
        LayoutInflater.from(getContext()).inflate(R.layout.bn_video_layout2, this, true);
    }

    /**
     * 获取播放的实体类.
     * @return
     */
    public BNMediaPlayer getPlayer() {
        if(null != mBnVideoView)
            return  mBnVideoView.getPlayer();
        return null;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBnVideoView = (BnVideoView2) findViewById(R.id.bnVideoView);
        mHintTv = (TextView) findViewById(R.id.hintText);
        mBnVideoView.setOnPlayingListener(this);
        mBnVideoView.setOnErrorListener(this);
        mHintTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //living type retry connect ．　
                if(null != mBnVideoView && mBnVideoView.getUrl() != null && BnVideoView2.BN_URL_TYPE_RTMP_LIVE == mBnVideoView.getURLType()){
                    if(!getResources().getString(R.string.loading).equals(mHintTv.getText().toString())){
                        post(new Runnable() {
                            @Override
                            public void run() {
                                mHintTv.setText(R.string.loading);
                                mHintTv.setVisibility(VISIBLE);
                            }
                        });
                        mBnVideoView.playNow();
                    }
                }

                if(null != mTextClickListener) mTextClickListener.onClick(v);
            }
        });
    }

    @Override
    public void onPlaying() {
        post(new Runnable() {
            @Override
            public void run() {
                mHintTv.setVisibility(INVISIBLE);
                mBnVideoView.requestFocus();
            }
        });
        if (mOnPlayingListener != null) mOnPlayingListener.onPlaying();
    }

    @Override
    public void onError(int errorCode , String errorMsg) {
        if (errorCode == -2 || 0 == errorCode ) {
            if(mBnVideoView.getURLType() == BnVideoView2.BN_URL_TYPE_RTMP_LIVE){
                post(new Runnable() {
                    @Override
                    public void run() {
                        mHintTv.setText(R.string.txt_video_meeting_no_input_stream_retry);
                        mHintTv.setVisibility(VISIBLE);
                    }
                });
            }else{
                post(new Runnable() {
                    @Override
                    public void run() {
                        mHintTv.setText(R.string.tv_detail_video_un_start);
                        mHintTv.setVisibility(VISIBLE);
                    }
                });
            }
        } else if( errorCode == -1){//不支持硬解，改为软解
            mBnVideoView.setEncodeType(BnVideoView2.BN_ENCODE_SOFTWARE);
        } else if(9 == errorCode){//9 no playable stream .
            post(new Runnable() {
                @Override
                public void run() {
                    mHintTv.setText(R.string.tv_detail_video_un_start);
                    mHintTv.setVisibility(VISIBLE);
                }
            });
        }

        if(null != mOnErrorListener) {
            mOnErrorListener.onError(errorCode,errorMsg);
        }
    }

    public BnVideoView2 getVideoView() {
        return mBnVideoView;
    }

    /**
     * @param videoUrl
     * @param urlType
     */
    public void setUrl(String videoUrl,int urlType) {
        if (mBnVideoView.setUrl(videoUrl,urlType)) {
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
        return mBnVideoView.isPlaying();
    }

    public boolean isUrlEmpty() {
        return mBnVideoView.isUrlEmpty();
    }

    /**
     * @param playType 0:play() 1:playWithChat() 2:audio mix
     */
    public void play(int playType) {
        mBnVideoView.play(playType);
    }

    public void setVolume(int i) {
        mBnVideoView.setVolume(i);
    }

    public void setReceiveVideo(boolean bReceived){
        mBnVideoView.setReceiveVideo(bReceived);
    }

    public void setTextClickListener(ITextClickListener textClickListener) {
        this.mTextClickListener = textClickListener;
    }

    /**
     * 设置混音器
     * @param mixer
     */
    public void setAudioMixer(BNAudioMixer mixer){
        mBnVideoView.setAudioMixer(mixer);
    }

    /**
     * 开始之后调用...
     * @param timeOut
     */
    public void setTimeOut(int timeOut){
        mBnVideoView.setTimeOut(timeOut);
    }

    public void stop() {
        mBnVideoView.stop();
    }

    public void setPlayingListener(BnVideoView2.OnPlayingListener onPlayingListener) {
        this.mOnPlayingListener = onPlayingListener;
    }


    /**
     * pause the video
     */
    public void pause() {
        mBnVideoView.pause();
    }

    public void resume() {
        mBnVideoView.resume();
    }

    public boolean isPaused() {
        return mBnVideoView.isPause();
    }

    public void close(){
        Cog.e(TAG,"close ~");
        mBnVideoView.close();
    }

    public void setEncodeType(int encodeType){
        mBnVideoView.setEncodeType(encodeType);
    }

    public SurfaceHolder getSurfaceHold() {
        return mBnVideoView.getSurfaceHold();
    }

    public void setOnErrorListener(BnVideoView2.OnBNErrorListener errorListener) {
        this.mOnErrorListener = errorListener;
    }

    public void setOnDurationChangeListener(BnVideoView2.OnBNDurationChangeListener durationChangeListener) {
        mBnVideoView.setOnDurationChangeListener(durationChangeListener);
    }

    public void setOnBufferUpdateListener(BnVideoView2.OnBNBufferUpdateListener bufferUpdateListener) {
        mBnVideoView.setOnBufferUpdateListener(bufferUpdateListener);
    }

    public void setOnCompleteListener(BnVideoView2.OnBNCompleteListener completeListener) {
        mBnVideoView.setOnCompleteListener(completeListener);
    }

    public void setOnPlayingListener(BnVideoView2.OnPlayingListener onPlayingListener) {
        this.mOnPlayingListener = onPlayingListener;
    }

    public void setOnSurfaceChangeListener(BnVideoView2.OnSurfaceChangeListener surfaceChangeListener) {
        mBnVideoView.setOnSurfaceChangeListener(surfaceChangeListener);
    }

    public void setOnTipsTouchListener(View.OnClickListener listener){
        mHintTv.setOnClickListener(listener);
    }

    public interface  ITextClickListener {
       void onClick(View v);
    }
}
