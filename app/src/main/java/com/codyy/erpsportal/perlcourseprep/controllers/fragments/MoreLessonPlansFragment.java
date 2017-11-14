package com.codyy.erpsportal.perlcourseprep.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codyy.url.URLConfig;
import com.codyy.erpsportal.perlcourseprep.models.entities.LessonPlan;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 更多个人备课
 * Created by gujiajia on 2016/3/3.
 */
public class MoreLessonPlansFragment extends LessonPlanListFragment {

    private final static String ARG_AREA_ID = "areaId";

    private final static String ARG_SCHOOL_ID = "schoolId";
    private final static String ARG_CLASSLEVEL_ID = "classLevelId";
    private final static String ARG_SORT_TYPE = "sortType";
    private final static String ARG_ORDER_TYPE = "orderType";
    private final static String ARG_SUBJECT_ID = "subjectId";

    private String mAreaId;

    private String mSchoolId;

    public static MoreLessonPlansFragment newInstance(String areaId, String schoolId,
                                                      String classLevelId, String sortType,
                                                      String orderType, String subjectId) {
        MoreLessonPlansFragment fragment = new MoreLessonPlansFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_AREA_ID, areaId);
        bundle.putString(ARG_SCHOOL_ID, schoolId);
        bundle.putString(ARG_CLASSLEVEL_ID, classLevelId);
        bundle.putString(ARG_SORT_TYPE, sortType);
        bundle.putString(ARG_ORDER_TYPE, orderType);
        bundle.putString(ARG_SUBJECT_ID, subjectId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAreaId = getArguments().getString(ARG_AREA_ID, "");
            mSchoolId = getArguments().getString(ARG_SCHOOL_ID, "");
            addParam("classLevelId", getArguments().getString(ARG_CLASSLEVEL_ID, ""));
            addParam("sortType", getArguments().getString(ARG_SORT_TYPE, ""));
            addParam("orderType", getArguments().getString(ARG_ORDER_TYPE, ""));
            addParam("subjectId", getArguments().getString(ARG_SUBJECT_ID, ""));
        }
    }

    @Override
    protected List<LessonPlan> getList(JSONObject response) {
        return LessonPlan.JSON_PARSER.parseArray(response.optJSONArray("list"));
    }

    @Override
    protected boolean checkHasMore(JSONObject response, int itemCount) {
        return response.optInt("total") > itemCount;
    }

    @Override
    protected void addParams(Map<String, String> map) {
        super.addParams(map);
        map.put("baseAreaId", mAreaId);
        map.put("schoolId", mSchoolId);
        /*map.put("classLevelId", "");
        map.put("sortType", "");
        map.put("orderType", "DESC");
        map.put("subjectId", "");*/
    }

    @Override
    protected String getUrl() {
        return URLConfig.MORE_PERSONAL_LES_PREP;
    }
}
