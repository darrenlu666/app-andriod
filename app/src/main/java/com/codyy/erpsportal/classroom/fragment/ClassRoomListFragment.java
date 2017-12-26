package com.codyy.erpsportal.classroom.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.activity.CustomLiveDetailActivity;
import com.codyy.erpsportal.classroom.utils.DMSUtils;
import com.codyy.erpsportal.commons.controllers.fragments.SimpleRequestDelegate;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DeviceUtils;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.classroom.activity.ClassRoomDetailActivity;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.classroom.models.ClassRoomInfoEntity;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsVhrCreator;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专递课堂 直录播课堂 列表碎片
 * Created by ldh on 2016/6/29.
 */
public class ClassRoomListFragment extends LoadMoreFragment<ClassRoomInfoEntity.ListEntity, ClassRoomListFragment.ClassRoomViewHolder> {
    private final String TAG = "ClassRoomListFragment";
    private String mFrom;
    private UserInfo mUserInfo;

    public static ClassRoomListFragment newInstance(String type,UserInfo userInfo) {
        Bundle args = new Bundle();
        args.putString(ClassRoomContants.FROM_WHERE_MODEL, type);
        args.putParcelable(Constants.USER_INFO,userInfo);
        ClassRoomListFragment fragment = new ClassRoomListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFrom = getArguments().getString(ClassRoomContants.FROM_WHERE_MODEL);
        mUserInfo = getArguments().getParcelable(Constants.USER_INFO);
        if(null == mUserInfo) mUserInfo = UserInfoKeeper.obtainUserInfo();
    }

    @Override
    protected String getUrl() {
        if (ClassRoomContants.TYPE_CUSTOM_LIVE.equals(mFrom)) {
            return URLConfig.GET_ONLINE_LIVE_CLASS_LIST;
        } else {
            return URLConfig.GET_SCHOOL_LIVE_CLASS_LIST;
        }
    }

    @Override
    protected void addParams(Map<String, String> params) {
        params.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        if (UserInfoKeeper.obtainUserInfo().getUserType().equals(UserInfo.USER_TYPE_PARENT)) {
            params.put(ClassRoomContants.TYPE_CUSTOM_LIVE.equals(mFrom) ? "studentUserId" : "studentId",
                    UserInfoKeeper.obtainUserInfo().getSelectedChild().getStudentId());
        }
    }

    @Override
    protected AbsVhrCreator<ClassRoomViewHolder> newViewHolderCreator() {
        return new AbsVhrCreator<ClassRoomViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_class_room;
            }

            @Override
            protected ClassRoomViewHolder doCreate(View view) {
                return new ClassRoomViewHolder(view);
            }
        };
    }

    @Override
    protected List<ClassRoomInfoEntity.ListEntity> getList(JSONObject response) {
        if (mTodayClassCountListener != null) {
            mTodayClassCountListener.getTodayClassCount((ClassRoomInfoEntity.parseResponse(response)).getClassCount());
        }
        return (ClassRoomInfoEntity.parseResponse(response)).getList();
    }

    public interface TodayClassCountListener {
        void getTodayClassCount(int count);
    }

    public TodayClassCountListener mTodayClassCountListener;

    public void setTodayClassCountListener(TodayClassCountListener listener) {
        mTodayClassCountListener = listener;
    }

    public class ClassRoomViewHolder extends RecyclerViewHolder<ClassRoomInfoEntity.ListEntity> {

        private TextView mStartTimeTv;
        private TextView mSchoolNameTv;
        private TextView mClassDetailTv;
        private TextView mStatusIv;
        private TextView mAreaTv;//行政区.
        private RelativeLayout mContainer;

        @Override
        public void setDataToView(final ClassRoomInfoEntity.ListEntity data) {
            super.setDataToView(data);
            String[] numArr = getResources().getStringArray(R.array.numbers);
            if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_LIVE)) {
                if ("INIT".equals(data.getStatus())) {
                    mStartTimeTv.setText(DateUtil.formatTime2(data.getSchoolTime()));
                } else {
                    mStartTimeTv.setText(DateUtil.formatTime2(data.getRealBeginTime()));
                }
            } else {
                mStartTimeTv.setText(numArr[Integer.valueOf(data.getClassPeriod())]);
            }
            if (UserInfoKeeper.obtainUserInfo().isArea()) {
                mAreaTv.setVisibility(View.VISIBLE);
                mClassDetailTv.setText(data.getGrade() + "/" + data.getSubject() + "/" + data.getTeacherName());

                StringBuffer mainStr = new StringBuffer(data.getMainSchoolName());
                if(data.isShowClassRoomName()
                        &&!TextUtils.isEmpty(data.getRoomName())){
                    if(!TextUtils.isEmpty(mainStr))mainStr.append("_");
                    mainStr.append(data.getRoomName());
                }
                mSchoolNameTv.setText(mainStr.toString());
                mAreaTv.setText(data.getAreaPath());
            } else {
                mClassDetailTv.setText(data.getGrade() + "/" + data.getSubject());
                mSchoolNameTv.setText(data.getTeacherName());
            }
            if ("INIT".equals(data.getStatus())) {
                mStatusIv.setText("未开始");
                mStatusIv.setBackgroundResource(R.drawable.status_un_start);
            } else if ("PROGRESS".equals(data.getStatus())) {
                mStatusIv.setText("正在直播");
                mStatusIv.setBackgroundResource(R.drawable.status_starting);
            } else {
                mStatusIv.setText("已结束");
                mStatusIv.setBackgroundResource(R.drawable.status_un_start);
            }
            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 观看人数加1
//                    DMSUtils.enterLiving(mRequestSender,data.getScheduleDetailId(),mUserInfo.getUuid());
                    if (ClassRoomContants.TYPE_CUSTOM_LIVE.equals(mFrom)) {//专递-直播
                        CustomLiveDetailActivity.startActivity(getContext(), mUserInfo,data.getScheduleDetailId(), mFrom, data.getStatus(),data.getSubject());
                    }else{//名校直播
                        ClassRoomDetailActivity.startActivity(getContext(), mUserInfo,data.getScheduleDetailId(), mFrom, data.getStatus(),data.getSubject());
                    }
                }
            });
        }

        @Override
        public void mapFromView(View view) {
            mContainer = (RelativeLayout) view.findViewById(R.id.rl_item_class);
            mStartTimeTv = (TextView) view.findViewById(R.id.tv_time);
            mSchoolNameTv = (TextView) view.findViewById(R.id.tv_school_name);
            mClassDetailTv = (TextView) view.findViewById(R.id.tv_class_detail);
            mStatusIv = (TextView) view.findViewById(R.id.tv_status);
            mAreaTv = (TextView) view.findViewById(R.id.tv_area);
        }

        ClassRoomViewHolder(View itemView) {
            super(itemView);
        }
    }
}
