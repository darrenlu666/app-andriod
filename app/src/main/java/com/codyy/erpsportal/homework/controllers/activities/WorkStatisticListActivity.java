package com.codyy.erpsportal.homework.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.controllers.adapters.RecyclerBaseAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewBaseHolder;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.homework.controllers.fragments.WorkItemDetailFragment;
import com.codyy.erpsportal.homework.models.entities.WorkFinishInfoList;
import com.codyy.erpsportal.homework.utils.WorkUtils;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * 完成情况详情列表 （共用：学生我的批阅-》批阅/查看批阅列表）
 * created by ldh
 * 2016/01/05
 */
public class WorkStatisticListActivity extends ToolbarActivity {

    private static final String TAG = WorkStatisticListActivity.class.getSimpleName();
    public static final String FROM_FINISH_INFO = "from_finish_info";//来自统计中的完成情况
    public static final String FROM_STU_READ = "from_stu_read";//来自我的批阅的“批阅”
    public static final String FROM_STU_READ_BROWSER = "from_stu_read_browser";//来自我的批阅的“查看批阅”

    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_WORK_ID = "workId";
    private static final String EXTRA_CLASS_ID = "classId";
    private static final String EXTRA_FROM = "from";
    private static final String EXTRA_IS_TEACHER_READ = "isTeacherRead";

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    protected TextView mTitleTv;//标题
    @Bind(R.id.tv_no_data)
    protected TextView mNoDataTv;//无数据界面
    private RecyclerView mRecyclerView;//列表内容

    private List<WorkFinishInfoList> mData;
    private RecyclerBaseAdapter mAdapter;
    private String mWorkId;//作业ID
    private String mClassId;//班级ID
    private String mFrom;//跳转来源
    private String mIsTeacherRead;//是否是老师批阅

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mTitle = getIntent().getExtras().getString(EXTRA_TITLE);
        mWorkId = getIntent().getExtras().getString(EXTRA_WORK_ID);
        mClassId = getIntent().getExtras().getString(EXTRA_CLASS_ID);
        mFrom = getIntent().getExtras().getString(EXTRA_FROM);
        mIsTeacherRead = getIntent().getExtras().getString(EXTRA_IS_TEACHER_READ);
        //设置界面标题
        mTitleTv.setText(mTitle);
        //详情列表
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        loadData();
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_work_statistic_list;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
    }

    private void loadData() {
        if (mFrom.equals(FROM_FINISH_INFO)) {
            loadFinishInfoData();//加载完成情况的列表
        } else if (mFrom.equals(FROM_STU_READ)) {
            loadReadInfoData(false);//加载我的批阅 批阅列表
        } else if (mFrom.equals(FROM_STU_READ_BROWSER)) {
            loadReadInfoData(true);//加载我的批阅 查看批阅列表
        }

    }

    //加载完成情况的列表
    private void loadFinishInfoData() {
        RequestSender requestSender = new RequestSender(this);
        Map<String, String> params = new HashMap<>();
        params.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        params.put("workId", mWorkId);
        params.put("baseClassId", mClassId);
        requestSender.sendRequest(new RequestSender.RequestData(URLConfig.GET_FINISH_INFO_LIST, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, response);
                if (response.optString("result").equals("success") && mToolbar != null) {
                    mData = WorkFinishInfoList.parseFinishInfoList(response);
                    if (mData != null) {
                        if (mData.size() > 0) {
                            initView(WorkItemDetailFragment.STATUS_TEACHER_STATISTIC);
                        } else {
                            mNoDataTv.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Log.e(TAG, "数据获取失败！");
            }
        }));
    }

    //加载我的批阅 批阅列表/查看批阅列表
    private void loadReadInfoData(final boolean isRead) {
        RequestSender requestSender = new RequestSender(this);
        Map<String, String> params = new HashMap<>();
        params.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        params.put("workId", mWorkId);
        params.put("baseUserId", UserInfoKeeper.obtainUserInfo().getBaseUserId());
        params.put("isRead", isRead ? "Y" : "N");
        requestSender.sendRequest(new RequestSender.RequestData(URLConfig.GET_STU_READ_INF0_LIST, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, response);
                if ("success".equals(response.optString("result")) && mToolbar != null) {
                    mData = WorkFinishInfoList.parseFinishInfoList(response);
                    if (mData == null) return;
                    if (mData.size() > 0) {
                        initView(isRead ? WorkItemDetailFragment.STATUS_STU_READ_BROWSER : WorkItemDetailFragment.STATUS_STU_READ);
                    } else {
                        mNoDataTv.setVisibility(View.VISIBLE);
                        if (mAdapter != null) {
                            mAdapter.setData(mData);
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Log.e(TAG, "数据获取失败！");
            }
        }));
    }

    private void initView(final String status) {
        if (mAdapter == null) {
            mAdapter = new RecyclerBaseAdapter<WorkFinishInfoList>(this, R.layout.item_work_statistic_list, mData) {
                @Override
                public void onBindView(RecyclerViewBaseHolder holder, final int position) {
                    ((TextView) holder.getView(R.id.tv_name)).setText(mData.get(position).getStudentName());
                    ((TextView) holder.getView(R.id.tv_done_percent)).setText(WorkUtils.switchStr(mData.get(position).getDonePercent(), getResources().getColor(R.color.work_statistic_circle_color_2)));
                    if (mData.get(position).getObjectiveRightPercent().equals("%")) {
                        ((TextView) holder.getView(R.id.tv_objective_right_percent)).setText("0%");
                    } else {
                        ((TextView) holder.getView(R.id.tv_objective_right_percent)).setText(WorkUtils.roundFloat(mData.get(position).getObjectiveRightPercent()));
                    }
                    ((TextView) holder.getView(R.id.tv_submit_time)).setText(mData.get(position).getSubmitTime());
                    holder.getmConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            WorkItemDetailActivity.startActivity(WorkStatisticListActivity.this, mWorkId, status, mData.get(position).getStudentId(), mIsTeacherRead, mData.get(position).getStudentName());
                        }
                    });
                }
            };
        } else {
            mAdapter.setData(mData);
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    /**
     * 统计详情页 -》完成情况列表
     *
     * @param context
     * @param title   班级名称
     * @param workId  作业id
     * @param classId 班级id
     */
    public static void startActivity(Context context, String title, String workId, String classId, String from, String readType) {
        Intent intent = new Intent(context, WorkStatisticListActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_WORK_ID, workId);
        intent.putExtra(EXTRA_CLASS_ID, classId);
        intent.putExtra(EXTRA_FROM, from);
        intent.putExtra(EXTRA_IS_TEACHER_READ, readType);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(String action) {
        if ((WorkItemDetailActivity.class.getSimpleName() + " read N").equals(action)) {
            ToastUtil.showToast(this, getResources().getString(R.string.work_answer_submit_success));
            loadReadInfoData(false);//加载我的批阅 批阅列表
        } else if (mFrom.equals(FROM_STU_READ_BROWSER)) {
            loadReadInfoData(true);//加载我的批阅 查看批阅列表
        }
    }
}
