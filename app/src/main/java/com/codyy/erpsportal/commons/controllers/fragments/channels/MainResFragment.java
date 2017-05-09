package com.codyy.erpsportal.commons.controllers.fragments.channels;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.ResourceSlidePagerHolder;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.ConfigBus.OnModuleConfigListener;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.engine.GroupViewStuffer;
import com.codyy.erpsportal.commons.models.engine.InfosSwitcher;
import com.codyy.erpsportal.commons.models.engine.ItemFillerUtil;
import com.codyy.erpsportal.commons.models.engine.LiveClassroomViewStuffer;
import com.codyy.erpsportal.commons.models.engine.ViewStuffer;
import com.codyy.erpsportal.commons.models.entities.MainPageConfig;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.mainpage.MainResClassroom;
import com.codyy.erpsportal.commons.models.entities.mainpage.MainResGroup;
import com.codyy.erpsportal.commons.models.listeners.MainLiveClickListener;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.models.parsers.JsonParseUtil;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.erpsportal.commons.widgets.infinitepager.HolderCreator;
import com.codyy.erpsportal.commons.widgets.infinitepager.SlidePagerAdapter;
import com.codyy.erpsportal.commons.widgets.infinitepager.SlidePagerHolder;
import com.codyy.erpsportal.commons.widgets.infinitepager.SlideView;
import com.codyy.erpsportal.resource.controllers.viewholders.VideoItemViewHolder;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.codyy.url.URLConfig;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 频道页 首页（资源&资源无直播）
 * Created by gujiajia on 2015/9/11.
 */
public class MainResFragment extends Fragment {

    private final static String TAG = "MainResFragment";

    public final static String ARG_NO_LIVE = "ARG_NO_LIVE";

    private View mRootView;

    @Bind(R.id.ll_container)
    LinearLayout mContainerLl;

    @Bind(R.id.rl_main_res)
    RefreshLayout mRefreshLayout;

    @Bind(R.id.slide_view)
    SlideView mSlideView;

    @Bind(R.id.rl_info_sheet)
    RelativeLayout mInfoSheetRl;

    @Bind(R.id.ts_info)
    TextSwitcher mInfoTitleTs;

    @Bind({R.id.fl_title1, R.id.fl_title2, R.id.fl_title3})
    FrameLayout[] mTitleFls;

    @Bind({R.id.tv_title1, R.id.tv_title2, R.id.tv_title3})
    protected TextView[] mTitleTvs;

    @Bind({R.id.gv_resource1, R.id.gv_resource2, R.id.gv_resource3})
    protected LinearLayout[] mResourceGvs;

    @Bind({R.id.bar_live_classroom})
    protected View mLiveClassroomBar;

    @Bind({R.id.tv_live_classroom})
    protected TextView mLiveClassroomTv;

    @Bind(R.id.tv_classroom_empty)
    TextView mNoClassroomTv;

    @Bind(R.id.bar_teachers)
    protected View mTeachersBar;

    @Bind(R.id.gl_teachers_recommendation)
    GridLayout mTeachersGl;

    @Bind({R.id.tv_teachers})
    protected TextView mTeachersTv;

    @Bind(R.id.tv_teacher_empty)
    TextView mNoTeacherTv;

//    private RequestQueue mRequestQueue;
//
//    private Object mRequestTag = new Object();

    private WebApi mWebApi;

    private CompositeDisposable mCompositeDisposable;

    private InfosSwitcher mInfosSwitcher;

    private LayoutInflater mInflater;

    /**
     * 正在加载中的任务个数
     */
    private volatile int mOnLoadingCount;

    /**
     * 无直播，显示圈组
     */
    private boolean mNoLive;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAttributes();
        initViews();
        ConfigBus.register(mOnModuleConfigListener);
    }

    private void initAttributes() {
        mInflater = LayoutInflater.from(getActivity());
        mWebApi = RsGenerator.create(WebApi.class);
        mCompositeDisposable = new CompositeDisposable();
        if (getArguments() != null) {
            mNoLive = getArguments().getBoolean(ARG_NO_LIVE);
        }
    }

    private void initViews(){
        if (mRootView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            mRootView = inflater.inflate(R.layout.fragment_main_res, null);
            ButterKnife.bind(this, mRootView);
            mInfoSheetRl.setVisibility(View.GONE);
            mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ModuleConfig moduleConfig = ConfigBus.getInstance().getModuleConfig();
                    mOnModuleConfigListener.onConfigLoaded(moduleConfig);
                }
            });
            mRefreshLayout.setColorSchemeResources(R.color.main_color);

            mInfosSwitcher = new InfosSwitcher(this, mInfoTitleTs);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
        mOnLoadingCount = 0;
        ConfigBus.unregister(mOnModuleConfigListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mInfosSwitcher.resume();
    }

    /**
     * 加载资源幻灯片数据
     * @param schoolId 学校id
     * @param areaId 地区id
     */
    private void loadSlides(String areaId, String schoolId) {
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(areaId)) params.put("baseAreaId" ,areaId);
        if (!TextUtils.isEmpty(schoolId)) params.put("schoolId", schoolId);
        mOnLoadingCount++;
        Cog.d(TAG, "loadSlides url=" + URLConfig.SLIDE_RESOURCES + params);
        Disposable disposable = mWebApi.post4Json(URLConfig.SLIDE_RESOURCES, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "loadSlides response=", response);
                        minusLoadingCount();
                        if ("success".equals(response.optString("result"))) {
                            JSONArray slideItems = response.optJSONArray("data");
                            if (slideItems == null || slideItems.length() == 0) {
                                mSlideView.setVisibility(View.GONE);
                            } else {
                                mSlideView.setVisibility(View.VISIBLE);
                                List<Resource> resources = JsonParseUtil.parseArray(slideItems, mResourceParser);
                                mSlideView.setAdapter(new SlidePagerAdapter(resources, new HolderCreator() {
                                    @Override
                                    public SlidePagerHolder<?> create(View view) {
                                        return new ResourceSlidePagerHolder(view);
                                    }
                                }));
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
                        minusLoadingCount();
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    private void minusLoadingCount() {
        mOnLoadingCount--;
        if (mOnLoadingCount == 0) {
            mRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 加载滚动的资讯数据
     * @param areaId 地区id
     * @param schoolId 学校id
     */
    private void loadInfoSlides(String areaId, String schoolId) {
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }
        params.put("eachSize", "1");
        params.put("thumbCount", "0");
        params.put("baseAreaId", areaId);
        mOnLoadingCount++;
        Cog.d(TAG, "loadInfoSlides url=", URLConfig.GET_MIXINFORMATION, params);
        Disposable disposable = mWebApi.post4Json(URLConfig.GET_MIXINFORMATION, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "loadInfoSlides response=", response);
                        minusLoadingCount();
                        if ("success".equals(response.optString("result"))) {
                            JSONArray infoArray = response.optJSONArray("data");
                            if (infoArray == null || infoArray.length() == 0) {
                                mInfoSheetRl.setVisibility(View.GONE);
                            } else {
                                mInfoSheetRl.setVisibility(View.VISIBLE);
                                mInfosSwitcher.setInfoArray(infoArray);
                                mInfosSwitcher.startSwitch();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
                        minusLoadingCount();
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    /**
     * 加载资源数据
     * @param areaId 地区id
     * @param schoolId 学校id
     */
    private void loadMainResources(String areaId, String schoolId) {
        Cog.d(TAG, "loadMainResources areaId=",areaId,",schoolId=",schoolId);
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }
        params.put("baseAreaId", areaId);
        params.put("categorySize", "3");
        params.put("resourceSize", "2");
        mOnLoadingCount++;
        Cog.d(TAG, "Url:", URLConfig.MAIN_RESOURCES, params);
        Disposable disposable = mWebApi.post4Json(URLConfig.MAIN_RESOURCES, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "loadMainResources response=", response);
                        minusLoadingCount();
                        if ("success".equals(response.optString("result"))) {
                            JSONArray resourceGroups = response.optJSONArray("data");
                            for (LinearLayout ll : mResourceGvs) {//清空原来的资源
                                ll.removeAllViews();
                            }
                            for (int i = 0; i < mTitleTvs.length; i++ ) {
                                if (i >= resourceGroups.length()) {
                                    mTitleFls[i].setVisibility(View.GONE);
                                    mResourceGvs[i].removeAllViews();
                                    mResourceGvs[i].setVisibility(View.GONE);
                                } else {
                                    mTitleFls[i].setVisibility(View.VISIBLE);
                                    mResourceGvs[i].setVisibility(View.VISIBLE);

                                    JSONObject resourceGroup = resourceGroups.optJSONObject(i);
                                    mTitleTvs[i].setText(resourceGroup.optString("categoryName"));
                                    JSONArray resourceArray = resourceGroup.optJSONArray("resources");
                                    List<Resource> resources = JsonParseUtil.parseArray(resourceArray, mResourceParser);

                                    if (resources != null) {
                                        for (final Resource resource : resources) {
                                            VideoItemViewHolder videoItemViewHolder = new VideoItemViewHolder();
                                            View view = mInflater.inflate(videoItemViewHolder.obtainLayoutId(), mResourceGvs[i], false);
                                            videoItemViewHolder.mapFromView(view);
                                            videoItemViewHolder.setDataToView(resource, getActivity());
                                            LayoutParams layoutParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
                                            layoutParams.weight = 1;
                                            mResourceGvs[i].addView(view, layoutParams);
                                            view.setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Resource.gotoResDetails(getActivity(), UserInfoKeeper.obtainUserInfo(), resource);
                                                }
                                            });
                                        }
                                    } else {
                                        TextView noContentsTv = new TextView(getActivity());
                                        noContentsTv.setText(R.string.sorry_for_no_contents);
                                        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                                        layoutParams.gravity = Gravity.CENTER;
                                        noContentsTv.setGravity(Gravity.CENTER);
                                        int padding = UIUtils.dip2px(getContext(), 4);
                                        noContentsTv.setPadding(0, padding, 0, padding);
                                        mResourceGvs[i].addView(noContentsTv, layoutParams);
                                    }
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
                        minusLoadingCount();
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    /**
     * 资源json数据解析器
     */
    private JsonParser<Resource> mResourceParser = new JsonParser<Resource>() {
        @Override
        public Resource parse(JSONObject jsonObject) {
            Resource resource = new Resource();
            resource.setId(jsonObject.optString("resourceId"));
            resource.setTitle(jsonObject.optString("resourceName"));
            resource.setType(jsonObject.optString("resourceColumn"));
            resource.setIconUrl(jsonObject.optString("thumbPath"));
            return resource;
        }
    };

    /**
     * 加载直播课堂
     * @param areaId 地区id
     * @param schoolId 学校id
     */
    private void loadLiveClass(String areaId, String schoolId) {
        Cog.d(TAG, "loadLiveClass areaId=", areaId, ",schoolId=", schoolId);
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }
        params.put("baseAreaId", areaId);
        params.put("size", "3");
        mOnLoadingCount++;
        Disposable disposable = mWebApi.post4Json(URLConfig.MAIN_LIVE_CLASSROOM, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "loadLiveClass response=", response);
                        minusLoadingCount();
                        if ("success".equals(response.optString("result"))) {
                            List<MainResClassroom> classroomList = MainResClassroom.PARSER
                                    .parseArray(response.optJSONArray("data"));
                            addItemsBetween(classroomList,
                                    new LiveClassroomViewStuffer(getActivity(), new MainLiveClickListener(
                                            MainResFragment.this, UserInfoKeeper.obtainUserInfo())));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
                        minusLoadingCount();
                        if (mContainerLl.indexOfChild(mNoClassroomTv)
                                - mContainerLl.indexOfChild(mLiveClassroomBar) == 1) {
                            mNoClassroomTv.setVisibility(View.VISIBLE);
                        }
                    }
                });
        mCompositeDisposable.add( disposable);
    }

    /**
     * 设置可配标题
     */
    private void initTitles() {
        if (mNoLive) {
            mLiveClassroomTv.setText(Titles.sPagetitleIndexNoLiveHotgroup);
            mTeachersTv.setText(Titles.sPagetitleIndexNoLiveTearec);
        } else {
            mLiveClassroomTv.setText(Titles.sPagetitleIndexResourceOlclass);
            mTeachersTv.setText(Titles.sPagetitleIndexResourceTearec);
        }
    }

    private void loadGroups(String baseAreaId, String schoolId) {
        Cog.d(TAG, "loadGroups areaId=", baseAreaId, ",schoolId=", schoolId);
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }
        params.put("baseAreaId", baseAreaId);
        params.put("size", "4");
        mOnLoadingCount++;
        Cog.d(TAG, "loadGroups url:", URLConfig.MAIN_GROUPS, params);
        Disposable disposable = mWebApi.post4Json(URLConfig.MAIN_GROUPS, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "loadGroups response=", response);
                        minusLoadingCount();
                        if ("success".equals(response.optString("result"))) {
                            JSONArray groupJa = response.optJSONArray("hotGroups");
                            Gson gson = new Gson();
                            List<MainResGroup> groupList = gson.fromJson(groupJa.toString(),
                                    new TypeToken<List<MainResGroup>>(){}.getType());
                            addItemsBetween(groupList,
                                    new GroupViewStuffer(getActivity()));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
                        minusLoadingCount();
                        if (mContainerLl.indexOfChild(mNoClassroomTv)
                                - mContainerLl.indexOfChild(mLiveClassroomBar) == 1) {
                            mNoClassroomTv.setVisibility(View.VISIBLE);
                        }
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    /**
     * mLiveClassroomBar与mNoClassroomTv间在添加项
     * @param list 实体列表
     * @param viewStuffer 视图组件数据填充器
     * @param <T> 实体类型
     */
    private <T> void addItemsBetween(List<T> list, ViewStuffer<T> viewStuffer) {
        addItemsBetween(mContainerLl, mLiveClassroomBar, mNoClassroomTv, list, viewStuffer);
    }

    /**
     * 在container的head与tail之间插入布局id为layoutId的view
     * @param container 容器
     * @param titleView 标题
     * @param emptyView 空提示view
     * @param list 实体列表
     * @param viewStuffer 视图组件数据填充器
     * @param <T> 实体类型
     */
    private <T> void addItemsBetween(LinearLayout container, View titleView, View emptyView,
            List<T> list, ViewStuffer<T> viewStuffer) {
        ItemFillerUtil.addItems(container, titleView, emptyView, list, viewStuffer);
    }

    /**
     * 加载名师推荐数据
     * @param areaId 地区id
     * @param schoolId 学校id
     */
    private void loadTeacherRecommended(String areaId, String schoolId) {
        Cog.d(TAG, "loadTeacherRecommended areaId=",areaId,",schoolId=",schoolId);
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }
        params.put("baseAreaId", areaId);
        params.put("size", "4");
        params.put("type", "resource");
        mOnLoadingCount++;
        Cog.d(TAG, "loadTeacherRecommended url=", URLConfig.MAIN_TEACHER_RECOMMENDED, params);
        Disposable disposable = mWebApi.post4Json(URLConfig.MAIN_TEACHER_RECOMMENDED, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "loadTeacherRecommended response=", response);
                        minusLoadingCount();
                        if ("success".equals(response.optString("result"))) {
                            JSONArray teacherRecommendedArray = response.optJSONArray("data");
                            if (teacherRecommendedArray == null || teacherRecommendedArray.length() == 0) {
                                mTeachersGl.setVisibility(View.GONE);
                                mNoTeacherTv.setVisibility(View.VISIBLE);
                                return;
                            } else {
                                mTeachersGl.setVisibility(View.VISIBLE);
                                mNoTeacherTv.setVisibility(View.GONE);
                            }
                            mTeachersGl.removeAllViews();
                            for (int i = 0; i < teacherRecommendedArray.length(); i++) {
                                JSONObject teacherObj = teacherRecommendedArray.optJSONObject(i);
                                addTeacher(teacherObj);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
                        minusLoadingCount();
                        if (mTeachersGl.getChildCount() ==0) {
                            mTeachersGl.setVisibility(View.GONE);
                            mNoTeacherTv.setVisibility(View.VISIBLE);
                        }
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    private void addTeacher(JSONObject teacherObj) {
        View view = mInflater.inflate(R.layout.famousteacher_layout_item, mTeachersGl, false);
        SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(R.id.famousteacher_image);
        TextView nameTv = (TextView) view.findViewById(R.id.famousteacher_name);
        TextView gradeTv = (TextView) view.findViewById(R.id.famousteacher_grade);

        nameTv.setText(teacherObj.optString("realName"));

        String subjectName = null;
        String gradeName = null;
        if (!teacherObj.isNull("subjectName")) {
            subjectName = teacherObj.optString("subjectName");
        }

        if (!teacherObj.isNull("classlevelName")) {
            gradeName = teacherObj.optString("classlevelName");
        }

        if (TextUtils.isEmpty(subjectName) && TextUtils.isEmpty(gradeName)) {
            gradeTv.setVisibility(View.INVISIBLE);
        } else if (!TextUtils.isEmpty(subjectName) && TextUtils.isEmpty(gradeName)) {
            gradeTv.setText(subjectName);
        } else if (TextUtils.isEmpty(subjectName) && !TextUtils.isEmpty(gradeName)) {
            gradeTv.setText(gradeName);
        } else {
            gradeTv.setText(gradeName + "/" + subjectName);
        }

        if (!teacherObj.isNull("headPic")) {
            String imageName = teacherObj.optString("headPic");
            if (!TextUtils.isEmpty(imageName)) {
                ImageFetcher.getInstance(getActivity()).fetchSmall(draweeView, imageName);
            }
        }

        final String userId = teacherObj.optString("baseUserId");

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        mTeachersGl.addView(view, layoutParams);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity == null) return;
                UserInfo userInfo = mainActivity.getUserInfo();
                if (userInfo == null) return;
                if(userId.equals(userInfo.getBaseUserId())){
                    MainActivity.start(getActivity() , userInfo , 2);
                }else{//2.访客
                    PublicUserActivity.start(getActivity() , userId);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mInfosSwitcher.release();
    }

    private OnModuleConfigListener mOnModuleConfigListener = new OnModuleConfigListener() {
        @Override
        public void onConfigLoaded(ModuleConfig config) {
            Cog.d(TAG, "onConfigLoaded config=", config);
            if (mSlideView == null) return;//界面没加载好直接返回
            MainPageConfig mainPageConfig = config.getMainPageConfig();
            initTitles();
            if (mainPageConfig.hasResource()) {
                loadSlides(config.getBaseAreaId(), config.getSchoolId());
            } else {
                mSlideView.setVisibility(View.GONE);
            }

            if (mainPageConfig.hasInformation()) {
                loadInfoSlides(config.getBaseAreaId(), config.getSchoolId());
            } else {
                mInfoSheetRl.setVisibility(View.GONE);
            }

            if (mainPageConfig.hasResource()) {
                loadMainResources(config.getBaseAreaId(), config.getSchoolId());
            }

            if (mNoLive) {
                if (mainPageConfig.hasGroup()) {
                    loadGroups(config.getBaseAreaId(), config.getSchoolId());
                } else {
                    mLiveClassroomBar.setVisibility(View.GONE);
                    mNoClassroomTv.setVisibility(View.GONE);
                }
            } else {
                if (mainPageConfig.hasOnlineClass() || mainPageConfig.hasLiveClass()) {
                    loadLiveClass(config.getBaseAreaId(), config.getSchoolId());
                } else {
                    mLiveClassroomBar.setVisibility(View.GONE);
                    mNoClassroomTv.setVisibility(View.GONE);
                }
            }
            loadTeacherRecommended(config.getBaseAreaId(), config.getSchoolId());
        }
    };
}
