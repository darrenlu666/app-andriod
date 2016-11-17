package com.codyy.erpsportal.groups.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.groups.controllers.fragments.GroupCollectiveLesonsFragment;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;

import butterknife.Bind;


public class GroupCollectiveLessonsActivity extends ToolbarActivity {
    public final static String EXTRA_GROUP_ID = "group_id";
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTextView;

    private String mGroupId;
    private GroupCollectiveLesonsFragment mGroupCollectiveLesonsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_group_collective_lessons;
    }

    @Override
    protected void initToolbar() {
        UiMainUtils.setNavigationTintColor(this , R.color.main_green);
        initToolbar(mToolbar);
        mTextView.setText("圈组集体备课");
        mGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        mGroupCollectiveLesonsFragment = GroupCollectiveLesonsFragment.newInstance(mGroupId);
        getSupportFragmentManager().beginTransaction().replace(R.id.group_collecive_fragment, mGroupCollectiveLesonsFragment).commit();
    }

    public static void start(Context context, String groupId) {
        Intent intent = new Intent(context, GroupCollectiveLessonsActivity.class);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }
}
