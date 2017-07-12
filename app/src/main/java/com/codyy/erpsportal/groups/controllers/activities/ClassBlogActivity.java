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

import com.codyy.erpsportal.R;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.viewholders.TitleItemViewHolder;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleHorizonDivider;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleRecyclerView;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolderBuilder;
import com.codyy.erpsportal.groups.controllers.viewholders.ChannelBlogViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.ClassBlogViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.MoreViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.TopTextViewHolder;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.models.entities.blog.BlogPost;
import com.codyy.erpsportal.commons.models.entities.blog.ClassBlogList;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;

/**
 * 班级博文列表
 * Created by poe on 16-3-2.
 */
public class ClassBlogActivity extends BaseHttpActivity {
    private static  final String TAG = "ClassBlogActivity";
    private static String EXTRA_ID = "class.blog.classId";
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.empty_view)EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    @Bind(R.id.drawer_layout)DrawerLayout mDrawerLayout;
    private String mClassId;//传递值-班级id
    private List<BlogPost> mDataList = new ArrayList<>();
    /**
     * 全部博文
     */
    private List<BlogPost> mAllBlogList = new ArrayList<>();
    private BaseRecyclerAdapter<BlogPost,BaseRecyclerViewHolder<BlogPost>> mAdapter ;

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_single_recycler_view;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_CLASS_BLOG_LIST;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if(null != mClassId) data.put("classId",mClassId);
        data.put("start",mDataList.size()+"");
        data.put("end",(mDataList.size()+2)+"");
        return data;
    }

    @Override
    public void init() {
        mClassId    =   getIntent().getStringExtra(EXTRA_ID);
        mTitleTextView.setText(getString(R.string.class_blog_title));
        initToolbar(mToolBar);

        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
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
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE:
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA:
                        viewHolder = TitleItemViewHolderBuilder.getInstance().constructTitleItem(parent.getContext(),
                                parent,TitleItemViewHolderBuilder.ITEM_TIPS_SIMPLE_TEXT);
                        break;
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE:
                        viewHolder = new MoreViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_class_blog_more,parent,false));
                        break;
                    case ChannelBlogViewHolder.ITEM_TYPE_TOP://置顶博文
                        viewHolder = new TopTextViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.text_line_item,parent,false));
                        break;
                    case ChannelBlogViewHolder.ITEM_TYPE_HOT://热门博文
                    case ChannelBlogViewHolder.ITEM_TYPE_ALL://全部博文
                        viewHolder =  new ClassBlogViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_class_blog_post,parent,false));
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
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE:
//                        mDataList.remove(mDataList.size()-1);
//                        showMore();
//                        loadMoreData();
                        break;
                    case ChannelBlogViewHolder.ITEM_TYPE_TOP://置顶博文
                        //博文详情跳转...
                        BlogPost blogPost = data;
                        BlogPostDetailActivity.start(ClassBlogActivity.this,blogPost.getBlogId(),BlogPostDetailActivity.FROM_TYPE_PERSON);
                        break;
                    case ChannelBlogViewHolder.ITEM_TYPE_HOT://热门博文
                    case ChannelBlogViewHolder.ITEM_TYPE_ALL://全部博文
                        if(v.getId() == R.id.sdv_pic){
                            //1.自己的信息跳转到首页-"我的"
                            if(data.getBaseUserId().equals(mUserInfo.getBaseUserId())){
                                MainActivity.start(ClassBlogActivity.this , mUserInfo , 2);
                            }else{//2.访客
                                PublicUserActivity.start(ClassBlogActivity.this , data.getBaseUserId());
                            }
                        }else{
                            BlogPostDetailActivity.start(ClassBlogActivity.this,data.getBlogId(),BlogPostDetailActivity.FROM_TYPE_PERSON);
                        }
                        break;
                }
            }
        });
        mAdapter.setOnLoadMoreClickListener(new BaseRecyclerAdapter.OnLoadMoreClickListener() {
            @Override
            public void onMoreData() {
                loadMoreData();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
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
        if(null == mRecyclerView || null == mRefreshLayout) return;
        if(isRefreshing) mDataList.clear();
        mRecyclerView.setRefreshing(false);
        mAdapter.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mEmptyView.setLoading(false);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                requestData(true);
            }
        });

        ClassBlogList blogPostList = new Gson().fromJson(response.toString(),ClassBlogList.class);
        if(null != blogPostList){
            mAllBlogList.clear();
            mDataList.clear();
            //置顶博文
            if(blogPostList.getTopBlogList() != null && blogPostList.getTopBlogList().size()>0){
                for(BlogPost blogPost : blogPostList.getTopBlogList()){
                    blogPost.setBaseViewHoldType(ChannelBlogViewHolder.ITEM_TYPE_TOP);
                    blogPost.setBaseTitle(blogPost.getBlogTitle());
                    mDataList.add(blogPost);
                }
            }
            //热门博文
            if(blogPostList.getHotBlogList() != null && blogPostList.getHotBlogList().size()>0){
                mDataList.add(new BlogPost(getString(R.string.blog_hot), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                for(BlogPost blogPost : blogPostList.getHotBlogList()){
                    blogPost.setBaseViewHoldType(ChannelBlogViewHolder.ITEM_TYPE_HOT);
                    mDataList.add(blogPost);
                }
            }else{
                //add no more text desc item .
                mDataList.add(new BlogPost(getString(R.string.blog_hot), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }

            //全部博文
            if(blogPostList.getAllBlogList() != null && blogPostList.getAllBlogList().size()>0){
                mDataList.add(new BlogPost(getString(R.string.blog_all), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                for(BlogPost blogPost : blogPostList.getAllBlogList()){
                    blogPost.setBaseViewHoldType(ChannelBlogViewHolder.ITEM_TYPE_ALL);
                    mDataList.add(blogPost);
                    mAllBlogList.add(blogPost);
                }
//                mDataList.add(new BlogPost(getString(R.string.more), TitleItemViewHolder.ITEM_TYPE_TITLE_MORE));
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
            if(mAllBlogList.size()>0){
                mAdapter.setHasMoreData(true);
            }
        }
    }

    @Override
    public void onFailure(Throwable error) {
        if(null == mRecyclerView || null == mRefreshLayout) return;
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
        mRecyclerView.setRefreshing(true);
        hideMore();
        requestData(true);
    }

    /**
     * 加载更多博文
     */
    private void loadMoreData() {
        if(null == mSender) return;
        // TODO: 16-3-13 重新输入 start & end ...
        HashMap hashMap = getParam(false);
        hashMap.put("start",mAllBlogList.size()+"");
        hashMap.put("end",(mAllBlogList.size()+sPageCount-1)+"");

        requestData(URLConfig.GET_CLASS_BLOG_LIST_MORE, hashMap, false,new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                Cog.d(TAG , response.toString());
                if(null == mRecyclerView ) return;
                ClassBlogList blogPostList = new Gson().fromJson(response.toString(),ClassBlogList.class);
                if(null != blogPostList){
                    //全部博文
                    if(blogPostList.getAllBlogList() != null && blogPostList.getAllBlogList().size()>0){
                        for(BlogPost blogPost : blogPostList.getAllBlogList()){
                            blogPost.setBaseViewHoldType(ChannelBlogViewHolder.ITEM_TYPE_ALL);
                            mDataList.add(blogPost);
                            mAllBlogList.add(blogPost);
                        }
//                        mDataList.add(new BlogPost(getString(R.string.more), TitleItemViewHolder.ITEM_TYPE_TITLE_MORE));
                        mAdapter.setData(mDataList);
                        mAdapter.setHasMoreData(true);
                    }else{
                        mAdapter.setHasMoreData(false);
                    }
                }

            }

            @Override
            public void onRequestFailure(Throwable error) {
                ToastUtil.showToast(error.getMessage());
                LogUtils.log(error);
                if(null == mRecyclerView ) return;
                hideMore();
            }
        });

    }



    private void hideMore() {
        mAdapter.setHasMoreData(false);
        mAdapter.notifyDataSetChanged();
    }

    public static void start(Context from , String classId){
        Intent intent = new Intent(from,ClassBlogActivity.class);
        intent.putExtra(EXTRA_ID , classId);
        from.startActivity(intent);
        UIUtils.addEnterAnim((Activity) from);
    }
}
