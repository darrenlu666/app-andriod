package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.activity.ClassRoomDetailActivity;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.fragments.filters.CategoryFilterFragment;
import com.codyy.erpsportal.commons.controllers.fragments.filters.GroupFilterFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.HistoryClassViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.customized.SipLessonViewHolder;
import com.codyy.erpsportal.commons.models.entities.customized.SipLesson;
import com.codyy.erpsportal.commons.models.entities.customized.SipLessonMoreParse;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ConfirmTextFilterListener;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleBisectDivider;
import com.codyy.erpsportal.groups.controllers.activities.GroupSpaceActivity;
import com.codyy.erpsportal.groups.controllers.viewholders.ChannelGroupViewHolder;
import com.codyy.erpsportal.groups.models.entities.Group;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.entities.FilterConstants;
import com.codyy.tpmp.filterlibrary.fragments.CommentFilterFragment;
import com.codyy.tpmp.filterlibrary.interfaces.HttpGetInterface;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleHorizonDivider;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleRecyclerView;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * 同步课堂－更多(sip)
 * Created by poe on 17-7-25.
 */
public class MoreSemesterLessonActivity extends BaseHttpActivity implements HttpGetInterface{
    private final static String TAG = "MoreSemesterLessonActivity";
    private final static String EXTRA_TITLE = "com.codyy.intent.title";//title
    private final static String EXTRA_SEMESTER_ID = "com.codyy.intent.semester.id";//semester id .

    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.empty_view)EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    @Bind(R.id.drawer_layout)DrawerLayout mDrawerLayout;

    private List<SipLesson> mData = new ArrayList<>();
    private BaseRecyclerAdapter<SipLesson,BaseRecyclerViewHolder<SipLesson>> mAdapter ;
    private String mGrade ;
    private String mSubject ;
    private String mSemester ;
    private String mBaseAreaId ;
    private String mSchoolId ;
    private CommentFilterFragment mFilterFragment;

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_single_recycler_view;
    }

    @Override
    public String obtainAPI() {
        //获取更多的课堂信息.
        return URLConfig.GET_SIP_ONLINE_CLASS_MORE;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if(null != mSemester) data.put("baseSemesterId",mSemester);
        if(null != mBaseAreaId) data.put("baseAreaId", mBaseAreaId);
        if(null != mSchoolId) data.put("schoolId", mSchoolId);
        if(null != mGrade)  data.put("baseClasslevelId",mGrade);
        if(null != mSubject) data.put("baseSubjectId",mSubject);
        data.put("start", mData.size()+"");
        data.put("end",(mData.size()+sPageCount-1)+"");
        return data;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG , response.toString());
        if(null == mRecyclerView ) return;
        if(isRefreshing) mData.clear();
        mRecyclerView.setRefreshing(false);
        mAdapter.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mEmptyView.setLoading(false);
        parseLesson(response);
        mAdapter.setData(mData);

        if(mData.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    private void parseLesson(JSONObject response) {
        SipLessonMoreParse parse = new Gson().fromJson(response.toString(),SipLessonMoreParse.class);
        if(null != parse) {
            if (parse.getData() != null && parse.getData().size()>0) {
                //clear data .
                mData.clear();
                // TODO: 25/07/17 测试数据，需要删除　ｉｆ　ａｐｉ　total is available .
                if(parse.getTotal() <= 0) parse.setTotal(parse.getData().size());
                for (SipLesson group : parse.getData()) {
                    group.setBaseViewHoldType(HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE);
                    mData.add(group);
                }
            }

            if(mData.size() <  parse.getTotal()){
                mAdapter.setRefreshing(true);
                mAdapter.setHasMoreData(true);
            }else{
                mAdapter.setHasMoreData(false);
            }
        }
    }

    @Override
    public void onFailure(Throwable error) {
        if(null == mRecyclerView ) return;
        mRecyclerView.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        if(mData.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void preInit() {
        super.preInit();
        mSemester = getIntent().getStringExtra(EXTRA_SEMESTER_ID);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    public void init() {
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        mBaseAreaId = mUserInfo.getBaseAreaId();
        mSchoolId   =   mUserInfo.getSchoolId();
        mTitleTextView.setText(title);
        initToolbar(mToolBar);
        UiMainUtils.setNavigationTintColor(this,R.color.main_green);

        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                requestData(true);
            }
        });
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        mRecyclerView.addItemDecoration(new SimpleBisectDivider(divider, (int) getResources().getDimension(R.dimen.poe_recycler_grid_layout_padding), new SimpleBisectDivider.IGridLayoutViewHolder() {
            @Override
            public int obtainSingleBigItemViewHolderType() {
                return HistoryClassViewHolder.ITEM_TYPE_BIG_IN_LINE;
            }

            @Override
            public int obtainMultiInLineViewHolderType() {
                return HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE;
            }
        }));
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新
                mRecyclerView.setRefreshing(true);
                mAdapter.setHasMoreData(false);
                requestData(true);
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if (mAdapter.getItemViewType(position) == HistoryClassViewHolder.ITEM_TYPE_DOUBLE_IN_LINE) {
                    return 1;
                }
                return 2;
            }
        });
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<BaseRecyclerViewHolder<SipLesson>>() {
            @Override
            public BaseRecyclerViewHolder<SipLesson> createViewHolder(ViewGroup parent, int viewType) {
                return  new SipLessonViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_customized_history_class_small, parent, false));
            }

            @Override
            public int getItemViewType(int position) {
                return mData.get(position).getBaseViewHoldType();
            }
        });
        //set on click listener .
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<SipLesson>() {
            @Override
            public void onItemClicked(View v, int position, SipLesson data) {
                ClassRoomDetailActivity.startActivity(MoreSemesterLessonActivity.this
                        , mUserInfo
                        , data.getId()
                        , ClassRoomContants.TYPE_CUSTOM_RECORD
                        , data.getSubjectName());
            }
        });
        mAdapter.setOnLoadMoreClickListener(new BaseRecyclerAdapter.OnLoadMoreClickListener() {
            @Override
            public void onMoreData() {
                requestData(false);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        this.enableLoadMore(mRecyclerView,false);
        //init filter fragment .
        initFilterFragment();
        //add drawableLayout swipe gesture.
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
        setFilterListener(new ConfirmTextFilterListener(mDrawerLayout) {
            @Override
            protected void doFilterConfirmed() {
                doFilterConfirm();
            }
        });
    }

    private void initFilterFragment() {
        mFilterFragment = CommentFilterFragment.newInstance(mUserInfo.getUuid()
                            , mUserInfo.getUserType()
                            , mUserInfo.getBaseAreaId()
                            , mUserInfo.getSchoolId()
                            , new int[]{
                                  FilterConstants.LEVEL_CLASS_LEVEL     //年级
                                , FilterConstants.LEVEL_CLASS_SUBJECT  //学科
                                });
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_filter, mFilterFragment);
        ft.commitAllowingStateLoss();
    }

    private void doFilterConfirm() {
        Cog.i(TAG," doFilterConfirmed ~");
        Bundle bd = mFilterFragment.getFilterData();
        if(null != bd){
            mGrade      =   bd.getString("class");
            mSubject    =   bd.getString("subject");
        }
        Cog.i(TAG,"mBaseAreaId"+mBaseAreaId
                +" mSemester"+mSemester
                +" mSchoolId"+mSchoolId
                +" mGrade"+mGrade
                +" mSubject"+mSubject);
        //refresh .
        requestData(true);
    }

    @Override
    public void sendRequest(String url, Map<String, String> param, final Listener listener, final ErrorListener errorListener) {
        requestData(url, (HashMap<String, String>) param, true, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response, boolean isRefreshing) throws Exception {
                if(null != listener) listener.onResponse(response);
            }

            @Override
            public void onRequestFailure(Throwable error) {
                if(null != errorListener) errorListener.onErrorResponse(error);
            }
        });
    }

    /**
     * @param context context
     * @param title title
     */
    public static void start(Context context , String title ,String semesterId){
        Intent intent = new Intent(context , MoreSemesterLessonActivity.class);
        intent.putExtra(EXTRA_TITLE , title);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }
}
