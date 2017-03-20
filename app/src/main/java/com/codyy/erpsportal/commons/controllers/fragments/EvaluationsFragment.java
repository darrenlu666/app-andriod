package com.codyy.erpsportal.commons.controllers.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.AssessmentClassActivity;
import com.codyy.erpsportal.commons.controllers.activities.AssessmentDetailsActivity;
import com.codyy.erpsportal.commons.controllers.adapters.AssessmentAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.AreaBase;
import com.codyy.erpsportal.commons.models.entities.Assessment;
import com.codyy.erpsportal.commons.models.entities.AssessmentDetails;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评课列表
 * Created by kmdai on 2015/4/20.
 */
public class EvaluationsFragment extends BaseRefreshFragment<Assessment> implements AssessmentAdapter.onItemClick {
    private static final String TAG = "EvaluationsFragment";
    private final static int GET_ASSESSMENT_LIST = 0x001;
    /**
     * 获取评课详情
     */
    private final static int GET_ASSESSMENT_DETAIL = 0x002;
    /**
     * 刷新完成
     */
    private final static int REFRESH_COMPLETE = 0x003;
    /**
     * 返回刷新
     */
    public static final int LIST_REFRESH = 0x001;
    /**
     * 网络请求
     */
    private RequestSender mSender;
    private UserInfo userInfo;
    private int type;
    /**
     * 状态（开始、结束等）
     */
    private FilterFragment.Status status = null;
    private DialogUtil dialogUtil;
    /**
     * 每次加载的数量
     */
    private final int count = 10;
    /**
     * 开始的位置
     */
    private int mStart = 0;
    /**
     * 结束的位置
     */
    private int mEnd = mStart + count;
    private AreaBase mAreaBase = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mSender = new RequestSender(context);
        userInfo = UserInfoKeeper.getInstance().getUserInfo();
        type = getArguments().getInt("class_type");
        switch (type) {
            case AssessmentClassActivity.SPONSOR://发起的评课
                setURL(URLConfig.GET_SPONSOR_EVALUATION);
                break;
            case AssessmentClassActivity.AREA://管辖范围的评课
                setURL(URLConfig.GET_AREA_EVALUALUATIONS);
                break;
            case AssessmentClassActivity.INVITED://受邀的的评课
                setURL(URLConfig.GET_INVITED_EVALUALUATIONS);
                break;
            case AssessmentClassActivity.SCHOOLTEACHER://本校教师的评课
                setURL(URLConfig.GET_SCHOOLTEACHER_EVALUALUATIONS);
                break;
            case AssessmentClassActivity.SCHOOLMASTER://本校主讲的
                setURL(URLConfig.GET_SCHOOL_MASTER_EVALUALUATION);
                break;
            case AssessmentClassActivity.MASTER://主讲的评课
                setURL(URLConfig.GET_MASTER_EVALUALUATIONS);
                break;
            case AssessmentClassActivity.ATTEND://参与的评课
                setURL(URLConfig.GET_ATTEND_EVALUALUATIONS);
                break;
        }
        setLastVisibleNB(count);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogUtil = new DialogUtil(getActivity());
    }

    @Override
    public void loadData() {
        if (mDatas == null || mDatas.size() <= 0) {
            mRefreshRecycleView.setRefreshing(true);
            httpConnect(getURL(), getParam(STATE_ON_DOWN_REFRESH), STATE_ON_DOWN_REFRESH);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public RefreshBaseAdapter<Assessment> getAdapter(List<Assessment> data) {
        AssessmentAdapter assessmentAdapter = new AssessmentAdapter(getContext(), data, type);
        assessmentAdapter.setOnItemClick(this);
        return assessmentAdapter;
    }

    @NonNull
    @Override
    public Map<String, String> getParam(int state) {
        Map<String, String> data = new HashMap<>();
        data.put("uuid", userInfo.getUuid());
        switch (state) {
            case STATE_ON_UP_REFRESH:
                mStart = mDatas.size();
                mEnd = mDatas.size() + count;
                break;
            case STATE_ON_DOWN_REFRESH:
                mStart = 0;
                mEnd = mStart + count;
                break;
        }
        switch (type) {
            case AssessmentClassActivity.AREA://管辖范围的评课
                if (mAreaBase != null) {
                    if ("area".equals(mAreaBase.getType())) {
                        data.put("baseAreaId", mAreaBase.getAreaId());
                    } else {
                        data.put("schoolId", mAreaBase.getSchoolID());
                    }
                }
                break;
        }
        if (status != null && status.getId() != null) {
            data.put("status", status.getId());
        }
        data.put("start", String.valueOf(mStart));
        data.put("end", String.valueOf(mEnd));
        return data;
    }

    @NonNull
    @Override
    public List<Assessment> getDataOnJSON(JSONObject object) {
        List<Assessment> assessments1 = new ArrayList<>();
        Assessment.getAssess(object, assessments1);
        return assessments1;
    }

    @Override
    protected boolean onRequestSuccess(JSONObject object, int msg) {
        switch (msg) {
            case GET_ASSESSMENT_DETAIL:
                dialogUtil.cancel();
                AssessmentDetails assessmentDetails = new AssessmentDetails();
                AssessmentDetails.getAssessmentDetail(object, assessmentDetails);
                Intent intent = new Intent(getActivity(), AssessmentDetailsActivity.class);
                intent.putExtra("assessmentDetails", assessmentDetails);
                intent.putExtra("type", type);
                intent.putExtra("userInfo", userInfo);
                startActivityForResult(intent, LIST_REFRESH);
                getActivity().overridePendingTransition(R.anim.slidemenu_show, R.anim.layout_hide);
                return true;
        }
        return super.onRequestSuccess(object, msg);
    }

    @Override
    protected void onRequestError(Throwable error, int msg) {
        super.onRequestError(error, msg);
        if (dialogUtil != null) {
            dialogUtil.cancel();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == LIST_REFRESH) {
            mStart = 0;
            mEnd = mStart + count;
            onRefresh();
        }
    }


    @Override
    public void onRefresh() {
        super.onRefresh();
    }


    @Override
    protected boolean hasData() {
        if (mDatas.size() < mEnd) {
            return false;
        }
        return true;
    }

    /**
     * 设置筛选状态
     *
     * @param status
     */
    public void setStatus(FilterFragment.Status status, AreaBase areaBase) {
        this.status = status;
        this.mAreaBase = areaBase;
        mStart = 0;
        mEnd = mStart + count;
        onRefresh();
    }

    public int getType() {
        return this.type;
    }

    @Override
    public void onDestroy() {
        mSender.stop(this.toString());
        super.onDestroy();
    }

    @Override
    public void onItemClick(Assessment assessment) {
        dialogUtil.showDialog();
        Map<String, String> data = new HashMap<>();
        data.put("uuid", userInfo.getUuid());
        if (type == AssessmentClassActivity.INVITED) {
            data.put("isInvited", "Y");
        }
        data.put("evaluationId", assessment.getEvaluationId());
        httpConnect(URLConfig.GET_EVALUATION_DETAIL, data, GET_ASSESSMENT_DETAIL);
    }
}
