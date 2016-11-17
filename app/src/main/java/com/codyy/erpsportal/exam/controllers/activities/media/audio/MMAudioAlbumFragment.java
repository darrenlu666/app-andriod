package com.codyy.erpsportal.exam.controllers.activities.media.audio;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.codyy.MediaFileUtils;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;
import com.codyy.erpsportal.exam.controllers.activities.media.adapters.MMBaseRecyclerViewAdapter;
import com.codyy.erpsportal.exam.controllers.activities.media.image.MMImageBean;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.media.audio.AccAudioRecord;
import com.codyy.media.audio.AudioPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 音频MP3，最大100M；音视频二者选其一上传；
 * Created by eachann on 2016/2/19.
 */
public class MMAudioAlbumFragment extends Fragment implements Handler.Callback {
    private static final String TAG = "MMAudioAlbumFragment";
    private MMAudioListAdapter mAdapter;
    private ViewStub mViewStubPlay;
    private ViewStub mViewStubRecord;
    private View mPlayMedia;
    private View mPauseMedia;
    private SeekBar mMediaSeekBar;
    private TextView mRunTime;
    private TextView mTotalTime;
    private TextView mAudioName;
    private TextView mRecordingTime;
    private ImageView mRecord;
    // 设置定时器
    private Timer mTimer = null;
    private boolean flag;
    private AccAudioRecord mAccAudioRecord;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);
        mAccAudioRecord = new AccAudioRecord(new AccAudioRecord.OnAccAudioRecordListener() {
            @Override
            public void onMaxFileSizeReached(String fileName, String filePath) {
                recordSuccess(fileName, filePath);
            }

            @Override
            public void onErrorListener() {
                Snackbar.make(mRecord, "发生错误...", Snackbar.LENGTH_SHORT).show();
                resetState();
            }

            @Override
            public void onCompleted(String fileName, String filePath) {
                recordSuccess(fileName, filePath);
            }

            @Override
            public void onException(String content) {
                Snackbar.make(mRecord, "发生错误...", Snackbar.LENGTH_SHORT).show();
                mRecord.post(new Runnable() {
                    @Override
                    public void run() {
                        resetState();
                    }
                });
            }
        },UserInfoKeeper.obtainUserInfo().getUserName());
    }

    private void recordSuccess(String fileName, String filePath) {
        MediaFileUtils.scanFile(getContext(), filePath);
        mAdapter.add(0, new MMAudioBean(DateUtil.getNow(DateUtil.YEAR_MONTH_DAY), fileName, filePath, mRecordingTime.getText().toString()));
        mRecyclerView.scrollToPosition(0);
        resetState();
    }

    private void resetState() {
        flag = false;
        mRecord.setBackgroundResource(R.drawable.ic_exam_media_start);
        mRecordingTime.setCompoundDrawables(null, null, null, null);
        mRecordingTime.setText(getString(R.string.exam_recording_time));
        cancleTimer();
    }

    private final static int WHAT = 0;
    /**
     * 扫描音频库标记
     */
    private final static int WHAT_SCAN = 1;
    private Handler mHandler;
    private Long mCurrentTimeMillis;
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_audio_recycler, container, false);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        mViewStubPlay = (ViewStub) mRootView.findViewById(R.id.vs_play);
        mViewStubPlay.inflate();
        mViewStubPlay.setVisibility(View.GONE);
        mViewStubRecord = (ViewStub) mRootView.findViewById(R.id.vs_record);
        mViewStubRecord.inflate();
        mPlayMedia = mRootView.findViewById(R.id.play);
        mPauseMedia = mRootView.findViewById(R.id.pause);
        mMediaSeekBar = (SeekBar) mRootView.findViewById(R.id.media_seekbar);
        mRunTime = (TextView) mRootView.findViewById(R.id.run_time);
        mTotalTime = (TextView) mRootView.findViewById(R.id.total_time);
        mAudioName = (TextView) mRootView.findViewById(R.id.tv_audio_name);
        mRecordingTime = (TextView) mRootView.findViewById(R.id.tv_recording_time);
        mRecord = (ImageView) mRootView.findViewById(R.id.iv_record);
        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag) {
                    mAccAudioRecord.startRecording();
                    flag = true;
                    mCurrentTimeMillis = System.currentTimeMillis();
                    mRecord.setBackgroundResource(R.drawable.ic_exam_media_stop);
                    Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_recording);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mRecordingTime.setCompoundDrawables(drawable, null, null, null);
                    mRecordingTime.setText(getString(R.string.exam_recording_time));
                    // 初始化定时器
                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            mHandler.sendEmptyMessage(WHAT);
                        }
                    }, 0, 1000);
                } else {
                    if (!mRecordingTime.getText().equals(getString(R.string.exam_recording_time))) {
                        mAccAudioRecord.stopRecording();
                    }
                }
            }
        });
        TextView mAudioCanclePlay = (TextView) mRootView.findViewById(R.id.tv_cancle_play);
        mAudioCanclePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioPlayer.getInstance().release();
                mViewStubPlay.setVisibility(View.GONE);
                mViewStubRecord.setVisibility(View.VISIBLE);
            }
        });
        TextView mAudioUpload = (TextView) mRootView.findViewById(R.id.tv_upload_play);
        mAudioUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MMImageBean> imageBeans = new ArrayList<>();
                MMImageBean bean = new MMImageBean(AudioPlayer.getInstance().getAudioPath(), true, null);
                imageBeans.add(bean);
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(EXTRA_DATA, imageBeans);
                intent.putExtra(EXTRA_TYPE, TYPE_AUDIO);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MMAudioListAdapter(new ArrayList<MMAudioBean>(), getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        return mRootView;
    }

    protected static final String EXTRA_DATA = ToolbarActivity.class.getPackage() + ".EXTRA_DATA";
    public static final String EXTRA_TYPE = ToolbarActivity.class.getPackage() + ".EXTRA_TYPE";
    public static final String TYPE_AUDIO = "TYPE_AUDIO";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter.setOnInViewClickListener(R.id.rl_item_audio_list, new MMBaseRecyclerViewAdapter.onInternalClickListener<MMAudioBean>() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, MMAudioBean values) {
                if (!flag) {
                    mViewStubRecord.setVisibility(View.GONE);
                    mViewStubPlay.setVisibility(View.VISIBLE);
                    playAudio(Uri.parse(values.getPath()), values.getName());
                } else {
                    Snackbar.make(v, "正在录音...", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void OnLongClickListener(View parentV, View v, Integer position, MMAudioBean values) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mThread.start();
    }

    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            // 扫描音频
            ArrayList<MMAudioBean> list = new ArrayList<>();
            String str[] = {MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media.DURATION};
            Cursor cursor = getContext().getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, str, MediaStore.Audio.Media.SIZE + "<? and " + MediaStore.Audio.Media.SIZE + ">0 and " /*+ MediaStore.Audio.Media.DURATION + ">'1000' and " */ + MediaStore.Audio.Media.DISPLAY_NAME + " like '%.mp3' or " +
                            MediaStore.Audio.Media.DISPLAY_NAME + " like '%.aac' or " + MediaStore.Audio.Media.DISPLAY_NAME + " like '%.wav' ",
                    new String[]{"104857601"}, MediaStore.Audio.Media.DATE_ADDED + " DESC");
            try {
                while (cursor != null && cursor.moveToNext()) {
                    //每个条目有多少项信息
                    long duration = Long.parseLong(cursor.getString(5));
                    if (duration > 1000) {
                        MMAudioBean bean = new MMAudioBean(DateUtil.getDateStr(Long.parseLong(cursor.getString(4)) * 1000, DateUtil.YEAR_MONTH_DAY), cursor.getString(1), cursor.getString(2), DateUtil.formatMediaTime(Long.parseLong(cursor.getString(5))));
                        list.add(bean);
                    }
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            Message message = new Message();
            message.what = WHAT_SCAN;
            message.obj = list;
            mHandler.sendMessage(message);
        }
    });

    private void playAudio(Uri uri, String name) {
        // mHandler for play/pause button
        Cog.i(TAG, uri.toString());
        AudioPlayer.getInstance().release();
        AudioPlayer.getInstance()
                .init(getContext(), uri)
                .setPlayView(mPlayMedia)
                .setPauseView(mPauseMedia)
                .setSeekBar(mMediaSeekBar)
                .setRuntimeView(mRunTime)
                .setTotalTimeView(mTotalTime).setAduioName(mAudioName, name);

        AudioPlayer.getInstance().addOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
//                Toast.makeText(getContext(), "Completed", Toast.LENGTH_SHORT).show();
                // do you stuff.
            }
        });

        AudioPlayer.getInstance().addOnPlayClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "Play", Toast.LENGTH_SHORT).show();
                // get-set-go. Lets dance.
            }
        });

        AudioPlayer.getInstance().addOnPauseClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "Pause", Toast.LENGTH_SHORT).show();
                // Your on audio pause stuff.
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mAccAudioRecord != null) {
            mAccAudioRecord.release();
        }
        resetState();
        AudioPlayer.getInstance().release();
        mViewStubPlay.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            cancleTimer();
            if (mThread.isAlive()) {
                mThread.interrupt();
            }
            mHandler.removeCallbacksAndMessages(null);
            AudioPlayer.getInstance().release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancleTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT:
                try {
                    mRecordingTime.setText(DateUtil.formatMediaTime(System.currentTimeMillis() - mCurrentTimeMillis));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case WHAT_SCAN:
                try {
                    mAdapter.setList((List<MMAudioBean>) msg.obj);
                } catch (Exception e) {
                    e.printStackTrace();
                    mAdapter.setList(new ArrayList<MMAudioBean>());
                }
                break;
            default:
                break;
        }
        return false;
    }
}
