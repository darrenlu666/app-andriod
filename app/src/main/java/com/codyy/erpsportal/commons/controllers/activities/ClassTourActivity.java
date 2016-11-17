package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.bennu.framework.BNAVFramework;
import com.codyy.erpsportal.BuildConfig;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.fragments.filters.LiveFilterFragment;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.commons.utils.Check3GUtil;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.erpsportal.commons.models.Jumpable;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.Classroom;
import com.codyy.erpsportal.commons.models.entities.Classroom.VideoUrlCallback;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestManager;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.commons.models.parsers.SpecialDeliveryClassroomParser;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 课堂巡视
 */
public class ClassTourActivity extends AppCompatActivity implements AbsListView.OnScrollListener,PullToRefreshBase.OnRefreshListener2,AdapterView.OnItemClickListener {

    private static final String TAG = "ClassTourActivity";

    private static final String EXTRA_USER_INFO = "user_info";

    private final static String EXTRA_TYPE = "type";

    /**
     * 专递课堂
     */
    public final static String TYPE_SPECIAL_DELIVERY_CLASSROOM = "specialDeliveryClassroom";

    /**
     * 名校网络课堂
     */
    public final static String TYPE_SCHOOL_NET = "schoolNet";

    private UserInfo mUserInfo;

    private String mType;

    @Bind(R.id.cb_filter)
    CheckBox mFilterCb;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @Bind(R.id.list_view)
    PullToRefreshListView mListView;

    @Bind(R.id.empty_view)
    EmptyView mEmptyView;

    @Bind(R.id.title_bar)
    TitleBar mTitleBar;

    private ClassroomsAdapter mAdapter;

    private static final int LOAD_COUNT = 10;

    private int mStart;

//    private BNPlayerFactory mBnPlayerFactory;

    private Handler mHandler;

    private LiveFilterFragment mLiveFilterFragment;

    private Bundle mBundleFilter = null;

    private RequestQueue mRequestQueue;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_tour);
        ButterKnife.bind(this);
        mRequestQueue = RequestManager.getRequestQueue();
        mHandler = new Handler();
        if (getIntent() != null) {
            mUserInfo = getIntent().getParcelableExtra( EXTRA_USER_INFO);
            mType = getIntent().getStringExtra( EXTRA_TYPE);
        }

        initTitle();

        mLiveFilterFragment = (LiveFilterFragment)getSupportFragmentManager().findFragmentByTag("class_tour_filter");

        mListView.getRefreshableView().setFriction(ViewConfiguration.getScrollFriction() * 10);
        mDrawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
//                mFilterBtn.setVisibility(View.GONE);
//                mConfirmBtn.setVisibility(View.VISIBLE);
                mFilterCb.setChecked(true);
            }

            @Override
            public void onDrawerClosed( View drawerView) {
//                mFilterBtn.setVisibility( View.VISIBLE);
//                mConfirmBtn.setVisibility( View.GONE);
                mFilterCb.setChecked(false);
            }
        });

        mListView.setEmptyView(mEmptyView);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                loadData(true);
            }
        });
        mListView.setOnItemClickListener(this);

        initPullToRefresh(mListView);

        mListView.setOnScrollListener(this);

//        mBnPlayerFactory = new BNPlayerFactory();
        mAdapter = new ClassroomsAdapter(this, mRequestQueue);
        mListView.setAdapter(mAdapter);

        tryToLoadData();
        Check3GUtil.instance().CheckNetType(this, new Check3GUtil.OnWifiListener() {

            @Override
            public void onNetError() { }

            @Override
            public void onContinue() {
                //默认不加在视频,set true...加载视频
                mAdapter.setPlayable(true);
            }
        });
    }

    /**
     * 设置标题栏标题
     */
    private void initTitle() {
        if (TYPE_SPECIAL_DELIVERY_CLASSROOM.equals(mType)) {
            mTitleBar.setTitle(Titles.sWorkspaceSpeclassRound);
        } else {
            mTitleBar.setTitle(Titles.sWorkspaceNetClassRound);
        }
    }

    /**
     * 先判断播放组件是否加载好，未加载好再等一会儿，直到播放组件加载完成之后再加载数据
     */
    private void tryToLoadData() {
        if (BNAVFramework.isInited()) {
            LoadingDialog loadingDialog = (LoadingDialog) getSupportFragmentManager().findFragmentByTag("loading");
            if (loadingDialog != null && loadingDialog.getDialog() != null && loadingDialog.getDialog().isShowing()) {
                loadingDialog.dismiss();
            }
            loadData(true);
        } else {
            LoadingDialog loadingDialog = (LoadingDialog) getSupportFragmentManager().findFragmentByTag("loading");
            if (loadingDialog == null) {
                loadingDialog = LoadingDialog.newInstance(R.string.loading_video_framework);
            }
            if (!loadingDialog.isAdded()) {
                loadingDialog.show(getSupportFragmentManager(), "loading");
            }

            postDelayLoadData();
        }
    }

    /**
     * 发送延后加载数据任务
     */
    private void postDelayLoadData() {
        mHandler.removeCallbacks(mLoadDataTask);
        mHandler.postDelayed(mLoadDataTask, 500);
    }

    /**
     * 尝试加载数据任务
     */
    private Runnable mLoadDataTask = new Runnable() {
        @Override
        public void run() {
            tryToLoadData();
        }
    };

    private void addSimulativeData() {
        List<Classroom> classrooms = new ArrayList<>();
        for (int i=0; i<10; i++) {
            //rtmp://10.1.200.83/dms/zj05
            Classroom classroom = new Classroom("数学第一章节", "rtmp://184.72.239.149/vod/BigBuckBunny_115k.mov");
            if (i == 1 || i==5 || i==8) {
                classroom.setVideoUrl("rtmp://184.72.239.149/vod/BigBuckBunny_115k.mov");
            }
            classrooms.add(classroom);
        }
        mAdapter.addClassrooms(classrooms);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cog.d(TAG, "+onItemClick position:", position);
        Classroom classroom = mAdapter.getItem(position - 1);
        LiveVideoListPlayActivity.start(this, classroom, mUserInfo, mType);
    }

    private void initPullToRefresh(PullToRefreshAdapterViewBase<?> view) {
        view.setMode(PullToRefreshBase.Mode.BOTH);
        view.getLoadingLayoutProxy(false, true).setPullLabel(getResources().getString(R.string.pull_to_refresh));
        view.getLoadingLayoutProxy(false, true).setRefreshingLabel(getResources().getString(R.string.loading));
        view.getLoadingLayoutProxy(false, true).setReleaseLabel(getResources().getString(R.string.release_to_refresh));
        view.setOnRefreshListener(this);
    }

    //default search
    private void loadData(final boolean refresh){
        if(mBundleFilter != null){
            loadData(refresh,mBundleFilter.getString("areaId"),mBundleFilter.getString("directSchoolId"),mBundleFilter.getString("class"),mBundleFilter.getString("subject"),mBundleFilter.getBoolean("hasDirect"));
        }else{
            loadData(refresh,null,null,null,null,false);
        }
    }

    /**
     * 加载课堂巡视
     * @param refresh 是否是刷新
     * @param areaId 地区id
     * @param schoolId 学校id
     * @param classLevelId 年级id
     * @param subjectId 学科id
     * @param hasDirect 是否是直属
     */
    private void loadData(final boolean refresh,String areaId ,String schoolId ,String classLevelId , String subjectId ,boolean hasDirect) {

        if (refresh) {
            mStart = 0;
        }
        int start = mStart;
        int end = start + LOAD_COUNT;

        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());

        if(!TextUtils.isEmpty(areaId)){
            params.put("baseAreaId",areaId);
        }else{
            params.put("baseAreaId", mUserInfo.getBaseAreaId());
        }

        if( !TextUtils.isEmpty(schoolId)){
            params.put("schoolId", schoolId);
        }else if ( !TextUtils.isEmpty(mUserInfo.getSchoolId())) {
            params.put("schoolId", mUserInfo.getSchoolId());
        }

        if(!TextUtils.isEmpty(classLevelId)){
            params.put("classlevelId",classLevelId);
        }

        if(!TextUtils.isEmpty(subjectId)){
            params.put("subjectId",subjectId);
        }

        if(!hasDirect){
            params.put("type", "nodirectly");
        }else{
            params.put("type", "directly");
        }
        params.put("start", "" + start);
        params.put("end", "" + (end - 1));

        String url = null;
        if (TYPE_SPECIAL_DELIVERY_CLASSROOM.equals(mType)) {
            url = URLConfig.SPECIAL_MONITOR_CLASSROOM;
        } else {
            url = URLConfig.NET_MONITOR_CLASSROOM;
        }
        Cog.d(TAG, "url:", url, ",params:", params);
        RequestSender sender = new RequestSender(this);
        sender.sendRequest(new RequestSender.RequestData(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse response:", response);
                mEmptyView.setLoading(false);
                if ("success".equals(response.optString("result"))) {
                    int total = response.optInt("total");
                    if (total == 0) {
                        mAdapter.setData(null);
                        mAdapter.notifyDataSetChanged();

                        mListView.onRefreshComplete();
                        mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    } else {
                        JSONArray jsonArray = response.optJSONArray("list");
                        JsonParser<Classroom> jsonParser = null;
                        if (TYPE_SPECIAL_DELIVERY_CLASSROOM.equals(mType)) {
                            jsonParser = new SpecialDeliveryClassroomParser();
                        } else {
//                            jsonParser = new SchoolNetClassroomParser();
                        }
                        List<Classroom> classrooms = jsonParser.parseArray( jsonArray);
                        if (refresh) {
                            mAdapter.setClassrooms(classrooms);
                        } else {
                            mAdapter.addClassrooms(classrooms);
                        }
                        if (BuildConfig.DEBUG) {
                            addSimulativeData();
                        }
                        mAdapter.notifyDataSetChanged();

                        mListView.onRefreshComplete();
                        //如果已经加载所有，下拉更多关闭
                        if (total <= mAdapter.getCount()) {
                            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            mListView.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                    }

                    mStart = mAdapter.getCount();
                } else {
                    mListView.onRefreshComplete();
                }
                hideLoadingFilterDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.e(TAG, "onErrorResponse error:", error);
                if (refresh && mAdapter.getCount() == 0) {
                    mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                mListView.onRefreshComplete();
                mEmptyView.setLoading(false);
                hideLoadingFilterDialog();
            }

        }));
        mEmptyView.setLoading(true);
    }

    private void hideLoadingFilterDialog() {
        LoadingDialog loadingDialog = (LoadingDialog) getSupportFragmentManager().findFragmentByTag("filtering");
        if (loadingDialog != null && loadingDialog.getDialog() != null && loadingDialog.getDialog().isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public void onExitClick(View view) {
        finish();
        UIUtils.addExitTranAnim(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UIUtils.addExitTranAnim(this);
    }

    private int mStartIndex;

    private int mEndIndex;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //当滑动停止时，播放在用户视线内的视频
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            Cog.d(TAG, "+onScrollStateChanged");
            for (int i = mStartIndex; i < mEndIndex; i++) {
                BnVideoLayout videoLayout = (BnVideoLayout)view.findViewWithTag(i - 1);
                Cog.d(TAG, "@onScrollStateChanged:i=", i);
                Cog.d(TAG, "@onScrollStateChanged:videoLayout=", videoLayout);
                if (videoLayout != null && !videoLayout.isPlaying() && !videoLayout.isUrlEmpty()) {
                    videoLayout.setVolume(0);
                    videoLayout.play();
                }
            }
            Cog.d(TAG, "-onScrollStateChanged");
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mStartIndex = firstVisibleItem;
        mEndIndex = firstVisibleItem + visibleItemCount;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        loadData(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        loadData(false);
    }

    public static void start(Activity activity, UserInfo userInfo, String type) {
        Intent intent = new Intent(activity, ClassTourActivity.class);
        intent.putExtra(EXTRA_USER_INFO, userInfo);
        intent.putExtra(EXTRA_TYPE, type);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    private class ClassroomsAdapter extends BaseAdapter {

        private Context mContext;

        private RequestQueue mRequestQueue;

        private LayoutInflater mInflater;

//        private BNPlayerFactory mBnPlayerFactory;

        private List<Classroom> list;

        private  boolean isPlayable = false;//是否允许播放/3G检测使用


        public void setPlayable(boolean isPlayable) {
            this.isPlayable = isPlayable;
            notifyDataSetChanged();
        }

        public ClassroomsAdapter(Context context, RequestQueue requestQueue) {
            this.mContext = context;
            this.mRequestQueue = requestQueue;
//            this.mBnPlayerFactory = bnPlayerFactory;
            this.mInflater = LayoutInflater.from(context);
        }
        public void setData(List<Classroom> data) {
            this.list = data;
        }


        @Override
        public int getCount() {
            if (list == null) return 0;
            return list.size();
        }

        @Override
        public Classroom getItem(int position) {
            if (list == null) return null;
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View contentView, ViewGroup parent) {
            ClassroomsAdapter.ClassroomViewHolder viewHolder;
            if (contentView == null) {
                contentView = mInflater.inflate(R.layout.item_tour_video, parent, false);
                adjustVideoSize(parent, contentView);
                viewHolder = new ClassroomsAdapter.ClassroomViewHolder();
                viewHolder.mapFromView(contentView);
                contentView.setTag(viewHolder);
            } else {
                viewHolder = (ClassroomsAdapter.ClassroomViewHolder) contentView.getTag();
            }
            viewHolder.setDataToView(position, getItem(position));
            return contentView;
        }

        public void addClassrooms(Collection<? extends Classroom> classrooms) {
            if (list == null) list = new ArrayList<>(classrooms.size());
            list.addAll(classrooms);
        }

        public void setClassrooms(List<Classroom> classroomList) {
            this.list = classroomList;
        }

        private int mVideoViewWidth;
        private int mVideoViewHeight;

        private void adjustVideoSize(ViewGroup parent, View view) {
            BnVideoLayout bnVideoLayout = (BnVideoLayout) view.findViewById(R.id.videoLayout);
            if (mVideoViewWidth == 0) {
                int height = parent.getHeight() / 2 + 8;
                view.getLayoutParams().height = height;
                RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_video);
                float maxWidth = parent.getWidth() - view.getPaddingLeft() - view.getPaddingRight()
                        - relativeLayout.getPaddingRight() - relativeLayout.getPaddingLeft();
                float maxHeight = height - view.getPaddingTop() - view.getPaddingBottom()
                        - relativeLayout.getPaddingTop() - relativeLayout.getPaddingBottom()
                        - UIUtils.dip2px(parent.getContext(), 28);
                float ratio = maxWidth / maxHeight;
                if (ratio > 1.5f) {
                    mVideoViewHeight = (int) maxHeight;
                    mVideoViewWidth = (int) (maxHeight * 1.5f);
                } else {
                    mVideoViewWidth = (int) maxWidth;
                    mVideoViewHeight = (int) (maxWidth / 1.5f);
                }
            }
            bnVideoLayout.getLayoutParams().height = mVideoViewHeight;
            bnVideoLayout.getLayoutParams().width = mVideoViewWidth;

        }
        
        class ClassroomViewHolder {

            private BnVideoLayout mBnVideoLayout;

            private TextView mVideoTitleTv;

            public void mapFromView(View view) {
                mBnVideoLayout = (BnVideoLayout) view.findViewById(R.id.videoLayout);
                mVideoTitleTv = (TextView) view.findViewById(R.id.videoTitle);
            }

            public void setDataToView(final int position, Classroom data) {
                Cog.d(TAG, "+setDataToView position=", position);
//                mBnVideoLayout.setUrl(data.getVideoUrl());
                Cog.d(TAG, "@setDataToView mBnVideoLayout:", mBnVideoLayout);
//                if (!mBnVideoLayout.isPlaying() && !mBnVideoLayout.isUrlEmpty()&&isPlayable) {
//                    mBnVideoLayout.play(mBnPlayerFactory);
//                }
                mBnVideoLayout.setTag( position);
                mVideoTitleTv.setText(data.getSchoolName());

                data.fetchMainVideoUrl(mRequestQueue, new VideoUrlCallback() {
                    @Override
                    public void onUrlFetched(String url) {
                        Cog.d(TAG, "onUrlFetched url=", url);
                        if (position >= mStartIndex || position <= mEndIndex) {
                            mBnVideoLayout.setUrl(url);
                            if (!mBnVideoLayout.isPlaying() && !mBnVideoLayout.isUrlEmpty() && isPlayable) {
                                mBnVideoLayout.setVolume(0);
                                mBnVideoLayout.play();
                            }
                        }
                    }

                    @Override
                    public void onError() {
                        Cog.d(TAG, "onError");
                        if (mBnVideoLayout.isPlaying()) {
                            mBnVideoLayout.pause();
                        }
                        mBnVideoLayout.setUrl(null);
                    }
                });
                Cog.d(TAG, "-setDataToView");
            }
        }
    }

    @OnClick(R.id.cb_filter)
    public void onFilterCbClick(){
        if (mFilterCb.isChecked()) {
            mDrawer.openDrawer(GravityCompat.END);
        } else {
            mBundleFilter = mLiveFilterFragment.getLiveFilterData();
            LoadingDialog loadingDialog = LoadingDialog.newInstance();
            loadingDialog.show(getSupportFragmentManager(), "filtering");
            loadData(true);
            mDrawer.closeDrawer(GravityCompat.END);
        }
    }

    public static class ClassTourJumper implements Jumpable {

        private String mType;

        public ClassTourJumper(String type){
            this.mType = type;
        }

        @Override
        public void jump(Context context) {
            UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
            ClassTourActivity.start((Activity) context, userInfo, mType);
        }
    }
}
