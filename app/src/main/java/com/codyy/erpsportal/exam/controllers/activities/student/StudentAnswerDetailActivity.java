package com.codyy.erpsportal.exam.controllers.activities.student;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.TaskActivity;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.exam.controllers.activities.ExamInfoActivity;
import com.codyy.erpsportal.exam.controllers.fragments.dialogs.SwitchTopicDialog;
import com.codyy.erpsportal.exam.models.entities.ExamInfo;
import com.codyy.erpsportal.exam.models.entities.QuestionInfo;
import com.codyy.erpsportal.homework.models.entities.ItemInfoClass;
import com.codyy.erpsportal.homework.widgets.MySubmitDialog;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.TaskAnswerDao;
import com.codyy.erpsportal.homework.models.entities.task.AnswerTimeLog;
import com.codyy.erpsportal.homework.models.entities.task.TaskAnswer;
import com.codyy.erpsportal.homework.models.entities.task.TaskPicInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * 学生答题
 * Created by eachann on 2015/12/24.
 */
public class StudentAnswerDetailActivity extends TaskActivity implements View.OnClickListener, Callback {
    public static final String TAG = "com.codyy.erpsportal.exam.controllers.activities.student.StudentAnswerDetailActivity";
    private TextView mCurrent;
    private TextView mTime;
    private ViewPager.OnPageChangeListener mListener;
    private ExamInfo mExamInfo = new ExamInfo();
    private ArrayList<ItemInfoClass> mData = new ArrayList<>();
    private DialogUtil mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        if (mDialog == null) {
            mDialog = new DialogUtil(this);
        }
        mDialog.showDialog();*/
        mHandler = new Handler(this);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_exam_answer_bottom_layout, null);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(this, 48));
        rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mRelativeLayout.addView(view, rl);
        mCurrent = (TextView) view.findViewById(R.id.tv_current_item);
        mCurrent.setOnClickListener(this);
        mTime = (TextView) view.findViewById(R.id.tv_exam_time);
        mTime.setOnClickListener(this);
        TextView mSubmit = (TextView) view.findViewById(R.id.tv_exam_submit);
        mSubmit.setText(getString(R.string.exam_submit_answer));
        mSubmit.setOnClickListener(this);
        setViewAnim(true, mCurrent, mTime, mSubmit);
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
        if (mListener != null && mPager != null) {
            mPager.removeOnPageChangeListener(mListener);
        }
        if (mTimer != null) {
            mTimer.cancel();
        }
        mHandler.removeMessages(WHAT);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private static final String EXTRA_EXAM_PRACTICE_STATUS = StudentViewCheckedActivity.class.getPackage() + ".EXTRA_EXAM_PRACTICE_STATUS";
    private static final String EXTRA_EXAM_SELF_DURATION = StudentViewCheckedActivity.class.getPackage() + ".EXTRA_EXAM_SELF_DURATION";
    private static final String EXTRA_EXAM_SELF_SUBJECTNAME = StudentViewCheckedActivity.class.getPackage() + ".EXTRA_EXAM_SELF_SUBJECTNAME";

    public static void startActivity(Activity activity, String title, String examTaskId, String practiceStatus, String examResultId) {
        Intent intent = new Intent(activity, StudentAnswerDetailActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_EXAM_TASK_ID, examTaskId);
        intent.putExtra(EXTRA_EXAM_RESULT_ID, examResultId);
        intent.putExtra(EXTRA_EXAM_PRACTICE_STATUS, practiceStatus);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    public static void startActivity(Activity activity, String title, String examTaskId, String practiceStatus, String examResultId, String duration, String subject) {
        Intent intent = new Intent(activity, StudentAnswerDetailActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_EXAM_TASK_ID, examTaskId);
        intent.putExtra(EXTRA_EXAM_RESULT_ID, examResultId);
        intent.putExtra(EXTRA_EXAM_PRACTICE_STATUS, practiceStatus);
        intent.putExtra(EXTRA_EXAM_SELF_DURATION, duration);
        intent.putExtra(EXTRA_EXAM_SELF_SUBJECTNAME, subject);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    @Override
    protected void addParams(Map<String, String> params) {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
    }

    private Timer mTimer = null;
    private final static int WHAT = 0;
    private final static String DIALOG_SUBMIT_ANSWERS = "DIALOG_SUBMIT_ANSWERS";
    private final static String DIALOG_AUTO_SUBMIT_ANSWERS = "DIALOG_AUTO_SUBMIT_ANSWERS";
    private Handler mHandler;

    private void autoSendAnswer() {
        final TaskAnswerDao dao = TaskAnswerDao.getInstance(StudentAnswerDetailActivity.this);
        final List<TaskAnswer> answerList = dao.queryExam(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
        Map<String, String> map = new HashMap<>();
        map.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
        map.put("examTaskId", getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
        map.put("examResultId", getIntent().getStringExtra(EXTRA_EXAM_RESULT_ID));
        JSONArray array = new JSONArray();
        for (TaskAnswer answer : answerList) {
            Map<String, String> data = new HashMap<>();
            data.put("examQuestionId", answer.getTaskItemId());
            data.put("answer", TextUtils.isEmpty(answer.getStudentAnswer()) ? "" : StringUtils.replaceHtml2(answer.getStudentAnswer()));
            data.put("answerPath", TextUtils.isEmpty(answer.getResourceId()) ? "" : answer.getResourceId());
            List<TaskPicInfo> picInfos = dao.queryPicInfo(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID), answer.getTaskItemId(), answer.getTaskItemType());
            if (picInfos != null && picInfos.size() > 0) {
                JSONArray pics = new JSONArray();
                for (TaskPicInfo picInfo : picInfos) {
                    pics.put(picInfo.getImageUrl());
                }
                data.put("imagePath", pics.toString());
            }
            data.put("answerVideo", answer.getResourceName());
            data.put("answerVideoServerResId", answer.getResourceId());
            JSONObject object = new JSONObject(data);
            array.put(object);
        }
        map.put("answerInfo", array.toString());
        RequestSender sender = new RequestSender(StudentAnswerDetailActivity.this);
        sender.sendRequest(new RequestSender.RequestData(URLConfig.STUDENT_SUBMIT_EXAM, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ("success".equals(response.optString("result")) && mToolbar != null) {
                    dao.deleteAnswerTimeLog(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
                    dao.deleteTaskInfo(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
                    dao.deleteAllPicInfo(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
                    EventBus.getDefault().post(TAG);
                    ToastUtil.showToast(getString(R.string.exam_submit_success));
                    mHandler.removeMessages(WHAT);
                    mHandler.removeCallbacksAndMessages(null);
                    finish();
                } else {
                    ToastUtil.showToast(getString(R.string.exam_submit_fail));
                    mHandler.removeMessages(WHAT);
                    mHandler.removeCallbacksAndMessages(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.showToast(getString(R.string.exam_submit_fail));
                mHandler.removeMessages(WHAT);
                mHandler.removeCallbacksAndMessages(null);
            }
        }
        ,false,false));
    }

    private boolean isDoHomeWork;

    @Override
    protected void addFragments(JSONObject response) {
        try {
            if ("error".equals(response.optString("result"))) {
                finish();
                ToastUtil.showToast(this, getString(R.string.refresh_state_loade_error));
                return;
            }
            mExamInfo = ExamInfoActivity.getExamInfo(getIntent().getStringExtra(EXTRA_EXAM_TASK_ID), 1, response);
            TaskAnswerDao dao = TaskAnswerDao.getInstance(this);
            long currentTime = System.currentTimeMillis();
            if (!TextUtils.isEmpty(getIntent().getStringExtra(EXTRA_EXAM_SELF_DURATION))) {
                dao.insertAnswerTimeLog(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID), (currentTime + Integer.parseInt(getIntent().getStringExtra(EXTRA_EXAM_SELF_DURATION)) * 60 * 1000), currentTime, getIntent().getStringExtra(EXTRA_EXAM_RESULT_ID));
            } else if (mExamInfo != null) {
                dao.insertAnswerTimeLog(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID), (currentTime + Integer.parseInt(mExamInfo.getExamDuartion().replace("分钟", "")) * 60 * 1000), currentTime, getIntent().getStringExtra(EXTRA_EXAM_RESULT_ID));
            }
            final AnswerTimeLog log = dao.queryAnswerTimeLog(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
            if (log != null) {
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        android.os.Message message = new android.os.Message();
                        message.what = WHAT;
                        message.obj = log;
                        mHandler.sendMessage(message);
                    }
                }, 0, 1000);
            }
            isDoHomeWork = TaskAnswerDao.getInstance(this).query(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID)).size() == 0;
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
                itemInfoClass.setWorkItemId(o.isNull("questionId") ? "" : o.getString("questionId"));
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
            ToastUtil.showToast(getApplicationContext(), getString(R.string.refresh_state_loade_error));
        } finally {
           /* mDialog.cancel();*/
        }
    }

    private QuestionInfo getQuestionInfo(JSONObject o) {
        QuestionInfo info = null;
        try {
            info = new QuestionInfo();
            if (mExamInfo != null) {
                if (mExamInfo.getExamSubject().contains(getResources().getStringArray(R.array.exam_subject_name)[0]) || mExamInfo.getExamSubject().contains(getResources().getStringArray(R.array.exam_subject_name)[1]) ||
                        mExamInfo.getExamSubject().contains(getResources().getStringArray(R.array.exam_subject_name)[2]) || mExamInfo.getExamSubject().contains(getResources().getStringArray(R.array.exam_subject_name)[3]) ||
                        mExamInfo.getExamSubject().contains(getResources().getStringArray(R.array.exam_subject_name)[4]) || mExamInfo.getExamSubject().contains(getResources().getStringArray(R.array.exam_subject_name)[5]))
                    info.setHint(getString(R.string.input_answer_page_hint));
            } else if (!TextUtils.isEmpty(getIntent().getStringExtra(EXTRA_EXAM_SELF_SUBJECTNAME)) && getIntent().getStringExtra(EXTRA_EXAM_SELF_SUBJECTNAME).contains(getResources().getStringArray(R.array.exam_subject_name)[0]) ||
                    getIntent().getStringExtra(EXTRA_EXAM_SELF_SUBJECTNAME).contains(getResources().getStringArray(R.array.exam_subject_name)[1]) || getIntent().getStringExtra(EXTRA_EXAM_SELF_SUBJECTNAME).contains(getResources().getStringArray(R.array.exam_subject_name)[2]) ||
                    getIntent().getStringExtra(EXTRA_EXAM_SELF_SUBJECTNAME).contains(getResources().getStringArray(R.array.exam_subject_name)[3]) || getIntent().getStringExtra(EXTRA_EXAM_SELF_SUBJECTNAME).contains(getResources().getStringArray(R.array.exam_subject_name)[4]) ||
                    getIntent().getStringExtra(EXTRA_EXAM_SELF_SUBJECTNAME).contains(getResources().getStringArray(R.array.exam_subject_name)[5])) {
                info.setHint(getString(R.string.input_answer_page_hint));
            }
            info.setStudentId(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId());
            info.setExamTaskId(getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
            info.setQuestionId(o.isNull("questionId") ? "" : o.getString("questionId"));
            info.setQuestionType(o.isNull("questionType") ? "" : o.getString("questionType"));
            info.setQuestionContent(o.isNull("questionContent") ? "" : o.getString("questionContent"));
            info.setQuestionOptions(o.isNull("questionOptions") ? "" : o.getString("questionOptions"));
            info.setQuestionMediaUrl(o.isNull("questionMediaUrl") ? "" : o.getString("questionMediaUrl"));
            info.setQuestionBlankCounts(o.isNull("questionBlankCounts") ? 0 : o.optInt("questionBlankCounts"));
            info.setQuestionScore(o.isNull("questionScore") ? 0 : o.optInt("questionScore"));
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
        return URLConfig.STUDENT_ANSWER_EXAM.replace("{examTaskId}", getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
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
                final TaskAnswerDao dao = TaskAnswerDao.getInstance(this);
                final List<TaskAnswer> answerList = dao.queryExam(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
                for (int i = 0; i < mData.size(); i++) {
                    for (int j = 0; j < answerList.size(); j++) {
                        if (mData.get(i).getWorkItemId().equals(answerList.get(j).getTaskItemId()) && mLastPosition != i) {
                            mData.get(i).setColor(Color.parseColor("#FFE86153"));
                        }
                    }
                }
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
                requestSubmitAnswer();
                break;
        }
    }

    private void requestSubmitAnswer() {
        final TaskAnswerDao dao = TaskAnswerDao.getInstance(this);
        final List<TaskAnswer> answerList = dao.queryExam(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
        MySubmitDialog unFinishDialog = MySubmitDialog.newInstance(answerList.size() == mData.size() ? getString(R.string.exam_answer_submit_completed) : getString(R.string.exam_answer_submit_uncompleted),
                MySubmitDialog.DIALOG_STYLE_TYPE_0, new MySubmitDialog.OnclickListener() {
                    @Override
                    public void leftClick(MySubmitDialog myDialog) {
                        myDialog.dismiss();
                    }

                    @Override
                    public void rightClick(MySubmitDialog myDialog) {
                        if (mDialog == null) {
                            mDialog = new DialogUtil(StudentAnswerDetailActivity.this);
                        }
                        mDialog.showDialog();
                        Map<String, String> map = new HashMap<>();
                        map.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
                        map.put("examTaskId", getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
                        map.put("examResultId", getIntent().getStringExtra(EXTRA_EXAM_RESULT_ID));
                        JSONArray array = new JSONArray();
                        for (TaskAnswer answer : answerList) {
                            Map<String, String> data = new HashMap<>();
                            data.put("examQuestionId", answer.getTaskItemId());
                            data.put("answer", TextUtils.isEmpty(answer.getStudentAnswer()) ? "" : StringUtils.replaceHtml2(answer.getStudentAnswer()));
                            data.put("answerPath", TextUtils.isEmpty(answer.getResourceId()) ? "" : answer.getResourceId());
                            List<TaskPicInfo> picInfos = dao.queryPicInfo(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID), answer.getTaskItemId(), answer.getTaskItemType());
                            if (picInfos != null && picInfos.size() > 0) {
                                JSONArray pics = new JSONArray();
                                for (TaskPicInfo picInfo : picInfos) {
                                    pics.put(picInfo.getImageUrl());
                                }
                                data.put("imagePath", pics.toString());
                            }
                            data.put("answerVideo", answer.getResourceName());
                            data.put("answerVideoServerResId", answer.getResourceId());
                            JSONObject object = new JSONObject(data);
                            array.put(object);
                        }
                        map.put("answerInfo", array.toString());
                        RequestSender sender = new RequestSender(StudentAnswerDetailActivity.this);
                        sender.sendRequest(new RequestSender.RequestData(URLConfig.STUDENT_SUBMIT_EXAM, map, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                mDialog.cancel();
                                if ("success".equals(response.optString("result"))) {
                                    dao.deleteAnswerTimeLog(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
                                    dao.deleteTaskInfo(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
                                    dao.deleteAllPicInfo(UserInfoKeeper.getInstance().getUserInfo().getBaseUserId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
                                    EventBus.getDefault().post(TAG);
                                    ToastUtil.showToast(getString(R.string.exam_submit_success));
                                    finish();
                                } else {
                                    ToastUtil.showToast(getString(R.string.exam_submit_fail));
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                mDialog.cancel();
                                ToastUtil.showToast(getString(R.string.exam_submit_fail));
                            }
                        }, false, false));
                        myDialog.dismiss();
                    }

                    @Override
                    public void dismiss() {

                    }
                });
        unFinishDialog.show(getSupportFragmentManager(), DIALOG_SUBMIT_ANSWERS);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 增加了默认的返回finish事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                exitActivity();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);

    }

    private void exitActivity() {
        MySubmitDialog unFinishDialog = MySubmitDialog.newInstance(getString(R.string.exam_answer_exit_title), getString(R.string.exam_answer_exit_not), getString(R.string.exam_answer_exit_confirm),
                MySubmitDialog.DIALOG_STYLE_TYPE_3, new MySubmitDialog.OnclickListener() {
                    @Override
                    public void leftClick(MySubmitDialog myDialog) {
                        myDialog.dismiss();
                    }

                    @Override
                    public void rightClick(MySubmitDialog myDialog) {
                        myDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void dismiss() {

                    }
                });
        unFinishDialog.show(getSupportFragmentManager(), DIALOG_SUBMIT_ANSWERS);
    }

    /**
     * 是否是当前activity
     */
    private boolean isOnCurrentActivity = true;

    @Override
    protected void onRestart() {
        super.onRestart();
        isOnCurrentActivity = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnCurrentActivity = false;
    }

    /**
     * 是否已自动提交
     */
    private boolean mIsAutoSend;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT:
                AnswerTimeLog log = (AnswerTimeLog) msg.obj;
                if (log != null) {
                    if (log.getEstimateEndTime() - System.currentTimeMillis() >= 0) {
                        mTime.setText(DateUtil.formatMediaTime(log.getEstimateEndTime() - System.currentTimeMillis()));
                        if (log.getEstimateEndTime() - System.currentTimeMillis() < 4000 && log.getEstimateEndTime() - System.currentTimeMillis() > 2000) {
                            Cog.i(TAG, String.valueOf(log.getEstimateEndTime() - System.currentTimeMillis()));
                            MySubmitDialog other = (MySubmitDialog) getSupportFragmentManager().findFragmentByTag(DIALOG_SUBMIT_ANSWERS);
                            if (other != null) other.dismiss();
                            MySubmitDialog unFinishDialog = (MySubmitDialog) getSupportFragmentManager().findFragmentByTag(DIALOG_AUTO_SUBMIT_ANSWERS);
                            if (unFinishDialog == null && isOnCurrentActivity) {
                                unFinishDialog = MySubmitDialog.newInstance(getString(R.string.exam_answer_submit_auto_tip), getString(R.string.exam_answer_submit_yes),
                                        MySubmitDialog.DIALOG_STYLE_TYPE_2, new MySubmitDialog.OnclickListener() {
                                            @Override
                                            public void leftClick(MySubmitDialog myDialog) {
                                                myDialog.dismiss();
                                            }

                                            @Override
                                            public void rightClick(MySubmitDialog myDialog) {
                                                autoSendAnswer();
                                                if (!mIsAutoSend) {
                                                    myDialog.dismiss();
                                                    mIsAutoSend = true;
                                                }
                                            }

                                            @Override
                                            public void dismiss() {

                                            }
                                        });
                                unFinishDialog.show(getSupportFragmentManager(), DIALOG_AUTO_SUBMIT_ANSWERS);
                            }
                        }
                    } else {
                        mTime.setText(DateUtil.formatMediaTime(0));
                        if (!mIsAutoSend) {
                            autoSendAnswer();
                            mIsAutoSend = true;
                        }
                    }
                }
                break;

            default:
                break;
        }
        return false;
    }
}
