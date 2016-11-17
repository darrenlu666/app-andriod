package com.codyy.erpsportal.resource.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;

/**
 * Created by gujiajia on 2016/7/6.
 */
public class AudioControlBar extends RelativeLayout {

    private final static int DEFAULT_HEIGHT_DP = 54;

    private int mDefaultHeight;

    private ImageButton mNextBtn;

    private ImageButton mPlayBtn;

    private ImageButton mPreviousBtn;

    private TextView mTrackNameTv;

    private ProgressBar mProgressBar;

    private ControlBarCallback mCallback;

    public AudioControlBar(Context context) {
        this(context, null);
    }

    public AudioControlBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AudioControlBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr,0);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public AudioControlBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr,defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater.from(context).inflate(R.layout.audio_controll_bar, this, true);
        if (getBackground() == null) setBackgroundColor(0xb3000000);
        mDefaultHeight = UIUtils.dip2px(context, DEFAULT_HEIGHT_DP);

        mPreviousBtn = (ImageButton) findViewById(R.id.ib_previous_audio);
        mPlayBtn = (ImageButton) findViewById(R.id.ib_play_audio);
        mNextBtn = (ImageButton) findViewById(R.id.ib_next_audio);
        mTrackNameTv = (TextView) findViewById(R.id.tv_audio_name);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_audio_current);

        mPreviousBtn.setOnClickListener(mOnBtnClickListener);
        mPlayBtn.setOnClickListener(mOnBtnClickListener);
        mNextBtn.setOnClickListener(mOnBtnClickListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = Math.min(mDefaultHeight, heightSize);
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = mDefaultHeight;
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
    }

    private OnClickListener mOnBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_previous_audio:
                    if (mCallback != null) mCallback.onPreviousClick();
                    break;
                case R.id.ib_next_audio:
                    if (mCallback != null) mCallback.onNextClick();
                    break;
                case R.id.ib_play_audio:
                    if (mCallback != null) mCallback.onPlayClick();
                    break;
            }
        }
    };

    public void setTrackName(CharSequence trackName) {
        mTrackNameTv.setText(trackName);
    }

    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
    }

    public void setMax(int max) {
        mProgressBar.setMax(max);
    }

    public int getMax() {
        return mProgressBar.getMax();
    }

    public void setCallback(ControlBarCallback callback) {
        mCallback = callback;
    }

    public void setPlayingStatus(boolean isPlaying) {
        if (isPlaying) {
            mPlayBtn.setImageResource(R.drawable.ic_bar_pause);
        } else {
            mPlayBtn.setImageResource(R.drawable.ic_bar_play);
        }
    }

    public interface ControlBarCallback {
        void onPlayClick();
        void onNextClick();
        void onPreviousClick();
    }
}
