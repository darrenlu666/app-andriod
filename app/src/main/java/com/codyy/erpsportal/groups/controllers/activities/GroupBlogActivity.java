package com.codyy.erpsportal.groups.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolderBuilder;
import com.codyy.erpsportal.commons.controllers.fragments.filters.CategoryFilterFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.ChannelBlogViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.GroupChildrenBlogViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.TopTextViewHolder;
import com.codyy.erpsportal.commons.models.entities.blog.BlogPost;
import com.codyy.erpsportal.commons.models.entities.blog.GroupBlogList;
import com.codyy.erpsportal.commons.models.entities.blog.GroupBlogParse;
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
 * 圈组博文列表（圈组空间）
 * Created by poe on 16-3-2.
 */
public class GroupBlogActivity extends BaseHttpActivity {
    private static  final String TAG = "GroupBlogActivity";
    private static String EXTRA_ID = "groupId";
    private static String EXTRA_TYPE = "type";
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.empty_view)EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    @Bind(R.id.drawer_layout)DrawerLayout mDrawerLayout;
    private String mGroupId;//传递值-班级id
    private List<BlogPost> mDataList = new ArrayList<>();
    private BaseRecyclerAdapter<BlogPost,BaseRecyclerViewHolder<BlogPost>> mAdapter ;
    private String mGroupType = CategoryFilterFragment.CATEGORY_TYPE_GROUP;//个人或者 门户

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_single_recycler_view;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_HOME_GROUP_BLOG_POST;
    }

    @Override
    public HashMap<String, String> getParam() {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if(null != mGroupId) data.put("groupId",mGroupId);
        return data;
    }

    @Override
    public void init() {
        UiMainUtils.setNavigationTintColor(this , R.color.main_green);
        mGroupId    =   getIntent().getStringExtra(EXTRA_ID);
        mGroupType  =   getIntent().getStringExtra(EXTRA_TYPE);
        mTitleTextView.setText(getString(R.string.group_blog_title));
        initToolbar(mToolBar);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                mRecyclerView.setEnabled(false);
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
            public BaseRecyclerViewHolder<BlogPost> createViewHolder(ViewGroup parent, int viewType) {
                BaseRecyclerViewHolder viewHolder = null;
                switch (viewType){
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE:
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA:
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE:
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA:
                        viewHolder = TitleItemViewHolderBuilder.getInstance().constructTitleItem(
                                parent.getContext(),parent,TitleItemViewHolderBuilder.ITEM_TIPS_SIMPLE_TEXT);
                        break;
                    case ChannelBlogViewHolder.ITEM_TYPE_TOP://置顶博文
                        viewHolder = new TopTextViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),R.layout.text_line_item));
                        break;
                    case ChannelBlogViewHolder.ITEM_TYPE_HOT://热门博文
                    case ChannelBlogViewHolder.ITEM_TYPE_ALL://全部博文
                        viewHolder =  new GroupChildrenBlogViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_group_child_blog_post,parent ,false));
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
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE:
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA:
                        if(v.getId() == R.id.btn_more){
                            GroupBlogMoreActivity.start(GroupBlogActivity.this , mGroupType , mGroupId);
                        }
                        break;
                    case ChannelBlogViewHolder.ITEM_TYPE_TOP://置顶博文
                        //博文详情跳转...
                        BlogPostDetailActivity.startFromGroup(GroupBlogActivity.this,data.getBlogId(),mGroupId);
                        break;
                    case ChannelBlogViewHolder.ITEM_TYPE_HOT://热门博文
                    case ChannelBlogViewHolder.ITEM_TYPE_ALL://全部博文
                        if(v.getId() == R.id.sdv_pic){
                            //1.自己的信息跳转到首页-"我的"
                            if(data.getBaseUserId().equals(mUserInfo.getBaseUserId())){
                                MainActivity.start(GroupBlogActivity.this , mUserInfo , 2);
                            }else{//2.访客
                                PublicUserActivity.start(GroupBlogActivity.this , data.getBaseUserId());
                            }
                        }else{
                            BlogPostDetailActivity.startFromGroup(GroupBlogActivity.this,data.getBlogId(),mGroupId);
                        }
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        requestData();
    }

    @Override
    public void onSuccess(JSONObject response) {
        Cog.d(TAG , response.toString());
        if(null == mRecyclerView ) return;
        mRecyclerView.setEnabled(true);
        mRecyclerView.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mEmptyView.setLoading(false);

        GroupBlogParse parse = new Gson().fromJson(response.toString() , GroupBlogParse.class);
        if(null != parse && parse.getDataList() != null){
            GroupBlogList blogPostList = parse.getDataList() ;
            mDataList.clear();
            //置顶博文
            if(blogPostList.getTopBlogList() != null && blogPostList.getTopBlogList().size()>0){
                for(BlogPost blogPost : blogPostList.getTopBlogList()){
                    blogPost.setBaseTitle(blogPost.getBlogTitle());
                    blogPost.setBaseViewHoldType(ChannelBlogViewHolder.ITEM_TYPE_TOP);
                    mDataList.add(blogPost);
                }
            }
            //热门博文
            if(blogPostList.getHotBloglist() != null && blogPostList.getHotBloglist().size()>0){
                mDataList.add(new BlogPost(getString(R.string.blog_hot), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                for(BlogPost blogPost : blogPostList.getHotBloglist()){
                    blogPost.setBaseViewHoldType(ChannelBlogViewHolder.ITEM_TYPE_HOT);
                    mDataList.add(blogPost);
                }
            }else{
                //add no more text desc item .
                mDataList.add(new BlogPost(getString(R.string.blog_hot), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }
            //全部博文
            if(blogPostList.getGroupBloglist() != null && blogPostList.getGroupBloglist().size()>0){
                mDataList.add(new BlogPost(getString(R.string.blog_all), TitleItemViewHolder.ITEM_TYPE_TITLE_MORE));
                for(BlogPost blogPost : blogPostList.getGroupBloglist()){
                    blogPost.setBaseViewHoldType(ChannelBlogViewHolder.ITEM_TYPE_ALL);
                    mDataList.add(blogPost);
                }
            }else{
                //add no more text desc item .
                mDataList.add(new BlogPost(getString(R.string.blog_all), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }
        }

        mAdapter.setData(mDataList);
        if(mDataList.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
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
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
    }
    //刷新数据
    private void refresh() {
        mDataList.clear();
        mAdapter.setHasMoreData(false);
        requestData();
    }

    /**
     * @param from from
     * @param groupId groupId
     * @param filterType {@link CategoryFilterFragment#CATEGORY_TYPE_DOOR,CategoryFilterFragment#CATEGORY_TYPE_PERSON}
     */
    public static void start(Context from , String groupId ,String filterType){
        Intent intent = new Intent(from,GroupBlogActivity.class);
        intent.putExtra(EXTRA_ID , groupId);
        intent.putExtra(EXTRA_TYPE , filterType);
        from.startActivity(intent);
        UIUtils.addEnterAnim((Activity) from);
    }
}
