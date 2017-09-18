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
import com.codyy.erpsportal.exam.controllers.activities.teacher.TeacherMineDetailActivity;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsVhrCreator;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.exam.models.entities.teacher.ExamTeacherMine;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 我的试卷列表碎片
 *
 * @author eachann
 * @date 2015-12-25
 */
public class MineFragment extends LoadMoreFragment<ExamTeacherMine, MineFragment.ExamTeacherMineViewHolder> implements TabsWithFilterActivity.OnFilterObserver {

    private static final String TAG = MineFragment.class.getSimpleName();

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
    protected AbsVhrCreator<ExamTeacherMineViewHolder> newViewHolderCreator() {
        return new AbsVhrCreator<ExamTeacherMineViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_exam_teacher_mine;
            }

            @Override
            protected ExamTeacherMineViewHolder doCreate(View view) {
                return new ExamTeacherMineViewHolder(view);
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.MINE_TEST_EXAM;
    }

    @Override
    protected List<ExamTeacherMine> getList(JSONObject response) {
        Cog.i(TAG, response.toString());
        return ExamTeacherMine.getExamTeacherMine(response);
    }

    @Override
    protected void addParams(Map<String, String> params) {
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

    public static class ExamTeacherMineViewHolder extends RecyclerViewHolder<ExamTeacherMine> {
        private TextView holderOneTv;
        private TextView holderTwoTv;
        private TextView holderThrTv;
        private TextView holderForTv;

        private View container;
        private Context mContext;

        public ExamTeacherMineViewHolder(View itemView) {
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
        }

        @Override
        public void setDataToView(final ExamTeacherMine data) {
            holderOneTv.setText(data.getExamName());
            holderTwoTv.setText(data.getExamGrade()+"/"+data.getExamType());
            //holderThrTv.setText(data.getExamGrade()+"/"+data.getExamType());
            holderForTv.setText("更新时间 "+data.getExamUpdateTime());
            container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TeacherMineDetailActivity.startActivity(mContext, holderOneTv.getText().toString(), data.getExamId(), true, 2, URLConfig.MINE_TEST_EXAM_DETAIL);
                }
            });
        }
    }

}
