/*
 *
 * 阔地教育科技有限公司版权所有(codyy.com/codyy.cn)
 * Copyright (c)  2017, Codyy and/or its affiliates. All rights reserved.
 *
 */

package com.codyy.erpsportal.commons.controllers.fragments.channels;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.activity.ClassRoomDetailActivity;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolderBuilder;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.HistoryClassViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.HistoryRecommendViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.LivingClassViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.onlineclass.PictureViewHolder;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.customized.HistoryClass;
import com.codyy.erpsportal.commons.models.entities.customized.HistoryClassParse;
import com.codyy.erpsportal.commons.models.entities.customized.LivingClass;
import com.codyy.erpsportal.commons.models.entities.customized.LivingParse;
import com.codyy.erpsportal.commons.models.entities.mainpage.MainResClassroom;
import com.codyy.erpsportal.commons.models.listeners.MainLiveClickListener;
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
 * 首页- 台州首页(学校)(v5.3.8补充版.)
 * Created by poe on 2016/6/1.
 * copied by poe on 2017/11/30.
 */
public class TaiZhouSchoolFragment extends BaseHttpFragment implements ConfigBus.OnModuleConfigListener {
    private final String TAG = "TaiZhouSchoolFragment";
    /**
     * 展示位Banner图片.
     */
    private static final int TYPE_ITEM_VIEW_HOLDER_BANNER = 0x000;

    /**
     * 近期课程
     */
    private static final int TYPE_ITEM_VIEW_HOLDER_LIVING = 0x001;

    @Bind(R.id.empty_view) EmptyView mEmptyView;
    @Bind(R.id.refresh_layout) RefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;

    private String baseAreaId;
    private String schoolId;
    private List<BaseTitleItemBar> mData = new ArrayList<>();
    private BaseRecyclerAdapter<BaseTitleItemBar,BaseRecyclerViewHolder> mAdapter ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfigBus.register(this);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_single_recycleview;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_TZ_LIVE_APPOINTMENT;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        data.put("baseAreaId", baseAreaId);
        data.put("schoolId", schoolId);
        data.put("size", "3");
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        return data;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        if(null == mRecyclerView ) return;

        LivingParse lp = new Gson().fromJson(response.toString(), LivingParse.class);
        if(null != lp) {
            //banner picture.
            mData.add(new BaseTitleItemBar("近期课程", TYPE_ITEM_VIEW_HOLDER_BANNER));
            //1.living
            List<LivingClass> liveList = lp.getData();
            if (null != liveList) {
                if (liveList.size() == 0) {
                    mData.add(new BaseTitleItemBar("近期课程", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                } else {
                    mData.add(new BaseTitleItemBar("近期课程", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                    for (LivingClass lr : liveList) {
                        lr.setBaseViewHoldType(TYPE_ITEM_VIEW_HOLDER_LIVING);
                        mData.add(lr);
                    }
                }
            } else {
                mData.add(new BaseTitleItemBar("近期课程", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }
        }
        getRecommendLesson();
    }

    private void refresh() {
        mRecyclerView.setEnabled(false);
        mData.clear();
        mRecyclerView.getRecycledViewPool().clear();
        mAdapter.notifyDataSetChanged();
        requestData(true);
    }


    @Override
    public void onFailure(Throwable error) {
        if(null == mRecyclerView ) return;
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mRecyclerView.setEnabled(true);
        if(mData.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(false);
        }else {
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
                requestData(true);
            }
        });
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        mRecyclerView.addItemDecoration(new SimpleBisectDivider(divider, (int)getResources().getDimension(R.dimen.poe_recycler_grid_layout_padding), new SimpleBisectDivider.IGridLayoutViewHolder() {
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
                refresh();
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(mAdapter.getItemViewType(position) == HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE){
                    return  1;
                }
                return 2;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder>() {
            @Override
            public BaseRecyclerViewHolder createViewHolder(ViewGroup parent, int viewType) {
                BaseRecyclerViewHolder viewHolder = null;
                switch (viewType){
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE:
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA:
                        viewHolder = TitleItemViewHolderBuilder.getInstance().constructTitleItem(
                                parent.getContext(),parent,TitleItemViewHolderBuilder.ITEM_TIPS_SIMPLE_TEXT);
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_BANNER://banner picture .
                        viewHolder = new PictureViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_banner_tz, parent, false));
                        break;
                    case TYPE_ITEM_VIEW_HOLDER_LIVING:
                        viewHolder = new LivingClassViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_channel_interact_live, parent, false));
                        break;
                    case HistoryClassViewHolder.ITEM_TYPE_BIG_IN_LINE://单行填充
                        viewHolder =  new HistoryRecommendViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_customized_history_class,parent,false));
                        break;
                    case HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE://多行
                        viewHolder =  new HistoryRecommendViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_customized_history_class,parent,false));
                        break;
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
                if(null == data) return;
                switch (mAdapter.getItemViewType(position)){
                    case TYPE_ITEM_VIEW_HOLDER_LIVING:
                        LivingClass live = (LivingClass) data;
                        MainResClassroom room = new MainResClassroom();
                        room.setId(live.getId());
                        room.setType(MainResClassroom.TYPE_LIVE);
                        room.setStatus(live.getStatus());
                        room.setSubjectName(live.getSubjectName());
                        new MainLiveClickListener(
                                TaiZhouSchoolFragment.this, UserInfoKeeper.obtainUserInfo())
                                .onLiveClassroomClick(room);
                        break;
                    case HistoryClassViewHolder.ITEM_TYPE_BIG_IN_LINE://单行填充
                        HistoryClass hc = (HistoryClass) data;
                        ClassRoomDetailActivity.startActivity(getActivity(),
                                mUserInfo,
                                hc.getId(),
                                ClassRoomContants.TYPE_LIVE_RECORD,
                                hc.getSubjectName()
                        );
                        break;
                    case HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE://多行
                        HistoryClass lrc = (HistoryClass) data;
                        ClassRoomDetailActivity.startActivity(getActivity(),
                                mUserInfo,
                                lrc.getId(),
                                ClassRoomContants.TYPE_LIVE_RECORD,
                                lrc.getSubjectName());
                        break;
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onConfigLoaded(ModuleConfig config) {
        schoolId = config.getSchoolId();
        baseAreaId = config.getBaseAreaId();
        refresh();
    }

    /**
     * 获取推荐课堂
     */
    private void getRecommendLesson() {
        HashMap<String, String> data = new HashMap<>();
        data.put("baseAreaId", baseAreaId);
        data.put("size", "4");
        data.put("schoolId", schoolId);
        data.put("uuid",mUserInfo.getUuid());
        requestData(URLConfig.GET_TZ_LIVE_APPOINTMENT_RECOMMEND, data,false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                HistoryClassParse hcp = new Gson().fromJson(response.toString(),HistoryClassParse.class);
                if(null != hcp ) {
                    List<HistoryClass> hcList = hcp.getData();
                    if (null != hcList) {
                        if (hcList.size() == 0) {
                            mData.add(new BaseTitleItemBar("优课资源", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                        } else {
                            mData.add(new BaseTitleItemBar("优课资源", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));

                            for (HistoryClass hc : hcList) {
                                //判断--奇数/偶数
                                if (hcList.indexOf(hc) == 0) {
                                    int hcount = hcList.size();
                                    if (hcount % 2 > 0) {//奇数
                                        hc.setBaseViewHoldType(HistoryClassViewHolder.ITEM_TYPE_BIG_IN_LINE);
                                    } else {//偶数
                                        hc.setBaseViewHoldType(HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE);
                                    }
                                } else {
                                    hc.setBaseViewHoldType(HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE);
                                }
                                mData.add(hc);
                            }
                        }
                    }else{
                        mData.add(new BaseTitleItemBar("优课资源", TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                    }
                }
                mAdapter.setData(mData);
                mAdapter.notifyDataSetChanged();
                if (mData.size() <= 0) {
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
    public void onDestroy() {
        super.onDestroy();
        ConfigBus.unregister(this);
    }
}
