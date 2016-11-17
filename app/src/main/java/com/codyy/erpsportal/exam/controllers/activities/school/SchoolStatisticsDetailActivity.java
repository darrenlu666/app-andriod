package com.codyy.erpsportal.exam.controllers.activities.school;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.TaskActivity;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.exam.controllers.fragments.dialogs.SwitchTopicDialog;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.exam.models.entities.QuestionInfo;
import com.codyy.erpsportal.homework.models.entities.ItemInfoClass;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * 学生统计-查看批阅详情
 * Created by eachann on 2016-03-02 11:36:11.
 */
public class SchoolStatisticsDetailActivity extends TaskActivity {
    private static final String TAG = SchoolStatisticsDetailActivity.class.getSimpleName();
    private ViewPager.OnPageChangeListener mListener;
    private ArrayList<ItemInfoClass> mData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                mTvTaskTitle.setText(str);
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

    private static final String EXTRA_BASE_USER_ID = SchoolStatisticsDetailActivity.class.getPackage() + ".EXTRA_BASE_USER_ID";

    public static void startActivity(Context activity, String title, String examTaskId, String baseUserId) {
        Intent intent = new Intent(activity, SchoolStatisticsDetailActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_EXAM_TASK_ID, examTaskId);
        intent.putExtra(EXTRA_BASE_USER_ID, baseUserId);
        activity.startActivity(intent);
        UIUtils.addEnterAnim((Activity) activity);
    }


    @Override
    protected void addParams(Map<String, String> params) {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
    }

    @Override
    protected void addFragments(JSONObject response) {
        try {
            if("error".equals(response.optString("result"))){
                finish();
                ToastUtil.showToast(getApplicationContext(),getString(R.string.refresh_state_loade_error));
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
                itemInfoClass.setWorkItemType(o.isNull("questionType") ? "" : o.getString("questionType"));
                mData.add(itemInfoClass);
                String str = 1 + "/" + mData.size();
                mTvTaskTitle.setText(str);
                Bundle bundle = new Bundle();
                bundle.putParcelable(TaskFragment.ARG_TASK_DATA, getQuestionInfo(o));
                bundle.putString(TaskFragment.ARG_TASK_TYPE, TaskFragment.TYPE_EXAM);
                bundle.putString(TaskFragment.ARG_TASK_STATUS, TaskFragment.STATUS_READ);
                addFragment(String.valueOf(i), TaskFragment.class, bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
            ToastUtil.showToast(getApplicationContext(),getString(R.string.refresh_state_loade_error));
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
            if (TaskFragment.TYPE_FILL_IN_BLANK.equals(info.getQuestionType()) && o.isNull("fillInAnswers")) {
                StringBuilder builder = new StringBuilder();
                try {
                    JSONArray array = o.getJSONArray("fillInAnswers");
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
                info.setQuestionCorrectAnswer(o.isNull("questionCorrectAnswer") ? "" : o.getString("questionCorrectAnswer"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    private TextView mTvTaskTitle;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_button, menu);
        MenuItem menuItem = menu.findItem(R.id.task_read_menu);
        LinearLayout linearLayout = (LinearLayout) menuItem.getActionView();
        mTvTaskTitle = (TextView) linearLayout.findViewById(R.id.task_title);
        mTvTaskTitle.setText(getString(R.string.exam_info));
        mTvTaskTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

            }
        });
        return super.onCreateOptionsMenu(menu);
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
        return URLConfig.GET_STUDENT_STATISTIC_DETAIL.replace("{examTaskId}", getIntent().getStringExtra(EXTRA_EXAM_TASK_ID)).replace("{baseUserId}",getIntent().getStringExtra(EXTRA_BASE_USER_ID) );
    }

    private int mLastPosition = 0;

}
