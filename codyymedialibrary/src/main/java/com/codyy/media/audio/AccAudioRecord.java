package com.codyy.media.audio;

import android.media.MediaRecorder;
import android.util.Log;

import com.codyy.MediaFileUtils;

import java.io.File;
import java.io.IOException;

public class AccAudioRecord {
    private static final String LOG_TAG = "AccRecord";
    private MediaRecorder mRecorder = null;
    private File mAccFile = null;
    private String mUserName;

    public AccAudioRecord(OnAccAudioRecordListener onAccAudioRecordListener,String name) {
        this.mOnAccAudioRecordListener = onAccAudioRecordListener;
        mUserName = name;
    }

    private OnAccAudioRecordListener mOnAccAudioRecordListener;

    public interface OnAccAudioRecordListener {
        /**
         * 录音文件达到最大文件大小
         *
         * @param fileName 文件名称
         * @param filePath 文件路径
         */
        void onMaxFileSizeReached(String fileName, String filePath);

        /**
         * 录音发生错误
         */
        void onErrorListener();

        /**
         * 手动停止录音，完成录制
         *
         * @param fileName 文件名称
         * @param filePath 文件路径
         */
        void onCompleted(String fileName, String filePath);

        void onException(String content);
    }

    public void startRecording() {
        try {
            release();
            mRecorder = new MediaRecorder();
            mAccFile = MediaFileUtils.getOutputMediaFile(MediaFileUtils.MEDIA_TYPE_AUDIO,mUserName);
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setOutputFile(mAccFile.getAbsolutePath());
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setMaxFileSize(100 * 1024 * 1024);
            mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    mOnAccAudioRecordListener.onErrorListener();
                    release();
                }
            });
            mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    switch (what) {
                        case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
                            mOnAccAudioRecordListener.onMaxFileSizeReached(mAccFile.getName(), mAccFile.getAbsolutePath());
                            release();
                            break;
                    }
                }
            });

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }

            mRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            release();
            mOnAccAudioRecordListener.onException(e.getLocalizedMessage());
        }
    }

    public void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            if (mAccFile != null) {
                mOnAccAudioRecordListener.onCompleted(mAccFile.getName(), mAccFile.getAbsolutePath());
            }
        }
    }

    public void release() {
        if (mRecorder != null) {
            mRecorder.release();
            if (mAccFile != null) {
                mAccFile.deleteOnExit();
                mAccFile = null;
            }
            mRecorder = null;
        }

    }
}
