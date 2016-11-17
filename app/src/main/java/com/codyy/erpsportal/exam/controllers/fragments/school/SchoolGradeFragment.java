package com.codyy.erpsportal.exam.controllers.fragments.school;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.exam.controllers.activities.school.SchoolGradeDetailActivity;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.exam.models.entities.school.ExamSchoolGrade;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 年级统考列表碎片
 *
 * @author eachann
 * @date 2015-12-25
 */
public class SchoolGradeFragment extends LoadMoreFragment<ExamSchoolGrade, SchoolGradeFragment.ExamSchoolGradeViewHolder> implements TabsWithFilterActivity.OnFilterObserver {

    private static final String TAG = SchoolGradeFragment.class.getSimpleName();

    @Override
    protected void extraInitViewsStyles() {
        super.extraInitViewsStyles();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

    }

    @Override
    protected ViewHolderCreator<ExamSchoolGradeViewHolder> newViewHolderCreator() {
        return new ViewHolderCreator<ExamSchoolGradeViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_exam_school_grade;
            }

            @Override
            protected ExamSchoolGradeViewHolder doCreate(View view) {
                return new ExamSchoolGradeViewHolder(view);
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.CLASS_LEVEL_EXAM;
    }

    @Override
    protected List<ExamSchoolGrade> getList(JSONObject response) {
        return ExamSchoolGrade.getExamSchoolGrade(response);
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

    public static class ExamSchoolGradeViewHolder extends RecyclerViewHolder<ExamSchoolGrade> {
        private TextView holderOneTv;
        private TextView holderTwoTv;
        private TextView holderThrTv;
        private TextView holderForTv;
        private TextView holderFivTv;

        private View container;
        private Context context;

        public ExamSchoolGradeViewHolder(View itemView) {
            super(itemView);
            this.context = itemView.getContext();
        }

        @Override
        public void mapFromView(View view) {
            container = view;
            holderOneTv = (TextView) view.findViewById(R.id.tv_holder_1);
            holderTwoTv = (TextView) view.findViewById(R.id.tv_holder_2);
            holderThrTv = (TextView) view.findViewById(R.id.tv_holder_3);
            holderForTv = (TextView) view.findViewById(R.id.tv_holder_4);
            holderFivTv = (TextView)view.findViewById(R.id.tv_holder_5);
            holderTwoTv.setVisibility(View.GONE);
            holderFivTv.setVisibility(View.GONE);
        }

        @Override
        public void setDataToView(final ExamSchoolGrade data) {
            holderOneTv.setText(data.getExamName());
            holderForTv.setText(data.getExamGrade()+"/"+data.getExamSubject()+"/"+data.getExamType());
            //holderThrTv.setText(data.getExamGrade()+"/"+data.getExamSubject()+"/"+data.getExamType());
            //holderForTv.setText(data.getStartTime());
            container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SchoolGradeDetailActivity.startActivity(context, holderOneTv.getText().toString(), data.getExamId(), true, 0, URLConfig.CLASS_LEVEL_EXAM_DETAIL);
                }
            });
        }
    }

}
