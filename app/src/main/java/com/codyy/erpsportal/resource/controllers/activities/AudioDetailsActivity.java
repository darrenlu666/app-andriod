package com.codyy.erpsportal.resource.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.VideoDownloadUtils;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.resource.controllers.adapters.TabsAdapter;
import com.codyy.erpsportal.resource.controllers.fragments.ResCommentsFragment;
import com.codyy.erpsportal.resource.controllers.fragments.ResourceDetailsFragment;
import com.codyy.erpsportal.resource.models.entities.Audio;
import com.codyy.erpsportal.resource.models.entities.ResourceDetails;
import com.codyy.erpsportal.resource.utils.CountIncreaser;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 音频资源详情
 * Created by gujiajia on 2016/7/6.
 */
public class AudioDetailsActivity extends AppCompatActivity implements Callback {

    private final static String TAG = "AudioDetailsActivity";

    private final static String EXTRA_POSITION = "com.codyy.erpsportal.AUDIO_POSITION";

    private final static String EXTRA_AUDIO_LIST = "com.codyy.erpsportal.AUDIO_LIST";

    private static final String EXTRA_FROM_CACHE = "com.codyy.erpsportal.EXTRA_FROM_CACHE";

    private final static int MSG_UPDATE_PROGRESS = 0xaa;

    @Bind(R.id.title_bar)
    TitleBar mTitleBar;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    @Bind(android.R.id.tabhost)
    TabHost mTabHost;

    @Bind(android.R.id.tabs)
    TabWidget mTabWidget;

    @Bind(R.id.btn_download)
    ImageButton mDownloadBtn;

    @Bind(R.id.tv_current)
    TextView mCurrentTv;

    @Bind(R.id.seek_bar)
    SeekBar mSeekBar;

    @Bind(R.id.tv_duration)
    TextView mDurationTv;

    @Bind(R.id.btn_play)
    ImageButton mPlayBtn;

    @Bind(R.id.btn_previous)
    ImageButton mPreviousBtn;

    @Bind(R.id.btn_next)
    ImageButton mNextBtn;

    private MediaPlayer mAudioPlayer;

    private TabsAdapter mTabsAdapter;

    private UserInfo mUserInfo;

    private String mResourceId;

    private String mClassId;

    private List<Audio> mAudioList;

    private int mPosition;

    private boolean mIsAudioPlaying;

    private RequestSender mRequestSender;

    private Object mRequestTag = new Object();

    private ResourceDetails mResourceDetails;

    private Handler mHandler;

    private Handler mAudioHandler;

    private AudioManager mAudioManager;

    private OnAudioFocusChangeListener mAfChangeListener = new OnAudioFocusChangeListener() {

        private boolean mPauseByAudioFocusLoss = false;

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                mPauseByAudioFocusLoss = true;
                pauseAudio();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (mPauseByAudioFocusLoss) {
                    mPauseByAudioFocusLoss = false;
                    playOrResume(mPosition);
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS){
                mPauseByAudioFocusLoss = true;
                pauseAudio();
                abandonAudioFocus();
            }
        }
    };

    private ResourceDetailsFragment mResDetailsFragment;

    private boolean mAudioFocusGranted;

    /**
     * 从缓存页进入，无需显示下载
     */
    private boolean mFromCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_details);
        ButterKnife.bind(this);
        initAttributes();
        initViews(savedInstanceState);
    }

    private void initAttributes() {
        mHandler = new Handler(this);
        HandlerThread handlerThread = new HandlerThread("audio");
        handlerThread.start();
        mAudioHandler = new Handler(handlerThread.getLooper());

        mRequestSender = new RequestSender(this);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mAudioList = getIntent().getParcelableArrayListExtra(EXTRA_AUDIO_LIST);
        mPosition = getIntent().getIntExtra(EXTRA_POSITION, -1);
        mClassId = getIntent().getStringExtra(Extra.CLASS_ID);
        mFromCache = getIntent().getBooleanExtra(EXTRA_FROM_CACHE, false);
        if (mPosition == -1) {
            Cog.e(TAG, "onCreate: Illegal position");
        }

        mResourceId = mAudioList.get(mPosition).getId();
    }

    private void initViews(Bundle savedInstanceState) {
        mTabHost.setup();
        mTabsAdapter = new TabsAdapter(this, getSupportFragmentManager(), mTabHost, mViewPager);
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTab(savedInstanceState.getInt("tab"));
        }

        if (mFromCache) {
            mDownloadBtn.setVisibility(View.GONE);
        }

        addResourceDetailsFragment();
        addResourceCommentsFragment();
    }

    private boolean mAudioPlayerInitialed;

    @Override
    protected void onStart() {
        super.onStart();
        if (!mAudioPlayerInitialed) {
            initAudioPlayer();
        }
    }

    private void initAudioPlayer() {
        mAudioPlayer = new MediaPlayer();
        mAudioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mAudioPlayer.setOnPreparedListener( mOnPreparedListener);
        mAudioPlayer.setOnCompletionListener( mOnCompletionListener);
        mAudioPlayer.setOnErrorListener( mOnErrorListener);
        mAudioPlayerInitialed = true;
        if (requestAudioFocus()) {
            startToPlay();
        }
    }

    private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            Cog.d(TAG, "onPrepared sessionId=", mp.getAudioSessionId());
            CountIncreaser.increaseViewCount(mRequestSender, mRequestTag, mUserInfo.getUuid(), mResourceId);
            mp.start();
            mIsAudioPlaying = true;
            updatePlayPauseIb();
            startUpdateProgressBar();
            mDurationTv.setText(acquireTimeStamp(mp.getDuration()));
        }
    };

    private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Cog.d(TAG, "onCompletion sessionId=", mp.getAudioSessionId());
            int position = mPosition + 1;
            if (position >= mAudioList.size()) {
                mAudioPlayer.pause();
                mIsAudioPlaying = false;
                updatePlayPauseIb();
                stopUpdateProgressBar();
            } else {
                playOrResume(position);
            }
        }
    };

    private OnErrorListener mOnErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Cog.e(TAG, "mOnErrorListener onError what=", what, ",extra=" + extra);
            ToastUtil.showToast(AudioDetailsActivity.this, "播放失败！");
            return true;
        }
    };

    private boolean requestAudioFocus() {
        if (mAudioFocusGranted) return true;
        int result = mAudioManager.requestAudioFocus(mAfChangeListener
                , AudioManager.STREAM_MUSIC
                , AudioManager.AUDIOFOCUS_GAIN);
        mAudioFocusGranted = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        return mAudioFocusGranted;
    }

    private void abandonAudioFocus() {
        mAudioFocusGranted = false;
        mAudioManager.abandonAudioFocus(mAfChangeListener);
    }

    private void playOrResume(int position) {
        if (!requestAudioFocus()) return;
        if (position != mPosition) {
            mPosition = position;
            startToPlay();
        } else {
            resumeAudio();
        }
    }

    private void startToPlay() {
        Audio currentAudio = mAudioList.get(mPosition);
        mResourceId = currentAudio.getId();
        updateDetailsAndComments();
        mTitleBar.setText(currentAudio.getName());
        final String streamUrl = currentAudio.getStreamUrl();
        mAudioHandler.post(new Runnable() {
            @Override
            public void run() {
                mAudioPlayer.stop();
                mAudioPlayer.reset();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        doAfterReset(streamUrl);
                    }
                });
            }
        });

    }

    private void doAfterReset(String streamUrl) {
        stopUpdateProgressBar();
        mSeekBar.setProgress(0);
        try {
            Uri uri = Uri.parse(streamUrl);
            Cog.d(TAG, "playOrResume uri=", uri);
            mAudioPlayer.setDataSource(this, uri);
            mAudioPlayer.prepareAsync();
        } catch (IOException e) {
            Toast.makeText(this, "无法播放", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void resumeAudio() {
        if (!mAudioPlayer.isPlaying()) {
            mAudioPlayer.start();
            mIsAudioPlaying = true;
            updatePlayPauseIb();
            startUpdateProgressBar();
        }
    }

    /**
     * 更新详情与评论
     */
    private void updateDetailsAndComments() {
        loadDetails();
        EventBus.getDefault().post(mResourceId);
    }

    private void updatePlayPauseIb() {
        if (mIsAudioPlaying){
            mPlayBtn.setImageResource(R.drawable.btn_detail_pause);
        } else {
            mPlayBtn.setImageResource(R.drawable.btn_detail_play);
        }
    }

    private String acquireTimeStamp(int mileSecond) {
        int second = Math.round(mileSecond / 1000f);
        int minute = second / 60;
        second = second % 60;
        if (second < 10) {
            return minute + ":0" + second;
        }
        return minute + ":" + second;
    }

    private void startUpdateProgressBar(){
        mHandler.removeMessages(MSG_UPDATE_PROGRESS);
        mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS);
    }

    private void stopUpdateProgressBar(){
        mHandler.removeMessages(MSG_UPDATE_PROGRESS);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MSG_UPDATE_PROGRESS) {
            updateProgressBar();
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS,100);
            return true;
        }
        return false;
    }

    private void updateProgressBar() {
        Cog.d(TAG, "updateProgressBar");
        if (!mAudioPlayer.isPlaying()) return;
        int position = mAudioPlayer.getCurrentPosition();
        int duration = mAudioPlayer.getDuration();
        if (duration == 0) {
            mSeekBar.setProgress(mSeekBar.getMax());
            mCurrentTv.setText(R.string.zero_time);
        } else {
            mSeekBar.setProgress(position * mSeekBar.getMax() / duration);
            mCurrentTv.setText(acquireTimeStamp(position));
        }
    }

    /**
     * 更新当前播放进度
     */
    private void updateCurrent() {
        if (!mAudioPlayer.isPlaying()) return;
        int position = mAudioPlayer.getCurrentPosition();
        int duration = mAudioPlayer.getDuration();
        if (duration == 0) {
            mCurrentTv.setText(R.string.zero_time);
        } else {
            mCurrentTv.setText(acquireTimeStamp(position));
        }
    }

    @OnClick(R.id.btn_download)
    public void onDownloadBtnClick(){
        if (mResourceDetails == null) return;
        if (VideoDownloadUtils.downloadAudio(mResourceDetails
                , mResourceDetails.getAttachPath()
                , mUserInfo.getBaseUserId())) {
            CountIncreaser.increaseDownloadCount(mRequestSender, mRequestTag, mUserInfo.getUuid(), mResourceId);
        }
    }

    @OnClick({R.id.btn_play, R.id.btn_previous, R.id.btn_next})
    public void onAudioControlBtnClick(View view) {
        switch (view.getId()){
            case R.id.btn_play:
                playOrPause();
                break;
            case R.id.btn_previous: {
                playNext();
                break;
            }
            case R.id.btn_next: {
                playPrevious();
                break;
            }
        }
    }

    private void playOrPause() {
        if (mIsAudioPlaying) {
            pauseAudio();
            updatePlayPauseIb();
        } else {
            if (mAudioPlayer == null) {
                initAudioPlayer();
            } else {
                resumeAudio();
            }
        }
    }

    private void playNext() {
        int position = mPosition - 1;
        if (position < 0) {
            Toast.makeText(AudioDetailsActivity.this, R.string.first_song_already, Toast.LENGTH_LONG).show();
        } else {
            playOrResume(position);
        }
    }

    private void playPrevious() {
        int position = mPosition + 1;
        if (position > mAudioList.size() - 1) {
            Toast.makeText(AudioDetailsActivity.this, R.string.last_song_already, Toast.LENGTH_LONG).show();
        } else {
            playOrResume(position);
        }
    }

    private void pauseAudio() {
        mIsAudioPlaying = false;
        if (mAudioPlayer != null && mAudioPlayer.isPlaying()) {
            mAudioPlayer.pause();
            updatePlayPauseIb();
            stopUpdateProgressBar();
        }
    }

    /**
     * 添加资源评论碎片页
     */
    private void addResourceCommentsFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ResCommentsFragment.ARG_USER_INFO, mUserInfo);
        bundle.putString(ResCommentsFragment.ARG_RESOURCE_ID, mResourceId);
        mTabsAdapter.addTab(mTabHost.newTabSpec("tab_comments").setIndicator(makeTabIndicator(getString(R.string.comment))),
                ResCommentsFragment.class, bundle);
    }

    /**
     * 添加资源详情碎片页
     */
    private void addResourceDetailsFragment() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ResourceDetailsFragment.ARG_IS_MEDIA, true);//设置为显示视频详情，显示时长而非大小
        if (!TextUtils.isEmpty(mClassId)) {
            bundle.putBoolean(ResourceDetailsFragment.ARG_SHOW_SHARER, true);
        }
        mTabsAdapter.addTab(mTabHost.newTabSpec("tab_video_details").setIndicator(makeTabIndicator("资源详情")),
                ResourceDetailsFragment.class, bundle);
    }

    /**
     * 创建标签
     *
     * @param title 标签标题
     * @return 标签组件
     */
    private View makeTabIndicator(String title) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tab, mTabWidget, false);
        TextView tabTitleTv = (TextView) view.findViewById(R.id.tab_title);
        tabTitleTv.setText(title);
        return view;
    }

    /**
     * 加载资源详情数据
     */
    private void loadDetails() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("resourceId", mResourceId);
        if (!TextUtils.isEmpty(mClassId)) {
            params.put("baseClassId", mClassId);
        }
        Cog.d(TAG, "loadDetails url=", URLConfig.RESOURCE_DETAILS);
        mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.RESOURCE_DETAILS, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                String result = response.optString("result");
                if (mTitleBar == null) return;
                if ("success".equals(result)) {
                    JSONObject detailsJsonObject = response.optJSONObject("data");
                    mResourceDetails = ResourceDetails.parseJson(detailsJsonObject);
                    if (mResourceDetails != null) {
                        mTitleBar.setText(mResourceDetails.getResourceName());
                    }

                    if(mResDetailsFragment == null) {
                        mResDetailsFragment = (ResourceDetailsFragment) getSupportFragmentManager()
                                .findFragmentByTag(UIUtils.obtainFragmentTag(mViewPager.getId(),
                                        mTabsAdapter.getItemId(0)));
                    }
                    if (mResDetailsFragment != null) {
                        mResDetailsFragment.setResourceDetails(mResourceDetails);
                    }
                    updateDownloadBtn();
                    playAudio(mResourceDetails);
                } else if ("error".equals(result)) {
                    String message = response.optString("message");
                    UIUtils.toast(AudioDetailsActivity.this, message, Toast.LENGTH_SHORT);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.d(TAG, "onErrorResponse:" + error);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
            }
        }));
    }

    private void updateDownloadBtn() {
        if (mFromCache) return;
        if (mResourceDetails == null || !TextUtils.isEmpty(mResourceDetails.getRtmpPath())) {
            mDownloadBtn.setVisibility(View.GONE);
        } else {
            mDownloadBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 播放视频
     *
     * @param resourceDetails 资源详情
     */
    private void playAudio(ResourceDetails resourceDetails) {
        if(resourceDetails == null ){
            return;
        }

        Cog.d(TAG, "playAudio url=" + resourceDetails.getPlayUrl());
        //检测网络环境 ,如果是3G则给提示
        String videoUrl=null;
        if (!TextUtils.isEmpty(resourceDetails.getPlayUrl())) {
            videoUrl = resourceDetails.getPlayUrl();
        }
        Cog.d(TAG, "playAudio url=" + videoUrl);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putInt("tab", mTabHost.getCurrentTab());
        }
        super.onSaveInstanceState(outState);
    }

    public void onBackClick(View view) {
        finish();
        UIUtils.addExitTranAnim(this);
    }

    private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

        private boolean mFromUser;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mFromUser = fromUser;
            if (fromUser) {
                if (mAudioPlayer.isPlaying()) {
                    stopUpdateProgressBar();
                    updateCurrent();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mFromUser) {
                if (mAudioPlayer.isPlaying()) {
                    int duration = mAudioPlayer.getDuration();
                    int current = seekBar.getProgress() * duration / seekBar.getMax();
                    mAudioPlayer.seekTo(current);
                    startUpdateProgressBar();
                }
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        pauseAudio();
        abandonAudioFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestSender.stop(mRequestTag);
        stopUpdateProgressBar();
        mAudioHandler.post(new Runnable() {
            @Override
            public void run() {
                releaseAudioPlayer();
                Looper looper = Looper.myLooper();
                if (looper != null) looper.quit();
            }
        });
    }

    private void releaseAudioPlayer() {
        if (mAudioPlayer != null){
            mAudioPlayer.release();
            mAudioPlayer = null;
        }
    }

    public static void start(Context context, UserInfo userInfo, List<Audio> audios, int position) {
        start(context, userInfo, audios, position, null, false);
    }

    public static void startFromClass(Context context, UserInfo userInfo, List<Audio> audios, int position, String classId) {
        start(context, userInfo, audios, position, classId, false);
    }

    public static void startFromCache(Context context, UserInfo userInfo, List<Audio> audios, int position) {
        start(context, userInfo, audios, position, null, true);
    }

    private static void start(Context context, UserInfo userInfo, List<Audio> audios, int position, String classId, boolean fromCache) {
        Intent intent = new Intent(context, AudioDetailsActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(EXTRA_AUDIO_LIST, new ArrayList<>(audios));
        intent.putExtra(EXTRA_POSITION, position);
        if (classId != null) {
            intent.putExtra(Extra.CLASS_ID, classId);
        }
        intent.putExtra(EXTRA_FROM_CACHE, fromCache);
        context.startActivity(intent);
        if (context instanceof Activity)
            UIUtils.addEnterAnim((Activity)context);
    }
}
