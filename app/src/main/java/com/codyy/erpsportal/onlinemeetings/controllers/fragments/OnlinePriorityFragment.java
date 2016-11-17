package com.codyy.erpsportal.onlinemeetings.controllers.fragments;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

import butterknife.Bind;

/**
 * 申请发言{占位，暂时未加载}
 * @author poe
 */
public class OnlinePriorityFragment extends OnlineFragmentBase {
    private static final String TAG = OnlinePriorityFragment.class.getSimpleName();
    @Bind(R.id.toolbar)Toolbar mToolbar;

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_online_priority;
    }

    private UserInfo getUserInfo() {
        return ((MainActivity) getActivity()).getUserInfo();
    }


    //set the application datas
    @Override
    public void viewLoadCompleted() {
        mToolbar.setNavigationIcon(R.drawable.ic_test_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }
}
