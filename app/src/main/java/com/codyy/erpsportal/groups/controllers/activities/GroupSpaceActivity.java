package com.codyy.erpsportal.groups.controllers.activities;

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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.activities.GroupCollectiveActivityDetail;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.perlcourseprep.controllers.activities.PersonalLesPrepContentActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleItemViewHolderBuilder;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.controllers.fragments.filters.CategoryFilterFragment;
import com.codyy.erpsportal.commons.controllers.fragments.filters.SimpleListFilterFragment;
import com.codyy.erpsportal.groups.controllers.viewholders.GroupAnnounceViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.GroupBlogViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.GroupCollectionPrepareViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.GroupPersonPrepareViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.GroupSpaceViewHolder;
import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;
import com.codyy.erpsportal.commons.models.entities.blog.GroupBlogPost;
import com.codyy.erpsportal.commons.models.entities.filter.FilterEntity;
import com.codyy.erpsportal.groups.models.entities.GroupPersonPrepare;
import com.codyy.erpsportal.groups.models.entities.GroupPrepareLesson;
import com.codyy.erpsportal.groups.models.entities.GroupResource;
import com.codyy.erpsportal.groups.models.entities.GroupSpace;
import com.codyy.erpsportal.groups.models.entities.GroupSpaceParse;
import com.codyy.erpsportal.groups.models.entities.ModuleBlog;
import com.codyy.erpsportal.groups.models.entities.ModuleParse;
import com.codyy.erpsportal.groups.models.entities.ModulePersonPrepare;
import com.codyy.erpsportal.groups.models.entities.ModulePrepareLesson;
import com.codyy.erpsportal.groups.models.entities.ModuleResource;
import com.codyy.erpsportal.commons.models.entities.my.MyBaseTitle;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.SoftKeyboardStateHelper;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.weibo.controllers.activities.WeiBoActivity;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleHorizonDivider;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleRecyclerView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 圈组空间
 * Created by poe on 16-1-18.
 */
public class GroupSpaceActivity extends BaseHttpActivity implements BaseRecyclerAdapter.OnItemClickListener, SoftKeyboardStateHelper.SoftKeyboardStateListener {
    private final static String TAG = "GroupSpaceActivity";
    public final static String EXTRA_GROUP_ID = "com.group.id";//
    public final static String EXTRA_TITLE = "com.group.title";//
    private static String EXTRA_TYPE = "type";
    /**
     * 圈组空间-基础信息item
     */
    public final static int ITEM_TYPE_GROUP_SPACE_BASE = 0x200;
    /**
     * 圈组空间-公告item
     */
    public final static int ITEM_TYPE_GROUP_SPACE_ANNOUNCE = 0x201;
    /**
     * 圈组空间-集体备课
     */
    public final static int ITEM_TYPE_GROUP_SPACE_COLLECTION_PREPARE = 0x202;
    /**
     * 圈组空间-个人备课
     */
    public final static int ITEM_TYPE_GROUP_SPACE_PERSON_PREPARE = 0x203;
    /**
     * 圈组空间-资源共享
     */
    public final static int ITEM_TYPE_GROUP_SPACE_RESOURCE_SHARE = 0x204;
    /**
     * 圈组空间-博文
     */
    public final static int ITEM_TYPE_GROUP_SPACE_BLOG_POST = 0x205;

    @Bind(R.id.lin_root_view)LinearLayout mRootView;
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.empty_view)EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    @Bind(R.id.drawer_layout)DrawerLayout mDrawerLayout;

    //new prompt input layout .
    @Bind(R.id.rlt_prompt_input)RelativeLayout mPromptRelativeLayout;//申请加入视图
    @Bind(R.id.iv_prompt_close)ImageView mClosePromptIv;//关闭申请输入框图标
    @Bind(R.id.tv_send_prompt)TextView mSendPromptTv;//发送按钮
    @Bind(R.id.edt_prompt_reason)EditText mInputEdt;

    private ArrayList<MyBaseTitle> mTitleList = new ArrayList<>();//模块标题 .
    private List<BaseTitleItemBar> mGroupList = new ArrayList<>();
    private BaseRecyclerAdapter<BaseTitleItemBar, BaseRecyclerViewHolder<BaseTitleItemBar>> mAdapter;
    private String mGroupId;
    private int mCount = 0;//圈组成员数
    private SimpleListFilterFragment mFilterFragment;
    private InputMethodManager mInputManager;
    private boolean mIsKeyBoardShow = false;
    private String mGroupType = CategoryFilterFragment.CATEGORY_TYPE_DOOR;
    private SoftKeyboardStateHelper mKeyboardHelper;
    private GroupSpace mGroupSpace;


    @Override
    public int obtainLayoutId() {
        return R.layout.activity_group_space;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_GROUP_SPACE_DETAIL;
    }

    @Override
    public HashMap<String, String> getParam() {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if (null != mGroupId) data.put("groupId", mGroupId);
        return data;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG, response.toString());
        if (null == mRecyclerView || null == mRefreshLayout) return;
        if(isRefreshing) {
            mGroupList.clear();
            mTitleList.clear();
        }
        mRecyclerView.setEnabled(true);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mEmptyView.setLoading(false);
        GroupSpaceParse groupSpaceParse = new Gson().fromJson(response.toString(), GroupSpaceParse.class);
        makeConstruction(groupSpaceParse);
        //set adapter
        mAdapter.setData(mGroupList);
        if (mGroupList.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
        //init the filter more fragment .
        initFilterFragment();
    }

    /**
     * 制造结构化的数据
     *
     * @param groupSpaceParse groupSpaceParse
     */
    private void makeConstruction(GroupSpaceParse groupSpaceParse) {
        GroupSpace groupSpace = groupSpaceParse.getGroupSpace();
        mGroupSpace =   groupSpace;
        if (null != groupSpace) {
            //update the operate action .
            if (TextUtils.isEmpty(groupSpace.getApproveStatus())) {
                groupSpace.setOperateType(GroupSpace.GROUP_OPERATE_TYPE_PROMPT);
            } else if ("APPROVED".equals(groupSpace.getApproveStatus())) {
                groupSpace.setOperateType(GroupSpace.GROUP_OPERATE_TYPE_EXIT);
            } else if ("REJECT".equals(groupSpace.getApproveStatus())) {
                groupSpace.setOperateType(GroupSpace.GROUP_OPERATE_TYPE_PROMPT);
            } else if ("WAIT".equals(groupSpace.getApproveStatus())) {
                groupSpace.setOperateType(GroupSpace.GROUP_OPERATE_TYPE_WAIT);
            }
            //圈组基本信息
            groupSpace.setBaseViewHoldType(ITEM_TYPE_GROUP_SPACE_BASE);
            mCount = Integer.parseInt(groupSpace.getMemberCount());
            mGroupList.add(groupSpace);

            ModuleParse moduleParse = groupSpace.getModuleData();
            if (null != moduleParse) {
                //公告
                if (!TextUtils.isEmpty(groupSpace.getPublishNotice())) {
                    mGroupList.add(new BaseTitleItemBar(getString(R.string.announcement), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                    mGroupList.add(new BaseTitleItemBar(groupSpace.getPublishNotice(), ITEM_TYPE_GROUP_SPACE_ANNOUNCE));
                } else {
                    mGroupList.add(new BaseTitleItemBar(getString(R.string.announcement), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                }
                //集体备课
                ModulePrepareLesson modulePrepareLesson = moduleParse.getPrepareLesson();
                if (null != modulePrepareLesson) {
                    mTitleList.add(new MyBaseTitle(modulePrepareLesson.getGroupModuleId(), modulePrepareLesson.getModuleName()));
                    if (modulePrepareLesson.getData().size() > 0) {
                        //add the title .....
                        mGroupList.add(new BaseTitleItemBar(getString(R.string.collective_prepare_lessons), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                        for (int i = 0; i < modulePrepareLesson.getData().size(); i++) {
                            GroupPrepareLesson mpl = modulePrepareLesson.getData().get(i);
                            mpl.setBaseViewHoldType(ITEM_TYPE_GROUP_SPACE_COLLECTION_PREPARE);
                            mGroupList.add(mpl);
                        }
                    } else {
                        //no more data .
                        mGroupList.add(new BaseTitleItemBar(getString(R.string.collective_prepare_lessons), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                    }
                }

                //个人备课
                ModulePersonPrepare modulePersonPrepare = moduleParse.getPersonPrepare();
                if (null != modulePersonPrepare) {
                    mTitleList.add(new MyBaseTitle(modulePersonPrepare.getGroupModuleId(), modulePersonPrepare.getModuleName()));
                    if (modulePersonPrepare.getData().size() > 0) {
                        //add the title .....
                        mGroupList.add(new BaseTitleItemBar(getString(R.string.person_lesson_prepare), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                        for (int i = 0; i < modulePersonPrepare.getData().size(); i++) {
                            GroupPersonPrepare mpl = modulePersonPrepare.getData().get(i);
                            mpl.setBaseViewHoldType(ITEM_TYPE_GROUP_SPACE_PERSON_PREPARE);
                            mGroupList.add(mpl);
                        }
                    } else {
                        //no more data .
                        mGroupList.add(new BaseTitleItemBar(getString(R.string.person_lesson_prepare), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                    }
                }
                // TODO: 16-5-16 资源分享模块留待添加...
                ModuleResource moduleResource = moduleParse.getResourceList() ;
                if(null != moduleResource ) {
                    //add titles for filter .
                    mTitleList.add(new MyBaseTitle(getString(R.string.resource_share), moduleResource.getModuleName()));
                    if (moduleResource.getData().size() > 0) {
                        mGroupList.add(new BaseTitleItemBar(getString(R.string.resource_share), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                        for (int i = 0; i < moduleResource.getData().size() ; i++){
                            GroupResource resource = moduleResource.getData().get(i);
                            resource.setBaseViewHoldType(ITEM_TYPE_GROUP_SPACE_RESOURCE_SHARE);
                            mGroupList.add(resource);
                        }
                    } else {
                        //no more data .
                        mGroupList.add(new BaseTitleItemBar(getString(R.string.resource_share), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                    }
                }

                //博文
                ModuleBlog moduleBlog = moduleParse.getBlog();
                if (null != moduleBlog) {
                    mTitleList.add(new MyBaseTitle(moduleBlog.getGroupModuleId(), moduleBlog.getModuleName()));
                    if (moduleBlog.getData().size() > 0) {
                        //add the title .....
                        mGroupList.add(new BaseTitleItemBar(getString(R.string.blog_title), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE));
                        for (int i = 0; i < moduleBlog.getData().size(); i++) {
                            GroupBlogPost mpl = moduleBlog.getData().get(i);
                            mpl.setBaseViewHoldType(ITEM_TYPE_GROUP_SPACE_BLOG_POST);
                            mGroupList.add(mpl);
                        }
                    } else {
                        //no more data .
                        mGroupList.add(new BaseTitleItemBar(getString(R.string.blog_title), TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA));
                    }
                }
            }
        }
    }

    @Override
    public void onFailure(VolleyError error) {
        if (null == mRecyclerView || null == mRefreshLayout) return;
        mRecyclerView.setEnabled(true);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        if (mGroupList.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    public void init() {
        UiMainUtils.setNavigationTintColor(this, R.color.main_green);
        mGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        mGroupType = getIntent().getStringExtra(EXTRA_TYPE);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        mTitleTextView.setText(title);
        initToolbar(mToolBar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsKeyBoardShow) {
                    UiMainUtils.hideKeyBoard(mInputManager);
                }
                finish();
                UIUtils.addExitTranAnim(GroupSpaceActivity.this);
            }
        });
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                //下拉刷新
                mRecyclerView.setEnabled(false);
                requestData(true);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder<BaseTitleItemBar>>() {
            @Override
            public BaseRecyclerViewHolder<BaseTitleItemBar> createViewHolder(final ViewGroup parent, int viewType) {
                BaseRecyclerViewHolder viewHolder = null;
                switch (viewType) {
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE://仅标题
                    case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA://标题 + NO DATA TIPS .
                        viewHolder = TitleItemViewHolderBuilder.getInstance().constructTitleItem(
                                parent.getContext(),parent,TitleItemViewHolderBuilder.ITEM_TIPS_SIMPLE_TEXT);
                        break;
                    case ITEM_TYPE_GROUP_SPACE_BASE:
                        viewHolder = new GroupSpaceViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_group_space, parent , false));
                        break;
                    case ITEM_TYPE_GROUP_SPACE_ANNOUNCE:
                        viewHolder = new GroupAnnounceViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_group_space_announce, parent,false));
                        break;
                    case ITEM_TYPE_GROUP_SPACE_COLLECTION_PREPARE://集体备课
                        viewHolder = new GroupCollectionPrepareViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_group_blog_post,parent,false));
                        break;
                    case ITEM_TYPE_GROUP_SPACE_PERSON_PREPARE://个人备课
                        viewHolder = new GroupPersonPrepareViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_group_prepare_lession, parent , false));
                        break;
                    case ITEM_TYPE_GROUP_SPACE_RESOURCE_SHARE://资源共享 ...暂时未实现
                        break;
                    case ITEM_TYPE_GROUP_SPACE_BLOG_POST://博文
                        viewHolder = new GroupBlogViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_group_blog_post , parent , false));
                        break;
                }
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
                return mGroupList.get(position).getBaseViewHoldType();
            }
        });

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<BaseTitleItemBar>() {
            @Override
            public void onItemClicked(View v, int position, BaseTitleItemBar data) {
                switch (mAdapter.getItemViewType(position)) {
                    case ITEM_TYPE_GROUP_SPACE_BASE:
                        GroupSpace groupSpace = (GroupSpace) data;
                        if(v.getId() == R.id.tv_operator){//操作按钮 ->申请加入 / 退出/批准
                            Cog.i(TAG,"operator type : " +groupSpace.getOperateType());
                            //申请加入/退出圈组
                            if (GroupSpace.GROUP_OPERATE_TYPE_PROMPT == groupSpace.getOperateType()) {
                                //弹出输入框
                                mPromptRelativeLayout.setVisibility(View.VISIBLE);
                                mPromptRelativeLayout.requestFocus();
                                if (!mIsKeyBoardShow) {
                                    UiMainUtils.showKeyBoard(mInputManager);
                                }
                            } else if (GroupSpace.GROUP_OPERATE_TYPE_EXIT == groupSpace.getOperateType()) {
                                if (mGroupList.size() > 0) {
                                    GroupSpace group = (GroupSpace) mGroupList.get(0);
                                    promptAddGroupSpace(group, null, GroupSpace.GROUP_OPERATE_TYPE_EXIT);
                                }
                            }
                        }else{
                            hideInput();
                            GroupSpaceDetailActivity.start(GroupSpaceActivity.this, (GroupSpace) data);
                        }
                        break;
                    case ITEM_TYPE_GROUP_SPACE_COLLECTION_PREPARE://集体备课
                        GroupPrepareLesson prepare = (GroupPrepareLesson) data;
                        GroupCollectiveActivityDetail.start(GroupSpaceActivity.this,prepare.getMeetingId());
                        break;
                    case ITEM_TYPE_GROUP_SPACE_PERSON_PREPARE://个人备课
                        GroupPersonPrepare personPrepare = (GroupPersonPrepare) data;
                        //click head icon .
                        if(v.getId() == R.id.sdv_pic){
                            if (personPrepare.getBaseUserId().equals(mUserInfo.getBaseUserId())) {
                                MainActivity.start(GroupSpaceActivity.this, mUserInfo, 2);//1.自己的信息跳转到首页-"我的"
                            } else {
                                PublicUserActivity.start(GroupSpaceActivity.this, personPrepare.getBaseUserId()); //2.访客
                            }
                        }else{
                            PersonalLesPrepContentActivity.start(GroupSpaceActivity.this, personPrepare.getPrepareLessonPlanId());
                        }
                        break;
                    case ITEM_TYPE_GROUP_SPACE_RESOURCE_SHARE://资源共享 ...暂时未实现
                        break;
                    case ITEM_TYPE_GROUP_SPACE_BLOG_POST://博文
                        GroupBlogPost blogPost = (GroupBlogPost) data;
                        //click head icon .
                        if(v.getId() == R.id.sdv_pic){
                            if (blogPost.getBaseUserId().equals(mUserInfo.getBaseUserId())) {
                                MainActivity.start(GroupSpaceActivity.this, mUserInfo, 2);//1.自己的信息跳转到首页-"我的"
                            } else {
                                PublicUserActivity.start(GroupSpaceActivity.this, blogPost.getBaseUserId()); //2.访客
                            }
                        }else{
                            BlogPostDetailActivity.startFromGroup(GroupSpaceActivity.this,blogPost.getBlogId(),mGroupId);
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
        //申请加入输入框 .
        mInputEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if (mGroupList.size() > 0) {
                            GroupSpace group = (GroupSpace) mGroupList.get(0);
                            promptAddGroupSpace(group, mInputEdt.getText().toString(), GroupSpace.GROUP_OPERATE_TYPE_PROMPT);
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        mKeyboardHelper = new SoftKeyboardStateHelper(getWindow().getDecorView());
        mKeyboardHelper.addSoftKeyboardStateListener(this);

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

    private void hideInput() {
        mPromptRelativeLayout.setVisibility(View.GONE);
        if (mIsKeyBoardShow) {
            UiMainUtils.hideKeyBoard(mInputManager);
        }
    }

    @OnClick(R.id.tv_send_prompt)
    void sendPromptReason(){
        if (mGroupList.size() > 0) {
            GroupSpace group = (GroupSpace) mGroupList.get(0);
            promptAddGroupSpace(group, mInputEdt.getText().toString(), GroupSpace.GROUP_OPERATE_TYPE_PROMPT);
        }
    }

    @OnClick(R.id.iv_prompt_close)
    void closePromptView(){
        hideInput();
    }

    /**
     * 申请加入
     *
     * @param reason 加入的理由
     * @param type   {@link GroupSpace#GROUP_OPERATE_TYPE_EXIT,GroupSpace#GROUP_OPERATE_TYPE_PROMPT}
     */
    private void promptAddGroupSpace(GroupSpace groupSpace, String reason, final int type) {
        if (type == GroupSpace.GROUP_OPERATE_TYPE_PROMPT) {
            if (!TextUtils.isEmpty(reason)&&reason.length() > 150) {
                ToastUtil.showToast("输入字数必须小于150！");
                return;
            }
        }

        if(type==GroupSpace.GROUP_OPERATE_TYPE_PROMPT&&!mUserInfo.isHasTeam(mGroupSpace.getGroupType()) ){
            ToastUtil.showToast("权限不够，无法加入本圈组！");
            return;
        }

        if (null == groupSpace) return;
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        data.put("groupId", groupSpace.getGroupId());
        if (GroupSpace.GROUP_OPERATE_TYPE_PROMPT == type) {
            data.put("joinReason", reason);
        }
        String url = URLConfig.UPDATE_GROUP_PROMPT_ENTER;
        if (GroupSpace.GROUP_OPERATE_TYPE_EXIT == type) {
            url = URLConfig.UPDATE_GROUP_PROMPT_OUT;
        }
        requestData(url, data, false, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response ,boolean isRefreshing) {
                if (response != null && response.optString("result").equals("success")) {
                    ToastUtil.showToast(response.optString("message"));
                    mRecyclerView.setEnabled(false);
                    requestData(true);
                    mInputEdt.setText("");
                    hideInput();
                } else if (null != response) {
                    ToastUtil.showToast(response.optString("message"));
                }
            }

            @Override
            public void onRequestFailure(VolleyError error) {
                String errorMsg = error.getMessage();
                if (TextUtils.isEmpty(errorMsg)) {
                    errorMsg = error.networkResponse.statusCode + " :ServerError ！";
                }
                ToastUtil.showToast(errorMsg);
                LogUtils.log(error);
            }
        });
    }

    private void initFilterFragment() {
        if (mFilterFragment == null) {
            mTitleList.add(new MyBaseTitle("", "微博"));
            mTitleList.add(new MyBaseTitle("", "成员"));
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
     * @param context context
     * @param title title
     * @param groupId groupId
     * @param type    {@link CategoryFilterFragment#CATEGORY_TYPE_DOOR,CategoryFilterFragment#CATEGORY_TYPE_PERSON}
     */
    public static void start(Context context, String title, String groupId, String type) {
        Intent intent = new Intent(context, GroupSpaceActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        mInputEdt.requestFocus();
        mIsKeyBoardShow = true;
    }

    @Override
    public void onSoftKeyboardClosed() {
        mIsKeyBoardShow = false;
        if (mInputEdt.getVisibility() == View.VISIBLE) {
            mPromptRelativeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClicked(View v, int position, Object data) {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }
        if (null == data) return;
        FilterEntity fe = (FilterEntity) data;
        if (getString(R.string.collective_prepare_lessons).equals(fe.getName())) {
            //集体备课
            GroupCollectiveLessonsActivity.start(this, mGroupId);
        }

        if (getString(R.string.person_lesson_prepare).equals(fe.getName()) || "个人备课".equals(fe.getName())) {
            //个人备课 -> 教案
            GroupLessonPlanActivity.start(this, mGroupId);
        }

        if (getString(R.string.resource_share).equals(fe.getName())) {
            //资源分享
        }

        if (getString(R.string.weibo).equals(fe.getName())) {
            //微博
            GroupSpace groupSpace = (GroupSpace) mGroupList.get(0);
            if ("CREATOR".equals(groupSpace.getUserType()) || "MANAGER".equals(groupSpace.getUserType())) {
                WeiBoActivity.start(this, WeiBoActivity.TYPE_GROUP_MANAGER, mGroupId, mUserInfo.getBaseUserId(),mUserInfo.getUserName());
            } else if ("APPROVED".equals(groupSpace.getApproveStatus())) {
                WeiBoActivity.start(this, WeiBoActivity.TYPE_GROUP, mGroupId, mUserInfo.getBaseUserId(),mUserInfo.getUserName());
            } else {
                WeiBoActivity.start(this, WeiBoActivity.TYPE_GROUP_VISITOR, mGroupId, mUserInfo.getBaseUserId(),mUserInfo.getUserName());
            }
        }

        if (getString(R.string.members).equals(fe.getName())) {
            //成员
            GroupMemberActivity.start(this, mGroupId, mCount);
        }
        if (getString(R.string.blog_title).equals(fe.getName())) {
            //博文
            GroupBlogActivity.start(this, mGroupId, mGroupType);
        }
    }
}
