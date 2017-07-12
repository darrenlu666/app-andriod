package com.codyy.erpsportal.groups.controllers.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.viewholders.TitleItemViewHolder;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleHorizonDivider;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleRecyclerView;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.fragments.channels.BaseHttpFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolderBuilder;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.erpsportal.groups.controllers.activities.BlogPostDetailActivity;
import com.codyy.erpsportal.groups.controllers.activities.MoreBlogPostsActivity;
import com.codyy.erpsportal.commons.controllers.fragments.filters.CategoryFilterFragment;
import com.codyy.erpsportal.groups.controllers.viewholders.ChannelBlogViewHolder;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.blog.BlogPost;
import com.codyy.erpsportal.commons.models.entities.blog.BlogPostList;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;

/**
 * 频道-博文类
 * @author poe 2016/1/5
 */
public class ChannelBlogPostFragment extends BaseHttpFragment implements ConfigBus.OnModuleConfigListener {
    private final static String TAG = "ChannelBlogPostFragment";
    @Bind(R.id.empty_view) EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)RefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    private List<BlogPost> mBlogList = new ArrayList<>();
    private BaseRecyclerAdapter<BlogPost,BaseRecyclerViewHolder<BlogPost>> mAdapter ;

    private String mBaseAreaId;
    private String mSchoolId;
    private UserInfo mUserInfo;

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_single_recycleview;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_HOME_BLOG_POST;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        data.put("baseAreaId", mBaseAreaId);
        data.put("schoolId", mSchoolId);
        return data;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG , response.toString());
        if(null == mRecyclerView ) return;
        if(isRefreshing)  mBlogList.clear();
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }

        BlogPostList blogPostList = new Gson().fromJson(response.toString(),BlogPostList.class);
        if(null != blogPostList){
            mBlogList.clear();
            //推荐博文
            if(blogPostList.getShareBlogList() != null && blogPostList.getShareBlogList().size()>0){
                mBlogList.add(new BlogPost(Titles.sPagetitleBlogRecommend, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                for(BlogPost blogPost : blogPostList.getShareBlogList()){
                    blogPost.setBaseViewHoldType(ChannelBlogViewHolder.ITEM_TYPE_RECOMMEND);
                    mBlogList.add(blogPost);
                }
            }else{
                //add no more text desc item .
                mBlogList.add(new BlogPost(Titles.sPagetitleBlogRecommend, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }
            //热门博文
            if(blogPostList.getHotBlogList() != null && blogPostList.getHotBlogList().size()>0){
                mBlogList.add(new BlogPost(Titles.sPagetitleBlogHot, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                for(BlogPost blogPost : blogPostList.getHotBlogList()){
                    blogPost.setBaseViewHoldType(ChannelBlogViewHolder.ITEM_TYPE_HOT);
                    mBlogList.add(blogPost);
                }
            }else{
                //add no more text desc item .
                mBlogList.add(new BlogPost(Titles.sPagetitleBlogHot, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }
            //全部分类
            if(blogPostList.getAllBlogList() != null && blogPostList.getAllBlogList().size()>0){
                mBlogList.add(new BlogPost(Titles.sPagetitleBlogClassify, TitleItemViewHolder.ITEM_TYPE_TITLE_MORE));
                for(BlogPost blogPost : blogPostList.getAllBlogList()){
                    blogPost.setBaseViewHoldType(ChannelBlogViewHolder.ITEM_TYPE_ALL);
                    mBlogList.add(blogPost);
                }
            }else{
                //add no more text desc item .
                mBlogList.add(new BlogPost(Titles.sPagetitleBlogClassify, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }
        }

        mAdapter.setData(mBlogList);
        if(mBlogList.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(false);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(Throwable error) {
        if(null == mRecyclerView ) return;
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        if(mBlogList.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(false);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfigBus.register(this);
    }

    @Override
    public void onViewLoadCompleted() {
        super.onViewLoadCompleted();
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                mEmptyView.setLoading(true);
                requestData(true);
            }
        });
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        mRecyclerView.addItemDecoration(new SimpleHorizonDivider(divider));
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(true);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                    case ChannelBlogViewHolder.ITEM_TYPE_RECOMMEND:
                    case ChannelBlogViewHolder.ITEM_TYPE_HOT:
                    case ChannelBlogViewHolder.ITEM_TYPE_ALL:
                        //解决半边view无法点击
                        viewHolder =  new ChannelBlogViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_channel_blog_post,parent,false));
                        break;
                }
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
                return mBlogList.get(position).getBaseViewHoldType();
            }
        });
        //set listener .
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<BlogPost>() {
            @Override
            public void onItemClicked(View v, int position, BlogPost data) {
                switch (mAdapter.getItemViewType(position)){
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE:
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA:
                        if(v.getId() == R.id.btn_more){
                            MoreBlogPostsActivity.start(getActivity() , CategoryFilterFragment.CATEGORY_TYPE_DOOR , Titles.sPagetitleBlogClassify);
                        }
                        break;
                    case ChannelBlogViewHolder.ITEM_TYPE_RECOMMEND:
                    case ChannelBlogViewHolder.ITEM_TYPE_HOT:
                    case ChannelBlogViewHolder.ITEM_TYPE_ALL:
//                        BlogPostDetailActivity.start(getActivity(),data.getBlogId());
                        if(v.getId() == R.id.sdv_pic){
                            //click icon .
                            if(data.getBaseUserId().equals(mUserInfo.getBaseUserId())){
                                MainActivity.start(getActivity() , mUserInfo , 2);
                            }else{//2.访客
                                PublicUserActivity.start(getActivity() , data.getBaseUserId());
                            }
                        }else{
                            BlogPostDetailActivity.start(getActivity(),data.getBlogId(),BlogPostDetailActivity.FROM_TYPE_SHARE);
                        }
                        break;

                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onConfigLoaded(ModuleConfig config) {
        if(mRefreshLayout == null) return;
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mBaseAreaId = config.getBaseAreaId();
        mSchoolId = config.getSchoolId();
        mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConfigBus.unregister(this);
    }
}
