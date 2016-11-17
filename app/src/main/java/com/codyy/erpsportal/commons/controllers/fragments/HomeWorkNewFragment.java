package com.codyy.erpsportal.commons.controllers.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.activities.HomeWorkDetailActivity;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.homenews.FamousClassBean;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 课堂作业碎片
 * Created by ldh on 2016/7/27.
 */
public class HomeWorkNewFragment extends LoadMoreFragment<FamousClassBean, HomeWorkNewFragment.HomeWorkViewHolder> implements TabsWithFilterActivity.OnFilterObserver {

    private UserInfo mUserInfo;
    private String mClassType;//课程类型（专递课堂 直录播课堂）

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClassType = getArguments().getString(ClassRoomContants.FROM_WHERE_MODEL);
    }


    @Override
    protected ViewHolderCreator<HomeWorkViewHolder> newViewHolderCreator() {
        return new ViewHolderCreator<HomeWorkViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_task;
            }

            @Override
            protected HomeWorkViewHolder doCreate(View view) {
                return new HomeWorkViewHolder(view, getActivity());
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.NEW_GET_HOMEWORK_CLASS_LIST;
    }

    @Override
    protected void addParams(Map<String, String> params) {
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        if (mUserInfo != null) {
            params.put("uuid", mUserInfo.getUuid());
            params.put("type", mClassType);
            if ("SCHOOL_USR".endsWith(mUserInfo.getUserType())) {
                params.put("userType", "SCHOOL");
            } else if ("TEACHER".endsWith(mUserInfo.getUserType())) {
                params.put("userType", "TEACHER");
            }
        }
    }

    @Override
    public void onFilterConfirmed(Map<String, String> params) {
        addParam("baseClasslevelId", TextUtils.isEmpty(params.get("classLevelId")) ? "" : params.get("classLevelId"));
        addParam("baseSubjectId", TextUtils.isEmpty(params.get("subjectId")) ? "" : params.get("subjectId"));
        loadData(true);
    }

    @Override
    protected List<FamousClassBean> getList(JSONObject response) {
        return FamousClassBean.parseResponse(response);
    }

    public class HomeWorkViewHolder extends RecyclerViewHolder<FamousClassBean> {

        SimpleDraweeView classIcon;
        TextView schoolName;
        TextView classType;
        TextView teacherName;
        TextView taskDate;
        TextView mTvWorkCount;
        RelativeLayout mRlItemView;
        Context mContext;

        public HomeWorkViewHolder(View itemView, Context context) {
            super(itemView);
            mContext = context;
        }

        @Override
        public void mapFromView(View view) {
            mRlItemView = (RelativeLayout) view.findViewById(R.id.rl_item_homework);
            classIcon = (SimpleDraweeView) view.findViewById(R.id.class_icon);
            schoolName = (TextView) view.findViewById(R.id.school_name);
            classType = (TextView) view.findViewById(R.id.class_type);
            teacherName = (TextView) view.findViewById(R.id.teacher_name);
            taskDate = (TextView) view.findViewById(R.id.task_date);
            mTvWorkCount = (TextView) view.findViewById(R.id.tv_work_count);
        }

        @Override
        public void setDataToView(final FamousClassBean data) {
            schoolName.setText(data.getSchoolName());
            classType.setText(data.getGrade() + "·" + data.getSubject());
            teacherName.setText(data.getTeacherName());
            taskDate.setText(data.getDate());
            ImageFetcher.getInstance(mContext).fetchSmall(classIcon, data.getSubjectPic());
            mTvWorkCount.setText(data.getWorkCount() + "份");
            mRlItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, HomeWorkDetailActivity.class);
                    intent.putExtra("data", data);
                    intent.putExtra("classType", mClassType);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
