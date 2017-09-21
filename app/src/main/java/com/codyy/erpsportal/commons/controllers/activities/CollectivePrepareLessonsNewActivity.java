package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.FilterGradeSubject;
import com.codyy.erpsportal.commons.controllers.viewholders.LessonsViewHold;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.EvaluationScore;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsShortEntity;
import com.codyy.erpsportal.commons.models.entities.TeachingResearchBase;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ConfirmTextFilterListener;
import com.codyy.erpsportal.commons.utils.DeviceUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RefreshLayout;
import com.codyy.erpsportal.commons.widgets.UpOrDownButton;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleHorizonDivider;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleRecyclerView;
import com.codyy.url.URLConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;


/**
 * * 频道页－网络授课－更多(集体备课、互动听课排序列表)
 * Created by yangxinwu on 2015/7/27.
 */
public class CollectivePrepareLessonsNewActivity extends BaseHttpActivity implements View.OnClickListener{
    private final static String TAG = "CollectivePrepareLessonsNewActivity";
    private final static String SORT_TYPE_VIEW = "VIEW";//点击量排序
    private final static String SORT_TYPE_TIME = "TIME";//时间排序
    private final static String SORT_TYPE_SCORE = "SCORE";//评分排序
    private final static String ORDER_TYPE_DESC = "DESC";//降序
    private final static String ORDER_TYPE_ASC = "ASC";//升序

    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.empty_view) EmptyView mEmptyView;
    @Bind(R.id.refresh_layout)RefreshLayout mRefreshLayout;
    @Bind(R.id.recycler_view)SimpleRecyclerView mRecyclerView;
    @Bind(R.id.udb_by_time) UpOrDownButton mUDBTime;
    @Bind(R.id.udb_by_quality) UpOrDownButton mUDBQuality;
    @Bind(R.id.udb_by_count) UpOrDownButton mUDBCount;
    private List<PrepareLessonsShortEntity> mDataList = new ArrayList<>();
    private BaseRecyclerAdapter<PrepareLessonsShortEntity,LessonsViewHold> mAdapter ;
    /*** 类型：评课\互动听课\集体备课 */
    private int mFromType;
    private String mShortType = "";
    private String mOrderType = ORDER_TYPE_DESC;
    private String mGradeId = "";
    private String mSubjectId = "";
    /**
     * 年级学科筛选
     */
    private FilterGradeSubject mFilterGradeSubject;
    private String baseAreaId;
    private String schoolId;
    private int mTotal = 0 ;//数据总数.

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_collective_prepare_new_lessons;
    }

    @Override
    public String obtainAPI() {
        if(TeachingResearchBase.PREPARE_LESSON == mFromType){
            return URLConfig.GET_PREPARE_LESSON;
        }else if(TeachingResearchBase.EVALUATION_LESSON == mFromType){
            return URLConfig.GET_EVALUATION_LESSON;
        }else if(TeachingResearchBase.INTERAC_LESSON == mFromType){
            return URLConfig.GET_INTERAC_LESSON;
        }
        return "";
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> param = new HashMap<>();
        param.put("userType", mUserInfo.getUserType());
        param.put("uuid", mUserInfo.getUuid());
        param.put("areaId", baseAreaId);
        param.put("schoolId", schoolId);
        param.put("sortType", mShortType);
        param.put("orderType", mOrderType);
        param.put("subjectId", mSubjectId);
        param.put("classLevelId", mGradeId);
        param.put("start", String.valueOf(mDataList.size()));
        param.put("end", String.valueOf(mDataList.size()+sPageCount-1));
        return param;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        initData();
    }

    @Override
    public void init() {
        baseAreaId = getIntent().getStringExtra("baseAreaId");
        schoolId = getIntent().getStringExtra("schoolId");
        mFromType = getIntent().getIntExtra("mFromType", -1);
        UiMainUtils.setNavigationTintColor(this,R.color.main_green);
        if(null == mUserInfo) return;

        String title = Titles.sPagetitleNetteachAllprepare;
        switch (mFromType) {
            case TeachingResearchBase.PREPARE_LESSON:
                title = Titles.sPagetitleNetteachAllprepare;
                break;
            case TeachingResearchBase.EVALUATION_LESSON:
                title = Titles.sPagetitleNetteachDisucss;
                break;
            case TeachingResearchBase.INTERAC_LESSON:
                title = Titles.sPagetitleNetteachInteract;
                break;
        }
        Cog.i(TAG,"title " + title);
        mTitleTextView.setText(title);
        initToolbar(mToolBar);

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
                initData();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUDBTime.setOnClickListener(this);
        mUDBQuality.setOnClickListener(this);
        mUDBCount.setOnClickListener(this);
        mUDBTime.setText(R.string.by_create_time);
        mUDBQuality.setText(R.string.by_quality);
        mUDBCount.setText(R.string.by_count);

        addGradeSubjectFilter();
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<LessonsViewHold>() {
            @Override
            public LessonsViewHold createViewHolder(ViewGroup parent, int viewType) {
                return new LessonsViewHold(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),R.layout.item_collective_prepare_lessons)
                        ,mFromType);
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
        mAdapter.setOnLoadMoreClickListener(new BaseRecyclerAdapter.OnLoadMoreClickListener() {
            @Override
            public void onMoreData() {
                requestData(false);
            }
        });
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<PrepareLessonsShortEntity>() {
            @Override
            public void onItemClicked(View v, int position, PrepareLessonsShortEntity data) throws Exception {
                ItemClick(data);
            }


        });
        mRecyclerView.setAdapter(mAdapter);
        this.enableLoadMore(mRecyclerView,false);
        setFilterListener(new ConfirmTextFilterListener(mDrawerLayout) {
            @Override
            protected void doFilterConfirmed() {
                //filter data to refresh .
                execSearch();
            }
        });
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
    }

    /** 初始化数据　or 筛选**/
    private void initData() {
        if(null != mRefreshLayout) mRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    @Override
    public void onSuccess(JSONObject response, boolean isRefreshing) throws Exception {
        if(null == mRefreshLayout) return;
        if(isRefreshing) mDataList.clear();
        mRecyclerView.setRefreshing(false);
        mAdapter.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mEmptyView.setLoading(false);
        parseEntity(response);
        mAdapter.setData(mDataList);
        //load more ...
        if(mDataList.size() <  mTotal){
            mAdapter.setRefreshing(true);
            mAdapter.setHasMoreData(true);
        }else{
            mAdapter.setHasMoreData(false);
        }
        mAdapter.notifyDataSetChanged();

        if(mDataList.size()<=0){
            mEmptyView.setLoading(false);
            mEmptyView.setVisibility(View.VISIBLE);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onFailure(Throwable error) throws Exception {
        if(null == mRecyclerView || null == mRefreshLayout) return;
        mRecyclerView.setRefreshing(false);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mAdapter.notifyDataSetChanged();
        if(mDataList.size()<=0){
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(false);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    /**
     * 添加学科、年级筛选
     */
    private void addGradeSubjectFilter() {
        mFilterGradeSubject = new FilterGradeSubject();
        Bundle bundle = new Bundle();
        bundle.putInt("filter_type", FilterGradeSubject.NO_STATUS);
        mFilterGradeSubject.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_collective_new_filter_fragment, mFilterGradeSubject).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.udb_by_time:
                mUDBTime.setChecked();
                mUDBQuality.setInitView();
                mUDBCount.setInitView();
                mShortType = SORT_TYPE_TIME;
                if (mUDBTime.getCurUpOrDown()) {
                    mOrderType = ORDER_TYPE_DESC;
                    initData();
                } else {
                    mOrderType = ORDER_TYPE_ASC;
                    initData();
                }
                break;
            case R.id.udb_by_quality:
                mUDBQuality.setChecked();
                mUDBTime.setInitView();
                mUDBCount.setInitView();
                mShortType = SORT_TYPE_SCORE;
                if (mUDBQuality.getCurUpOrDown()) {
                    mOrderType = ORDER_TYPE_DESC;
                    initData();
                } else {
                    mOrderType = ORDER_TYPE_ASC;
                    initData();
                }
                break;
            case R.id.udb_by_count:
                mUDBCount.setChecked();
                mUDBQuality.setInitView();
                mUDBTime.setInitView();
                mShortType = SORT_TYPE_VIEW;
                if (mUDBCount.getCurUpOrDown()) {
                    mOrderType = ORDER_TYPE_DESC;
                    initData();
                } else {
                    mOrderType = ORDER_TYPE_ASC;
                    initData();
                }
                break;
            case R.id.btn_back:
                DeviceUtils.hideSoftKeyboard(mEmptyView);
                this.finish();
                overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
                break;
            default:
                break;
        }
    }

    private void ItemClick(PrepareLessonsShortEntity item) {
        switch (mFromType) {
            case TeachingResearchBase.PREPARE_LESSON:
                ActivityThemeActivity.start(this,ActivityThemeActivity.PREPARE_LESSON,item.getId(),item.getViewCount());
                break;
            case TeachingResearchBase.EVALUATION_LESSON:
                EvaluationScore evaluationScore = new EvaluationScore();
                evaluationScore.setAvgScore( item.getAverageScore());
                evaluationScore.setScoreType(item.getScoreType());
                float totalScore = 0f;
                try {
                    totalScore = Float.parseFloat(item.getTotalScore());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                evaluationScore.setTotalScore(totalScore);
                ActivityThemeActivity.start(this,ActivityThemeActivity.EVALUATION_LESSON,item.getId(),item.getViewCount(),evaluationScore);
                break;
            case TeachingResearchBase.INTERAC_LESSON:
                ActivityThemeActivity.start(this,ActivityThemeActivity.INTERACT_LESSON,item.getId(),item.getViewCount());
                break;
        }
    }

    /**
     * 开始筛选
     */
    private void execSearch() {
        if (mFilterGradeSubject.getmGradeID() != null) {
            mGradeId = mFilterGradeSubject.getmGradeID();
        } else {
            mGradeId = "";
        }
        if (mFilterGradeSubject.getmSubjectID() != null) {
            mSubjectId = mFilterGradeSubject.getmSubjectID();
        } else {
            mSubjectId = "";
        }
        initData();
    }

    /**解析实体类**/
    private void parseEntity(JSONObject response) throws JSONException {
        if ("success".equals(response.optString("result"))) {
            JSONObject jsonObject = null;
            switch (mFromType) {
                case TeachingResearchBase.PREPARE_LESSON:
                    jsonObject = response.getJSONObject("groupPreparation");
                    break;
                case TeachingResearchBase.EVALUATION_LESSON:
                    jsonObject = response.getJSONObject("evaluationAndDiscussion");
                    break;
                case TeachingResearchBase.INTERAC_LESSON:
                    jsonObject = response.getJSONObject("interactionListen");
                    break;
            }

            mTotal = jsonObject.optInt("total");
            JSONArray jsonArray = jsonObject.optJSONArray("list");
            List<PrepareLessonsShortEntity> prepareLessonsShortEntityList = PrepareLessonsShortEntity.parseJsonArray(jsonArray);
            if(null != prepareLessonsShortEntityList) mDataList.addAll(prepareLessonsShortEntityList);
        }
    }

    public static void start(Context context, int type, String schoolId, String baseAreaId) {
        Intent intent = new Intent(context, CollectivePrepareLessonsNewActivity.class);
        intent.putExtra("mFromType", type);
        intent.putExtra("schoolId", schoolId);
        intent.putExtra("baseAreaId", baseAreaId);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }
}
