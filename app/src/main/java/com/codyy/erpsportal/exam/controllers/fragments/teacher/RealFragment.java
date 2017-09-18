package com.codyy.erpsportal.exam.controllers.fragments.teacher;

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
import com.codyy.erpsportal.exam.controllers.activities.school.SchoolGradeDetailActivity;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsVhrCreator;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.exam.models.entities.teacher.ExamTeacherReal;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 真题试卷列表碎片
 *
 * @author eachann
 * @date 2015-12-25
 */
public class RealFragment extends LoadMoreFragment<ExamTeacherReal, RealFragment.ExamTeacherRealViewHolder> implements TabsWithFilterActivity.OnFilterObserver {

    private static final String TAG = RealFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void extraInitViewsStyles() {
        super.extraInitViewsStyles();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
    }

    @Override
    protected AbsVhrCreator<ExamTeacherRealViewHolder> newViewHolderCreator() {
        return new AbsVhrCreator<ExamTeacherRealViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_exam_teacher_real;
            }

            @Override
            protected ExamTeacherRealViewHolder doCreate(View view) {
                return new ExamTeacherRealViewHolder(view);
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.GET_REAL_EXAM_LIST;
    }

    @Override
    protected List<ExamTeacherReal> getList(JSONObject response) {
        return ExamTeacherReal.getExamTeacherReal(response);
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
        addParam("areaName", TextUtils.isEmpty(params.get("areaName")) ? "" : params.get("areaName"));
        addParam("year", TextUtils.isEmpty(params.get("year")) ? "" : params.get("year"));
        loadData(true);
    }

    public static class ExamTeacherRealViewHolder extends RecyclerViewHolder<ExamTeacherReal> {

        private TextView holderOneTv;
        private TextView holderTwoTv;
        private TextView holderThrTv;
        private TextView holderForTv;
        private TextView holderFivTv;

        private View container;
        private Context mContext;

        public ExamTeacherRealViewHolder(View itemView) {
            super(itemView);
            this.mContext = itemView.getContext();
        }

        @Override
        public void mapFromView(View view) {
            container = view;
            holderOneTv = (TextView) view.findViewById(R.id.tv_holder_1);
            holderTwoTv = (TextView) view.findViewById(R.id.tv_holder_2);
            holderThrTv = (TextView) view.findViewById(R.id.tv_holder_3);
            holderForTv = (TextView)view.findViewById(R.id.tv_holder_4);
            holderFivTv = (TextView)view.findViewById(R.id.tv_holder_5);
            holderTwoTv.setVisibility(View.GONE);
            holderFivTv.setVisibility(View.GONE);
        }

        @Override
        public void setDataToView(final ExamTeacherReal data) {
            holderOneTv.setText(data.getExamName());
            holderForTv.setText(data.getExamArea()+"/"+data.getExamYear()+"/"+data.getExamGrade()+"/"+data.getExamSubject());
            //holderThrTv.setText(data.getExamArea()+"/"+data.getExamYear()+"/"+data.getExamGrade()+"/"+data.getExamSubject());
            container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SchoolGradeDetailActivity.startActivity(mContext, holderOneTv.getText().toString(), data.getExamId(), false, 3, URLConfig.MINE_TEST_EXAM_DETAIL);
                }
            });
        }
    }

}
