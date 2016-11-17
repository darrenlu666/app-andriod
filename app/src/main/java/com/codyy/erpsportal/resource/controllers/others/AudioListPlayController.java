package com.codyy.erpsportal.resource.controllers.others;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.resource.controllers.adapters.ResourcesAdapter;
import com.codyy.erpsportal.resource.models.entities.Audio;
import com.codyy.erpsportal.resource.models.entities.AudioEvent;
import com.codyy.erpsportal.resource.widgets.AudioControlBar;
import com.codyy.erpsportal.resource.widgets.AudioControlBar.ControlBarCallback;

import java.io.IOException;
import java.util.List;

/**
 * 音频列表播放控制器
 * Created by gujiajia on 2016/9/8.
 */
public class AudioListPlayController {

    private static final String TAG = "AudioListPlayController";

    private final static int MSG_UPDATE_PROGRESS = 21;

    private Activity mActivity;

    private ResourcesAdapter mAdapter;

    private AudioControlBar mAudioControlBar;

    private Audio mCurrentAudio;

    private boolean mIsAudioPlaying;

    private MediaPlayer mMediaPlayer;

    private AudioManager mAudioManager;

    private Handler mHandler;

    private Handler mAudioHandler;

    private HandlerThread mHandlerThread;

    private boolean mAudioFocusGranted;

    private OnAudioPlayingListener mOnAudioPlayingListener;

    private AudioListPlayController(Activity activity) {
        this.mActivity = activity;
        mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        mHandler = new Handler(mCallback);
        mHandlerThread = new HandlerThread("audio");
        mHandlerThread.start();
        mAudioHandler = new Handler(mHandlerThread.getLooper());
    }

    public void onAudioClick(AudioEvent audioEvent) {
        Audio audio = audioEvent.getAudio();
        if (!audio.isPlaying() && requestAudioFocus()) {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
                mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
                mMediaPlayer.setOnErrorListener(mOnErrorListener);
            }
            playOrResume(audio);
        } else {//暂停
            if (audio.equals(mCurrentAudio)) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    updatePlayPauseIb(false);
                    audio.setPlaying(false);
                    updateAudioInList(audio);
                    stopUpdateProgressBar();
                }
            } else {
                Cog.e(TAG, "Audio pausing is different from current.");
            }
        }
    }

    /**
     * 请求音频焦点
     * @return
     */
    private boolean requestAudioFocus() {
        if (mAudioFocusGranted) return true;
        int result = mAudioManager.requestAudioFocus(mAfChangeListener
                , AudioManager.STREAM_MUSIC
                , AudioManager.AUDIOFOCUS_GAIN);
        mAudioFocusGranted = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        return mAudioFocusGranted;
    }

    private void abandonAudioFocus() {
        if (mAudioFocusGranted) {
            mAudioManager.abandonAudioFocus(mAfChangeListener);
            mAudioFocusGranted = false;
        }
    }

    private OnAudioFocusChangeListener mAfChangeListener = new OnAudioFocusChangeListener() {

        private boolean mPausedByMe;

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                mPausedByMe = true;
                pauseAudio();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (mPausedByMe) {
                    mPausedByMe = false;
                    playOrResume(mCurrentAudio);
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                mPausedByMe = true;
                stopAudio();
            }
        }
    };

    public void onPause() {
        if (mMediaPlayer != null) {
            pauseAudio();
            abandonAudioFocus();
            updatePlayPauseIb(false);
        }
    }

    public void stopAudio() {
        abandonAudioFocus();
        if (mMediaPlayer != null) {
            mHandler.removeMessages(MSG_UPDATE_PROGRESS);
            mMediaPlayer.release();
            mCurrentAudio = null;
            mMediaPlayer = null;
        }
        hideAudioBar();
    }

    public void onDestroy() {
        mAudioHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer != null){
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    Looper looper = Looper.myLooper();
                    if (looper != null) looper.quit();
                }
            }
        });
    }

    private void playOrResume(Audio audio) {
        if (!requestAudioFocus()) {
            Toast.makeText(mActivity, "音频输出被占用了，无法播放。", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCurrentAudio == null || !mCurrentAudio.equals(audio)) {
            if (mCurrentAudio != null) {
                mCurrentAudio.setPlaying(false);
                updateAudioInList(mCurrentAudio);
            }
            mCurrentAudio = audio;
            mAudioControlBar.setTrackName(mCurrentAudio.getName());
            mAudioHandler.post(new Runnable() {
                @Override
                public void run() {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            doAfterReset();
                        }
                    });
                }
            });
        } else {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
                mIsAudioPlaying = true;
                updatePlayPauseIb(true);
                mCurrentAudio.setPlaying(true);
                updateAudioInList(mCurrentAudio);
                startUpdateProgressBar();
            }
        }
    }

    private void doAfterReset() {
        stopUpdateProgressBar();
        mAudioControlBar.setProgress(0);
        try {
            Uri uri = Uri.parse(mCurrentAudio.getStreamUrl());
            Cog.d(TAG, uri);
            mMediaPlayer.setDataSource(mActivity, uri);
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.prepareAsync();

            mCurrentAudio.setPlaying(true);
            updateAudioInList(mCurrentAudio);
            showMediaControlBar();
            updatePlayPauseIb(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            Cog.d(TAG, "onPrepared sessionId=", mp.getAudioSessionId());

            if (mOnAudioPlayingListener != null)
                mOnAudioPlayingListener.onAudioPlaying(getCurrentAudio().getId());
            mp.start();
            mIsAudioPlaying = true;
            startUpdateProgressBar();
        }
    };

    private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Cog.d(TAG, "onCompletion sessionId=", mp.getAudioSessionId());
            Audio nextAudio = nextAudio();
            if (nextAudio != null) {
                playOrResume(nextAudio);
            } else {
                mMediaPlayer.pause();
                mIsAudioPlaying = false;
                updatePlayPauseIb(false);
                mCurrentAudio.setPlaying(false);
                updateAudioInList(mCurrentAudio);
                stopUpdateProgressBar();
            }
        }
    };

    private OnErrorListener mOnErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Cog.d(TAG, "mOnErrorListener onError what=", what, ",extra=", extra);
            mCurrentAudio.setPlaying(false);
            stopUpdateProgressBar();
            Toast.makeText(mActivity, "无法播放", Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    private Audio nextAudio() {
        Audio nextAudio = null;
        int hitIndex = -1;//搜寻到当前正在播放的
        List items = mAdapter.getData();
        for (int i=0; i<items.size(); i++) {
            Object obj = items.get(i);
            if (obj instanceof Audio) {
                Audio audio = (Audio)obj;
                if (hitIndex > -1) {
                    nextAudio = audio;
                    break;
                }
                if (audio.equals(mCurrentAudio)) {
                    hitIndex = i;
                }
            }
        }
        if (hitIndex > -1 && nextAudio != null) {
            mCurrentAudio.setPlaying(false);
            mAdapter.notifyItemChanged(hitIndex);
        }
        return nextAudio;
    }

    private Audio previousAudio() {
        Audio previousAudio = null;
        int hitIndex = -1;//搜寻到当前正在播放的
        List items = mAdapter.getData();
        for (int i=items.size()-1; i>=0; i--) {
            Object obj = items.get(i);
            if (obj instanceof Audio) {
                Audio audio = (Audio)obj;
                if (hitIndex > -1) {
                    previousAudio = audio;
                    break;
                }
                if (audio.equals(mCurrentAudio)) {
                    hitIndex = i;
                }
            }
        }
        if (hitIndex > -1 && previousAudio != null) {
            mCurrentAudio.setPlaying(false);
            mAdapter.notifyItemChanged(hitIndex);
        }
        return previousAudio;
    }

    /**
     * 显示音频播放控制条
     */
    private void showMediaControlBar() {
        if (mAudioControlBar.getVisibility() != View.VISIBLE) {
            mAudioControlBar.setVisibility(View.VISIBLE);
            mIsToHideBar = false;
            mAudioControlBar.setTranslationY(mAudioControlBar.getHeight());
            mAudioControlBar.animate().translationY(0).setDuration(500).setListener(mAnimatorListener);
        }
    }

    private AnimatorListener mAnimatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            if (mIsToHideBar) mAudioControlBar.setVisibility(View.INVISIBLE);
        }
    };

    private void hideAudioBar() {
        if (mAudioControlBar.getVisibility() == View.VISIBLE) {
            mIsToHideBar = true;
            mAudioControlBar.animate().translationY(mAudioControlBar.getHeight()).setDuration(500);
        }
    }

    private boolean mIsToHideBar;

    /**
     * 更新单个Audio状态
     */
    private void updateAudioInList(@NonNull Audio currentAudio) {
        List items = mAdapter.getData();
        int position = items.indexOf(currentAudio);
        if (position >=0 ) {
            mAdapter.notifyItemChanged(position);
        }
    }

    private ControlBarCallback mControlBarCallback = new ControlBarCallback() {
        @Override
        public void onPlayClick() {
            playOrPause();
        }

        @Override
        public void onNextClick() {
            playNextAudio();
        }

        @Override
        public void onPreviousClick() {
            playPreviousAudio();
        }
    };

    private void playOrPause() {
        if (mIsAudioPlaying) {
            pauseAudio();
            updatePlayPauseIb(false);
        } else {
            mIsAudioPlaying = true;
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
                mCurrentAudio.setPlaying(true);
                updateAudioInList( mCurrentAudio);
                startUpdateProgressBar();
            }
            updatePlayPauseIb(true);
        }
    }

    private void playNextAudio() {
        Audio nextAudio = nextAudio();
        if (nextAudio == null) {
            Toast.makeText(mActivity, "已经是最后一首了", Toast.LENGTH_LONG).show();
        } else {
            playOrResume(nextAudio);
        }
    }

    private void playPreviousAudio() {
        Audio previousAudio = previousAudio();
        if (previousAudio == null) {
            Toast.makeText(mActivity, "已经是第一首了", Toast.LENGTH_LONG).show();
        } else {
            playOrResume(previousAudio);
        }
    }

    private void pauseAudio() {
        mIsAudioPlaying = false;
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mCurrentAudio.setPlaying(false);
            updateAudioInList( mCurrentAudio);
            stopUpdateProgressBar();
        }
    }

    private void updateProgressBar() {
        if (mMediaPlayer == null) return;
        int position = mMediaPlayer.getCurrentPosition();
        int duration = mMediaPlayer.getDuration();
        if (duration == 0) {
            mAudioControlBar.setProgress(mAudioControlBar.getMax());
        } else {
            mAudioControlBar.setProgress(position * mAudioControlBar.getMax() / duration);
        }
    }

    private Callback mCallback = new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE_PROGRESS) {
                updateProgressBar();
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS, 100);
                return true;
            }
            return false;
        }
    };

    private void startUpdateProgressBar(){
        mHandler.removeMessages(MSG_UPDATE_PROGRESS);
        mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS);
    }

    private void stopUpdateProgressBar(){
        mHandler.removeMessages(MSG_UPDATE_PROGRESS);
    }

    private void updatePlayPauseIb(boolean playing) {
        mAudioControlBar.setPlayingStatus(playing);
    }

    public void setAudioControlBar(AudioControlBar audioControlBar) {
        mAudioControlBar = audioControlBar;
        mAudioControlBar.setCallback(mControlBarCallback);
    }

    public Audio getCurrentAudio() {
        return mCurrentAudio;
    }

    /**
     * 更新当前音频
     * @param audio
     */
    public void updateCurrentAudio(Audio audio) {
        Audio currAudio = mCurrentAudio;
        if (currAudio == null) return;
        if (audio.equals(currAudio)) {
            audio.setPlaying(currAudio.isPlaying());
            mCurrentAudio = audio;
        }
    }

    public void setOnAudioPlayingListener(OnAudioPlayingListener onAudioPlayingListener) {
        mOnAudioPlayingListener = onAudioPlayingListener;
    }

    public static class Builder {

        private Activity activity;

        private ResourcesAdapter adapter;

        private AudioControlBar audioControlBar;

        private OnAudioPlayingListener onAudioPlayingListener;

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setAdapter(ResourcesAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder setAudioControlBar(AudioControlBar audioControlBar) {
            this.audioControlBar = audioControlBar;
            return this;
        }

        public Builder setOnAudioPlayingListener(OnAudioPlayingListener audioPlayingListener) {
            this.onAudioPlayingListener = audioPlayingListener;
            return this;
        }

        public AudioListPlayController build() {
            AudioListPlayController controller = new AudioListPlayController(activity);
            controller.mAdapter = adapter;
            controller.setAudioControlBar( audioControlBar);
            controller.setOnAudioPlayingListener( onAudioPlayingListener);
            return controller;
        }
    }

    public interface OnAudioPlayingListener {
        void onAudioPlaying(String audioId);
    }
}
