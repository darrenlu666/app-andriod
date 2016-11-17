package com.codyy.erpsportal.exam.controllers.fragments.classspace;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.exam.controllers.activities.school.SchoolStatisticsDetailActivity;
import com.codyy.erpsportal.exam.controllers.fragments.school.StudentStatisticsLoadFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.exam.models.entities.school.ExamStudentStatistics;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;
import com.codyy.erpsportal.commons.widgets.UpOrDownButton;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 班级测试学生统计列表碎片
 *
 * @author eachann
 * @date 2016-01-15
 */
public class StudentStatisticsFragment extends StudentStatisticsLoadFragment<ExamStudentStatistics, StudentStatisticsFragment.ExamStudentStatisticsViewHolder> {

    private static final String TAG = StudentStatisticsFragment.class.getSimpleName();

    @Override
    protected void extraInitViewsStyles() {
        super.extraInitViewsStyles();
        mUpOrDwonClass.setVisibility(View.GONE);
        mLineView.setVisibility(View.GONE);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mUdName.setStateChangedListener(new UpOrDownButton.OnStateChangedListener() {
            @Override
            public void onSelected(boolean isUp) {
                if (!isUp) {
                    mUdName.setChecked(true);
                    mUdName.setUp(true);
                    mUdScore.setInitView();
                    mUdRate.setInitView();
                    mUdCnt.setInitView();
                    mNameSort = "1";
                    mScoreSort = "";
                    mNumSort = "";
                    mRightSort = "";
                    getData();
                }
            }

            @Override
            public void onUp() {
                mUdName.setChecked(true);
                mUdScore.setInitView();
                mUdRate.setInitView();
                mUdCnt.setInitView();
                mNameSort = "1";
                mScoreSort = "";
                mNumSort = "";
                mRightSort = "";
                getData();
            }

            @Override
            public void onDown() {
                mUdName.setChecked(true);
                mUdScore.setInitView();
                mUdRate.setInitView();
                mUdCnt.setInitView();
                mScoreSort = "";
                mNumSort = "";
                mRightSort = "";
                mNameSort = "0";
                getData();
            }
        });
        mUdScore.setStateChangedListener(new UpOrDownButton.OnStateChangedListener() {
            @Override
            public void onSelected(boolean isUp) {
                if (!isUp) {
                    mUdScore.setChecked(true);
                    mUdScore.setUp(true);
                    mUdName.setInitView();
                    mUdRate.setInitView();
                    mUdCnt.setInitView();
                    mNameSort = "";
                    mScoreSort = "1";
                    mNumSort = "";
                    mRightSort = "";
                    getData();
                }
            }

            @Override
            public void onUp() {
                mUdScore.setChecked(true);
                mUdName.setInitView();
                mUdRate.setInitView();
                mUdCnt.setInitView();
                mNameSort = "";
                mScoreSort = "1";
                mNumSort = "";
                mRightSort = "";
                getData();
            }

            @Override
            public void onDown() {
                mUdScore.setChecked(true);
                mUdName.setInitView();
                mUdRate.setInitView();
                mUdCnt.setInitView();
                mNameSort = "";
                mScoreSort = "0";
                mNumSort = "";
                mRightSort = "";
                getData();
            }
        });
        mUdRate.setStateChangedListener(new UpOrDownButton.OnStateChangedListener() {
            @Override
            public void onSelected(boolean isUp) {
                if (!isUp) {
                    mUdRate.setChecked(true);
                    mUdRate.setUp(true);
                    mUdName.setInitView();
                    mUdScore.setInitView();
                    mUdCnt.setInitView();
                    mNameSort = "";
                    mScoreSort = "";
                    mNumSort = "";
                    mRightSort = "1";
                    getData();
                }
            }

            @Override
            public void onUp() {
                mUdRate.setChecked(true);
                mUdName.setInitView();
                mUdScore.setInitView();
                mUdCnt.setInitView();
                mNameSort = "";
                mScoreSort = "";
                mNumSort = "";
                mRightSort = "1";
                getData();
            }

            @Override
            public void onDown() {
                mUdRate.setChecked(true);
                mUdName.setInitView();
                mUdScore.setInitView();
                mUdCnt.setInitView();
                mNameSort = "";
                mScoreSort = "";
                mNumSort = "";
                mRightSort = "0";
                getData();
            }
        });
        mUdCnt.setStateChangedListener(new UpOrDownButton.OnStateChangedListener() {
            @Override
            public void onSelected(boolean isUp) {
                if (!isUp) {
                    mUdCnt.setChecked(true);
                    mUdCnt.setUp(true);
                    mUdName.setInitView();
                    mUdScore.setInitView();
                    mUdRate.setInitView();
                    mNameSort = "";
                    mScoreSort = "";
                    mNumSort = "1";
                    mRightSort = "";
                    getData();
                }
            }

            @Override
            public void onUp() {
                mUdCnt.setChecked(true);
                mUdName.setInitView();
                mUdScore.setInitView();
                mUdRate.setInitView();
                mNameSort = "";
                mScoreSort = "";
                mNumSort = "1";
                mRightSort = "";
                getData();
            }

            @Override
            public void onDown() {
                mUdCnt.setChecked(true);
                mUdName.setInitView();
                mUdScore.setInitView();
                mUdRate.setInitView();
                mNameSort = "";
                mScoreSort = "";
                mNumSort = "0";
                mRightSort = "";
                getData();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ViewHolderCreator<ExamStudentStatisticsViewHolder> newViewHolderCreator() {
        return new ViewHolderCreator<ExamStudentStatisticsViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_exam_student_statistics;
            }

            @Override
            protected ExamStudentStatisticsViewHolder doCreate(View view) {
                return new ExamStudentStatisticsViewHolder(view, getArguments().getString("examTaskId"));
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.GET_STUDENT_STATISTIC;
    }


    @Override
    protected List<ExamStudentStatistics> getList(JSONObject response) {
        List<ExamStudentStatistics> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                ExamStudentStatistics statistics = new ExamStudentStatistics();
                statistics.setExamResultId(object.isNull("examResultId") ? "" : object.getString("examResultId"));
                statistics.setBaseUserId(object.isNull("baseUserId") ? "" : object.getString("baseUserId"));
                statistics.setBaseUserName(object.isNull("baseUserName") ? "" : object.getString("baseUserName"));
                statistics.setHeadUrl(object.isNull("headUrl") ? "" : object.getString("headUrl"));
                statistics.setScore(object.isNull("score") ? "0" : object.getString("score"));
                statistics.setAnswerCount(object.isNull("answerCount") ? "0" : object.getString("answerCount"));
                statistics.setTotalCount(object.isNull("totalCount") ? "0" : object.getString("totalCount"));
                statistics.setRightRate(object.isNull("rightRate") ? "0" : object.getString("rightRate"));
                list.add(statistics);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private String mNameSort = "";
    private String mScoreSort = "";
    private String mNumSort = "";
    private String mRightSort = "";

    @Override
    protected void addParams(Map<String, String> map) {
        map.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        map.put("examTaskId", getArguments().getString("examTaskId"));
        map.put("classId", getArguments().getString("ARG_CLASS_ID"));
    }

    private void getData() {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        addParam("examTaskId", getArguments().getString("examTaskId"));
        addParam("classId", getArguments().getString("ARG_CLASS_ID"));
        addParam("nameSort", mNameSort);
        addParam("scoreSort", mScoreSort);
        addParam("numSort", mNumSort);
        addParam("rightSort", mRightSort);
        loadData(true);
    }

    public static class ExamStudentStatisticsViewHolder extends RecyclerViewHolder<ExamStudentStatistics> {

        private SimpleDraweeView headIconSDV;

        private TextView nameTV;

        private TextView answerCountsTv;
        private TextView scoreTv;
        private TextView rateTv;
        private View container;
        private Context mContext;
        private String mExamTaskId;

        public ExamStudentStatisticsViewHolder(View itemView, String examTaskId) {
            super(itemView);
            this.mContext = itemView.getContext();
            this.mExamTaskId = examTaskId;
        }

        @Override
        public void mapFromView(View view) {
            container = view;
            headIconSDV = (SimpleDraweeView) view.findViewById(R.id.sdv_student_head_icon);
            nameTV = (TextView) view.findViewById(R.id.tv_exam_student_name);
            answerCountsTv = (TextView) view.findViewById(R.id.tv_exam_student_answer_counts);
            scoreTv = (TextView) view.findViewById(R.id.tv_exam_student_score);
            rateTv = (TextView) view.findViewById(R.id.tv_exam_student_rate);
        }

        @Override
        public void setDataToView(final ExamStudentStatistics data) {
            headIconSDV.setImageURI(Uri.parse(data.getHeadUrl()));
            nameTV.setText(data.getBaseUserName());
            SpannableStringBuilder builder = new SpannableStringBuilder(mContext.getString(R.string.exam_answer_cnt, data.getAnswerCount(), data.getTotalCount()));
            builder.setSpan(new ForegroundColorSpan(Color.RED), 4, 4 + data.getAnswerCount().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            answerCountsTv.setText(builder);
            scoreTv.setText(mContext.getString(R.string.exam_answer_score, data.getScore()));
            rateTv.setText(mContext.getString(R.string.exam_answer_rate, data.getRightRate()));
            container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String title = mContext.getString(R.string.exam_statistics_detail_title, data.getBaseUserName(), data.getScore());
                    SchoolStatisticsDetailActivity.startActivity(mContext, title, mExamTaskId, data.getBaseUserId());
                }
            });
        }
    }
}
