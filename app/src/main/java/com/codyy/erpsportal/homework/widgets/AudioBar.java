package com.codyy.erpsportal.homework.widgets;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.exam.utils.MediaCheck;
import com.codyy.erpsportal.homework.utils.WorkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * 音频播放条
 * Created by ldh on 2016/6/14.
 */
public class AudioBar extends LinearLayout implements View.OnClickListener {
    /**
     * 音频条的时长
     */
    private String audioLength;

    /**
     * 音频播放器，单例
     */
    private MediaPlayer mCommentMediaPlayer;

    /**
     * 显示的时长
     */
    private TextView mAudioTimeTv;

    /**
     * 上传进度圈
     */
    private ImageView mProgressIv;

    private ImageView mUploadFailureIv;

    /**
     * 是否显示上传进度圈
     */
    private boolean isShowProgress;
    /**
     * 音频上传时的旋转动画
     */
    private AnimationDrawable mAudioUploadDrawable;
    /**
     * 音频绿条
     */
    private LinearLayout mAudioItemLayout;

    /**
     * 当前正在播放的语音动画
     */
    private AnimationDrawable mCurrentDrawable;

    /**
     * 上一次播放的语音动画 目的：为了使得前一个语音动画停止
     */
    private static AnimationDrawable mPreviousDrawable;

    /**
     * 上一次播放的url
     */
    private static String mPreviousUrl = "";

    /**
     * 播放的URL
     */
    private String mAudioUrl = "";

    /**
     * 音频上传地址
     */
    private String mUploadUrl = "";

    private UploadAsyncTask uploadAsyncTask;


    public AudioBar(Context context) {
        this(context, null);
    }

    public AudioBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.audio_item, this, true);
        mAudioTimeTv = (TextView) view.findViewById(R.id.tv_audio_time); //时长
        mProgressIv = (ImageView) view.findViewById(R.id.iv_uploading);
        mUploadFailureIv = (ImageView) view.findViewById(R.id.iv_audio_upload_failure);
        mUploadFailureIv.setOnClickListener(this);
        mAudioItemLayout = (LinearLayout) view.findViewById(R.id.container_audio);
        mCommentMediaPlayer = WorkAnswerMediaPlayer.newInstance();
        startProgressAnimation();
    }

    /**
     * 设置进度条是否显示,如果显示，则上传
     *
     * @param isVisible
     */
    public void setProgressVisible(boolean isVisible) {
        this.isShowProgress = isVisible;
        if (isShowProgress) {
            upload();
        } else {//当为false时，表明不需要上传，已经保存到本地，需要做的就是获取下时间
            initLocalMedia(mAudioUrl);
        }
    }

    private void initLocalMedia(String url) {
        try {
            mCommentMediaPlayer.stop();
            mCommentMediaPlayer.reset();
            mCommentMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mCommentMediaPlayer.setDataSource(getContext(), Uri.fromFile(new File(url)));
            mCommentMediaPlayer.prepare();
            mProgressIv.setVisibility(GONE);
            mAudioTimeTv.setText(WorkUtils.formatCommentTime(mCommentMediaPlayer.getDuration(), false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void upload() {
        uploadAsyncTask = new UploadAsyncTask() {
            @Override
            public void onMyPreExecute() {
                //startProgressAnimation();
            }

            @Override
            public void onMyProgressUpdate(Integer... values) {

            }

            @Override
            public void onMyPostExecute(String result) {
                setResult(result);
            }
        };
        uploadAsyncTask.execute(mAudioUrl, mUploadUrl);
    }

    private void setResult(String result) {
        if (result == null) {
            setUploadFailure();
            return;
        }
        JSONObject resultObject = null;
        try {
            resultObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if ("true".equals(resultObject.optString("result")) && mAudioListener != null) {
            mAudioListener.insertAudioInfo(resultObject.optString("originalName"), resultObject.optString("message"));
            setUploadSuccess();
        } else {
            setUploadFailure();
        }
    }


    private void startProgressAnimation() {
        if (mProgressIv != null) {
            mProgressIv.setVisibility(View.VISIBLE);
            mUploadFailureIv.setVisibility(View.INVISIBLE);
            mAudioUploadDrawable = (AnimationDrawable) mProgressIv.getBackground();
            mAudioUploadDrawable.start();
        }
    }

    /**
     * 上传成功
     */
    public void setUploadSuccess() {
        if (mProgressIv != null && mAudioUploadDrawable != null) {
            mAudioUploadDrawable.stop();
            mProgressIv.setVisibility(View.GONE);
            //当前一次上传失败，显示了感叹号，点击再次上传，若成功，则让感叹号消失
            if (mUploadFailureIv != null) {
                mUploadFailureIv.setVisibility(GONE);
            }
        }
    }

    /**
     * 上传失败
     */
    public void setUploadFailure() {
        if (mUploadFailureIv != null && mAudioUploadDrawable != null) {
            mAudioUploadDrawable.stop();
            mProgressIv.setVisibility(View.GONE);
            mUploadFailureIv.setVisibility(VISIBLE);
        }
    }

    public void setUrl(String audioUrl, String upLoadUrl, boolean isCommentAudio) {
        mAudioUrl = audioUrl;
        mUploadUrl = upLoadUrl;
        //audioLength = initMediaPlayer(audioUrl, isCommentAudio);
        if (!upLoadUrl.equals("")) {
            MediaCheckThread thread = new MediaCheckThread(audioUrl);
            new Thread(thread).start();
        }
        mAudioItemLayout.setTag(audioUrl);
        setAudioBarLength(isCommentAudio);
        setListener();
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
            MediaInfo mediaInfo = new MediaInfo(url, false, check.isUsable(url));
            message.obj = mediaInfo;
            mAudioHandler.sendMessage(message);

        }
    }

    class MediaInfo {
        private String url;
        private boolean isComment;
        private boolean isUsable;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isComment() {
            return isComment;
        }

        public void setComment(boolean comment) {
            isComment = comment;
        }

        public boolean isUsable() {
            return isUsable;
        }

        public void setUsable(boolean usable) {
            isUsable = usable;
        }

        public MediaInfo(String url, boolean isComment, boolean isUsable) {
            this.url = url;
            this.isComment = isComment;
            this.isUsable = isUsable;
        }
    }

    private AudioHandler mAudioHandler = new AudioHandler();

    class AudioHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (((MediaInfo) msg.obj).isUsable) {
                initMediaPlayer(((MediaInfo) msg.obj).url, false);
            } else if (mProgressIv != null) {
                mProgressIv.setVisibility(GONE);
            }
        }
    }


    /**
     * 设置进度条长度
     */
    private void setAudioBarLength(boolean isCommentAudio) {
        ViewGroup.LayoutParams params = mAudioItemLayout.getLayoutParams();//根据时长显示不同长度的音频条
        if (isCommentAudio) {
            params.width = params.width + Integer.valueOf(audioLength.substring(0, audioLength.length() - 1)) * 10;
        } else {
            params.width = params.width + 100;
        }

        mAudioItemLayout.setLayoutParams(params);
    }

    /**
     * 所有的监听都放置于此
     */
    private void setListener() {
        mAudioItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommentMediaPlayer != null) {
                    if (compareUrl(mPreviousUrl, v.getTag().toString())) {//点击的是同一个语音播放条
                        if (mCommentMediaPlayer.isPlaying()) {
                            stopPlay(v);
                        } else {
                            startPlay(v);
                        }
                    } else {//点击的是不同的语音条
                        if (mCommentMediaPlayer.isPlaying()) {//正在播放一个语音时，点击另一个语音条
                            mPreviousDrawable.stop();
                            stopPlay(v);
                            startPlay(v);
                        } else {
                            startPlay(v);
                        }
                    }
                    mPreviousUrl = v.getTag().toString();
                    mPreviousDrawable = mCurrentDrawable;
                    mCommentMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (mCurrentDrawable != null)
                                mCurrentDrawable.stop();
                        }
                    });
                    Log.d("AudioBar", v.getTag().toString());
                }
            }
        });

        mAudioItemLayout.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mAudioListener != null) {
                    mAudioListener.longClick(v.getTag().toString());
                }
                if (uploadAsyncTask != null) {
                    uploadAsyncTask.setCancel(true);
                }
                return true;
            }
        });
    }

    private void startPlay(final View v) {
        if (!mAudioTimeTv.getText().equals("")) {
            initMediaPlayer(v.getTag().toString(), false);
            mCommentMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mAudioTimeTv.getText().equals("")) {
                        mAudioTimeTv.setText(WorkUtils.formatCommentTime(mCommentMediaPlayer.getDuration(), false));
                    }
                    mCommentMediaPlayer.start();
                    mCurrentDrawable = (AnimationDrawable) v.findViewById(R.id.iv_audio_bar).getBackground();
                    mPreviousDrawable = mCurrentDrawable;
                    startDrawable();
                }
            });
        } else {
            ToastUtil.showToast("播放失败!");
            if (mAudioUploadDrawable != null) {
                if (mAudioUploadDrawable.isRunning()) {
                    mProgressIv.setVisibility(INVISIBLE);
                }
            }
        }
    }

    private void stopPlay(View v) {
        mCommentMediaPlayer.stop();
        mCurrentDrawable = (AnimationDrawable) v.findViewById(R.id.iv_audio_bar).getBackground();
        stopDrawable();
    }

    /**
     * 转换语音条动画的状态
     */
    private void startDrawable() {
        if (mCurrentDrawable != null) {
            mCurrentDrawable.start();
        }
    }

    private void stopDrawable() {
        if (mCurrentDrawable != null) {
            mCurrentDrawable.stop();
        }
    }

    /**
     * 比较两个播放地址是否一样
     *
     * @param curUrl 当前的播放地址
     * @param preUrl 前一个播放地址
     * @return
     */
    private boolean compareUrl(String curUrl, String preUrl) {
        if (curUrl.equals(preUrl))
            return true;
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_audio_upload_failure:
                upload();
                break;
        }
    }

    public interface OnAudioListener {
        /**
         * 长按时调用
         *
         * @param tag
         */
        void longClick(String tag);

        /**
         * 音频上传成功后本地保存记录
         */
        void insertAudioInfo(String originalName, String messageId);

    }

    private OnAudioListener mAudioListener;

    public void setOnAudioLongClickListener(OnAudioListener listener) {
        mAudioListener = listener;
    }

    /**
     * 设置播放器地址
     *
     * @param playUrl
     * @return 时长
     */
    private String initMediaPlayer(final String playUrl, final boolean isCommentAudio) {
        String audioLength = "";
        try {
            mCommentMediaPlayer.stop();
            mCommentMediaPlayer.reset();
            mCommentMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (playUrl.contains("http")) {
                mCommentMediaPlayer.setDataSource(playUrl);
            } else {
                mCommentMediaPlayer.setDataSource(getContext(), Uri.fromFile(new File(playUrl)));
            }
            mCommentMediaPlayer.prepareAsync();
            mCommentMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mAudioTimeTv.getText().equals("")) {
                        mAudioTimeTv.setText(WorkUtils.formatCommentTime(mCommentMediaPlayer.getDuration(), isCommentAudio));
                        if(!isShowProgress){
                            mProgressIv.setVisibility(INVISIBLE);
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return audioLength;
    }

    public String getLength() {
        return audioLength;
    }

    public static AnimationDrawable getAnimationDrawable() {
        return mPreviousDrawable;
    }
}
