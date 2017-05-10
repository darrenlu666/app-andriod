package com.codyy.erpsportal.exam.controllers.activities.teacher;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.TaskActivity;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.TaskReadDao;
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
import com.codyy.erpsportal.homework.models.entities.student.StudentPersonalInfo;
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
 * 老师-按学生批阅
 * Created by eachann on 2016-02-04.
 */
public class TeacherDoReadActivity extends TaskActivity implements View.OnClickListener {
    private static final String TAG = TeacherDoReadActivity.class.getSimpleName();
    private TextView mTime;
    private ViewPager.OnPageChangeListener mListener;
    private ArrayList<ItemInfoClass> mData = new ArrayList<>();
    private StudentPersonalInfo mPersonalInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPersonalInfo = getIntent().getParcelableExtra(EXTRA_DATA);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_exam_answer_bottom_layout, null);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(this, 48));
        rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mRelativeLayout.addView(view, rl);
        TextView mCurrent = (TextView) view.findViewById(R.id.tv_current_item);
        mCurrent.setOnClickListener(this);
        mCurrent.setVisibility(View.GONE);
        mTime = (TextView) view.findViewById(R.id.tv_exam_time);
        mTime.setOnClickListener(this);
        TextView mSubmit = (TextView) view.findViewById(R.id.tv_exam_submit);
        mSubmit.setText(getString(R.string.exam_read_submit));
        mSubmit.setOnClickListener(this);
        setViewAnim(true, mCurrent, mTime, mSubmit);
        mTitle.setText(mPersonalInfo.getStudentName());
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHomeKeyDown();
            }
        });
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
        if (mListener != null) {
            mPager.removeOnPageChangeListener(mListener);
        }
        super.onDestroy();
    }


    public static void startActivity(Activity activity, String examTaskId, StudentPersonalInfo personalInfo, String examResultId) {
        Intent intent = new Intent(activity, TeacherDoReadActivity.class);
        intent.putExtra(EXTRA_EXAM_TASK_ID, examTaskId);
        intent.putExtra(EXTRA_EXAM_RESULT_ID, examResultId);
        intent.putExtra(EXTRA_DATA, personalInfo);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }


    @Override
    protected void addParams(Map<String, String> params) {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        addParam("examResultId", getIntent().getStringExtra(EXTRA_EXAM_RESULT_ID));
    }

    @Override
    protected void addFragments(JSONObject response) {
        try {
            if ("error".equals(response.optString("result"))) {
                finish();
                ToastUtil.showToast(getApplicationContext(), getString(R.string.refresh_state_loade_error));
                return;
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
                itemInfoClass.setWorkItemType(o.isNull("type") ? "" : o.getString("type"));
                itemInfoClass.setWorkItemId(o.isNull("examQuestionId") ? "" : o.getString("examQuestionId"));
                mData.add(itemInfoClass);
                String str = 1 + "/" + mData.size();
                int position = str.indexOf("/");
                SpannableStringBuilder itemBuilder = new SpannableStringBuilder(str);
                itemBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 0, position, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTime.setText(itemBuilder);
                Bundle bundle = new Bundle();
                bundle.putParcelable(TaskFragment.ARG_TASK_DATA, getQuestionInfo(o));
                bundle.putString(TaskFragment.ARG_TASK_TYPE, TaskFragment.TYPE_EXAM);
                bundle.putString(TaskFragment.ARG_TASK_STATUS, TaskFragment.STATUS_DO_READ);
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

            info.setStudentId(mPersonalInfo.getStudentId());
            info.setExamTaskId(getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
            info.setQuestionId(o.isNull("examQuestionId") ? "" : o.getString("examQuestionId"));
            info.setQuestionResultId(o.isNull("examQuestionResultId") ? "" : o.getString("examQuestionResultId"));
            info.setQuestionType(o.isNull("type") ? "" : o.getString("type"));
            info.setQuestionContent(o.isNull("content") ? "" : o.getString("content"));
            info.setQuestionOptions(o.isNull("options") ? "" : o.getString("options"));
            info.setQuestionMediaUrl(o.isNull("contentVideo") ? "" : o.getString("contentVideo"));
            info.setQuestionScores(o.isNull("score") ? "0" : String.valueOf(o.getString("score")));
            info.setQuestionScore(o.isNull("myScore") ? 0 : o.optInt("myScore"));
            info.setQuestionDifficultyFactor(o.isNull("difficulty") ? "" : o.getString("difficulty"));
            info.setQuestionStudentAnswer(o.isNull("myAnswer") ? "" : o.getString("myAnswer"));
            info.setQuestionStudentAnswerMediaUrl(o.isNull("answerVideo") ? "" : o.getString("answerVideo"));
//        info.setQuestionResolve(o.isNull("questionResolve") ? "" : o.getString("questionResolve"));
//        info.setQuestionResolveVideo(o.isNull("questionResolveVideo") ? "" : o.getString("questionResolveVideo"));
            if (TaskFragment.TYPE_FILL_IN_BLANK.equals(info.getQuestionType()) && !o.isNull("fillList")) {
                StringBuilder builder = new StringBuilder();
                try {
                    JSONArray array = o.getJSONArray("fillList");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        builder.append(object.isNull("answerGrp1") ? "" : object.getString("answerGrp1"));
                        builder.append(object.isNull("answerGrp2") ? "" : "/" + object.getString("answerGrp2"));
                        builder.append(object.isNull("answerGrp3") ? "" : "/" + object.getString("answerGrp3"));
                        builder.append(object.isNull("answerGrp4") ? "" : "/" + object.getString("answerGrp4") + ";");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                info.setQuestionCorrectAnswer(builder.toString());
            } else {
                info.setQuestionCorrectAnswer(o.isNull("answer") ? "" : o.getString("answer"));
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
        return URLConfig.GET_EXAM_READ_BY_STUDENT_PRE;
    }

    private int mLastPosition = 0;
    private DialogUtil mDialog;

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_exam_time:
                TaskReadDao topicDao = TaskReadDao.newInstance(this);
                final List<TaskReadDao.TaskItemReadInfo> topicList = topicDao.query(mPersonalInfo.getStudentId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
                for (int i = 0; i < mData.size(); i++) {
                    for (int j = 0; j < topicList.size(); j++) {
                        if (mData.get(i).getWorkItemId().equals(topicList.get(j).getTaskItemId()) && mLastPosition != i) {
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
            case R.id.tv_current_item:
                break;
            case R.id.tv_exam_submit:
                TaskReadDao dao = TaskReadDao.newInstance(this);
                final List<TaskReadDao.TaskItemReadInfo> answerList = dao.queryExam(mPersonalInfo.getStudentId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
                ArrayList<ItemInfoClass> subjectiveData = new ArrayList<>();
                for (ItemInfoClass item : mData) {
                    if ("ASK_ANSWER".equals(item.getWorkItemType()) || "COMPUTING".equals(item.getWorkItemType())) {
                        subjectiveData.add(item);
                    }
                }
                int mScoreCount = 0;//是否已经添加了“得分”
                for (TaskReadDao.TaskItemReadInfo item : answerList) {
                    if ("ASK_ANSWER".equals(item.getTaskItemType()) || "COMPUTING".equals(item.getTaskItemType())) {
                        if (item.getTaskItemReadScore().equals("") || item.getTaskItemReadScore().startsWith("∷")) {
                        } else {
                            mScoreCount++;
                        }
                    }
                }
                if (mScoreCount >= subjectiveData.size()) {//answerList.size() == mData.size()
                    MySubmitDialog unFinishDialog = MySubmitDialog.newInstance("您已完成打分,确认提交吗?",
                            MySubmitDialog.DIALOG_STYLE_TYPE_0, new MySubmitDialog.OnclickListener() {
                                @Override
                                public void leftClick(MySubmitDialog myDialog) {
                                    myDialog.dismiss();
                                }

                                @Override
                                public void rightClick(MySubmitDialog myDialog) {
                                    myDialog.dismiss();
                                    requestSubmitAnswer(answerList);
                                }

                                @Override
                                public void dismiss() {

                                }
                            });
                    unFinishDialog.show(getSupportFragmentManager(), "submitAnswers");
                } else {//if (answerList.size() < mData.size())
                    MySubmitDialog unFinishDialog = MySubmitDialog.newInstance("您有试题未打分,请完成打分!", "继续批阅",
                            MySubmitDialog.DIALOG_STYLE_TYPE_2, new MySubmitDialog.OnclickListener() {
                                @Override
                                public void leftClick(MySubmitDialog myDialog) {
                                    myDialog.dismiss();
                                }

                                @Override
                                public void rightClick(MySubmitDialog myDialog) {
                                    myDialog.dismiss();
                                }

                                @Override
                                public void dismiss() {

                                }
                            });
                    unFinishDialog.show(getSupportFragmentManager(), "submitAnswers");
                }


                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //exitActivity();
            onHomeKeyDown();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onHomeKeyDown(){
        MySubmitDialog unReadBackDialog = MySubmitDialog.newInstance("您还未批阅完，确定退出吗？", "退出", "取消", MySubmitDialog.DIALOG_STYLE_TYPE_3, new MySubmitDialog.OnclickListener() {
            @Override
            public void leftClick(MySubmitDialog myDialog) {
                myDialog.dismiss();
            }

            @Override
            public void rightClick(MySubmitDialog myDialog) {
                myDialog.dismiss();
                TeacherDoReadActivity.this.finish();
            }

            @Override
            public void dismiss() {

            }
        });
        unReadBackDialog.show(getSupportFragmentManager(), "show");
    }

    private void exitActivity() {
        TaskReadDao dao = TaskReadDao.newInstance(this);
        final List<TaskReadDao.TaskItemReadInfo> answerList = dao.queryExam(mPersonalInfo.getStudentId(), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
        ArrayList<ItemInfoClass> subjectiveData = new ArrayList<>();
        for (ItemInfoClass item : mData) {
            if ("ASK_ANSWER".equals(item.getWorkItemType()) || "COMPUTING".equals(item.getWorkItemType())) {
                subjectiveData.add(item);
            }
        }
        if (answerList.size() < subjectiveData.size()) {
            MySubmitDialog unFinishDialog = MySubmitDialog.newInstance("您有试题未打分,请完成打分!", "继续批阅",
                    MySubmitDialog.DIALOG_STYLE_TYPE_2, new MySubmitDialog.OnclickListener() {
                        @Override
                        public void leftClick(MySubmitDialog myDialog) {
                            myDialog.dismiss();
                        }

                        @Override
                        public void rightClick(MySubmitDialog myDialog) {
                            myDialog.dismiss();
                        }

                        @Override
                        public void dismiss() {

                        }
                    });
            unFinishDialog.show(getSupportFragmentManager(), "submitAnswers");
        } else if (answerList.size() == subjectiveData.size()) {
            MySubmitDialog unFinishDialog = MySubmitDialog.newInstance("您已完成打分,确认提交吗?",
                    MySubmitDialog.DIALOG_STYLE_TYPE_0, new MySubmitDialog.OnclickListener() {
                        @Override
                        public void leftClick(MySubmitDialog myDialog) {
                            myDialog.dismiss();
                            finish();
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
        }
    }

    private void requestSubmitAnswer(List<TaskReadDao.TaskItemReadInfo> answerList) {
        if (mDialog == null) {
            mDialog = new DialogUtil(TeacherDoReadActivity.this);
        }
        mDialog.showDialog();
        Map<String, String> map = new HashMap<>();
        map.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
        map.put("examResultId", getIntent().getStringExtra(EXTRA_EXAM_RESULT_ID));
        map.put("examTaskId", getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
        JSONArray array = new JSONArray();
        for (TaskReadDao.TaskItemReadInfo answer : answerList) {
            Map<String, String> data = new HashMap<>();
            data.put("examResultQuestionId", answer.getTaskItemId());
            if ((answer.getTaskItemType().equals(TaskFragment.TYPE_ASK_ANSWER) || answer.getTaskItemType().equals(TaskFragment.TYPE_COMPUTING))) {
                String[] strings = answer.getTaskItemReadScore().split("∷");
                if (strings.length > 0)
                    data.put("score", strings[0]);
                if (strings.length > 1)
                    data.put("standScore", strings[1]);
            }
            data.put("teacherComment", TextUtils.isEmpty(answer.getTaskItemReadComment()) ? "" : StringUtils.replaceHtml2(answer.getTaskItemReadComment()));
            JSONObject object = new JSONObject(data);
            array.put(object);
        }
        map.put("commitInfo", array.toString());
        RequestSender sender = new RequestSender(this);
        sender.sendRequest(new RequestSender.RequestData(URLConfig.POST_UPDATE_STU_COMENT, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.i("STUDENT_SUBMIT_EXAM", response.toString());
                mDialog.cancel();
                if ("success".equals(response.optString("result"))) {
                    ToastUtil.showToast("提交成功");
                    EventBus.getDefault().post(TeacherDoReadActivity.class.getSimpleName());
                    finish();
                } else {
                    ToastUtil.showToast("提交失败");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                mDialog.cancel();
                ToastUtil.showToast("提交失败");
            }
        }));
    }
}
