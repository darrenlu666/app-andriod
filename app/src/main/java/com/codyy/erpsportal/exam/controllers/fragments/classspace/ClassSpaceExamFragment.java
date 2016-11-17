package com.codyy.erpsportal.exam.controllers.fragments.classspace;

import android.content.Context;
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
import com.codyy.erpsportal.exam.controllers.activities.classroom.ExamClassSpaceDetailActivity;
import com.codyy.erpsportal.exam.models.entities.school.ExamSchoolClass;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 班级测试列表碎片
 *
 * @author eachann
 * @date 2015-12-25
 */
public class ClassSpaceExamFragment extends LoadMoreFragment<ExamSchoolClass, ClassSpaceExamFragment.ExamSchoolClassViewHolder> implements TabsWithFilterActivity.OnFilterObserver {

    private static final String TAG = ClassSpaceExamFragment.class.getSimpleName();

    @Override
    protected void extraInitViewsStyles() {
        super.extraInitViewsStyles();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
    }

    @Override
    protected ViewHolderCreator<ExamSchoolClassViewHolder> newViewHolderCreator() {
        return new ViewHolderCreator<ExamSchoolClassViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_exam_student_task;
            }

            @Override
            protected ExamSchoolClassViewHolder doCreate(View view) {
                return new ExamSchoolClassViewHolder(view, getArguments().getString("ARG_CLASS_ID"));
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.CLASS_EXAM_LIST;
    }

    @Override
    protected void addParams(Map<String, String> map) {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        addParam("classId", getArguments().getString("ARG_CLASS_ID"));
    }

    /**
     * 确认过滤时调用
     *
     * @param params 过滤参数，用于附加到url请求参数列表，实现过滤。
     */
    @Override
    public void onFilterConfirmed(Map<String, String> params) {
        addParam("subjectId", TextUtils.isEmpty(params.get("subjectId")) ? "" : params.get("subjectId"));
        addParam("state", TextUtils.isEmpty(params.get("schoolClsState")) ? "" : params.get("schoolClsState"));
        loadData(true);
    }

    @Override
    protected List<ExamSchoolClass> getList(JSONObject response) {
        return ExamSchoolClass.getExamSchoolClass(response);
    }

    public static class ExamSchoolClassViewHolder extends RecyclerViewHolder<ExamSchoolClass> {

        private TextView holderOneTv;
        private TextView holderTwoTv;
        private TextView holderThrTv;
        private TextView holderForTv;
        private TextView holderFivTv;

        private TextView examUnifiedTv;
        private View container;
        private static final String STATE_INIT = "INIT";
        private static final String STATE_PROGRESS = "PROGRESS";
        private static final String STATE_END = "END";
        private Context context;
        private String classId;

        public ExamSchoolClassViewHolder(View itemView, String classId) {
            super(itemView);
            this.context = itemView.getContext();
            this.classId = classId;
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
        public void setDataToView(final ExamSchoolClass data) {
            holderOneTv.setText(data.getExamName());
            holderTwoTv.setText(data.getExamSubject() + "/" + data.getClassSpaceType());
            holderForTv.setText("开始时间" + data.getExamStartTime());
            switch (data.getExamState()) {
                case STATE_INIT:
                    holderFivTv.setText(context.getResources().getStringArray(R.array.school_states_name)[0]);
                    holderFivTv.setTextColor(context.getResources().getColor(R.color.exam_read_waiting));
                    break;
                case STATE_PROGRESS:
                    holderFivTv.setText(context.getResources().getStringArray(R.array.school_states_name)[1]);
                    holderFivTv.setTextColor(context.getResources().getColor(R.color.main_color));
                    break;
                case STATE_END:
                    holderFivTv.setText(context.getResources().getStringArray(R.array.school_states_name)[2]);
                    holderFivTv.setTextColor(context.getResources().getColor(R.color.exam_divider));
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
                    if (STATE_END.equals(data.getExamState())) {
                        ExamClassSpaceDetailActivity.startActivity(context, holderOneTv.getText().toString(), data.getExamTaskId(), data.getExamId(), null, classId);
                    }
                }
            });
        }
    }

}
