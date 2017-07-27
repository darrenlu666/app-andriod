package com.codyy.erpsportal.commons.controllers.fragments.channels;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.activity.ClassRoomDetailActivity;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.activities.MoreSemesterLessonActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolderBuilder;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.HistoryClassViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.SipLessonViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.onlineclass.PictureViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.onlineclass.SchoolRankTitleViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.onlineclass.SchoolRankViewHolder;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.customized.SchoolRank;
import com.codyy.erpsportal.commons.models.entities.customized.SchoolRankParse;
import com.codyy.erpsportal.commons.models.entities.customized.SipLesson;
import com.codyy.erpsportal.commons.models.entities.customized.SipLessonParse;
import com.codyy.erpsportal.commons.models.entities.customized.SipSemesterLesson;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleBisectDivider;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
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
 * 首页-专递课堂(苏州园区)
 * Created by poe on 2016/6/1.
 * modified by poe on 2017/07/19.
 */
public class SipCustomizedFragment extends BaseHttpFragment implements ConfigBus.OnModuleConfigListener {
    private final String TAG = "SipCustomizedFragment";
    public static final String EXTRA_ARG_TITLE = "com.codyy.sip.title";
    /**
     * 展示位Banner图片.
     */
    private static final int TYPE_ITEM_VIEW_HOLDER_BANNER = 0x000;
    /**
     * 学段－专递课程.
     */
    private static final int TYPE_ITEM_VIEW_HOLDER_SEMESTER_CLASS = 0x001;
    /**
     * 学校排名title.
     */
    public static final int TYPE_ITEM_VIEW_HOLDER_RANK_SCHOOL_HEADER = 0x002;
    /**
     * 学校排名title.暂无数据.
     */
    public static final int TYPE_ITEM_VIEW_HOLDER_RANK_SCHOOL_HEADER_NO_DATA = 0x0020;
    /**
     * 学校排名title.
     */
    private static final int TYPE_ITEM_VIEW_HOLDER_RANK_SCHOOL = 0x003;

    @Bind(R.id.empty_view)
    EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)
    RefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private String mTitle = "同步课堂";//默认标题.传递到更多页面.
    private String baseAreaId;
    private String schoolId;
    private List<BaseTitleItemBar> mData = new ArrayList<>();
    private BaseRecyclerAdapter<BaseTitleItemBar, BaseRecyclerViewHolder> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfigBus.register(this);
        Log.i(TAG, "onCreate()");
        if (null != getArguments()) {
            mTitle = getArguments().getString(EXTRA_ARG_TITLE);
        }
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_single_recycleview;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_SIP_ONLINE_CLASS;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        data.put("baseAreaId", baseAreaId);
        data.put("schoolId", schoolId);
        data.put("size", String.valueOf(8));
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
        SipLessonParse lp = new Gson().fromJson(response.toString(), SipLessonParse.class);
        parseLessonData(lp);
        mAdapter.setData(mData);
        mAdapter.notifyDataSetChanged();
        if (mData.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
        //判断如果是学校则不显示学校排名
        if (UserInfo.USER_TYPE_AREA_USER.equals(mUserInfo.getUserType())) {
            getSchoolRankData();
        }
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
                    case TYPE_ITEM_VIEW_HOLDER_BANNER://banner picture .
                        viewHolder = new PictureViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_picture_banner_sip, parent, false));
                        break;
                    case HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE://同步课堂－学科－多行
                        viewHolder = new SipLessonViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_customized_history_class_small, parent, false));
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_RANK_SCHOOL_HEADER://学校排行header
                        viewHolder = new SchoolRankTitleViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_rank_school_title, null));
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_RANK_SCHOOL://学校排行.
                        viewHolder = new SchoolRankViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_rank_school, parent, false));
                        break;
                }
                if (null == viewHolder)
                    new Throwable("viewHolder is NULL viewType: " + viewType).printStackTrace();
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
                        MoreSemesterLessonActivity.start(getActivity(), mTitle, data.getCacheId());
                        break;
                    case HistoryClassViewHolder.ITEM_TYPE_BIG_IN_LINE://单行填充
                    case HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE://多行
                        SipLesson hc = (SipLesson) data;
                        ClassRoomDetailActivity.startActivity(getActivity(), mUserInfo, hc.getId()
                                , ClassRoomContants.TYPE_CUSTOM_RECORD, hc.getSubjectName());
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }


    /**
     * 解析同步课堂的数据　& 设置到数据栈中.
     *
     * @param lp
     */
    private void parseLessonData(SipLessonParse lp) {
        if (null != lp && "success".equals(lp.getResult())) {
            List<SipSemesterLesson> dataList = lp.getData();
            mData.clear();
            //banner picture.
            mData.add(new BaseTitleItemBar(Titles.sPagetitleIndexSipRecentClass, TYPE_ITEM_VIEW_HOLDER_BANNER));
            //解析学段
            if (null != dataList) {
                for (SipSemesterLesson ssl : dataList) {
                    if (ssl == null) continue;
                    //学段
                    if (ssl.getScheduleList() == null || ssl.getScheduleList().size() == 0) {
                        mData.add(new BaseTitleItemBar(ssl.getSemesterName(), TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA));
                    } else {
                        BaseTitleItemBar titleItemBar = new BaseTitleItemBar(ssl.getSemesterName(), TitleItemViewHolder.ITEM_TYPE_TITLE_MORE);
                        titleItemBar.setCacheId(ssl.getSemesterId());
                        mData.add(titleItemBar);
                        for (SipLesson lc : ssl.getScheduleList()) {
                            lc.setBaseViewHoldType(HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE);
                            mData.add(lc);
                        }
                    }
                }

            } else {
//                mData.add(new BaseTitleItemBar(Titles.sPagetitleSpeclassLive,TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }
        }

    }

    /**
     * 学校排名
     */
    private void getSchoolRankData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("baseAreaId", baseAreaId);
        data.put("schoolId", schoolId);
        data.put("uuid", mUserInfo.getUuid());

        requestData(URLConfig.GET_SIP_SCHOOL_RANK, data, false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response, boolean isRefreshing) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                parseSchoolRank(response);
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

    private void parseSchoolRank(JSONObject response) {
        SchoolRankParse hcp = new Gson().fromJson(response.toString(), SchoolRankParse.class);
        if (null != hcp) {
            List<SchoolRank> hcList = hcp.getData();
            //学校tag.
            mData.add(new BaseTitleItemBar("学校排行", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
            if (null != hcList) {
                if (hcList.size() == 0) {
                    //排行－暂无数据
                    mData.add(new BaseTitleItemBar("学校排行", TYPE_ITEM_VIEW_HOLDER_RANK_SCHOOL_HEADER_NO_DATA));
                } else {
                    //title .
                    mData.add(new BaseTitleItemBar("学校排行", TYPE_ITEM_VIEW_HOLDER_RANK_SCHOOL_HEADER));
                    for (int i = 0; i < hcList.size(); i++) {
                        SchoolRank hc = hcList.get(i);
                        hc.setRankPosition(i);
                        hc.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_RANK_SCHOOL);
                        mData.add(hc);
                    }
                }
            } else {
                //排行－暂无数据
                mData.add(new BaseTitleItemBar("学校排行", TYPE_ITEM_VIEW_HOLDER_RANK_SCHOOL_HEADER_NO_DATA));
            }
        }
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
