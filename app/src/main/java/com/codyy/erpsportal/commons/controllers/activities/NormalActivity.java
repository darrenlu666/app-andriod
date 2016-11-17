package com.codyy.erpsportal.commons.controllers.activities;

import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;

import butterknife.Bind;

/**
 * 普通Activity
 * Created by eachann on 2015/12/28.
 */
public abstract class NormalActivity extends ToolbarActivity {
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


    @Override
    protected int getLayoutView() {
        return R.layout.activity_normal;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
    }
}
