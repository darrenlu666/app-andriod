package com.codyy.erpsportal.exam.controllers.fragments.school;

import android.content.Context;
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
import com.codyy.erpsportal.commons.controllers.adapters.RecyclerAdapter.OnLoadMoreListener;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.UpOrDownButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集成下拉刷新与上拉更多的碎片
 * Created by gujiajia on 2015/12/22.
 */
public abstract class StudentStatisticsLoadFragment<T, VH extends RecyclerViewHolder<T>> extends Fragment implements OnRefreshListener, OnLoadMoreListener {

    private static final String TAG = StudentStatisticsLoadFragment.class.getSimpleName();

    private static final int LOAD_COUNT = 10;

    private final static long MIN_LOADING_INTERVAL = 500L;

    protected RecyclerView mRecyclerView;

    protected TextView mEmptyTv;

    protected SwipeRefreshLayout mSwipeRefreshLayout;

    private RequestSender mRequestSender;

    private RecyclerAdapter<T, VH> mAdapter;

    private int mStart;

    private Handler mHandler;

    private View mRootView;
    /**
     * 参数
     */
    private Map<String, String> mParams;
    protected UpOrDownButton mUdName, mUdScore, mUdRate, mUdCnt;
    protected TextView mUpOrDwonClass;
    protected View mLineView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParams = new HashMap<>();
        mRequestSender = new RequestSender(this);
        mHandler = new Handler();
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_data_recycle_student_statistics, null);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        mUpOrDwonClass = (TextView) mRootView.findViewById(R.id.ud_class);
        mLineView = mRootView.findViewById(R.id.ll_fragment_data_student_statistics_linel);
        mUdName = (UpOrDownButton) mRootView.findViewById(R.id.ud_name);
        mUdName.setUp(false);
        mUdScore = (UpOrDownButton) mRootView.findViewById(R.id.ud_score);
        mUdScore.setUp(false);
        mUdRate = (UpOrDownButton) mRootView.findViewById(R.id.ud_rate);
        mUdRate.setUp(false);
        mUdCnt = (UpOrDownButton) mRootView.findViewById(R.id.ud_count);
        mUdCnt.setUp(false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_layout);
        mEmptyTv = (TextView) mRootView.findViewById(R.id.tv_empty);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        initEmptyTv();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = newRecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);
        extraInitViewsStyles();
        loadData(true);
    }

    @Nullable
    @Override
    final public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mRootView;
    }

    /**
     * 需要特殊的风格的话RecyclerView在这里设置
     */
    protected void extraInitViewsStyles() {
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        int end = start + LOAD_COUNT;
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
        mRequestSender.sendRequest(new RequestSender.RequestData(getUrl(), params,
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
                }
                )
        );
    }

    /**
     * 延迟响应
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
     * @param response
     * @param isRefreshing
     */
    private void handleNormalResponse(JSONObject response, boolean isRefreshing) {
        mAdapter.setLoading(false);
        mSwipeRefreshLayout.setRefreshing(false);
        if ("success".equals(response.optString("result"))) {
            int total = response.optInt("total");
            if (total == 0) {
                handleEmpty();
            } else {
                mEmptyTv.setVisibility(View.GONE);
                List<T> list = getList(response);
                if (isRefreshing) {
                    mAdapter.setData(list);
                } else {//删除最后一项加载进度，添加新加的数据项
                    mAdapter.removeItem(mAdapter.getItemCount() - 1);
                    mAdapter.addData(list);
                }
                //如果已经加载所有，下拉更多关闭
                if (total <= mAdapter.getItemCount()) {
                    mAdapter.disableLoadMore();
                } else {
                    mAdapter.enableLoadMore();
                }
                mAdapter.notifyDataSetChanged();

            }
            mStart = mAdapter.getItemCount();
        } else {
            if (isRefreshing) {
                handleEmpty();
            }
        }
    }

    /**
     * 处理空情况
     */
    private void handleEmpty() {
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
     * 添加请求参数
     *
     * @param params
     */
    protected void addParams(Map<String, String> params) {
    }

    protected void addParam(String key, String value) {
        mParams.put(key, value);
    }

    protected void delParam(String key) {
        mParams.remove(key);
    }

    protected void addMapToParam(Map<String, String> newParams) {
        mParams.putAll(newParams);
    }

    public void loadData(boolean refresh) {
        loadData(mParams, refresh);
    }

}
