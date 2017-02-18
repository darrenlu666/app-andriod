package com.codyy.erpsportal.county.controllers.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.county.controllers.fragments.DialogStatisticsFragment;
import com.codyy.erpsportal.county.controllers.models.entities.CountyClassDetial;

import java.text.DecimalFormat;

import butterknife.Bind;

/**
 * create by kmdai 17-2-13
 */
public class CountyClassStatisticalActivity extends ToolbarActivity implements View.OnClickListener {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTextView;
    private String mRealityNum;
    private String mRealityNumRate;
    private CountyClassDetial mCountyClassDetial;
    private String mClassRoomID;
    private String mReceiveClassRoomId;
    private String mTeacherID;
    private int mType;
    private int mCurrentWeek;
    private String mCurrentDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getBundleExtra("data");
        mRealityNum = bundle.getString("mRealityNum");
        mRealityNumRate = bundle.getString("mRealityNumRate");
        mCountyClassDetial = bundle.getParcelable("mCountyClassDetial");
        mClassRoomID = bundle.getString("mClassRoomID");
        mReceiveClassRoomId = bundle.getString("mReceiveClassRoomId");
        mTeacherID = bundle.getString("mTeacherID");
        mType = bundle.getInt("mType");
        mCurrentWeek = bundle.getInt("mCurrentWeek");
        mCurrentDate = bundle.getString("mCurrentDate");
        if (mCountyClassDetial != null) {
            setData();
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_conty_item_detail2;
    }

    @Override
    protected void initToolbar() {
        initToolbar(mToolbar);
        mTextView.setText("课程统计");
    }

    private void setData() {
        if (mCountyClassDetial != null) {
            DecimalFormat df = new DecimalFormat("0.##");
            TextView week_plan = (TextView) findViewById(R.id.week_plan);
            week_plan.setText("计划课时数 " + mCountyClassDetial.getWeekClass().getWeekPlanNum());
            TextView week_week = (TextView) findViewById(R.id.week_week);
            week_week.setText("周课时数 " + mCountyClassDetial.getWeekClass().getWeekScheNum());
            TextView week_reality = (TextView) findViewById(R.id.week_Reality);
            week_reality.setText(mRealityNum);

            String week_realityStr = String.valueOf(mCountyClassDetial.getWeekClass().getWeekRealityNum());
            SpannableStringBuilder week_realitySpan = new SpannableStringBuilder(week_realityStr);
            week_realitySpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 0, week_realityStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            week_reality.append(week_realitySpan);
            week_reality.setOnClickListener(this);

            TextView week_rate = (TextView) findViewById(R.id.week_rate);
            float rate;
            if (mCountyClassDetial.getWeekClass().getWeekPlanNum() > 0) {
                rate = ((float) mCountyClassDetial.getWeekClass().getWeekRealityNum() / mCountyClassDetial.getWeekClass().getWeekPlanNum()) * 100;
            } else {
                rate = 0;
            }
            week_rate.setText(mRealityNumRate);
            String week_rateStr = df.format(rate) + "%";
            SpannableStringBuilder week_rateSpan = new SpannableStringBuilder(week_rateStr);
            week_rateSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 0, week_rateStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            week_rate.append(week_rateSpan);
            week_rate.setOnClickListener(this);

            TextView term_all = (TextView) findViewById(R.id.term_all);
            term_all.setText("计划总课时数 " + mCountyClassDetial.getTermClass().getMaxTermPlanNum());
            TextView term_stu = (TextView) findViewById(R.id.term_stu);
            term_stu.setText("受益学生数 " + mCountyClassDetial.getTermClass().getBenefitStuNum());
            TextView term_plan = (TextView) findViewById(R.id.term_plan);
            term_plan.setText("计划课时数 " + mCountyClassDetial.getTermClass().getPlanScheduleNum());

            TextView term_reality = (TextView) findViewById(R.id.term_reality);
            term_reality.setText(mRealityNum);
            String term_realityStr = String.valueOf(mCountyClassDetial.getTermClass().getRealityScheduleNum());
            SpannableStringBuilder term_realitySpan = new SpannableStringBuilder(term_realityStr);
            term_realitySpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 0, term_realitySpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            term_reality.append(term_realitySpan);
            term_reality.setOnClickListener(this);
            float rateTerm;
            if (mCountyClassDetial.getTermClass().getPlanScheduleNum() > 0) {
                rateTerm = ((float) mCountyClassDetial.getTermClass().getRealityScheduleNum() / mCountyClassDetial.getTermClass().getPlanScheduleNum()) * 100;
            } else {
                rateTerm = 0;
            }
            TextView term_rate = (TextView) findViewById(R.id.term_rate);
            term_rate.setText(mRealityNumRate);
            String str = df.format(rateTerm) + "%";
            SpannableStringBuilder term_rateSpan = new SpannableStringBuilder(str);
            term_rateSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            term_rate.append(term_rateSpan);
            term_rate.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int type = DialogStatisticsFragment.DIALOG_TYPE_DETIAL;
        Bundle bundle = new Bundle();
        String urlType = null;
        switch (v.getId()) {
            case R.id.week_Reality:
                urlType = DialogStatisticsFragment.URL_TYPE_WEEK;
            case R.id.term_reality:
                if (urlType == null) {
                    urlType = DialogStatisticsFragment.URL_TYPE_SEMESTER;
                }
                type = DialogStatisticsFragment.DIALOG_TYPE_DETIAL;
                bundle.putString(DialogStatisticsFragment.EXTRA_URL_TYPE, urlType);
                if (mType == CountyClassDetailActivity.TYPE_MASTERCLASSROOM) {//主讲教室
                    bundle.putString(DialogStatisticsFragment.EXTRA_CLASSROOMID, mClassRoomID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_RECEIVE_CLASSROOMID, mReceiveClassRoomId);
                    bundle.putInt(DialogStatisticsFragment.EXTRA_WEEKSEQ, mCurrentWeek);
                } else if (mType == CountyClassDetailActivity.TYPE_RECEIVEROOM) {//接收教室
                    bundle.putString(DialogStatisticsFragment.EXTRA_RECEIVE_CLASSROOMID, mClassRoomID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_CLASSROOMID, mReceiveClassRoomId);
                    bundle.putString(DialogStatisticsFragment.EXTRA_DATATIME, mCurrentDate);
                } else {//主讲教师
                    bundle.putString(DialogStatisticsFragment.EXTRA_CLASSROOMID, mClassRoomID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_RECEIVE_CLASSROOMID, mReceiveClassRoomId);
                    bundle.putString(DialogStatisticsFragment.EXTRA_BASEUSERID, mTeacherID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_DATATIME, mCurrentDate);
                }
                break;
            case R.id.week_rate:
                urlType = DialogStatisticsFragment.URL_TYPE_WEEK;
            case R.id.term_rate:
                if (urlType == null) {
                    urlType = DialogStatisticsFragment.URL_TYPE_SEMESTER;
                }
                type = DialogStatisticsFragment.DIALOG_TYPE_STATISTICS;
                bundle.putString(DialogStatisticsFragment.EXTRA_URL_TYPE, urlType);
                if (mType == CountyClassDetailActivity.TYPE_MASTERCLASSROOM) {//主讲教室
                    bundle.putString(DialogStatisticsFragment.EXTRA_CLASSROOMID, mClassRoomID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_RECEIVE_CLASSROOMID, mReceiveClassRoomId);
                    bundle.putInt(DialogStatisticsFragment.EXTRA_WEEKSEQ, mCurrentWeek);
                } else if (mType == CountyClassDetailActivity.TYPE_RECEIVEROOM) {//接收教室
                    bundle.putString(DialogStatisticsFragment.EXTRA_RECEIVE_CLASSROOMID, mClassRoomID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_CLASSROOMID, mReceiveClassRoomId);
                    bundle.putString(DialogStatisticsFragment.EXTRA_DATATIME, mCurrentDate);
                } else {//主讲教师
                    bundle.putString(DialogStatisticsFragment.EXTRA_CLASSROOMID, mClassRoomID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_RECEIVE_CLASSROOMID, mReceiveClassRoomId);
                    bundle.putString(DialogStatisticsFragment.EXTRA_BASEUSERID, mTeacherID);
                    bundle.putString(DialogStatisticsFragment.EXTRA_DATATIME, mCurrentDate);
                }
                break;
        }
        DialogStatisticsFragment dialogStatisticsFragment = DialogStatisticsFragment.newInstance(type, mType, bundle);
        dialogStatisticsFragment.show(getSupportFragmentManager(), "dialog--");
    }
}
