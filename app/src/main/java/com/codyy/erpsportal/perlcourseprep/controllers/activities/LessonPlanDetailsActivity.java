package com.codyy.erpsportal.perlcourseprep.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.NumberUtils;
import com.codyy.erpsportal.perlcourseprep.models.entities.LessonPlanDetails;
import com.codyy.erpsportal.commons.utils.UIUtils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.NumberFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 教案详情
 * Created by gujiajia on 2016/1/21.
 */
public class LessonPlanDetailsActivity extends AppCompatActivity {

    @Bind(R.id.tv_lesson_name)
    TextView mLessonNameTv;
    @Bind(R.id.tv_teacher_name)
    TextView mTeacherNameTv;

    @Bind(R.id.classLevelName)
    TextView mClassLevelNameTv;

    @Bind(R.id.subjectName)
    TextView mSubjectNameTv;

    @Bind(R.id.versionName)
    TextView mVersionNameTv;

    @Bind(R.id.volumeName)
    TextView mVolumeNameTv;

    @Bind(R.id.chapterName)
    TextView mChapterNameTv;

    @Bind(R.id.sectionName)
    TextView mSectionNameTv;

    @Bind(R.id.edit_time)
    TextView mEditTimeTv;

    @Bind(R.id.rb_rate)
    RatingBar mRateRb;

    @Bind(R.id.tv_rate)
    TextView mRateTv;

    @Bind(R.id.tv_click_count)
    TextView mClickCountTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_plan_details);
        ButterKnife.bind(this);
        LessonPlanDetails lessonPlanDetails = getIntent().getParcelableExtra(LessonPlanDetails.EXTRA_LESSON_PLAN_DETAILS);
        if (lessonPlanDetails != null) {
            mLessonNameTv.setText(lessonPlanDetails.getLessonPlanName());
            mTeacherNameTv.setText(lessonPlanDetails.getTeacherName());
            mClassLevelNameTv.setText(lessonPlanDetails.getClasslevelName());
            mSubjectNameTv.setText(lessonPlanDetails.getSubjectName());
            mVersionNameTv.setText(lessonPlanDetails.getVersionName());
            mVolumeNameTv.setText(lessonPlanDetails.getVolumnName());
            mChapterNameTv.setText(lessonPlanDetails.getChapterName());
            mSectionNameTv.setText(lessonPlanDetails.getSectionName());
            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
            mEditTimeTv.setText(dateTimeFormatter.print(lessonPlanDetails.getOperateTime()));
            float rating = NumberUtils.floatOf(lessonPlanDetails.getAvgScore());
            mRateRb.setRating(rating / 2);
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMaximumFractionDigits(1);
            numberFormat.setMinimumFractionDigits(0);
            mRateTv.setText(getString(R.string.n_score, numberFormat.format(rating)));
            mClickCountTv.setText(getString(R.string.n_times, lessonPlanDetails.getViewCount()));
        }
    }

    public static void start(Activity activity, LessonPlanDetails lessonPlanDetails) {
        Intent intent = new Intent(activity, LessonPlanDetailsActivity.class);
        intent.putExtra(LessonPlanDetails.EXTRA_LESSON_PLAN_DETAILS, lessonPlanDetails);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }
}
