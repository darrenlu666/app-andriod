package com.codyy.erpsportal.repairs.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.codyy.erpsportal.commons.controllers.fragments.BaseFilterFragment;
import com.codyy.erpsportal.commons.models.entities.AreaFilterItem;
import com.codyy.erpsportal.commons.models.entities.FilterItem;

import java.util.List;

/**
 * 地区筛选
 * Created by gujiajia on 2017/3/21.
 */

public class AreaFilterFragment extends BaseFilterFragment {

    /**
     * @param areaId 地区筛选根级，无需地区筛选的传null
     * @return
     */
    public static AreaFilterFragment newInstance(@NonNull String areaId) {
        AreaFilterFragment fragment = new AreaFilterFragment();
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(areaId)) {
            bundle.putString(ARG_AREA_ID, areaId);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    public static AreaFilterFragment newInstance(AreaFilterItem areaFilterItem) {
        AreaFilterFragment filterFragment = new AreaFilterFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_INIT, areaFilterItem);
        filterFragment.setArguments(bundle);
        return filterFragment;
    }

    @Override
    protected void appendExtraOptions(List<FilterItem> items) {
        //默认实现添加了年级与学科，单独的地区筛选是不需要的
//        super.appendExtraOptions(items);
    }

    @Override
    protected boolean onDirectSchoolClick() {
        return true;//已处理
    }

    @Override
    protected boolean onLowestAreaClick() {
        return true;//已处理
    }
}
