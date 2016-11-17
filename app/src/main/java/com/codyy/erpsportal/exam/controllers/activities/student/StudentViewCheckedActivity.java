package com.codyy.erpsportal.exam.controllers.activities.student;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.TaskActivity;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.exam.controllers.activities.ExamInfoActivity;
import com.codyy.erpsportal.exam.controllers.activities.ExamStatisticsActivity;
import com.codyy.erpsportal.exam.controllers.fragments.dialogs.SwitchTopicDialog;
import com.codyy.erpsportal.exam.models.entities.ExamInfo;
import com.codyy.erpsportal.exam.models.entities.QuestionInfo;
import com.codyy.erpsportal.homework.models.entities.ItemInfoClass;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 学生答题-查看已批阅
 * Created by eachann on 2016-02-04.
 */
public class StudentViewCheckedActivity extends TaskActivity implements View.OnClickListener {
    private static final String TAG = StudentViewCheckedActivity.class.getSimpleName();
    private TextView mTime;
    private TextView mSubmit;
    private ViewPager.OnPageChangeListener mListener;
    private ExamInfo mExamInfo = new ExamInfo();
    private ArrayList<ItemInfoClass> mData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_exam_answer_bottom_layout, null);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(this, 48));
        rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mRelativeLayout.addView(view, rl);
        TextView mCurrent = (TextView) view.findViewById(R.id.tv_current_item);
        mCurrent.setText(getString(R.string.exam_statistics));
        mCurrent.setOnClickListener(this);
        mTime = (TextView) view.findViewById(R.id.tv_exam_time);
        mTime.setOnClickListener(this);
        mSubmit = (TextView) view.findViewById(R.id.tv_exam_submit);
        if (getIntent().getStringExtra(EXTRA_EXAM_PRACTICE_STATUS) == null) {
            mSubmit.setVisibility(View.GONE);
        } else if (getIntent().getStringExtra(EXTRA_EXAM_PRACTICE_STATUS).equals("END")) {
            mSubmit.setText(getString(R.string.exam_practice_result));
        } else {
            mSubmit.setText(getString(R.string.exam_practice));
        }
        mSubmit.setOnClickListener(this);
        setViewAnim(true, mCurrent, mTime, mSubmit);
    }

    public void onEventMainThread(String action) {
        if (("finish" + StudentPracticeDetailActivity.TAG).equals(action)) {
            finish();
        }
    }

    @Override
    protected void onViewBound() {
        super.onViewBound();
        mListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String str = position + 1 + "/" + mData.size();
                int length = str.indexOf("/");
                SpannableStringBuilder builder = new SpannableStringBuilder(str);
                builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTime.setText(builder);
                if (mLastPosition != position) {
                    mData.get(mLastPosition).setColor(0);
                }
                mLastPosition = position;
                mData.get(mLastPosition).setColor(Color.parseColor("#ff69be40"));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        setViewAnim(false, mTitle);
        setCustomTitle(getIntent().getStringExtra(EXTRA_TITLE));
        mPager.addOnPageChangeListener(mListener);

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mListener != null) {
            mPager.removeOnPageChangeListener(mListener);
        }
        super.onDestroy();
    }

    private static final String EXTRA_EXAM_PRACTICE_STATUS = StudentViewCheckedActivity.class.getPackage() + ".EXTRA_EXAM_PRACTICE_STATUS";
    private static final String EXTRA_EXAM_PRACTICE_ID = StudentViewCheckedActivity.class.getPackage() + ".EXTRA_EXAM_PRACTICE_ID";

    public static void startActivity(Activity activity, String title, String examTaskId, String practiceStatus, String practiceId) {
        Intent intent = new Intent(activity, StudentViewCheckedActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_EXAM_TASK_ID, examTaskId);
        intent.putExtra(EXTRA_EXAM_PRACTICE_STATUS, practiceStatus);
        intent.putExtra(EXTRA_EXAM_PRACTICE_ID, practiceId);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    private String mStatistics;

    @Override
    protected void addParams(Map<String, String> params) {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        addParam("examTaskId", getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
    }

    @Override
    protected void addFragments(JSONObject response) {
        try {
            if ("error".equals(response.optString("result"))) {
                finish();
                ToastUtil.showToast(getApplicationContext(), getString(R.string.refresh_state_loade_error));
                return;
            }
            mExamInfo = ExamInfoActivity.getExamInfo(getIntent().getStringExtra(EXTRA_EXAM_TASK_ID), 1, response);
            try {
                if (response.getJSONObject("statistics") != null)
                    mStatistics = response.getJSONObject("statistics").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
                //item筛选
                ItemInfoClass itemInfoClass = new ItemInfoClass();
                itemInfoClass.setWorkItemIndex(i + 1);
                if (i == 0) {
                    itemInfoClass.setColor(Color.parseColor("#ff69be40"));
                }
                itemInfoClass.setWorkItemType(o.isNull("questionType") ? "" : o.getString("questionType"));
                mData.add(itemInfoClass);
                String str = 1 + "/" + mData.size();
                int position = str.indexOf("/");
                SpannableStringBuilder itemBuilder = new SpannableStringBuilder(str);
                itemBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 0, position, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTime.setText(itemBuilder);
                Bundle bundle = new Bundle();
                bundle.putParcelable(TaskFragment.ARG_TASK_DATA, getQuestionInfo(o));
                bundle.putString(TaskFragment.ARG_TASK_TYPE, TaskFragment.TYPE_EXAM);
                bundle.putString(TaskFragment.ARG_TASK_STATUS,getIntent().getStringExtra(EXTRA_EXAM_PRACTICE_STATUS) != null?TaskFragment.STATUS_READ: TaskFragment.STATUS_READ_SELF);
                addFragment(String.valueOf(i), TaskFragment.class, bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
            ToastUtil.showToast(getApplicationContext(), getString(R.string.refresh_state_loade_error));
        }
    }

    private QuestionInfo getQuestionInfo(JSONObject o) {
        QuestionInfo info = null;
        try {
            info = new QuestionInfo();
            info.setQuestionType(o.isNull("questionType") ? "" : o.getString("questionType"));
            info.setQuestionContent(o.isNull("questionContent") ? "" : o.getString("questionContent"));
            info.setQuestionOptions(o.isNull("questionOptions") ? "" : o.getString("questionOptions"));
            info.setQuestionMediaUrl(o.isNull("questionMediaUrl") ? "" : o.getString("questionMediaUrl"));
            info.setQuestionScores(o.isNull("questionScores") ? "0" : String.valueOf(o.optInt("questionScores")));
            info.setQuestionScore(o.isNull("questionScore") ? 0 : o.optInt("questionScore"));
            info.setQuestionDifficultyFactor(o.isNull("questionDifficultyFactor") ? "" : o.getString("questionDifficultyFactor"));
            info.setQuestionStudentAnswer(o.isNull("questionStudentAnswer") ? "" : o.getString("questionStudentAnswer"));
            info.setQuestionStudentAnswerMediaUrl(o.isNull("questionStudentAnswerMediaUrl") ? "" : o.getString("questionStudentAnswerMediaUrl"));
            info.setQuestionResolve(o.isNull("questionResolve") ? "" : o.getString("questionResolve"));
            info.setQuestionResolveVideo(o.isNull("questionResolveVideo") ? "" : o.getString("questionResolveVideo"));
            info.setQuestionTeacherReviews(o.isNull("questionTeacherReviews") ? "" : o.getString("questionTeacherReviews"));
            if (TaskFragment.TYPE_FILL_IN_BLANK.equals(info.getQuestionType()) && o.isNull("fillInAnswers")) {
                StringBuilder builder = new StringBuilder();
                builder.append("<br>");
                try {
                    JSONArray array = o.getJSONArray("fillInAnswers");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        builder.append(" (").append(String.valueOf(i + 1)).append(") ");
                        builder.append(object.isNull("answerGrp1") ? "" : object.optString("answerGrp1"));
                        builder.append(object.isNull("answerGrp2") ? "" : " / " + object.optString("answerGrp2"));
                        builder.append(object.isNull("answerGrp3") ? "" : " / " + object.optString("answerGrp3"));
                        builder.append(object.isNull("answerGrp4") ? "" : " / " + object.optString("answerGrp4"));
                        builder.append("<br>");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                info.setQuestionCorrectAnswer(builder.toString());
            } else {
                info.setQuestionCorrectAnswer(o.isNull("questionCorrectAnswer") ? "" : o.getString("questionCorrectAnswer"));
            }
            info.setQuestionKnowledgePoint(o.isNull("questionKnowledgePoint") ? "" : parseKnowledgePoint(o.getJSONArray("questionKnowledgePoint")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    private String parseKnowledgePoint(JSONArray jsonArray) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            builder.append(jsonArray.opt(i).toString() + ";");
        }
        return builder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_button, menu);
        MenuItem menuItem = menu.findItem(R.id.task_read_menu);
        LinearLayout linearLayout = (LinearLayout) menuItem.getActionView();
        TextView mTvTaskTitle = (TextView) linearLayout.findViewById(R.id.task_title);
        mTvTaskTitle.setText(getString(R.string.exam_info));
        if (getIntent().getStringExtra(EXTRA_EXAM_PRACTICE_STATUS) == null) {
            mTvTaskTitle.setVisibility(View.GONE);
        }
        mTvTaskTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startExamInfo();

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void startExamInfo() {
        ExamInfoActivity.startActivity(this, mExamInfo, 1, getIntent().getStringExtra(EXTRA_TITLE));
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
    }

    /**
     * 获取请求地址
     *
     * @return
     */
    @Override
    protected String getUrl() {
        return URLConfig.STUDENT_VIEW_CHECKED;
    }

    private int mLastPosition = 0;

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_exam_time:
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(SwitchTopicDialog.ARG_KEY, mData);
                final SwitchTopicDialog dialog = new SwitchTopicDialog();
                dialog.setArguments(bundle);
                dialog.setOnItemClickListener(new SwitchTopicDialog.OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        dialog.dismiss();
                        if (mLastPosition != position - 1) {
                            mData.get(mLastPosition).setColor(0);
                        }
                        mLastPosition = position - 1;
                        mData.get(mLastPosition).setColor(Color.parseColor("#ff69be40"));
                        mPager.setCurrentItem(position - 1, true);
                    }
                });
                dialog.show(getSupportFragmentManager(), SwitchTopicDialog.TAG);
                break;
            case R.id.tv_current_item:
                if (TextUtils.isEmpty(mStatistics)) {
                    ToastUtil.showToast(getString(R.string.exam_practice_no_statistics));
                    return;
                }
                Intent intent;
                if (getIntent().getStringExtra(EXTRA_EXAM_PRACTICE_STATUS) == null && !TextUtils.isEmpty(mStatistics)) {
                    intent = new Intent(this, ExamStatisticsActivity.class);
                    intent.putExtra(EXTRA_TYPE, "SELF");//自主测试
                    intent.putExtra(EXTRA_DATA, mStatistics);
                    startActivity(intent);
                } else {
                    intent = new Intent(this, ExamStatisticsActivity.class);
                    intent.putExtra(EXTRA_TYPE, "NOT_SELF");//非自主测试
                    intent.putExtra(EXTRA_DATA, mStatistics + "∷" + mExamInfo.getExamTotalScore());
                    startActivity(intent);
                }
                break;
            case R.id.tv_exam_submit:
                if (getString(R.string.exam_practice).equals(mSubmit.getText().toString())) {
                    StudentPracticeDetailActivity.startActivity(this, mTitle.getText().toString() + getString(R.string.exam_practice), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
                } else if (getString(R.string.exam_practice_result).equals(mSubmit.getText().toString())) {
                    StudentViewPracticeActivity.startActivity(this, mTitle.getText().toString() + getString(R.string.exam_practice), getIntent().getStringExtra(EXTRA_EXAM_PRACTICE_ID));
                }
                break;
        }
    }
}
