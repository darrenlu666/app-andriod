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
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ActivityThemeActivity;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.activities.CollectivePrepareLessonsNewActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.LessonsViewHold;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolderBuilder;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.HistoryClassViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.homepage.AnnounceViewHolder;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.engine.ItemFillerUtil;
import com.codyy.erpsportal.commons.models.engine.LiveClassroomViewStuffer;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsShortEntity;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsShortEntityParse;
import com.codyy.erpsportal.commons.models.entities.TeachingResearchBase;
import com.codyy.erpsportal.commons.models.entities.customized.HistoryClass;
import com.codyy.erpsportal.commons.models.entities.customized.HistoryClassParse;
import com.codyy.erpsportal.commons.models.entities.mainpage.AnnounceParse;
import com.codyy.erpsportal.commons.models.entities.mainpage.MainResClassroom;
import com.codyy.erpsportal.commons.models.listeners.MainLiveClickListener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleBisectDivider;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.erpsportal.perlcourseprep.controllers.activities.MoreLessonPlansActivity;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.viewholders.TitleItemViewHolder;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 频道页 首页（资源&资源无直播）
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
        data.put("eachSize", "1");
        data.put("thumbCount", "0");
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
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
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        mRecyclerView.addItemDecoration(new SimpleBisectDivider(divider, (int) getResources().getDimension(R.dimen.poe_recycler_grid_layout_padding), new SimpleBisectDivider.IGridLayoutViewHolder() {
            @Override
            public int obtainSingleBigItemViewHolderType() {
                return HistoryClassViewHolder.ITEM_TYPE_BIG_IN_LINE;
            }

            @Override
            public int obtainMultiInLineViewHolderType() {
                return HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE;
            }
        }));
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

                if (mAdapter.getItemViewType(position) == HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE) {
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
                                parent.getContext(), parent, TitleItemViewHolderBuilder.ITEM_TIPS_SIMPLE_TEXT);
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
                    /*case HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE://课程回放
                        viewHolder = new HistoryClassViewHolderSip(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_customized_history_class_small, parent, false));
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_INTERACT_CLASS://互动听课
                        viewHolder = new SipInteractClassViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_interact_class_sip, parent, false));
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_PERSONAL_PREPARE_CLASS://个人备课
                        viewHolder = new SipPersonalClassViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_personal_class_sip, parent, false));
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_GROUP_PREPARE_CLASS://集体备课
                        viewHolder = new SipInteractClassViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_interact_class_sip, parent, false));
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_EVALUATE_CLASS://评课议课
                        viewHolder = new SipEvaluateClassViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_evaluate_class_sip, parent, false));
                        break;*/
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
                        ActivityThemeActivity.start(getActivity(),ActivityThemeActivity.PREPARE_LESSON,lc.getId(),lc.getViewCount());
                        break;
                    /*case HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE://课程回放.
                        HistoryClass hc2 = (HistoryClass) data;
                        ClassRoomDetailActivity.startActivity(getActivity(), mUserInfo, hc2.getId(), ClassRoomContants.TYPE_CUSTOM_RECORD, hc2.getSubjectName());//ClassRoomContants.FROM_WHERE_LINE ,
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_INTERACT_CLASS://互动听课
                        if (data instanceof SipNetResearch) {
                            ActivityThemeActivity.start(getActivity(), ActivityThemeActivity.INTERACT_LESSON
                                    , ((SipNetResearch) data).getId()
                                    , ((SipNetResearch) data).getViewCount());
                            ((SipNetResearch) data).setViewCount(((SipNetResearch) data).getViewCount() + 1);
                            mAdapter.notifyItemChanged(position);
                        }
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_PERSONAL_PREPARE_CLASS://个人备课
                        PersonalLesPrepContentActivity.start(getActivity(), ((SipNetResearch) data).getId());
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_GROUP_PREPARE_CLASS://集体备课
                        if (data instanceof SipNetResearch) {
                            ActivityThemeActivity.start(getActivity(), ActivityThemeActivity.PREPARE_LESSON
                                    , ((SipNetResearch) data).getId(), ((SipNetResearch) data).getViewCount());
                            ((SipNetResearch) data).setViewCount(((SipNetResearch) data).getViewCount() + 1);
                            mAdapter.notifyItemChanged(position);
                        }
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_EVALUATE_CLASS://评课议课
                        if (data instanceof SipNetResearch) {
                            SipNetResearch netResearch = (SipNetResearch) data;
                            EvaluationScore evaluationScore = new EvaluationScore();
                            evaluationScore.setTotalScore(NumberUtils.floatOf(netResearch.getTotalScore()));
                            evaluationScore.setScoreType(netResearch.getScoreType());
                            evaluationScore.setAvgScore(netResearch.getAverageScore());
                            ActivityThemeActivity.start(getActivity(), ActivityThemeActivity.EVALUATION_LESSON,
                                    netResearch.getId(),
                                    netResearch.getViewCount(),
                                    evaluationScore);
                            ((SipNetResearch) data).setViewCount(((SipNetResearch) data).getViewCount() + 1);
                            mAdapter.notifyItemChanged(position);
                        }
                        break;*/
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
                    if(null != hcp && hcp.getList() != null && hcp.getList().size()>0){
                        mData.add(new BaseTitleItemBar("教研活动",TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                        for(PrepareLessonsShortEntity entity: hcp.getList()){
                            entity.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_NET_TEACH);
                            mData.add(entity);
                        }
                    }else{
                        mData.add(new BaseTitleItemBar("教研活动",TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                    }
                }else{
                    mData.add(new BaseTitleItemBar("教研活动",TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
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
                    if(null != classroomList && classroomList.size()>0){
                        mData.add(new BaseTitleItemBar("直播课堂",TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                        for (MainResClassroom room : classroomList){
                            room.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_LIVING_CLASS);
                            mData.add(room);
                        }
                    }else{
                        mData.add(new BaseTitleItemBar("直播课堂",TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                    }
                }else{
                    mData.add(new BaseTitleItemBar("直播课堂",TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                }

                mAdapter.notifyDataSetChanged();
                if (mData.size() <= 0) {
                    mEmptyView.setLoading(false);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
                // TODO: 17-8-7 获取校本资源

            }

            @Override
            public void onRequestFailure(Throwable error) {
                onFailure(error);
                // TODO: 17-8-7 获取校本资源

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
        data.put("size", String.valueOf(4));
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
                                hc.setBaseViewHoldType(HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE);
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
                // TODO: 17-8-7 获取直播课堂
            }

            @Override
            public void onRequestFailure(Throwable error) {
                onFailure(error);
                // TODO: 17-8-7 获取直播课堂
            }
        });
    }

    /**
     * 处理网络授课.
     *
     * @param
     */
    /*private void parseNetTeachJson(JSONObject response) {
        if (null == response || !"success".equals(response.optString("result"))) return;
        JSONObject interactJson = response.optJSONObject("interactionListen");
        JSONObject personJson = response.optJSONObject("personPrepareLesson");
        JSONObject groupJson = response.optJSONObject("groupPreparation");
        JSONObject evaluationJson = response.optJSONObject("evaluationAndDiscussion");
        // 21/07/17 互动听课解析
        if (null != interactJson
                && interactJson.optInt("total") > 0) {
            //add title .
            mData.add(new BaseTitleItemBar(Titles.sPagetitleNetteachInteract, TitleItemViewHolder.ITEM_TYPE_TITLE_MORE));
            //parse data .

            SipNetResearchParse parse = new Gson().fromJson(interactJson.toString(), SipNetResearchParse.class);
            if (null != parse && parse.getList() != null) {
                //set item type and add to stack .
                for (SipNetResearch snr : parse.getList()) {
                    snr.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_INTERACT_CLASS);
                    mData.add(snr);
                }

            } else {
                throw new IllegalArgumentException("interact json return format error & poe can not parse it !");
            }

        } else {
            mData.add(new BaseTitleItemBar(Titles.sPagetitleNetteachInteract, TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA));
        }

        // 21/07/17 个人备课
        if (null != personJson
                && personJson.optInt("total") > 0) {
            //add title .
            mData.add(new BaseTitleItemBar(Titles.sPagetitleNetteachPrepare, TitleItemViewHolder.ITEM_TYPE_TITLE_MORE));
            //parse data .

            SipNetResearchParse parse = new Gson().fromJson(personJson.toString(), SipNetResearchParse.class);
            if (null != parse && parse.getList() != null) {
                //set item type and add to stack .
                for (SipNetResearch snr : parse.getList()) {
                    snr.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_PERSONAL_PREPARE_CLASS);
                    mData.add(snr);
                }

            } else {
                throw new IllegalArgumentException("interact json return format error & poe can not parse it !");
            }

        } else {
            mData.add(new BaseTitleItemBar(Titles.sPagetitleNetteachPrepare, TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA));
        }
        // 21/07/17 集体备课
        if (null != groupJson
                && groupJson.optInt("total") > 0) {
            //add title .
            mData.add(new BaseTitleItemBar(Titles.sPagetitleNetteachAllprepare, TitleItemViewHolder.ITEM_TYPE_TITLE_MORE));
            //parse data .

            SipNetResearchParse parse = new Gson().fromJson(groupJson.toString(), SipNetResearchParse.class);
            if (null != parse && parse.getList() != null) {
                //set item type and add to stack .
                for (SipNetResearch snr : parse.getList()) {
                    snr.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_GROUP_PREPARE_CLASS);
                    mData.add(snr);
                }

            } else {
                throw new IllegalArgumentException("interact json return format error & poe can not parse it !");
            }

        } else {
            mData.add(new BaseTitleItemBar(Titles.sPagetitleNetteachAllprepare, TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA));
        }
        // 21/07/17 评课议课
        if (null != evaluationJson
                && evaluationJson.optInt("total") > 0) {
            //add title .
            mData.add(new BaseTitleItemBar(Titles.sPagetitleNetteachDisucss, TitleItemViewHolder.ITEM_TYPE_TITLE_MORE));
            //parse data .

            SipNetResearchParse parse = new Gson().fromJson(evaluationJson.toString(), SipNetResearchParse.class);
            if (null != parse && parse.getList() != null) {
                //set item type and add to stack .
                for (SipNetResearch snr : parse.getList()) {
                    snr.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_EVALUATE_CLASS);
                    mData.add(snr);
                }

            } else {
                throw new IllegalArgumentException("interact json return format error & poe can not parse it !");
            }

        } else {
            mData.add(new BaseTitleItemBar(Titles.sPagetitleNetteachDisucss, TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA));
        }
    }*/

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
