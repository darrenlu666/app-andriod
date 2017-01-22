package com.codyy.erpsportal.exam.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.NormalActivity;
import com.codyy.erpsportal.exam.models.entities.ExamInfo;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.homework.utils.WorkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试-试卷详情界面
 * Created by eachann on 2015/12/24.
 */
public class ExamInfoActivity extends NormalActivity {
    private static final String TAG = ExamInfoActivity.class.getSimpleName();
    public static final int TYPE_DEFAULT = -1;
    /**
     * 学校-年级统考
     */
    private static final int TYPE_GRADE = 0;
    private static final String[] TYPE_GRADE_VALUES = {"测试名称", "更新时间", "试卷类型", "年级", "学科", "答题时长", "试卷总分"};
    /**
     * 学校-班级测试
     */
    private static final int TYPE_CLASS = 1;
    private static final String[] TYPE_CLASS_VALUES = {"测试名称", "年级", "学科", "试卷类型", "测试人数", "答题时长", "试卷总分", "开始时间", "结束时间"};
    /**
     * 教师-我的试卷
     */
    private static final int TYPE_MINE = 2;
    private static final String[] TYPE_MINE_VALUES = {"测试名称", "更新时间", "试卷类型", "年级", "学科", "答题时长", "试卷总分", "题量"};
    /**
     * 教师-真题试卷
     */
    private static final int TYPE_REAL = 3;
    private static final String[] TYPE_REAL_VALUES = {"测试名称", "地区", "年份", "年级", "学科", "试卷类型", "答题时长", "试卷总分", "题量", "使用次数"};
    /**
     * 教师-测试任务
     */
    private static final int TYPE_EXAM = 4;
    private static final int VIEW_HEIGHT = 48;
    private int mHeight;
    private String mExamName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHeight = UIUtils.dip2px(this, VIEW_HEIGHT);
        setViewAnim(false, mTitle);
        setCustomTitle(getString(R.string.exam_info));
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rl.addRule(RelativeLayout.BELOW, R.id.toolbar);
        ScrollView scrollView = new ScrollView(this);
        mRelativeLayout.addView(scrollView, rl);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mExamName = getIntent().getExtras().getString(EXTRA_NAME);
        switch (getIntent().getIntExtra(EXTRA_TYPE, TYPE_DEFAULT)) {
            case TYPE_GRADE:
                setTypeGrade((ExamInfo) getIntent().getParcelableExtra(EXTRA_INFO), linearLayout);
                break;
            case TYPE_CLASS:
                setTypeClass((ExamInfo) getIntent().getParcelableExtra(EXTRA_INFO), linearLayout);
                break;
            case TYPE_MINE:
                setMineClass((ExamInfo) getIntent().getParcelableExtra(EXTRA_INFO), linearLayout);
                break;
            case TYPE_REAL:
                setRealClass((ExamInfo) getIntent().getParcelableExtra(EXTRA_INFO), linearLayout);
                break;
            case TYPE_EXAM:
                setTypeClass((ExamInfo) getIntent().getParcelableExtra(EXTRA_INFO), linearLayout);
                break;
        }
    }

    public static ExamInfo getExamInfo(String examId, int type, JSONObject response) {
        ExamInfo info = new ExamInfo();
        try {
            JSONObject object = null;
            if (examId != null) {
                object = response.getJSONObject("examInfo");

            } else {
                object = response.getJSONObject("examTaskInfo");
            }

            switch (type) {
                case TYPE_GRADE:
                    info.setExamSubject(object.isNull("subjectName") ? "" : object.getString("subjectName"));
                    info.setExamUpdateTime(object.isNull("examUpdateTime") ? "" : object.getString("examUpdateTime"));
                    info.setExamType(object.isNull("examType") ? "" : object.getString("examType"));
                    info.setExamGrade(object.isNull("classlevelName") ? "" : object.getString("classlevelName"));
                    info.setExamDuartion(object.isNull("examDuartion") ? "" : object.getString("examDuartion") + "分钟");
                    info.setExamTotalScore(object.isNull("examTotalScore") ? "" : object.getString("examTotalScore") + "分");
                    break;
                case TYPE_CLASS:
                    info.setExamGrade(object.isNull("examClasslevelName") ? "" : object.getString("examClasslevelName"));
                    info.setExamSubject(object.isNull("examSubjectName") ? "" : object.getString("examSubjectName"));
                    info.setExamType(object.isNull("examType") ? "" : object.getString("examType"));
                    info.setExamJoinNums((object.isNull("examReceiveNum") ? "0" : object.getString("examReceiveNum")) + "/" + (object.isNull("examTotalNum") ? "0" : object.getString("examTotalNum")) + "人");
                    info.setExamDuartion(object.isNull("examDuartion") ? "" : object.getString("examDuartion") + "分钟");
                    info.setExamTotalScore(object.isNull("examTotalScore") ? "" : object.getString("examTotalScore") + "分");
                    info.setExamStartTime(object.isNull("examStartTime") ? "" : object.getString("examStartTime"));
                    info.setExamCompleteTime(object.isNull("examCompleteTime") ? "" : object.getString("examCompleteTime"));
                    break;
                case TYPE_MINE:
                    info.setExamSubject(object.isNull("subjectName") ? "" : object.getString("subjectName"));
                    info.setExamUpdateTime(object.isNull("examUpdateTime") ? "" : object.getString("examUpdateTime"));
                    info.setExamType(object.isNull("examType") ? "" : object.getString("examType"));
                    info.setExamGrade(object.isNull("classlevelName") ? "" : object.getString("classlevelName"));
                    info.setExamDuartion(object.isNull("examDuartion") ? "" : object.getString("examDuartion") + "分钟");
                    info.setExamTotalScore(object.isNull("examTotalScore") ? "" : object.getString("examTotalScore") + "分");
                    info.setExamAmountOfQuestions(object.isNull("questionCount") ? String.valueOf(response.isNull("total") ? "0" : response.getInt("total")) + "题" : object.getString("questionCount") + "题");
                    break;
                case TYPE_REAL:
                    info.setExamArea(object.isNull("areaName") ? "" : object.getString("areaName"));
                    info.setExamYear(object.isNull("year") ? "" : TextUtils.isDigitsOnly(object.getString("year")) ? (object.getString("year") + "年") : object.getString("year"));
                    info.setExamGrade(object.isNull("classlevelName") ? "" : object.getString("classlevelName"));
                    info.setExamSubject(object.isNull("subjectName") ? "" : object.getString("subjectName"));
                    info.setExamType(object.isNull("examType") ? "" : object.getString("examType"));
                    info.setExamDuartion(object.isNull("examDuartion") ? "" : object.getString("examDuartion") + "分钟");
                    info.setExamTotalScore(object.isNull("examTotalScore") ? "" : object.getString("examTotalScore") + "分");
                    info.setExamAmountOfQuestions(object.isNull("questionCount") ? String.valueOf(response.isNull("total") ? "0" : response.getInt("total")) + "题" : object.getString("questionCount") + "题");
                    info.setExamUsedCounts(object.isNull("useCount") ? "" : object.getString("useCount") + "次");
                    break;
                case TYPE_EXAM:
                    break;
                default:
                    info = new ExamInfo();
                    break;
            }
        } catch (JSONException e) {
            info = null;
        }
        return info;
    }

    public static void startActivity(Context from, ExamInfo info, int type, String examName) {
        Intent intent = new Intent(from, ExamInfoActivity.class);
        switch (type) {
            case TYPE_GRADE:
                intent.putExtra(EXTRA_TYPE, ExamInfoActivity.TYPE_GRADE);
                break;
            case TYPE_CLASS:
                intent.putExtra(EXTRA_TYPE, ExamInfoActivity.TYPE_CLASS);
                break;
            case TYPE_MINE:
                intent.putExtra(EXTRA_TYPE, ExamInfoActivity.TYPE_MINE);
                break;
            case TYPE_REAL:
                intent.putExtra(EXTRA_TYPE, ExamInfoActivity.TYPE_REAL);
                break;
            case TYPE_EXAM:
                intent.putExtra(EXTRA_TYPE, ExamInfoActivity.TYPE_EXAM);
                break;
        }
        intent.putExtra(EXTRA_INFO, info);
        intent.putExtra(EXTRA_NAME, examName);
        from.startActivity(intent);
        UIUtils.addEnterAnim((Activity) from);
    }

    private void setRealClass(ExamInfo examInfo, LinearLayout linearLayout) {
        List<String> stringList = new ArrayList<>();
        stringList.add(examInfo.getExamArea());
        stringList.add(examInfo.getExamYear());
        stringList.add(examInfo.getExamGrade());
        stringList.add(examInfo.getExamSubject());
        stringList.add(examInfo.getExamType());
        stringList.add(examInfo.getExamDuartion());
        stringList.add(examInfo.getExamTotalScore());
        stringList.add(examInfo.getExamAmountOfQuestions());
        stringList.add(examInfo.getExamUsedCounts());
        for (int i = 0; i < TYPE_REAL_VALUES.length; i++) {
            View view;
            if (i == 0) {
                view = LayoutInflater.from(this).inflate(R.layout.activity_exam_info_base_layout_2, null);
            } else {
                view = LayoutInflater.from(this).inflate(R.layout.activity_exam_info_base_layout, null);
            }
            linearLayout.addView(view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight));
            RelativeLayout layout = (RelativeLayout) view;

            if (i == 0) {
                ((TextView) layout.getChildAt(1)).setText(TYPE_REAL_VALUES[0]);
                ((TextView) layout.getChildAt(2)).setText(mExamName);
            } else {
                ((TextView) layout.getChildAt(2)).setText(TYPE_REAL_VALUES[i]);
                ((TextView) layout.getChildAt(0)).setText(stringList.get(i - 1));
            }

        }
    }

    private void setMineClass(ExamInfo examInfo, LinearLayout linearLayout) {
        List<String> stringList = new ArrayList<>();
        stringList.add(examInfo.getExamUpdateTime());
        stringList.add(examInfo.getExamType());
        stringList.add(examInfo.getExamGrade());
        stringList.add(examInfo.getExamSubject());
        stringList.add(examInfo.getExamDuartion());
        stringList.add(examInfo.getExamTotalScore());
        stringList.add(examInfo.getExamAmountOfQuestions());
        for (int i = 0; i < TYPE_MINE_VALUES.length; i++) {
            View view;
            if (i == 0) {
                view = LayoutInflater.from(this).inflate(R.layout.activity_exam_info_base_layout_2, null);
            } else {
                view = LayoutInflater.from(this).inflate(R.layout.activity_exam_info_base_layout, null);
            }
            linearLayout.addView(view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight));
            RelativeLayout layout = (RelativeLayout) view;
            if (i == 0) {
                ((TextView) layout.getChildAt(1)).setText(TYPE_REAL_VALUES[0]);
                ((TextView) layout.getChildAt(2)).setText(mExamName);
            } else {
                ((TextView) layout.getChildAt(2)).setText(TYPE_MINE_VALUES[i]);
                ((TextView) layout.getChildAt(0)).setText(stringList.get(i - 1));
            }
        }
    }

    private void setTypeClass(ExamInfo examInfo, LinearLayout linearLayout) {
        List<String> stringList = new ArrayList<>();
        stringList.add(examInfo.getExamGrade());
        stringList.add(examInfo.getExamSubject());
        stringList.add(examInfo.getExamType());
        stringList.add(examInfo.getExamJoinNums());
        stringList.add(examInfo.getExamDuartion());
        stringList.add(examInfo.getExamTotalScore());
        stringList.add(examInfo.getExamStartTime());
        stringList.add(examInfo.getExamCompleteTime());
        for (int i = 0; i < TYPE_CLASS_VALUES.length; i++) {
            View view;
            if (i == 0) {
                view = LayoutInflater.from(this).inflate(R.layout.activity_exam_info_base_layout_2, null);
            } else {
                view = LayoutInflater.from(this).inflate(R.layout.activity_exam_info_base_layout, null);
            }
            linearLayout.addView(view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight));
            RelativeLayout layout = (RelativeLayout) view;

            //setViewAnim(true, (TextView) layout.getChildAt(0));
            //setViewAnim(true, (TextView) layout.getChildAt(2));
            if (i == 0) {
                ((TextView) layout.getChildAt(1)).setText(TYPE_REAL_VALUES[0]);
                ((TextView) layout.getChildAt(2)).setText(mExamName);
            } else {
                ((TextView) layout.getChildAt(2)).setText(TYPE_CLASS_VALUES[i]);
                if ("测试人数".equals(TYPE_CLASS_VALUES[i])) {
                    ((TextView) layout.getChildAt(0)).setText(WorkUtils.switchStr(stringList.get(i - 1), Color.RED));
                } else {
                    ((TextView) layout.getChildAt(0)).setText(stringList.get(i - 1));
                }
            }

        }
    }

    private void setTypeGrade(ExamInfo examInfo, LinearLayout linearLayout) {
        List<String> stringList = new ArrayList<>();
        stringList.add(examInfo.getExamUpdateTime());
        stringList.add(examInfo.getExamType());
        stringList.add(examInfo.getExamGrade());
        stringList.add(examInfo.getExamSubject());
        stringList.add(examInfo.getExamDuartion());
        stringList.add(examInfo.getExamTotalScore());
        for (int i = 0; i < TYPE_GRADE_VALUES.length; i++) {
            View view;
            if (i == 0) {
                view = LayoutInflater.from(this).inflate(R.layout.activity_exam_info_base_layout_2, null);
            } else {
                view = LayoutInflater.from(this).inflate(R.layout.activity_exam_info_base_layout, null);
            }
            linearLayout.addView(view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight));
            RelativeLayout layout = (RelativeLayout) view;
            if (i == 0) {
                ((TextView) layout.getChildAt(1)).setText(TYPE_REAL_VALUES[0]);
                ((TextView) layout.getChildAt(2)).setText(mExamName);
            } else {
                ((TextView) layout.getChildAt(2)).setText(TYPE_GRADE_VALUES[i]);
                ((TextView) layout.getChildAt(0)).setText(stringList.get(i - 1));
            }
        }
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
    }
}
