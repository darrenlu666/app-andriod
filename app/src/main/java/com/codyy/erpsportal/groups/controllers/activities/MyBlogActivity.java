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

import com.codyy.erpsportal.R;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleHorizonDivider;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleRecyclerView;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.fragments.filters.CategoryFilterFragment;
import com.codyy.erpsportal.groups.controllers.viewholders.ChannelBlogViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.MyBlogViewHolder;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.blog.BlogPost;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.models.entities.blog.MyBlogPostList;
import com.codyy.erpsportal.commons.models.entities.filter.FilterEntity;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;

/**
 * 我的博文列表
 * Created by poe on 16-3-2.
 */
public class MyBlogActivity extends BaseHttpActivity implements BaseRecyclerAdapter.OnItemClickListener{
    private static  final String TAG = "ClassBlogActivity";
    private static final String EXTRA_DATA = "extra.data";
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.empty_view)EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    @Bind(R.id.drawer_layout)DrawerLayout mDrawerLayout;
    private List<BlogPost> mDataList = new ArrayList<>();
    private BaseRecyclerAdapter<BlogPost,BaseRecyclerViewHolder<BlogPost>> mAdapter ;
    private UserInfo mNativeUserInfo;
    //筛选
    private CategoryFilterFragment mFilterFragment ;
    private String mFilterType = CategoryFilterFragment.CATEGORY_TYPE_PERSON;
    private String mCategory ;//分类

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_single_recycler_view;
    }

    @Override
    public String obtainAPI() {
        if(!isVisitor()){
            return URLConfig.GET_PERSONAL_BLOG_LIST;
        }else{
//            return URLConfig.GET_PUBLIC_PERSONAL_BLOG_LIST;
            return URLConfig.GET_PERSONAL_BLOG_LIST;
        }
    }

    /**
     * 是否为访客
     * @return
     */
    private boolean isVisitor(){
        boolean result = true ;
        if(mNativeUserInfo != null && mUserInfo != null ){
            if(mNativeUserInfo.getBaseUserId().equals(mUserInfo.getBaseUserId())){
                result = false ;
            }
        }
        return result ;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if(null != mNativeUserInfo) {
            data.put("visitedUserId",mNativeUserInfo.getBaseUserId());
        }
        //------------------------------new add page num ------------
        if(null != mCategory) data.put("blogCategoryId",mCategory);
        data.put("start",mDataList.size()+"");
        data.put("end",(mDataList.size()+sPageCount-1)+"");
        //------------------------------new add end -------------------
        return data;
    }

    @Override
    public void init() {
        UiMainUtils.setNavigationTintColor(this , R.color.main_green);
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
                switch (viewType){
                    case ChannelBlogViewHolder.ITEM_TYPE_TOP://置顶博文
                    case ChannelBlogViewHolder.ITEM_TYPE_HOT://热门博文
                    case ChannelBlogViewHolder.ITEM_TYPE_ALL://全部博文
                        viewHolder =  new MyBlogViewHolder(LayoutInflater.from(parent.getContext()).inflate( R.layout.item_my_blog_post , parent ,false));
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
                switch (mAdapter.getItemViewType(position)){
                    case BaseRecyclerAdapter.TYPE_FOOTER://加载更多
//                        showMore();
//                        requestData();
                        break;
                    default:
                        if(v.getId() == R.id.sdv_pic){
                            //1.自己的信息跳转到首页-"我的"
                            if(data.getBaseUserId().equals(mUserInfo.getBaseUserId())){
                                MainActivity.start(MyBlogActivity.this , mUserInfo , 2);
                            }else{//2.访客
                                PublicUserActivity.start(MyBlogActivity.this , data.getBaseUserId());
                            }
                        }else{
                            BlogPostDetailActivity.start(MyBlogActivity.this,data.getBlogId(),BlogPostDetailActivity.FROM_TYPE_PERSON);
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
//        this.enableLoadMore(mRecyclerView);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mFilterFragment = CategoryFilterFragment.create(mFilterType,mNativeUserInfo.getBaseUserId(),"");
        ft.replace(R.id.fl_filter, mFilterFragment);
        ft.commit();

        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener(){
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

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG , response.toString());
        if(null == mRecyclerView ) return;
        if(isRefreshing) mDataList.clear();
        mRecyclerView.setRefreshing(false);
        mAdapter.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        MyBlogPostList blogPostList = new Gson().fromJson(response.toString(),MyBlogPostList.class);
        if(null != blogPostList){
            if(blogPostList.getBlogList() != null && blogPostList.getBlogList().size()>0){
                List<BlogPost> topList = new ArrayList<>();
                List<BlogPost> allList = new ArrayList<>();
                for(BlogPost blogPost : blogPostList.getBlogList()){
                    if("Y".equals(blogPost.getTopFlag())){
                        //置顶博文
                        blogPost.setBaseViewHoldType(ChannelBlogViewHolder.ITEM_TYPE_TOP);
                        blogPost.setBaseTitle(blogPost.getBlogTitle());
                        topList.add(blogPost);
                    }else{
                        blogPost.setBaseViewHoldType(ChannelBlogViewHolder.ITEM_TYPE_ALL);
                        allList.add(blogPost);
                    }
                }
                if(topList.size()>0) mDataList.addAll(topList);
                if(allList.size()>0) mDataList.addAll(allList);
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

        //more .
        if(mDataList.size() < blogPostList.getTotal()){
            showMore();
        }else{
            hideMore();
        }
    }

    @Override
    public void onFailure(Throwable error) {
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


    private void showMore(){
        mAdapter.setHasMoreData(true);
        mAdapter.notifyDataSetChanged();
    }

    private void hideMore() {
        mAdapter.setHasMoreData(false);
        mAdapter.notifyDataSetChanged();
    }
    //刷新数据
    private void refresh() {
        if(null != mRefreshLayout) mRefreshLayout.setRefreshing(true);
        mRecyclerView.setRefreshing(true);
        requestData(true);
    }

    public static void start(Context from ,UserInfo userInfo){
        Intent intent = new Intent(from,MyBlogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra(EXTRA_DATA , userInfo);
        from.startActivity(intent);
        UIUtils.addEnterAnim((Activity) from);
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
