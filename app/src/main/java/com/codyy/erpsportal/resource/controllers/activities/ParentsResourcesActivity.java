package com.codyy.erpsportal.resource.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.commons.models.parsers.JsonParser.OnParsedListener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.erpsportal.commons.widgets.slidinguppanel.ResSlidingUpPanelLayout;
import com.codyy.erpsportal.commons.widgets.slidinguppanel.ResSlidingUpPanelLayout.PanelState;
import com.codyy.erpsportal.resource.controllers.adapters.ResourcesAdapter;
import com.codyy.erpsportal.resource.controllers.adapters.ResourcesAdapter.OnItemClickListener;
import com.codyy.erpsportal.resource.controllers.adapters.ResourcesAdapter.OnLoadMoreListener;
import com.codyy.erpsportal.resource.controllers.fragments.ParentsResourcesFilterFragment;
import com.codyy.erpsportal.resource.controllers.others.AudioListPlayController;
import com.codyy.erpsportal.resource.controllers.others.AudioListPlayController.OnAudioPlayingListener;
import com.codyy.erpsportal.resource.models.entities.Audio;
import com.codyy.erpsportal.resource.models.entities.AudioEvent;
import com.codyy.erpsportal.resource.models.entities.Document;
import com.codyy.erpsportal.resource.models.entities.Image;
import com.codyy.erpsportal.resource.models.entities.Video;
import com.codyy.erpsportal.resource.utils.CountIncreaser;
import com.codyy.erpsportal.resource.widgets.AudioControlBar;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 530家长资源页
 * Created by gujiajia on 2016/7/6
 */
public class ParentsResourcesActivity extends AppCompatActivity implements OnLoadMoreListener, OnRefreshListener, Callback {

    private final static String TAG = "ParentsResourcesActivity";

    private static final int LOAD_COUNT = 10;

    private final static String EXTRA_PARENT_ID = "com.codyy.erpsportal.EXTRA_PARENT_ID";

    /**
     * 最小加载间隔，加载太快看不到加载动画。
     */
    private final static long MIN_LOADING_INTERVAL = 500L;

    private final static int MSG_COLLAPSE_PANEL = 23;

    private final static String PARAMS_TYPE = "resType";

    @StringDef({TYPE_VIDEO, TYPE_AUDIO, TYPE_DOCUMENT, TYPE_IMAGE, TYPE_ALBUM})
    @interface ResourceType {}

    public static final String TYPE_VIDEO = "VIDEO";

    public static final String TYPE_AUDIO = "AUDIO";

    public static final String TYPE_DOCUMENT = "DOC";

    public static final String TYPE_IMAGE = "IMAGE";

    public static final String TYPE_ALBUM = "ALBUM";

    @Bind(R.id.tv_empty)
    TextView mEmptyTv;

    @Bind(R.id.title_bar)
    TitleBar mTitleBar;

    @Bind(R.id.rl_resources)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.rv_resources)
    RecyclerView mRecyclerView;

    @Bind(R.id.pl_scope)
    ResSlidingUpPanelLayout mSlidingPanelLayout;

    @Bind(R.id.rg_type)
    RadioGroup mTypeRg;

    @Bind(R.id.audio_control_bar)
    AudioControlBar mAudioControlBar;

    @Bind(R.id.filter_drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.ib_filter)
    ImageButton mFilterIb;

    @Bind(R.id.btn_confirm_filter)
    Button mConfirmFilterBtn;

    @Bind(R.id.ib_open_simple_filter)
    ImageButton mOpenSimpleFilterIb;

    private ResourcesAdapter mAdapter;

    private ParentsResourcesFilterFragment mFilterFragment;

    private int mStart;

    private Map<String, String> mParams;

    private RequestSender mRequestSender;

    private Object mRequestTag = new Object();

    /**
     * 音频播放控制器
     */
    private AudioListPlayController mPlayController;


    private Handler mHandler;

    private UserInfo mUserInfo;

    private String mParentId;

    private String mSchoolId;

    private String mUrl;

    /**
     * 资源类型
     */
    @ResourceType
    private String mType = TYPE_VIDEO;

    private ItemDecoration mItemDecoration;

    private ResSlidingUpPanelLayout.PanelSlideListener mPanelSlideListener = new ResSlidingUpPanelLayout.PanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            Cog.d(TAG, "onPanelSlide slideOffset=", slideOffset);
            Cog.d(TAG, "onPanelSlide slideDistance=", slideOffset * panel.getHeight());
            Cog.d(TAG, "onPanelSlide panel bottom=", panel.getBottom());
            int panelBottom = panel.getBottom();
            if (panelBottom >= 0) {
                MarginLayoutParams lp = (MarginLayoutParams) mRefreshLayout.getLayoutParams();
                lp.topMargin = panelBottom;
                mRefreshLayout.setLayoutParams(lp);
            }
        }

        @Override
        public void onPanelStateChanged(View panel, ResSlidingUpPanelLayout.PanelState previousState, ResSlidingUpPanelLayout.PanelState newState) {
            Cog.d(TAG, "onPanelStateChanged previousState=", previousState, ",newState=", newState);
            mPanelStateCollapsed = newState == PanelState.COLLAPSED;
            if (mPanelStateCollapsed) {
                mOpenSimpleFilterIb.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
            } else {
                mOpenSimpleFilterIb.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_resources);
        ButterKnife.bind(this);
        initAttributes();
        initViews();

        addRequestParams();
        loadData(true);
    }

    private void initAttributes() {
        mHandler = new Handler(this);
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mParentId = getIntent().getStringExtra(EXTRA_PARENT_ID);
        if (TextUtils.isEmpty( mParentId)) {
            mParentId = mUserInfo.getBaseUserId();
        }
        mSchoolId = getIntent().getStringExtra(Extra.SCHOOL_ID);
        mParams = new HashMap<>();
        mRequestSender = new RequestSender(this);
        mUrl = URLConfig.RESOURCE_LIST_PARENT;
    }

    private void initViews() {
        mSlidingPanelLayout.addPanelSlideListener(mPanelSlideListener);
        mSlidingPanelLayout.setContentScrollableView(mRecyclerView);
        mDrawerLayout.addDrawerListener(mDrawerListener);
        createAndAddFilterFragment();
        mItemDecoration = new DividerItemDecoration(this);

        mTitleBar.setTitle(Titles.sWorkspaceResource);
        mRefreshLayout.setColorSchemeResources(R.color.main_color);
        mRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new ResourcesAdapter(mRecyclerView, this);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);

        mPlayController = new AudioListPlayController.Builder()
                .setActivity(this)
                .setAudioControlBar(mAudioControlBar)
                .setAdapter(mAdapter)
                .setOnAudioPlayingListener(new OnAudioPlayingListener() {
                    @Override
                    public void onAudioPlaying(String audioId) {
                        Cog.d(TAG, "onAudioPlaying audioId=", audioId);
                        CountIncreaser.increaseViewCount(mRequestSender, mRequestTag, mUserInfo.getUuid(),audioId);
                    }
                })
                .build();
    }

    private void createAndAddFilterFragment() {
        mFilterFragment = ParentsResourcesFilterFragment.newInstance(mUserInfo, mSchoolId);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fl_filter, mFilterFragment);
        ft.commit();
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Cog.d(TAG, "onItemClick position=", position);
            Object object = mAdapter.getItem( position);
            if (object instanceof Video){
                Video video = (Video) object;
                VideoDetailsActivity.start(ParentsResourcesActivity.this, mUserInfo, video.getId());
            } else if (object instanceof Audio) {
                List<Audio> audios = mAdapter.getData();
                AudioDetailsActivity.start(ParentsResourcesActivity.this, mUserInfo, audios, position);
            } else if (object instanceof Document) {
                Document document = (Document) object;
                DocumentContentActivity.start(ParentsResourcesActivity.this, mUserInfo, document);
            } else if (object instanceof Image) {
                Image image = (Image) object;
                ImageDetailsActivity.start(ParentsResourcesActivity.this, mUserInfo, image.getId());
            }
        }
    };

    private DrawerListener mDrawerListener = new DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) { }

        @Override
        public void onDrawerOpened(View drawerView) {
            mConfirmFilterBtn.setVisibility(View.VISIBLE);
            mFilterIb.setVisibility(View.GONE);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            mConfirmFilterBtn.setVisibility(View.GONE);
            mFilterIb.setVisibility(View.VISIBLE);
        }

        @Override
        public void onDrawerStateChanged(int newState) { }
    };

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestSender.stop(mRequestTag);
        mPlayController.onDestroy();
    }

    public void onEvent(AudioEvent audioEvent) {
        mPlayController.onAudioClick(audioEvent);
    }

    private boolean updateRequestParam(String paramName, String paramValue) {
        String originalValue = mParams.get(paramName);
        if (originalValue != null) {
            if (paramValue == null) {
                mParams.remove(paramName);
                return true;
            } else if (!paramValue.equals(originalValue)){
                mParams.put(paramName, paramValue);
                return true;
            }
        } else {
            if (paramValue != null) {
                mParams.put(paramName, paramValue);
                return true;
            }
        }
        return false;
    }

    @OnClick({R.id.rb_filter_video, R.id.rb_filter_audio, R.id.rb_filter_image, R.id.rb_filter_document})
    public void onResourceTypeClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rb_filter_video:
                if (checked) updateType(TYPE_VIDEO);
                onTypeNotAudioChanged();
                break;
            case R.id.rb_filter_audio:
                if (checked) updateType(TYPE_AUDIO);
                break;
            case R.id.rb_filter_image:
                if (checked) updateType(TYPE_IMAGE);
                onTypeNotAudioChanged();
                break;
            case R.id.rb_filter_document:
                if (checked) updateType(TYPE_DOCUMENT);
                onTypeNotAudioChanged();
                break;
        }
        mSlidingPanelLayout.setPanelState(PanelState.COLLAPSED);
    }

    private void onTypeNotAudioChanged() {
        mPlayController.stopAudio();
    }

    private void updateType(@NonNull @ResourceType String type) {
        if (!type.equals(mType)) {
            mType = type;
            if (updateRequestParam(PARAMS_TYPE, mType)) {
                loadData(true);
            }
        }
    }

    @OnClick(R.id.ib_filter)
    public void onFilterClick(View view) {
        mDrawerLayout.openDrawer(GravityCompat.END);
        mFilterIb.setVisibility(View.GONE);
        mConfirmFilterBtn.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_confirm_filter)
    public void onConfirmFilter(View view) {
        mDrawerLayout.closeDrawer(GravityCompat.END);
        mFilterIb.setVisibility(View.VISIBLE);
        mConfirmFilterBtn.setVisibility(View.GONE);
        Map<String,String> filterParams = mFilterFragment.acquireFilterParams();
        mergeParams(filterParams);
        loadData(true);
    }

    private boolean mergeParams(Map<String, String> newParams) {
        byte changedMask = 0;
        for (Entry<String,String> entrySet: newParams.entrySet()) {
            if (updateRequestParam(entrySet.getKey(), entrySet.getValue())){
                changedMask |= 1;
            }
        }
        return changedMask == 1;
    }

    /**
     * 加载数据
     *
     * @param refresh true刷新，false加载更多
     */
    public void loadData(final boolean refresh) {
        if (refresh) {
            mStart = 0;
        }
        int start = mStart;
        int end = start + LOAD_COUNT;
        mParams.put("start", "" + start);
        mParams.put("end", "" + (end - 1));
        mParams.put("parentId", mParentId);

        final long startTime = SystemClock.currentThreadTimeMillis();
        Cog.d(TAG, "loadData:", getUrl(), mParams);
        if (refresh) {
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(true);
                }
            });
        }

        mRequestSender.sendRequest(new RequestData(getUrl(), mParams,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        Cog.d(TAG, "onResponse:" + response);
                        delayResponding(startTime, new ResponseCallable() {
                            @Override
                            public void handle() {
                                handleNormalResponse(response, refresh);
                            }
                        });
                    }
                }, new ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Cog.e(TAG, "onErrorResponse:" + error);
                        delayResponding(startTime, new ResponseCallable() {
                            @Override
                            public void handle() {
                                handleErrorResponse(refresh);
                            }
                        });
                    }
                }, mRequestTag)
        );
    }

    private String getUrl() {
        return mUrl;
    }

    /**
     * 延迟响应，为了有足够时间显示加载动画。
     *
     * @param startTime 请求开始时间
     * @param callable  请求响应回调
     */
    private void delayResponding(long startTime, final ResponseCallable callable) {
        long interval = SystemClock.currentThreadTimeMillis() - startTime;
        if (interval > MIN_LOADING_INTERVAL) {
            callable.handle();
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    callable.handle();
                }
            }, MIN_LOADING_INTERVAL - interval);
        }
    }

    private boolean mPanelStateCollapsed = true;

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MSG_COLLAPSE_PANEL) {
            if (!mPanelStateCollapsed) {
                mSlidingPanelLayout.setPanelState(PanelState.COLLAPSED);
            }
            return true;
        }
        return false;
    }

    /**
     * 响应回调
     */
    interface ResponseCallable {
        void handle();
    }

    /**
     * 处理请求响应
     *
     * @param response 响应数据
     * @param isRefreshing 刷新
     */
    private void handleNormalResponse(JSONObject response, boolean isRefreshing) {
        mAdapter.setLoading(false);
        mRefreshLayout.setRefreshing(false);
        if (checkSuccessful(response)) {
            List list = getList(response);
            updateItemDecoration();
            if (list == null || list.size() == 0) {
                if (isRefreshing) {
                    handleEmpty();//
                } else {
                    mAdapter.removeItem(mAdapter.getItemCount() - 1);
                }
            } else {
                if (isRefreshing) {
                    mEmptyTv.setVisibility(View.GONE);
                    mAdapter.setData(list);
                    mAdapter.notifyItemRangeInserted(0, list.size());
                } else {
                    mAdapter.removeItem(mAdapter.getItemCount() - 1);
                    mAdapter.addData(list);
                    mAdapter.notifyDataSetChanged();
                }
            }
            //如果已经加载所有，下拉更多关闭
            if (checkHasMore(response, mAdapter.getItemCount())) {
                mAdapter.disableLoadMore();
            } else {
                mAdapter.enableLoadMore();
            }
            mAdapter.notifyDataSetChanged();
            mStart = mAdapter.getItemCount();
        } else {
            if (isRefreshing) {
                handleEmpty();
            }
        }
    }

    /**
     * 根据类型设置分隔线
     */
    private void updateItemDecoration() {
        if (TYPE_AUDIO.equals(mType)) {
            mRecyclerView.addItemDecoration(mItemDecoration, 0);
        } else {
            mRecyclerView.removeItemDecoration(mItemDecoration);
        }
    }

    /**
     * 检查请求是否成功，默认检查result字段值是否为success
     *
     * @param response 响应数据
     * @return true请求成功
     */
    protected boolean checkSuccessful(JSONObject response) {
        return "success".equals(response.optString("result"));
    }

    /**
     * 检查是否有更多，默认方式是比较total字段与当前项数
     *
     * @param response 响应
     * @param itemCount 已有item数量
     * @return true 请求成功
     */
    protected boolean checkHasMore(JSONObject response, int itemCount) {
        return response.optInt("total") <= itemCount;
    }

    /**
     * 处理空情况
     */
    protected void handleEmpty() {
        mAdapter.setData(null);
        mAdapter.notifyDataSetChanged();
        mEmptyTv.setText(obtainEmptyText());
        mEmptyTv.setVisibility(View.VISIBLE);
    }

    private String obtainEmptyText(){
        String typeText;
        switch (mType) {
            case TYPE_VIDEO:
                typeText = getString(R.string.video);
                break;
            case TYPE_AUDIO:
                typeText = getString(R.string.audio);
                break;
            case TYPE_DOCUMENT:
                typeText = getString(R.string.document);
                break;
            case TYPE_IMAGE:
                typeText = getString(R.string.image);
                break;
            default:
                typeText = "";
        }
        return getString(R.string.no_some_type_resources, typeText);
    }

    protected List getList(JSONObject response) {
        JsonParser parser;
        switch (mType) {
            case TYPE_VIDEO:
                parser = Video.sParser;
                break;
            case TYPE_AUDIO:
                return Audio.sParser.parseArrayAdditionally(response.optJSONArray("data"),
                        new OnParsedListener<Audio>() {
                    @Override
                    public void handleParsedObj(Audio audio) {
                        mPlayController.updateCurrentAudio(audio);
                    }
                });
            case TYPE_DOCUMENT:
                parser = Document.sParser;
                break;
            case TYPE_IMAGE:
                parser = Video.sParser;
                break;
            default:
                return null;
        }
        return parser.parseArray(response.optJSONArray("data"));
    }

    /**
     * 处理错误响应
     *
     * @param refresh 是否是刷新
     */
    private void handleErrorResponse(boolean refresh) {
        if (!refresh) {
            mAdapter.removeItem(mAdapter.getItemCount() - 1);
            mAdapter.notifyItemRemoved(mAdapter.getItemCount());
        } else {
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(false);
                }
            });
        }
        mAdapter.setLoading(false);
        if (mAdapter.isEmpty()) {
            mEmptyTv.setText(obtainEmptyText());
            mEmptyTv.setVisibility(View.VISIBLE);
        }

        UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
    }

    @OnClick(R.id.ib_open_simple_filter)
    protected void onOpenSimpleFilterClick() {
        if (mPanelStateCollapsed) {
            mSlidingPanelLayout.setPanelState(PanelState.EXPANDED);
        } else {
            mSlidingPanelLayout.setPanelState(PanelState.COLLAPSED);
        }
    }

    /**
     * 添加请求参数
     */
    protected void addRequestParams() {
        mParams.put("uuid", mUserInfo.getUuid());
        mParams.put(PARAMS_TYPE, mType);
    }

    public static void start(Context context, UserInfo userInfo, String schoolId) {
        start(context, userInfo, null, schoolId);
    }

    /**
     * 启动
     * @param context 上下文
     * @param userInfo 用户信息
     * @param parentId 家长id
     * @param schoolId 学校id
     */
    public static void start(Context context, UserInfo userInfo, String parentId, String schoolId) {
        Intent intent = new Intent(context, ParentsResourcesActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(Extra.SCHOOL_ID, schoolId);
        if (parentId != null) {
            intent.putExtra(EXTRA_PARENT_ID, parentId);
        }
        context.startActivity(intent);
        if (context instanceof Activity) {
            UIUtils.addEnterAnim( (Activity)context);
        }
    }

    public void addParam(String key, String value) {
        mParams.put(key, value);
    }

    @Override
    public void onLoadMore() {
        loadData(false);
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }
}
