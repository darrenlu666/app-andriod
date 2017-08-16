package com.codyy.erpsportal.commons.controllers.fragments.channels;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.activity.ClassRoomDetailActivity;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.activities.ActivityThemeActivity;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.activities.CollectivePrepareLessonsNewActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.LessonsViewHold;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolderBuilder;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.HistoryClassViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.homepage.AnnounceViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.homepage.HomeGroupSchoolViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.homepage.HomeResourceViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.homepage.HomeTeacherViewHolder;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsShortEntity;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsShortEntityParse;
import com.codyy.erpsportal.commons.models.entities.TeachingResearchBase;
import com.codyy.erpsportal.commons.models.entities.customized.HistoryClass;
import com.codyy.erpsportal.commons.models.entities.customized.HistoryClassParse;
import com.codyy.erpsportal.commons.models.entities.mainpage.AnnounceParse;
import com.codyy.erpsportal.commons.models.entities.mainpage.GreatTeacher;
import com.codyy.erpsportal.commons.models.entities.mainpage.GreatTeacherParse;
import com.codyy.erpsportal.commons.models.entities.mainpage.MainResClassroom;
import com.codyy.erpsportal.commons.models.listeners.MainLiveClickListener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleBisectDivider;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.erpsportal.perlcourseprep.controllers.activities.MoreLessonPlansActivity;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.codyy.erpsportal.resource.models.entities.ResourceParse;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.viewholders.TitleItemViewHolder;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

/**
 * 频道页 首页（集团校）
 * created by poe on 2017/08/04 .
 */
public class MainGroupSchoolFragment extends BaseHttpFragment implements ConfigBus.OnModuleConfigListener {

    private final static String TAG = "MainGroupSchoolFragment";

    @Bind(R.id.empty_view)
    EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)
    RefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    /**
     * 新闻通知
     */
    private static final int TYPE_ITEM_VIEW_HOLDER_INFO = 0x000;
    /**
     * 教研活动
     */
    private static final int TYPE_ITEM_VIEW_HOLDER_NET_TEACH = 0x001;
    /**
     * 直播课堂
     */
    private static final int TYPE_ITEM_VIEW_HOLDER_LIVING_CLASS = 0x002;

    /**
     * 校本资源
     */
    private static final int TYPE_ITEM_VIEW_HOLDER_SCHOOL_RESOURCE = 0x011;
    /**
     * 优课资源
     */
    private static final int TYPE_ITEM_VIEW_HOLDER_LESSON_RESOURCE = 0x012;
    /**
     * 名师推荐
     */
    private static final int TYPE_ITEM_VIEW_HOLDER_TEACHER_SUGGEST = 0x013;
    /**
     * 集团学校
     */
    private static final int TYPE_ITEM_VIEW_HOLDER_GROUP_SCHOOL = 0x014;

    private String baseAreaId;
    private String schoolId;
    private List<BaseTitleItemBar> mData = new ArrayList<>();
    private BaseRecyclerAdapter<BaseTitleItemBar, BaseRecyclerViewHolder> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfigBus.register(this);
        Log.i(TAG, "onCreate()");
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_single_recycleview;
    }

    @Override
    public String obtainAPI() {//获取信息
        return URLConfig.GET_MIXINFORMATION;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        data.put("baseAreaId", baseAreaId);
        if (!TextUtils.isEmpty(schoolId)) {
            data.put("schoolId", schoolId);
        }
//        data.put("eachSize", "1"); //v5.3.4已废弃 不赋值
        data.put("thumbCount", "0");
        return data;
    }

    @Override
    public void onSuccess(JSONObject response, boolean isRefreshing) {
        if (null == mRecyclerView) return;
        if (isRefreshing) mData.clear();
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mRecyclerView.setEnabled(true);
        mEmptyView.setLoading(false);
        AnnounceParse lp = new Gson().fromJson(response.toString(), AnnounceParse.class);
        if (null != lp) {
            mData.clear();
            lp.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_INFO);
            mData.add(lp);
        }
        mAdapter.setData(mData);
        mAdapter.notifyDataSetChanged();
        if (mData.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        // 17-8-7 获取教研活动(集体备课)
        getNetTeach();
    }

    @Override
    public void onFailure(Throwable error) {
        if (null == mRecyclerView) return;
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mRecyclerView.setEnabled(true);
        if (mData.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(false);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewLoadCompleted() {
        super.onViewLoadCompleted();
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                mEmptyView.setLoading(true);
                requestData(true);
            }
        });
        //add divider item which only draw the space.
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        mRecyclerView.addItemDecoration((new SimpleBisectDivider(divider
                , (int) getResources().getDimension(R.dimen.poe_recycler_grid_layout_padding)
                , new SimpleBisectDivider.IMultiLine() {
            @Override
            public int[] obtainMultiInLine() {
                return new int[]{
                        TYPE_ITEM_VIEW_HOLDER_LIVING_CLASS
                        ,TYPE_ITEM_VIEW_HOLDER_SCHOOL_RESOURCE
                        ,TYPE_ITEM_VIEW_HOLDER_LESSON_RESOURCE
                        ,TYPE_ITEM_VIEW_HOLDER_TEACHER_SUGGEST
                        ,TYPE_ITEM_VIEW_HOLDER_GROUP_SCHOOL
                };
            }

            @Override
            public int[] obtainMultiSingleLine() {
                return new int[]{
                        TYPE_ITEM_VIEW_HOLDER_NET_TEACH
                };
            }
        })));
        mRefreshLayout.setColorSchemeColors(UiMainUtils.getColor(R.color.main_color));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.setEnabled(false);
                requestData(true);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if (mAdapter.getItemViewType(position) == TYPE_ITEM_VIEW_HOLDER_LIVING_CLASS
                        || mAdapter.getItemViewType(position) == TYPE_ITEM_VIEW_HOLDER_SCHOOL_RESOURCE
                        || mAdapter.getItemViewType(position) == TYPE_ITEM_VIEW_HOLDER_LESSON_RESOURCE
                        || mAdapter.getItemViewType(position) == TYPE_ITEM_VIEW_HOLDER_TEACHER_SUGGEST
                        || mAdapter.getItemViewType(position) == TYPE_ITEM_VIEW_HOLDER_GROUP_SCHOOL) {
                    return 1;
                }
                return 2;
            }
        });
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder>() {
            @Override
            public BaseRecyclerViewHolder createViewHolder(ViewGroup parent, int viewType) {
                BaseRecyclerViewHolder viewHolder = null;
                switch (viewType) {
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE:
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA:
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE:
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA:
                        viewHolder = TitleItemViewHolderBuilder.getInstance().constructTitleItem(
                                parent.getContext(), parent, TitleItemViewHolderBuilder.ITEM_TIPS_SIMPLE_CENTER);
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_INFO://info announce  .
                        viewHolder = new AnnounceViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_home_announce, parent, false));
                        break;

                    case TYPE_ITEM_VIEW_HOLDER_NET_TEACH://教研活动－集体备课
                        viewHolder = new LessonsViewHold(UiMainUtils.setMatchWidthAndWrapHeight(
                                parent.getContext(),
                                R.layout.item_collective_prepare_lessons),
                                TeachingResearchBase.PREPARE_LESSON);
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_LIVING_CLASS://直播课堂
                        viewHolder = new HistoryClassViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_customized_history_class_small, parent, false));
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_SCHOOL_RESOURCE://校本资源(往期录播)
                        viewHolder = new HistoryClassViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_customized_history_class_small, parent, false));
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_LESSON_RESOURCE://优课资源
                        viewHolder = new HomeResourceViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.firstpageclass_item_layout, parent, false));
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_TEACHER_SUGGEST://名师推荐
                        viewHolder = new HomeTeacherViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_home_great_teacher, parent, false));
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_GROUP_SCHOOL://集团学校.
                        viewHolder = new HomeGroupSchoolViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_home_group_school, parent, false));
                        break;
                }
                if (null == viewHolder) {
                    new Throwable("view holder is NULL ~: " + viewType).printStackTrace();
                }
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
                return mData.get(position).getBaseViewHoldType();
            }
        });

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<BaseTitleItemBar>() {
            @Override
            public void onItemClicked(View v, int position, BaseTitleItemBar data) {
                if (null == data) return;
                switch (mAdapter.getItemViewType(position)) {
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE://网络授课－更多
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA://网络授课－更多
                        if (Titles.sPagetitleNetteachInteract.equals(data.getBaseTitle())) {//互动听课
                            CollectivePrepareLessonsNewActivity.start(getActivity(), TeachingResearchBase.INTERAC_LESSON, schoolId, baseAreaId);
                        } else if (Titles.sPagetitleNetteachPrepare.equals(data.getBaseTitle())) {//个人备课
                            MoreLessonPlansActivity.start(getActivity(), baseAreaId, schoolId);
                        } else if (Titles.sPagetitleNetteachAllprepare.equals(data.getBaseTitle())) {//集体备课
                            CollectivePrepareLessonsNewActivity.start(getActivity(), TeachingResearchBase.PREPARE_LESSON, schoolId, baseAreaId);
                        } else if (Titles.sPagetitleNetteachDisucss.equals(data.getBaseTitle())) {//评课议课
                            CollectivePrepareLessonsNewActivity.start(getActivity(), TeachingResearchBase.EVALUATION_LESSON, schoolId, baseAreaId);
                        }
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_NET_TEACH://网络教研(集体备课)
                        PrepareLessonsShortEntity lc = (PrepareLessonsShortEntity) data;
                        ActivityThemeActivity.start(getActivity(), ActivityThemeActivity.PREPARE_LESSON, lc.getId(), lc.getViewCount());
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_LIVING_CLASS://直播课堂
                        new MainLiveClickListener(
                                MainGroupSchoolFragment.this, UserInfoKeeper.obtainUserInfo())
                                .onLiveClassroomClick((MainResClassroom) data);
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_SCHOOL_RESOURCE://校本资源.(课程回放)
                        HistoryClass hc2 = (HistoryClass) data;
                        ClassRoomDetailActivity.startActivity(getActivity(), mUserInfo, hc2.getId(), ClassRoomContants.TYPE_CUSTOM_RECORD, hc2.getSubjectName());//ClassRoomContants.FROM_WHERE_LINE ,
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_LESSON_RESOURCE://优课资源.
                        Resource.gotoResDetails(getActivity(), UserInfoKeeper.obtainUserInfo(), (Resource) data);
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_TEACHER_SUGGEST://名师推荐
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_GROUP_SCHOOL://集团学校
                        //  do nothing .
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }


    /**
     * 教研活动
     */
    private void getNetTeach() {
        HashMap<String, String> data = new HashMap<>();
        data.put("areaId", baseAreaId);
        data.put("schoolId", schoolId);
        data.put("userType", mUserInfo.getUserType());
        data.put("uuid", mUserInfo.getUuid());
        data.put("start", String.valueOf(0));
        data.put("end", String.valueOf(3));
        data.put("orderType", "DESC");

        requestData(URLConfig.GET_PREPARE_LESSON, data, false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response, boolean isRefreshing) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                //parse data .
                if ("success".equals(response.optString("result"))) {
                    JSONObject jsonObject = response.optJSONObject("groupPreparation");
                    PrepareLessonsShortEntityParse hcp = new Gson().fromJson(jsonObject.toString(), PrepareLessonsShortEntityParse.class);
                    if (null != hcp && hcp.getList() != null && hcp.getList().size() > 0) {
                        mData.add(new BaseTitleItemBar("教研活动", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                        for (PrepareLessonsShortEntity entity : hcp.getList()) {
                            entity.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_NET_TEACH);
                            mData.add(entity);
                        }
                    } else {
                        mData.add(new BaseTitleItemBar("教研活动", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                    }
                } else {
                    mData.add(new BaseTitleItemBar("教研活动", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                }

                mAdapter.notifyDataSetChanged();
                if (mData.size() <= 0) {
                    mEmptyView.setLoading(false);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
                //  获取直播课堂
                loadLiveClass();
            }

            @Override
            public void onRequestFailure(Throwable error) {
                onFailure(error);
                // 获取直播课堂
                loadLiveClass();
            }
        });
    }


    /**
     * 加载直播课堂
     */
    private void loadLiveClass() {
        Cog.d(TAG, "loadLiveClass areaId=", baseAreaId, ",schoolId=", schoolId);
        HashMap<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }
        params.put("baseAreaId", baseAreaId);
        params.put("size", "3");

        requestData(URLConfig.MAIN_LIVE_CLASSROOM, params, false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response, boolean isRefreshing) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                //parse data .
                if ("success".equals(response.optString("result"))) {
                    List<MainResClassroom> classroomList = MainResClassroom.PARSER
                            .parseArray(response.optJSONArray("data"));
                    if (null != classroomList && classroomList.size() > 0) {
                        mData.add(new BaseTitleItemBar("直播课堂", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                        for (MainResClassroom room : classroomList) {
                            room.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_LIVING_CLASS);
                            mData.add(room);
                        }
                    } else {
                        mData.add(new BaseTitleItemBar("直播课堂", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                    }
                } else {
                    mData.add(new BaseTitleItemBar("直播课堂", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                }

                mAdapter.notifyDataSetChanged();
                if (mData.size() <= 0) {
                    mEmptyView.setLoading(false);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
                //  获取校本资源
                getRecommendSchedule();
            }

            @Override
            public void onRequestFailure(Throwable error) {
                onFailure(error);
                //  获取校本资源
                getRecommendSchedule();
            }
        });
    }

    /**
     * 校本资源
     */
    private void getRecommendSchedule() {
        HashMap<String, String> data = new HashMap<>();
        data.put("baseAreaId", baseAreaId);
        data.put("schoolId", schoolId);
        data.put("size", String.valueOf(2));
        data.put("uuid", mUserInfo.getUuid());

        requestData(URLConfig.GET_RECOMMEND_SCHEDULE, data, false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response, boolean isRefreshing) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                HistoryClassParse hcp = new Gson().fromJson(response.toString(), HistoryClassParse.class);
                if (null != hcp) {
                    List<HistoryClass> hcList = hcp.getData();
                    if (null != hcList) {
                        if (hcList.size() == 0) {
                            mData.add(new BaseTitleItemBar(Titles.sPagetitleSpeclassReplay, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                        } else {
                            mData.add(new BaseTitleItemBar(Titles.sPagetitleSpeclassReplay, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));

                            for (HistoryClass hc : hcList) {
                                hc.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_SCHOOL_RESOURCE);
                                mData.add(hc);
                            }
                        }
                    } else {
                        mData.add(new BaseTitleItemBar(Titles.sPagetitleSpeclassReplay, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (mData.size() <= 0) {
                    mEmptyView.setLoading(false);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
                // 17-8-7 获取优课资源
                getLessonResources();
            }

            @Override
            public void onRequestFailure(Throwable error) {
                onFailure(error);
                // 17-8-7 获取优课资源
                getLessonResources();
            }
        });
    }

    /**
     * 优课资源
     */
    private void getLessonResources() {
        HashMap<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }
        params.put("baseAreaId", baseAreaId);
        params.put("size", "2");

        requestData(URLConfig.GET_RECOMMEND_RESOURCE, params, false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response, boolean isRefreshing) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                ResourceParse hcp = new Gson().fromJson(response.toString(), ResourceParse.class);
                if (null != hcp) {
                    List<Resource> hcList = hcp.getData();
                    if (null != hcList) {
                        if (hcList.size() == 0) {
                            mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexCompositeResource, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                        } else {
                            mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexCompositeResource, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));

                            for (Resource hc : hcList) {
                                hc.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_LESSON_RESOURCE);
                                mData.add(hc);
                            }
                        }
                    } else {
                        mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexCompositeResource, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (mData.size() <= 0) {
                    mEmptyView.setLoading(false);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
                // 17-8-7 获取名师推荐
                loadTeacherRecommended();
            }

            @Override
            public void onRequestFailure(Throwable error) {
                onFailure(error);
                // 17-8-7 获取名师推荐
                loadTeacherRecommended();
            }
        });
    }


    /**
     * 加载名师推荐数据
     */
    private void loadTeacherRecommended() {
        HashMap<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }
        params.put("baseAreaId", baseAreaId);
        params.put("size", "4");//请求4个数据
        params.put("type", "composite");

        requestData(URLConfig.MAIN_TEACHER_RECOMMENDED, params, false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response, boolean isRefreshing) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                GreatTeacherParse hcp = new Gson().fromJson(response.toString(), GreatTeacherParse.class);
                if (null != hcp) {
                    List<GreatTeacher> hcList = hcp.getData();
                    if (null != hcList) {
                        if (hcList.size() == 0) {
                            mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexCompositeTearec, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                        } else {
                            mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexCompositeTearec, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));

                            for (GreatTeacher hc : hcList) {
                                hc.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_TEACHER_SUGGEST);
                                mData.add(hc);
                            }
                        }
                    } else {
                        mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexCompositeTearec, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (mData.size() <= 0) {
                    mEmptyView.setLoading(false);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
                //  获取集团学校
                getGroupSchool();
            }

            @Override
            public void onRequestFailure(Throwable error) {
                onFailure(error);
                // 获取集团学校
                getGroupSchool();

            }
        });
    }

    /**
     * 获取集团校列表
     */
    private void getGroupSchool() {
        HashMap<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }
        params.put("baseAreaId", baseAreaId);
        params.put("size", "4");

        requestData(URLConfig.GET_RECOMMEND_RESOURCE, params, false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response, boolean isRefreshing) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                ResourceParse hcp = new Gson().fromJson(response.toString(), ResourceParse.class);
                if (null != hcp) {
                    List<Resource> hcList = hcp.getData();
                    if (null != hcList) {
                        if (hcList.size() == 0) {
                            mData.add(new BaseTitleItemBar("集团学校", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                        } else {
                            mData.add(new BaseTitleItemBar("集团学校", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));

                            for (Resource hc : hcList) {
                                hc.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_GROUP_SCHOOL);
                                mData.add(hc);
                            }
                        }
                    } else {
                        mData.add(new BaseTitleItemBar("集团学校", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (mData.size() <= 0) {
                    mEmptyView.setLoading(false);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {
                onFailure(error);
            }
        });
    }


    @Override
    public void onConfigLoaded(ModuleConfig config) {
        if (null == mRefreshLayout) return;
        baseAreaId = config.getBaseAreaId();
        schoolId = config.getSchoolId();
        if (mRefreshLayout == null) return;//防止界面回收还有回调
        mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConfigBus.unregister(this);
    }
}
