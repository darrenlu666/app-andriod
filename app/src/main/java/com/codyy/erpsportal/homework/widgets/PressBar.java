package com.codyy.erpsportal.homework.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.homework.interfaces.RecordStrategy;

/**
 * 按住说话/松开结束 的bar
 * Created by ldh on 2016/5/18.
 */
public class PressBar extends Button {

    private static final String TAG = "PressBar---";
    /**
     * 手指向上滑动距离,超过此距离则认为想取消录制
     */
    private static int FIGURE_MOVE_DISTANCE_Y;

    private final static int[] NUMBER = {
            R.drawable.number_1,
            R.drawable.number_2,
            R.drawable.number_3,
            R.drawable.number_4,
            R.drawable.number_5,
            R.drawable.number_6,
            R.drawable.number_7,
            R.drawable.number_8,
            R.drawable.number_9,
            R.drawable.number_10};

    /**
     * 是否取消录制
     */
    private boolean isCancel = false;
    /**
     * 屏幕Y方向初始点位置
     */
    private float touchY = 0;
    private Context mContext;

    /**
     * audio record time
     */
    private float mRecordTime = 0.0f;

    /**
     * audio record state
     */
    private String mRecordState = STATE_RECORD_OFF;
    private static final String STATE_RECORD_ON = "STATE_RECORD_ON";
    private static final String STATE_RECORD_OFF = "STATE_RECORD_OFF";
    /**
     * 最短为１ｓ
     */
    private static final int MIN_RECORD_TIME = 1;//最短录音时间为 1s

    private static final int LAST_RECORD_TIME = 10;

    private static final int MAX_RECORD_TIME = 60;

    /**
     * dialog image
     */
    private ImageView mRecordingDialogIv;
    /**
     * dialog text
     */
    private TextView mRecordingTipTv;

    /**
     * 弹出对话框
     */
    private Dialog mRecordingDialog;

    /**
     * 弹出对话框的样式
     */
    public static final String TYPE_RECORDING_DIALOG_RECORDING = "TYPE_RECORDING_DIALOG_RECORDING";
    public static final String TYPE_RECORDING_DIALOG_CANCEL = "TYPE_RECORDING_DIALOG_CANCEL";
    public static final String TYPE_RECORDING_DIALOG_LESS_TIME = "TYPE_RECORDING_DIALOG_LESS_TIME";
    /**
     * dialog dimiss duration
     */
    private static final int DIALOG_LESS_TIME_MISS = 500;

    private RecordStrategy mRecordStrategy;

    private long mDuration;

    private long mStartTime;
    /**
     * 是否可以录制
     */
    private boolean mIsCanRecoder;

    public PressBar(Context context) {
        super(context);
        init(context);
    }

    public PressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        mIsCanRecoder = true;
        FIGURE_MOVE_DISTANCE_Y = UIUtils.dip2px(context, 100);
        this.setText("按住说话");
        setTextColor(getResources().getColor(R.color.remote_text_bg));
        setBackgroundResource(R.drawable.header_bt_nor);
        if (isInEditMode()) {
            return;
        }
    }

    public void setAudioRecorder(RecordStrategy recordStrategy) {
        mRecordStrategy = recordStrategy;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!checkRecordAudioPermission()) {
            Toast.makeText(getContext(), "没有录音权限！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!mIsCanRecoder) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setTextColor(getResources().getColor(R.color.main_color));
                setBackgroundResource(R.drawable.bg_btn_download_normal);
                isCancel = false;
                mDuration = 0;
                mRecordTime = 0;
                mStartTime = System.currentTimeMillis();
                getParent().requestDisallowInterceptTouchEvent(true);
                Log.d(TAG, "ACTION_DOWN");
                if (mRecordState.equals(STATE_RECORD_OFF)) {
                    Log.d(TAG, "STATE_RECORD_ON");
                    mRecordState = STATE_RECORD_ON;
                    if (mRecordStrategy != null) {
                        mRecordStrategy.ready();
                        mRecordStrategy.start();
                    }
                    touchY = event.getY();
                    showRecordingDialog(TYPE_RECORDING_DIALOG_RECORDING);
                    new AudioTask().execute(0);
                    this.setText("松开结束");
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mRecordTime >= MAX_RECORD_TIME) {
                    break;
                }
                if (touchY - event.getY() > FIGURE_MOVE_DISTANCE_Y) {
                    if (!isCancel) {
                        isCancel = true;
                        showRecordingDialog(TYPE_RECORDING_DIALOG_CANCEL);
                        this.setText("松开手指，取消发送");
                    }
                } else {
                    if (isCancel) {
                        isCancel = false;
                        showRecordingDialog(TYPE_RECORDING_DIALOG_RECORDING);
                        this.setText("松开结束");
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP");
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "ACTION_CANCEL");
                setTextColor(getResources().getColor(R.color.remote_text_bg));
                setBackgroundResource(R.drawable.header_bt_nor);
                if (mRecordTime >= MAX_RECORD_TIME) {
                    break;
                }
                mDuration = System.currentTimeMillis() - mStartTime;
                if (mDuration < MIN_RECORD_TIME * 1000) {//时间小于１s,提示
                    isCancel = true;
                    mIsCanRecoder = false;
                    mRecordingTipTv.setText("说话时间太短");
                    mRecordingDialogIv.setImageDrawable(getResources().getDrawable(R.drawable.less_time));
                    break;
                } else {
                    mIsCanRecoder = true;
                }
                stopRecord();
                break;
        }
        return true;
    }

    /**
     * 判断是否有录音权限
     *
     * @return
     */
    public boolean checkRecordAudioPermission() {
        PackageManager pm = mContext.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.RECORD_AUDIO", getContext().getPackageName()));
        return permission;
    }

    /**
     * 手指松开时，停止录制操作
     */
    private void stopRecord() {
        mRecordState = STATE_RECORD_OFF;
        closeRecordingDialog();
        mRecordStrategy.stop();
        if (!isCancel || mIsCanRecoder) {//record success
            Log.d(TAG, mRecordStrategy.getFilePath());
            if (mRecordListener != null) {
                mRecordListener.RecordEnd(mRecordStrategy.getFilePath(), (int) mDuration);
            }
        } else {//record cancel
            mRecordStrategy.deleteOldFiles();
            Log.d(TAG, "取消录制，删除文件");
        }
        /**
         * 还原状态
         */
        this.setText("按住说话");
        isCancel = false;
    }

    /**
     * 显示不同的dialog
     *
     * @param dialogType
     */
    public void showRecordingDialog(String dialogType) {
        if (mRecordingDialog == null) {
            mRecordingDialog = new Dialog(mContext, R.style.RecordingStyle);
            mRecordingDialog.setContentView(R.layout.recording_dialog);
            mRecordingDialog.setCancelable(false);
            mRecordingDialogIv = (ImageView) mRecordingDialog.findViewById(R.id.iv_recording_dialog);
            mRecordingTipTv = (TextView) mRecordingDialog.findViewById(R.id.tv_recording_tip);
        }
        switch (dialogType) {
            case TYPE_RECORDING_DIALOG_RECORDING:
                mRecordingDialogIv.setImageDrawable(getResources().getDrawable(R.drawable.phone_1));
                mRecordingTipTv.setText("手指上滑 取消录制");
                mRecordingTipTv.setTextColor(getResources().getColor(R.color.white));
                break;
            case TYPE_RECORDING_DIALOG_CANCEL:
                mRecordingDialogIv.setImageDrawable(getResources().getDrawable(R.drawable.audio_cancel));
                mRecordingTipTv.setText("松开手指 取消录制");
                mRecordingTipTv.setTextColor(getResources().getColor(R.color.md_red_800));
                break;
            case TYPE_RECORDING_DIALOG_LESS_TIME:
                mRecordingDialogIv.setImageDrawable(getResources().getDrawable(R.drawable.less_time));
                mRecordingTipTv.setText("说话时间太短");
                mRecordingTipTv.setTextColor(getResources().getColor(R.color.white));
                break;
        }
        if (!mRecordingDialog.isShowing()) {
            mRecordingDialog.show();
        }
    }


    /**
     * 显示不同音量的图标
     *
     * @param value
     */
    private void showDifferentVolume(Integer... value) {
        if (isCancel)//取消时不在更新录音音量提示
            return;
        int resource = R.drawable.phone_1;
        if (value[1] > 0 && !isCancel) {
            resource = NUMBER[value[1] - 1];
        } else if (value[0] >= 0 && value[0] <= 6000) {
            resource = R.drawable.phone_1;
        } else if (value[0] > 8000 && value[0] <= 16000) {
            resource = R.drawable.phone_2;
        } else if (value[0] > 16000 && value[0] <= 24000) {
            resource = R.drawable.phone_3;
        } else if (value[0] > 24000 && value[0] <= 30000) {
            resource = R.drawable.phone_4;
        } else if (value[0] > 30000) {
            resource = R.drawable.phone_5;
        }
        mRecordingDialogIv.setImageDrawable(getResources().getDrawable(resource));
    }

    public void closeRecordingDialog() {
        if (mRecordingDialog != null && mRecordingDialog.isShowing())
            mRecordingDialog.dismiss();
    }

    public interface RecordListener {
        void RecordEnd(String recordFilePath, int duration);
    }

    private RecordListener mRecordListener;

    public void setRecordListener(RecordListener listener) {
        mRecordListener = listener;
    }

    class AudioTask extends AsyncTask<Integer, Integer, Boolean> {
        boolean vibrator = true;

        @Override
        protected Boolean doInBackground(Integer... params) {
            while (mRecordState.equals(STATE_RECORD_ON)) {
                mRecordTime += 0.1;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //时间太短，等１ｓ后取消录制
                if (!mIsCanRecoder && mRecordTime >= 1) {
                    return false;
                }
                if (mRecordTime >= 60 - LAST_RECORD_TIME) {
                    publishProgress(mRecordStrategy.getAmplitude(), 60 - (int) mRecordTime);
                    if (mRecordTime >= 60) {
                        return false;
                    }
                } else {
                    publishProgress(mRecordStrategy.getAmplitude(), -1);
                }
            }
            return true;
        }

        /**
         * 返回false表示需要停止录制
         *
         * @param aBoolean
         */
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean) {
                mDuration = 60 * 1000 * 1000;
                stopRecord();
                mIsCanRecoder = true;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            showDifferentVolume(values);
            if (values[1] == LAST_RECORD_TIME && vibrator) {
                vibrator = false;
                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{0, 50}, -1);
            }
        }
    }
}
