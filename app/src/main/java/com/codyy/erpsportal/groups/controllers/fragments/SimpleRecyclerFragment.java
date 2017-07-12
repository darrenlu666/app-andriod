package com.codyy.erpsportal.groups.controllers.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.channels.BaseHttpFragment;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.interfaces.SimpleRecyclerDelegate;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleHorizonDivider;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

/**
 * 适应-单个RecyclerView的Fragment场景
 * 1.下拉刷新
 * 2.为空判断EmptyView
 * Created by poe on 16-1-20.
 */
public abstract class SimpleRecyclerFragment<T> extends BaseHttpFragment {
    private final static String TAG = "SimpleRecyclerFragment";
    /**     * 单页最大数据请求    */
    public static final int sPageCount = 10 ;
    @Bind(R.id.empty_view) EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)RefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    public List<T> mDataList = new ArrayList<>();
    protected BaseRecyclerAdapter<T,BaseRecyclerViewHolder<T>> mAdapter ;
    public int mTotal = 0;//总数据量
    public abstract SimpleRecyclerDelegate<T> getSimpleRecyclerDelegate();

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_single_recycleview;
    }

    @Override
    public String obtainAPI() {
        if(getSimpleRecyclerDelegate() == null){
            throw new IllegalAccessError("method {@link#SimpleRecyclerDelegate} is not implemented !");
        }
        return getSimpleRecyclerDelegate().obtainAPI();
    }

    /**请求数据传递的参数**/
    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        if(getSimpleRecyclerDelegate() == null){
            throw new IllegalAccessError("method {@link#SimpleRecyclerDelegate} is not implemented !");
        }
        return getSimpleRecyclerDelegate().getParams(isRefreshing);
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
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
    public void onFailure(Throwable error) {
        if(null == mRecyclerView || null == mRefreshLayout) return;
        mRecyclerView.setRefreshing(false);
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

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getSimpleRecyclerDelegate() == null){
            throw new IllegalAccessError("method {@link#SimpleRecyclerDelegate} is not implemented !");
        }
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                mEmptyView.setLoading(true);
                Cog.i(TAG," EmptyView () requestData ~");
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder<T>>() {
            @Override
            public BaseRecyclerViewHolder<T> createViewHolder(ViewGroup parent, int viewType) {
                return getSimpleRecyclerDelegate().getViewHolder(parent,viewType);
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Cog.i(TAG,"onActivityCreated ~ ");
    }

    /** * 初始化数据 */
    public void initData(){
        if(null != mRefreshLayout) mRefreshLayout.setRefreshing(true);
        Cog.i(TAG," initData () requestData ~");
        requestData(true);
    }
    /**
     * 下拉刷新
     */
    public void refresh() {
        Cog.i(TAG," refresh () ~");
        if(null == mRecyclerView ) return;
        if(null != mRefreshLayout) mRefreshLayout.setRefreshing(true);
        mRecyclerView.setRefreshing(true);
        mAdapter.setHasMoreData(false);
        Cog.i(TAG," refresh () requestData ~");
        requestData(true);
    }
}
