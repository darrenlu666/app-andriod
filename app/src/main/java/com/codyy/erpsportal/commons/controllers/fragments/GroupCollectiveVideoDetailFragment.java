package com.codyy.erpsportal.commons.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsDetailEntity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kmdai on 16-4-15.
 */
public class GroupCollectiveVideoDetailFragment extends Fragment {
    @Bind(R.id.tv_active_name)
    TextView mTitleTV;
    @Bind(R.id.tv_active_start_time)
    TextView mStartTimeTV;
    @Bind(R.id.group_collective_et_textview)
    TextView mETTimeTV;
    @Bind(R.id.group_collective_collective_cont)
    TextView mContentTV;
    @Bind(R.id.group_collective_main_people)
    TextView mMainName;
    @Bind(R.id.group_collective_collective_join)
    TextView mTeachersTV;
    private PrepareLessonsDetailEntity mPrepareLessonsDetailEntity;

    public static GroupCollectiveVideoDetailFragment newInstance(PrepareLessonsDetailEntity entity) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.PREPARE_DATA, entity);
        GroupCollectiveVideoDetailFragment fragment = new GroupCollectiveVideoDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrepareLessonsDetailEntity = getArguments().getParcelable(Constants.PREPARE_DATA);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collective_video_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setData();
    }

    private void setData() {
        if (mPrepareLessonsDetailEntity != null) {
            mTitleTV.setText(mPrepareLessonsDetailEntity.getData().getTitle().equals("null") ? "暂无主题" : mPrepareLessonsDetailEntity.getData().getTitle());
            mMainName.setText("主备人:" + mPrepareLessonsDetailEntity.getData().getSponsorName());
            String startDate = mPrepareLessonsDetailEntity.getData().getStartDate() != null && !mPrepareLessonsDetailEntity.getData().getStartDate().equals("null") ? mPrepareLessonsDetailEntity.getData().getStartDate() : "无";
            String endDate = mPrepareLessonsDetailEntity.getData().getFinishDate() != null && !mPrepareLessonsDetailEntity.getData().getFinishDate().equals("null") ? mPrepareLessonsDetailEntity.getData().getFinishDate() : "无";
            mStartTimeTV.setText(startDate + " 至 " + endDate);
            mETTimeTV.setText("预计时长:" + getData(mPrepareLessonsDetailEntity.getData().getDuration()));
            mContentTV.setText(mPrepareLessonsDetailEntity.getData().getDescription().equals("nill") ? "暂无备课内容!" : mPrepareLessonsDetailEntity.getData().getDescription());
            String name = "";
            for (PrepareLessonsDetailEntity.MeetMember meetMember : mPrepareLessonsDetailEntity.getMeetMembers()) {
                name += meetMember.getRealName() + "，";
            }
            mTeachersTV.setText(name);
        }
    }

    private String getData(long date) {
        if (date <= 0) {
            return "未知";
        }
        long h = date / 60;
        long m = date % 60;
        if (h > 0) {
            return h + "小时" + m + "分钟";
        }
        return m + "分钟";
    }
}
