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
import com.codyy.erpsportal.classroom.utils.DMSUtils;
import com.codyy.erpsportal.commons.controllers.activities.ActivityThemeActivity;
import com.codyy.erpsportal.commons.controllers.activities.CollectivePrepareLessonsNewActivity;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.GSLessonsViewHold;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolderBuilder;
import com.codyy.erpsportal.commons.controllers.viewholders.homepage.AnnounceViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.homepage.HomeGroupSchoolViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.homepage.HomeResourceViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.homepage.HomeTeacherViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.homepage.MainLiveViewHolder;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsShortEntity;
import com.codyy.erpsportal.commons.models.entities.TeachingResearchBase;
import com.codyy.erpsportal.commons.models.entities.mainpage.AnnounceParse;
import com.codyy.erpsportal.commons.models.entities.mainpage.FormatJsonParse;
import com.codyy.erpsportal.commons.models.entities.mainpage.GreatTeacher;
import com.codyy.erpsportal.commons.models.entities.mainpage.GroupLive;
import com.codyy.erpsportal.commons.models.entities.mainpage.GroupSchool;
import com.codyy.erpsportal.commons.models.entities.mainpage.MainResClassroom;
import com.codyy.erpsportal.commons.models.entities.mainpage.MainResource;
import com.codyy.erpsportal.commons.models.listeners.MainLiveClickListener;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleBisectDivider;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.erpsportal.perlcourseprep.controllers.activities.MoreLessonPlansActivity;
import com.codyy.erpsportal.resource.models.entities.Resource;
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
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;

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
        Cog.i(TAG,"onSuccess =>"+response.toString());

        if(response!=null && null != response.optString("seq")){
            switch (response.optString("seq")){
                case "0"://公告
                    parseAnnounce(response);
                    break;
                case "1"://网络教研
                    parseNetTeach(response);
                    break;
                case "2"://直播
                    parseLive(response);
                    break;
                case "3"://校本
                    parseSchoolResource(response);
                    break;
                case "4"://优客资源
                    parseLessonResource(response);
                    break;
                case "5"://名师推荐
                    parseTeacher(response);
                    break;
                case "6"://集团学校
                    parseGroupSchool(response);
                    if (mRefreshLayout.isRefreshing()) {
                        mRefreshLayout.setRefreshing(false);
                    }
                    mAdapter.setData(mData);
                    mAdapter.notifyDataSetChanged();
                    if (mData.size() <= 0) {
                        mEmptyView.setLoading(false);
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        mEmptyView.setVisibility(View.GONE);
                    }
                    break;
            }
        }
        // 17-8-7 获取教研活动(集体备课)
//        getNetTeach();
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
            mAdapter.setData(mData);
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
                refresh();
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
                        , TYPE_ITEM_VIEW_HOLDER_SCHOOL_RESOURCE
                        , TYPE_ITEM_VIEW_HOLDER_LESSON_RESOURCE
                        , TYPE_ITEM_VIEW_HOLDER_TEACHER_SUGGEST
                        , TYPE_ITEM_VIEW_HOLDER_GROUP_SCHOOL
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
                refresh();
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
                        viewHolder = new GSLessonsViewHold(UiMainUtils.setMatchWidthAndWrapHeight(
                                parent.getContext(),
                                R.layout.item_group_school_prepare_lessons));
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_LIVING_CLASS://直播课堂
                        viewHolder = new MainLiveViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_customized_history_class_small, parent, false));
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_SCHOOL_RESOURCE://校本资源(往期录播)
                        viewHolder = new MainLiveViewHolder(LayoutInflater.from(parent.getContext())
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
                        GroupLive live = (GroupLive) data;
                        MainResClassroom room = new MainResClassroom();
                        room.setId(live.getCourseId());
                        room.setType("live".equals(live.getType())?MainResClassroom.TYPE_ONLINE_CLASS:MainResClassroom.TYPE_LIVE);
                        room.setStatus(live.getStatus());
                        room.setSubjectName(live.getBaseSubjectName());
                        new MainLiveClickListener(
                                MainGroupSchoolFragment.this, UserInfoKeeper.obtainUserInfo())
                                .onLiveClassroomClick(room);
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_SCHOOL_RESOURCE://校本资源.(课程回放)
                        GroupLive hc2 = (GroupLive) data;
                        if("live".equals(hc2.getType())){
                            DMSUtils.enterLiving(getSender(),hc2.getCourseId(), UserInfoKeeper.obtainUserInfo().getUuid());
                        }
                        ClassRoomDetailActivity.startActivity(getActivity(),
                                mUserInfo,
                                hc2.getCourseId(),
                                "live".equals(hc2.getType())?ClassRoomContants.TYPE_CUSTOM_RECORD:ClassRoomContants.TYPE_LIVE_RECORD,
                                hc2.getBaseSubjectName());
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_LESSON_RESOURCE://优课资源.
                        MainResource resource = (MainResource) data;
                        Resource bundle = new Resource(resource.getResourceId(),
                                resource.getResourceName(),
                                resource.getThumbPath());
                        bundle.setType(resource.getResourceColumn());
                        Resource.gotoResDetails(getActivity(), UserInfoKeeper.obtainUserInfo(), bundle);
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_TEACHER_SUGGEST://名师推荐
                        GreatTeacher teacher = (GreatTeacher) data;
                        //1.自己的信息跳转到首页-"我的"
                        if(teacher.getBaseUserId().equals(mUserInfo.getBaseUserId())){
                            MainActivity.start(getActivity(), mUserInfo , 2);
                        }else{//2.访客
                            PublicUserActivity.start(getActivity(), teacher.getBaseUserId());
                        }
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_GROUP_SCHOOL://集团学校
                        //  do nothing .
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private Disposable mDisposable ;

    private void refresh() {
        //停止网络请求.
        if(null != mDisposable){
            try {
                mDisposable.dispose();
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
            }
        }

        mRecyclerView.setEnabled(false);
        mData.clear();
        mRecyclerView.getRecycledViewPool().clear();
        mAdapter.notifyDataSetChanged();
        //公告
        Observable<JSONObject> gonggao = getAnnounce();
        //教研活动
        Observable<JSONObject> netteach = getNetTeach();
        //直播课堂
        Observable<JSONObject> living = getLiveClass();
        //校本资源
        Observable<JSONObject> schoolresource = getSchoolResource();
        //优客资源
        Observable<JSONObject> goodlesson = getLessonResources();
        //名师推荐
        Observable<JSONObject> teacherrec = getTeacherRecommended();
        //集团学校
        Observable<JSONObject> groupschool = getGroupSchool();

        mDisposable = getSender().sendCombineRequest(new RequestSender.RequestData(
                obtainAPI(),
                getParam(true),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            onSuccess(response,true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.log(e);

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                onFailure(error);
                ToastUtil.showToast(getString(R.string.net_connect_error));
                LogUtils.log(error);
            }
        }
        ),gonggao,netteach,living,schoolresource,goodlesson,teacherrec,groupschool);
    }

    private Observable<JSONObject> getAnnounce() {
        return getSender().constructObservable(new RequestSender.RequestData(
                obtainAPI(), getParam(true), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseAnnounce(response);
            }
        }, null));
    }

    private void parseAnnounce(JSONObject response) {
        AnnounceParse lp = new Gson().fromJson(response.toString(), AnnounceParse.class);
        if (null != lp) {
            lp.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_INFO);
            mData.add(lp);
        }
    }


    /**
     * 教研活动
     */
    private Observable<JSONObject> getNetTeach() {
        HashMap<String, String> data = new HashMap<>();
        data.put("clsSchoolId", schoolId);
        data.put("uuid", mUserInfo.getUuid());
        return getSender().constructObservable(new RequestSender.RequestData(
                URLConfig.GET_GROUP_SCHOOL_NET_PREPARE, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseNetTeach(response);
            }
        }, null
        ));
    }

    private void parseNetTeach(JSONObject response) {
        if ("success".equals(response.optString("result"))) {
            FormatJsonParse<PrepareLessonsShortEntity> parse = new FormatJsonParse<PrepareLessonsShortEntity>().parse(response, PrepareLessonsShortEntity.class);
            if (null != parse && parse.getData() != null && parse.getData().size() > 0) {
                mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchoolTeachingActivity, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                for (PrepareLessonsShortEntity entity : parse.getData()) {
                    entity.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_NET_TEACH);
                    mData.add(entity);
                }
            } else {
                mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchoolTeachingActivity, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }
        } else {
            mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchoolTeachingActivity, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
        }
    }


    /**
     * 加载直播课堂
     */
    private Observable<JSONObject> getLiveClass() {
        HashMap<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("clsSchoolId", schoolId);
        }
        params.put("uuid", mUserInfo.getUuid());

        return getSender().constructObservable(new RequestSender.RequestData(
                URLConfig.GET_GROUP_SCHOOL_LIVING_LESSON, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseLive(response);
            }
        }, null
        ));
    }

    private void parseLive(JSONObject response) {
        if ("success".equals(response.optString("result"))) {
            FormatJsonParse<GroupLive> parse = new FormatJsonParse<GroupLive>().parse(response, GroupLive.class);
            if (null != parse && parse.getData().size() > 0) {
                mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchoolLiveClass, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                for (GroupLive room : parse.getData()) {
                    room.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_LIVING_CLASS);
                    mData.add(room);
                }
            } else {
                mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchoolLiveClass, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }
        } else {
            mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchoolLiveClass, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
        }
    }

    /**
     * 校本资源
     */
    private Observable<JSONObject> getSchoolResource() {
        HashMap<String, String> data = new HashMap<>();
        data.put("clsSchoolId", schoolId);
        return getSender().constructObservable(new RequestSender.RequestData(
                URLConfig.GET_GROUP_SCHOOL_HISTORY_LESSON, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseSchoolResource(response);
            }
        }, null
        ));
    }

    private void parseSchoolResource(JSONObject response) {
        FormatJsonParse<GroupLive> hcp = new FormatJsonParse<GroupLive>().parse(response, GroupLive.class);
        if (null != hcp) {
            List<GroupLive> hcList = hcp.getData();
            if (null != hcList) {
                if (hcList.size() == 0) {
                    mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchoolSchoolResource, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                } else {
                    mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchoolSchoolResource, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));

                    for (GroupLive hc : hcList) {
                        hc.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_SCHOOL_RESOURCE);
                        mData.add(hc);
                    }
                }
            } else {
                mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchoolSchoolResource, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }
        }
    }

    /**
     * 优课资源
     */
    private Observable<JSONObject> getLessonResources() {
        HashMap<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }

        return getSender().constructObservable(new RequestSender.RequestData(
                URLConfig.GET_GROUP_SCHOOL_RESOURCE, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseLessonResource(response);
            }
        }, null
        ));
    }

    private void parseLessonResource(JSONObject response) {
        FormatJsonParse<MainResource> hcp = new FormatJsonParse<MainResource>().parse(response, MainResource.class);
        if (null != hcp) {
            List<MainResource> hcList = hcp.getData();
            if (null != hcList) {
                if (hcList.size() == 0) {
                    mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchoolResource, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                } else {
                    mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchoolResource, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));

                    for (MainResource hc : hcList) {
                        hc.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_LESSON_RESOURCE);
                        mData.add(hc);
                    }
                }
            } else {
                mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchoolResource, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }
        }
    }


    /**
     * 加载名师推荐数据
     */
    private Observable<JSONObject> getTeacherRecommended() {
        HashMap<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        }
        params.put("size", "4");//请求4个数据
        params.put("type", "composite");
        return getSender().constructObservable(new RequestSender.RequestData(
                URLConfig.GET_GROUP_SCHOOL_TEACHER_RECOMMEND, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseTeacher(response);
            }
        }, null
        ));
    }

    private void parseTeacher(JSONObject response) {
        FormatJsonParse<GreatTeacher> hcp = new FormatJsonParse<GreatTeacher>().parse(response, GreatTeacher.class);
        if (null != hcp) {
            List<GreatTeacher> hcList = hcp.getData();
            if (null != hcList) {
                if (hcList.size() == 0) {
                    mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchoolTeacherSuggest, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                } else {
                    mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchoolTeacherSuggest, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));

                    for (GreatTeacher hc : hcList) {
                        hc.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_TEACHER_SUGGEST);
                        mData.add(hc);
                    }
                }
            } else {
                mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchoolTeacherSuggest, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }
        }
    }

    /**
     * 获取集团校列表
     */
    private Observable<JSONObject> getGroupSchool() {
        HashMap<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("clsSchoolId", schoolId);
        }
        params.put("size", "4");
        return getSender().constructObservable(new RequestSender.RequestData(
                URLConfig.GET_GROUP_SCHOOL_LIST, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseGroupSchool(response);
            }
        }, null
        ));
    }

    private void parseGroupSchool(JSONObject response) {
        FormatJsonParse<GroupSchool> hcp = new FormatJsonParse<GroupSchool>().parse(response, GroupSchool.class);
        if (null != hcp) {
            List<GroupSchool> hcList = hcp.getData();
            if (null != hcList) {
                if (hcList.size() == 0) {
                    mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchool, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                } else {
                    mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchool, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));

                    for (GroupSchool hc : hcList) {
                        hc.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_GROUP_SCHOOL);
                        mData.add(hc);
                    }
                }
            } else {
                mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexClubSchool, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }
        }
    }


    @Override
    public void onConfigLoaded(ModuleConfig config) {
        if (null == mRefreshLayout) return;
        baseAreaId = config.getBaseAreaId();
        schoolId = config.getSchoolId();
        if (mRefreshLayout == null) return;//防止界面回收还有回调
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConfigBus.unregister(this);
    }
}
