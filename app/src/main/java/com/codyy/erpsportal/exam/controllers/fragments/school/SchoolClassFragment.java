package com.codyy.erpsportal.exam.controllers.fragments.school;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.exam.controllers.activities.school.SchoolClassDetailActivity;
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
public class SchoolClassFragment extends LoadMoreFragment<ExamSchoolClass, SchoolClassFragment.ExamSchoolClassViewHolder> implements TabsWithFilterActivity.OnFilterObserver {

    private static final String TAG = SchoolClassFragment.class.getSimpleName();

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
                return R.layout.item_exam_school;
            }

            @Override
            protected ExamSchoolClassViewHolder doCreate(View view) {
                return new ExamSchoolClassViewHolder(view);
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.CLASS_TEST_EXAM;
    }

    @Override
    protected void addParams(Map<String, String> map) {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
    }

    @Override
    protected List<ExamSchoolClass> getList(JSONObject response) {
        return ExamSchoolClass.getExamSchoolClass(response);
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
        addParam("classId", TextUtils.isEmpty(params.get("classId")) ? "" : params.get("classId"));
        addParam("state", TextUtils.isEmpty(params.get("schoolClsState")) ? "" : params.get("schoolClsState"));
        loadData(true);
    }


    public static class ExamSchoolClassViewHolder extends RecyclerViewHolder<ExamSchoolClass> {

        private TextView holderOneTv;
        private TextView holderTwoTv;
        private TextView holderThrTv;
        private TextView holderForTv;
        private TextView holderFivTv;
        private ImageView examStateIv;
        private TextView examUnifiedTv;
        private View container;
        private static final String STATE_INIT = "INIT";
        private static final String STATE_PROGRESS = "PROGRESS";
        private static final String STATE_END = "END";
        private Context context;

        public ExamSchoolClassViewHolder(View itemView) {
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
            holderFivTv = (TextView) view.findViewById(R.id.tv_holder_5);
            examUnifiedTv = (TextView) view.findViewById(R.id.tv_is_unified);
        }

        @Override
        public void setDataToView(final ExamSchoolClass data) {
            holderOneTv.setText(data.getExamName());
            holderTwoTv.setText(data.getExamGrade()+"/"+data.getExamSubject()+"/"+data.getExamType());
            //holderThrTv.setText(data.getExamGrade()+"/"+data.getExamSubject()+"/"+data.getExamType());
            holderForTv.setText("开始时间 "+data.getExamStartTime());
            switch (data.getExamState()) {
                case STATE_INIT:
                    holderFivTv.setText(context.getResources().getStringArray(R.array.school_states_name)[0]);
                    holderFivTv.setTextColor(context.getResources().getColor(R.color.video_text_selected));
                    break;
                case STATE_PROGRESS:
                    holderFivTv.setText(context.getResources().getStringArray(R.array.school_states_name)[1]);
                    holderFivTv.setTextColor(context.getResources().getColor(R.color.main_color));
                    break;
                case STATE_END:
                    holderFivTv.setText(context.getResources().getStringArray(R.array.school_states_name)[2]);
                    holderFivTv.setTextColor(context.getResources().getColor(R.color.gray));
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
                        SchoolClassDetailActivity.startActivity(context, holderOneTv.getText().toString(), data.getExamTaskId(), data.getExamId(), null);
                    }
                }
            });
        }
    }

}
