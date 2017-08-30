package com.codyy.erpsportal.groups.controllers.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.commons.models.parsers.JsonParser.OnParsedListener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.NetworkUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.erpsportal.commons.widgets.TitleItemBar;
import com.codyy.erpsportal.perlcourseprep.controllers.activities.PersonalLesPrepContentActivity;
import com.codyy.url.URLConfig;
import com.facebook.drawee.view.SimpleDraweeView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 圈组个人备课
 */
public class GroupLessonPlanActivity extends AppCompatActivity implements OnRefreshListener {

    private final static String TAG = "GroupLessonPlanActivity";

    private final static String EXTRA_GROUP_ID = "com.codyy.erpsportal.groupId";

    private static final int LOAD_COUNT = 8;

    private TitleBar mTitleBar;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerView;

    private TextView mEmptyTv;

    private GroupLessonPlanAdapter mAdapter;

    private String mGroupId;

    private RequestSender mRequestSender;

    private DateTimeFormatter mDateTimeFormatter;

    private int mStart;

    private int mRefreshingCount;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        /**
         * 如果因为网络断开禁用了加载更多，需要保存此状态，在网络恢复时重新开启加载更多
         */
        boolean disabled = false;
        @Override
        public void onReceive(Context context, Intent intent) {
            Cog.d(TAG, "current networkInfo type is ", intent);
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                Cog.d(TAG, "current networkInfo is ", networkInfo);
                if (networkInfo != null && networkInfo.isAvailable()) {
                    mSwipeRefreshLayout.setEnabled(true);
                    if (disabled) {
                        mAdapter.enableLoadMore();
                        disabled = false;
                    }
                } else {
                    mSwipeRefreshLayout.setEnabled(false);
                    disabled = mAdapter.disableLoadMore();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_lesson_plan);
        UiMainUtils.setNavigationTintColor(this, R.color.main_green);
        initAttributes();
        initViews();
        loadData(true);
    }

    private void initAttributes() {
        mGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        mRequestSender = new RequestSender(this);
        mDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void initViews() {
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.rl_group_lesson_plan);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mEmptyTv = (TextView) findViewById(R.id.tv_empty);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new GroupLessonPlanAdapter(mRecyclerView, new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Cog.d(TAG, "onLoadMore");
                loadAllGroupLessonPlans(false);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        initEmptyTv();
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

        SpannableString spStr = new SpannableString(getString(R.string.no_data_for_now));
        spStr.setSpan(mMyClickableSpan, 0, spStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mEmptyTv.setText(spStr);
        mEmptyTv.setHighlightColor(Color.TRANSPARENT);
        mEmptyTv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onRefresh() {
        Cog.d(TAG, "onRefresh");
        loadData(true);
    }

    private void loadData(final boolean isRefreshing) {
        if (!NetworkUtils.isConnected()) {
            UIUtils.toast(R.string.please_connect_internet, Toast.LENGTH_SHORT);
            return;
        }
        if (isRefreshing) {
            loadTopHotGroupLessonPlans();
        }
        loadAllGroupLessonPlans(isRefreshing);
    }

    private void loadTopHotGroupLessonPlans() {
        Map<String, String> params = new HashMap<>();
        UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
        params.put("uuid", userInfo.getUuid());
        params.put("groupId", mGroupId);
        showRefreshing();
        mRequestSender.sendRequest(new RequestData(URLConfig.GROUP_PERSONAL_LESSON_PLANS, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadData response=", response);
                hideRefreshing();
                if ("success".equals(response.optString("result"))) {
                    mAdapter.clearTopHotItems();
                    JSONArray topJa = response.optJSONArray("topmostPreparations");
                    mTopParser.parseArray(topJa, mOnTopParsedTopHotListener);
                    JSONArray hotJa = response.optJSONArray("hotPreparations");
                    mAdapter.addTopHotItem(new Title("热门教案"));
                    if (hotJa != null && hotJa.length() > 0) {
                        mItemParser.parseArray(hotJa, mOnHotParsedTopHotListener);
                    } else {
                        mAdapter.addTopHotItem(new EmptyItem());
                    }
                    mAdapter.notifyDataSetChanged();
                }
                updateEmptyView();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "loadData error=" + error);
                hideRefreshing();
                updateEmptyView();
            }
        }));
    }

    private void loadAllGroupLessonPlans(final boolean isRefreshing) {
        if (isRefreshing) {
            mStart = 0;
        }
        Map<String, String> params = new HashMap<>();
        UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
        params.put("uuid", userInfo.getUuid());
        params.put("groupId", mGroupId);

        int start = mStart;
        int end = start + LOAD_COUNT;
        params.put("start", "" + start);
        params.put("end", "" + (end - 1));
        if (isRefreshing) showRefreshing();
        Cog.d(TAG, "loadAllGroupLessonPlans url=", URLConfig.GROUP_PERSONAL_LESSON_PLANS_ALL, params);
        mRequestSender.sendRequest(new RequestData(URLConfig.GROUP_PERSONAL_LESSON_PLANS_ALL, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadData response=", response);
                doFinishLoading(isRefreshing);

                if ("success".equals(response.optString("result"))) {
                    if (isRefreshing) {
                        mAdapter.clearAllItems();
                        mAdapter.addAllItem(new Title("全部教案"));
                    }
                    if (!isRefreshing) {
                        mAdapter.removeLoadMoreItem();//删除加载更多
                    }
                    JSONArray allJa = response.optJSONArray("list");
                    if (allJa != null && allJa.length() > 0) {
                        mItemParser.parseArray(allJa, mOnParsedAllListener);
                    }
                    if (mAdapter.getAllCount() == 0) {
                        mAdapter.addAllItem(new EmptyItem());
                    }
                    mAdapter.configLoadMore(response.optInt("total"));
                    mAdapter.notifyDataSetChanged();
                }
                updateEmptyView();
                mStart = mAdapter.getAllCount();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "loadData error=" + error);
                doFinishLoading(isRefreshing);
                if (!isRefreshing) {
                    mAdapter.removeLoadMoreItem();
                    mAdapter.notifyDataSetChanged();
                }
                updateEmptyView();
            }
        }));
    }

    /**
     * 完成加载更多
     *
     * @param isRefreshing
     */
    private void doFinishLoading(boolean isRefreshing) {
        if (isRefreshing) {
            hideRefreshing();
        } else {
            mAdapter.setLoading(false);
        }
    }

    private void showRefreshing() {
        mRefreshingCount++;
        mSwipeRefreshLayout.setRefreshing(true);
    }

    private void hideRefreshing() {
        mRefreshingCount--;
        if (mRefreshingCount == 0) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    private void updateEmptyView() {
        if (mAdapter.getItemCount() > 0) {
            mEmptyTv.setVisibility(View.GONE);
        } else {
            mEmptyTv.setVisibility(View.VISIBLE);
        }
    }

    private JsonParser<Top> mTopParser = new JsonParser<Top>() {
        @Override
        public Top parse(JSONObject jsonObject) {
            Top top = new Top();
            top.id = jsonObject.optString("id");
            top.text = jsonObject.optString("title");
            return top;
        }
    };

    private JsonParser<Item> mItemParser = new JsonParser<Item>() {
        @Override
        public Item parse(JSONObject jsonObject) {
            Item item = new Item();
            item.id = jsonObject.optString("id");
            item.title = jsonObject.optString("title");
            item.icon = jsonObject.optString("subjectPic");
            item.mainTeacher = jsonObject.optString("mainTeacher");
            item.startTime = mDateTimeFormatter.print(jsonObject.optLong("startTime"));
            return item;
        }
    };

    private OnParsedListener<Item> mOnHotParsedTopHotListener = new OnParsedListener<Item>() {
        @Override
        public void handleParsedObj(Item item) {
            mAdapter.addTopHotItem(item);
        }
    };

    private OnParsedListener<Top> mOnTopParsedTopHotListener = new OnParsedListener<Top>() {
        @Override
        public void handleParsedObj(Top top) {
            mAdapter.addTopHotItem(top);
        }
    };

    private OnParsedListener<Item> mOnParsedAllListener = new OnParsedListener<Item>() {
        @Override
        public void handleParsedObj(Item obj) {
            mAdapter.addAllItem(obj);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestSender.stop();
        mRequestSender = null;
    }

    static class GroupLessonPlanAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        private final static int TYPE_TITLE = 0;

        private final static int TYPE_TOP = 1;

        private final static int TYPE_LESSON_PLAN = 2;

        private final static int TYPE_EMPTY = 3;

        private final static int TYPE_EMPTY_ITEM = 4;

        private final static int TYPE_MORE = 5;

        private List<Object> mTopHotItems;

        private List<Object> mAllItems;

        private int mVisibleThreshold = 2;

        private int mLastVisibleItem;

        /**
         * 已绑定到RecyclerView的项目个数。从LayoutManager中获取。
         */
        private int mTotalItemCount;

        private boolean mLoading;

        private boolean mLoadMoreEnabled = false;

        private OnLoadMoreListener mOnLoadMoreListener;

        public GroupLessonPlanAdapter(RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
            mTopHotItems = new ArrayList<>();
            mAllItems = new ArrayList<>();
            mOnLoadMoreListener = onLoadMoreListener;
            addOnScrollListenerToRecyclerView(recyclerView);
        }

        private void addOnScrollListenerToRecyclerView(RecyclerView recyclerView) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!mLoadMoreEnabled) return;
                    mTotalItemCount = linearLayoutManager.getItemCount();
                    mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!mLoading && mTotalItemCount <= (mLastVisibleItem + mVisibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            addAllItem(null);
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    notifyItemInserted(getItemCount() - 1);
                                }
                            });
                            mLoading = true;
                            mOnLoadMoreListener.onLoadMore();
                        }
                        mLoading = true;
                    }
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            Object item = getItem(position);
            if (item == null) {
                return TYPE_MORE;
            } else if (item instanceof Top) {
                return TYPE_TOP;
            } else if (item instanceof Title) {
                return TYPE_TITLE;
            } else if (item instanceof Item) {
                return TYPE_LESSON_PLAN;
            } else if (item instanceof EmptyItem) {
                return TYPE_EMPTY_ITEM;
            } else {
                return TYPE_EMPTY;
            }
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_TOP: {
                    TextView topTv = new TextView(parent.getContext());
                    topTv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    topTv.setLines(1);
                    topTv.setTextSize(18);
                    topTv.setBackgroundDrawable(makeBackground(parent.getContext()));
                    int padding = UIUtils.dip2px(parent.getContext(), 12);
                    topTv.setPadding(padding, padding, padding, padding);
                    return new TopViewHolder(topTv);
                }
                case TYPE_TITLE: {
                    return new TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.item_group_personal_lesson_plan_title, parent, false));
                }
                case TYPE_LESSON_PLAN: {
                    return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.item_group_personal_lesson_plan, parent, false));
                }
                case TYPE_EMPTY_ITEM: {
                    TextView view = new TextView(parent.getContext());
                    view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    view.setText(R.string.no_data_for_now);
                    int padding = UIUtils.dip2px(parent.getContext(), 8);
                    view.setPadding(padding, padding, padding, padding);
                    view.setBackgroundColor(0xFFF7F7F7);
                    view.setGravity(Gravity.CENTER);
                    return new EmptyItemHolder(view);
                }
                case TYPE_MORE: {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_last, parent, false);
                    return new LastItemHolder(view);
                }
            }
            return null;
        }

        private Drawable makeBackground(Context context) {
            int attrs[] = new int[]{R.attr.selectableItemBackground};
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs);
            Drawable bg = typedArray.getDrawable(0);
            typedArray.recycle();

            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                    context.getResources().getDrawable(R.drawable.bg_bottom_line),
                    bg
            });
            return layerDrawable;
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            if (holder instanceof LastItemHolder) {
                ((LastItemHolder) holder).setDataToView(mLoadMoreEnabled);
            } else {
                holder.setDataToView(getItem(position));
            }
        }

        private Object getItem(int position) {
            return position >= mTopHotItems.size() ?
                    mAllItems.get(position - mTopHotItems.size()) : mTopHotItems.get(position);
        }

        @Override
        public int getItemCount() {
            return (mTopHotItems == null ? 0 : mTopHotItems.size())
                    + (mAllItems == null ? 0 : mAllItems.size());
        }

        public void addTopHotItem(Object item) {
            mTopHotItems.add(item);
        }

        public void clearTopHotItems() {
            mTopHotItems.clear();
        }

        public void addAllItem(Object item) {
            mAllItems.add(item);
        }

        public void clearAllItems() {
            mAllItems.clear();
        }

        public boolean disableLoadMore() {
            if (mLoadMoreEnabled) {
                mLoadMoreEnabled = false;
                return true;
            } else {
                return false;
            }
        }

        public void enableLoadMore() {
            if (!mLoadMoreEnabled) {
                mLoadMoreEnabled = true;
            }
        }

        public void configLoadMore(int total) {
            if (mAllItems.size() > 0 && total > mAllItems.size() - 1) {
                enableLoadMore();
            } else {
                disableLoadMore();
            }
        }


        public void setLoading(boolean isLoading) {
            mLoading = isLoading;
        }

        /**
         * 获取全部个人备课数量，剔除标题
         *
         * @return
         */
        public int getAllCount() {
            if (mAllItems.size() > 0) {
                return mAllItems.size() - 1;
            } else {
                return 0;
            }
        }

        public void removeLoadMoreItem() {
            if (mAllItems.size() > 0) {
                mAllItems.remove(mAllItems.size() - 1);
            }
        }
    }

    interface OnLoadMoreListener {
        void onLoadMore();
    }

    static class TitleViewHolder extends RecyclerViewHolder<Title> {

        private TitleItemBar mTitleBar;

        public TitleViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void mapFromView(View view) {
            mTitleBar = (TitleItemBar) view.findViewById(R.id.title_item_bar);
        }

        @Override
        public void setDataToView(Title item) {
            mTitleBar.setTitle(item.text);
        }
    }

    static class Title {
        String text;

        public Title(String title) {
            this.text = title;
        }
    }

    static class TopViewHolder extends RecyclerViewHolder<Top> {

        private Context context;

        private TextView textTv;

        public TopViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void mapFromView(View view) {
            context = view.getContext();
            textTv = (TextView) view;
        }

        @Override
        public void setDataToView(Top top) {
            String titleStr = context.getString(R.string.put_top_something, top.text);
            SpannableString ss = new SpannableString(titleStr);
            ss.setSpan(new ForegroundColorSpan(0xFFF2B76C), 0, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            textTv.setText(ss);
            PersonalLesPrepContentActivity.addEnterListenerOnView(itemView, top.id);
        }
    }

    static class Top {
        String id;
        String text;
    }

    static class ItemViewHolder extends RecyclerViewHolder<Item> {

        private Context context;

        private SimpleDraweeView dvIcon;

        private TextView nameTv;

        private TextView descTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void mapFromView(View view) {
            context = view.getContext();
            dvIcon = (SimpleDraweeView) view.findViewById(R.id.dv_icon);
            nameTv = (TextView) view.findViewById(R.id.tv_name);
            descTv = (TextView) view.findViewById(R.id.et_desc);
        }

        @Override
        public void setDataToView(Item item) {
            ImageFetcher.getInstance(context).fetchImage(dvIcon, URLConfig.BASE+"/images/" + item.icon);
            nameTv.setText(item.title);
            String teacherName = item.mainTeacher;
            if (teacherName.length() > 4) {
                teacherName = teacherName.substring(0, 4).concat("…");
            }
            descTv.setText(context.getString(R.string.name_and_time, teacherName, item.startTime));
            PersonalLesPrepContentActivity.addEnterListenerOnView(itemView, item.id);
        }
    }

    /**
     * 最后一项，用于显示没有更多或加载数据ProgressBar
     */
    private static class LastItemHolder extends RecyclerViewHolder {

        private TextView mEmptyTv;

        private ProgressBar mLoadingPb;

        public LastItemHolder(View itemView) {
            super(itemView);
            mapFromView(itemView);
        }

        public void mapFromView(View view) {
            mEmptyTv = (TextView) view.findViewById(R.id.tv_empty);
            mLoadingPb = (ProgressBar) view.findViewById(R.id.pb_loading);
        }

        public void setDataToView(boolean loadMoreEnabled) {
            if (loadMoreEnabled) {
                mEmptyTv.setVisibility(View.GONE);
                mLoadingPb.setVisibility(View.VISIBLE);
            } else {
                mEmptyTv.setVisibility(View.VISIBLE);
                mLoadingPb.setVisibility(View.GONE);
            }
        }
    }

    static class Item {
        String id;
        String icon;
        String title;
        String mainTeacher;
        String startTime;
    }

    static class EmptyItem{}

    private static class EmptyItemHolder extends RecyclerViewHolder<EmptyItem> {

        public EmptyItemHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void mapFromView(View view) { }
    }

    public static void start(Context context, String groupId) {
        Intent intent = new Intent(context, GroupLessonPlanActivity.class);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        context.startActivity(intent);
        if (context instanceof Activity) {
            UIUtils.addEnterAnim((Activity) context);
        }
    }
}
