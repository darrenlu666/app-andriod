package com.codyy.erpsportal.groups.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.filters.CategoryFilterFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.ChannelBlogViewHolder;
import com.codyy.erpsportal.commons.models.entities.blog.AllBlogParse;
import com.codyy.erpsportal.commons.models.entities.filter.FilterEntity;
import com.codyy.erpsportal.commons.models.entities.blog.BlogPost;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleHorizonDivider;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleRecyclerView;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;

/**
 * 门户-博文-更多(全部博文分类)
 * Created by poe on 16-1-18.
 */
public class MoreBlogPostsActivity extends BaseHttpActivity implements BaseRecyclerAdapter.OnItemClickListener{
    private final static String TAG = "MoreBlogPostsActivity";
    private static String EXTRA_FILTER_TYPE = "type";
    private static String EXTRA_TITLE = "title";
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.empty_view)EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    @Bind(R.id.drawer_layout)DrawerLayout mDrawerLayout;

    private List<BlogPost> mDataList = new ArrayList<>();
    private BaseRecyclerAdapter<BlogPost,BaseRecyclerViewHolder<BlogPost>> mAdapter ;
    private String mSchoolId ;
    private String mCategory ;//分类
    private String mBaseAreaId;
    protected CategoryFilterFragment mFilterFragment ;
    protected String mFilterType = CategoryFilterFragment.CATEGORY_TYPE_DOOR;
    protected String mTitle;

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_single_recycler_view;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_ALL_BLOG_POST_LIST;
    }

    @Override
    public HashMap<String, String> getParam() {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if(null != mSchoolId) data.put("schoolId", mSchoolId);
        if(null != mCategory) data.put("blogCategoryId",mCategory);
        if(null !=mBaseAreaId) data.put("baseAreaId", mBaseAreaId);
        data.put("start",mDataList.size()+"");
        data.put("end",(mDataList.size()+sPageCount-1)+"");
        return data;
    }

    @Override
    public void onSuccess(JSONObject response) {
        Cog.d(TAG , response.toString());
        if(null == mRecyclerView ) return;
        mRecyclerView.setRefreshing(false);
        mAdapter.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        AllBlogParse parse = new Gson().fromJson(response.toString() , AllBlogParse.class);

        if(null != parse) {
            if(null != parse.getAllBlogList() && parse.getAllBlogList().size()>0){
                for(BlogPost blog : parse.getAllBlogList()){
                    blog.setBaseViewHoldType(ChannelBlogViewHolder.ITEM_TYPE_RECOMMEND);
                    mDataList.add(blog);
                }
            }

            if(mDataList.size() < parse.getTotal()){
                mAdapter.setHasMoreData(true);
            }else{
                mAdapter.setHasMoreData(false);
            }
        }

        mAdapter.setData(mDataList);

        if(mDataList.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(false);
            mAdapter.setHasMoreData(false);
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
        if(mDataList.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(false);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        requestData();
    }

    public void init() {
        UiMainUtils.setNavigationTintColor(this,R.color.main_green);
        mFilterType =   getIntent().getStringExtra(EXTRA_FILTER_TYPE);
        mTitle  =   getIntent().getStringExtra(EXTRA_TITLE);
        mSchoolId   =   mUserInfo.getSchoolId();
        mBaseAreaId =   mUserInfo.getBaseAreaId();
        mTitleTextView.setText(getString(R.string.blog_all_title));
        initToolbar(mToolBar);
        if(null != mTitle){
            mTitleTextView.setText(mTitle);
        }

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
                //下拉刷新
               refresh();
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder<BlogPost>>() {
            @Override
            public BaseRecyclerViewHolder<BlogPost> createViewHolder(final ViewGroup parent, int viewType) {
                return new ChannelBlogViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_channel_blog_post,parent,false));
            }

            @Override
            public int getItemViewType(int position) {
                return mDataList.get(position).getBaseViewHoldType();
            }
        });

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<BlogPost>() {
            @Override
            public void onItemClicked(View v, int position, BlogPost data) {
                if(v.getId() == R.id.sdv_pic){
                    //1.自己的信息跳转到首页-"我的"
                    if(data.getBaseUserId().equals(mUserInfo.getBaseUserId())){
                        MainActivity.start(MoreBlogPostsActivity.this , mUserInfo , 2);
                    }else{//2.访客
                        PublicUserActivity.start(MoreBlogPostsActivity.this , data.getBaseUserId());
                    }
                }else{
                    BlogPostDetailActivity.start(MoreBlogPostsActivity.this,data.getBlogId(),BlogPostDetailActivity.FROM_TYPE_SHARE);
                }
            }
        });
        mAdapter.setOnLoadMoreClickListener(new BaseRecyclerAdapter.OnLoadMoreClickListener() {
            @Override
            public void onMoreData() {
                requestData();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
//        this.enableLoadMore(mRecyclerView);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mFilterFragment = CategoryFilterFragment.create(mFilterType);
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

    /**
     * @param context context
     */
    public static void start(Context context , String filterType ,String title){
        Intent intent = new Intent(context , MoreBlogPostsActivity.class);
        intent.putExtra(EXTRA_FILTER_TYPE , filterType);
        intent.putExtra(EXTRA_TITLE , title);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }

    //刷新数据
    private void refresh() {
        mRecyclerView.setRefreshing(true);
        mDataList.clear();
        mAdapter.setHasMoreData(false);
        requestData();
    }

    @Override
    public void onItemClicked(View v, int position, Object data) {
        if(null == data) return;
        FilterEntity fe = (FilterEntity) data;
        mTitleTextView.setText(fe.getName());
        if(mDrawerLayout.isDrawerOpen(Gravity.RIGHT)){
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }
        mCategory   =   fe.getId();
        //refresh
        refresh();
    }
}
