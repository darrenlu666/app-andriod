package com.codyy.erpsportal.commons.controllers.fragments.channels;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.activity.ClassRoomDetailActivity;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolderBuilder;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.HistoryClassViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.LivingClassViewHolder;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.customized.HistoryClass;
import com.codyy.erpsportal.commons.models.entities.customized.HistoryClassParse;
import com.codyy.erpsportal.commons.models.entities.customized.LivingClass;
import com.codyy.erpsportal.commons.models.entities.customized.LivingParse;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleBisectDivider;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

/**
 * 首页-专递课堂v5.3.0
 * Created by poe on 2016/6/1.
 */
public class ChannelCustomizedFragment extends BaseHttpFragment implements ConfigBus.OnModuleConfigListener {
    private final String TAG = "ChannelCustomizedFragment";
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
        return URLConfig.GET_SCHEDULE_LIVE;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        data.put("baseAreaId", baseAreaId);
        data.put("schoolId", schoolId);
        data.put("size", String.valueOf(4));
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        return  data ;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        if(null == mRecyclerView ) return;
        if(isRefreshing) mData.clear();
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mRecyclerView.setEnabled(true);
        mEmptyView.setLoading(false);
        LivingParse lp = new Gson().fromJson(response.toString(),LivingParse.class);
        if(null != lp){
            List<LivingClass> dataList = lp.getData();
            mData.clear();
            if(null != dataList){
                if(dataList.size() ==0){
                    mData.add(new BaseTitleItemBar(Titles.sPagetitleNetclassLive,TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                }else{
                    mData.add(new BaseTitleItemBar(Titles.sPagetitleNetclassLive,TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                    for(LivingClass lc : dataList){
                        lc.setBaseViewHoldType(LivingClassViewHolder.ITEM_TYPE_LIVING);
                        mData.add(lc);
                    }
                }
            }else{
                mData.add(new BaseTitleItemBar(Titles.sPagetitleNetclassLive,TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }
        }
        mAdapter.setData(mData);
        mAdapter.notifyDataSetChanged();
        if (mData.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        getRecommendSchedule();
    }

    @Override
    public void onFailure(VolleyError error) {
        if(null == mRecyclerView ) return;
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mRecyclerView.setEnabled(true);
        if(mData.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(false);
        }else {
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
                mRecyclerView.setEnabled(false);
                requestData(true);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if(mAdapter.getItemViewType(position) == HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE){
                    return  1;
                }
                return 2;
            }
        });
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
                    case LivingClassViewHolder.ITEM_TYPE_LIVING:
                    case LivingClassViewHolder.ITEM_TYPE_LIVING_PREPARE:
                        //解决半边view无法点击
                        viewHolder =  new LivingClassViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate( R.layout.item_channel_interact_live,parent,false));
                        break;
                    case HistoryClassViewHolder.ITEM_TYPE_BIG_IN_LINE://单行填充
                        viewHolder =  new HistoryClassViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_customized_history_class,parent,false));
                        break;
                    case HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE://多行
                        viewHolder =  new HistoryClassViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_customized_history_class_small,parent,false));
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
                    case LivingClassViewHolder.ITEM_TYPE_LIVING:
                    case LivingClassViewHolder.ITEM_TYPE_LIVING_PREPARE:
                        //解决半边view无法点击
                        LivingClass lc = (LivingClass) data;
                        ClassRoomDetailActivity.startActivity(getActivity(),mUserInfo,lc.getId(),ClassRoomContants.TYPE_CUSTOM_LIVE,lc.getSubjectName());//ClassRoomContants.FROM_WHERE_LINE ,
                        break;
                    case HistoryClassViewHolder.ITEM_TYPE_BIG_IN_LINE://单行填充
                        HistoryClass hc = (HistoryClass) data;
                        ClassRoomDetailActivity.startActivity(getActivity(),mUserInfo,hc.getId(),ClassRoomContants.TYPE_CUSTOM_RECORD,hc.getSubjectName());//ClassRoomContants.FROM_WHERE_LINE ,
                        break;
                    case HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE://多行
                        HistoryClass hc2 = (HistoryClass) data;
                        ClassRoomDetailActivity.startActivity(getActivity(),mUserInfo,hc2.getId(),ClassRoomContants.TYPE_CUSTOM_RECORD,hc2.getSubjectName());//ClassRoomContants.FROM_WHERE_LINE ,
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 课堂回放
     */
    private void getRecommendSchedule() {
        HashMap<String, String> data = new HashMap<>();
        data.put("baseAreaId", baseAreaId);
        data.put("schoolId", schoolId);
        data.put("size", "8");
        data.put("uuid",mUserInfo.getUuid());

        requestData(URLConfig.GET_RECOMMEND_SCHEDULE, data,false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                HistoryClassParse hcp = new Gson().fromJson(response.toString(),HistoryClassParse.class);
                if(null != hcp ){
                    List<HistoryClass> hcList = hcp.getData();
                    if(null != hcList){
                        if(hcList.size()==0){
                            mData.add(new BaseTitleItemBar(Titles.sPagetitleSpeclassReplay,TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                        }else{
                            mData.add(new BaseTitleItemBar(Titles.sPagetitleSpeclassReplay,TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));

                            for (HistoryClass hc : hcList){
                                //判断--奇数/偶数
                                if(hcList.indexOf(hc) == 0){
                                    int hcount = hcList.size();
                                    if(hcount%2 >0){//奇数
                                        hc.setBaseViewHoldType(HistoryClassViewHolder.ITEM_TYPE_BIG_IN_LINE);
                                    }else{//偶数
                                        hc.setBaseViewHoldType(HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE);
                                    }
                                }else{
                                    hc.setBaseViewHoldType(HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE);
                                }
                                mData.add(hc);
                            }
                        }
                    }else{
                        mData.add(new BaseTitleItemBar(Titles.sPagetitleSpeclassReplay,TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
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
            public void onRequestFailure(VolleyError error) {
                onFailure(error);
            }
        });
    }

    @Override
    public void onConfigLoaded(ModuleConfig config) {
        baseAreaId = config.getBaseAreaId();
        schoolId = config.getSchoolId();
        mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConfigBus.unregister(this);
    }
}
