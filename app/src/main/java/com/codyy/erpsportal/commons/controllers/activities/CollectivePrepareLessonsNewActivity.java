package com.codyy.erpsportal.commons.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.FilterGradeSubject;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.EvaluationScore;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsShortEntity;
import com.codyy.erpsportal.commons.models.entities.TeachingResearchBase;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.UpOrDownButton;
import com.codyy.url.URLConfig;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


/**
 * * 集体备课、互动听课排序列表
 * Created by yangxinwu on 2015/7/27.
 */
public class CollectivePrepareLessonsNewActivity extends FragmentActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2, AdapterView.OnItemClickListener {

    private final static String TAG = "CollectivePrepareLessonsNewActivity";
    private final static String SORT_TYPE_VIEW = "VIEW";//点击量排序
    private final static String SORT_TYPE_TIME = "TIME";//时间排序
    private final static String SORT_TYPE_SCORE = "SCORE";//评分排序
    private final static String ORDER_TYPE_DESC = "DESC";//降序
    private final static String ORDER_TYPE_ASC = "ASC";//升序

    private UpOrDownButton mUDBTime;
    private UpOrDownButton mUDBQuality;
    private UpOrDownButton mUDBCount;
    private CheckBox mBtnSelect;
    private Button mBtnBack;
    private PullToRefreshListView mListView;
    private EmptyView mEmptyView;
    private ObjectsAdapter<PrepareLessonsShortEntity, LessonsViewHold1> mAdapter;
    /**
     * 开始请求位置
     */
    private int mStart = 0;
    /**
     * 每次加载内容条数
     */
    private int mLoadCount = 9;
    /**
     * 筛选抽屉
     */
    private DrawerLayout mDrawerLayout;
    /**
     * 网络请求
     */
    private RequestSender mSender;
    /**
     * 类型：评课\互动听课\集体备课
     */
    private int type;
    private String mURL;
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
    private UserInfo mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseAreaId = getIntent().getStringExtra("baseAreaId");
        schoolId = getIntent().getStringExtra("schoolId");
        setContentView(R.layout.activity_collective_prepare_new_lessons);
        type = getIntent().getIntExtra("type", -1);
        //        type=TeachingResearchBase.PREPARE_LESSON;
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        initView();
    }

    private void initView() {
        mSender = new RequestSender(this);
        mUDBTime = (UpOrDownButton) findViewById(R.id.udb_by_time);
        mUDBQuality = (UpOrDownButton) findViewById(R.id.udb_by_quality);
        mUDBCount = (UpOrDownButton) findViewById(R.id.udb_by_count);
        mUDBTime.setOnClickListener(this);
        mUDBQuality.setOnClickListener(this);
        mUDBCount.setOnClickListener(this);
        mUDBTime.setText(R.string.by_create_time);
        mUDBQuality.setText(R.string.by_quality);
        mUDBCount.setText(R.string.by_count);
//        mUDBTime.setChecked();
        mBtnBack = (Button) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);
        mBtnSelect = (CheckBox) findViewById(R.id.btn_select);
        mBtnSelect.setOnClickListener(this);
        mListView = (PullToRefreshListView) findViewById(R.id.ptrl_prepare_lseeons_list);
        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mListView.setEmptyView(mEmptyView);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                httpConnect(true);
            }
        });
        initPullToRefresh(mListView);
        mListView.setOnItemClickListener(this);
        mAdapter = new ObjectsAdapter<>(this, new ObjectsAdapter.ViewHolderBuilder() {
            @Override
            public AbsViewHolder createViewHolder() {
                return new LessonsViewHold1();
            }
        });
        mListView.setAdapter(mAdapter);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_collective_prepare_lesson_new_drawerlayout);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mBtnSelect.setChecked(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mBtnSelect.setChecked(false);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        TextView textView = (TextView) findViewById(R.id.tv_title);
        switch (type) {
            case TeachingResearchBase.PREPARE_LESSON:
                textView.setText(Titles.sPagetitleNetteachAllprepare);
                addGradeSubjectFilter();
                mURL = URLConfig.GET_PREPARE_LESSON;
                httpConnect(true);
                break;
            case TeachingResearchBase.EVALUATION_LESSON:
                textView.setText(Titles.sPagetitleNetteachDisucss);
                addGradeSubjectFilter();
                mURL = URLConfig.GET_EVALUATION_LESSON;
                httpConnect(true);
                break;
            case TeachingResearchBase.INTERAC_LESSON:
                textView.setText(Titles.sPagetitleNetteachInteract);
                addGradeSubjectFilter();
                mURL = URLConfig.GET_INTERAC_LESSON;
                httpConnect(true);
                break;
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

    private void initPullToRefresh(PullToRefreshAdapterViewBase<?> view) {
        view.setMode(PullToRefreshBase.Mode.BOTH);
        view.getLoadingLayoutProxy(false, true).setPullLabel(getResources().getString(R.string.pull_to_refresh));
        view.getLoadingLayoutProxy(false, true).setRefreshingLabel(getResources().getString(R.string.loading));
        view.getLoadingLayoutProxy(false, true).setReleaseLabel(getResources().getString(R.string.release_to_refresh));
        view.setOnRefreshListener(this);
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
                    httpConnect(true);
                } else {
                    mOrderType = ORDER_TYPE_ASC;
                    httpConnect(true);
                }
                break;
            case R.id.udb_by_quality:
                mUDBQuality.setChecked();
                mUDBTime.setInitView();
                mUDBCount.setInitView();
                mShortType = SORT_TYPE_SCORE;
                if (mUDBQuality.getCurUpOrDown()) {
                    mOrderType = ORDER_TYPE_DESC;
                    httpConnect(true);
                } else {
                    mOrderType = ORDER_TYPE_ASC;
                    httpConnect(true);
                }
                break;
            case R.id.udb_by_count:
                mUDBCount.setChecked();
                mUDBQuality.setInitView();
                mUDBTime.setInitView();
                mShortType = SORT_TYPE_VIEW;
                if (mUDBCount.getCurUpOrDown()) {
                    mOrderType = ORDER_TYPE_DESC;
                    httpConnect(true);
                } else {
                    mOrderType = ORDER_TYPE_ASC;
                    httpConnect(true);
                }
                break;
            case R.id.btn_back:
                this.finish();
                overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
                break;
            case R.id.btn_select:
                if (!mBtnSelect.isChecked()) {
                    execSearch();
                }
                if (!mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.openDrawer(GravityCompat.END);
                } else {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 网络请求
     */
    private void httpConnect(final boolean refresh) {
        mEmptyView.setLoading(true);
        HashMap<String, String> data = new HashMap<>();
        data.put("userType", mUserInfo.getUserType());
        data.put("uuid", mUserInfo.getUuid());
        data.put("areaId", baseAreaId);
        data.put("schoolId", schoolId);
        data.put("sortType", mShortType);
        data.put("orderType", mOrderType);
        data.put("subjectId", mSubjectId);
        data.put("classLevelId", mGradeId);
        if (refresh) {
            mStart = 0;
        }
        int start = mStart;
        int end = start + mLoadCount;
        data.put("start", String.valueOf(start));
        data.put("end", String.valueOf(end));
        mSender.sendRequest(new RequestSender.RequestData(mURL, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if ("success".equals(response.optString("result"))) {
                    try {
                        JSONObject jsonObject = null;
                        switch (type) {
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

                        int total = jsonObject.optInt("total");
                        if (total == 0) {
                            mAdapter.setData(null);
                            mAdapter.notifyDataSetChanged();
                            mListView.onRefreshComplete();
                            mEmptyView.setLoading(false);
                            mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                        } else {
                            JSONArray jsonArray = jsonObject.optJSONArray("list");
                            List<PrepareLessonsShortEntity> prepareLessonsShortEntityList = PrepareLessonsShortEntity.parseJsonArray(jsonArray);
                            if (refresh) {
                                mAdapter.setData(prepareLessonsShortEntityList);
                            } else {
                                mAdapter.addData(prepareLessonsShortEntityList);
                            }

                            mAdapter.notifyDataSetChanged();

                            mListView.onRefreshComplete();
                            //如果已经加载所有，下拉更多关闭
                            if (total <= mAdapter.getCount()) {
                                mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                mListView.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mStart = mAdapter.getCount();
                } else {
                    mListView.onRefreshComplete();
                    mEmptyView.setLoading(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.e(TAG, "onErrorResponse:" + error);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                if (refresh && mAdapter.getCount() == 0) {
                    mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                mListView.onRefreshComplete();
                mEmptyView.setLoading(false);
            }
        }));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PrepareLessonsShortEntity item = mAdapter.getItem(position - 1);
        Intent intent = new Intent(this, ActivityThemeActivity.class);
        switch (type) {
            case TeachingResearchBase.PREPARE_LESSON:
                intent.putExtra("type", ActivityThemeActivity.PREPARE_LESSON);
                break;
            case TeachingResearchBase.EVALUATION_LESSON:
                EvaluationScore evaluationScore = new EvaluationScore();
                evaluationScore.setAvgScore(item.getAverageScore());
                evaluationScore.setScoreType(item.getScoreType());
                float totalScore = 0f;
                try {
                    totalScore = Float.parseFloat(item.getTotalScore());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                evaluationScore.setTotalScore(totalScore);
                intent.putExtra("type", ActivityThemeActivity.EVALUATION_LESSON);
                intent.putExtra("score", evaluationScore);
                break;
            case TeachingResearchBase.INTERAC_LESSON:
                intent.putExtra("type", ActivityThemeActivity.INTERACT_LESSON);
                break;
        }
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        httpConnect(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        httpConnect(false);
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
        httpConnect(true);
    }

    class LessonsViewHold1 extends AbsViewHolder<PrepareLessonsShortEntity> {
        SimpleDraweeView headerImage;
        TextView title;
        TextView teachName;
        TextView date;
        TextView clickCount;
        RatingBar ratingBar;
        TextView teacherTitle;
        TextView scoreTv;
        TextView rateTv;

        @Override
        public int obtainLayoutId() {
            return R.layout.item_collective_prepare_lessons;
        }

        @Override
        public void mapFromView(View view) {
            headerImage = (SimpleDraweeView) view.findViewById(R.id.img_lesson_item);
            title = (TextView) view.findViewById(R.id.tv_lesson_title);
            teachName = (TextView) view.findViewById(R.id.tv_teacher);
            date = (TextView) view.findViewById(R.id.tv_date);
            clickCount = (TextView) view.findViewById(R.id.tv_count);
            ratingBar = (RatingBar) view.findViewById(R.id.rb_star);
            teacherTitle = (TextView) view.findViewById(R.id.tv_teacher_view);
            scoreTv = (TextView) view.findViewById(R.id.tv_star);
            rateTv = (TextView) view.findViewById(R.id.tv_rate);
        }

        @Override
        public void setDataToView(PrepareLessonsShortEntity data, Context context) {
            switch (type) {
                case TeachingResearchBase.EVALUATION_LESSON:
                    teacherTitle.setText(Titles.sMasterTeacher);//"主讲教师");
                    if ("SCORE".equals(data.getScoreType())) {
                        rateTv.setVisibility(View.GONE);
                        ratingBar.setVisibility(View.GONE);
                        scoreTv.setText("评分   " + data.getAverageScore() + "/" + data.getTotalScore());
                    } else {
                        rateTv.setVisibility(View.VISIBLE);
                        ratingBar.setVisibility(View.VISIBLE);
                        scoreTv.setText("评分");
                        ratingBar.setRating((float) data.getAverageScore() / 2);
                    }
                    break;
                case TeachingResearchBase.INTERAC_LESSON:
                    teacherTitle.setText(Titles.sMasterTeacher);//"主讲教师");
                case TeachingResearchBase.PREPARE_LESSON:

                    break;
            }
            rateTv.setText(context.getString(R.string.d_score, data.getAverageScore()));
            title.setText(data.getTitle());
            teachName.setText(data.getMainTeacher());
            date.setText(DateTimeFormat.forPattern("yyyy-MM-dd").print(data.getStartTime()));
            clickCount.setText(String.valueOf(data.getViewCount()));
            ratingBar.setProgress(data.getAverageScore());
            ImageFetcher.getInstance(context).fetchImage(headerImage, data.getSubjectPic());
        }
    }

    public static void start(Context context, int type, String schoolId, String baseAreaId) {
        Intent intent = new Intent(context, CollectivePrepareLessonsNewActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("schoolId", schoolId);
        intent.putExtra("baseAreaId", baseAreaId);
        context.startActivity(intent);
    }
}
