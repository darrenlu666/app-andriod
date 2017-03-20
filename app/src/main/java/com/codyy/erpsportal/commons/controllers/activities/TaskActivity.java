package com.codyy.erpsportal.commons.controllers.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.TabsAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * 测试、作业模块题目详情滑动
 * Created by eachann on 2015/12/28.
 */
public abstract class TaskActivity extends ToolbarActivity {
    private static final String TAG = TaskActivity.class.getSimpleName();
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
    @Bind(R.id.toolbar_title)
    protected TextView mTitle;
    @Bind(R.id.pager)
    protected ViewPager mPager;
    protected TabsAdapter mTabsAdapter;
    private RequestSender mRequestSender;
    private LoadingDialog mLoadingDialog;
    /**
     * 参数
     */
    private Map<String, String> mParams;
    private Context mContext = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParams = new HashMap<>();
        addParams(mParams);
        mRequestSender = new RequestSender(this);
        mPager.setPadding(0, 0, 0, UIUtils.dip2px(this, 48));
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
        mLoadingDialog = LoadingDialog.newInstance(true);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.show(getSupportFragmentManager(), "loading data");
        mRequestSender.sendRequest(new RequestSender.RequestData(getUrl(), mParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mToolbar != null) {
                    mTabsAdapter = new TabsAdapter((FragmentActivity) mContext, getSupportFragmentManager(), mPager);
                    addFragments(response);
                    mPager.setAdapter(mTabsAdapter);
                    mLoadingDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                if (mToolbar != null) {
                    mLoadingDialog.dismiss();
                    finish();
                    ToastUtil.showToast(getApplicationContext(), getString(R.string.refresh_state_loade_error));
                }
            }
        }));
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_task;
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
