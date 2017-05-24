package com.codyy.erpsportal.groups.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleBisectDivider;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.viewholders.TitleItemViewHolder;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleRecyclerView;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.activities.ClassMemberActivity;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.activities.UserTimeTableActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolderBuilder;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.ClassResourceViewHolder;
import com.codyy.erpsportal.exam.controllers.activities.classroom.ExamClassSpaceActivity;
import com.codyy.erpsportal.homework.controllers.activities.WorkListsClassSpaceActivity;
import com.codyy.erpsportal.commons.controllers.fragments.filters.SimpleListFilterFragment;
import com.codyy.erpsportal.groups.controllers.viewholders.GroupAnnounceViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.ClassSpaceViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.MyClassBlogViewHolder;
import com.codyy.erpsportal.commons.models.entities.my.ClassCont;
import com.codyy.erpsportal.commons.models.entities.filter.FilterEntity;
import com.codyy.erpsportal.commons.models.entities.my.ClassResource;
import com.codyy.erpsportal.commons.models.entities.my.MyAnnounce;
import com.codyy.erpsportal.commons.models.entities.my.MyBaseTitle;
import com.codyy.erpsportal.commons.models.entities.my.MyBlog;
import com.codyy.erpsportal.commons.models.entities.my.MyClassRoom;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.resource.controllers.activities.ClassResourcesActivity;
import com.codyy.erpsportal.resource.controllers.activities.ImageDetailsActivity;
import com.codyy.erpsportal.resource.controllers.activities.VideoDetailsActivity;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

/**
 * 我的-班级空间
 * Created by poe on 16-2-29.
 */
public class ClassSpaceActivity extends BaseHttpActivity implements BaseRecyclerAdapter.OnItemClickListener {
    private final static String TAG = "ClassSpaceActivity";
    public final static String EXTRA_CLASS_ID = "com.class.id";//
    public final static String EXTRA_TITLE = "com.class.title";//
    public final static String EXTRA_CLASS_LIST = "com.class.list";//
    /**
     * 班级空间-基础信息item
     */
    public final static int ITEM_TYPE_SPACE_BASE = 0x200;
    /**
     * 班级空间-公告item
     */
    public final static int ITEM_TYPE_SPACE_ANNOUNCE = 0x201;
    /**
     * 班级空间-优课资源
     */
    public final static int ITEM_TYPE_SPACE_RESOURCE_SHARE = 0x204;
    /**
     * 班级空间-博文
     */
    public final static int ITEM_TYPE_SPACE_BLOG_POST = 0x205;

    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.empty_view)EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    @Bind(R.id.drawer_layout)DrawerLayout mDrawerLayout;
    @Bind(R.id.forbidden_frame_layout)FrameLayout mForbiddenFrameLayout;

    private ArrayList<MyBaseTitle> mTitleList = new ArrayList<>();//模块标题 .
    private List<BaseTitleItemBar> mDataList = new ArrayList<>();
    private BaseRecyclerAdapter<BaseTitleItemBar, BaseRecyclerViewHolder<BaseTitleItemBar>> mAdapter;
    private String mClassId;
    private String mClassTitle;
    private SimpleListFilterFragment mFilterFragment;
    private MyClassRoom mClassRoom;
    private List<ClassCont> mData;//班级信息集合

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_single_recycler_view;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_MY_CLASSROOM_INFO;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if (null != mClassId) data.put("classId", mClassId);
        return data;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG, response.toString());
        if (null == mRecyclerView || null == mRefreshLayout) return;
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mRecyclerView.setEnabled(true);
        mRecyclerView.setRefreshing(false);
        mEmptyView.setLoading(false);
        mDataList.clear();
        mTitleList.clear();
        mClassRoom = new Gson().fromJson(response.toString(), MyClassRoom.class);
        makeConstruction(mClassRoom);
        //set adapter
        mAdapter.setData(mDataList);
        if (mDataList.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        //init the filter more fragment
        initMenu();
        initFilterFragment();
    }

    @Override
    public void onFailure(Throwable error) {
        if (null == mRecyclerView || null == mRefreshLayout) return;
        mRecyclerView.setEnabled(true);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mRecyclerView.setRefreshing(false);
        if (mDataList.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    /**
     * 制造结构化的数据
     *
     * @param myClassRoom
     */
    private void makeConstruction(MyClassRoom myClassRoom) {
        if (null != myClassRoom) {
            //基本信息
            myClassRoom.setBaseViewHoldType(ITEM_TYPE_SPACE_BASE);
            mClassTitle = myClassRoom.getClassName();
            mDataList.add(myClassRoom);
            MyAnnounce announce = myClassRoom.getAnnouncement();
            if (announce != null && !TextUtils.isEmpty(announce.getContent())) {
                //公告
                mDataList.add(new BaseTitleItemBar(getString(R.string.announcement), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                String announceContent = announce.getContent();
                if (announceContent == null) {
                    announceContent = "";
                } else {
                    int nIndex = announceContent.indexOf('\n');
                    if (nIndex > 0) {
                        announceContent = announceContent.substring(0, nIndex);
                    }
                }
                mDataList.add(new BaseTitleItemBar(announceContent,
                        ITEM_TYPE_SPACE_ANNOUNCE));
            } else {
                mDataList.add(new BaseTitleItemBar(getString(R.string.announcement), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
            }

            List<MyBaseTitle> titles = myClassRoom.getBlogTitleList();
            String blogTitle = "博文";
            String resourceTitle = "优课资源";

            if (null != titles && titles.size() > 0) {
                mTitleList.clear();
                for (MyBaseTitle baseTitle : titles) {
                    if ("blog.id".equals(baseTitle.getBaseMenuId())) {
                        blogTitle = baseTitle.getMenuName();
                    }
                    if ("resource.id".equals(baseTitle.getBaseMenuId())) {
                        resourceTitle = baseTitle.getMenuName();
                    }
                    mTitleList.add(baseTitle);
                }
            }
            //博文
            List<MyBlog> blogList = myClassRoom.getBlogPostList();
            if (blogList != null) {
                if (blogList.size() > 0) {
                    //add the title .....
                    mDataList.add(new BaseTitleItemBar(blogTitle, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                    for (int i = 0; i < blogList.size(); i++) {
                        MyBlog mpl = blogList.get(i);
                        mpl.setBaseViewHoldType(ITEM_TYPE_SPACE_BLOG_POST);
                        mDataList.add(mpl);
                    }
                } else {
                    //no more data .
                    mDataList.add(new BaseTitleItemBar(blogTitle, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                }
            }

            //资源
            List<ClassResource> classResource = myClassRoom.getResourceList();
            if (null != classResource) {
                if (classResource.size() > 0) {
                    //add the title .....
                    mDataList.add(new BaseTitleItemBar(resourceTitle, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                    for (int i = 0; i < classResource.size(); i++) {
                        ClassResource mpl = classResource.get(i);
                        mpl.setBaseViewHoldType(ITEM_TYPE_SPACE_RESOURCE_SHARE);
                        mDataList.add(mpl);
                    }
                } else {
                    //no more data .
                    mDataList.add(new BaseTitleItemBar(resourceTitle, TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                }
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
//        requestData(true);
        checkForbidden();
    }

    private void checkForbidden() {
        HashMap<String,String> param = new HashMap<>();
        param.put("accountId",mClassId);
        param.put("accountType","CLASS");

        requestData(URLConfig.CHECK_USER_FORBIDDEN, param, true, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response, boolean isRefreshing) throws Exception {
                if(!TextUtils.isEmpty(response.toString()) && "true".equals(response.optString("result"))){
                    mForbiddenFrameLayout.setVisibility(View.GONE);
                    mRefreshLayout.setRefreshing(true);
                    requestData(true);
                }else{
                    mForbiddenFrameLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {
                if (mDataList.size() <= 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        });

    }

    public void init() {
        UiMainUtils.setNavigationTintColor(this, R.color.main_green);
        mClassId = getIntent().getStringExtra(EXTRA_CLASS_ID);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        mData = getIntent().getParcelableArrayListExtra(EXTRA_CLASS_LIST);
        mTitleTextView.setText(title);
        initToolbar(mToolBar);
        //用户默认没有被禁用.
        mForbiddenFrameLayout.setVisibility(View.GONE);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                requestData(true);
            }
        });
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        mRecyclerView.addItemDecoration(new SimpleBisectDivider(divider,(int)getResources().getDimension(R.dimen.poe_recycler_grid_layout_padding), new SimpleBisectDivider.IGridLayoutViewHolder() {
            @Override
            public int obtainSingleBigItemViewHolderType() {
                return -1;
            }

            @Override
            public int obtainMultiInLineViewHolderType() {
                return ITEM_TYPE_SPACE_RESOURCE_SHARE;
            }
        }));
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新

                mRecyclerView.setEnabled(false);
                mRecyclerView.setRefreshing(true);
                requestData(true);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter.getItemViewType(position) == ITEM_TYPE_SPACE_RESOURCE_SHARE) {
                    return 1;
                }
                return 2;
            }
        });
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder<BaseTitleItemBar>>() {
            @Override
            public BaseRecyclerViewHolder<BaseTitleItemBar> createViewHolder(ViewGroup parent, int viewType) {
                BaseRecyclerViewHolder viewHolder = null;
                switch (viewType) {
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE://
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA://标题 + NO DATA TIPS .仅标题
                        viewHolder = TitleItemViewHolderBuilder.getInstance().constructTitleItem(
                                parent.getContext(), parent, TitleItemViewHolderBuilder.ITEM_TIPS_SIMPLE_TEXT);
                        break;
                    case ITEM_TYPE_SPACE_BASE:
                        viewHolder = new ClassSpaceViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(
                                parent.getContext(), R.layout.item_class_space));
                        break;
                    case ITEM_TYPE_SPACE_ANNOUNCE:
                        viewHolder = new GroupAnnounceViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(
                                parent.getContext(), R.layout.item_group_space_announce));
                        break;
                    case ITEM_TYPE_SPACE_BLOG_POST://博文
                        viewHolder = new MyClassBlogViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(
                                parent.getContext(), R.layout.item_group_blog_post));
                        break;
                    case ITEM_TYPE_SPACE_RESOURCE_SHARE://资源
                        // TODO: 16-8-16 new resource ViewHolder .．．　
                        viewHolder = new ClassResourceViewHolder(LayoutInflater.from(ClassSpaceActivity.this).inflate(R.layout.item_class_space_resource, parent, false));
                        break;
                }
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
                return mDataList.get(position).getBaseViewHoldType();
            }
        });

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<BaseTitleItemBar>() {
            @Override
            public void onItemClicked(View v, int position, BaseTitleItemBar data) {
                switch (mAdapter.getItemViewType(position)) {
                    case ITEM_TYPE_SPACE_BLOG_POST://博文
                        MyBlog myBlog = (MyBlog) data;
                        if (v.getId() == R.id.sdv_pic) {
                            //1.自己的信息跳转到首页-"我的"
                            if (myBlog.getBaseUserId().equals(mUserInfo.getBaseUserId())) {
                                MainActivity.start(ClassSpaceActivity.this, mUserInfo, 2);
                            } else {//2.访客
                                PublicUserActivity.start(ClassSpaceActivity.this, myBlog.getBaseUserId());
                            }
                        } else {
                            BlogPostDetailActivity.start(ClassSpaceActivity.this, myBlog.getBlogId(), BlogPostDetailActivity.FROM_TYPE_PERSON);
                        }
                        break;
                    case ITEM_TYPE_SPACE_RESOURCE_SHARE:
                        ClassResource classResource = (ClassResource) data;
                        if ("image".equals(classResource.getType())) {
                            ImageDetailsActivity.start(ClassSpaceActivity.this, mUserInfo, classResource.getResourceId(), mClassId);
                        } else {
                            VideoDetailsActivity.start(ClassSpaceActivity.this, mUserInfo, classResource.getResourceId(), mClassId);
                        }
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);

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

//        initMenu();
    }

    private void initMenu() {
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

    private void initFilterFragment() {
        if (mFilterFragment == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            mFilterFragment = new SimpleListFilterFragment();
            Bundle bd = new Bundle();
            bd.putParcelableArrayList(SimpleListFilterFragment.EXTRA_FILTER_TITLES, mTitleList);
            mFilterFragment.setArguments(bd);
            ft.replace(R.id.fl_filter, mFilterFragment);
            ft.commit();
        }
    }

    /**
     * @param context
     * @param title
     * @param classId
     */
    public static void start(Context context, String title, String classId, List<ClassCont> classCont, UserInfo userInfo) {
        Intent intent = new Intent(context, ClassSpaceActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_CLASS_ID, classId);
        intent.putParcelableArrayListExtra(EXTRA_CLASS_LIST, (ArrayList<? extends Parcelable>) classCont);
        intent.putExtra(Constants.USER_INFO,userInfo);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }

    @Override
    public void onItemClicked(View v, int position, Object data) {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }

        if (null == data) return;
        FilterEntity fe = (FilterEntity) data;

        if ("resource.id".equals(fe.getId())) {
            ClassResourcesActivity.start(this, mUserInfo, mClassId);
        }

        if ("homework.id".equals(fe.getId())) {
            WorkListsClassSpaceActivity.startActivity(this, mClassId,mClassTitle);
        }

        if ("test.id".equals(fe.getId())) {
            //测试
            ExamClassSpaceActivity.start(ClassSpaceActivity.this, mClassId, fe.getName());
        }

        if ("schedule.id".equals(fe.getId())) {
            //课表
            UserTimeTableActivity.start(ClassSpaceActivity.this, mUserInfo, mClassId, fe.getName(), UserTimeTableActivity.ROOM_TYPE_CLASS);
        }

        if ("class.member.id".equals(fe.getId())) {
            //成员
            ClassMemberActivity.start(ClassSpaceActivity.this, mUserInfo, mClassId, mClassRoom.getClassName(), mData , ClassMemberActivity.TYPE_FROM_MY);
        }

        if ("blog.id".equals(fe.getId())) {
            //博文
            ClassBlogActivity.start(ClassSpaceActivity.this, mClassId);
        }
    }
}
