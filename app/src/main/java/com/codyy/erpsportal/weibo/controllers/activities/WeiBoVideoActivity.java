package com.codyy.erpsportal.weibo.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.BNVideoControlView;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WeiBoVideoActivity extends AppCompatActivity {
    public static final String VIDEO_PATH = "video_path";
    @Bind(R.id.weibo_video)
    BnVideoView2 mVideoView;
    @Bind(R.id.videoControl)
    BNVideoControlView mBnVideoControlView2;
    @Bind(R.id.title)
    TextView mTitleTV;
    @Bind(R.id.rltControlTitle)
    LinearLayout mTitlelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wei_bo_video);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);
        mBnVideoControlView2.bindVideoView(mVideoView,getSupportFragmentManager());
        String url = getIntent().getStringExtra(VIDEO_PATH);
        mBnVideoControlView2.setVideoPath(url, BnVideoView2.BN_URL_TYPE_HTTP, false);
        mBnVideoControlView2.setDisplayListener(new BNVideoControlView.DisplayListener() {
            @Override
            public void show() {
                mTitlelayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void hide() {
                mTitlelayout.setVisibility(View.GONE);
            }
        });
        mBnVideoControlView2.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Cog.e("WeiBoVideoActivity", "onPause~~~~~~~~~~~~~~");
        mBnVideoControlView2.onPause();
    }

    public void onBackClick(View v) {
        this.finish();
    }

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, WeiBoVideoActivity.class);
        intent.putExtra(VIDEO_PATH, url);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        UIUtils.addExitTranAnim(this);
    }
}
