package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.interfaces.IFragmentMangerInterface;
import com.codyy.erpsportal.commons.services.FileDownloadService;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.BNVideoControlView;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;
import com.codyy.erpsportal.commons.models.entities.CacheItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 离线缓存视频播放页面
 * Created by caixingming on 2016/4/15 .
 */
public class CacheVideoPlayActivity extends AppCompatActivity implements BnVideoView2.OnBNCompleteListener {

    private String TAG = HistoryVideoPlayActivity.class.getSimpleName();
    /**
     * 标题
     */
    private static final String EXTRA_TITLE = "title";
    /**
     * 默认播放地址 。
     */
    private static final String EXTRA_URL = "url";

    @Bind(R.id.tv_title)TextView mTitleTextView;
    @Bind(R.id.bn_video_view2)BnVideoView2 mVideoView;
    @Bind(R.id.relative_header)RelativeLayout mRelativeLayout;
    @Bind(R.id.video_control_view2)BNVideoControlView mVideoControl;

    private boolean mIsVideoPlayEnd = false;//视频是否已经播放结束

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_video_play);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        String defaultUrl = getIntent().getStringExtra(EXTRA_URL);
        String title = getIntent().getStringExtra(EXTRA_TITLE);

        mTitleTextView.setText(StringUtils.filterNullString(title));
        mVideoControl.bindVideoView(mVideoView, new IFragmentMangerInterface() {
            @Override
            public FragmentManager getNewFragmentManager() {
                return getSupportFragmentManager();
            }
        });
        mVideoControl.setExpandable(true);
        mVideoControl.setOnCompleteListener(this);
        mVideoControl.setOnErrorListener(new BnVideoView2.OnBNErrorListener() {
            @Override
            public void onError(int errorCode , String errorMsg) {
                if (-2 == errorCode || 0 == errorCode) {
                    //do your action .\
                    Cog.d("----------------------------:", "onError()");
                    mIsVideoPlayEnd = true;
                    ToastUtil.showToast("视频出错，自动播放下一集");
                }
            }
        });
        mVideoControl.setDisplayListener(new BNVideoControlView.DisplayListener() {

            @Override
            public void show() {
                mRelativeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void hide() {
                mRelativeLayout.setVisibility(View.GONE);
            }

        });

        mVideoControl.setVideoPath(defaultUrl, BnVideoView2.BN_URL_TYPE_HTTP, true);
    }

    //进入后台，暂停播放
    @Override
    protected void onPause() {
        super.onPause();
        mVideoControl.onPause();
        Cog.e(TAG, "onPause~~~~~~~~~~~~~~");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void onBackClick(View view){
        this.finish();
        overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 下载缓存过来的视频 ，直接播放本地视频
     * @param activity
     * @param cacheItem
     */
    public static void start(Activity activity,CacheItem cacheItem) {
        String resId = cacheItem.getId();
        ArrayList<CharSequence> ids = new ArrayList<>();
        ids.add(resId);
        Intent intent = new Intent(activity, CacheVideoPlayActivity.class);
        intent.putExtra(EXTRA_TITLE, cacheItem.getName());
        intent.putExtra(EXTRA_URL, FileDownloadService.getCachedMp4File(cacheItem.getBaseUserId(), resId));

        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    @Override
    public void onComplete() {
        mIsVideoPlayEnd = true;

        //普通暂停
        if(!mIsVideoPlayEnd){
            Cog.d(TAG, "----------pause----------");
            return;
        }
    }
}
