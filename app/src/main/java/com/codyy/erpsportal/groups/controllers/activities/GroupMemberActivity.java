package com.codyy.erpsportal.groups.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.GroupMemberTitleViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.GroupMemberViewHolder;
import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;
import com.codyy.erpsportal.groups.models.entities.BaseModuleParse;
import com.codyy.erpsportal.groups.models.entities.GroupMember;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleRecyclerView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

/**
 * 圈组-成员
 * Created by poe on 16-3-2.
 */
public class GroupMemberActivity extends BaseHttpActivity {
    private static  final String TAG = "GroupMemberActivity";
    private static String EXTRA_ID = "groupId";
    private static String EXTRA_MAX_COUNT = "count";
    /**
     * 圈主、管理员类型
     */
    private static String TYPE_REQUEST_MANAGER = "MANAGER";
    /**
     * 普通成员类型
     */
    private static String TYPE_REQUEST_MEMBER = "MEMBER";
    /**
     * 标题
     */
    private static final int ITEM_TYPE_TITLE = 0x110;
    /**
     * 管理员 or 成员
     */
    private static final int ITEM_TYPE_MEMBER = 0x120;

    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.empty_view)EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    private String mGroupId;//传递值-班级id
    private List<BaseTitleItemBar> mDataList = new ArrayList<>();
    private BaseRecyclerAdapter<BaseTitleItemBar,BaseRecyclerViewHolder<BaseTitleItemBar>> mAdapter ;
    private int mMaxCount = 10;

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_single_recycler_view;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_GROUP_SPACE_MEMBER;
    }

    @Override
    public HashMap<String, String> getParam() {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        data.put("groupId",mGroupId);
        data.put("type",TYPE_REQUEST_MANAGER);
        //此处比较特殊，只会从0开始否则#mDataList.size()->start ...
        data.put("start","0");
        data.put("end",mMaxCount+"");
        return data;
    }

    @Override
    public void init() {
        mGroupId    =   getIntent().getStringExtra(EXTRA_ID);
        int count   =   getIntent().getIntExtra(EXTRA_MAX_COUNT,0);
        if(mMaxCount < count){
            mMaxCount   =   count ;
        }
        mTitleTextView.setText(getString(R.string.members));
        initToolbar(mToolBar);

        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                mEmptyView.setLoading(false);
                requestData();
            }
        });
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新
                refresh();
            }
        });

        GridLayoutManager manger = new GridLayoutManager(this,4);
        manger.setSpanSizeLookup(new GroupSpanSizeLookup(mDataList));
        mRecyclerView.setLayoutManager(manger);
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder<BaseTitleItemBar>>() {
            @Override
            public BaseRecyclerViewHolder createViewHolder(ViewGroup parent, int viewType) {
                BaseRecyclerViewHolder viewHolder = null;
                switch (viewType){
                    case ITEM_TYPE_TITLE:
                        viewHolder  = new GroupMemberTitleViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(
                                parent.getContext(),R.layout.item_simple_black_text));
                        break;
                    case ITEM_TYPE_MEMBER:
                        viewHolder = new GroupMemberViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_group_memeber,parent,false));
                        break;
                }
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
                return mDataList.get(position).getBaseViewHoldType();
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<BaseTitleItemBar>() {
            @Override
            public void onItemClicked(View v, int position, BaseTitleItemBar data) {
                switch (mAdapter.getItemViewType(position)){
                    case ITEM_TYPE_MEMBER:

                        break;
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        requestData();
    }

    @Override
    public void onSuccess(JSONObject response) {
        Cog.d(TAG , response.toString());
        if(null == mRecyclerView ) return;
        mRecyclerView.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mEmptyView.setLoading(false);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                requestData();
            }
        });

        List<GroupMember> memberList = getGroupMembers(response);

        if(null != memberList ){
            mDataList.clear();
            mDataList.add(new BaseTitleItemBar("创建人和组长",ITEM_TYPE_TITLE));
            mDataList.addAll(memberList);
        }

        mAdapter.setData(mDataList);
        if(mDataList.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
            mAdapter.setHasMoreData(false);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }

        //get the members ...
        loadMoreData();
    }

    /**
     * 根据返回的json 解析成成员集合
     * @param response
     * @return List<GroupMember>
     */
    @NonNull
    private List<GroupMember> getGroupMembers(JSONObject response) {
        List<GroupMember> memberList = new ArrayList<>();
        BaseModuleParse parse = new Gson().fromJson(response.toString(),BaseModuleParse.class);
        if(null != parse && parse.getList() != null){
            List<GroupMember> groupMemberList = parse.getList().getDataList();
            if(null != groupMemberList && groupMemberList.size() >0 ){
                for(GroupMember member :groupMemberList){
                    member.setBaseViewHoldType(ITEM_TYPE_MEMBER);
                    memberList.add(member);
                }
            }
        }
        return memberList;
    }

    @Override
    public void onFailure(VolleyError error) {
        if(null == mRecyclerView ) return;
        mRecyclerView.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        if(mDataList.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
    }
    //刷新数据
    private void refresh() {
        mDataList.clear();
        mRecyclerView.setRefreshing(true);
        mAdapter.setHasMoreData(false);
        requestData();
    }

    /**
     * 加载更多博文
     */
    private void loadMoreData() {
        if(null == mSender) return;
        HashMap hashMap = getParam();
        hashMap.put("type",TYPE_REQUEST_MEMBER);
        requestData(obtainAPI(), hashMap, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                Cog.d(TAG , response.toString());
//                onSuccess(response);
                List<GroupMember> memberList = getGroupMembers(response);
                if(null != memberList ){
                    mDataList.add(new BaseTitleItemBar("普通成员",ITEM_TYPE_TITLE));
                    mDataList.addAll(memberList);
                }

                mAdapter.setData(mDataList);
                if(mDataList.size()<=0){
                    mEmptyView.setVisibility(View.VISIBLE);
                    mAdapter.setHasMoreData(false);
                }else {
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRequestFailure(VolleyError error) {
            }
        });

    }

    public class GroupSpanSizeLookup extends GridLayoutManager.SpanSizeLookup{

        private List<BaseTitleItemBar> mList ;

        public GroupSpanSizeLookup(List<BaseTitleItemBar> list) {
            this.mList = list;
        }

        @Override
        public int getSpanSize(int position) {
            int widthCount = 4;
            if(position < mList.size()){
                switch (mList.get(position).getBaseViewHoldType()){
                    case ITEM_TYPE_TITLE:
                        widthCount =  4;
                        break;
                    case ITEM_TYPE_MEMBER:
                        widthCount =  1;
                        break;
                }
            }
            return widthCount;
        }
    }

    public static void start(Context from , String groupID , int memberCount){
        Intent intent = new Intent(from,GroupMemberActivity.class);
        intent.putExtra(EXTRA_ID , groupID);
        intent.putExtra(EXTRA_MAX_COUNT , memberCount);
        from.startActivity(intent);
        UIUtils.addEnterAnim((Activity) from);
    }
}
