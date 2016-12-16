package com.codyy.erpsportal.classroom.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.classroom.activity.ClassRoomDetailActivity;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.classroom.models.NoAreaRecordedDetail;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 非区域的往期录播列表 碎片
 * Created by ldh on 2016/7/5.
 */
public class NoAreaRecordedListFragment extends LoadMoreFragment<NoAreaRecordedDetail.ListEntity, NoAreaRecordedListFragment.NoAreaRecordedHolder> implements TabsWithFilterActivity.OnFilterObserver {

    private String mSchoolId;
    private String mFrom;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSchoolId = getArguments().getString(ClassRoomContants.EXTRA_SCHOOL_ID);
            mFrom = getArguments().getString(ClassRoomContants.FROM_WHERE_MODEL);
        }
    }

    @Override
    protected String getUrl() {
        if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_RECORD)) {
            return URLConfig.NEW_PERSON_LIVE_RECORD_LIST;
        } else {
            return URLConfig.NEW_PERSON_SCHOOL_RECORD_LIST;
        }
    }

    @Override
    protected void addParams(Map<String, String> params) {
        params.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        if (UserInfoKeeper.obtainUserInfo().isArea()) {
            params.put("schoolId", mSchoolId);
        } else {
            params.put("schoolId", UserInfoKeeper.obtainUserInfo().getSchoolId());
        }
        if (UserInfoKeeper.obtainUserInfo().getUserType().equals(UserInfo.USER_TYPE_PARENT)) {//bug fixed: bug9675 学生账号没有数据
            params.put("studentId", UserInfoKeeper.obtainUserInfo().getSelectedChild().getStudentId());
        }
        if(UserInfoKeeper.obtainUserInfo().getUserType().equals(UserInfo.USER_TYPE_STUDENT)){
            params.put("studentId", UserInfoKeeper.obtainUserInfo().getBaseUserId());
        }
    }

    @Override
    public void onFilterConfirmed(Map<String, String> params) {
        addParam("subjectId", TextUtils.isEmpty(params.get("subjectId")) ? "" : params.get("subjectId"));
        if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_RECORD)) {
            addParam("classlevelId", TextUtils.isEmpty(params.get("classLevelId")) ? "" : params.get("classLevelId"));
        } else {
            addParam("gradeId", TextUtils.isEmpty(params.get("classLevelId")) ? "" : params.get("classLevelId"));
        }
        addParam("teacherId", TextUtils.isEmpty(params.get("teacherId")) ? "" : params.get("teacherId"));
        loadData(true);
    }

    @Override
    protected List<NoAreaRecordedDetail.ListEntity> getList(JSONObject response) {
        if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_RECORD)) {
            return NoAreaRecordedDetail.parseResponse(response);
        } else {
            return NoAreaRecordedDetail.parseSchoolResponse(response);
        }
    }

    @Override
    protected ViewHolderCreator<NoAreaRecordedHolder> newViewHolderCreator() {
        return new ViewHolderCreator<NoAreaRecordedHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_recorded_list_school;
            }

            @Override
            protected NoAreaRecordedHolder doCreate(View view) {
                return new NoAreaRecordedHolder(view, getActivity(), mFrom);
            }
        };
    }

    public static class NoAreaRecordedHolder extends RecyclerViewHolder<NoAreaRecordedDetail.ListEntity> {
        private RelativeLayout mContainer;
        private SimpleDraweeView mImgSdv;
        private TextView mTeacherTv;
        private TextView mGradeSubjectTv;
        private TextView mSchoolNameTv;
        private TextView mPlayCountTv;
        private TextView mTimeTv;
        private Context mContext;
        private String mFrom;

        NoAreaRecordedHolder(View itemView, Context context, String from) {
            super(itemView);
            mContext = context;
            mFrom = from;
        }

        @Override
        public void mapFromView(View view) {
            mContainer = (RelativeLayout) view.findViewById(R.id.rl_item);
            mImgSdv = (SimpleDraweeView) view.findViewById(R.id.sd_record_list_pic);
            mTeacherTv = (TextView) view.findViewById(R.id.tv_teacher);
            mGradeSubjectTv = (TextView) view.findViewById(R.id.tv_grade_subject);
            mSchoolNameTv = (TextView) view.findViewById(R.id.tv_school_name);
            mPlayCountTv = (TextView) view.findViewById(R.id.tv_play_count);
            mTimeTv = (TextView) view.findViewById(R.id.tv_time);
        }

        @Override
        public void setDataToView(final NoAreaRecordedDetail.ListEntity data) {
            super.setDataToView(data);
            ImageFetcher.getInstance(mContext).fetchSmall(mImgSdv, data.getThumbnailUrl());
            mTeacherTv.setText(data.getMainTeacher());
            mGradeSubjectTv.setText(data.getGrade() + "·" + data.getSubject());
            mSchoolNameTv.setText(data.getMainSchoolName());
            mPlayCountTv.setText(data.getPlayCount());
            mTimeTv.setText(DateUtil.getDateStr(data.getStartTime(), DateUtil.DEF_FORMAT));
            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.getVideoDeleteFlag().equals("N")) {
                        ClassRoomDetailActivity.startActivity(mContext, data.getScheduleDetailId(), mFrom);
                    } else {
                        ToastUtil.showToast(mContext, "资源被删除！");
                    }
                }
            });
        }
    }
}
