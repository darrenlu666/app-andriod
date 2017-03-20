package com.codyy.erpsportal.homework.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.controllers.adapters.RecyclerBaseAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewBaseHolder;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.homework.models.entities.WorkStatisticInfo;
import com.codyy.erpsportal.homework.utils.WorkUtils;
import com.codyy.erpsportal.homework.widgets.RoundProgressBar;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 班级空间作业统计界面
 * created by ldh 2015/12/24
 */
public class WorkClassStatisticDetailActivity extends ToolbarActivity {

    private static final String TAG = WorkClassStatisticDetailActivity.class.getSimpleName();
    private static final String EXTRA_WORD_ID = "workId";
    private static final String EXTRA_CLASS_ID = "classId";
    private static final String EXTRA_CLASS_NAME = "className";
    private static final String EXTRA_IS_TEACHER_READ = "isTeacherRead";
    public static final int GREEN_LINE_COUNT = 60;

    @Bind(R.id.recycler_percent_statistic_work)
    RecyclerView mPercentStatisticRecyclerView;
    /**
     * 标题
     */
    @Bind(R.id.toolbar_title)
    protected TextView mTitle;
    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    @Bind(R.id.tv_work_name)
    TextView mWorkNameTv;//作业名称
    @Bind(R.id.tv_work_publish_time)
    TextView mPublishTimeTv;//发布时间
    @Bind(R.id.tv_work_item_count)
    TextView mItemCountTv;//作业题量
    @Bind(R.id.tv_work_objective_count)
    TextView mObjectiveItemCountTv;//客观题量
    @Bind(R.id.tv_work_subjective_count)
    TextView mSubjectiveItemCountTv;//主观题量
    @Bind(R.id.tv_work_finish_info)
    TextView mWorkFinishInfoTv;//完成情况
    @Bind(R.id.tv_total_percent)
    TextView mTotalPercentTv;//总正确率

    private WorkStatisticInfo mWorkStatisticInfo;
    private String mWorkId;//作业id
    private String mCurrentClassId;//当前选中的班级id
    private String mCurrentClassName;//当前选中的班级名称
    private String mIsTeacherRead;//是否是老师批阅
    private RoundProgressBar mRoundProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mWorkId = getIntent().getStringExtra(EXTRA_WORD_ID);
        mIsTeacherRead = getIntent().getStringExtra(EXTRA_IS_TEACHER_READ);
        loadClassData();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_work_statistic;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
    }

    private void loadStatisticsData() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        params.put("workId", mWorkId);
        params.put("baseClassId", mCurrentClassId);
        RequestSender requestSender = new RequestSender(this);
        requestSender.sendRequest(new RequestSender.RequestData(URLConfig.GET_HOMEWORK_STATISTICS_INFO, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.optString("result").equals("success") && mToolbar != null) {
                    mWorkStatisticInfo = WorkStatisticInfo.parseResponse(response);
                    initView();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Log.e(TAG, "数据获取失败！");
            }
        }));
    }

    /**
     * 获取班级列表
     */
    private void loadClassData() {
        if (getIntent().getExtras() != null) {
            mTitle.setText(getIntent().getExtras().getString(EXTRA_CLASS_NAME));
            mCurrentClassId = getIntent().getExtras().getString(EXTRA_CLASS_ID);
            loadStatisticsData();
        }
    }

    private void initView() {
        mWorkNameTv.setText(mWorkStatisticInfo.getWorkName());
        mPublishTimeTv.setText(mWorkStatisticInfo.getPublishTime());
        mItemCountTv.setText(mWorkStatisticInfo.getItemTotalCount());
        mObjectiveItemCountTv.setText(mWorkStatisticInfo.getObjectiveItemCount());
        mSubjectiveItemCountTv.setText(mWorkStatisticInfo.getSubjectiveItemCount());
        mWorkFinishInfoTv.setText(WorkUtils.switchStr(mWorkStatisticInfo.getFinishPercent(), getResources().getColor(R.color.work_statistic_circle_color_2)));
        mTotalPercentTv.setText(getResources().getString(R.string.work_total_accuracy_statistic, mWorkStatisticInfo.getTotalPercent().equals("") ? "0%" : mWorkStatisticInfo.getTotalPercent()));//getResources().getString(R.string.work_total_accuracy_statistic) + "  " + mWorkStatisticInfo.getTotalPercent()
        mPercentStatisticRecyclerView.setAdapter(new RecyclerBaseAdapter<WorkStatisticInfo.ListEntity>(this, R.layout.item_percent_statistic, mWorkStatisticInfo.getList()) {
            @Override
            public void onBindView(RecyclerViewBaseHolder holder, final int position) {
                ((TextView) holder.getView(R.id.tv_item_num)).setText(mWorkStatisticInfo.getList().get(position).getItemIndex());
                mRoundProgressBar = holder.getView(R.id.roundProgressBar);
                String progress = mWorkStatisticInfo.getList().get(position).getItemAccuary();
                if (progress.contains("%")) {
                    int result = Integer.valueOf(progress.substring(0, progress.indexOf("%")));
                    if (result < GREEN_LINE_COUNT) {
                        mRoundProgressBar.setCricleProgressColor(getResources().getColor(R.color.work_statistic_circle_color_2));
                        mRoundProgressBar.setTextColor(getResources().getColor(R.color.black));
                    } else {
                        mRoundProgressBar.setTextColor(getResources().getColor(R.color.work_statistic_circle_color));
                    }
                    mRoundProgressBar.setProgress(result);
                }
            }
        });
        mPercentStatisticRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
    }

    //主界面-》统计详情界面
    public static void startActivity(Context context,String classId, String className, String workId, String readType) {
        Intent intent = new Intent(context, WorkClassStatisticDetailActivity.class);
        intent.putExtra(EXTRA_WORD_ID, workId);
        intent.putExtra(EXTRA_CLASS_ID,classId);
        intent.putExtra(EXTRA_CLASS_NAME, className);
        intent.putExtra(EXTRA_IS_TEACHER_READ, readType);
        context.startActivity(intent);
    }

    //点击“完成情况”跳转-》完成情况详情列表
    @OnClick(R.id.rl_finish_info)
    void OnClick() {
        int finishCount = Integer.valueOf(mWorkStatisticInfo.getFinishPercent().substring(0, mWorkStatisticInfo.getFinishPercent().indexOf("/")));//当为0时，不跳转
        if (finishCount > 0) {
            WorkStatisticListActivity.startActivity(this, mCurrentClassName, mWorkId, mCurrentClassId, WorkStatisticListActivity.FROM_FINISH_INFO, mIsTeacherRead);
        } else {
            ToastUtil.showToast(this, getResources().getString(R.string.work_static_no_finish));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
