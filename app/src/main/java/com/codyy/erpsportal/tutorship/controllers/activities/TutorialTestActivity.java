package com.codyy.erpsportal.tutorship.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.TabsAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.commons.models.parsers.JsonParser.OnParsedListener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.erpsportal.exam.models.entities.QuestionInfo;
import com.codyy.erpsportal.tutorship.models.entities.TutorialTest;
import com.codyy.url.URLConfig;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TutorialTestActivity extends AppCompatActivity implements OnPageChangeListener {

    private final static String TAG = "TutorialTestActivity";

    private final static String EXTRA_TUTORIAL_TEST = "com.codyy.erpsportal.TUTORIAL_TEST";

    @Bind(R.id.title_bar)
    TitleBar mTitleBar;

    @Bind(R.id.pager)
    ViewPager mViewPager;

    private TabsAdapter mTabsAdapter;

    private TutorialTest mTutorialTest;

    private UserInfo mUserInfo;

    private RequestSender mRequestSender;

    private List<QuestionInfo> mQuestionInfos;

    private int mSingleChoiceQuestionCount;

    private int mMultiChoiceQuestionCount;

    @OnClick(R.id.btn_question_number)
    void onQuestionNumberBtnClick(View view) {
        TutorialTestNumbersActivity.start(this, mSingleChoiceQuestionCount, mMultiChoiceQuestionCount, mViewPager.getCurrentItem());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_test);
        ButterKnife.bind(this);
        mViewPager.addOnPageChangeListener(this);
        mTabsAdapter = new TabsAdapter(this, getSupportFragmentManager(), mViewPager);
        mTutorialTest = getIntent().getParcelableExtra(EXTRA_TUTORIAL_TEST);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        mRequestSender = new RequestSender(this);
        loadData();
    }

    private void loadData() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("meetExamId", mTutorialTest.getId());
        showLoadingDialog();
        mRequestSender.sendRequest(new RequestData(URLConfig.GET_TUTORIAL_TEST, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "loadData response=" , response);
                dismissLoadingDialog();
                JsonParser<QuestionInfo> jsonParser = new JsonParser<QuestionInfo>() {
                    @Override
                    public QuestionInfo parse(JSONObject jsonObject) {
                        QuestionInfo questionInfo = new QuestionInfo();
                        questionInfo.setQuestionId(jsonObject.optString("meetExamQuestionId"));
                        questionInfo.setQuestionContent(jsonObject.optString("content"));
                        if (!jsonObject.isNull("contentVideo")) {
                            questionInfo.setQuestionMediaUrl(jsonObject.optString("contentVideo"));
                        }
                        questionInfo.setQuestionOptions(jsonObject.optString("options"));
                        questionInfo.setQuestionCorrectAnswer(jsonObject.optString("answer"));
                        questionInfo.setQuestionType(jsonObject.optString("type"));
                        questionInfo.setQuestionResolve(jsonObject.optString("resolve"));
                        questionInfo.setQuestionDifficultyFactor(jsonObject.optString("difficulty"));
                        if (!jsonObject.isNull("resolveVideo"))
                            questionInfo.setQuestionResolveVideo(jsonObject.optString("resolveVideo"));
                        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                        questionInfo.setQuestionUpdateDate(dateTimeFormatter.print( jsonObject.optLong("updateTime")));
                        String source = jsonObject.optString("queSource");
                        questionInfo.setQuestionComeFrom(source);
                        return questionInfo;
                    }
                };

                JSONArray singleChoiceJa = response.optJSONArray("singleChoice");
                JSONArray multiChoiceJa = response.optJSONArray("multiChoice");
                mSingleChoiceQuestionCount = singleChoiceJa.length();
                mMultiChoiceQuestionCount = multiChoiceJa.length();
                mQuestionInfos = new ArrayList<>(mSingleChoiceQuestionCount + mMultiChoiceQuestionCount);
                OnQuestionParsedListener questionParsedListener = new OnQuestionParsedListener(mQuestionInfos);
                jsonParser.parseArray(singleChoiceJa, questionParsedListener);
                jsonParser.parseArray(multiChoiceJa, questionParsedListener);
                int i =0;
                for (QuestionInfo questionInfo: mQuestionInfos) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(TaskFragment.ARG_TASK_DATA, questionInfo);
                    bundle.putString(TaskFragment.ARG_TASK_TYPE, TaskFragment.TYPE_EXAM);
                    bundle.putString(TaskFragment.ARG_TASK_STATUS, TaskFragment.STATUS_VIEW);
                    addFragment(String.valueOf(i++), TaskFragment.class, bundle);
                }
                mTitleBar.setTitle(getString(R.string.fraction_line, 1, mQuestionInfos.size()));
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.e(TAG, "loadData error=", error);
                dismissLoadingDialog();
                UIUtils.toast(TutorialTestActivity.this, getString(R.string.get_tutorial_test_failed), Toast.LENGTH_SHORT);
            }
        }));
    }

    private void showLoadingDialog() {
        LoadingDialog loadingDialog = LoadingDialog.newInstance(R.string.loading);
        loadingDialog.show(getSupportFragmentManager(), "loadingDialog");
    }

    private void dismissLoadingDialog() {
        LoadingDialog loadingDialog = (LoadingDialog) getSupportFragmentManager().findFragmentByTag("loadingDialog");
        if (loadingDialog != null && loadingDialog.getDialog() != null && loadingDialog.getDialog().isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mViewPager.setCurrentItem(data.getIntExtra(TutorialTestNumbersActivity.EXTRA_POSITION, 0));
        }
    }

    /**
     * 调用了往界面里加Fragment和标签
     *
     * @param title  标签名字
     * @param clazz  Fragment类对象
     * @param bundle 一些数据，可在Fragment里通过getArguments获得
     */
    protected void addFragment(String title, Class<? extends Fragment> clazz, Bundle bundle) {
        mTabsAdapter.addTab(title, clazz, bundle);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) {
        mTitleBar.setTitle(getString(R.string.fraction_line, position + 1, mQuestionInfos.size()));
    }

    @Override
    public void onPageScrollStateChanged(int state) { }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestSender.stop();
        mRequestSender = null;
    }

    static class OnQuestionParsedListener implements OnParsedListener<QuestionInfo> {

        List<QuestionInfo> mQuestionInfos;

        OnQuestionParsedListener(List<QuestionInfo> questionInfos){
            mQuestionInfos = questionInfos;
        }

        @Override
        public void handleParsedObj(QuestionInfo obj) {
            mQuestionInfos.add(obj);
        }
    }

    public static void start(Activity activity, TutorialTest tutorialTest) {
        Intent intent = new Intent(activity, TutorialTestActivity.class);
        intent.putExtra(EXTRA_TUTORIAL_TEST, tutorialTest);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }
}
