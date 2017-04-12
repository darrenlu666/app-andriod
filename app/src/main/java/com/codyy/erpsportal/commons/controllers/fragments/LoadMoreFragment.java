package com.codyy.erpsportal.commons.controllers.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.RecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.RecyclerAdapter.OnItemClickListener;
import com.codyy.erpsportal.commons.controllers.adapters.RecyclerAdapter.OnLoadMoreListener;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集成下拉刷新与上拉更多的碎片
 * Created by gujiajia on 2015/12/22.
 */
public abstract class LoadMoreFragment<T, VH extends RecyclerViewHolder<T>> extends Fragment implements OnRefreshListener, OnLoadMoreListener {

    private static final String TAG = "LoadMoreFragment";

    private static final int LOAD_COUNT = 10;

    /**
     * 最小加载间隔，加载太快看不到加载动画。
     */
    private final static long MIN_LOADING_INTERVAL = 500L;

    protected RecyclerView mRecyclerView;

    protected TextView mEmptyTv;

    private View mRootView;

    protected SwipeRefreshLayout mSwipeRefreshLayout;

    private RequestSender mRequestSender;

    protected RecyclerAdapter<T, VH> mAdapter;

    private int mStart;

    private Handler mHandler;

    /**
     * 是否立即加载，或手动触发
     */
    protected boolean mEager = true;

    /**
     * 参数
     */
    private Map<String, String> mParams;

    /**
     * 加载回调
     */
    private LoadedCallback mLoadedCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParams = new HashMap<>();
        mRequestSender = new RequestSender(this);
        mHandler = new Handler();
    }

    @Nullable
    @Override
    final public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_data_recycle, container, false);
            mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
            mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_layout);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.main_color);
            mEmptyTv = (TextView) mRootView.findViewById(R.id.tv_empty);
            mSwipeRefreshLayout.setOnRefreshListener(this);
            initEmptyTv();
            mRecyclerView.setLayoutManager(createLayoutManager());
            mAdapter = newRecyclerAdapter();
            mRecyclerView.setAdapter(mAdapter);
            extraInitViewsStyles();
            loadDataFirstly();
        }
        return mRootView;
    }

    /**
     * 需要特殊的风格的话RecyclerView在这里设置
     */
    protected void extraInitViewsStyles() {
    }

    protected LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    /**
     * 创建适配器
     *
     * @return
     */
    protected RecyclerAdapter<T, VH> newRecyclerAdapter() {
        return new RecyclerAdapter<>(mRecyclerView, this, newViewHolderCreator());
    }

    /**
     * 创建RecyclerViewHolder创建器
     *
     * @return
     */
    protected abstract ViewHolderCreator<VH> newViewHolderCreator();

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void onLoadMore() {
        loadData(false);
    }

    /**
     * 初始化空提示
     */
    private void initEmptyTv() {
        ClickableSpan mMyClickableSpan = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                Cog.d(TAG, "updateDrawState");
                ds.setColor(getResources().getColor(R.color.green));
            }

            @Override
            public void onClick(View widget) {
                Cog.d(TAG, "onEmptyViewClick");
                loadData(true);
            }
        };

        SpannableString spStr = new SpannableString(getContext().getString(R.string.no_data_for_now));
        spStr.setSpan(mMyClickableSpan, 0, spStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mEmptyTv.setText(spStr);
        mEmptyTv.setHighlightColor(Color.TRANSPARENT);
        mEmptyTv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 首次加载数据
     */
    private void loadDataFirstly() {
        if (mEager) loadData(true);
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    /**
     * 加载数据
     *
     * @param params  筛选参数
     * @param refresh true刷新，false加载更多
     */
    public void loadData(Map<String, String> params, final boolean refresh) {
        if (refresh) {
            mStart = 0;
        }
        int start = mStart;
        int end = start + obtainLoadCount();
        params.put("start", "" + start);
        params.put("end", "" + (end - 1));

        addParams(params);
        final long startTime = SystemClock.currentThreadTimeMillis();
        Cog.d(TAG, "loadData:", getUrl(), params);
        if (refresh) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }
        mRequestSender.sendRequest(new RequestData(getUrl(), params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                Cog.d(TAG, "onResponse:" + response);
                                delayResponding(startTime, new ResponseCallable() {
                                    @Override
                                    public void handle() {
                                        handleNormalResponse(response, refresh);
                                    }
                                });
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final Throwable error) {
                        Cog.e(TAG, "onErrorResponse:" + error);
                        delayResponding(startTime, new ResponseCallable() {
                            @Override
                            public void handle() {
                                handleErrorResponse(error, refresh);
                            }
                        });
                    }
                }, 1
                )
        );
    }

    public int obtainLoadCount() {
        return LOAD_COUNT;
    }

    /**
     * 延迟响应，为了有足够时间显示加载动画。
     *
     * @param startTime 请求开始时间
     * @param callable  请求响应回调
     */
    private void delayResponding(long startTime, final ResponseCallable callable) {
        long interval = SystemClock.currentThreadTimeMillis() - startTime;
        if (interval > MIN_LOADING_INTERVAL) {
            callable.handle();
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    callable.handle();
                }
            }, MIN_LOADING_INTERVAL - interval);
        }
    }

    /**
     * 响应回调
     */
    interface ResponseCallable {
        void handle();
    }

    /**
     * 处理请求响应
     *
     * @param response 响应
     * @param isRefreshing true刷新
     */
    private void handleNormalResponse(JSONObject response, boolean isRefreshing) {
        mAdapter.setLoading(false);
        mSwipeRefreshLayout.setRefreshing(false);
        if (checkSuccessful(response)) {
            List<T> list = getList(response);
            int loadedFrag = 0;//是否成功加载1:成功刷新2：成功加载更多
            if (list == null || list.size() == 0) {
                if (isRefreshing) {
                    handleEmpty();//
                } else {
                    mAdapter.removeItem(mAdapter.getItemCount() - 1);
                }
            } else {
                if (isRefreshing) {
                    mEmptyTv.setVisibility(View.GONE);
                    mAdapter.setData(list);
                    mAdapter.notifyItemRangeInserted(0, list.size());
                    loadedFrag = 1;
                } else {
                    mAdapter.removeItem(mAdapter.getItemCount() - 1);
                    mAdapter.addData(list);
                    mAdapter.notifyDataSetChanged();
                    loadedFrag = 2;
                }
            }
            //如果已经加载所有，下拉更多关闭
            if (checkHasMore(response, mAdapter.getItemCount())) {
                mAdapter.disableLoadMore();
            } else {
                mAdapter.enableLoadMore();
            }
            mAdapter.notifyDataSetChanged();
            mStart = mAdapter.getItemCount();
            if (loadedFrag == 2) {//成功加载更多
                onRefreshSuccess();
            } else {//成功刷新
                onLoadMoreSuccess();
            }
        } else {
            if (isRefreshing) {
                handleEmpty();
            }
        }
    }

    public void setLoadedCallback(LoadedCallback loadedCallback) {
        mLoadedCallback = loadedCallback;
    }

    /**
     * 刷新成功回调方法
     */
    protected void onRefreshSuccess() {
        if (mLoadedCallback != null) mLoadedCallback.onRefreshSuccess();
    }

    /**
     * 加载更多成功回调方法
     */
    protected void onLoadMoreSuccess() {
        if (mLoadedCallback != null) mLoadedCallback.onLoadMoreSuccess();
    }

    /**
     * 检查请求是否成功，默认检查result字段值是否为success
     *
     * @param response 响应
     * @return 响应是否成功
     */
    protected boolean checkSuccessful(JSONObject response) {
        return "success".equals(response.optString("result"));
    }

    /**
     * 检查是否有更多，默认方式是比较total字段与当前项数
     *
     * @param response
     * @param itemCount 已有item数量
     * @return
     */
    protected boolean checkHasMore(JSONObject response, int itemCount) {
        return response.optInt("total") <= itemCount;
    }

    /**
     * 处理空情况
     */
    protected void handleEmpty() {
        mAdapter.setData(null);
        mAdapter.notifyDataSetChanged();
        mEmptyTv.setVisibility(View.VISIBLE);
    }

    /**
     * 获取请求地址
     *
     * @return
     */
    protected abstract String getUrl();

    /**
     * 获取数据列表
     *
     * @param response 响应数据
     * @return 数据列表
     */
    protected abstract List<T> getList(JSONObject response);

    /**
     * 处理错误响应
     *  @param error   错误信息
     * @param refresh 是否是刷新
     */
    private void handleErrorResponse(Throwable error, boolean refresh) {
        if (!refresh) {
            mAdapter.removeItem(mAdapter.getItemCount() - 1);
            mAdapter.notifyItemRemoved(mAdapter.getItemCount());
        } else {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
        mAdapter.setLoading(false);
        if (mAdapter.isEmpty()) {
            mEmptyTv.setVisibility(View.VISIBLE);
        }

        UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
    }

    /**
     * 添加请求参数，也可使用 {@link #addParam(String, String)}加参数,这个用来覆盖，不是用来调用了
     *
     * @param params 请求参数，加入这个map的参数会用来作为http请求参数
     */
    protected void addParams(Map<String, String> params) {
    }

    public void addParam(String key, String value) {
        mParams.put(key, value);
    }

    /**
     * 更新请求参数,如果传入参数中有对应的的值则设置新值，反之则清除参数。
     *
     * @param params
     * @param key
     * @see #updateParamsBaseOnMap(Map, String, String)
     */
    public void updateParamsBaseOnMap(Map<String, String> params, String key) {
        updateParamsBaseOnMap(params, key, key);
    }

    /**
     * 更新请求参数,如果传入参数中有对应的的值则设置新值，反之则清除参数。
     *
     * @param params  新参数
     * @param fromKey 对应在params中的key
     * @param toKey   要设置的参数的key
     */
    public void updateParamsBaseOnMap(Map<String, String> params, String fromKey, String toKey) {
        if (params.containsKey(fromKey)) {
            addParam(toKey, params.get(fromKey));
        } else {
            removeParam(toKey);
        }
    }

    public void removeParam(String key) {
        mParams.remove(key);
    }

    public void addMapToParam(Map<String, String> newParams) {
        mParams.putAll(newParams);
    }

    public void loadData(boolean refresh) {
        loadData(mParams, refresh);
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        mAdapter.setOnItemClickListener(onItemClickListener);
    }

    /**
     * 加载完成回调
     */
    public interface LoadedCallback{
        /**
         * 刷新成功回调方法
         */
        void onRefreshSuccess();

        /**
         * 加载更多成功回调方法
         */
        void onLoadMoreSuccess();
    }
}
