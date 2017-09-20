package com.codyy.erpsportal.groups.controllers.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.viewholders.TitleItemViewHolder;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleHorizonDivider;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.fragments.channels.BaseHttpFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolderBuilder;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.erpsportal.groups.controllers.activities.GroupSpaceActivity;
import com.codyy.erpsportal.groups.controllers.activities.MoreGroupListActivity;
import com.codyy.erpsportal.commons.controllers.fragments.filters.CategoryFilterFragment;
import com.codyy.erpsportal.groups.controllers.viewholders.ChannelGroupViewHolder;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.groups.models.entities.Group;
import com.codyy.erpsportal.groups.models.entities.GroupChannel;
import com.codyy.erpsportal.groups.models.entities.GroupList;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;

/**
 * 频道-圈组
 *
 * @author poe 2016/1/5
 */
public class GroupFragment extends BaseHttpFragment implements ConfigBus.OnModuleConfigListener {
    private final static String TAG = "GroupFragment";
    @Bind(R.id.empty_view)    EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)    RefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)    RecyclerView mRecyclerView;
    private List<Group> mGroupList = new ArrayList<>();
    private BaseRecyclerAdapter<Group, BaseRecyclerViewHolder<Group>> mAdapter;
    private String mBaseAreaId;
    private String mSchoolId;
    private UserInfo mUserInfo;

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_single_recycleview;
    }

    //  16-1-8  获取数据获取的api
    @Override
    public String obtainAPI() {
        return URLConfig.GET_HOME_GROUP;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        data.put("baseAreaId", mBaseAreaId);
        data.put("schoolId", mSchoolId);
        return data;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG, response.toString());
        if(null == mRecyclerView ) return;
        if(isRefreshing) mGroupList.clear();
        if (null !=mRefreshLayout&&mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }

        GroupChannel groupChannel = new Gson().fromJson(response.toString(), GroupChannel.class);
        GroupList groupList = groupChannel.getGroupList();
        if (null != groupList) {
            mGroupList.clear();
            //推荐圈组
            if (groupList.getRecommendedList() != null && groupList.getRecommendedList().size() > 0) {
                //标题需要动态配置，稍后需要使用Titles.获取！！！！
                mGroupList.add(new Group(Titles.sPagetitleGroupRecommend, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                for (Group group : groupList.getRecommendedList()) {
                    group.setBaseViewHoldType(ChannelGroupViewHolder.ITEM_TYPE_GROUP_RECOMMEND);
                    mGroupList.add(group);
                }
            } else {
                //add no more text desc item .
                mGroupList.add(new Group(Titles.sPagetitleGroupRecommend, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }

            if(groupList.hasTeam(Group.TYPE_TEACHING)){
                //教研组
                if (groupList.getTeachList() != null && groupList.getTeachList().size() > 0) {
                    mGroupList.add(new Group(Titles.sHomepageGroupTeaching, TitleItemViewHolder.ITEM_TYPE_TITLE_MORE));
                    for (Group group : groupList.getTeachList()) {
                        group.setBaseViewHoldType(ChannelGroupViewHolder.ITEM_TYPE_GROUP_TEACHING);
                        mGroupList.add(group);
                    }
                } else {
                    mGroupList.add(new Group(Titles.sHomepageGroupTeaching, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                }
            }

            if(groupList.hasTeam(Group.TYPE_INTEREST)){
                //兴趣组
                if (groupList.getInterestingList() != null && groupList.getInterestingList().size() > 0) {
                    mGroupList.add(new Group(Titles.sHomepageGroupInterest, TitleItemViewHolder.ITEM_TYPE_TITLE_MORE));
                    for (Group group : groupList.getInterestingList()) {
                        group.setBaseViewHoldType(ChannelGroupViewHolder.ITEM_TYPE_GROUP_INTEREST);
                        mGroupList.add(group);
                    }
                } else {
                    mGroupList.add(new Group(Titles.sHomepageGroupInterest, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                }
            }
        }

        mAdapter.setData(mGroupList);
        if (mGroupList.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(false);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(Throwable error) {
        if(null == mRecyclerView ) return;
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        if (mGroupList.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(false);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfigBus.register(this);
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
        mRecyclerView.addItemDecoration(new SimpleHorizonDivider(divider));
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(true);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder<Group>>() {
            @Override
            public BaseRecyclerViewHolder<Group> createViewHolder(ViewGroup parent, int viewType) {
                BaseRecyclerViewHolder viewHolder = null;
                switch (viewType) {
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE:
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA:
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE:
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA:
                        viewHolder = TitleItemViewHolderBuilder.getInstance().constructTitleItem(
                                parent.getContext(),parent,
                                TitleItemViewHolderBuilder.ITEM_TIPS_SIMPLE_TEXT);
                        break;
                    case ChannelGroupViewHolder.ITEM_TYPE_GROUP_RECOMMEND:
                    case ChannelGroupViewHolder.ITEM_TYPE_GROUP_INTEREST:
                    case ChannelGroupViewHolder.ITEM_TYPE_GROUP_TEACHING:
                        viewHolder = new ChannelGroupViewHolder(LayoutInflater.from(
                                parent.getContext()).inflate(R.layout.item_channel_group,
                                parent,false));
                        break;
                }
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
                return mGroupList.get(position).getBaseViewHoldType();
            }
        });

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<Group>() {
            @Override
            public void onItemClicked(View v, int position, Group data) {
                switch (mAdapter.getItemViewType(position)) {
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE:
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA:
                        if(v.getId() == R.id.btn_more){
                            MoreGroupListActivity.start(getActivity(), data.getBaseTitle(), Titles.sHomepageGroupInterest.equals(data.getBaseTitle()) ? Group.TYPE_INTEREST : Group.TYPE_TEACHING);
                        }
                        break;
                    case ChannelGroupViewHolder.ITEM_TYPE_GROUP_RECOMMEND:
                    case ChannelGroupViewHolder.ITEM_TYPE_GROUP_INTEREST:
                    case ChannelGroupViewHolder.ITEM_TYPE_GROUP_TEACHING:
                        GroupSpaceActivity.start(getActivity(), Titles.sHomepageGroup, data.getGroupId(), CategoryFilterFragment.CATEGORY_TYPE_GROUP);
                        break;
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onConfigLoaded(ModuleConfig config) {
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mBaseAreaId = config.getBaseAreaId();
        mSchoolId = config.getSchoolId();
        mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    @Override
    public void onDestroy() {
        ConfigBus.unregister(this);
        super.onDestroy();
    }
}
