package com.codyy.erpsportal.resource.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.interfaces.IFragmentMangerInterface;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.services.FileDownloadService;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.VideoDownloadUtils;
import com.codyy.erpsportal.commons.widgets.BNVideoControlView;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;
import com.codyy.erpsportal.commons.widgets.BnVideoView2.OnPlayingListener;
import com.codyy.erpsportal.resource.controllers.adapters.TabsAdapter;
import com.codyy.erpsportal.resource.controllers.fragments.ResCommentsFragment;
import com.codyy.erpsportal.resource.controllers.fragments.ResourceDetailsFragment;
import com.codyy.erpsportal.resource.models.entities.ResourceDetails;
import com.codyy.erpsportal.resource.utils.CountIncreaser;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 视频资源详情
 * Created by gujiajia on 2016/7/6
 */
public class VideoDetailsActivity extends FragmentActivity {

    private final static String TAG = "VideoDetailsActivity";

    private final static String EXTRA_USER_INFO = "extra_user_info";

    private final static String EXTRA_RESOURCE_ID = "extra_resource_id";

    private TextView mTitleTv;

    private ViewPager mPager;

    private TabHost mTabHost;

    private TabsAdapter mTabsAdapter;

    private BnVideoView2 mVideoView;

    private FrameLayout mFrameLayout;

    private BNVideoControlView mVideoControl;

    private RelativeLayout mTitleRl;

    private Button mDownloadBtn;

    private RequestSender mRequestSender;

    private Object mRequestTag = new Object();

    private ResourceDetails mResourceDetails;

    private UserInfo mUserInfo;

    private String mResourceId;

    private String mClassId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);
        initAttributes();
        initViews(savedInstanceState);

        loadData();
    }

    private void initAttributes() {
        mRequestSender = new RequestSender(this);
        mUserInfo = getIntent().getParcelableExtra(EXTRA_USER_INFO);
        mResourceId = getIntent().getStringExtra(EXTRA_RESOURCE_ID);
        mClassId = getIntent().getStringExtra(Extra.CLASS_ID);
    }

    private void initViews(Bundle savedInstanceState) {
        mTitleTv = (TextView) findViewById(R.id.title);
        mPager = (ViewPager) findViewById(R.id.pager);
        mVideoView = (BnVideoView2) findViewById(R.id.video_view);
        mFrameLayout = (FrameLayout) findViewById(R.id.video_area);

        mTitleRl = (RelativeLayout) findViewById(R.id.rltControlTitle);
        mTitleRl.setVisibility(View.GONE);

        mVideoControl = (BNVideoControlView) findViewById(R.id.videoControl);
        mVideoControl.bindVideoView(mVideoView, new IFragmentMangerInterface() {
            @Override
            public FragmentManager getNewFragmentManager() {
                return getSupportFragmentManager();
            }
        });
        mVideoControl.setDisplayListener(new BNVideoControlView.DisplayListener() {

            @Override
            public void show() {
                mTitleRl.setVisibility(View.VISIBLE);
            }

            @Override
            public void hide() {
                mTitleRl.setVisibility(View.GONE);
            }

        });

        mVideoControl.setOnPlayingListener(new OnPlayingListener() {
            private boolean firstTime = true;
            @Override
            public void onPlaying() {
                Cog.d(TAG, "onPlaying");
                if (firstTime) {
                    CountIncreaser.increaseViewCount(mRequestSender, mUserInfo.getUuid(), mResourceId);
                    firstTime = false;
                }
            }
        });

        mDownloadBtn = (Button) findViewById(R.id.btn_download);
        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mResourceDetails == null) return;//详情没有加载就不下载
                if (VideoDownloadUtils.downloadVideo(mResourceDetails, mResourceDetails.getAttachPath(),
                        mUserInfo.getBaseUserId())) {
                    CountIncreaser.increaseDownloadCount(mRequestSender, mUserInfo.getUuid(), mResourceId);
                }
            }
        });

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabsAdapter = new TabsAdapter(this, getSupportFragmentManager(), mTabHost, mPager);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTab(savedInstanceState.getInt("tab"));
        }

        addResourceDetailsFragment();
        addResourceCommentsFragment();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            mFrameLayout.setLayoutParams(layoutParams);
        } else {
            int height = UIUtils.dip2px(this, 180);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            mFrameLayout.setLayoutParams(layoutParams);
        }
    }

    /**
     * 创建标签
     *
     * @param title
     * @return
     */
    private View makeTabIndicator(String title) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
        TextView tabTitleTv = (TextView) view.findViewById(R.id.tab_title);
        tabTitleTv.setText(title);
        return view;
    }

    /**
     * 加载数据
     */
    private void loadData() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("resourceId", mResourceId);
        if (!TextUtils.isEmpty(mClassId)) {
            params.put("baseClassId", mClassId);
        }
        mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.RESOURCE_DETAILS, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if (mTitleTv == null) return;
                String result = response.optString("result");
                if ("success".equals(result)) {
                    JSONObject detailsJsonObject = response.optJSONObject("data");
                    mResourceDetails = ResourceDetails.parseJson(detailsJsonObject);
                    if (mResourceDetails != null) {
                        mTitleTv.setText(mResourceDetails.getResourceName());
                    }

                    Fragment fragment = mTabsAdapter.getRegisteredFragment(0);
                    if (fragment == null) return;
                    ResourceDetailsFragment resourceDetailsFragment = (ResourceDetailsFragment) fragment;
                    resourceDetailsFragment.setResourceDetails(mResourceDetails);
                    updateDownloadBtn();
                    playVideo(mResourceDetails);
                } else if ("error".equals(result)) {
                    String message = response.optString("message");
                    UIUtils.toast(VideoDetailsActivity.this, message, Toast.LENGTH_SHORT);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "onErrorResponse:" + error);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
            }
        }, mRequestTag));
    }

    private void updateDownloadBtn() {
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
    private void playVideo(ResourceDetails resourceDetails) {
        if(resourceDetails == null ){
            return;
        }
        if (!TextUtils.isEmpty(resourceDetails.getRtmpPath())) {
            mVideoControl.setVideoPath(resourceDetails.getRtmpPath(), BnVideoView2.BN_URL_TYPE_RTMP_HISTORY, false);
            return;
        }

        //1.判断是否已经下载 本视频
        if(FileDownloadService.hasMp4Downloaded(mUserInfo.getBaseUserId(), mResourceId)){
            String video =FileDownloadService.getCachedMp4File(mUserInfo.getBaseUserId(), mResourceId);
            Cog.i(TAG, "video:",video);
            mVideoControl.setVideoPath(video,BnVideoView2.BN_URL_TYPE_HTTP, true);
            return ;
        }

        Cog.d(TAG, "playVideo url=" + resourceDetails.getPlayUrl());
        //检测网络环境 ,如果是3G则给提示
        String videoUrl = resourceDetails.getPlayUrl();
        if (!TextUtils.isEmpty(videoUrl)) {
            playVideo(videoUrl);
        }
    }

    private void playVideo(String videoUrl) {
        if (mVideoView == null || TextUtils.isEmpty(videoUrl))
            return;
        mVideoControl.setVideoPath(videoUrl, BnVideoView2.BN_URL_TYPE_HTTP, false);
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

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 1) {
            Fragment fragment = mTabsAdapter.getRegisteredFragment(1);
            if (fragment != null) {
                ResCommentsFragment resCommentsFragment = (ResCommentsFragment) fragment;
                if (resCommentsFragment.hideEmojiKeyboard()){
                    return;
                }
            }
        }
        super.onBackPressed();
        UIUtils.addExitTranAnim(this);
    }

    @Override
    protected void onPause() {
        mVideoControl.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mVideoControl.start();
    }

    @Override
    protected void onDestroy() {
        mVideoView.stop();
        mRequestSender.stop(mRequestTag);
        super.onDestroy();
    }

    public static void start(Activity activity, UserInfo userInfo, String resourceId) {
        start(activity, userInfo , resourceId, null);
    }

    public static void start(Activity activity, UserInfo userInfo, String resourceId, String classId) {
        Intent intent = new Intent(activity, VideoDetailsActivity.class);
        intent.putExtra(EXTRA_USER_INFO, userInfo);
        intent.putExtra(EXTRA_RESOURCE_ID, resourceId);
        if (classId != null) {
            intent.putExtra(Extra.CLASS_ID, classId);
        }
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }
}
