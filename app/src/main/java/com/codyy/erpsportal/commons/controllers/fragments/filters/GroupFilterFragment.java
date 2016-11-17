package com.codyy.erpsportal.commons.controllers.fragments.filters;

import android.support.annotation.NonNull;

import com.codyy.erpsportal.commons.models.entities.filter.AreaList;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.filter.FilterEntity;
import java.util.List;

/**
 * 门户-圈组-兴趣组/教研组-筛选 新数据结构
 * 期望：目标清晰，结构明确 .
 * 能够满足不同的筛选条件 .
 * 根据角色显示不同的数据结构
 * 区县->Area
 * 地区管理员->学段/学校
 * 学校
 * Created by poe on 2016/1/25 .
 */
public class GroupFilterFragment extends BaseFilterFragment {
//    private static final String TAG = "GroupFilterFragment";

    @Override
    public void onLeftItemClick(int position, FilterEntity fe) {
        List<FilterEntity> list = mData.get(mRightClickPosition).getChildren();
        mLeftClickPosition = getConditionPosition(fe ,list);
        //update the condition Name value .
        mData.get(mRightClickPosition).setName(fe.getName());
        mConditionAdapter.notifyDataSetChanged();
        updateChoiceItemBackground(list);
        //All 并且非固定选项
        if (mLeftClickPosition == 0) {
            //init the stable conditions id and cacheId .
            mData.get(mRightClickPosition).setSelectedId(fe.getId());
            //init the stable conditions id and cacheId .
            updateNextCondition(fe);
            if(mRightClickPosition<(mData.size()-mBottom.size())){
                //清空
                clearRightData();
                addBottom();
                mConditionAdapter.notifyDataSetChanged();
            }
        }else{// if(FilterEntity.LEVEL_CLASS_END != fe.getLevel())
            switch (fe.getLevel()){
                case FilterEntity.LEVEL_AREA:
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());//对应右边的地区筛选
                    //选择不同的区域则更新对应的缓存ｃａｃｈｅＩｄ　．.. 方便直属校设置ｃａｃｈｅ的ＡｒｅａＩｄ
                    mData.get(mRightClickPosition).setCacheId(fe.getId());
                    loadData(fe, CLICK_LEFT, AreaList.TYPE_PARSE_AREA);
                    updateNextCondition(fe);
                    break;
                case FilterEntity.LEVEL_CLASS_SEMESTER:
                    if(STR_SCHOOL_DIRECT.equals(fe.getName())){//直属校
                        updateNextCondition(fe);
                    }
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());//对应右边的学段筛选条件
                    loadData(fe, CLICK_LEFT, AreaList.TYPE_PARSE_SEMESTER);
                    break;
                case FilterEntity.LEVEL_SCHOOL:
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());//右边的年级 右边的学校筛选条件（对应关系 child和parent的level关系）
                    updateNextCondition(fe);
                    loadData(fe , CLICK_LEFT , AreaList.TYPE_PARSE_SCHOOL);
                    break;
                case FilterEntity.LEVEL_CLASS_LEVEL://年级
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());//右边的年级 右边的学校筛选条件（对应关系 child和parent的level关系）
                    updateNextCondition(fe);
                    loadData(fe , CLICK_LEFT , AreaList.TYPE_PARSE_GRADE);
                    break;
                case FilterEntity.LEVEL_CLASS_SUBJECT://学科
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());//右边的年级 右边的学校筛选条件（对应关系 child和parent的level关系）
                    updateNextCondition(fe);
                    loadData(fe , CLICK_LEFT , AreaList.TYPE_PARSE_SUBJECT);
                    break;
                case FilterEntity.LEVEL_CLASS_CATEGORY://分组
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());//右边的学校筛选条件（对应关系 child和parent的level关系）
                    updateNextCondition(fe);
                    loadData(fe , CLICK_LEFT , AreaList.TYPE_PARSE_CATEGORY);
                    break;
                case FilterEntity.LEVEL_CLASS_END://结束位置
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());//右边的学科 or 分类
                    break;
            }
        }
    }

    @Override
    public void onRightItemClick(int position, FilterEntity fe) {

        mRightClickPosition = getConditionPosition(fe , mData);
        updateItemBackground();
        //固定的选项
        switch (fe.getLevelName()){
            case STR_SEMESTER:
                loadData(mData.get(mRightClickPosition), CLICK_RIGHT, AreaList.TYPE_PARSE_SEMESTER);
                break;
            case STR_SCHOOL:
                loadData(mData.get(mRightClickPosition), CLICK_RIGHT, AreaList.TYPE_PARSE_SCHOOL);
                break;
            case STR_LEVEL:
                loadData(mData.get(mRightClickPosition), CLICK_RIGHT, AreaList.TYPE_PARSE_GRADE);
                break;
            case STR_SUBJECT:
                loadData(mData.get(mRightClickPosition), CLICK_RIGHT, AreaList.TYPE_PARSE_SUBJECT);
                break;
            case STR_CATEGORY:
                loadData(mData.get(mRightClickPosition), CLICK_RIGHT, AreaList.TYPE_PARSE_CATEGORY);
                break;
            default:
                loadData(mData.get(mRightClickPosition), CLICK_RIGHT, AreaList.TYPE_PARSE_AREA);
                break;
        }
    }

    /**
     * 添加底部的固定筛选条件
     * type -教研组： 分段/学校/年级/学科
     * type -兴趣组： 分段/学校/分类
     */
    @Override
    public void addBottom() {
        mBottom.clear();
        if(UserInfo.USER_TYPE_AREA_USER.equals(mUserInfo.getUserType())){
            //学段
            mBottom.add(getSemester());
            //学校
            mBottom.add(new FilterEntity(mUserInfo.getUuid(),"",mUserInfo.getBaseAreaId(),mUserInfo.getSchoolId(),STR_ALL,FilterEntity.LEVEL_SCHOOL,STR_SCHOOL,FilterEntity.getURL(STR_SCHOOL),false,false));
        }

        if(TYPE_FILTER_INTEREST.equals(mFilterType)){
            //分类
            mBottom.add(new FilterEntity(mUserInfo.getUuid(),"",mUserInfo.getBaseAreaId(),mUserInfo.getSchoolId(),STR_ALL,FilterEntity.LEVEL_CLASS_CATEGORY,STR_CATEGORY,FilterEntity.getURL(STR_CATEGORY),false,false));
        }else if(TYPE_FILTER_TEACH.equals(mFilterType)){
            //年级
            if(!hasLevel(STR_LEVEL)){
                mBottom.add(getClassLevel());
            }
            //学科
            if(!hasLevel(STR_SUBJECT)){
                mBottom.add(getSubject());
            }
        }

        //add bottom .
        mData.addAll(mBottom);
        mConditionAdapter.setData(mData);
    }

    @NonNull
    private FilterEntity getSemester() {
        FilterEntity fe = new FilterEntity(mUserInfo.getUuid(),"",getCacheAreaId(),mUserInfo.getSchoolId(),STR_ALL,FilterEntity.LEVEL_CLASS_SEMESTER,STR_SEMESTER,FilterEntity.getURL(STR_SEMESTER),false,false);
        fe.setCacheAreaId(mUserInfo.getBaseAreaId());
        return fe;
    }
}


