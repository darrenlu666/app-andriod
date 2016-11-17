package com.codyy.erpsportal.exam.controllers.fragments.school;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.exam.models.entities.school.ExamOverallStatistics;
import com.codyy.erpsportal.exam.widgets.AnalysisProgress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 总体统计碎片
 *
 * @author eachann
 * @date 2016-01-13
 */
public class OverallStatisticsFragment extends LoadMoreFragment<ExamOverallStatistics, OverallStatisticsFragment.ExamOverallStatisticsViewHolder> {

    private static final String TAG = OverallStatisticsFragment.class.getSimpleName();
    public static final String ARG_EXAM_TASK_ID = "examTaskId";

    @Override
    protected ViewHolderCreator<ExamOverallStatisticsViewHolder> newViewHolderCreator() {
        return new ViewHolderCreator<ExamOverallStatisticsViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_exam_overall_statistics;
            }

            @Override
            protected ExamOverallStatisticsViewHolder doCreate(View view) {
                return new ExamOverallStatisticsViewHolder(view);
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.CLASS_GLOBLE_STATISTIC;
    }

    @Override
    protected List<ExamOverallStatistics> getList(JSONObject response) {
        List<ExamOverallStatistics> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
            ExamOverallStatistics statistics = new ExamOverallStatistics();
            statistics.setClassName(getString(R.string.exam_overall_commit_cnt));
            statistics.setReal(-1);
            statistics.setTotal(-1);
            list.add(statistics);
            JSONArray jsonArray1 = jsonObject1.getJSONArray("classList");
            for (int j = 0; j < jsonArray1.length(); j++) {
                JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                statistics = new ExamOverallStatistics();
                statistics.setClassName(jsonObject2.isNull("className") ? "" : jsonObject2.getString("className"));
                statistics.setReal(Integer.parseInt(jsonObject2.isNull("commitCnt") ? "0" : jsonObject2.getString("commitCnt")));
                statistics.setTotal(Integer.parseInt(jsonObject2.isNull("totalPerson") ? "0" : jsonObject2.getString("totalPerson")));
                statistics.setType(-1);
                list.add(statistics);
            }

            statistics = new ExamOverallStatistics();
            statistics.setClassName(getString(R.string.exam_overall_highest));
            statistics.setReal(-2);
            statistics.setTotal(-2);
            list.add(statistics);
            for (int j = 0; j < jsonArray1.length(); j++) {
                JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                statistics = new ExamOverallStatistics();
                statistics.setClassName(jsonObject2.isNull("className") ? "" : jsonObject2.getString("className"));
                statistics.setReal(Float.parseFloat(jsonObject2.isNull("highestScore") ? "0" : jsonObject2.getString("highestScore")));
                statistics.setTotal("".equals(jsonObject2.optString("totalScore")) ? 0 : Integer.parseInt(jsonObject2.getString("totalScore")));
                list.add(statistics);
            }
            statistics = new ExamOverallStatistics();
            statistics.setClassName(getString(R.string.exam_overall_lowest));
            statistics.setReal(-3);
            statistics.setTotal(-3);
            list.add(statistics);
            for (int j = 0; j < jsonArray1.length(); j++) {
                JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                statistics = new ExamOverallStatistics();
                statistics.setClassName(jsonObject2.isNull("className") ? "" : jsonObject2.getString("className"));
                statistics.setReal(Float.parseFloat(jsonObject2.isNull("lowestScore") ? "0" : jsonObject2.getString("lowestScore")));
                statistics.setTotal("".equals(jsonObject2.optString("totalScore")) ? 0 : Integer.parseInt(jsonObject2.getString("totalScore")));
                list.add(statistics);
            }
            statistics = new ExamOverallStatistics();
            statistics.setClassName(getString(R.string.exam_overall_average));
            statistics.setReal(-4);
            statistics.setTotal(-4);
            list.add(statistics);
            for (int j = 0; j < jsonArray1.length(); j++) {
                JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                statistics = new ExamOverallStatistics();
                statistics.setClassName(jsonObject2.isNull("className") ? "" : jsonObject2.getString("className"));
                statistics.setReal(Float.parseFloat(jsonObject2.isNull("avgScore") ? "0" : jsonObject2.getString("avgScore")));
                statistics.setTotal("".equals(jsonObject2.optString("totalScore")) ? 0 : Integer.parseInt(jsonObject2.getString("totalScore")));
                list.add(statistics);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void addParams(Map<String, String> map) {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        addParam("examTaskId", getArguments().getString(ARG_EXAM_TASK_ID));
    }

    public static class ExamOverallStatisticsViewHolder extends RecyclerViewHolder<ExamOverallStatistics> {

        private TextView examOverallClassNmTv;
        private TextView examOverallTitleTv;
        private TextView examOverallTextTv;
        private AnalysisProgress examOverallProgressAp;
        private View container;
        private Context mContext;

        public ExamOverallStatisticsViewHolder(View itemView) {
            super(itemView);
            this.mContext = itemView.getContext();
        }

        @Override
        public void mapFromView(View view) {
            container = view;
            examOverallTitleTv = (TextView) view.findViewById(R.id.tv_exam_overall_title);
            examOverallClassNmTv = (TextView) view.findViewById(R.id.tv_exam_overall_class_nm);
            examOverallTextTv = (TextView) view.findViewById(R.id.tv_exam_overall_text);
            examOverallProgressAp = (AnalysisProgress) view.findViewById(R.id.ap_exam_overall_progress);
        }

        @Override
        public void setDataToView(final ExamOverallStatistics data) {
            examOverallTitleTv.setVisibility(View.GONE);
            examOverallClassNmTv.setVisibility(View.VISIBLE);
            examOverallTextTv.setVisibility(View.VISIBLE);
            examOverallProgressAp.setVisibility(View.VISIBLE);
            SpannableString spannableString = new SpannableString(data.getClassName());
            LinearLayout.LayoutParams layoutParams;
            switch (data.getTotal()) {
                case -1:
                    examOverallTitleTv.setVisibility(View.VISIBLE);
                    examOverallClassNmTv.setVisibility(View.GONE);
                    examOverallTextTv.setVisibility(View.GONE);
                    examOverallProgressAp.setVisibility(View.GONE);
                    if (spannableString != null) {
                        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    examOverallTitleTv.setText(spannableString);
                    break;
                case -2:
                    examOverallTitleTv.setVisibility(View.VISIBLE);
                    examOverallClassNmTv.setVisibility(View.GONE);
                    examOverallTextTv.setVisibility(View.GONE);
                    examOverallProgressAp.setVisibility(View.GONE);
                    if (spannableString != null) {
                        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    examOverallTitleTv.setText(spannableString);
                    break;
                case -3:
                    examOverallTitleTv.setVisibility(View.VISIBLE);
                    examOverallClassNmTv.setVisibility(View.GONE);
                    examOverallTextTv.setVisibility(View.GONE);
                    examOverallProgressAp.setVisibility(View.GONE);
                    if (spannableString != null) {
                        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    examOverallTitleTv.setText(spannableString);
                    break;
                case -4:
                    examOverallTitleTv.setVisibility(View.VISIBLE);
                    examOverallClassNmTv.setVisibility(View.GONE);
                    examOverallTextTv.setVisibility(View.GONE);
                    examOverallProgressAp.setVisibility(View.GONE);
                    if (spannableString != null) {
                        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    examOverallTitleTv.setText(spannableString);
                    break;
                default:
                    examOverallTitleTv.setVisibility(View.GONE);
                    examOverallClassNmTv.setVisibility(View.VISIBLE);
                    examOverallTextTv.setVisibility(View.VISIBLE);
                    examOverallProgressAp.setVisibility(View.VISIBLE);
                    spannableString.setSpan(new AbsoluteSizeSpan(14, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    examOverallClassNmTv.setText(spannableString);
                    examOverallProgressAp.setProgress(data.getReal(), data.getTotal());
                    switch (data.getType()) {
                        case -1:
                            examOverallTextTv.setText((int) data.getReal() + "/" + data.getTotal());
                            break;
                        default:
                            if (data.getReal() != (int) data.getReal())
                                examOverallTextTv.setText(data.getReal() + "分");
                            else
                                examOverallTextTv.setText((int) data.getReal() + "分");
                            break;
                    }

                    break;
            }
        }
    }

}
