package com.codyy.erpsportal.rethink.controllers.fragments;

import android.os.Bundle;
import android.text.TextUtils;

import com.codyy.erpsportal.commons.controllers.fragments.BaseFilterFragment;
import com.codyy.erpsportal.commons.models.entities.Choice;
import com.codyy.erpsportal.commons.models.entities.ChoicesOption;
import com.codyy.erpsportal.commons.models.entities.FilterItem;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.statistics.models.entities.AreaInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 教学反思筛选
 * Created by gujiajia on 2016/3/7.
 */
public class RethinkFilterFragment extends BaseFilterFragment {

    public static RethinkFilterFragment newInstance(UserInfo userInfo, String areaId) {
        RethinkFilterFragment fragment = new RethinkFilterFragment();
        Bundle bundle = new Bundle();
        AreaInfo areaInfo = new AreaInfo();
        if (userInfo.isArea()) {
            areaInfo.setType(AreaInfo.TYPE_AREA);
            areaInfo.setId(userInfo.getBaseAreaId());
        } else {
            areaInfo.setType(AreaInfo.TYPE_SCHOOL);
            areaInfo.setId(userInfo.getSchoolId());
        }
        bundle.putParcelable(ARG_AREA_INFO, areaInfo);

        if (!TextUtils.isEmpty(areaId)) {
            bundle.putString(ARG_AREA_ID, areaId);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    public static RethinkFilterFragment newInstance(UserInfo userInfo) {
        if (userInfo.isArea()) {
            return newInstance(userInfo, userInfo.getBaseAreaId());
        } else {
            return newInstance(userInfo, null);
        }
    }

    @Override
    protected void appendExtraOptions(List<FilterItem> items) {
        super.appendExtraOptions(items);
        ChoicesOption choicesOption = new ChoicesOption();
        choicesOption.setParamName("type");
        choicesOption.setTypeName("类型");
        Choice[] choices = new Choice[]{
                new Choice(Choice.ALL, "全部"),
                new Choice("CLASS", "课时反思"),
                new Choice("DAY", "日反思"),
                new Choice("WEEK", "周反思"),
                new Choice("MONTH", "月反思"),
                new Choice("SEMESTER_MIDDLE", "期中反思"),
                new Choice("SEMESTER_END", "期末反思")
        };
        List<Choice> choiceList = new ArrayList<>(Arrays.asList(choices));
        choicesOption.setChoices(choiceList);
        items.add(choicesOption);
    }
}
