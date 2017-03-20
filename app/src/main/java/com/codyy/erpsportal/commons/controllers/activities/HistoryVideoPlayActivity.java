package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.CacheItem;
import com.codyy.erpsportal.commons.models.entities.HistoryVideoDetail;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.services.FileDownloadService;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.BNVideoControlView;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;
import com.codyy.url.URLConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 直播详细、视频播放页面
 * Created by caixingming on 2015/4/24.
 */
public class HistoryVideoPlayActivity extends AppCompatActivity implements BnVideoView2.OnBNCompleteListener, Handler.Callback {

    private String TAG = HistoryVideoPlayActivity.class.getSimpleName();
    /**
     * 标题
     */
    private static final String EXTRA_TITLE = "title";
    /**
     * 默认播放地址 。
     */
    private static final String EXTRA_URL = "url";
    /**
     * 主讲人
     */
    private static final String EXTRA_TEACHER = "teacher";
    /**
     * 是否已经下载
     */
    private static final String EXTRA_LOCAL = "local";
    /**
     * 视频段落id集合 。
     */
    private static final String EXTRA_DATA = "ids";
    /**
     * 父类的起源 0：专递课堂 1：名校网络课堂
     */
    private static final String EXTRA_FROM_TYPE = "parent_from_type";

    @Bind(R.id.tv_title)
    TextView mTitleTextView;
    @Bind(R.id.bn_video_view2)
    BnVideoView2 mVideoView;
    @Bind(R.id.relative_header)
    RelativeLayout mRelativeLayout;
    @Bind(R.id.video_control_view2)
    BNVideoControlView mVideoControl;
    @Bind(R.id.tv_list)
    TextView mIndexTextView;//列表
    @Bind(R.id.tv_teacher)
    TextView mTeacherNameTv;//主讲老师
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;//视频列表的抽屉控件
    @Bind(R.id.grid_view_period_selected)
    GridView mPeriodGridView;

    private boolean mIsVideoPlayEnd = false;//视频是否已经播放结束
    private int mCurIndex = 0;//当前播放第几集，从0开始
    private GridViewAdapter mGridViewAdapter;
    private static boolean mIsLocal = false;
    private String mSpeakUserName;
    private String mTitle;
    private List<String> mHistoryVideoListData = new ArrayList<>();
    private String mType = "0";//0:专递课堂  1：名校网咯课堂（改名：直录播课堂）
    private String mPlayUrl;
    private Handler handler;
    private static final String TYPE_NOT_DEFAULT = "notdefault";
    private static final String TYPE_DEFAULT = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_history_video_play);
        ButterKnife.bind(this);
        handler = new Handler(this);
        mTitle = getIntent().getStringExtra(EXTRA_TITLE);
        mIsLocal = getIntent().getBooleanExtra(EXTRA_LOCAL, false);
        mSpeakUserName = getIntent().getStringExtra(EXTRA_TEACHER);
        mType = getIntent().getStringExtra(EXTRA_FROM_TYPE);
        getUrl(getIntent().getStringExtra(EXTRA_URL), mType, TYPE_DEFAULT);
        mVideoControl.setExpandable(false);
    }

    public void init() {
        List<CharSequence> ids = getIntent().getCharSequenceArrayListExtra(EXTRA_DATA);
        if (null != ids && ids.size() > 0) {
            for (CharSequence cs : ids) {
                mHistoryVideoListData.add(cs.toString());
            }
        }

        mTitleTextView.setText(StringUtils.filterNullString(mTitle));
        String format = getString(R.string.live_class_teacher_fomat);
        mTeacherNameTv.setText(String.format(format, StringUtils.filterNullString(mSpeakUserName)));

        if (mHistoryVideoListData.size() < 2) {
            mIndexTextView.setVisibility(View.GONE);
        } else {
            mIndexTextView.setVisibility(View.VISIBLE);
        }

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
        mVideoControl.bindVideoView(mVideoView,getSupportFragmentManager());
        mVideoControl.setOnCompleteListener(this);
        mVideoControl.setOnErrorListener(new BnVideoView2.OnBNErrorListener() {
            @Override
            public void onError(int errorCode, String errorMsg) {
                if (-2 == errorCode || 0 == errorCode) {
                    //do your action .\
                    Cog.d("----------------------------:", "onError()");
                    mCurIndex = mCurIndex + 1;
                    if (mCurIndex <= mHistoryVideoListData.size() - 1) {
                        mIsVideoPlayEnd = true;
                        ToastUtil.showToast(HistoryVideoPlayActivity.this, "视频出错，自动播放下一集");
                    }
                }
            }
        });
        mVideoControl.setDisplayListener(new BNVideoControlView.DisplayListener() {

            @Override
            public void show() {
                mRelativeLayout.setVisibility(View.VISIBLE);
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }

            @Override
            public void hide() {
                mRelativeLayout.setVisibility(View.GONE);
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            }

        });

        mGridViewAdapter = new GridViewAdapter();
        mPeriodGridView.setAdapter(mGridViewAdapter);
        //start play ~
        mVideoControl.setVideoPath(mPlayUrl, BnVideoView2.BN_URL_TYPE_HTTP, mIsLocal);
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

    public void onBackClick(View view) {
        this.finish();
        overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
    }

    /**
     * @param activity
     * @param isLocal
     * @param type               父类源头： 0 ：专递课堂  1：名校网络课堂
     * @param historyVideoDetail
     */
    public static void start(Activity activity, boolean isLocal, String type, HistoryVideoDetail historyVideoDetail) {
        String playUrl = "";
        ArrayList<CharSequence> ids = new ArrayList<CharSequence>();
        if (null != historyVideoDetail) {
            List<HistoryVideoDetail.DataEntity> datas = historyVideoDetail.getData();
            playUrl = datas.get(0).getLiveAppointmentVideoId();//getUrl(datas.get(0).getLiveAppointmentVideoId(), type);
            if (null != datas && datas.size() > 0) {
                for (HistoryVideoDetail.DataEntity de : datas) {
                    ids.add(de.getLiveAppointmentVideoId());
                }
            }
        }

        Intent intent = new Intent(activity, HistoryVideoPlayActivity.class);
        intent.putExtra(EXTRA_URL, playUrl);
        intent.putExtra(EXTRA_TITLE, historyVideoDetail.getClassroomName());
        intent.putExtra(EXTRA_LOCAL, isLocal);
        intent.putExtra(EXTRA_TEACHER, historyVideoDetail.getSpeakerUserName());
        intent.putExtra(EXTRA_FROM_TYPE, type);
        intent.putCharSequenceArrayListExtra(EXTRA_DATA, ids);

        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    @NonNull
    private void getUrl(String resID, String type, final String from) {
        String base = URLConfig.HISTORY_VIDEO_ONLINE_CLASS;
        if (type != null && "1".equals(type)) {
            base = URLConfig.HISTORY_VIDEO_ONLINE_VIDEO;
        }
        WebApi webApi = RsGenerator.create(WebApi.class);
        webApi.get4Str(base + "?id=" + resID + "&uuid=" + UserInfoKeeper.getInstance().getUserInfo().getUuid())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        mPlayUrl = response;
                        if (from.equals(TYPE_NOT_DEFAULT)) {
                            mVideoControl.setVideoPath(mPlayUrl, BnVideoView2.BN_URL_TYPE_HTTP, mIsLocal);
                        } else {
                            init();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.d(TAG, "getUrl error=", throwable.getMessage());
                    }
                });
    }

    /**
     * 下载缓存过来的视频 ，直接播放本地视频
     *
     * @param activity
     * @param cacheItem
     */
    public static void start(Activity activity, CacheItem cacheItem) {
        String resId = cacheItem.getId();
        ArrayList<CharSequence> ids = new ArrayList<CharSequence>();
        ids.add(resId);
        Intent intent = new Intent(activity, HistoryVideoPlayActivity.class);
        intent.putExtra(EXTRA_TITLE, cacheItem.getName());
        intent.putExtra(EXTRA_URL, FileDownloadService.getCachedMp4File(cacheItem.getBaseUserId(), resId));
        intent.putExtra(EXTRA_LOCAL, true);
        intent.putExtra(EXTRA_TEACHER, "");
        intent.putExtra(EXTRA_FROM_TYPE, "0");
        intent.putCharSequenceArrayListExtra(EXTRA_DATA, ids);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    /**
     * 选择不同的分段
     *
     * @param liveAppointmentVideoId
     */
    public void setVideoPosition(String liveAppointmentVideoId, boolean isAutoPlay) {

        if (!isAutoPlay) {
            if (mIndexTextView.getText().toString().equals("关闭")) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            } else if (mIndexTextView.getText().toString().equals("列表")) {
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        }

        mVideoControl.stop();
        getUrl(liveAppointmentVideoId, mType, TYPE_NOT_DEFAULT);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                mIsVideoPlayEnd = false;
                setVideoPosition(mHistoryVideoListData.get(mCurIndex), true);
        }
        return false;
    }

    @Override
    public void onComplete() {
        mIsVideoPlayEnd = true;
        mCurIndex = mCurIndex + 1;

        //普通暂停
        if (!mIsVideoPlayEnd) {
            Cog.d("----------------------------", "----------普通暂停----------");
            return;
        }
        if (mCurIndex <= mHistoryVideoListData.size() - 1) {
            Cog.d("----------------------------", "----------播放下一集----------");
            Message message = new Message();
            message.what = 0;
            handler.sendMessage(message);
        }
    }


    class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mHistoryVideoListData == null ? 0 : mHistoryVideoListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mHistoryVideoListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder mViewHolder = null;
            if (convertView == null) {
                mViewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_video_list, null);
                mViewHolder.mVideoBtn = (Button) convertView.findViewById(R.id.btn_video_list);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            mViewHolder.mVideoBtn.setText((position + 1) + "");

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE); // 画框
            drawable.setStroke(1, Color.GRAY); // 边框粗细及颜色
            drawable.setColor(Color.WHITE); // 边框内部颜色

            mViewHolder.mVideoBtn.setBackgroundDrawable(drawable);

            mViewHolder.mVideoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurIndex = position;
                    setVideoPosition(mHistoryVideoListData.get(position), false);
                }
            });

            return convertView;
        }

        class ViewHolder {
            Button mVideoBtn;
        }
    }
}
