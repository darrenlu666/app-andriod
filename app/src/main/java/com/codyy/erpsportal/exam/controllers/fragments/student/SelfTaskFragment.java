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
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.exam.controllers.activities.student.StudentAnswerDetailActivity;
import com.codyy.erpsportal.exam.controllers.activities.student.StudentViewCheckedActivity;
import com.codyy.erpsportal.exam.models.entities.student.ExamStudentSelfTask;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 学生-自主测试任务列表碎片
 *
 * @author eachann
 * @date 2015-12-28
 */
public class SelfTaskFragment extends LoadMoreFragment<ExamStudentSelfTask, SelfTaskFragment.ExamStudentSelfTaskViewHolder> implements TabsWithFilterActivity.OnFilterObserver {

    private static final String TAG = SelfTaskFragment.class.getSimpleName();

    public SelfTaskFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(String action) {
        if (StudentAnswerDetailActivity.class.getSimpleName().equals(action))
            loadData(true);
    }

    @Override
    protected void extraInitViewsStyles() {
        super.extraInitViewsStyles();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
    }

    @Override
    protected ViewHolderCreator<ExamStudentSelfTaskViewHolder> newViewHolderCreator() {
        return new ViewHolderCreator<ExamStudentSelfTaskViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_exam_student_self;
            }

            @Override
            protected ExamStudentSelfTaskViewHolder doCreate(View view) {
                return new ExamStudentSelfTaskViewHolder(view);
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.LIST_SELF_TASK_EXAM;
    }

    @Override
    protected List<ExamStudentSelfTask> getList(JSONObject response) {
        Cog.i(TAG, response.toString());
        return ExamStudentSelfTask.getExamStudentSelfTask(response);
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
        addParam("state", TextUtils.isEmpty(params.get("stateSelf")) ? "" : params.get("stateSelf"));
        addParam("subjectId", TextUtils.isEmpty(params.get("subjectId")) ? "" : params.get("subjectId"));
        loadData(true);
    }

    public static class ExamStudentSelfTaskViewHolder extends RecyclerViewHolder<ExamStudentSelfTask> {
        private TextView holderOneTv;
        private TextView holderTwoTv;
        private TextView holderThrTv;
        private TextView holderForTv;
        private TextView holderFivTv;
        private View container;
        private static final String STATE_INIT = "INIT";
        private static final String STATE_CHECKED = "CHECKED";
        private Context mContext;

        public ExamStudentSelfTaskViewHolder(View itemView) {
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
        }

        @Override
        public void setDataToView(final ExamStudentSelfTask data) {
            holderOneTv.setText(data.getExamName());
            holderTwoTv.setText(data.getExamSubject());
            holderForTv.setText("开始时间 " + data.getExamTime());
            switch (data.getExamState()) {
                case STATE_INIT:
                    holderFivTv.setText(mContext.getResources().getStringArray(R.array.self_states_name)[0]);
                    holderFivTv.setTextColor(mContext.getResources().getColor(R.color.exam_un_finish_red));
                    break;
                case STATE_CHECKED:
                    holderFivTv.setText(mContext.getResources().getStringArray(R.array.self_states_name)[1]);
                    holderFivTv.setTextColor(mContext.getResources().getColor(R.color.main_color));
                    break;
            }
            container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (data.getExamState()) {
                        case STATE_INIT:
                            /*if (data.getExamSubject().contains("数学") || data.getExamSubject().contains("物理") || data.getExamSubject().contains("化学") || data.getExamSubject().contains("生物") || data.getExamSubject().contains("几何") || data.getExamSubject().contains("代数")) {
                                ToastUtil.showToast("为便捷公式输入,请至网页作答");
                                return;
                            }*/
                            StudentAnswerDetailActivity.startActivity((Activity) mContext, holderOneTv.getText().toString(), data.getExamTaskId(), null, data.getExamResultId(), data.getDuration(), data.getExamSubject());
                            break;
                        case STATE_CHECKED:
                            StudentViewCheckedActivity.startActivity((Activity) mContext, holderOneTv.getText().toString(), data.getExamTaskId(), null, null);
                            break;
                    }
                }
            });
        }
    }

}
