package com.codyy.erpsportal.commons.controllers.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.View;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.RecyclerAdapter.OnItemClickListener;
import com.codyy.erpsportal.commons.controllers.adapters.RecyclerAdapter.OnLoadMoreListener;
import com.codyy.erpsportal.commons.controllers.adapters.RecyclerCommonAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecyclerView数据加载器
 * Created by gujiajia on 2016/7/12.
 */
public class RvLoader<T, VH extends RecyclerViewHolder<T>, INFO> implements OnRefreshListener, OnLoadMoreListener {
    private static final String TAG = "RvLoader";
    private static final int LOAD_COUNT = 10;
    /**
     * 最小加载间隔，加载太快看不到加载动画。
     */
    private final static long MIN_LOADING_INTERVAL = 500L;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private SwipeRefreshLayout mRefreshLayout;
    private RequestSender mRequestSender;
    protected RecyclerCommonAdapter<T, VH, INFO> mAdapter;
    private int mStart;
    private Handler mHandler;
    private Map<String, String> mParams;
    private ListExtractor<T, VH> mListExtractor;

    /**
     * 项与项间的分隔线
     */
    private ItemDecoration mItemDecoration;

    private RvLoader() {
    }

    private void init(Object object) {
        Context context;
        if (object instanceof Activity) {
            mRequestSender = new RequestSender((Activity) object);
            context = (Activity) object;
        } else if (object instanceof Fragment) {
            mRequestSender = new RequestSender((Fragment) object);
            context = ((Fragment) object).getContext();
        } else {
            throw new RuntimeException("Please pass Activity or Fragment!");
        }
        mHandler = new Handler();
        mParams = new HashMap<>();
        if (object instanceof ListExtractor) {
            this.mListExtractor = (ListExtractor<T, VH>) object;
        } else {
            throw new RuntimeException("The object passed has to implement ListExtractor!");
        }
        mItemDecoration = new DividerItemDecoration(context);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = newRecyclerAdapter();
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    public void setEmptyView(View view) {
        this.mEmptyView = view;
    }

    public void setRefreshLayout(SwipeRefreshLayout refreshLayout) {
        this.mRefreshLayout = refreshLayout;
        this.mRefreshLayout.setOnRefreshListener(this);
    }

    public void setInfo(INFO info) {
        mAdapter.setInfo(info);
    }

    public void notifyInfoChanged() {
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 创建适配器
     *
     * @return
     */
    protected RecyclerCommonAdapter<T, VH, INFO> newRecyclerAdapter() {
        return new RecyclerCommonAdapter<>(mRecyclerView, this, newViewHolderCreator());
    }

    /**
     * 创建RecyclerViewHolder创建器
     *
     * @return
     */
    protected ViewHolderCreator<VH> newViewHolderCreator() {
        if (mListExtractor == null) return null;
        return mListExtractor.newViewHolderCreator();
    }

    /**
     * 加载数据
     *
     * @param refresh true刷新，false加载更多
     */
    public void loadData(final boolean refresh) {
        if (refresh) {
            mStart = 0;
        }
        int start = mStart;
        int end = start + LOAD_COUNT;
        mParams.put("start", "" + start);
        mParams.put("end", "" + (end - 1));

        final long startTime = SystemClock.currentThreadTimeMillis();
        Cog.d(TAG, "loadData:", getUrl(), mParams);
        if (refresh) {
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(true);
                }
            });
        }
        mRequestSender.sendRequest(new RequestData(getUrl(), mParams,
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
                },
                new Response.ErrorListener() {
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
                }));
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

    public void showDivider() {
        mRecyclerView.addItemDecoration(mItemDecoration);
    }

    public void hideDivider() {
        mRecyclerView.removeItemDecoration(mItemDecoration);
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        mAdapter.setOnItemClickListener(onItemClickListener);
    }

    /**
     * 清除数据
     */
    public void clearData() {
        mAdapter.clear();
        mEmptyView.setVisibility(View.VISIBLE);
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
     * @param response
     * @param isRefreshing
     */
    private void handleNormalResponse(JSONObject response, boolean isRefreshing) {
        mAdapter.setLoading(false);
        mRefreshLayout.setRefreshing(false);
        if (checkSuccessful(response)) {
            List<T> list = getList(response);
            if (list == null || list.size() == 0) {
                if (isRefreshing) {
                    handleEmpty();
                } else {
                    mAdapter.removeItem(mAdapter.getItemCount() - 1);
                }
            } else {
                if (isRefreshing) {
                    mEmptyView.setVisibility(View.GONE);
                    mAdapter.setData(list);
                    mAdapter.notifyItemRangeInserted(0, list.size());
                } else {
                    mAdapter.removeItem(mAdapter.getItemCount() - 1);
                    mAdapter.addData(list);
                    mAdapter.notifyDataSetChanged();
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
        } else {
            if (isRefreshing) {
                handleEmpty();
            }
        }
    }

    protected List<T> getList(JSONObject response) {
        if (mListExtractor == null) return null;
        return mListExtractor.extractList(response);
    }

    /**
     * 检查请求是否成功，默认检查result字段值是否为success
     *
     * @param response
     * @return
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
        mEmptyView.setVisibility(View.VISIBLE);
    }

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
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(false);
                }
            });
        }
        mAdapter.setLoading(false);
        if (mAdapter.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        }

        UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
    }

    /**
     * 获取请求地址
     *
     * @return
     */
    protected String getUrl() {
        if (mListExtractor == null) return null;
        return mListExtractor.getUrl();
    }

    @Override
    public void onLoadMore() {
        loadData(false);
    }

    @Override
    public void onRefresh() {
        loadData(true);
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

    /**
     * 列表抽取器，从响应数据中获取实体对象列表
     *
     * @param <O>  实体类
     * @param <VH> ViewHolder类{@link RecyclerViewHolder}
     */
    public interface ListExtractor<O, VH extends RecyclerViewHolder<O>> {
        /**
         * 请求列表数据的url
         *
         * @return
         */
        String getUrl();

        /**
         * 从响应数据中获取实体对象列表
         *
         * @param response 响应数据
         * @return 实体列表
         */
        List<O> extractList(JSONObject response);

        /**
         * 获取{@link RecyclerViewHolder}的创建器
         *
         * @return
         */
        ViewHolderCreator<VH> newViewHolderCreator();
    }

    /**
     * 列表数据加载器
     * @param <TT> 实体
     * @param <VHH> 实体组持器
     * @param <InfoT> 公用信息类
     */
    public static class Builder<TT, VHH extends RecyclerViewHolder<TT>, InfoT> {

        private RecyclerView mRecyclerView;

        private View mEmptyView;

        private SwipeRefreshLayout mRefreshLayout;

        private Activity mActivity;

        private Fragment mFragment;

        private InfoT mInfo;

        private OnItemClickListener<TT> mOnItemClickListener;

        public Builder<TT, VHH, InfoT> setRecyclerView(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
            return this;
        }

        public Builder<TT, VHH, InfoT> setEmptyView(View emptyView) {
            mEmptyView = emptyView;
            return this;
        }

        public Builder<TT, VHH, InfoT> setRefreshLayout(SwipeRefreshLayout refreshLayout) {
            mRefreshLayout = refreshLayout;
            return this;
        }

        public Builder<TT, VHH, InfoT> setActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public Builder<TT, VHH, InfoT> setFragment(Fragment fragment) {
            mFragment = fragment;
            return this;
        }

        public Builder<TT, VHH, InfoT> setInfo(InfoT info) {
            mInfo = info;
            return this;
        }

        public Builder<TT, VHH, InfoT> setOnItemClickListener(OnItemClickListener<TT> onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
            return this;
        }

        public RvLoader<TT, VHH, InfoT> build() {
            RvLoader<TT, VHH, InfoT> instance = new RvLoader<>();
            if (mRefreshLayout == null) throw new RuntimeException("No SwipeRefreshLayout passed");
            instance.setRefreshLayout(mRefreshLayout);
            if (mRecyclerView == null) throw new RuntimeException("No RecyclerView passed");
            instance.setRecyclerView(mRecyclerView);
            if (mEmptyView == null) throw new RuntimeException("No EmptyView passed");
            instance.setEmptyView(mEmptyView);
            if (mActivity != null) {
                instance.init(mActivity);
            } else if (mFragment != null) {
                instance.init(mFragment);
            } else {
                throw new RuntimeException("No activity or fragment passed");
            }
//            if (mInfo == null) throw new RuntimeException("Please set info!");
            instance.setInfo(mInfo);
            if (mOnItemClickListener != null) {
                instance.setOnItemClickListener(mOnItemClickListener);
            }
            return instance;
        }

    }
}
