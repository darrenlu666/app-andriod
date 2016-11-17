package com.codyy.erpsportal.commons.controllers.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.TabsAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.homework.widgets.MyViewPager;
import com.codyy.erpsportal.homework.widgets.SlidingFloatScrollView;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * 测试按习题批阅
 * Created by eachann on 2015/12/28.
 */
public abstract class TeacherReadByTopicActivity extends ToolbarActivity implements SlidingFloatScrollView.OnScrollListener{
    private static final String TAG = TeacherReadByTopicActivity.class.getSimpleName();
    /**
     * 标题栏
     */
    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    /**
     * 根布局
     */
    @Bind(R.id.rl_root_view)
    protected RelativeLayout mRelativeLayout;
    /**
     * 标题
     */
    @Bind(R.id.ll_answer_webview)
    protected LinearLayout mAnswerLinearLayout;
    @Bind(R.id.toolbar_title)
    protected TextView mTitle;
    @Bind(R.id.tv_wait_to_read_by)
    protected TextView mTvWaitToReadBy;//待批阅题数
    @Bind(R.id.tv_people_wait)
    protected TextView mTvPeopleWait;//待批阅人数
    @Bind(R.id.tv_max_score)
    protected TextView mTvMaxScore;//提示得分
    @Bind(R.id.et_score)
    protected EditText mEtScore;//得分
    @Bind(R.id.et_comment)
    protected EditText mEtComment;//老师点评
    @Bind(R.id.btn_submit)
    protected Button mSubmit;//提交答案
    @Bind(R.id.recycler_view)
    protected RecyclerView mRvStuHead;
    @Bind(R.id.pager)
    protected MyViewPager mPager;
/*    @Bind(R.id.scroll_view_read_by_topic)
    protected ScrollView mScrollView;*/
    protected TabsAdapter mTabsAdapter;
    @Bind(R.id.ll_score)
    protected LinearLayout mLlScore;
    @Bind(R.id.tv_comment)
    protected TextView mTvComment;
    @Bind(R.id.rl_submit)
    protected RelativeLayout mRlSubmit;
    //@Bind(R.id.iv_audio_bar_answer)
    //protected ImageView mIvAudioBarAnswer;
    //@Bind(R.id.container_audio_answer)
    //protected LinearLayout mContainerAudioAnswer;
    //@Bind(R.id.tv_audio_time_answer)
    //protected TextView mTvAudioTimeAnswer;
    @Bind(R.id.layout_audio_answer)
    protected LinearLayout mLayoutAudioAnswer;
    @Bind(R.id.fl_video_view)
    protected FrameLayout mFlVideoView;
    @Bind(R.id.sd_video_task_view)
    protected SimpleDraweeView sdVideoTaskView;
    @Bind(R.id.iv_controller)
    protected ImageView ivController;
    private RequestSender mRequestSender;
    private LoadingDialog mLoadingDialog;
    /**
     * 参数
     */
    private Map<String, String> mParams;
    private Context mContext = this;

    /**
     * 自定义的滑动ScrollView，监听滑动距离大于一定数值时，隐藏题目信息
     */
    protected SlidingFloatScrollView mStuAnswerInfoSfsv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParams = new HashMap<>();
        addParams(mParams);
        mRequestSender = new RequestSender(this);
        onViewBound();
    }

    /**
     * 设置自定义标题名
     *
     * @param stringId 标题名的字串资源id
     */
    protected void setCustomTitle(@StringRes int stringId) {
        mTitle.setText(stringId);
    }

    /**
     * 设置自定义标题名
     *
     * @param title 标题名
     */
    protected void setCustomTitle(CharSequence title) {
        mTitle.setText(title);
    }

    /**
     * 调用addFragment方法，把Fragment加入
     */
    protected abstract void addFragments(JSONObject response);

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

    /**
     * butterknife绑定后将调用这个方法，
     * 初始化标题栏setCustomTitle在这里调用为妙，
     * 以及做些其它数据初始化也不错呀
     */
    protected void onViewBound() {
        mLoadingDialog = LoadingDialog.newInstance();
        mLoadingDialog.show(getSupportFragmentManager(), "loading data");
        mRequestSender.sendRequest(new RequestSender.RequestData(getUrl(), mParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mTabsAdapter = new TabsAdapter((FragmentActivity) mContext, getSupportFragmentManager(), mPager);
                addFragments(response);
                mPager.setAdapter(mTabsAdapter);
                mLoadingDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mLoadingDialog.dismiss();
                finish();
                ToastUtil.showToast(getApplicationContext(), getString(R.string.refresh_state_loade_error));
            }
        }));
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_teacher_read_by_topic;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);

    }

    /**
     * 获取请求地址
     *
     * @return
     */
    protected abstract String getUrl();

    /**
     * 添加请求参数
     *
     * @param params
     */
    protected void addParams(Map<String, String> params) {
    }

    protected void addParam(String key, String value) {
        mParams.put(key, value);
    }
}
