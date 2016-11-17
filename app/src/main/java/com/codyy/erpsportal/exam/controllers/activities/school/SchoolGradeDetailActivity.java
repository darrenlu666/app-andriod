package com.codyy.erpsportal.exam.controllers.activities.school;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.TaskActivity;
import com.codyy.erpsportal.exam.controllers.activities.ExamInfoActivity;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.exam.controllers.fragments.dialogs.SwitchTopicDialog;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.exam.models.entities.ExamInfo;
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
 * 测试-试卷详情
 * Created by eachann on 2015/12/24.
 */
public class SchoolGradeDetailActivity extends TaskActivity implements View.OnClickListener {
    private static final String TAG = SchoolGradeDetailActivity.class.getSimpleName();
    private TextView mCurrent;
    private ViewPager.OnPageChangeListener mListener;
    private ExamInfo mExamInfo = new ExamInfo();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_exam_grade_bottom_layout, null);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(this, 48));
        rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mRelativeLayout.addView(view, rl);
        mCurrent = (TextView) view.findViewById(R.id.tv_current_item);
        mCurrent.setOnClickListener(this);
        TextView mArrange = (TextView) view.findViewById(R.id.tv_arrange);
        mArrange.setOnClickListener(this);
        TextView mDetail = (TextView) view.findViewById(R.id.tv_exam_detail);
        mDetail.setOnClickListener(this);
        if (!getIntent().getBooleanExtra(EXTRA_EXAM_IS_ARRANGE, true)) {
            ((LinearLayout) view).removeView(mArrange);
            setViewAnim(true, mCurrent, mDetail);
        } else {
            setViewAnim(true, mCurrent, mArrange, mDetail);
        }
    }

    @Override
    protected void addParams(Map<String, String> params) {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        if (getIntent().getStringExtra(EXTRA_EXAM_ID) == null) {
            addParam("examTaskId", getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
        } else {
            addParam("examId", getIntent().getStringExtra(EXTRA_EXAM_ID));
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

    private ArrayList<ItemInfoClass> mData = new ArrayList<>();

    @Override
    protected void addFragments(JSONObject response) {
        try {
            if ("error".equals(response.optString("result"))) {
                finish();
                ToastUtil.showToast(getApplicationContext(), getString(R.string.refresh_state_loade_error));
                return;
            }
            mExamInfo = ExamInfoActivity.getExamInfo(getIntent().getStringExtra(EXTRA_EXAM_ID), getIntent().getIntExtra(EXTRA_TYPE, ExamInfoActivity.TYPE_DEFAULT), response);
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
                mCurrent.setText(itemBuilder);
                Bundle bundle = new Bundle();
                bundle.putParcelable(TaskFragment.ARG_TASK_DATA, getQuestionInfo(o));
                bundle.putString(TaskFragment.ARG_TASK_TYPE, TaskFragment.TYPE_EXAM);
                bundle.putString(TaskFragment.ARG_TASK_STATUS, TaskFragment.STATUS_VIEW);
                addFragment(String.valueOf(i), TaskFragment.class, bundle);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            finish();
            ToastUtil.showToast(getApplicationContext(), getString(R.string.refresh_state_loade_error));
        }

    }

    private QuestionInfo getQuestionInfo(JSONObject o) {
        QuestionInfo info = new QuestionInfo();
        try {
            info.setQuestionType(o.isNull("questionType") ? "" : o.getString("questionType"));
            info.setQuestionContent(o.isNull("questionContent") ? "" : o.getString("questionContent"));
            info.setQuestionOptions(o.isNull("questionOptions") ? "" : o.getString("questionOptions"));
            info.setQuestionMediaUrl(o.isNull("questionMediaUrl") ? "" : o.getString("questionMediaUrl"));
            SpannableStringBuilder builder = null;
            if (!o.isNull("questionFillCorrectAnswer")) {
                builder = new SpannableStringBuilder();
                builder.append("<br>");
                JSONArray fillArray = o.getJSONArray("questionFillCorrectAnswer");
                for (int j = 0; j < fillArray.length(); j++) {
                    JSONObject fillObject = fillArray.getJSONObject(j);
                    builder.append(" (").append(String.valueOf(j + 1)).append(") ");
                    builder.append(fillObject.isNull("answer1") ? "" : fillObject.getString("answer1"));
                    if (!fillObject.isNull("answer2")) {
                        builder.append(" / ").append(fillObject.getString("answer2"));
                    }
                    if (!fillObject.isNull("answer3")) {
                        builder.append(" / ").append(fillObject.getString("answer3"));
                    }
                    if (!fillObject.isNull("answer4")) {
                        builder.append(" / ").append(fillObject.getString("answer4"));
                    }
                    builder.append("<br>");
                }
                builder.setSpan(new UnderlineSpan(), 0, builder.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            try {
                info.setQuestionCorrectAnswer(TaskFragment.TYPE_FILL_IN_BLANK.equals(o.getString("questionType")) ? builder.toString() : o.getString("questionCorrectAnswer"));
            } catch (Exception e) {
                info.setQuestionCorrectAnswer(o.isNull("questionCorrectAnswer") ? "" : o.getString("questionCorrectAnswer"));
            }
            info.setQuestionResolve(o.isNull("questionResolve") ? "" : o.getString("questionResolve"));
            info.setQuestionKnowledgePoint(o.isNull("questionKnowledgePoint") ? "" : o.getString("questionKnowledgePoint"));
            info.setQuestionAssembleCounts(o.isNull("questionAssembleCounts") ? "" : o.getString("questionAssembleCounts"));
            info.setQuestionDifficultyFactor(o.isNull("questionDifficultyFactor") ? "" : o.getString("questionDifficultyFactor"));
            info.setQuestionComeFrom(o.isNull("questionComeFrom") ? "" : o.getString("questionComeFrom"));
            info.setQuestionUpdateDate(o.isNull("questionUpdateDate") ? "" : o.getString("questionUpdateDate"));
            info.setQuestionResolveVideo(o.isNull("questionResolveVideo")?"":o.getString("questionResolveVideo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        return getIntent().getStringExtra(EXTRA_URL) == null ? getIntent().getStringExtra(EXTRA_TASK_URL) : getIntent().getStringExtra(EXTRA_URL);
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
            case R.id.tv_arrange:
                SchoolArrangeActivity.startActivity(this, getIntent().getStringExtra(EXTRA_EXAM_ID));
                break;
            case R.id.tv_exam_detail:
                ExamInfoActivity.startActivity(this, mExamInfo, getIntent().getIntExtra(EXTRA_TYPE, ExamInfoActivity.TYPE_DEFAULT),getIntent().getStringExtra(EXTRA_TITLE));
                break;
        }
    }

    public static void startActivity(Context from, String title, String id, boolean isArrange, int type, String url) {
        Intent intent = new Intent(from, SchoolGradeDetailActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_EXAM_ID, id);
        intent.putExtra(EXTRA_EXAM_IS_ARRANGE, isArrange);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_URL, url);
        from.startActivity(intent);
        UIUtils.addEnterAnim((Activity) from);
    }

    public static void startTaskActivity(Context from, String title, String taskId, boolean isArrange, int type, String url) {
        Intent intent = new Intent(from, SchoolGradeDetailActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_EXAM_TASK_ID, taskId);
        intent.putExtra(EXTRA_EXAM_IS_ARRANGE, isArrange);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_URL, url);
        from.startActivity(intent);
        UIUtils.addEnterAnim((Activity) from);
    }
}
