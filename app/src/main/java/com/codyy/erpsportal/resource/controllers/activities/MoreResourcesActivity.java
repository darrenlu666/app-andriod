package com.codyy.erpsportal.resource.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.Choice;
import com.codyy.erpsportal.commons.models.entities.EduLevel;
import com.codyy.erpsportal.commons.models.entities.EduLevelResult;
import com.codyy.erpsportal.commons.models.entities.FilterItem;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.NormalGetRequest;
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
import com.codyy.erpsportal.commons.widgets.slidinguppanel.ResSlidingUpPanelLayout.PanelSlideListener;
import com.codyy.erpsportal.commons.widgets.slidinguppanel.ResSlidingUpPanelLayout.PanelState;
import com.codyy.erpsportal.resource.controllers.adapters.ResourcesAdapter;
import com.codyy.erpsportal.resource.controllers.adapters.ResourcesAdapter.OnItemClickListener;
import com.codyy.erpsportal.resource.controllers.adapters.ResourcesAdapter.OnLoadMoreListener;
import com.codyy.erpsportal.resource.controllers.others.AudioListPlayController;
import com.codyy.erpsportal.resource.controllers.others.AudioListPlayController.OnAudioPlayingListener;
import com.codyy.erpsportal.resource.controllers.others.FilterTypeMenuController;
import com.codyy.erpsportal.resource.controllers.others.FilterTypeMenuController.Builder;
import com.codyy.erpsportal.resource.controllers.others.FilterTypeMenuController.OnMenuClickListener;
import com.codyy.erpsportal.resource.models.entities.Audio;
import com.codyy.erpsportal.resource.models.entities.AudioEvent;
import com.codyy.erpsportal.resource.models.entities.Document;
import com.codyy.erpsportal.resource.models.entities.Image;
import com.codyy.erpsportal.resource.models.entities.Video;
import com.codyy.erpsportal.resource.utils.CountIncreaser;
import com.codyy.erpsportal.resource.utils.ViewCreator;
import com.codyy.erpsportal.resource.widgets.AudioControlBar;
import com.codyy.erpsportal.statistics.models.entities.AreaInfo;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 530资源模块页
 * Created by gujiajia on 2016/7/6
 */
public class MoreResourcesActivity extends AppCompatActivity {

    private final static String TAG = "MoreResourcesActivity";

    private static final int LOAD_COUNT = 10;

    /**
     * 最小加载间隔，加载太快看不到加载动画。
     */
    private final static long MIN_LOADING_INTERVAL = 500L;

    private final static int MSG_COLLAPSE_PANEL = 23;

    private final static int MSG_UPDATE_PROGRESS = 21;

    private final static String PARAMS_SEMESTER = "baseSemesterId";

    private final static String PARAMS_TYPE = "resType";

    private final static String PARAMS_ORDER = "orderBy";

    private List<EduLevel> mSemesters;

    @StringDef({TYPE_ALL, TYPE_VIDEO, TYPE_AUDIO, TYPE_DOCUMENT, TYPE_IMAGE})
    @interface ResourceType {}

    public static final String TYPE_ALL = "ALL";

    public static final String TYPE_VIDEO = "VIDEO";

    public static final String TYPE_AUDIO = "AUDIO";

    public static final String TYPE_DOCUMENT = "DOC";

    public static final String TYPE_IMAGE = "IMAGE";

    @StringDef({ORDER_TIME, ORDER_RATE, ORDER_VIEW_COUNT, ORDER_DOWNLOAD_COUNT})
    @interface ResourceOrder { }

    public static final String ORDER_TIME = "CREATE_TIME";

    public static final String ORDER_RATE = "RATING_AVG";

    public static final String ORDER_VIEW_COUNT = "VIEW_CNT";

    public static final String ORDER_DOWNLOAD_COUNT = "DOWNLOAD_CNT";

    @Bind(R.id.tv_empty)
    TextView mEmptyTv;

    @Bind(R.id.title_bar)
    TitleBar mTitleBar;

    @Bind(R.id.ll_simple_filter)
    LinearLayout mSimpleFilterLl;

    @Bind(R.id.tv_current_scope)
    TextView mCurrentScopeTv;

    @Bind(R.id.rl_resources)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.rv_resources)
    RecyclerView mRecyclerView;

    @Bind(R.id.pl_scope)
    ResSlidingUpPanelLayout mSlidingPanelLayout;

    @Bind(R.id.rg_from)
    RadioGroup mSemesterRg;

    @Bind(R.id.rg_type)
    RadioGroup mTypeRg;

    @Bind(R.id.rg_order)
    RadioGroup mOrderRg;

    @Bind(R.id.sv_order_filter_items)
    HorizontalScrollView mOrderFilterItemsSv;

    @Bind(R.id.audio_control_bar)
    AudioControlBar mAudioControlBar;

    private ResourcesAdapter mAdapter;

    private int mStart;

    private Map<String, String> mParams;

    private RequestSender mRequestSender;

    private Object mRequestTag = new Object();

    private Handler mHandler;

    private UserInfo mUserInfo;

    private AreaInfo mAreaInfo;

    private String mSemesterId;

    private String mSemesterName;

    private String mUrl;

    /**
     * 资源类型
     */
    @ResourceType
    private String mType = TYPE_VIDEO;

    /**
     * 资源排序
     */
    @ResourceOrder
    private String mOrder = ORDER_TIME;

    private int mRadioBtnPadding;

    private ItemDecoration mItemDecoration;

    private boolean mPanelStateCollapsed = true;

    private FilterTypeMenuController mFilterTypeMenuController;

    /**
     * 音频播放控制器
     */
    private AudioListPlayController mPlayController;

    private PanelSlideListener mPanelSlideListener = new PanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            Cog.d(TAG, "onPanelSlide slideOffset=", slideOffset);
            Cog.d(TAG, "onPanelSlide slideDistance=", slideOffset * panel.getHeight());
            Cog.d(TAG, "onPanelSlide panel bottom=", panel.getBottom());
            int panelBottom = panel.getBottom();
            int simpleFilterBarHeight = mSimpleFilterLl.getHeight();
            if (panelBottom >= simpleFilterBarHeight) {
                MarginLayoutParams lp = (MarginLayoutParams) mRefreshLayout.getLayoutParams();
                lp.topMargin = panelBottom - simpleFilterBarHeight;
                mRefreshLayout.setLayoutParams(lp);
            }
        }

        @Override
        public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
            Cog.d(TAG, "onPanelStateChanged previousState=", previousState, ",newState=", newState);
            mPanelStateCollapsed = newState == PanelState.COLLAPSED;
        }
    };

    private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            loadData(false);
        }
    };

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Cog.d(TAG, "onItemClick position=", position);
            Object object = mAdapter.getItem( position);
            if (object instanceof Video){
                Video video = (Video) object;
                VideoDetailsActivity.start(MoreResourcesActivity.this, mUserInfo, video.getId());
            } else if (object instanceof Audio) {
                List<Audio> audios = mAdapter.getData();
                AudioDetailsActivity.start(MoreResourcesActivity.this, mUserInfo, audios, position);
            } else if (object instanceof Document) {
                Document document = (Document) object;
                DocumentContentActivity.start(MoreResourcesActivity.this, mUserInfo, document);
            } else if (object instanceof Image) {
                Image image = (Image) object;
                ImageDetailsActivity.start(MoreResourcesActivity.this, mUserInfo, image.getId());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources_new);
        ButterKnife.bind(this);
        initAttributes();
        initViews();

        addRequestParams();
        loadData(true);
    }

    private void initAttributes() {
        mHandler = new Handler( mHandlerCallback);
        mRadioBtnPadding = UIUtils.dip2px(this, 8);
        mItemDecoration = new DividerItemDecoration(this);

        mType = TYPE_VIDEO;
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mSemesterId = getIntent().getStringExtra(Extra.SEMESTER_ID);
        mSemesterName = getIntent().getStringExtra(Extra.SEMESTER_NAME);
        mAreaInfo = getIntent().getParcelableExtra(Extra.AREA_INFO);
        mParams = new HashMap<>();
        mRequestSender = new RequestSender(this);
        mUrl = URLConfig.MORE_RESOURCE;
    }

    private void initViews() {
        mSlidingPanelLayout.addPanelSlideListener(mPanelSlideListener);
        mSlidingPanelLayout.setContentScrollableView(mRecyclerView);

        mTitleBar.setTitle(R.string.resource);
        mRefreshLayout.setColorSchemeResources(R.color.main_color);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(true);
            }
        });
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new ResourcesAdapter( mRecyclerView, mOnLoadMoreListener);
        mAdapter.setOnItemClickListener( mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        initSemesterFilterItems();
        updateCurrentScopeTv();

        mFilterTypeMenuController = new Builder().setActivity(this)
                .setTitleBar(mTitleBar)
                .setUserInfo(mUserInfo)
                .setOnMenuClickListener(new OnMenuClickListener() {
                    @Override
                    public void onPropertiesBtnClick() {
                        if (mSemesterId != null) {
                            String semesterPlaceId = getSemesterPlaceId();
                            ResourcePropertyFilterAct.startForResult(MoreResourcesActivity.this, mAreaInfo,
                                    new Choice(mSemesterId, semesterPlaceId, mSemesterName), mUserInfo.isStudent());
                        } else {
                            ResourcePropertyFilterAct.startForResult(MoreResourcesActivity.this, mAreaInfo, mUserInfo.isStudent());
                        }
                    }

                    @Override
                    public void onKnowledgeBtnClick() {
                        if (mSemesterId != null) {
                            String semesterPlaceId = getSemesterPlaceId();
                            ResourceKnowledgeFilterAct.startForResult(MoreResourcesActivity.this, mAreaInfo,
                                    new Choice(mSemesterId, semesterPlaceId, mSemesterName));
                        } else {
                            ResourceKnowledgeFilterAct.startForResult(MoreResourcesActivity.this, mAreaInfo);
                        }
                    }
                })
                .build();
        mPlayController = new AudioListPlayController.Builder()
                .setActivity(this)
                .setAudioControlBar(mAudioControlBar)
                .setAdapter(mAdapter)
                .setOnAudioPlayingListener(new OnAudioPlayingListener() {
                    @Override
                    public void onAudioPlaying(String audioId) {
                        Cog.d(TAG, "onAudioPlaying audioId=", audioId);
                        CountIncreaser.increaseViewCount(mRequestSender, mRequestTag, mUserInfo.getUuid(), audioId);
                    }
                })
                .build();
    }

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
    protected void onPause() {
        super.onPause();
        mPlayController.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayController.onDestroy();
        mRequestSender.stop(mRequestTag);
    }

    public void onEvent(AudioEvent audioEvent) {
        mPlayController.onAudioClick(audioEvent);
    }

    /**
     * 更新过滤范围文字描述
     */
    private void updateCurrentScopeTv() {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(mSemesterName)) {
            sb.append(mSemesterName).append(" • ");
        }
        RadioButton typeRb = (RadioButton) mTypeRg.findViewById(mTypeRg.getCheckedRadioButtonId());
        sb.append(typeRb.getText());
        if (mOrderRg.getVisibility() == View.VISIBLE) {
            RadioButton orderRb = (RadioButton) mOrderRg.findViewById(mOrderRg.getCheckedRadioButtonId());
            sb.append(" • ").append(orderRb.getText());
        }
        mCurrentScopeTv.setText(sb.toString());
    }

    private void initSemesterFilterItems() {
        String url = URLConfig.GET_SEMESTER_LIST;
        if (mAreaInfo.isArea()) {
            url = url + "?areaId=" + mAreaInfo.getId();
        } else {
            url = url + "?schoolId=" + mAreaInfo.getId();
        }
        Cog.d(TAG, "initSemesterFilterItems url=", url);
        mRequestSender.add(new NormalGetRequest(url, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "initSemesterFilterItems response=" + response);
                Gson gson = new Gson();
                EduLevelResult eduLevelResult = gson.fromJson(response.toString(), EduLevelResult.class);
                if ("success".equals(eduLevelResult.getResult())) {
                    mSemesters = eduLevelResult.getList();
                    for (EduLevel eduLevel : mSemesters) {
                        addFromFilterItem(eduLevel.getName(), eduLevel.getId());
                    }
                    View view = mSemesterRg.findViewWithTag(mSemesterId);
                    if (view != null) {
                        mSemesterRg.check(view.getId());
                    }
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.e(TAG, "initSemesterFilterItems error=" + error);
                Toast.makeText(MoreResourcesActivity.this, "获取学段列表失败。", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private int mRadioButtonId;

    /**
     * 添加来源筛选项
     *
     * @param text
     * @param from
     */
    private void addFromFilterItem(String text, String from) {
        RadioButton radioButton = ViewCreator.createRadioButton(this, mRadioBtnPadding);
        radioButton.setId(mRadioButtonId++);
        radioButton.setText(text);
        radioButton.setTag(from);
        radioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onSemesterFilterItemSelected(v);
            }
        });
        mSemesterRg.addView(radioButton);
    }

    /**
     * 学段过滤项被选中
     *
     * @param view 被选中的组件
     */
    private void onSemesterFilterItemSelected(View view) {
        Object tag = view.getTag();
        if (tag == null || !(tag instanceof String)) return;
        if (tag.equals( mSemesterId)) return;
        mSemesterId = (String) tag;
        mSemesterName = ((RadioButton)view).getText().toString();
        if (updateRequestParam(PARAMS_SEMESTER, mSemesterId)) {
            loadData(true);
        }
        updateCurrentScopeTv();
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
    }

    private void onTypeNotAudioChanged() {
        mPlayController.stopAudio();
    }

    private void updateType(@NonNull @ResourceType String type) {
        if (!type.equals(mType)) {
            mType = type;
            updateCurrentScopeTv();
            if (updateRequestParam(PARAMS_TYPE, mType)) {
                loadData(true);
            }
        }
    }

    @OnClick({R.id.rb_order_time, R.id.rb_order_rate, R.id.rb_order_heat, R.id.rb_order_download})
    public void onResourceOrderClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rb_order_time:
                if (checked) updateOrder(ORDER_TIME);
                break;
            case R.id.rb_order_rate:
                if (checked) updateOrder(ORDER_RATE);
                break;
            case R.id.rb_order_heat:
                if (checked) updateOrder(ORDER_VIEW_COUNT);
                break;
            case R.id.rb_order_download:
                if (checked) updateOrder(ORDER_DOWNLOAD_COUNT);
                break;
        }
    }

    @OnClick(R.id.ib_filter)
    public void onFilterClick(View view) {
        mFilterTypeMenuController.onFilterBtnClick();
    }

    /**
     * 获取学段-地方id
     * @return
     */
    private String getSemesterPlaceId() {
        if (mSemesters == null || TextUtils.isEmpty(mSemesterId)) return null;
        for (EduLevel eduLevel: mSemesters) {
            if (eduLevel.getId().equals(mSemesterId)) {
                return eduLevel.getPlaceId();
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ArrayList<FilterItem> propertyFilterItems = null;
            if (requestCode == ResourcePropertyFilterAct.REQUEST_FILTER) {
                propertyFilterItems = data.getParcelableArrayListExtra(
                        ResourcePropertyFilterAct.EXTRA_PROPERTY_FILTER);
            } else if (requestCode == ResourceKnowledgeFilterAct.REQUEST_FILTER) {
                propertyFilterItems = data.getParcelableArrayListExtra(
                        ResourceKnowledgeFilterAct.EXTRA_KNOWLEDGE_FILTER);
            }
            Map<String, String> filterParams = FilterItem.obtainParams(propertyFilterItems);
            Cog.d(TAG, "onActivityResult filterParams=", filterParams);
            if (extractSemester(filterParams) || mergeParams(filterParams)) {
                loadData(true);
            }
        }
    }

    /**
     * 从过滤参数中提取学段
     * @param filterParams 过滤参数
     * @return 学段是否改变
     */
    private boolean extractSemester(Map<String, String> filterParams) {
        boolean semesterChanged = false;
        String semesterId = filterParams.get(PARAMS_SEMESTER);
        if (semesterId != null) {
            if (!semesterId.equals(mSemesterId)) {
                mSemesterId = semesterId;
                RadioButton semesterRb = (RadioButton) mSemesterRg.findViewWithTag(mSemesterId);
                mSemesterRg.check(semesterRb.getId());
                mSemesterName = semesterRb.getText().toString();
                updateCurrentScopeTv();
                addParam(PARAMS_SEMESTER, mSemesterId);
                semesterChanged = true;
            }
        } else {
            if (mSemesterId != null) {
                mSemesterId = null;
                mSemesterName = null;
                mSemesterRg.clearCheck();
                updateCurrentScopeTv();
                removeParam(PARAMS_SEMESTER);
                semesterChanged = true;
            }
        }
        return semesterChanged;
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
     * 更新排序
     *
     * @param order
     */
    private void updateOrder(@NonNull @ResourceOrder String order) {
        if (!order.equals(mOrder)) {
            mOrder = order;
            updateCurrentScopeTv();
            if (updateRequestParam(PARAMS_ORDER, mOrder)) {
                loadData(true);
            }
        }
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
                                handleErrorResponse(error, refresh);
                            }
                        });
                    }
                }, mRequestTag )
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

    private Callback mHandlerCallback = new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_COLLAPSE_PANEL) {
                if (!mPanelStateCollapsed) {
                    mSlidingPanelLayout.setPanelState(PanelState.COLLAPSED);
                }
                return true;
            } else if (msg.what == MSG_UPDATE_PROGRESS) {
//                updateProgressBar();
//                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS,100);
//                return true;
            }
            return false;
        }
    };

    /**
     * 响应回调
     */
    interface ResponseCallable {
        void handle();
    }

    /**
     * 处理请求响应
     *
     * @param response
     * @param isRefreshing
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
     * @param response
     * @return
     */
    protected boolean checkSuccessful(JSONObject response) {
        return "success".equals(response.optString("result"));
    }

    /**
     * 检查是否有更多，默认方式是比较total字段与当前项数
     *
     * @param response
     * @param itemCount 已有item数量
     * @return
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

    protected List getList(JSONObject response) {
        JsonParser parser;
        switch (mType) {
            case (TYPE_VIDEO):
                parser = Video.sParser;
                break;
            case (TYPE_AUDIO):
                return Audio.sParser.parseArrayAdditionally(response.optJSONArray("data"),
                        new OnParsedListener<Audio>() {
                    @Override
                    public void handleParsedObj(Audio audio) {
                        mPlayController.updateCurrentAudio(audio);
                    }
                });
            case (TYPE_DOCUMENT):
                parser = Document.sParser;
                break;
            case (TYPE_IMAGE):
                parser = Image.sParser;
                break;
            default:
                return null;
        }
        return parser.parseArray(response.optJSONArray("data"));
    }

    /**
     * 处理错误响应
     *
     * @param error   错误信息
     * @param refresh 是否是刷新
     */
    private void handleErrorResponse(VolleyError error, boolean refresh) {
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

    private String obtainEmptyText() {
        RadioButton typeRb = (RadioButton) mTypeRg.findViewById(mTypeRg.getCheckedRadioButtonId());
        String emptyText;
        if (typeRb == null) {
            emptyText = getString(R.string.no_data_for_now);
        } else {
            emptyText = getString(R.string.no_some_type_resources, typeRb.getText());
        }
        return emptyText;
    }

    @OnClick({R.id.ib_open_simple_filter, R.id.tv_current_scope})
    protected void onOpenSimpleFilterClick() {
        mSlidingPanelLayout.setPanelState(PanelState.EXPANDED);
    }

    /**
     * 添加请求参数
     */
    protected void addRequestParams() {
        mParams.put("uuid", mUserInfo.getUuid());
        addRegionParams();

        if (TextUtils.isEmpty(mSemesterId)){
            mParams.remove(PARAMS_SEMESTER);
        } else {
            mParams.put(PARAMS_SEMESTER , mSemesterId);
        }

        if (TYPE_ALL.equals(mType) || TextUtils.isEmpty(mType)) {
            mParams.remove(PARAMS_TYPE);
        } else {
            mParams.put(PARAMS_TYPE, mType);
            if (mType.equals(TYPE_AUDIO)) {
                mParams.put(PARAMS_TYPE, TYPE_VIDEO);
            }
        }

        if (TextUtils.isEmpty(mOrder)) {
            mParams.remove(PARAMS_ORDER);
        } else {
            mParams.put(PARAMS_ORDER, mOrder);
        }
    }

    private void addRegionParams() {
        if (mUserInfo != null) {//如果已经登录了，使用登录信息的地区id和学校id
            putAreaIdAndSchoolId(mUserInfo.getBaseAreaId(), mUserInfo.getSchoolId());
        } else {
            if (mAreaInfo.isSchool()) {
                mParams.put("schoolId", mAreaInfo.getId());
            } else if (mAreaInfo.isArea()) {
                mParams.put("baseAreaId", mAreaInfo.getId());
            }
        }
    }

    /**
     * 请求参数中加入地区id和学校id
     * @param areaId
     * @param schoolId
     */
    private void putAreaIdAndSchoolId(String areaId, String schoolId) {
        if (!TextUtils.isEmpty(areaId)) {
            mParams.put("baseAreaId", areaId);
        }
        if (!TextUtils.isEmpty(schoolId)) {
            mParams.put("schoolId", schoolId);
        }
    }

    public static void start(Context context, AreaInfo areaInfo,String semesterId, String semesterName) {
        Intent intent = new Intent(context, MoreResourcesActivity.class);
        intent.putExtra(Extra.AREA_INFO, areaInfo);
        intent.putExtra(Extra.SEMESTER_ID, semesterId);
        intent.putExtra(Extra.SEMESTER_NAME, semesterName);
        context.startActivity(intent);
    }

    public void addParam(String key, String value) {
        mParams.put(key, value);
    }

    public void removeParam(String key) {
        mParams.remove(key);
    }

    public void addMapToParam(Map<String, String> newParams) {
        mParams.putAll(newParams);
    }

}
