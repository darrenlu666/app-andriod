package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;

/**
 * Created by gujiajia on 2015/6/17.
 */
public class BnVideoLayout extends FrameLayout implements BnVideoView.OnPlayingListener, BnVideoView.OnBNErrorListener {

    private BnVideoView mBnVideoView;

    private TextView mHintTv;

    private BnVideoView.OnPlayingListener mOnPlayingListener;

    public BnVideoLayout(Context context) {
        this(context, null);
    }

    public BnVideoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BnVideoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BnVideoLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs){
        if(isInEditMode()){
            return;
        }
        LayoutInflater.from(getContext()).inflate(R.layout.bn_video_layout, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBnVideoView = (BnVideoView) findViewById(R.id.bnVideoView);
        mHintTv = (TextView) findViewById(R.id.hintText);
        if (isInEditMode()){
            return;
        }
        mBnVideoView.setPlayingListener(this);
        mBnVideoView.setErrorListener(this);
    }

    @Override
    public void onPlaying() {
        post(new Runnable() {
            @Override
            public void run() {
                mHintTv.setVisibility(INVISIBLE);
            }
        });
        if (mOnPlayingListener != null) mOnPlayingListener.onPlaying();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mOnPlayingListener != null) mOnPlayingListener.surfaceCreated(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mOnPlayingListener != null) mOnPlayingListener.surfaceDestroyed(holder);
    }

    @Override
    public void onError(int errorCode) {
        if (errorCode == -2) {
            post(new Runnable() {
                @Override
                public void run() {
                    mHintTv.setText(R.string.txt_live_no_input_stream);
                    mHintTv.setVisibility(VISIBLE);
                }
            });
        }
    }

    public BnVideoView getVideoView() {
        return mBnVideoView;
    }

    public void setUrl(String videoUrl) {
        if (mBnVideoView.setUrl(videoUrl)) {
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

    public void close() {
        mBnVideoView.close();
    }

    public void play() {
        mBnVideoView.play();
    }

    public void playNow(){
        mBnVideoView.playNow();
    }

    public void setVolume(int i) {
        mBnVideoView.setVolume(i);
    }

    public void stop() {
        mBnVideoView.stop();
    }

    public void setPlayingListener(BnVideoView.OnPlayingListener onPlayingListener) {
        this.mOnPlayingListener = onPlayingListener;
    }

  /*  public void setAssistUris(String[] videos) {
       mBnVideoView.setAssistUris(videos);
    }*/

    public void pause() {
        mBnVideoView.pause();
    }

    public void resume() {
        mBnVideoView.resume();
    }
}
