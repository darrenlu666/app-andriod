package com.codyy.erpsportal.onlinemeetings.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ActivityThemeActivity;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.EvaluationScore;
import com.codyy.erpsportal.commons.models.entities.MeetDetail;

import java.text.DecimalFormat;

/**
 * 集体备课、互动听课视频详情fragment
 * Created by yangxinwu on 2015/7/29.
 */
public class VideoIntroductionFragment extends Fragment {
    TextView mTvGrade;
    TextView mTvSubject;
    TextView mTvStartTime;
    TextView mTvClickCount;
    TextView mTvTeacherName;
    TextView mTvProduce;
    RatingBar mRbStar;
    TextView textView;
    TextView mScoreTV;
    int type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_introduction, container, false);
        type = getArguments().getInt("type");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mTvGrade = (TextView) view.findViewById(R.id.tv_grade);
        mTvSubject = (TextView) view.findViewById(R.id.tv_subject);
        mTvStartTime = (TextView) view.findViewById(R.id.tv_start_time);
        mTvClickCount = (TextView) view.findViewById(R.id.tv_click_count);
        mRbStar = (RatingBar) view.findViewById(R.id.rb_star);
        mTvTeacherName = (TextView) view.findViewById(R.id.tv_teacher_name);
        mTvProduce = (TextView) view.findViewById(R.id.tv_produce);
        textView = (TextView) view.findViewById(R.id.tv_teacher_name_text);
        mScoreTV = (TextView) view.findViewById(R.id.tv_score);
    }

    public void setVideoDetails(MeetDetail meetDetail, EvaluationScore score) {
        if(TextUtils.isEmpty(meetDetail.getClassLevelName())||"不限年级".equals(meetDetail.getClassLevelName())){
            mTvGrade.setText("不限");
        }else{
            String levels = meetDetail.getClassLevelName();
            if(levels.contains(",")){
                levels = levels.replace(","," ");
            }
            mTvGrade.setText(levels);
        }

        mTvSubject.setText( meetDetail.getSubjectName());
        mTvStartTime.setText( meetDetail.getStartTime());
        mTvClickCount.setText( String.valueOf(meetDetail.getViewCount()));
        mTvTeacherName.setText( meetDetail.getMainTeacher());
        if (score != null) {
            if ("SCORE".equals(score.getScoreType())) {
                mScoreTV.setVisibility(View.VISIBLE);
                mRbStar.setVisibility(View.GONE);
                DecimalFormat df = new DecimalFormat("#.#");
                mScoreTV.setText(df.format(score.getAvgScore()) + "/" + df.format(score.getTotalScore()));
            } else {
                mScoreTV.setVisibility(View.GONE);
                mRbStar.setVisibility(View.VISIBLE);
                mRbStar.setRating(score.getAvgScore() / 2);
            }
        } else {
            if ("score".equals(meetDetail.getScoreType())) {
                mScoreTV.setVisibility(View.VISIBLE);
                mRbStar.setVisibility(View.GONE);
                mScoreTV.setText(meetDetail.getAverageScore() + "/" + meetDetail.getTotalScore());
            } else {
                mScoreTV.setVisibility(View.GONE);
                mRbStar.setVisibility(View.VISIBLE);
                double a = Math.ceil(Double.valueOf(meetDetail.getAverageScore())) / 2;
                mRbStar.setRating((float) a);
            }
        }
        switch (type) {
            case ActivityThemeActivity.PREPARE_LESSON:
                break;
            case ActivityThemeActivity.INTERACT_LESSON:
                textView.setText(Titles.sMasterTeacher);//"主讲教师");
                break;
            case ActivityThemeActivity.EVALUATION_LESSON:
                textView.setText(Titles.sMasterTeacher);//"主讲教师");
                break;
            case ActivityThemeActivity.DELIVERY_CLASS:
            case ActivityThemeActivity.LIVE_APPOINTMENT:
                break;
        }
        mTvProduce.setText(meetDetail.getDescription());
    }
}
