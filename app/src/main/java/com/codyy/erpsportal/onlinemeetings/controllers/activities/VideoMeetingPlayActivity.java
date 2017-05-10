package com.codyy.erpsportal.onlinemeetings.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.interfaces.IFragmentMangerInterface;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.services.FileDownloadService;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.VideoDownloadUtils;
import com.codyy.erpsportal.commons.widgets.BNVideoControlView;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleRecyclerView;
import com.codyy.erpsportal.commons.models.entities.VideoDetails;
import com.codyy.erpsportal.onlinemeetings.controllers.viewholders.VideoGridViewHolder;
import com.codyy.erpsportal.resource.models.entities.ResourceDetails;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 视频会议-观看视频
 * Created by poe on 2017/7/19.
 */
public  class VideoMeetingPlayActivity extends BaseHttpActivity implements BnVideoView2.OnBNCompleteListener, Handler.Callback {
    private String TAG = VideoMeetingPlayActivity.class.getSimpleName();
     /**     * 标题     */
    private static final String EXTRA_TITLE = "title";
    @Bind(R.id.download_text_view)    TextView mDownloadTextView;
    @Bind(R.id.video_recycler_view)    SimpleRecyclerView mVideoRecyclerView;
    @Bind(R.id.tv_title)    TextView mTitleTextView;
    @Bind(R.id.video_name_tv)    TextView mVideoNameTextView;//主讲老师
    @Bind(R.id.tv_list)    TextView mIndexTextView;//列表
    @Bind(R.id.bn_video_view2)    BnVideoView2 mVideoView;
    @Bind(R.id.relative_header)    RelativeLayout mRelativeLayout;
    @Bind(R.id.video_control_view2)    BNVideoControlView mVideoControl;
    @Bind(R.id.drawer_layout)    DrawerLayout mDrawerLayout;//视频列表的屉控件

    private String mTitle;
    private String mPreparationId;//视频id
    private boolean mIsVideoPlayEnd = false;//视频是否已经播放结束
    public static int mCurrentIndex = 0;//当前播放第几集，从0开始
    private List<VideoDetails> mVideoDetailsList = new ArrayList<>();
    private BaseRecyclerAdapter<VideoDetails,VideoGridViewHolder> mAdapter ;
    private Handler mHandler = new Handler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_video_meeting_play;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_ONLINE_MEETING_VIDEOS;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("remoteId", mPreparationId);
        return params;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        JSONArray jsonArray = response.optJSONArray("videoList");
        List<VideoDetails> list = VideoDetails.parseJsonArray(jsonArray);
        if(list != null){
            mVideoDetailsList.addAll(list);
        }
        if (mVideoDetailsList != null && mVideoDetailsList.size()>0) {
            if (mVideoDetailsList.size() < 2) {
                mIndexTextView.setVisibility(View.GONE);
                mVideoRecyclerView.setVisibility(View.GONE);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                mIndexTextView.setVisibility(View.VISIBLE);
                mVideoRecyclerView.setVisibility(View.VISIBLE);
            }
            mVideoNameTextView.setText(mVideoDetailsList.get(0).getVideoName());
            // TODO: 16-7-19 play the first video by default .
            playVideo(mVideoDetailsList.get(0));
        }else{
            mDownloadTextView.setVisibility(View.GONE);
        }

        mAdapter.setData(mVideoDetailsList);
    }

    @Override
    public void onFailure(Throwable error) {
        Cog.e(TAG, "onErrorResponse:" + error);
        UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
    }

    public void init() {
        mTitle = getIntent().getStringExtra(EXTRA_TITLE);
        mPreparationId = getIntent().getStringExtra(Constants.PREPARATIONID);
        mTitleTextView.setText(StringUtils.filterNullString(mTitle));

        GridLayoutManager manager = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        mVideoRecyclerView.setLayoutManager(manager);

        mVideoControl.bindVideoView(mVideoView, new IFragmentMangerInterface() {
            @Override
            public FragmentManager getNewFragmentManager() {
                return getSupportFragmentManager();
            }
        });
        mVideoControl.setOnCompleteListener(this);
        mVideoControl.setExpandable(false);
        mVideoControl.setOnErrorListener(new BnVideoView2.OnBNErrorListener() {
            @Override
            public void onError(int errorCode, String errorMsg) {
                if (-2 == errorCode || 0 == errorCode) {
                    //do your action .\\
                    Cog.d("----------------------------:", "onError()");
                    mCurrentIndex = mCurrentIndex + 1;
                    if (mCurrentIndex <= mVideoDetailsList.size() - 1) {
                        mIsVideoPlayEnd = true;
                        ToastUtil.showToast(VideoMeetingPlayActivity.this, "视频出错，自动播放下一集");
                    }
                }
            }
        });
        mVideoControl.setDisplayListener(new BNVideoControlView.DisplayListener() {

            @Override
            public void show() {
                mRelativeLayout.setVisibility(View.VISIBLE);
                /*if (mVideoDetailsList.size() >= 2) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }*/
            }

            @Override
            public void hide() {
                mRelativeLayout.setVisibility(View.GONE);
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            }

        });
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mIndexTextView.setText("关闭");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mIndexTextView.setText("列表");
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        mIndexTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIndexTextView.getText().toString().equals("关闭")) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                } else if (mIndexTextView.getText().toString().equals("列表")) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
            }
        });
        setAdapter();
        //get video list data .
        requestData(true);
    }

    private void setAdapter() {
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<VideoGridViewHolder>() {
            @Override
            public VideoGridViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new VideoGridViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_gride_selector,parent,false));
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<VideoDetails>() {
            @Override
            public void onItemClicked(View v, int position, VideoDetails data) {
                mCurrentIndex = position;
                mHandler.sendEmptyMessage(0);
                mAdapter.notifyDataSetChanged();
            }
        });
        mVideoRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.download_text_view)
    void downloadVideo(){
        VideoDetails videoDetails = mVideoDetailsList.get(mCurrentIndex);
        ResourceDetails resourceDetails = new ResourceDetails();
        resourceDetails.setId(videoDetails.getMeetVideoId());
        resourceDetails.setResourceName(videoDetails.getVideoName());
        resourceDetails.setThumbPath(videoDetails.getThumbPath());
        VideoDownloadUtils.downloadVideo(resourceDetails, videoDetails.getFilePath(), mUserInfo.getBaseUserId());
    }

    /**
     * 播放视频
     * @param videoDetails
     */
    private void playVideo(VideoDetails videoDetails) {
        //1.判断是否已经下载 本视频
        if (FileDownloadService.hasMp4Downloaded(mUserInfo.getBaseUserId(), videoDetails.getMeetVideoId())) {
            String video = FileDownloadService.getCachedMp4File(mUserInfo.getBaseUserId(), videoDetails.getMeetVideoId());
            Cog.i("video:", video);
            mVideoControl.setVideoPath(video, BnVideoView2.BN_URL_TYPE_RTMP_HISTORY, true);
            return;
        }
        if (videoDetails == null) {
            Cog.i("video:videoDetails", "videoDetails");
            return;
        }
        //视频会议>>> rtmp点播
        mVideoControl.setVideoPath(videoDetails.getFilePath(), BnVideoView2.BN_URL_TYPE_HTTP, false);
    }

    //进入后台，暂停播放
    @Override
    protected void onPause() {
        super.onPause();
        mVideoControl.onPause();
        Cog.e(TAG, "onPause~~~~~~~~~~~~~~");
    }

    public void onBackClick(View view) {
        this.finish();
        overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
    }



    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                mIsVideoPlayEnd = false;
                mVideoNameTextView.setText(mVideoDetailsList.get(mCurrentIndex).getVideoName());
                playVideo(mVideoDetailsList.get(mCurrentIndex));
        }
        return false;
    }

    @Override
    public void onComplete() {
        mIsVideoPlayEnd = true;
        mCurrentIndex = mCurrentIndex + 1;
        //普通暂停
        if (!mIsVideoPlayEnd) {
            Cog.d("----------------------------", "----------普通暂停----------");
            return;
        }
        if (mCurrentIndex <= mVideoDetailsList.size() - 1) {
            Cog.d("----------------------------", "----------播放下一集----------");
            Message message = new Message();
            message.what = 0;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 视频会议-观看视频跳转
     * @param activity
     */
    public static void start(Activity activity,String title,String meetId ) {
        Intent intent = new Intent(activity, VideoMeetingPlayActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(Constants.PREPARATIONID , meetId);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }
}
