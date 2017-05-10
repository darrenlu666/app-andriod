package com.codyy.erpsportal.groups.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;


import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.groups.models.entities.Group;
import com.codyy.erpsportal.groups.models.entities.GroupSpace;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

import butterknife.Bind;

/**
 * 圈组空间详情
 * Created by poe on 16-1-18.
 */
public class GroupSpaceDetailActivity extends BaseHttpActivity implements Serializable {
    private final static String TAG = "GroupSpaceDetailActivity";
    public final static String EXTRA_DATA = "com.group.data";//
    protected GroupSpace mGroupSpace ;
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.tv_detail_title)TextView mGroupTitleTv;
    @Bind(R.id.tv_detail_creator)TextView mCreatorTv;
    @Bind(R.id.tv_detail_create_time)TextView mCreateTimeTv;
    @Bind(R.id.tv_team)TextView mTeamTv;
    @Bind(R.id.tv_subject_or_category_desc)TextView mSubjectDescTv;
    @Bind(R.id.tv_subject)TextView mSubjectTv;
    @Bind(R.id.tv_school)TextView mSchoolTv;
    @Bind(R.id.tv_group_desc)TextView mDescTv;

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_group_space_detail;
    }

    @Override
    public String obtainAPI() {
        return null;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        return null;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG , response.toString());
    }

    @Override
    public void onFailure(Throwable error) {

    }

    public void init() {
        mGroupSpace = (GroupSpace) getIntent().getSerializableExtra(EXTRA_DATA);
        initToolbar(mToolBar);
        mTitleTextView.setText(getString(R.string.group_detail));
        if(null == mGroupSpace) return;
        mGroupTitleTv.setText(UIUtils.filterNull(mGroupSpace.getGroupName()));
        mCreatorTv.setText(UIUtils.filterNull(mGroupSpace.getGroupCreator()));
        mCreateTimeTv.setText(DateUtil.getDateStr(Long.valueOf(mGroupSpace.getCreateTime()),DateUtil.DEF_FORMAT));

        String teamName = "教研组";
        if(Group.TYPE_INTEREST.equals(mGroupSpace.getGroupType())){
            teamName = "兴趣组";
        }
        mTeamTv.setText(UIUtils.filterNull(teamName));

        //小学一年级语文
        switch (mGroupSpace.getGroupType()){
            case Group.TYPE_INTEREST:
                mSubjectDescTv.setText("分类");
                if(!TextUtils.isEmpty(mGroupSpace.getCategoryName()))mSubjectTv.append(mGroupSpace.getCategoryName());
                break;
            case Group.TYPE_TEACHING:
                mSubjectDescTv.setText("学科");
                StringBuilder tvSubject = new StringBuilder();
                if(!TextUtils.isEmpty(mGroupSpace.getSemesterName()))tvSubject.append(mGroupSpace.getSemesterName());
                if(!TextUtils.isEmpty(mGroupSpace.getGrade()))tvSubject.append(mGroupSpace.getGrade());
                if(!TextUtils.isEmpty(mGroupSpace.getSubjectName()))tvSubject.append(mGroupSpace.getSubjectName());
                mSubjectTv.setText(UIUtils.filterNull(tvSubject.toString()));
                break;
        }

        //学校 xxx省-xxx市-xxx县-xxx校
        StringBuilder tvSB = new StringBuilder();
        if(!TextUtils.isEmpty(mGroupSpace.getAreaPath())) tvSB.append(mGroupSpace.getAreaPath());
        if(!TextUtils.isEmpty(mGroupSpace.getSchoolName())){
            tvSB.append("-");
            tvSB.append(mGroupSpace.getSchoolName());
        }
        mSchoolTv.setText(UIUtils.filterNull(tvSB.toString()));
        mDescTv.setText(UIUtils.filterNull(mGroupSpace.getGroupDesc()));
        UiMainUtils.setNavigationTintColor(this,R.color.main_green);
    }

    public static void start(Context context , GroupSpace groupSpace){
        Intent intent = new Intent(context , GroupSpaceDetailActivity.class);
        intent.putExtra(EXTRA_DATA , groupSpace);
        context.startActivity(intent);
        UIUtils.addEnterAnim((Activity) context);
    }
}
