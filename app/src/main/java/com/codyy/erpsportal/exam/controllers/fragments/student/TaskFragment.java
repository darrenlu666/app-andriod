package com.codyy.erpsportal.exam.controllers.fragments.student;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsVhrCreator;
import com.codyy.erpsportal.exam.controllers.activities.student.StudentAnswerDetailActivity;
import com.codyy.erpsportal.exam.controllers.activities.student.StudentPracticeDetailActivity;
import com.codyy.erpsportal.exam.controllers.activities.student.StudentViewCheckedActivity;
import com.codyy.erpsportal.exam.controllers.activities.student.StudentViewWaitActivity;
import com.codyy.erpsportal.exam.models.entities.student.ExamStudentTask;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 学生-测试任务列表碎片
 *
 * @author eachann
 * @date 2015-12-28
 */
public class TaskFragment extends LoadMoreFragment<ExamStudentTask, TaskFragment.ExamStudentTaskViewHolder> implements TabsWithFilterActivity.OnFilterObserver {

    private static final String TAG = TaskFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onEventMainThread(String action) {
        if (StudentAnswerDetailActivity.TAG.equals(action) || StudentPracticeDetailActivity.TAG.equals(action))
            loadData(true);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void extraInitViewsStyles() {
        super.extraInitViewsStyles();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
    }

    @Override
    protected AbsVhrCreator<ExamStudentTaskViewHolder> newViewHolderCreator() {
        return new AbsVhrCreator<ExamStudentTaskViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_exam_student_task;
            }

            @Override
            protected ExamStudentTaskViewHolder doCreate(View view) {
                return new ExamStudentTaskViewHolder(view);
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.LIST_TASK_EXAM;
    }

    @Override
    protected List<ExamStudentTask> getList(JSONObject response) {
        Cog.i(TAG, response.toString());
        return ExamStudentTask.getExamStudentTask(response);
    }

    @Override
    protected void addParams(Map<String, String> map) {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
    }

    /**
     * 确认过滤时调用
     *
     * @param params 过滤参数，用于附加到url请求参数列表，实现过滤。
     */
    @Override
    public void onFilterConfirmed(Map<String, String> params) {
        addParam("state", TextUtils.isEmpty(params.get("state")) ? "" : params.get("state"));
        addParam("subjectId", TextUtils.isEmpty(params.get("subjectId")) ? "" : params.get("subjectId"));
        loadData(true);
    }

    public static class ExamStudentTaskViewHolder extends RecyclerViewHolder<ExamStudentTask> {

        private TextView holderOneTv;
        private TextView holderTwoTv;
        private TextView holderThrTv;
        private TextView holderForTv;
        private TextView holderFivTv;
        private TextView examUnifiedTv;
        private View container;
        /**
         * 未完成
         */
        private static final String STATE_INIT = "INIT";
        /**
         * 已提交，待批阅
         */
        private static final String STATE_SUBMITTED = "SUBMITTED";
        /**
         * 已批阅
         */
        private static final String STATE_CHECKED = "CHECKED";
        private Context mContext;

        public ExamStudentTaskViewHolder(View itemView) {
            super(itemView);
            this.mContext = itemView.getContext();
        }

        @Override
        public void mapFromView(View view) {
            container = view;
            holderOneTv = (TextView) view.findViewById(R.id.tv_holder_1);
            holderTwoTv = (TextView) view.findViewById(R.id.tv_holder_2);
            holderThrTv = (TextView) view.findViewById(R.id.tv_holder_3);
            holderForTv = (TextView) view.findViewById(R.id.tv_holder_4);
            holderFivTv = (TextView) view.findViewById(R.id.tv_holder_5);
            examUnifiedTv = (TextView) view.findViewById(R.id.tv_is_unified);
        }

        @Override
        public void setDataToView(final ExamStudentTask data) {
            holderOneTv.setText(data.getExamName());
            holderTwoTv.setText(data.getExamSubject());
            holderForTv.setText("开始时间 " + data.getExamStartTime());
            switch (data.getExamState()) {
                case STATE_INIT:
                    holderFivTv.setText(mContext.getResources().getStringArray(R.array.states_name)[0]);
                    holderFivTv.setTextColor(mContext.getResources().getColor(R.color.exam_un_finish_red));
                    break;
                case STATE_CHECKED:
                    holderFivTv.setText(mContext.getResources().getStringArray(R.array.states_name)[2]);
                    holderFivTv.setTextColor(mContext.getResources().getColor(R.color.main_color));
                    break;
                case STATE_SUBMITTED:
                    holderFivTv.setText(mContext.getResources().getStringArray(R.array.states_name)[1]);
                    holderFivTv.setTextColor(mContext.getResources().getColor(R.color.exam_read_waiting));
                    break;
            }
            if (data.isUnified()) {
                examUnifiedTv.setVisibility(View.VISIBLE);
            } else {
                examUnifiedTv.setVisibility(View.GONE);
            }

            container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (data.getExamState()) {
                        case STATE_INIT://未完成
                            String time = data.getExamStartTime().replace("开始时间 ", "");
                            if (System.currentTimeMillis() > DateUtil.stringToLong(time, DateUtil.DEF_FORMAT)) {
                                StudentAnswerDetailActivity.startActivity((Activity) mContext, holderOneTv.getText().toString(), data.getExamTaskId(), data.getPracticeStatus(), data.getExamResultId());
                            } else {
                                ToastUtil.showToast(mContext.getString(R.string.exam_answer_time_yet_to_come));
                            }
                            break;
                        case STATE_CHECKED://已完成
                            StudentViewCheckedActivity.startActivity((Activity) mContext, holderOneTv.getText().toString(), data.getExamTaskId(), data.getPracticeStatus(), data.getExamPracticeId());
                            break;
                        case STATE_SUBMITTED://待批阅
                            StudentViewWaitActivity.startViewWaitActivity(mContext, holderOneTv.getText().toString(), data.getExamTaskId(), false, 1);
                            break;
                    }
                }
            });
        }
    }

}
