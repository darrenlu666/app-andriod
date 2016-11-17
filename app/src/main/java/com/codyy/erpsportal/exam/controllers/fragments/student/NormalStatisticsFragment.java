package com.codyy.erpsportal.exam.controllers.fragments.student;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.exam.widgets.AnalysisProgress;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 测试-统计碎片-总体统计
 * Created by eachann on 2016/2/27.
 */
public class NormalStatisticsFragment extends Fragment {
    private static final String ARG_DATA = NormalStatisticsFragment.class.getPackage().getName() + ".ARG_DATA";
    @Bind(R.id.tv_class_level_name)
    TextView tvClassLevelName;
    @Bind(R.id.ap_readovercount_alltestcount)
    AnalysisProgress apReadovercountAlltestcount;
    @Bind(R.id.tv_readovercount_alltestcount)
    TextView tvReadovercountAlltestcount;
    @Bind(R.id.tv_max_score)
    TextView tvMaxScore;
    @Bind(R.id.ap_max_score_total)
    AnalysisProgress apMaxScoreTotal;
    @Bind(R.id.tv_max_score_total)
    TextView tvMaxScoreTotal;
    @Bind(R.id.tv_min_score)
    TextView tvMinScore;
    @Bind(R.id.ap_min_score_total)
    AnalysisProgress apMinScoreTotal;
    @Bind(R.id.tv_min_score_total)
    TextView tvMinScoreTotal;
    @Bind(R.id.tv_avg_score)
    TextView tvAvgScore;
    @Bind(R.id.ap_avg_score_total)
    AnalysisProgress apAvgScoreTotal;
    @Bind(R.id.tv_avg_score_total)
    TextView tvAvgScoreTotal;
    @Bind(R.id.tv_my_score)
    TextView tvMyScore;
    @Bind(R.id.ap_my_score_total)
    AnalysisProgress apMyScoreTotal;
    @Bind(R.id.tv_my_score_total)
    TextView tvMyScoreTotal;
    @Bind(R.id.tv_exam_rightrate)
    TextView tvExamRightrate;
    @Bind(R.id.ap_exam_rightrate)
    AnalysisProgress apExamRightrate;
    @Bind(R.id.tv_exam_mistake)
    TextView tvExamMistake;
    @Bind(R.id.ap_exam_mistake)
    AnalysisProgress apExamMistake;

    public static NormalStatisticsFragment getInstance(String data) {
        NormalStatisticsFragment fragment = new NormalStatisticsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_DATA, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exam_statistics_class, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindData(view);
    }

    private void bindData(View view) {
        if (TextUtils.isEmpty(getArguments().getString(ARG_DATA))) {
            Snackbar.make(view, "无统计数据!", Snackbar.LENGTH_SHORT).show();
            return;
        }
        try {
            String[] data = getArguments().getString(ARG_DATA).split("∷");
            if (data.length != 2) return;
            int totalScore = 0;
            if (TextUtils.isDigitsOnly(data[1])) {
                totalScore = Integer.parseInt(data[1]);
            } else {
                totalScore = Integer.parseInt(data[1].substring(0, data[1].lastIndexOf("分")));
            }
            JSONObject jsonObject = new JSONObject(data[0]);
            setViewAnimate(apReadovercountAlltestcount);
            apReadovercountAlltestcount.setProgress(jsonObject.optInt("answerNum"), jsonObject.optInt("allTestCount"));
            SpannableStringBuilder builder = new SpannableStringBuilder(jsonObject.optString("answerNum"));
            builder.setSpan(new ForegroundColorSpan(Color.RED), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append("/").append(jsonObject.optString("allTestCount"));
            tvReadovercountAlltestcount.setText(builder);
            tvClassLevelName.setText(jsonObject.optString("className"));
            tvMaxScore.setText("最高得分");
            setViewAnimate(apMaxScoreTotal);
            apMaxScoreTotal.setProgress(jsonObject.optInt("maxScore"), totalScore);
            tvMaxScoreTotal.setText(getString(R.string.exam_answer_score_total, jsonObject.optInt("maxScore")));
            tvMinScore.setText("最低得分");
            setViewAnimate(apMinScoreTotal);
            apMinScoreTotal.setProgress(jsonObject.optInt("minScore"), totalScore);
            tvMinScoreTotal.setText(getString(R.string.exam_answer_score_total, jsonObject.optInt("minScore")));
            tvAvgScore.setText("平均得分");
            setViewAnimate(apAvgScoreTotal);
            apAvgScoreTotal.setProgress(Float.valueOf(jsonObject.optString("avgScore")), totalScore);
            tvAvgScoreTotal.setText(getString(R.string.exam_answer_score_avg, jsonObject.optString("avgScore")));
            tvMyScore.setText("我的得分");
            setViewAnimate(apMyScoreTotal);
            apMyScoreTotal.setProgress(jsonObject.optInt("myScore"), totalScore);
            tvMyScoreTotal.setText(getString(R.string.exam_answer_score_total, jsonObject.optInt("myScore")));
            setViewAnimate(apExamRightrate);
            apExamRightrate.setProgress(jsonObject.isNull("rightrate") ? 0 : jsonObject.optInt("rightrate"), 100);
            tvExamRightrate.setText(new StringBuilder(String.valueOf(jsonObject.isNull("rightrate") ? 0 : Float.parseFloat(String.valueOf(jsonObject.opt("rightrate"))))).append("%"));
            setViewAnimate(apExamMistake);
            apExamMistake.setProgress(jsonObject.isNull("mistakecnt") ? 0 : jsonObject.optInt("mistakecnt"), jsonObject.isNull("subjectivityCount") ? 0 : jsonObject.optInt("subjectivityCount"));
            builder = new SpannableStringBuilder(String.valueOf(jsonObject.isNull("mistakecnt") ? 0 : jsonObject.optInt("mistakecnt")));
            builder.setSpan(new ForegroundColorSpan(Color.RED), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append("/").append(String.valueOf(jsonObject.isNull("subjectivityCount") ? 0 : jsonObject.optInt("subjectivityCount")));
            tvExamMistake.setText(builder);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setViewAnimate(View view) {
        view.setScaleX(0f);
        view.setPivotX(0f);
        view.animate()
                .scaleXBy(0f)
                .scaleX(1f)
                .setStartDelay(300L)
                .setDuration(400L)
                .setInterpolator(new FastOutLinearInInterpolator())
                .start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
