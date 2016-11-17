package com.codyy.erpsportal.tutorship.controllers.fragments;

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
public class TutorialFilterFragment extends BaseFilterFragment {

    public static TutorialFilterFragment newInstance(UserInfo userInfo, String areaId) {
        TutorialFilterFragment fragment = new TutorialFilterFragment();
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

    public static TutorialFilterFragment newInstance(UserInfo userInfo) {
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
        choicesOption.setParamName("status");
        choicesOption.setTypeName("状态");
        Choice[] choices = new Choice[]{
                new Choice(Choice.ALL, "全部"),
                new Choice("INIT", "未开始"),
                new Choice("PROGRESS", "进行中"),
                new Choice("END", "已结束"),
        };
        List<Choice> choiceList = new ArrayList<>(Arrays.asList(choices));
        choicesOption.setChoices(choiceList);
        items.add(choicesOption);
    }
}
