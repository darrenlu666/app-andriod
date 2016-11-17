package com.codyy.erpsportal.homework.implementclass;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;

import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.homework.interfaces.RecordStrategy;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 录制音频  接口实现
 * Created by ldh on 2016/5/20.
 */
public class AudioRecorder implements RecordStrategy {

    private static final String FORMAT_RECORD_FILE = ".aac";
    /**
     * 是否正在录制
     */
    private boolean isRecording;

    private MediaRecorder mMediaRecorder;

    private String mFileFolder = Environment.getExternalStorageDirectory().getPath() + "/codyy/audioRecord";

    private String mFileName;

    private Context mContext;

    public AudioRecorder(Context context) {
        this.mContext = context;
    }

    @Override
    public void ready() {
        mFileName = getCurrentDate();
        File file = new File(mFileFolder);
        if (!file.exists()) {
            file.mkdirs();
        }
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setOutputFile(mFileFolder + "/" + mFileName + FORMAT_RECORD_FILE);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//        mMediaRecorder.setAudioSamplingRate(8000);
//        mMediaRecorder.setAudioEncodingBitRate(16);
        mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {

            }
        });
        mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {

            }
        });
        mMediaRecorder.setMaxDuration(60 * 1000);//最长录制时间
    }

    @Override
    public void start() {
        if (!isRecording) {
            isRecording = true;
            try {
                mMediaRecorder.prepare();
                mMediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        if (isRecording && mMediaRecorder != null) {
            isRecording = false;
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                if (!checkRecordAudioPermission()) {
                    ToastUtil.showToast(mContext, "没有权限!");
                }
            }

        }
    }

    @Override
    public void release() {
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    @Override
    public void deleteOldFiles() {
        new File(mFileFolder + "/" + mFileName + FORMAT_RECORD_FILE).deleteOnExit();
    }

    @Override
    public int getAmplitude() {
        if (isRecording && mMediaRecorder != null) {
            return mMediaRecorder.getMaxAmplitude();
        }
        return -1;
    }

    @Override
    public String getFilePath() {
        return mFileFolder + "/" + mFileName + FORMAT_RECORD_FILE;
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        return dateFormat.format(curDate);
    }

    /**
     * 判断是否有录音权限
     *
     * @return
     */
    public boolean checkRecordAudioPermission() {
        PackageManager pm = mContext.getPackageManager();
        return  (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.RECORD_AUDIO", "packageName"));
    }
}
