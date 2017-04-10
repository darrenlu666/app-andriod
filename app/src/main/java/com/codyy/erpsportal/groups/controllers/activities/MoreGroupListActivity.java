package com.codyy.erpsportal.groups.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.filters.CategoryFilterFragment;
import com.codyy.erpsportal.commons.controllers.fragments.filters.GroupFilterFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ConfirmTextFilterListener;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleHorizonDivider;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleRecyclerView;
import com.codyy.erpsportal.groups.controllers.viewholders.ChannelGroupViewHolder;
import com.codyy.erpsportal.groups.models.entities.Group;
import com.codyy.erpsportal.groups.models.entities.GroupTeaching;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

/**
 * 教研圈组/兴趣圈组
 * Created by poe on 16-1-18.
 */
public class MoreGroupListActivity extends BaseHttpActivity {
    private final static String TAG = "MoreGroupListActivity";
    public final static String EXTRA_TYPE = "com.group.type";//
    public final static String EXTRA_TITLE = "com.group.title";//

    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.empty_view)EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    @Bind(R.id.drawer_layout)DrawerLayout mDrawerLayout;

    private List<Group> mGroupList = new ArrayList<>();
    private BaseRecyclerAdapter<Group,BaseRecyclerViewHolder<Group>> mAdapter ;
    private String mGroupType = Group.TYPE_TEACHING ;//default : TYPE_TEACHING
    private String mGrade ;
    private String mSubject ;
    private String mSemester ;
    private String mBaseAreaId ;
    private String mSchoolId ;
    private String mCategory ;//分类
    private boolean mIsDirectory = false;//是否为直属校 nodirectory /directory
    private GroupFilterFragment mFilterFragment ;

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_single_recycler_view;
    }

    @Override
    public String obtainAPI() {
        String url = null ;
        switch (mGroupType){
            case Group.TYPE_TEACHING:
                url = URLConfig.GET_HOME_GROUP_RESEARCH_LIST;
                break;
            case Group.TYPE_INTEREST:
                url = URLConfig.GET_HOME_GROUP_INTEREST_LIST;
                break;
        }
        return url;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if(null != mSemester) data.put("semester",mSemester);
        data.put("type",mIsDirectory?"directly":"nodirectly");
        if(null != mBaseAreaId) data.put("baseAreaId", mBaseAreaId);
        if(null != mSchoolId) data.put("schoolId", mSchoolId);
        if(null != mGrade)  data.put("grade",mGrade);
        if(null != mSubject) data.put("subject",mSubject);
        if(null != mCategory) data.put("category",mCategory);
        data.put("start",mGroupList.size()+"");
        data.put("end",(mGroupList.size()+sPageCount-1)+"");
        return data;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG , response.toString());
        if(null == mRecyclerView ) return;
        if(isRefreshing) mGroupList.clear();
        mRecyclerView.setRefreshing(false);
        mAdapter.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mEmptyView.setLoading(false);
        GroupTeaching  groupTeaching = new Gson().fromJson(response.toString(),GroupTeaching.class);
        if(null != groupTeaching) {
            if (groupTeaching.getDataList() != null && groupTeaching.getDataList().size() > 0) {
                for (Group group : groupTeaching.getDataList()) {
                    group.setBaseViewHoldType(ChannelGroupViewHolder.ITEM_TYPE_GROUP_RECOMMEND);
                    mGroupList.add(group);
                }
            }

            if(mGroupList.size() <  Integer.parseInt(groupTeaching.getTotal())){
                mAdapter.setRefreshing(true);
                mAdapter.setHasMoreData(true);
            }else{
                mAdapter.setHasMoreData(false);
            }
        }
        mAdapter.setData(mGroupList);

        if(mGroupList.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(VolleyError error) {
        if(null == mRecyclerView ) return;
        mRecyclerView.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        if(mGroupList.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    public void init() {
        mBaseAreaId = mUserInfo.getBaseAreaId();
        mSchoolId   =   mUserInfo.getSchoolId();
        mGroupType  =   getIntent().getStringExtra(EXTRA_TYPE);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        mTitleTextView.setText(title);
        initToolbar(mToolBar);
        UiMainUtils.setNavigationTintColor(this,R.color.main_green);

        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                requestData(true);
            }
        });
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        mRecyclerView.addItemDecoration(new SimpleHorizonDivider(divider));
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新
                mRecyclerView.setRefreshing(true);
                mAdapter.setHasMoreData(false);
                requestData(true);
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder<Group>>() {
            @Override
            public BaseRecyclerViewHolder<Group> createViewHolder(ViewGroup parent, int viewType) {
                return  new ChannelGroupViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_group,parent,false));
            }

            @Override
            public int getItemViewType(int position) {
                return mGroupList.get(position).getBaseViewHoldType();
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<Group>() {
            @Override
            public void onItemClicked(View v, int position, Group data) {
                GroupSpaceActivity.start(MoreGroupListActivity.this,"圈组",data.getGroupId(), CategoryFilterFragment.CATEGORY_TYPE_GROUP);
            }
        });
        mAdapter.setOnLoadMoreClickListener(new BaseRecyclerAdapter.OnLoadMoreClickListener() {
            @Override
            public void onMoreData() {
                requestData(false);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        this.enableLoadMore(mRecyclerView,false);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mFilterFragment = new GroupFilterFragment();
        Bundle bd = new Bundle();
        if(Group.TYPE_TEACHING.equals(mGroupType)){
            bd.putString(GroupFilterFragment.EXTRA_TYPE, GroupFilterFragment.TYPE_FILTER_TEACH);
        }else{
            bd.putString(GroupFilterFragment.EXTRA_TYPE, GroupFilterFragment.TYPE_FILTER_INTEREST);
        }
        mFilterFragment.setArguments(bd);
        ft.replace(R.id.fl_filter, mFilterFragment);
        ft.commit();

        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener(){
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
            }
        });
        /*this.setFilterListener(new IFilterListener() {
            @Override
            public void onFilterClick(MenuItem item) {
                if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                } else {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                    doFilterConfirmed();
                }
            }

            @Override
            public void onPreFilterCreate(Menu menu) {
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    menu.getItem(0).setActionView(R.layout.textview_filter_confirm_button);
                    TextView tv = (TextView) menu.getItem(0).getActionView().findViewById(R.id.tv_title);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
                            doFilterConfirmed();
                        }
                    });
                } else {
                    menu.getItem(0).setIcon(R.drawable.ic_filter);
                }
            }
        });*/
        setFilterListener(new ConfirmTextFilterListener(mDrawerLayout) {
            @Override
            protected void doFilterConfirmed() {
                doFilterConfirm();
            }
        });
    }

    private void doFilterConfirm() {
        Cog.i(TAG," doFilterConfirmed ~");
        Bundle bd = mFilterFragment.getFilterData();
        if(null != bd){
            if(!TextUtils.isEmpty(bd.getString("areaId"))){
                mBaseAreaId = bd.getString("areaId");
            }
            if(!TextUtils.isEmpty(bd.getString("directSchoolId"))){
                mSchoolId   =   bd.getString("directSchoolId");
            }
            mIsDirectory = bd.getBoolean("hasDirect");
            mSemester   =   bd.getString("semester");
            mGrade      =   bd.getString("class");
            mSubject    =   bd.getString("subject");
            mCategory   =   bd.getString("category");
        }
        Cog.i(TAG,"mBaseAreaId"+mBaseAreaId+" mIsDirectory"+mIsDirectory+" mSemester"+mSemester+" mSchoolId"+mSchoolId+" mGrade"+mGrade+" mSubject"+mSubject+" mCategory"+mCategory);
        //refresh .
        mGroupList.clear();
        requestData(true);
    }

    /**
     * @param context context
     * @param title title
     * @param type {@link Group#TYPE_TEACHING#TYPE_INTEREST}
     */
    public static void start(Context context , String title , String type){
        Intent intent = new Intent(context , MoreGroupListActivity.class);
        intent.putExtra(EXTRA_TITLE , title);
        intent.putExtra(EXTRA_TYPE , type);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }
}
