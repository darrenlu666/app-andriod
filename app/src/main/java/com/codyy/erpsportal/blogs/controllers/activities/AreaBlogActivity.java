/*
 *
 * 阔地教育科技有限公司版权所有(codyy.com/codyy.cn)
 * Copyright (c)  2017, Codyy and/or its affiliates. All rights reserved.
 *
 */

package com.codyy.erpsportal.blogs.controllers.activities;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.fragments.filters.CategoryFilterFragment;
import com.codyy.erpsportal.commons.controllers.fragments.filters.SimpleListFragment;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.blog.BlogPost;
import com.codyy.erpsportal.commons.models.entities.blog.MyBlogPostList;
import com.codyy.erpsportal.commons.models.entities.filter.FilterEntity;
import com.codyy.erpsportal.commons.models.entities.my.MyBaseTitle;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.groups.controllers.activities.BlogPostDetailActivity;
import com.codyy.erpsportal.groups.controllers.viewholders.ChannelBlogViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.MyBlogViewHolder;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleHorizonDivider;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleRecyclerView;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 应用-博文列表(v5.3.8)
 * Created by poe on 17-09-14.
 */
public class AreaBlogActivity extends BaseHttpActivity implements BaseRecyclerAdapter.OnItemClickListener {
    private static final String TAG = "AreaBlogActivity";
    private static final String EXTRA_DATA = "extra.data";
    @Bind(R.id.tv_sort)
    TextView mTvSort;
    @Bind(R.id.tv_role)
    TextView mTvRole;
    @Bind(R.id.tv_recommend)
    TextView mTvRecommend;
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.toolbar_title)
    TextView mTitleTextView;
    @Bind(R.id.empty_view)
    EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)
    SimpleRecyclerView mRecyclerView;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private ArrayList<MyBaseTitle> mTimes = new ArrayList<>();//排序是啊选
    private ArrayList<MyBaseTitle> mRoles = new ArrayList<>();//角色类型筛选
    private ArrayList<MyBaseTitle> mRecommends = new ArrayList<>();//推荐类型筛选
    private List<BlogPost> mDataList = new ArrayList<>();
    private BaseRecyclerAdapter<BlogPost, BaseRecyclerViewHolder<BlogPost>> mAdapter;
    private UserInfo mNativeUserInfo;
    //筛选
    private CategoryFilterFragment mFilterFragment;
    private SimpleListFragment mSortFragment, mRoleFragment, mReCommendFragment;
    private int mSortIndex = 0;// 0 : 排序 1:角色排序 2: 推荐类型.

    private String mFilterType = CategoryFilterFragment.CATEGORY_TYPE_PERSON;
    private String mCategory;//分类.
    /**
     * TIMEASC、READDESC、READASC、COMMENTDESC、COMMENTDESC、默认为时间降序，不需要传值
     **/
    private String mRankType = "";//default:'' 时间降序
    /**
     * 推送类型
     * 全部(null)、已推送到门户(HOME)、已推送到上级(HIGHER)、未推送(NONE)
     */
    private String mRecommendType = "";
    /**
     * 角色身份类型
     * 全部、老师(TEACHER)、学生(STUDENT)、家长(PARENT)
     */
    private String mRoleType = "";


    @Override
    public int obtainLayoutId() {
        return R.layout.activity_area_blog;
    }

    @Override
    public String obtainAPI() {
        if (null != mUserInfo && UserInfo.USER_TYPE_AREA_USER.equals(mUserInfo.getUserType())) {
            return URLConfig.GET_APP_AREA_BLOG_LIST;
        } else {
            return URLConfig.GET_APP_SCHOOL_BLOG_LIST;
        }
    }


    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if (null != mNativeUserInfo) {
            data.put("visitedUserId", mNativeUserInfo.getBaseUserId());
        }
        if (null != mCategory) data.put("blogCategoryId", mCategory);
        //排序
        if (null != mRankType) data.put("rank", mRankType);
        //推荐类型
        if (null != mRecommendType) data.put("sortRecommend", mRecommendType);
        //角色
        if (null != mRoleType) data.put("sortRole", mRoleType);

        data.put("start", mDataList.size() + "");
        data.put("end", (mDataList.size() + sPageCount - 1) + "");
        return data;
    }

    @Override
    public void init() {
        UiMainUtils.setNavigationTintColor(this, R.color.main_green);
        initFilterData();
        mNativeUserInfo = getIntent().getParcelableExtra(EXTRA_DATA);
        mTitleTextView.setText("全部博文");
        initToolbar(mToolBar);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                mEmptyView.setLoading(false);
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
                refresh();
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder<BlogPost>>() {
            @Override
            public BaseRecyclerViewHolder<BlogPost> createViewHolder(ViewGroup parent, int viewType) {
                BaseRecyclerViewHolder viewHolder = null;
                switch (viewType) {
                    case ChannelBlogViewHolder.ITEM_TYPE_TOP://置顶博文
                    case ChannelBlogViewHolder.ITEM_TYPE_HOT://热门博文
                    case ChannelBlogViewHolder.ITEM_TYPE_ALL://全部博文
                        viewHolder = new MyBlogViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_blog_post, parent, false));
                        break;
                }
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
                return mDataList.get(position).getBaseViewHoldType();
            }
        });

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<BlogPost>() {
            @Override
            public void onItemClicked(View v, int position, BlogPost data) {
                switch (mAdapter.getItemViewType(position)) {
                    case BaseRecyclerAdapter.TYPE_FOOTER://加载更多
//                        showMore();
//                        requestData();
                        break;
                    default:
                        if (v.getId() == R.id.sdv_pic) {
                            //1.自己的信息跳转到首页-"我的"
                            if (data.getBaseUserId().equals(mUserInfo.getBaseUserId())) {
                                MainActivity.start(AreaBlogActivity.this, mUserInfo, 2);
                            } else {//2.访客
                                PublicUserActivity.start(AreaBlogActivity.this, data.getBaseUserId());
                            }
                        } else {
                            BlogPostDetailActivity.start(AreaBlogActivity.this, data.getBlogId(), BlogPostDetailActivity.FROM_TYPE_PERSON);
                        }
                        break;
                }

            }
        });
        mAdapter.setOnLoadMoreClickListener(new BaseRecyclerAdapter.OnLoadMoreClickListener() {
            @Override
            public void onMoreData() {
                mAdapter.setRefreshing(true);
                requestData(false);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        initCategoryFilter();
//        this.enableLoadMore(mRecyclerView);
    }

    //排序/角色/推荐 数据初始化
    private void initFilterData() {
        //排序
        mTimes.clear();
        mTimes.add(new MyBaseTitle("", "时间倒序"));
        mTimes.add(new MyBaseTitle("TIMEASC", "时间顺序"));
        mTimes.add(new MyBaseTitle("READDESC", "阅读量高到低"));
        mTimes.add(new MyBaseTitle("READASC", "阅读量低到高"));
        mTimes.add(new MyBaseTitle("COMMENTDESC", "评论数高到低"));
        mTimes.add(new MyBaseTitle("COMMENTDESC", "评论数低到高"));
        //角色
        mRoles.clear();
        mRoles.add(new MyBaseTitle("", "全部"));
        mRoles.add(new MyBaseTitle("TEACHER", "老师"));
        mRoles.add(new MyBaseTitle("STUDENT", "学生"));
        mRoles.add(new MyBaseTitle("PARENT", "家长"));
        //推荐
        mRecommends.clear();
        mRecommends.add(new MyBaseTitle("", "全部"));
        mRecommends.add(new MyBaseTitle("", "已推送到门户"));
        mRecommends.add(new MyBaseTitle("", "已推送到上级"));
        mRecommends.add(new MyBaseTitle("", "未推送"));
    }

    private void initCategoryFilter() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mFilterFragment = CategoryFilterFragment.create(mFilterType, mNativeUserInfo.getBaseUserId(), "");
        ft.replace(R.id.fl_filter, mFilterFragment);
        ft.commit();

        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
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

        this.setFilterListener(new IFilterListener() {
            @Override
            public void onFilterClick(MenuItem item) {
                if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                } else {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                }
            }

            @Override
            public void onPreFilterCreate(Menu menu) {
                menu.getItem(0).setIcon(R.drawable.ic_menu_normal);
            }
        });
    }


    private void showHeadFilter() {

        if (mSortFragment == null) {
            mSortFragment = SimpleListFragment.newInstance(mTimes);
            mSortFragment.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<FilterEntity>() {
                @Override
                public void onItemClicked(View v, int position, FilterEntity data) throws Exception {
                    mTvSort.setText(data.getName());
                    mRankType = data.getId();
                    hideHeadFilter();
                    //refresh
                    refresh();
                }
            });
        }

        if (mRoleFragment == null) {
            mRoleFragment = SimpleListFragment.newInstance(mRoles);
            mRoleFragment.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<FilterEntity>() {
                @Override
                public void onItemClicked(View v, int position, FilterEntity data) throws Exception {
                    mTvRole.setText(data.getName());
                    mRoleType = data.getId();
                    hideHeadFilter();
                    //refresh
                    refresh();
                }
            });
        }

        if (mReCommendFragment == null) {
            mReCommendFragment = SimpleListFragment.newInstance(mRecommends);
            mRoleFragment.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<FilterEntity>() {
                @Override
                public void onItemClicked(View v, int position, FilterEntity data) throws Exception {
                    mTvRecommend.setText(data.getName());
                    mRecommendType = data.getId();
                    hideHeadFilter();
                    //refresh
                    refresh();
                }
            });
        }


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (mSortIndex) {
            case 0:
                ft.replace(R.id.fl_sort, mSortFragment);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.fl_sort, mRoleFragment);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.fl_sort, mReCommendFragment);
                ft.commit();
                break;
        }

    }

    private void hideHeadFilter() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (mSortIndex) {
            case 0:
//                ft.replace(R.id.fl_filter, mSortFragment);
                ft.remove(mSortFragment);
                ft.commit();
                break;
            case 1:
                ft.remove(mRoleFragment);
//                ft.replace(R.id.fl_filter, mRoleFragment);
                ft.commit();
                break;
            case 2:
                ft.remove(mReCommendFragment);
//                ft.replace(R.id.fl_filter, mReCommendFragment);
                ft.commit();
                break;
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    @Override
    public void onSuccess(JSONObject response, boolean isRefreshing) {
        Cog.d(TAG, response.toString());
        if (null == mRecyclerView) return;
        if (isRefreshing) mDataList.clear();
        mRecyclerView.setRefreshing(false);
        mAdapter.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        MyBlogPostList blogPostList = new Gson().fromJson(response.toString(), MyBlogPostList.class);
        if (null != blogPostList) {
            if (blogPostList.getBlogList() != null && blogPostList.getBlogList().size() > 0) {
                List<BlogPost> topList = new ArrayList<>();
                List<BlogPost> allList = new ArrayList<>();
                for (BlogPost blogPost : blogPostList.getBlogList()) {
                    if ("Y".equals(blogPost.getTopFlag())) {
                        //置顶博文
                        blogPost.setBaseViewHoldType(ChannelBlogViewHolder.ITEM_TYPE_TOP);
                        blogPost.setBaseTitle(blogPost.getBlogTitle());
                        topList.add(blogPost);
                    } else {
                        blogPost.setBaseViewHoldType(ChannelBlogViewHolder.ITEM_TYPE_ALL);
                        allList.add(blogPost);
                    }
                }
                if (topList.size() > 0) mDataList.addAll(topList);
                if (allList.size() > 0) mDataList.addAll(allList);
            }
        }
        mAdapter.setData(mDataList);
        if (mDataList.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(false);
            mAdapter.setHasMoreData(false);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        //more .
        if (mDataList.size() < blogPostList.getTotal()) {
            showMore();
        } else {
            hideMore();
        }
    }

    @Override
    public void onFailure(Throwable error) {
        if (null == mRecyclerView) return;
        mRecyclerView.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        if (mDataList.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(false);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }


    private void showMore() {
        mAdapter.setHasMoreData(true);
        mAdapter.notifyDataSetChanged();
    }

    private void hideMore() {
        mAdapter.setHasMoreData(false);
        mAdapter.notifyDataSetChanged();
    }

    //刷新数据
    private void refresh() {
        if (null != mRefreshLayout) mRefreshLayout.setRefreshing(true);
        mRecyclerView.setRefreshing(true);
        requestData(true);
    }

    public static void start(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, AreaBlogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra(EXTRA_DATA, userInfo);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }

    @Override
    public void onItemClicked(View v, int position, Object data) {
        if (null == data) return;
        FilterEntity fe = (FilterEntity) data;
        mTitleTextView.setText(fe.getName());
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }
        mCategory = fe.getId();
        //refresh
        refresh();
    }

    @OnClick({R.id.tv_sort, R.id.tv_role, R.id.tv_recommend})
    void headFilterClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sort:
                mSortIndex = 0;

                break;
            case R.id.tv_role:
                mSortIndex = 1;

                break;
            case R.id.tv_recommend:
                mSortIndex = 2;

                break;

        }
        ToastUtil.showToast("select index : " + mSortIndex);
//        mSortFragment
        // TODO: 17-9-14 展示筛选的Fragment.
        showHeadFilter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
