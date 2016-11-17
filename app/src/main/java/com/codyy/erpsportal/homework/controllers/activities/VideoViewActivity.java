package com.codyy.erpsportal.homework.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.homework.widgets.MySubmitDialog;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;

/**
 * 全屏视频播放
 * Created by ldh on 2016/3/10.
 */
public class VideoViewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String EXTRA_URL = "URL";
    private static final String EXTRA_FROM = "from";
    private static final String EXTRA_TITLE = "title";

    private String mPlayUrl;//播放地址
    private String mFrom;//来自本地/网络
    private String mTitle;//名称
    private VideoView mVideoView;
    private RelativeLayout mTitleBar;//标题栏
    private DialogUtil mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        mFrom = getIntent().getStringExtra(EXTRA_FROM);
        mPlayUrl = getIntent().getStringExtra(EXTRA_URL);
        mTitle = getIntent().getStringExtra(EXTRA_TITLE);
        init();
    }

    private void init() {
        mVideoView = (VideoView) findViewById(R.id.vv_video_view);
        ImageView mBackImageView = (ImageView) findViewById(R.id.imgBtn2);
        RelativeLayout mVideoViewRelativeLayout = (RelativeLayout) findViewById(R.id.rl_video_view);
        mTitleBar = (RelativeLayout) findViewById(R.id.relative_header);
        TextView mTitleTv = (TextView) findViewById(R.id.tv_title);
        mTitleTv.setText(mTitle);
        mBackImageView.setOnClickListener(this);
        MediaController mMediaController = new MediaController(this);
        if (mFrom.equals(TaskFragment.FROM_LOACAL)) {
            mVideoView.setVideoPath(mPlayUrl);
        } else if (mFrom.equals(TaskFragment.FROM_NET)) {
            mVideoView.setVideoURI(Uri.parse(mPlayUrl));
        }
        mVideoView.setMediaController(mMediaController);
        mMediaController.setMediaPlayer(mVideoView);
        if (mProgressDialog == null) {
            mProgressDialog = new DialogUtil(this, true);
            mProgressDialog.showDialog();
        } else {
            mProgressDialog.showDialog();
        }
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
                mProgressDialog.cancel();
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mProgressDialog.cancel();
                MySubmitDialog unFinishDialog = MySubmitDialog.newInstance("视频地址无效!", "确定",
                        MySubmitDialog.DIALOG_STYLE_TYPE_2, new MySubmitDialog.OnclickListener() {
                            @Override
                            public void leftClick(MySubmitDialog myDialog) {
                                myDialog.dismiss();
                            }

                            @Override
                            public void rightClick(MySubmitDialog myDialog) {
                                myDialog.dismiss();
                                finish();
                            }

                            @Override
                            public void dismiss() {

                            }
                        });
                unFinishDialog.show(getSupportFragmentManager(), "mVideoView");
                return true;
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
        mVideoViewRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleBar.setVisibility(mTitleBar.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }

    public static void startActivity(Context context, String playUrl, String from, String title) {
        Intent intent = new Intent(context, VideoViewActivity.class);
        intent.putExtra(EXTRA_URL, playUrl);
        intent.putExtra(EXTRA_FROM, from);
        intent.putExtra(EXTRA_TITLE, title);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }

    @Override
    protected void onDestroy() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.resume();
    }

    @Override
    public void onClick(View v) {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        finish();
        UIUtils.addExitTranAnim(VideoViewActivity.this);
    }
}
