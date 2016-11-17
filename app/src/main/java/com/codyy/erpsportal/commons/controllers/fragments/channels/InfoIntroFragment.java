package com.codyy.erpsportal.commons.controllers.fragments.channels;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.info.controllers.activities.MoreInformationActivity;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.NoScrollListView;
import com.codyy.erpsportal.commons.widgets.TitleItemBar;
import com.codyy.erpsportal.commons.widgets.TitleItemBar.OnMoreClickListener;
import com.codyy.erpsportal.commons.widgets.infinitepager.HolderCreator;
import com.codyy.erpsportal.commons.widgets.infinitepager.SlidePagerAdapter;
import com.codyy.erpsportal.commons.widgets.infinitepager.SlidePagerHolder;
import com.codyy.erpsportal.commons.widgets.infinitepager.SlideView;
import com.codyy.erpsportal.commons.widgets.infinitepager.SlideView.OnPageClickListener;
import com.codyy.erpsportal.info.controllers.activities.InfoDetailActivity;
import com.codyy.erpsportal.info.utils.Info;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.ConfigBus.OnModuleConfigListener;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.network.NormalPostRequest;
import com.codyy.erpsportal.commons.models.network.RequestManager;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 资讯频道页
 * Maintained by GuJiajia.
 */
public class InfoIntroFragment extends Fragment {

    private final static String TAG = "InfoIntroFragment";

    private final static int INDEX_NEWS = 0;

    private final static int INDEX_NOTICE = 1;

    private View mRootView;

    @Bind(R.id.sr_info_intro)
    SwipeRefreshLayout mInfoIntroRefreshLayout;

    /**
     * 新闻幻灯片
     */
    @Bind(R.id.slide_view)
    SlideView mSlideView;

    /**
     * 新闻标题栏
     */
    @Bind(R.id.bar_news)
    TitleItemBar mNewsTitleBar;

    /**
     * 通知标题栏
     */
    @Bind(R.id.bar_notification)
    TitleItemBar mNotificationTb;

    /**
     * 公告标题栏
     */
    @Bind(R.id.bar_announcement)
    TitleItemBar mAnnouncementTb;

    /**
     * 新闻列表
     */
    @Bind(R.id.lv_news)
    NoScrollListView mNewsLv;

    @Bind(R.id.tv_news_empty)
    TextView mNewsEmptyTv;

    /**
     * 通知列表
     */
    @Bind(R.id.lv_notification)
    NoScrollListView mNotificationLv;

    @Bind(R.id.tv_notification_empty)
    TextView mNotificationEmptyTv;

    /**
     * 公告列表
     */
    @Bind(R.id.lv_announcement)
    NoScrollListView mAnnouncementLv;

    @Bind(R.id.tv_announcement_empty)
    TextView mAnnouncementEmptyTv;

    /**
     * 新闻适配器
     */
    private ObjectsAdapter<Info, InfoContentViewHolder> mNewsAdapter;

    /**
     * 通知适配器
     */
    private ObjectsAdapter<Info, InfoContentViewHolder> mNotificationAdapter;

    /**
     * 公告适配器
     */
    private ObjectsAdapter<Info, InfoContentViewHolder> mAnnouncementAdapter;

    private RequestQueue mRequestQueue;

    private final Object mRequestTag = new Object();

    /**
     * 正在加载中的任务个数
     */
    private volatile int mOnLoadingCount;

    /**
     * 新闻为空数量，如果幻灯片与列表都为空，即为空数量等于2，则显示很抱歉，没内容，否则不显示空提示
     */
    private volatile int mNewsEmptyCount;

    /**
     * 已加载过幻灯片
     */
    private boolean mInfoSlideLoaded = false;

    /**
     * 减一下加载任务数量计数器
     */
    private void minusLoadingCount() {
        mOnLoadingCount--;
        if (mOnLoadingCount == 0 && mInfoIntroRefreshLayout.isRefreshing()) {
            mInfoIntroRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 增加新闻块为空计数器，一块是幻灯片，一块是列表，都为空时显示为空提示：很抱歉，暂时没有相关内容
     */
    private void increaseNewsEmptyCount() {
        mNewsEmptyCount++;
        if (mNewsEmptyCount >= 2) {
            mNewsEmptyTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = RequestManager.getRequestQueue();
        initViews();
        ConfigBus.register(mOnModuleConfigListener);
    }

    private void initViews() {
        if (mRootView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            mRootView = inflater.inflate(R.layout.fragment_info_intro, null);
            ButterKnife.bind(this, mRootView);
            mNewsTitleBar.setOnMoreClickListener(mOnMoreClickListener);
            mNotificationTb.setOnMoreClickListener(mOnMoreClickListener);
            mAnnouncementTb.setOnMoreClickListener(mOnMoreClickListener);
            mInfoIntroRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                }
            });
            mInfoIntroRefreshLayout.setColorSchemeResources(R.color.main_color);

            mNewsAdapter = new ObjectsAdapter<>(getActivity(), InfoContentViewHolder.class);
            mNotificationAdapter = new ObjectsAdapter<>(getActivity(), InfoContentViewHolder.class);
            mAnnouncementAdapter = new ObjectsAdapter<>(getActivity(), InfoContentViewHolder.class);

            mNewsLv.setAdapter( mNewsAdapter);
            mNotificationLv.setAdapter( mNotificationAdapter);
            mNotificationLv.setEmptyView(mNotificationEmptyTv);
            mAnnouncementLv.setAdapter( mAnnouncementAdapter);
            mAnnouncementLv.setEmptyView(mAnnouncementEmptyTv);

            mNewsLv.setOnItemClickListener(mOnItemClickListener);
            mNotificationLv.setOnItemClickListener(mOnItemClickListener);
            mAnnouncementLv.setOnItemClickListener(mOnItemClickListener);

        }
    }

    private void setTitles() {
        mNewsTitleBar.setTitle(Titles.sPagetitleIndexInfoNew);
        mNotificationTb.setTitle(Titles.sPagetitleIndexInfoNotice);
        mAnnouncementTb.setTitle(Titles.sPagetitleIndexInfoAnnouncement);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConfigBus.unregister(mOnModuleConfigListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRequestQueue.cancelAll(mRequestTag);
    }

    @Override
    public void onStop() {
        super.onStop();
        mSlideView.stopScrolling();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mInfoSlideLoaded) {
            mSlideView.startToScroll();
        }
    }

    /**
     * 加载数据
     */
    public void loadData() {
        mNewsEmptyCount = 0;
        mNewsEmptyTv.setVisibility(View.GONE);
        loadSlideNews();
        loadNews();
        loadNotification();
        loadAnnouncement();
    }

    /**
     * 加载公告
     */
    private void loadAnnouncement() {
        Map<String, String> params = new HashMap<>();
        params.put("size", "3");//?size=3&infoType=NOTICE&baseAreaId=3268c4982fbd47b996de356d00d8adcc&schoolId=
        params.put("thumbCount", "4");
        params.put("infoType", "ANNOUNCEMENT");
        ModuleConfig moduleConfig = ConfigBus.getInstance().getModuleConfig();
        params.put("baseAreaId", (moduleConfig.getBaseAreaId()));
        String schoolId = moduleConfig.getSchoolId();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }
        loadNewsItemData(URLConfig.HOME_ANNOUNCEMENT, params, 2);
    }

    /**
     * 加载通知
     */
    private void loadNotification() {
        Map<String, String> params = new HashMap<>();
        params.put("size", "3");//?size=3&infoType=NOTICE&baseAreaId=3268c4982fbd47b996de356d00d8adcc&schoolId=
        params.put("thumbCount", "4");
        params.put("infoType", "NOTICE");
        putAreaIdAndSchoolId(params);
        loadNewsItemData(URLConfig.HOME_NOTIFICATION, params, 1);
    }

    private void putAreaIdAndSchoolId(Map<String, String> params) {
        ModuleConfig moduleConfig = ConfigBus.getInstance().getModuleConfig();
        params.put("baseAreaId", moduleConfig.getBaseAreaId());
        String schoolId = moduleConfig.getSchoolId();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }
    }

    private void loadNews() {
        Map<String, String> params = new HashMap<>();
        params.put("size", "3");//?size=3&thumbCount=4&baseAreaId=3268c4982fbd47b996de356d00d8adcc&schoolId=
        params.put("thumbCount", "4");
        putAreaIdAndSchoolId(params);
        loadNewsItemData(URLConfig.HOME_NEWS, params, 0);
    }

    private void loadNewsItemData(String url, Map<String, String> params, final int position) {
        Cog.d(TAG, "loadNewsItemData url=", url, params);
        mOnLoadingCount++;
        mRequestQueue.add(new NormalPostRequest(url, params, mRequestTag,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        minusLoadingCount();
                        Cog.d(TAG, "loadNewsItemData response=", response);
                        if ("success".equals(response.optString("result"))) {
                            JSONArray news = response.optJSONArray("data");
                            final List<Info> infoList = Info.JSON_PARSER.parseArray(news);
                            if (position == 0) {
                                mNewsAdapter.setData(infoList);
                                mNewsAdapter.notifyDataSetChanged();
                                if (infoList == null || infoList.size() == 0) {
                                    increaseNewsEmptyCount();
                                }
                            } else if (position == 1) {
                                mNotificationAdapter.setData(infoList);
                                mNotificationAdapter.notifyDataSetChanged();
                                if (infoList == null || infoList.size() == 0) {//手动显示空提示，莫名其妙的重新进不显示
                                    mNotificationEmptyTv.setVisibility(View.VISIBLE);
                                }
                            } else {
                                mAnnouncementAdapter.setData(infoList);
                                mAnnouncementAdapter.notifyDataSetChanged();
                                if (infoList == null || infoList.size() == 0) {//同上
                                    mAnnouncementEmptyTv.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            showErrorMsgWhenLoadInfo(position);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showErrorMsgWhenLoadInfo(position);
                minusLoadingCount();
            }
        }));
    }

    /**
     * 根据不同请求（通知、新闻或公告）弹出不同错误提示。
     * @param position 位置
     */
    private void showErrorMsgWhenLoadInfo(int position) {
        String errorMsg;
        if (position == INDEX_NEWS) {
            increaseNewsEmptyCount();
            errorMsg = getString(R.string.obtain_s_failed, Titles.sPagetitleIndexInfoNew);
        } else if (position == INDEX_NOTICE) {
            errorMsg = getString(R.string.obtain_s_failed, Titles.sPagetitleIndexInfoNotice);
        } else {
            errorMsg = getString(R.string.obtain_s_failed, Titles.sPagetitleIndexInfoAnnouncement);
        }
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 加载幻灯片新闻
     */
    private void loadSlideNews() {
        Map<String, String> params = new HashMap<>();
        params.put("size", "4");
        putAreaIdAndSchoolId(params);
        mOnLoadingCount++;
        Cog.d(TAG, "loadSlideNews", URLConfig.HOME_NEWS_SLIDE, params);
        mRequestQueue.add(new NormalPostRequest(URLConfig.HOME_NEWS_SLIDE, params, mRequestTag,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Cog.d(TAG, "loadSlideNews response = " + response);
                        minusLoadingCount();
                        mInfoSlideLoaded = true;
                        if ("success".equals(response.optString("result"))) {
                            JSONArray slideItems = response.optJSONArray("data");
                            if (slideItems == null || slideItems.length() == 0) {
                                mSlideView.setVisibility(View.GONE);
                                increaseNewsEmptyCount();
                                return;
                            } else {
                                mSlideView.setVisibility(View.VISIBLE);
                            }
                            final List<InfoSlide> infoSlides = new ArrayList<>(slideItems.length());
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
                            mSlideView.setOnPageClickListener(new OnPageClickListener() {
                                @Override
                                public void onPageClick(int position) {
                                    Cog.d(TAG, "onPageClick p=" + position);
                                    InfoDetailActivity.startFromChannel(getActivity(), infoSlides.get(position).id);
                                }
                            });
                        } else {
                            increaseNewsEmptyCount();
                            Toast.makeText(getActivity(), "获取推荐的资讯出错!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "获取推荐的资讯出错!", Toast.LENGTH_SHORT).show();
                minusLoadingCount();
                increaseNewsEmptyCount();
            }
        }));
    }

    private OnMoreClickListener mOnMoreClickListener = new OnMoreClickListener() {
        @Override
        public void onMoreClickListener(View view) {
            if (view.getId() == R.id.bar_news) {
                MoreInformationActivity.startMore(view, Info.TYPE_NEWS);
            } else if (view.getId() == R.id.bar_notification) {
                MoreInformationActivity.startMore(view, Info.TYPE_NOTICE);
            } else {
                MoreInformationActivity.startMore(view, Info.TYPE_ANNOUNCEMENT);
            }
        }
    };

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Info info = (Info) parent.getAdapter().getItem(position);
            InfoDetailActivity.startFromChannel(getActivity(), info.getInformationId());
        }
    };

    private OnModuleConfigListener mOnModuleConfigListener = new OnModuleConfigListener() {
        @Override
        public void onConfigLoaded(ModuleConfig config) {
            Cog.d(TAG, "onConfigLoaded");
            setTitles();
            if(mRootView != null) {
                loadData();
            }
        }
    };

    public static class InfoContentViewHolder extends AbsViewHolder<Info> {

        private TextView titleTv;

        private TextView introTv;

        private SimpleDraweeView iconDv;

        @Override
        public int obtainLayoutId() {
            return R.layout.item_info_intro;
        }

        @Override
        public void mapFromView(View view) {
            titleTv = (TextView) view.findViewById(R.id.tv_title);
            introTv = (TextView) view.findViewById(R.id.tv_intro);
            iconDv = (SimpleDraweeView) view.findViewById(R.id.dv_icon);
        }

        @Override
        public void setDataToView(Info data, Context context) {
            titleTv.setText(data.getTitle());
            String content = data.getContent();
            if (TextUtils.isEmpty(content)) {
                content = "";
            }
            introTv.setText(content);
            if (TextUtils.isEmpty(data.getThumb())) {
                iconDv.setVisibility(View.GONE);
            } else {
                iconDv.setVisibility(View.VISIBLE);
                ImageFetcher.getInstance(iconDv.getContext()).fetchSmall(iconDv, data.getThumb());
            }
        }
    }

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
}
