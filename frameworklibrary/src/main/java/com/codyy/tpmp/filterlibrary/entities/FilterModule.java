package com.codyy.tpmp.filterlibrary.entities;

import com.codyy.tpmp.filterlibrary.entities.interfaces.DataBuilder;
import com.codyy.tpmp.filterlibrary.entities.interfaces.FilterModuleInterface;
import com.codyy.tpmp.filterlibrary.entities.interfaces.ParamBuilder;
import com.codyy.tpmp.filterlibrary.interfaces.HttpGetInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一个独立的筛选子模块 .
 * 筛选中只有右侧的item被认为是一个个模块.
 * 1. 包含一个FilterEntity
 * Created by poe on 28/04/17.
 */

public class FilterModule implements ParamBuilder, FilterModuleInterface<FilterCell> {

    /**
     * 模块类型 default: {@link com.codyy.tpmp.filterlibrary.entities.interfaces.FilterModuleInterface.Mode#SINGLE}
     */
    private FilterModuleInterface.Mode type = FilterModuleInterface.Mode.SINGLE;

    /**
     * 当前模块选中的id
     */
    private String selectedId;

    /**
     * 当前模块是否处于选中状态
     */
    private boolean isSelected = false;
    /**
     * 孩子目前的选中状态.
     */
    private int selectPosition = 0;

    /**
     * 筛选的单元实体类
     */
    private FilterCell data;
    /**
     * 孩子的数据合集
     */
    private List<FilterCell> childrenData;


    private DataBuilder<FilterCell,List<FilterCell>> dataBuilder;
    /**
     * 用来获取网络请求时的参数
     */
    private FilterParam filterParam;

    /**
     * 上一个相连的模块
     */
    private FilterModule last;
    /**
     * 下一个相连的模块
     */
    private FilterModule next;

    public FilterModule() {
    }

    public FilterModule(FilterCell data, DataBuilder<FilterCell, List<FilterCell>> dataBuilder, FilterParam filterParam) {
        this.data = data;
        this.dataBuilder = dataBuilder;
        this.filterParam = filterParam;

        //init the dataBuilder if its type is {@link #RemoteFilterBuilder}
        if(null != dataBuilder && null != filterParam){
            if(dataBuilder instanceof SimpleFilterBuilder){
                //do noting ...
            }else if(dataBuilder instanceof RemoteFilterBuilder){
                ((RemoteFilterBuilder)dataBuilder).setParamBuilder(this);
            }
        }
    }

    @Override
    public Map<String, String> getParams() {
        if(null != filterParam) return filterParam.getParams();
        return new HashMap<>();
    }

    public Mode getType() {
        return type;
    }

    public void setType(Mode type) {
        this.type = type;
    }

    public String getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public FilterCell getData() {
        return data;
    }

    public void setData(FilterCell data) {
        this.data = data;
    }

    public DataBuilder<FilterCell, List<FilterCell>> getDataBuilder() {
        return dataBuilder;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int newSelectPos) {
        if(null != childrenData && childrenData.size()>0 && newSelectPos >-1 && newSelectPos < childrenData.size()){
            childrenData.get(selectPosition).setCheck(false);
            this.selectPosition = newSelectPos;
            childrenData.get(selectPosition).setCheck(true);
        }
    }

    public void setDataBuilder(DataBuilder<FilterCell, List<FilterCell>> dataBuilder) {
        if(null == filterParam) throw new RuntimeException("you need setFilterParam before #setDataBuilder!");
        this.dataBuilder = dataBuilder;
        if(null != dataBuilder){
            if(dataBuilder instanceof SimpleFilterBuilder){
                //do noting ...
            }else if(dataBuilder instanceof RemoteFilterBuilder){
                ((RemoteFilterBuilder)dataBuilder).setParamBuilder(this);
            }
        }
    }

    public FilterParam getFilterParam() {
        return filterParam;
    }

    public void setFilterParam(FilterParam filterParam) {
        this.filterParam = filterParam;
    }

    public FilterModule getLast() {
        return last;
    }

    public void setLast(FilterModule last) {
        this.last = last;
    }

    public FilterModule getNext() {
        return next;
    }

    public void setNext(FilterModule next) {
        this.next = next;
    }


    public List<FilterCell> getChildrenData() {
        return childrenData;
    }

    public void setChildrenData(List<FilterCell> childrenData) {
        this.childrenData = childrenData;
    }

    @Override
    public void onItemClick(final DataBuilder.BuildListener<List<FilterCell>> listener) {
//        dataBuilder.build(getData(), listener);
        if(getChildrenData() != null && !filterParam.isUpdate()){
            //已缓存了数据了.
            if(null != listener) listener.onSuccess(getData().getLevelName(),getChildrenData());
        }else{
            dataBuilder.build(getData(), new DataBuilder.BuildListener<List<FilterCell>>() {

                @Override
                public void onSuccess(String levelName, List<FilterCell> result) {
                    filterParam.setUpdate(false);
                    //填充孩子数据.
                    if(null != result) {
                        setChildrenData(result);
                        setSelectPosition(0);
                    }
                    if(null != listener) listener.onSuccess(levelName,result);
                }

                @Override
                public void onError(Throwable t) {
                    if(null != listener) listener.onError(t);
                }
            });
        }
    }

    @Override
    public void onChildrenClick(int position, FilterCell data, DataBuilder.BuildListener<List<FilterCell>> listener) {
        if(null == childrenData) return;
        if(position<0 || position>= childrenData.size()) throw new IllegalArgumentException("position is out of the mData index !");
        if(childrenData == null) childrenData = new ArrayList<>();

        if(Mode.COMPLEX == type){//区域孩子数据->left item .+
            //update the condition show str .
            getData().setName(data.getName());
            //update the selected position .
            setSelectPosition(position);
            //"全部"，"直属校"
            if(FilterConstants.STR_SCHOOL_DIRECT.equals(data.getName())
                    || FilterConstants.STR_ALL.equals(data.getName())){
                //use the current module id .
                setSelectedId(data.getId());
                if(null != listener) listener.onSuccess(data.getName(),new ArrayList<FilterCell>());
            }else{
                setSelectedId(data.getId());
                //缓存当前Module的区域id
                String currentAreaId = filterParam.getAreaId();
                //更新选中的区域id，方便获取下一级区域的数据.
                filterParam.setAreaId(data.getId());
                //因为区域特殊性，相同的api所以可以重用同一个DataBuilder.使用当前的dataBuilder获取数据.
                dataBuilder.build(data , listener);
                //reset the current areaId;
                filterParam.setAreaId(currentAreaId);
                filterParam.setUpdate(false);
            }
        }else if(Mode.SINGLE == type){//学校 等 简单数据.
            //过滤掉快速点击产生的干扰数据
            if(data.getParent() == getData()){
                //选中的id
                setSelectedId(data.getId());
                //update the condition show str .
                getData().setName(data.getName());
                //update the selected position .
                setSelectPosition(position);
                if(position == 0) clearSelect();
            }
            if(null != listener) listener.onSuccess(null,new ArrayList<FilterCell>());
        }
    }

    /**
     * 清除选中情况
     */
    public void clearSelect(){
        setSelectedId("");
        setSelectPosition(0);
    }

    /**
     * 构建简单的筛选模块.
     *
     * @param level
     * @return
     */
    public static FilterModule constructSimpleModule(int level, FilterUser filterUser) {
        FilterModule areaModule = new FilterModule();
        //组别->特殊,产生节点的增减.
        if(FilterConstants.LEVEL_CLASS_TEAM == level){
            areaModule.setType(Mode.COMPLEX);
        }
        //data .
        String levelName = FilterConstants.getLevelName(level);
        FilterCell filterCell = new FilterCell(
                filterUser.getUuid()
                , filterUser.getBaseAreaId()
                , FilterConstants.STR_ALL
                , level
                , levelName
                , false
                , false);

        areaModule.setData(filterCell);
        //param
        FilterParam param = new FilterParam(filterUser.getUuid(), filterUser.getBaseAreaId(),false);
        areaModule.setFilterParam(param);
        //dataBuilder
        SimpleFilterBuilder dataBuilder = null;
        switch (level) {
            case FilterConstants.LEVEL_CLASS_STATE://状态
                dataBuilder = new SimpleFilterBuilder(levelName
                        , new Filter(FilterConstants.VAL_STATE_INIT,FilterConstants.STR_STATE_INIT)//未开始
                        , new Filter(FilterConstants.VAL_STATE_PROGRESS,FilterConstants.STR_STATE_PROGRESS)//进行中
                        , new Filter(FilterConstants.VAL_STATE_END,FilterConstants.STR_STATE_END)//已结束
                );
                break;
            case FilterConstants.LEVEL_MANAGER_STATE://状态
                dataBuilder = new SimpleFilterBuilder(levelName
                        , new Filter(FilterConstants.VAL_STATE_PENDING,FilterConstants.STR_STATE_PENDING)//待处理
                        , new Filter(FilterConstants.VAL_STATE_PASS,FilterConstants.STR_STATE_PASS)//通过
                        , new Filter(FilterConstants.VAL_STATE_REJECT,FilterConstants.STR_STATE_REJECT)//未通过
                        , new Filter(FilterConstants.VAL_STATE_CLOSED,FilterConstants.STR_STATE_CLOSED)//已关闭
                );
                break;
            case FilterConstants.LEVEL_LESSON_CATEGORY://课程类别[单节课/系列课].
                dataBuilder = new SimpleFilterBuilder(levelName
                        , new Filter(filterUser.getBaseAreaId(),FilterConstants.STR_CATEGORY_LESSON_SINGLE)
                        , new Filter(filterUser.getBaseAreaId(),FilterConstants.STR_CATEGORY_LESSON_SERIES)
                );
                break;
            case FilterConstants.LEVEL_CLASS_TEAM://分组
                dataBuilder = new SimpleFilterBuilder(levelName
                        , new Filter(filterUser.getBaseAreaId(),FilterConstants.STR_TEAM_TEACH)
                        , new Filter(filterUser.getBaseAreaId(),FilterConstants.STR_TEAM_INTEREST)
                );
                break;
        }
        areaModule.setDataBuilder(dataBuilder);
        // 默认选中
        areaModule.setSelected(false);
        areaModule.setLast(null);

        return areaModule;
    }

    /**
     * 构建远程的筛选模块.
     *
     * @param level
     * @return
     */
    public static FilterModule constructRemoteModule(int level , FilterUser filterUser , HttpGetInterface requestSender) {
        FilterModule areaModule = new FilterModule();
        if (level == FilterConstants.LEVEL_AREA) {
            areaModule.setType(FilterModuleInterface.Mode.COMPLEX);
        }
        //data .
        String levelName = FilterConstants.getLevelName(level);
        FilterCell filterCell = new FilterCell(
                filterUser.getUuid()
                , filterUser.getBaseAreaId()
                , FilterConstants.STR_ALL
                , level
                , levelName
                , false
                , false);

        areaModule.setData(filterCell);
        //param
        boolean isCasCade = false;
        //区域需要areaId级联.
        if (level == FilterConstants.LEVEL_AREA){
            isCasCade = true;
        }
        //直属校需要级联.
        if(level == FilterConstants.LEVEL_SCHOOL){
            isCasCade = true;
        }
        FilterParam param = new FilterParam(filterUser.getUuid(), filterUser.getBaseAreaId(),isCasCade);
        areaModule.setFilterParam(param);
        //dataBuilder
        RemoteFilterBuilder dataBuilder = new RemoteFilterBuilder(URLFilter.getURL(level), requestSender);
        areaModule.setDataBuilder(dataBuilder);

        // 默认选中
        areaModule.setSelected(false);
        areaModule.setLast(null);

        return areaModule;
    }
}
