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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.TaskActivity;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.TaskAnswerDao;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.exam.controllers.fragments.dialogs.SwitchTopicDialog;
import com.codyy.erpsportal.exam.models.entities.QuestionInfo;
import com.codyy.erpsportal.homework.models.entities.ItemInfoClass;
import com.codyy.erpsportal.homework.models.entities.task.TaskAnswer;
import com.codyy.erpsportal.homework.models.entities.task.TaskPicInfo;
import com.codyy.erpsportal.homework.widgets.MySubmitDialog;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 学生答题-巩固测试
 * Created by eachann on 2016-02-15
 */
public class StudentPracticeDetailActivity extends TaskActivity implements View.OnClickListener {
    public static final String TAG = "com.codyy.erpsportal.exam.controllers.activities.student.StudentPracticeDetailActivity";
    private TextView mCurrent;
    private ViewPager.OnPageChangeListener mListener;
    private ArrayList<ItemInfoClass> mData = new ArrayList<>();
    private DialogUtil mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_exam_answer_bottom_layout, null);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(this, 48));
        rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mRelativeLayout.addView(view, rl);
        mCurrent = (TextView) view.findViewById(R.id.tv_current_item);
        mCurrent.setOnClickListener(this);
        TextView mTime = (TextView) view.findViewById(R.id.tv_exam_time);
        mTime.setOnClickListener(this);
        mTime.setVisibility(View.GONE);
        TextView mSubmit = (TextView) view.findViewById(R.id.tv_exam_submit);
        mSubmit.setText(getString(R.string.exam_submit_answer));
        mSubmit.setOnClickListener(this);
        setViewAnim(true, mCurrent, mTime, mSubmit);
        if (mDialog == null) {
            mDialog = new DialogUtil(this);
        }
        mDialog.showDialog();
    }


    /**
     * 数据为本地测试
     */
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
                mCurrent.setText(builder);
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
        if (mListener != null) {
            mPager.removeOnPageChangeListener(mListener);
        }
        super.onDestroy();
    }

    public static void startActivity(Activity activity, String title, String examTaskId) {
        Intent intent = new Intent(activity, StudentPracticeDetailActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_EXAM_TASK_ID, examTaskId);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    @Override
    protected void addParams(Map<String, String> params) {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        addParam("examTaskId", getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
    }

    private boolean isDoHomeWork;

    @Override
    protected void addFragments(JSONObject response) {
        try {
            if ("error".equals(response.optString("result"))) {
                finish();
                ToastUtil.showToast(getApplicationContext(), response.optString("message"));
                return;
            }
            JSONArray jsonArray = response.getJSONArray("list");
            if (jsonArray.length() > 0) {
                JSONObject o = jsonArray.getJSONObject(0);
                isDoHomeWork = TaskAnswerDao.getInstance(this).query(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), o.optString("examPracticeId")).size() == 0;
            }
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
                mCurrent.setText(itemBuilder);
                Bundle bundle = new Bundle();
                bundle.putParcelable(TaskFragment.ARG_TASK_DATA, getQuestionInfo(o));
                bundle.putString(TaskFragment.ARG_TASK_TYPE, TaskFragment.TYPE_EXAM);
                bundle.putString(TaskFragment.ARG_TASK_STATUS, TaskFragment.STATUS_DO);
                addFragment(String.valueOf(i), TaskFragment.class, bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
            ToastUtil.showToast(getApplicationContext(), response.optString("message"));
        } finally {
            mDialog.cancel();
        }
    }

    private String mExamPracticeId;

    private QuestionInfo getQuestionInfo(JSONObject o) {
        QuestionInfo info = null;
        try {
            info = new QuestionInfo();
            mExamPracticeId = o.optString("examPracticeId");
            info.setStudentId(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId());
            info.setExamTaskId(o.optString("examPracticeId"));
            info.setQuestionId(o.optString("examPracticeQuestionId"));
            info.setQuestionType(o.isNull("questionType") ? "" : o.getString("questionType"));
            info.setQuestionContent(o.isNull("questionContent") ? "" : o.getString("questionContent"));
            info.setQuestionOptions(o.isNull("questionOptions") ? "" : o.getString("questionOptions"));
            info.setQuestionMediaUrl(o.isNull("questionMediaUrl") ? "" : o.getString("questionMediaUrl"));
            info.setQuestionBlankCounts(o.isNull("questionBlankCounts") ? 0 : o.optInt("questionBlankCounts"));
            info.setQuestionScore(o.isNull("questionScore") ? -1 : o.optInt("questionScore"));
            info.setQuestionDifficultyFactor(o.isNull("questionDifficultyFactor") ? "" : o.getString("questionDifficultyFactor"));
            TaskAnswer answer = TaskAnswerDao.getInstance(this).queryItemInfo(info.getStudentId(), info.getExamTaskId(), info.getQuestionId(), info.getQuestionType());
            if (answer != null && !TextUtils.isEmpty(answer.getStudentAnswer())) {
                info.setQuestionCorrectAnswer(answer.getStudentAnswer());
            }
            if (isDoHomeWork) {
                TaskAnswerDao.getInstance(this).insert(info.getStudentId(), info.getExamTaskId(), info.getQuestionId(), info.getQuestionType(), "", "", "", "", "", "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
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
        return URLConfig.STUDENT_PRACTICE_EXAM;
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
                break;
            case R.id.tv_current_item:
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
            case R.id.tv_exam_submit:
                TaskAnswerDao dao = TaskAnswerDao.getInstance(this);
                final List<TaskAnswer> answerList = dao.queryExam(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), mExamPracticeId);
                getSubmitInfo(answerList);
//                if (answerList == null || answerList.size() == 0) return;
                MySubmitDialog unFinishDialog = MySubmitDialog.newInstance(answerList.size() == mAnswerNum ? "习题已答完,确认要提交吗?" : "你还有习题未做完,确认要提交吗?",
                        MySubmitDialog.DIALOG_STYLE_TYPE_0, new MySubmitDialog.OnclickListener() {
                            @Override
                            public void leftClick(MySubmitDialog myDialog) {
                                myDialog.dismiss();
                            }

                            @Override
                            public void rightClick(MySubmitDialog myDialog) {
                                requestSubmitAnswer(answerList);
                                myDialog.dismiss();
                            }

                            @Override
                            public void dismiss() {

                            }
                        });
                unFinishDialog.show(getSupportFragmentManager(), "submitAnswers");
//                requestSubmitAnswer(answerList);
                break;
        }
    }

    private Map<String, String> map;
    private TaskAnswerDao dao;
    private int mAnswerNum;//已做题目数量

    private void getSubmitInfo(List<TaskAnswer> answerList) {
        mAnswerNum = 0;
        dao = TaskAnswerDao.getInstance(this);
        map = new HashMap<>();
        map.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
        map.put("examPracticeId", mExamPracticeId);
        JSONArray array = new JSONArray();
        for (TaskAnswer answer : answerList) {
            Map<String, String> data = new HashMap<>();
            data.put("examPracticeQuestionId", answer.getTaskItemId());
            data.put("myAnswer", TextUtils.isEmpty(answer.getStudentAnswer()) ? "" : StringUtils.replaceHtml2(answer.getStudentAnswer()));
            if (!TextUtils.isEmpty(answer.getResourceName()))
                data.put("answerVideo", answer.getResourceName());
            if (!TextUtils.isEmpty(answer.getResourceId()))
                data.put("answerVideoServerResId", answer.getResourceId());
            List<TaskPicInfo> picInfos = dao.queryPicInfo(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), mExamPracticeId, answer.getTaskItemId(), answer.getTaskItemType());
            if (picInfos != null && picInfos.size() > 0) {
                JSONArray pics = new JSONArray();
                for (TaskPicInfo picInfo : picInfos) {
                    pics.put(picInfo.getImageUrl());
                }
                data.put("imagePath", pics.toString());
            }
            JSONObject object = new JSONObject(data);
            array.put(object);
            if (!answer.getStudentAnswer().equals("") || !answer.getResourceLocalPath().equals("") || picInfos.size() > 0) {
                mAnswerNum = mAnswerNum + 1;
            }
        }
        map.put("answerInfo", array.toString());
    }

    private void requestSubmitAnswer(List<TaskAnswer> answerList) {
        if (mDialog == null) {
            mDialog = new DialogUtil(this);
        }
        mDialog.showDialog();

        RequestSender sender = new RequestSender(this);
        sender.sendRequest(new RequestSender.RequestData(URLConfig.SUBMIT_PRACTICE_EXAM, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.i("STUDENT_SUBMIT_EXAM", response.toString());
                mDialog.cancel();
                if ("success".equals(response.optString("result"))) {
                    dao.deleteTaskInfo(UserInfoKeeper.getInstance().getUserInfo().getUuid(), mExamPracticeId);
                    dao.deleteAllPicInfo(UserInfoKeeper.getInstance().getUserInfo().getUuid(), mExamPracticeId);
                    EventBus.getDefault().post(TAG);
                    EventBus.getDefault().post("finish" + TAG);
                    StudentViewPracticeActivity.startActivity(StudentPracticeDetailActivity.this, mTitle.getText().toString(), mExamPracticeId);
//                    ToastUtil.showToast(getString(R.string.exam_submit_success));
                    finish();
                } else {
                    ToastUtil.showToast(getString(R.string.exam_submit_fail));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                mDialog.cancel();
                ToastUtil.showToast(getString(R.string.exam_submit_fail));

            }
        },false));
    }
}
