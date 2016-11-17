package com.codyy.erpsportal.commons.controllers.fragments.filters;

import android.os.Bundle;
import android.text.TextUtils;

import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.models.entities.filter.AreaItem;
import com.codyy.erpsportal.commons.models.entities.filter.AreaList;
import com.codyy.erpsportal.commons.models.entities.filter.FilterEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用-网络授课
 * Created by poe on 2016/3/16 .
 */
public class NetTeachFilterFragment extends BaseFilterFragment {
    private static final String TAG = "NetTeachFilterFragment";
    /***  网络授课的进行状态 *****/
    public static final String STR_NET_STATE_INIT = "未开始";
    public static final String STR_NET_STATE_PROGRESS = "进行中";
    public static final String STR_NET_STATE_CLOSED = "已结束";

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
            updateNextCondition(fe);
            if(mRightClickPosition<(mData.size()-1)){
                //清空
                clearRightData();
                if(fe.getLevelName().equals(STR_LEVEL)){
                    addTeachBottom();
                }else{
                    addBottom();
                }
                mConditionAdapter.notifyDataSetChanged();
            }
        }else{// if(FilterEntity.LEVEL_CLASS_END != fe.getLevel())
            switch (fe.getLevel()){
                case FilterEntity.LEVEL_AREA://地区点击
                    //对应右边的地区筛选
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());
                    updateNextCondition(fe);
                    loadData(fe, CLICK_LEFT, AreaList.TYPE_PARSE_AREA);
                    break;
                case FilterEntity.LEVEL_CLASS_SEMESTER://学段
                    //直属校
                    if(STR_SCHOOL_DIRECT.equals(fe.getName())){
                        updateNextCondition(fe);
                    }
                    //对应右边的学段筛选条件
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());
                    loadData(fe, CLICK_LEFT, AreaList.TYPE_PARSE_SEMESTER);
                    break;
                case FilterEntity.LEVEL_SCHOOL://学校
                    //右边的年级 右边的学校筛选条件（对应关系 child和parent的level关系）
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());
                    updateNextCondition(fe);
                    loadData(fe, CLICK_LEFT, AreaList.TYPE_PARSE_SCHOOL);
                    break;
                case FilterEntity.LEVEL_CLASS_TEAM://组别
                    //右边的年级 右边的学校筛选条件（对应关系 child和parent的level关系）
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());
                    mData.get(mRightClickPosition).setCacheSchoolId(fe.getId());
                    //网络授课－学校下面跳级处理兴趣组筛选...
                    if(TYPE_FILTER_NET_TEACH_AREA_MANAGER.equals(mFilterType)){
                        loadData(fe, CLICK_RIGHT, AreaList.TYPE_PARSE_GRADE);
                    }else{
                        loadTeamData(fe,true);
                    }
                    break;
                case FilterEntity.LEVEL_CLASS_LEVEL://年级-{click->组别item}
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());
                    mData.get(mRightClickPosition).setCacheSchoolId(fe.getCacheSchoolId());
                    //添加底部数据 -教研组{年纪，学科} -兴趣组{分类}
                    //清空
                    clearRightData();
                    addBottomLis(fe);
                    updateNextCondition(fe);
                    loadData(fe,CLICK_LEFT,AreaList.TYPE_PARSE_GRADE);
                    break;
                case FilterEntity.LEVEL_CLASS_SUBJECT://学科-{click->年级item}
                    //右边的年级 右边的学校筛选条件（对应关系 child和parent的level关系）
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());
                    clearRightData();
                    addTeachBottom();
                    updateNextCondition(fe);
                    loadData(fe,CLICK_LEFT,AreaList.TYPE_PARSE_SUBJECT);
                    break;
                case FilterEntity.LEVEL_CLASS_CATEGORY:
                    //右边的组别筛选条件（对应关系 child和parent的level关系）
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());
                    //添加底部数据 -教研组{年纪，学科} -兴趣组{分类}
                    clearRightData();
                    addBottomLis(fe);
                    updateNextCondition(fe);
                    loadData(fe,CLICK_LEFT,AreaList.TYPE_PARSE_CATEGORY);
                    break;
                case FilterEntity.LEVEL_CLASS_STATE:
                    //右边的年级 右边的学科分类（对应关系 child和parent的level关系）
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());
                    updateNextCondition(fe);
                    loadStateData(fe,true);
                    break;
                case FilterEntity.LEVEL_CLASS_END://结束位置
                    //右边的学科 or 分类
                    mData.get(mRightClickPosition).setSelectedId(fe.getId());
                    break;
            }
        }
    }

    @Override
    public void onRightItemClick(int position, FilterEntity fe) {
        mRightClickPosition = getConditionPosition(fe , mData);
        updateItemBackground();
        String title = fe.getLevelName();
        //固定的选项
        if(title.equals(STR_SEMESTER)){//学段
            loadData(fe, CLICK_RIGHT, AreaList.TYPE_PARSE_SEMESTER);
        } else if (title.equals(STR_SCHOOL)) {//学校
            loadData(fe, CLICK_RIGHT, AreaList.TYPE_PARSE_SCHOOL);
        } else if (title.equals(STR_TEAM)) {//组别
            loadTeamData(fe,false);
        }else if (title.equals(STR_LEVEL)) {//年级
            loadData(fe, CLICK_RIGHT, AreaList.TYPE_PARSE_GRADE);
        } else if (title.equals(STR_SUBJECT)) {//学科
            loadData(fe, CLICK_RIGHT, AreaList.TYPE_PARSE_SUBJECT);
        }else if (title.equals(STR_CATEGORY)) {//分类
            loadData(fe, CLICK_RIGHT, AreaList.TYPE_PARSE_CATEGORY);
        }else if (title.equals(STR_STATE)) {//状态
            loadStateData(fe,false);
        }else{
            loadData(fe, CLICK_RIGHT, AreaList.TYPE_PARSE_AREA);
        }
    }

    /**
     * 添加底部的固定筛选条件
     * type -教研组： 分段/学校/年级/学科
     * type -兴趣组： 分段/学校/分类
     */
    public void addBottom() {
        mBottom.clear();
//        mockData(2);
        switch (mFilterType){
            case TYPE_FILTER_NET_TEACH://网络授课
                addNetTeachBottom();
                break;
            case TYPE_FILTER_NET_TEACH_AREA_MANAGER://网络授课－区内授课
                addNetTeachAreaBottom();
                break;
            case TYPE_FILTER_LIVE_WATCH_AREA://实时伦须－区域
                addWatchAreaBottom();
                break;
            case TYPE_FILTER_LIVE_WATCH_SCHOOL://实时轮巡－学校
                addTeachBottom();
                break;
        }
        //add bottom .
        mData.addAll(mBottom);
        mConditionAdapter.setData(mData);
    }

    private void addNetTeachBottom() {
        //年级
        if(!hasLevel(STR_LEVEL)){
            mBottom.add(getClassLevel());
        }
        //学科
        if(!hasLevel(STR_SUBJECT)){
            mBottom.add(getSubject());
        }
        //状态 {未开始,进行中，已结束} (CLOSED/WAIT/APPROVED/REJECT)
        if(!hasLevel(STR_STATE)){
            mBottom.add(new FilterEntity(mUserInfo.getUuid(),"",mUserInfo.getBaseAreaId(),getCacheSchoolId(),STR_ALL,FilterEntity.LEVEL_CLASS_STATE,STR_STATE,FilterEntity.getURL(STR_STATE),false,false));
        }
    }



    private void addNetTeachAreaBottom() {
        //学校
        if(!hasLevel(STR_SCHOOL)){
            mBottom.add(new FilterEntity(mUserInfo.getUuid(),"",getCacheAreaId(),mUserInfo.getSchoolId(),STR_ALL,FilterEntity.LEVEL_SCHOOL,STR_SCHOOL,FilterEntity.getURL(STR_SCHOOL),false,false) );
        }
        addNetTeachBottom();
    }

    /** 实时轮巡－区域**/
    private void addWatchAreaBottom() {
        //学校
        if(!hasLevel(STR_SCHOOL)){
            mBottom.add(new FilterEntity(mUserInfo.getUuid(),"",getCacheAreaId(),mUserInfo.getSchoolId(),STR_ALL,FilterEntity.LEVEL_SCHOOL,STR_SCHOOL,FilterEntity.getURL(STR_SCHOOL),false,false) );
        }
    }
    /**
     * 添加教研组bottom{@link #STR_LEVEL,#STR_SUBJECT}
     */
    private void addTeachBottom() {
        //年级
        if(!hasLevel(STR_LEVEL)){
            mData.add(getClassLevel());
        }
        //学科
        if(!hasLevel(STR_SUBJECT)){
            mData.add(getSubject());
        }
        //状态
        switch (mFilterType){
            case TYPE_FILTER_NET_TEACH://状态 {未开始，进行中，已结束} (INIT/PROGRESS/END)
            case TYPE_FILTER_NET_TEACH_AREA_MANAGER://状态 {未开始，进行中，已结束} (INIT/PROGRESS/END)
                if(!hasLevel(STR_STATE)){
                    mData.add(new FilterEntity(mUserInfo.getUuid(),"",getCacheAreaId(),getCacheSchoolId(),STR_ALL,FilterEntity.LEVEL_CLASS_STATE,STR_STATE,FilterEntity.getURL(STR_STATE),false,false));
                }
                break;
        }
    }

    /**
     * 添加底部item
     * 教研组：年纪/学科
     * 兴趣组：分类
     */
    private void addBottomLis(FilterEntity fe ) {
        if(STR_TEAM_INTEREST.equals(fe.getName())){//兴趣组
            //分类
            if(!hasLevel(STR_CATEGORY)){
                mData.add(new FilterEntity(mUserInfo.getUuid(),"",mUserInfo.getBaseAreaId(),mUserInfo.getSchoolId(),STR_ALL,FilterEntity.LEVEL_CLASS_CATEGORY,STR_CATEGORY,FilterEntity.getURL(STR_CATEGORY),false,false));
            }
            if(TYPE_FILTER_GROUP_AREA.equals(mFilterType)){
                //状态 {待处理，通过，未通过，被关闭} (CLOSED/WAIT/APPROVED/REJECT)
                if(!hasLevel(STR_STATE)){
                    mData.add(new FilterEntity(mUserInfo.getUuid(),"",mUserInfo.getBaseAreaId(),mUserInfo.getSchoolId(),STR_ALL,FilterEntity.LEVEL_CLASS_STATE,STR_STATE,FilterEntity.getURL(STR_STATE),false,false));
                }
            }

        }else if(STR_TEAM_TEACH.equals(fe.getName())){//教研组
            //年级
            addTeachBottom();
        }else if(TYPE_FILTER_NET_TEACH_AREA_MANAGER.equals(mFilterType)){
            //网络教研组
            addTeachBottom();
        }
        mConditionAdapter.notifyDataSetChanged();
    }

    /**
     * 加载“组别”数据
     * @param filterEntity
     */
    private void loadTeamData(final FilterEntity filterEntity ,boolean isLeft) {
        Cog.i(TAG,"loadTeamData:id 【"+filterEntity.getId()+"】 : 【"+filterEntity.getLevelName() +filterEntity.getName()+"】"+" cacheID 【"+ filterEntity.getCacheId()+"】");
        //【全部，教研组，兴趣组】
        if(isLeft){
            //left click .
            refreshLeft(filterEntity);
        }else{
            if(filterEntity.getChildren() == null || filterEntity.getChildren().size() == 0){
                //初始化
                updateChildren(filterEntity);
            }else{
                //右边点击直接更新数据
                mChoiceAdapter.setData(filterEntity.getChildren());
            }
        }
    }

    /**
     * 加载“状态”数据
     * @param filterEntity
     */
    private void loadStateData(final FilterEntity filterEntity ,boolean isLeft) {
        //【全部，教研组，兴趣组】
        if(isLeft){
            //left click .
            makeStateData();
            refreshLeft(filterEntity);
        }else{
            if(filterEntity.getChildren() == null || filterEntity.getChildren().size() == 0){
                //初始化
                makeStateData();
                updateChildren(filterEntity);
            }else{
                //右边点击直接更新数据
                mChoiceAdapter.setData(filterEntity.getChildren());
            }
        }
    }

    /**
     * 制造状态数据
     */
    private void makeStateData() {
        List<AreaItem> areaItems = new ArrayList<>();
        areaItems.add(new AreaItem(STR_ALL,"",null));
        if(mFilterType.equals(TYPE_FILTER_NET_TEACH) || mFilterType.equals(TYPE_FILTER_NET_TEACH_AREA_MANAGER)){
            areaItems.add(new AreaItem(STR_NET_STATE_INIT,"INIT",null));
            areaItems.add(new AreaItem(STR_NET_STATE_PROGRESS,"PROGRESS",null));
            areaItems.add(new AreaItem(STR_NET_STATE_CLOSED,"END",null));
        }
        mAreaList = new AreaList();
        mAreaList.setLevelName(STR_STATE);
        mAreaList.setAreaItemlist(areaItems);
    }

    @Override
    public Bundle getFilterData() {

        Bundle bundle = super.getFilterData();
        if(TYPE_FILTER_NET_TEACH_AREA_MANAGER.equals(mFilterType)
                || TYPE_FILTER_LIVE_WATCH_AREA.equals(mFilterType)){
            // TODO: 16-9-19 区域id 股择需要重新定义　．
            if (mUserInfo.isArea()) {
                int lastAreaPosition = getConditionPosition(STR_SCHOOL);
                //区域id
                if(lastAreaPosition > 0){
                    if(!TextUtils.isEmpty(mData.get(lastAreaPosition-1).getSelectedId())){
                        bundle.putString("areaId", mData.get(getConditionPosition(STR_SCHOOL)-1).getSelectedId());
                    }else if((lastAreaPosition-2 >= 0)&&!TextUtils.isEmpty(mData.get(lastAreaPosition-2).getSelectedId())){
                        bundle.putString("areaId", mData.get(getConditionPosition(STR_SCHOOL)-2).getSelectedId());
                    }
                }else{
                    bundle.putString("areaId", mUserInfo.getBaseAreaId());
                }
            }

        }

        return bundle;
    }
}


