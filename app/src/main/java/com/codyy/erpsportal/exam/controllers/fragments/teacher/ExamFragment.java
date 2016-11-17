package com.codyy.erpsportal.exam.controllers.fragments.teacher;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.exam.controllers.activities.school.SchoolClassDetailActivity;
import com.codyy.erpsportal.exam.controllers.activities.school.SchoolGradeDetailActivity;
import com.codyy.erpsportal.exam.controllers.activities.student.StudentReadActivity;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.exam.models.entities.teacher.ExamTeacherExam;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 测试任务列表碎片
 *
 * @author eachann
 * @date 2015-12-25
 */
public class ExamFragment extends LoadMoreFragment<ExamTeacherExam, ExamFragment.ExamTeacherExamViewHolder> implements TabsWithFilterActivity.OnFilterObserver {

    private static final String TAG = ExamFragment.class.getSimpleName();

    @Override
    protected void extraInitViewsStyles() {
        super.extraInitViewsStyles();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
    }

    @Override
    protected ViewHolderCreator<ExamTeacherExamViewHolder> newViewHolderCreator() {
        return new ViewHolderCreator<ExamTeacherExamViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_exam_teacher_exam;
            }

            @Override
            protected ExamTeacherExamViewHolder doCreate(View view) {
                return new ExamTeacherExamViewHolder(view);
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.GET_CLASS_EXAM_LIST;
    }

    @Override
    protected List<ExamTeacherExam> getList(JSONObject response) {
        return ExamTeacherExam.getExamTeacherExam(response);
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
        addParam("classlevelId", TextUtils.isEmpty(params.get("classLevelId")) ? "" : params.get("classLevelId"));
        addParam("subjectId", TextUtils.isEmpty(params.get("subjectId")) ? "" : params.get("subjectId"));
        loadData(true);
    }

    public static class ExamTeacherExamViewHolder extends RecyclerViewHolder<ExamTeacherExam> {

        private TextView holderOneTv;
        private TextView holderTwoTv;
        private TextView holderThrTv;
        private TextView holderForTv;

        private TextView examUnifiedTv;
        private TextView examReadTv;
        private TextView examAnalysisTv;

        private View container;
        private Context context;

        public ExamTeacherExamViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
        }

        @Override
        public void mapFromView(View view) {
            container = view;
            holderOneTv = (TextView) view.findViewById(R.id.tv_holder_1);
            holderTwoTv = (TextView) view.findViewById(R.id.tv_holder_2);
            holderThrTv = (TextView) view.findViewById(R.id.tv_holder_3);
            holderForTv = (TextView) view.findViewById(R.id.tv_holder_4);
            examUnifiedTv = (TextView) view.findViewById(R.id.tv_is_unified);
            examReadTv = (TextView) view.findViewById(R.id.tv_read);
            examAnalysisTv = (TextView) view.findViewById(R.id.tv_analysis);
        }

        @Override
        public void setDataToView(final ExamTeacherExam data) {
            holderOneTv.setText(data.getExamName());
            holderTwoTv.setText(data.getExamGrade()+"/"+data.getExamType());
            //holderThrTv.setText(data.getExamGrade()+"/"+data.getExamType());
            holderForTv.setText("开始时间 "+data.getStartTime());
            if (data.isUnified()) {
                examUnifiedTv.setVisibility(View.VISIBLE);
            } else {
                examUnifiedTv.setVisibility(View.GONE);
            }
            /**
             * 点击列表
             */
            container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SchoolGradeDetailActivity.startTaskActivity(context, holderOneTv.getText().toString(), data.getExamTaskId(), false, 1, URLConfig.CLASS_TEST_EXAM_DETAIL);
                }
            });
            /**
             * 点击批阅
             */
            examReadTv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.getStatus().equals("1")) {
                        StudentReadActivity.startActivity(context, data.getExamTaskId(), data.getClasslevelId());
                    } else {
                        ToastUtil.showToast("当前没有可批阅的学生");
                    }

                    UIUtils.addEnterAnim((Activity) context);
                }
            });
            /**
             * 统计
             */
            examAnalysisTv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SchoolClassDetailActivity.startActivity(context, holderOneTv.getText().toString(), data.getExamTaskId(), data.getExamId(), "NO_EXAM_DETAIL");
                }
            });
        }
    }

}
