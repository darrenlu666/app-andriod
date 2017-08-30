package com.codyy.erpsportal.commons.controllers.fragments.channels;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleBisectDivider;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.viewholders.TitleItemViewHolder;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.classroom.activity.ClassRoomDetailActivity;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolderBuilder;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.HistoryClassViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.HistoryRecommendViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.LivingRecordViewHolder;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.customized.HistoryClass;
import com.codyy.erpsportal.commons.models.entities.customized.HistoryClassParse;
import com.codyy.erpsportal.commons.models.entities.customized.LivingRecordLesson;
import com.codyy.erpsportal.commons.models.entities.customized.LivingRecordParse;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;

/**
 * 名校网络课堂(直录播课堂)
 * Created by poe on 2016/6/2.
 */
public class ChannelLivingFragment extends BaseHttpFragment implements ConfigBus.OnModuleConfigListener {
    private final String TAG = "ChannelLivingFragment";
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
        return URLConfig.GET_INDEX_LIVE_APPOINTMENT;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        data.put("baseAreaId", baseAreaId);
        data.put("size", "4");
        data.put("schoolId", schoolId);
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        return data;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        if(null == mRecyclerView ) return;
        if(isRefreshing) mData.clear();
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mEmptyView.setLoading(false);
        mRecyclerView.setEnabled(true);
        LivingRecordParse lrp = new Gson().fromJson(response.toString(),LivingRecordParse.class);
        if(null != lrp) {
            mData.clear();
            //1.living
            List<LivingRecordLesson> liveList = lrp.getData();
            if (null != liveList) {
                if (liveList.size() == 0) {
                    mData.add(new BaseTitleItemBar(Titles.sPagetitleNetclassLive, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                } else {
                    mData.add(new BaseTitleItemBar(Titles.sPagetitleNetclassLive, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                    for (LivingRecordLesson lr : liveList) {
                        lr.setBaseViewHoldType(LivingRecordViewHolder.ITEM_TYPE_LIVING);
                        mData.add(lr);
                    }
                }
            } else {
                mData.add(new BaseTitleItemBar(Titles.sPagetitleNetclassLive, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }
        }
        mAdapter.setData(mData);
        mAdapter.notifyDataSetChanged();
        if (mData.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
        getRecommendLesson();
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
                requestData(false);
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
                    case LivingRecordViewHolder.ITEM_TYPE_LIVING:
                        viewHolder =  new LivingRecordViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(
                                parent.getContext(), R.layout.item_channel_live_record));
                        break;
                    case HistoryClassViewHolder.ITEM_TYPE_BIG_IN_LINE://单行填充
                        viewHolder =  new HistoryRecommendViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(
                                parent.getContext(), R.layout.item_customized_history_class));
                        break;
                    case HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE://多行
                        viewHolder =  new HistoryRecommendViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(
                                parent.getContext(), R.layout.item_customized_history_class));
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
                    case LivingRecordViewHolder.ITEM_TYPE_LIVING:
                        LivingRecordLesson lc = (LivingRecordLesson) data;
                        ClassRoomDetailActivity.startActivity(getActivity(),mUserInfo,lc.getId(),ClassRoomContants.TYPE_LIVE_LIVE,lc.getSubjectName());//ClassRoomContants.FROM_WHERE_LINE ,
                        break;
                    case HistoryClassViewHolder.ITEM_TYPE_BIG_IN_LINE://单行填充
                        HistoryClass hc = (HistoryClass) data;
                        ClassRoomDetailActivity.startActivity(getActivity(),mUserInfo,hc.getId(),ClassRoomContants.TYPE_LIVE_RECORD,hc.getSubjectName());// ClassRoomContants.FROM_WHERE_LINE ,
                        break;
                    case HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE://多行
                        HistoryClass lrc = (HistoryClass) data;
                        ClassRoomDetailActivity.startActivity(getActivity(),mUserInfo,lrc.getId(),ClassRoomContants.TYPE_LIVE_RECORD,lrc.getSubjectName());//ClassRoomContants.FROM_WHERE_LINE ,
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
        mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    /**
     * 获取推荐课堂
     */
    private void getRecommendLesson() {
        HashMap<String, String> data = new HashMap<>();
        data.put("baseAreaId", baseAreaId);
        data.put("size", "7");
        data.put("schoolId", schoolId);
        data.put("uuid",mUserInfo.getUuid());
        requestData(URLConfig.GET_INDEX_LIVE_APPOINTMENT_RECOMMEND, data,false, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                HistoryClassParse hcp = new Gson().fromJson(response.toString(),HistoryClassParse.class);
                if(null != hcp ) {
                    List<HistoryClass> hcList = hcp.getData();
                    if (null != hcList) {
                        if (hcList.size() == 0) {
                            mData.add(new BaseTitleItemBar(Titles.sPagetitleNetclassRecomcourse, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                        } else {
                            mData.add(new BaseTitleItemBar(Titles.sPagetitleNetclassRecomcourse, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));

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
                        mData.add(new BaseTitleItemBar(Titles.sPagetitleNetclassRecomcourse, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                    }
                }

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
