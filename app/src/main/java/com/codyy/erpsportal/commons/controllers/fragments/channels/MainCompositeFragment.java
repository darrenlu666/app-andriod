package com.codyy.erpsportal.commons.controllers.fragments.channels;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.erpsportal.commons.widgets.infinitepager.HolderCreator;
import com.codyy.erpsportal.commons.widgets.infinitepager.SlidePagerAdapter;
import com.codyy.erpsportal.commons.widgets.infinitepager.SlidePagerHolder;
import com.codyy.erpsportal.commons.widgets.infinitepager.SlideView;
import com.codyy.erpsportal.info.controllers.activities.InfoDetailActivity;
import com.codyy.erpsportal.info.utils.Info;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.ConfigBus.OnModuleConfigListener;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.engine.BlogPostViewStuffer;
import com.codyy.erpsportal.commons.models.engine.InfoTitleTextFactory;
import com.codyy.erpsportal.commons.models.engine.ItemFillerUtil;
import com.codyy.erpsportal.commons.models.engine.LiveClassroomViewStuffer;
import com.codyy.erpsportal.commons.models.entities.MainPageConfig;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.mainpage.MainBlogPost;
import com.codyy.erpsportal.commons.models.entities.mainpage.MainResClassroom;
import com.codyy.erpsportal.commons.models.listeners.MainLiveClickListener;
import com.codyy.erpsportal.commons.models.network.NormalPostRequest;
import com.codyy.erpsportal.commons.models.network.RequestManager;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 首页（综合）
 * Created by gujiajia on 2016/7/18.
 */
public class MainCompositeFragment extends Fragment implements OnModuleConfigListener, Callback, OnRefreshListener {

    private final static String TAG = "MainCompositeFragment";

    /**
     * 资讯切换消息，用于定时翻上面的资讯
     */
    private final static int MSG_SWITCH = 0x47;

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

    @Bind(R.id.bar_resource)
    View mResourceBar;

    @Bind(R.id.tv_res_title)
    TextView mResTitleTv;

    @Bind(R.id.gl_resources_recommendation)
    GridLayout mResourcesGl;

    @Bind(R.id.tv_resource_empty)
    TextView mNoResourceTv;

    @Bind(R.id.gl_teachers_recommendation)
    GridLayout mTeachersGl;

    @Bind(R.id.bar_live_classroom)
    View mLiveClassroomBar;

    @Bind(R.id.tv_live_classroom)
    TextView mLiveClassroomTv;

    @Bind(R.id.tv_classroom_empty)
    TextView mNoClassroomTv;

    @Bind(R.id.bar_blog)
    View mBlogBar;

    @Bind(R.id.tv_blog)
    TextView mBlogTv;

    @Bind(R.id.tv_blog_empty)
    TextView mNoBlogTv;

    @Bind(R.id.bar_teachers)
    View mTeachersBar;

    @Bind(R.id.tv_teachers)
    TextView mTeachersTv;

    @Bind(R.id.tv_teacher_empty)
    TextView mNoTeacherTv;

    private RequestQueue mRequestQueue;

    private Object mRequestTag = new Object();

    /**
     * 资讯数据json数组
     */
    private JSONArray mInfoArray;

    private Handler mHandler;

    private LayoutInflater mInflater;

    /**
     * 正在加载中的任务个数
     */
    private volatile int mOnLoadingCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(getActivity());
        mRequestQueue = RequestManager.getRequestQueue();
        mHandler = new Handler(this);

        initViews();
        ConfigBus.register(this);
    }

    private void initViews(){
        if (mRootView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            mRootView = inflater.inflate(R.layout.fragment_main_composite, null);
            ButterKnife.bind(this, mRootView);
            mRefreshLayout.setOnRefreshListener(this);
            mRefreshLayout.setColorSchemeResources(R.color.main_color);

            mInfoTitleTs.setFactory(new InfoTitleTextFactory(getActivity()));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll(mRequestTag);
        ConfigBus.unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mInfoArray != null && mInfoArray.length() > 1) {
            startSwitch();
        }
    }

    /**
     * 加载幻灯片新闻
     */
    private void loadSlideNews(String schoolId, String areaId) {
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(areaId)) params.put("baseAreaId" ,areaId);
        if (!TextUtils.isEmpty(schoolId)) params.put("schoolId", schoolId);
        params.put("size", "4");
        mOnLoadingCount++;
        Cog.d(TAG, "loadSlideNews url=", URLConfig.HOME_NEWS_SLIDE, params);
        mRequestQueue.add(new NormalPostRequest(URLConfig.HOME_NEWS_SLIDE, params, mRequestTag,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Cog.d(TAG, "loadSlideNews response = " + response);
                        minusLoadingCount();
                        if ("success".equals(response.optString("result"))) {
                            JSONArray slideItems = response.optJSONArray("data");
                            if (slideItems == null || slideItems.length() == 0) {
                                mSlideView.setVisibility(View.GONE);
                                return;
                            } else {
                                mSlideView.setVisibility(View.VISIBLE);
                            }
                            List<InfoSlide> infoSlides = new ArrayList<>(slideItems.length());
                            for (int i = 0; i < slideItems.length(); i++) {
                                JSONObject slideItem = slideItems.optJSONObject(i);
                                infoSlides.add(new InfoSlide(slideItem));
                            }

                            mSlideView.setAdapter(new SlidePagerAdapter(infoSlides, new HolderCreator() {
                                @Override
                                public SlidePagerHolder<?> create(View view) {
                                    return new InfoSlidePagerHolder(view);
                                }
                            }));
                        } else {
                            Toast.makeText(getActivity(), "获取推荐的资讯出错!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "获取推荐的资讯出错!", Toast.LENGTH_SHORT).show();
                minusLoadingCount();
            }
        }));
    }

    /**
     * 自滑动资讯幻灯片
     */
    class InfoSlide {
        public String id;
        public String title;
        public String url;

        public InfoSlide(JSONObject jsonObject) {
            id = jsonObject.optString("informationId");
            title = jsonObject.optString("title");
            url = jsonObject.optString("thumb");
        }
    }

    /**
     * 资讯幻灯片页保持器
     */
    class InfoSlidePagerHolder extends SlidePagerHolder<InfoSlide> {

        public InfoSlidePagerHolder(View view) {
            super(view);
        }

        @Override
        public void bindView(final InfoSlide infoSlide) {
            container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    InfoDetailActivity.startFromChannel(getActivity(), infoSlide.id);
                }
            });
            titleTv.setText(infoSlide.title);
            ImageFetcher.getInstance(container).fetchSmall(iconDv, infoSlide.url);
        }
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
    private void loadInfoSwitches(String areaId, String schoolId) {
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }
        params.put("eachSize", "1");
        params.put("thumbCount", "0");
        params.put("baseAreaId", areaId);
        mOnLoadingCount++;
        Cog.d(TAG, "loadInfoSwitches url=", URLConfig.GET_MIXINFORMATION, params);
        mRequestQueue.add(new NormalPostRequest(URLConfig.GET_MIXINFORMATION, params, mRequestTag,
                new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadInfoSwitches response=", response);
                minusLoadingCount();
                if ("success".equals(response.optString("result"))) {
                    mInfoArray = response.optJSONArray("data");
                    startSwitch();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
                minusLoadingCount();
            }
        }));
    }

    /**
     * 加载推荐的资源数据（与资源首页分类的资源不一样的）
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
        params.put("size", "4");
        mOnLoadingCount++;
        Cog.d(TAG, "Url:", URLConfig.GET_RECOMMEND_RESOURCE, params);
        mRequestQueue.add(new NormalPostRequest(URLConfig.GET_RECOMMEND_RESOURCE, params, mRequestTag,
                new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadMainResources response=", response);
                minusLoadingCount();
                if ("success".equals(response.optString("result"))) {
                    JSONArray resourcesJa = response.optJSONArray("data");
                    List<Resource> resources = mResourcesParser.parseArray(resourcesJa);
                    if (resources == null || resources.size() == 0) {
                        mResourcesGl.removeAllViews();
                    } else {
                        mResourcesGl.removeAllViews();
                        addResources(resources);
                        if (resources.size() % 2 != 0) {
                            View gridSpace = new View(getActivity());
                            mResourcesGl.addView(gridSpace, createGridItemLp());
                        }
                    }

                }
                updateNoResourceTv();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
                minusLoadingCount();
            }
        }));
    }

    private void updateNoResourceTv() {
        if (mResourcesGl.getChildCount() > 0) {
            mNoResourceTv.setVisibility(View.GONE);
        } else {
            mNoResourceTv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 将资源加入布局
     * @param resources 资源列表
     */
    private void addResources(List<Resource> resources) {
        for (final Resource resource: resources) {
            View view = mInflater.inflate(R.layout.firstpageclass_item_layout, mResourcesGl, false);
            SimpleDraweeView iconIvDrawee = (SimpleDraweeView) view.findViewById(
                    R.id.firstpageclass_item_simpledraweeview);
            TextView titleTv = (TextView) view.findViewById(
                    R.id.firstpageclass_item_textview);
            ImageFetcher.getInstance(getContext()).fetchSmall( iconIvDrawee, resource.getIconUrl());
            titleTv.setText( resource.getTitle());
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Resource.gotoResDetails(getActivity(), UserInfoKeeper.obtainUserInfo(), resource);
                }
            });
            mResourcesGl.addView(view, createGridItemLp());
        }
    }

    /**
     * 资源json数据解析器
     */
    private JsonParser<Resource> mResourcesParser = new JsonParser<Resource>() {
        @Override
        public Resource parse(JSONObject jsonObject) {
            Resource resource = new Resource();
            resource.setId(jsonObject.optString("resourceId"));
            resource.setTitle(jsonObject.optString("resourceName"));
            resource.setIconUrl(jsonObject.optString("thumbPath"));
            resource.setType(jsonObject.optString("resourceColumn"));
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
        mRequestQueue.add(new NormalPostRequest(URLConfig.MAIN_LIVE_CLASSROOM, params, mRequestTag,
                new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadLiveClass response=", response);
                minusLoadingCount();
                if ("success".equals(response.optString("result"))) {
                    List<MainResClassroom> classroomList = MainResClassroom.PARSER
                            .parseArray(response.optJSONArray("data"));
                    ItemFillerUtil.addItems(mContainerLl,
                            mLiveClassroomBar,
                            mNoClassroomTv,
                            classroomList,
                            new LiveClassroomViewStuffer(getActivity(), new MainLiveClickListener(
                                    MainCompositeFragment.this,UserInfoKeeper.obtainUserInfo())));
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
                minusLoadingCount();
            }
        }));
    }

    /**
     * 加载博客
     * @param baseAreaId 地区
     * @param schoolId 学校id
     */
    private void loadBlog(String baseAreaId, String schoolId) {
        Cog.d(TAG, "loadBlog areaId=", baseAreaId, ",schoolId=", schoolId);
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }
        params.put("baseAreaId", baseAreaId);
        params.put("size", "3");
        mOnLoadingCount++;
        Cog.d(TAG, "loadBlog url:", URLConfig.MAIN_BLOG, params);
        mRequestQueue.add(new NormalPostRequest(URLConfig.MAIN_BLOG, params, mRequestTag
                , new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadBlog response=", response);
                minusLoadingCount();
                if ("success".equals(response.optString("result"))) {
                    JSONArray groupJa = response.optJSONArray("data");
                    if (groupJa != null) {
                        Gson gson = new Gson();
                        List<MainBlogPost> blogPosts = gson.fromJson(groupJa.toString(),
                                new TypeToken<List<MainBlogPost>>() {
                                }.getType());
                        ItemFillerUtil.addItems(mContainerLl,
                                mBlogBar, mNoBlogTv,
                                blogPosts, new BlogPostViewStuffer(getActivity()));
                    }
                } else {
                    if (mContainerLl.indexOfChild(mNoBlogTv)
                            - mContainerLl.indexOfChild(mBlogBar) == 1) {
                        mNoBlogTv.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
                minusLoadingCount();
                tryToShowBlogEmptyView();
                if (mContainerLl.indexOfChild(mNoBlogTv)
                        - mContainerLl.indexOfChild(mBlogBar) == 1) {
                    mNoBlogTv.setVisibility(View.VISIBLE);
                }
            }
        }));
    }

    /**
     * 尝试显示没有博文
     */
    private void tryToShowBlogEmptyView() {
        if (mContainerLl.indexOfChild(mNoBlogTv)
                - mContainerLl.indexOfChild(mBlogBar) == 1) {
            mNoBlogTv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 开始切换标题
     */
    private void startSwitch() {
        if (mInfoArray == null || mInfoArray.length() < 2) {
            mHandler.removeMessages(MSG_SWITCH);
            return;
        }
        mInfoCurrentPos = 0;
        showInfo();
        switchInfo();
    }

    /**
     * 开始切换资讯
     */
    private void switchInfo() {
        mHandler.removeMessages(MSG_SWITCH);
        Message message = Message.obtain();
        message.what = MSG_SWITCH;
        message.arg1 = mInfoCurrentPos;
        mHandler.sendMessageDelayed(message, 3000);
    }

    @Override
    public void onConfigLoaded(ModuleConfig config) {
        Cog.d(TAG, "onConfigLoaded config=", config);
        if (mSlideView == null) return;//界面没加载好直接返回
        MainPageConfig mainPageConfig = config.getMainPageConfig();
        initTitles();

        if (mainPageConfig.hasInformation()) {
            mInfoSheetRl.setVisibility(View.VISIBLE);
            mSlideView.setVisibility(View.VISIBLE);
            loadSlideNews(config.getSchoolId(), config.getBaseAreaId());
            loadInfoSwitches(config.getBaseAreaId(), config.getSchoolId());
        } else {
            mInfoSheetRl.setVisibility(View.GONE);
            mSlideView.setVisibility(View.GONE);
        }

        if (mainPageConfig.hasResource()) {
            mResourceBar.setVisibility(View.VISIBLE);
            mResourcesGl.setVisibility(View.VISIBLE);
            loadMainResources(config.getBaseAreaId(), config.getSchoolId());
        } else {
            mResourceBar.setVisibility(View.GONE);
            mResourcesGl.setVisibility(View.GONE);
            mNoResourceTv.setVisibility(View.GONE);
        }
        if (mainPageConfig.hasOnlineClass() || mainPageConfig.hasLiveClass()) {
            mLiveClassroomBar.setVisibility(View.VISIBLE);
            loadLiveClass(config.getBaseAreaId(), config.getSchoolId());
        } else {
            mLiveClassroomBar.setVisibility(View.GONE);
            mNoClassroomTv.setVisibility(View.GONE);
        }
        if (mainPageConfig.hasBlog()) {
            mBlogBar.setVisibility(View.VISIBLE);
            loadBlog(config.getBaseAreaId(), config.getSchoolId());
        } else {
            mBlogBar.setVisibility(View.GONE);
            mNoBlogTv.setVisibility(View.GONE);
        }
        loadTeacherRecommended(config.getBaseAreaId(), config.getSchoolId());
    }

    /**
     * 初始化标题
     */
    private void initTitles() {
        mResTitleTv.setText(Titles.sPagetitleIndexCompositeResource);
        mLiveClassroomTv.setText(Titles.sPagetitleIndexCompositeOlclass);
        mBlogTv.setText(Titles.sPagetitleIndexCompositeBlog);
        mTeachersTv.setText(Titles.sPagetitleIndexCompositeTearec);
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
        params.put("size", "4");//请求4个数据
        params.put("type", "composite");
        mOnLoadingCount++;
        Cog.d(TAG, "loadTeacherRecommended url=", URLConfig.MAIN_TEACHER_RECOMMENDED, params);
        mRequestQueue.add(new NormalPostRequest(URLConfig.MAIN_TEACHER_RECOMMENDED, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
                    if (teacherRecommendedArray.length() % 2 > 0) {
                        View view = new View(getActivity());
                        mTeachersGl.addView(view, createGridItemLp());
                    }
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
                minusLoadingCount();
            }
        }));
    }

    /**
     * 添加教师推荐项
     * @param teacherObj 教师json数据
     */
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

        mTeachersGl.addView(view, createGridItemLp());
        final String userId = teacherObj.optString("baseUserId");
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

    /**
     * 创建名师推荐项的布局参数
     * @return 布局参数
     */
    private GridLayout.LayoutParams createGridItemLp() {
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        return layoutParams;
    }

    /**
     * 当期滚动咨询位置
     */
    private int mInfoCurrentPos;

    @Override
    public boolean handleMessage(Message msg) {
        mInfoCurrentPos = msg.arg1;
        if (msg.what == MSG_SWITCH) {
            if (isDetached()) {//如果未附上Activity，直接返回不显示资讯
                mHandler.removeMessages(MSG_SWITCH);
                return true;
            }

            showInfo();
            switchInfo();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeMessages(MSG_SWITCH);
    }

    /**
     * 显示咨询
     */
    private void showInfo() {
        String infoType = mInfoArray.optJSONObject(mInfoCurrentPos).optString("infoType");
        String infoTypeName;
        switch (infoType) {
            case Info.TYPE_NEWS:
                infoTypeName = Titles.sPagetitleIndexCompositeNew;
                break;
            case Info.TYPE_ANNOUNCEMENT:
                infoTypeName = Titles.sPagetitleIndexCompositeAnnouncement;
                break;
            default: // if (infoType.equals(Info.TYPE_NOTICE))
                infoTypeName = Titles.sPagetitleIndexCompositeNotice;
                break;
        }
        String infoTitleStr = mInfoArray.optJSONObject(mInfoCurrentPos).optString("title");
        String infoStr = String.format("(%s) %s", infoTypeName, infoTitleStr);
        mInfoTitleTs.setText(infoStr);
        mInfoCurrentPos++;
        if (mInfoCurrentPos >= mInfoArray.length())
            mInfoCurrentPos = mInfoCurrentPos % mInfoArray.length();
    }

    @OnClick(R.id.ts_info)
    public void onInfoClick() {
        if (mInfoArray != null && mInfoArray.length() != 0 && mInfoCurrentPos < mInfoArray.length()) {
            int position = mInfoCurrentPos - 1;
            if (position < 0) position = mInfoArray.length() - 1;
            JSONObject infoObj = mInfoArray.optJSONObject(position);
            if (infoObj != null) {
                InfoDetailActivity.startFromChannel(getActivity(), infoObj.optString("informationId"));
            }
        }
    }

    @Override
    public void onRefresh() {
        ModuleConfig moduleConfig = ConfigBus.getInstance().getModuleConfig();
        onConfigLoaded(moduleConfig);
    }
}
