package com.codyy.erpsportal.groups.controllers.activities;

import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.interfaces.SimpleRecyclerDelegate;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleHorizonDivider;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleRecyclerView;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;

/**
 * １．实现一个纯粹列表的页面
 * 2. 带有ToolBar和返回键
 * 3. 拥有自由切换filter功能
 * 4. 可实现下拉刷新
 * 5. 能够自动加载更多
 * 6. 是否拥有加载更多可以关闭/打开控制.
 * Created by poe on 3/6/17.
 */
public abstract class SimpleRecyclerActivity<T extends BaseTitleItemBar> extends BaseHttpActivity {
    private final static String TAG = "SimpleRecyclerActivity";
    /**     * 单页最大数据请求    */
    public static final int sPageCount = 10 ;
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.empty_view)  EmptyView mEmptyView;
    @Bind(R.id.drawer_layout)  DrawerLayout mDrawerLayout;
    @Bind(R.id.refresh_layout)RefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    public List<T> mDataList = new ArrayList<>();
    protected BaseRecyclerAdapter<T,BaseRecyclerViewHolder<T>> mAdapter ;
    /**获取参数配置　before {#init()} 可进行UI初始化**/
    public abstract  void preInitArguments();
    public abstract SimpleRecyclerDelegate<T> getSimpleRecyclerDelegate();

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_single_recycler_view;
    }

    @Override
    public String obtainAPI() {
        if(getSimpleRecyclerDelegate() == null){
            throw new IllegalAccessError("method {@link　SimpleRecyclerDelegate} has not implemented !");
        }
        return getSimpleRecyclerDelegate().obtainAPI();
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        if(getSimpleRecyclerDelegate() == null){
            throw new IllegalAccessError("method {@link#SimpleRecyclerDelegate} is not implemented !");
        }
        return getSimpleRecyclerDelegate().getParams(isRefreshing);
    }

    /** 设置标题 **/
    public void setTitle(String title) {
        if(null != mTitleTextView && !TextUtils.isEmpty(title)){
            mTitleTextView.setText(title);
        }
    }

    @Override
    public void init() {
        if(getSimpleRecyclerDelegate() == null){
            throw new IllegalAccessError("method {@link#SimpleRecyclerDelegate} is not implemented !");
        }
        preInitArguments();
        initToolbar(mToolBar);
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
                refresh();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder<T>>() {
            @Override
            public BaseRecyclerViewHolder<T> createViewHolder(ViewGroup parent, int viewType) {
                BaseRecyclerViewHolder viewHolder = getSimpleRecyclerDelegate().getViewHolder(parent,viewType);
                if(null == viewHolder) throw new IllegalArgumentException("ViewHolder should not be NULL !");
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
                return mDataList.get(position).getBaseViewHoldType();
            }
        });
        mAdapter.setOnLoadMoreClickListener(new BaseRecyclerAdapter.OnLoadMoreClickListener() {
            @Override
            public void onMoreData() {
                    requestData(false);
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<T>() {
            @Override
            public void onItemClicked(View v, int position, T data) {
                if(getSimpleRecyclerDelegate() == null){
                    throw new IllegalAccessError("method {@link#SimpleRecyclerDelegate} is not implemented !");
                }
                getSimpleRecyclerDelegate().OnItemClicked(v,position,data);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        this.enableLoadMore(mRecyclerView,false);
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
    }

    @Override
    public void onSuccess(JSONObject response, boolean isRefreshing) throws Exception {
        Cog.d(TAG , response.toString());
        if(getSimpleRecyclerDelegate() == null){
            throw new IllegalAccessError("method {@link#SimpleRecyclerDelegate} is not implemented !");
        }
        if(null == mRecyclerView || null == mRefreshLayout) return;
        if(isRefreshing) mDataList.clear();
        mRecyclerView.setRefreshing(false);
        mAdapter.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mEmptyView.setLoading(false);
        getSimpleRecyclerDelegate().parseData(response,isRefreshing);
        mAdapter.setData(mDataList);
        //load more ...
        Cog.i(TAG," data size : "+mDataList.size()+" total: "+getSimpleRecyclerDelegate().getTotal());
        if(mDataList.size() <  getSimpleRecyclerDelegate().getTotal()){
            mAdapter.setRefreshing(true);
            mAdapter.setHasMoreData(true);
        }else{
            mAdapter.setHasMoreData(false);
            notifyLoadCompleted();
        }
        mAdapter.notifyDataSetChanged();

        if(mDataList.size()<=0){
            mEmptyView.setLoading(false);
            mEmptyView.setVisibility(View.VISIBLE);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(Throwable error) throws Exception {
        if(null == mRecyclerView || null == mRefreshLayout) return;
        mRecyclerView.setRefreshing(false);
//        mAdapter.setHasMoreData(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }

        mAdapter.notifyDataSetChanged();

        if(mDataList.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(false);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
    }
    /**
     * 下拉刷新
     */
    public void refresh() {
        if(null == mRecyclerView ) return;
        if(null != mRefreshLayout) mRefreshLayout.setRefreshing(true);
        mRecyclerView.setRefreshing(true);
        mAdapter.setHasMoreData(false);
        requestData(true);
    }

    /**
     * 多个recyclerView状态切换，init page index .
     */
    public void changeTabClear(){
        mDataList.clear();
        mAdapter.setRefreshing(false);
        mAdapter.setHasMoreData(false);
        setCurrentPageIndex(1);
    }

    public void initData(){
        if(null != mRefreshLayout) mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    public void setEmptyText(int tips,int color){
        if(null != mEmptyView) mEmptyView.setText(tips,color);
    }
}
