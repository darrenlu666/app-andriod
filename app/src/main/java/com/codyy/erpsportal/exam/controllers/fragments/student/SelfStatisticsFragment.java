package com.codyy.erpsportal.exam.controllers.fragments.student;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.exam.widgets.AnalysisProgress;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * 自主测试-统计碎片
 * Created by eachann on 2016/2/27.
 */
public class SelfStatisticsFragment extends Fragment {
    private static final String ARG_DATA = SelfStatisticsFragment.class.getPackage().getName() + ".ARG_DATA";
    private AnalysisProgress mApNum;
    private TextView mTvNum;
    private AnalysisProgress mApRightrate;
    private TextView mTvRightrate;
    private AnalysisProgress mApMistake;
    private TextView mTvMistake;

    public static SelfStatisticsFragment getInstance(String data) {
        SelfStatisticsFragment fragment = new SelfStatisticsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_DATA, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exam_statistics, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        mApNum = (AnalysisProgress) rootView.findViewById(R.id.ap_exam_num);
        mTvNum = (TextView) rootView.findViewById(R.id.tv_exam_num);
        mApRightrate = (AnalysisProgress) rootView.findViewById(R.id.ap_exam_rightrate);
        mTvRightrate = (TextView) rootView.findViewById(R.id.tv_exam_rightrate);
        mApMistake = (AnalysisProgress) rootView.findViewById(R.id.ap_exam_mistake);
        mTvMistake = (TextView) rootView.findViewById(R.id.tv_exam_mistake);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindData();
    }

    private void bindData() {
        try {
            String data = getArguments().getString(ARG_DATA);
            JSONObject jsonObject = new JSONObject(data);
            setViewAnimate(mApNum);
            mApNum.setProgress(Float.parseFloat(jsonObject.isNull("answerNum") ? "0" : jsonObject.optString("answerNum")), Integer.parseInt(jsonObject.isNull("questionNum") ? "0" : jsonObject.optString("questionNum")));
            SpannableStringBuilder builder = new SpannableStringBuilder(jsonObject.isNull("answerNum") ? "0" : jsonObject.optString("answerNum"));
            builder.setSpan(new ForegroundColorSpan(Color.RED), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append("/").append(jsonObject.isNull("questionNum") ? "0" : jsonObject.optString("questionNum"));
            mTvNum.setText(builder);
            setViewAnimate(mApRightrate);
            mApRightrate.setProgress(jsonObject.isNull("rightrate") ? 0 : jsonObject.optInt("rightrate"), 100);
            DecimalFormat df = new DecimalFormat("0.##");
            mTvRightrate.setText(df.format(jsonObject.isNull("rightrate") ? 0 : (Float.parseFloat(String.valueOf(jsonObject.opt("rightrate")))))+"%");
            setViewAnimate(mApMistake);
            mApMistake.setProgress(jsonObject.isNull("mistakecnt") ? 0 : jsonObject.optInt("mistakecnt"), jsonObject.isNull("subjectivityCount") ? 0 : jsonObject.optInt("subjectivityCount"));
            builder = new SpannableStringBuilder(String.valueOf(jsonObject.isNull("mistakecnt") ? 0 : jsonObject.optInt("mistakecnt")));
            builder.setSpan(new ForegroundColorSpan(Color.RED), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append("/").append(String.valueOf(jsonObject.isNull("subjectivityCount") ? 0 : jsonObject.optInt("subjectivityCount")));
            mTvMistake.setText(builder);
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

}
