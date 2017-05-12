package com.codyy.erpsportal.commons.controllers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.groups.controllers.fragments.SimpleRecyclerFragment;
import com.codyy.tpmp.filterlibrary.interfaces.SimpleRecyclerDelegate;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.CollectivePrepareLessonsDetailActivity;
import com.codyy.erpsportal.commons.controllers.activities.ListenDetailsActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.PrePareLessonsViewHolder;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.models.entities.AreaBase;
import com.codyy.erpsportal.commons.models.entities.PreparationEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;

/**
 * 集体备课、互动听课列表fragment
 * Created by yangxinwu on 2015/7/27.
 */
public class CollectivePrepareLessonsFragment extends SimpleRecyclerFragment<PreparationEntity> {
    private String TAG = "CollectivePrepareLessonsFragment";
    public static final String PREPARE_LESSONS_TYPE = "prepare_lessons_type";
    public static final int REQUEST_COLLECTIVE_PREPARE_OUT = 0x001;
    /**
     * 发起的
     */
    public static final int TYPE_LAUNCH = 0x01;
    /**
     * 参与的
     */
    public static final int TYPE_JOIN = 0x02;
    /**
     * 管理的
     */
    public static final int TYPE_MANAGE = 0x03;
    private String mGradeId = null;
    private String mSubjectId = null;
    private String mStatus = null;
    private String mAreaId = null;
    private String mClsSchoolId = null;
    private String mType;
    private int mCurType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null != getArguments()){
            mCurType = getArguments().getInt(PREPARE_LESSONS_TYPE);
            mType = getArguments().getString(Constants.TYPE_LESSON); //判断是集体备课还是互动听课
        }
    }

    @Override
    public void onViewLoadCompleted() {
        super.onViewLoadCompleted();
        initData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_COLLECTIVE_PREPARE_OUT && resultCode == 1) {
            requestData(true);
            TipProgressFragment fragment = TipProgressFragment.newInstance(TipProgressFragment.OUT_STATUS_TIP);
            fragment.show(getFragmentManager(), "showtips");
        }
    }

    /**
     * 获取发起、参与、管理的备课、听课的URL
     *
     * @param type
     * @param mType
     * @return
     */
    private String getURL(int type, String mType) {
        String url = "";
        switch (type) {
            case TYPE_LAUNCH:
                if (mType.equals(Constants.TYPE_PREPARE_LESSON)) {
                    url = URLConfig.GET_SPONSOR_PREPARATION;
                } else {
                    url = URLConfig.GET_SPONSOR_LECTURE;
                }
                break;
            case TYPE_JOIN:
                if (mType.equals(Constants.TYPE_PREPARE_LESSON)) {
                    url = URLConfig.GET_PARTICIPANT_PREPARATION;
                } else {
                    url = URLConfig.GET_PARTICIPANT_LECTURE;
                }
                break;
            case TYPE_MANAGE:
                if (mType.equals(Constants.TYPE_PREPARE_LESSON)) {
                    url = URLConfig.GET_AREA_PREPARATION;
                } else {
                    url = URLConfig.GET_AREA_LECTURE;
                }
                break;
            default:
                break;
        }
        return url;
    }

    /**
     * 只有年级、学科、状态的筛选
     *
     * @param gradeId
     * @param subjectId
     */
    public void execSearch(String gradeId, String subjectId, String status) {
        mGradeId = gradeId;
        mSubjectId = subjectId;
        mStatus = status;
        initData();
    }

    /**
     * 包含省、年级、学科、状态的筛选
     *
     * @param gradeId
     * @param subjectId
     */
    public void execAreaSearch(String gradeId, String subjectId, String status, AreaBase areaBase) {
        mGradeId = gradeId;
        mSubjectId = subjectId;
        mStatus = status;
        mClsSchoolId = "";
        mAreaId = "";
        if ("area".equals(areaBase.getType())) {
            mAreaId = areaBase.getAreaId();
        } else if ("school".equals(areaBase.getType())) {
            mClsSchoolId = areaBase.getSchoolID();
        }
        initData();
    }

    @Override
    public SimpleRecyclerDelegate<PreparationEntity> getSimpleRecyclerDelegate() {
        return new SimpleRecyclerDelegate<PreparationEntity>() {
            @Override
            public String obtainAPI() {
                return getURL(mCurType, mType);
            }

            @Override
            public HashMap<String, String> getParams(boolean isRefreshing) {
                HashMap<String, String> params = new HashMap<>();
                params.put("uuid", mUserInfo.getUuid());
                if (mGradeId != null) params.put("classlevelId", "" + mGradeId);
                if (mSubjectId != null) params.put("subjectId", "" + mSubjectId);
                if (mStatus != null) params.put("status", "" + mStatus);
                if (mAreaId != null) params.put("baseAreaId", "" + mAreaId);
                if (mClsSchoolId != null) params.put("clsSchoolId", "" + mClsSchoolId);
                params.put("start", "" + mDataList.size());
                params.put("end", "" + (mDataList.size()+sPageCount-1));
                return params;
            }

            @Override
            public void parseData(JSONObject response,boolean isRefreshing) {
                if ("success".equals(response.optString("result"))) {
                    mTotal = response.optInt("total");
                    JSONArray jsonArray = null;
                    if (mType.equals(Constants.TYPE_PREPARE_LESSON)) {
                        jsonArray = response.optJSONArray("preparation");
                    } else {
                        jsonArray = response.optJSONArray("lecture");
                    }
                    List<PreparationEntity> data = PreparationEntity.parseJsonArray(jsonArray, mType);

                    if(null != data) mDataList.addAll(data);
                }
            }

            @Override
            public BaseRecyclerViewHolder<PreparationEntity> getViewHolder(ViewGroup parent, int viewType) {
                return new PrePareLessonsViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),R.layout.item_collective_prepare));
            }

            @Override
            public void OnItemClicked(View v, int position, PreparationEntity data) {
                if (mType.equals(Constants.TYPE_PREPARE_LESSON)) { //点击进入集体备课详情
                    Intent intent = new Intent(getActivity(), CollectivePrepareLessonsDetailActivity.class);
                    intent.putExtra(Constants.PREPARATIONID, data.getPreparationId());
                    CollectivePrepareLessonsFragment.this.startActivityForResult(intent, REQUEST_COLLECTIVE_PREPARE_OUT);
                    UIUtils.addEnterAnim(getActivity());
                } else {
                    Intent intent = new Intent(getActivity(), ListenDetailsActivity.class);//点击进入互动听课详情
                    intent.putExtra(Constants.PREPARATIONID, data.getPreparationId());
                    CollectivePrepareLessonsFragment.this.startActivityForResult(intent, REQUEST_COLLECTIVE_PREPARE_OUT);
                    UIUtils.addEnterAnim(getActivity());
                }
            }

            @Override
            public int getTotal() {
                return mTotal;
            }
        };
    }
}
