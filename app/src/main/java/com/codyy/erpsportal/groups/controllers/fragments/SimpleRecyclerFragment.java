package com.codyy.erpsportal.groups.controllers.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.channels.BaseHttpFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleHorizonDivider;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleRecyclerView;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
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
    /**
     * 单页最大数据请求
     */
    public static final int sPageCount = 10 ;
    @Bind(R.id.empty_view) EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)RefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    public List<T> mDataList = new ArrayList<>();
    protected BaseRecyclerAdapter<T,BaseRecyclerViewHolder<T>> mAdapter ;
    public int mTotal = 0;//总数据量
    public int mCurrentPageIndex = 1 ;//当前页码

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_single_recycleview;
    }

    @Override
    public String obtainAPI() {
        return getAPI();
    }

    public abstract  String getAPI();

    public abstract HashMap<String, String> getParams() ;

    // TODO: 16-1-20 解析服务器返回的数据，可能是刷新，也可能是更多 
    public abstract void parseData(JSONObject response);

    // TODO: 16-1-20 每个UI需要实现的item布局部分 
    public abstract BaseRecyclerAdapter.ViewCreator getViewCreator();

    // TODO: 16-7-22 设置监听器
    public abstract void setOnClickListener();

    // TODO: 16-2-16 获取最大上限
    public abstract int getTotal();

    // TODO: 16-1-20 请求数据传递的参数
    @Override
    public HashMap<String, String> getParam() {
        return getParams();
    }

    @Override
    public void onSuccess(JSONObject response) {
        Cog.d(TAG , response.toString());
        if(null == mRecyclerView || null == mRefreshLayout) return;
        mRecyclerView.setRefreshing(false);
        mAdapter.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mEmptyView.setLoading(false);
        parseData(response);
        mAdapter.setData(mDataList);
        //load more ...
        if(mDataList.size() <  getTotal()){
            mAdapter.setRefreshing(true);
            mAdapter.setHasMoreData(true);
        }else{
            mAdapter.setHasMoreData(false);
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
    public void onFailure(VolleyError error) {
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                mEmptyView.setLoading(true);
                requestData();
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
        mAdapter = new BaseRecyclerAdapter<>(getViewCreator());
        mAdapter.setOnLoadMoreClickListener(new BaseRecyclerAdapter.OnLoadMoreClickListener() {
            @Override
            public void onMoreData() {
                requestData();
            }
        });
        setOnClickListener();
        mRecyclerView.setAdapter(mAdapter);
        this.enableLoadMore(mRecyclerView);

    }

    /**
     * 下拉刷新
     */
    public void refresh() {
        if(null == mRecyclerView ) return;
        mRecyclerView.setRefreshing(true);
        mAdapter.setHasMoreData(false);
        mDataList.clear();
        requestData();
    }

}
