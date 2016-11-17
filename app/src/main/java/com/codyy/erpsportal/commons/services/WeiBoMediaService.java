package com.codyy.erpsportal.commons.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.codyy.erpsportal.exam.utils.MediaCheck;
import com.codyy.erpsportal.homework.utils.WorkUtils;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;

import java.io.IOException;

/**
 * Created by kmdai on 16-3-10.
 */
public class WeiBoMediaService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private final static String TAG = "WeiBoMediaService-----";
    private MediaPlayer mMediaPlayer;
    private String mMediaUrl;
    public final static String ACTION_ON_STOP = "com.codyy.weibo.media.stop";
    private AnimationDrawable mAnimationDrawable;//音频播放条动画
    private TextView mTextView;//时长

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
        mMediaPlayer.setLooping(false);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MediaBinder();
    }


    @Override
    public boolean onUnbind(Intent intent) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        if (mMediaCheckHandler != null) {
            mMediaCheckHandler.removeCallbacksAndMessages(null);
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Cog.d(TAG, "onCompletion--");
        playStop();
    }

    /**
     * 音频播放停止（播放完成或播放错误）
     */
    private void playStop() {
        Intent intent = new Intent();
        intent.setAction(ACTION_ON_STOP);
        sendBroadcast(intent);
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Cog.d(TAG, "onError--");
        playStop();
        return true;
    }

    class MediaCheckHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ((boolean) msg.obj) {
                if (mMediaPlayer != null) {
                    try {
                        mMediaPlayer.reset();
                        mMediaPlayer.setDataSource(mMediaUrl);
                        mMediaPlayer.prepareAsync();
                        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                                if (mTextView != null) {
                                    mTextView.setText(mp.getDuration() == -1 ? "未知时长" : WorkUtils.formatTime(mp.getDuration()));
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (mAnimationDrawable != null) {
                    mAnimationDrawable.stop();
                }
                ToastUtil.showToast("音频地址无效!");
            }
        }
    }

    class MediaCheckThread implements Runnable {
        private String url;

        public MediaCheckThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            Message message = new Message();
            MediaCheck check = new MediaCheck();
            message.obj = check.isUsable(url);
            mMediaCheckHandler.sendMessage(message);

        }
    }

    private MediaCheckHandler mMediaCheckHandler;

    public class MediaBinder extends Binder {
        public WeiBoMediaService getService() {
            return WeiBoMediaService.this;
        }

        public void play(String url) {
            mMediaUrl = url;
            mMediaCheckHandler = new MediaCheckHandler();
            MediaCheckThread thread = new MediaCheckThread(url);
            new Thread(thread).start();
        }

        /**
         * 测试、作业模块专用
         *
         * @param url                      播放地址
         * @param animationDrawable 音频动画
         */
        public void playTask(String url, AnimationDrawable animationDrawable, TextView textView) {
            if (mMediaUrl != null && mMediaUrl.equals(url)) {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    if (mAnimationDrawable != null) {
                        if (mAnimationDrawable.isRunning()) {
                            mAnimationDrawable.stop();

                        }
                    }
                } else if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    if (mAnimationDrawable != null) {
                        mAnimationDrawable.start();

                    } else {
                        mAnimationDrawable = animationDrawable;
                    }
                    mAnimationDrawable.start();
                }
            } else {
                stopTask();
                mAnimationDrawable = animationDrawable;
                mAnimationDrawable.start();
                mTextView = textView;
                mMediaUrl = url;
                mMediaCheckHandler = new MediaCheckHandler();
                MediaCheckThread thread = new MediaCheckThread(url);
                new Thread(thread).start();
            }
        }

        /**
         * 测试、作业模块专用
         */
        public void stopTask() {
            if (mAnimationDrawable != null) {
                if (mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.stop();

                }
            }
            mAnimationDrawable = null;
            mTextView = null;
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.reset();
                mMediaPlayer.stop();
            }
        }

        public void stop() {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.reset();
                mMediaPlayer.stop();
            }
        }

        public boolean isPlaying() {
            return mMediaPlayer != null && mMediaPlayer.isPlaying();
        }
    }

}
