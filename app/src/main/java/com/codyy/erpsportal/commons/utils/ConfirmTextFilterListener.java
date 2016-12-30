package com.codyy.erpsportal.commons.utils;

import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;

/**
 * 确认筛选
 * Created by gujiajia on 2016/12/30.
 */

public abstract class ConfirmTextFilterListener implements BaseHttpActivity.IFilterListener {

    private DrawerLayout mDrawerLayout;

    public ConfirmTextFilterListener(DrawerLayout drawerLayout) {
        this.mDrawerLayout = drawerLayout;
    }

    @Override
    public void onFilterClick(MenuItem item) {
        if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.openDrawer(Gravity.RIGHT);
        } else {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
            doFilterConfirmed();
        }
    }

    @Override
    public void onPreFilterCreate(Menu menu) {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            menu.getItem(0).setActionView(R.layout.textview_filter_confirm_button);
            TextView tv = (TextView) menu.getItem(0).getActionView().findViewById(R.id.tv_title);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                    doFilterConfirmed();
                }
            });
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_filter);
        }
    }

    protected abstract void doFilterConfirmed();
}
