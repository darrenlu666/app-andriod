package com.codyy.tpmp.filterlibrary.entities;

import android.text.TextUtils;

import com.codyy.tpmp.filterlibrary.entities.interfaces.DataBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地直接构造一些筛选数据.
 * ex: --|分组
 *       ---| 教研组
 *       ---| 兴趣组
 *  {@link FilterModule#constructSimpleModule(int, FilterUser)}
 * Created by poe on 28/04/17.
 */

public class SimpleFilterBuilder implements DataBuilder<FilterCell,List<FilterCell>> {
    private static final String TAG = "SimpleFilterBuilder";
    private String mLevelName;
    /**
     * 选项的名字集合,按照传入的顺序进行初始化.
     */
    private List<Filter> mChoices = new ArrayList<>();

    public SimpleFilterBuilder() {
    }

    public SimpleFilterBuilder(String name ,Filter ...params) {
        this.mLevelName = name;
        for(Filter param:params){
            if(!TextUtils.isEmpty(param.getName())){
                mChoices.add(param);
            }
        }
    }

    @Override
    public void build(FilterCell param, BuildListener<List<FilterCell>> listener) {
        List<FilterCell> mData = new ArrayList<>();
        //all
        FilterCell all = param.clone();
        all.setName(FilterConstants.STR_ALL);
        all.setParent(param);
        all.setCheck(true);
        all.setId("");//all id 为“”空
        mData.add(all);
        //build
        if(mChoices != null && mChoices.size()>0){
            for(Filter filter : mChoices){
                FilterCell fe = param.clone();
                fe.setId(filter.getId());
                fe.setName(filter.getName());
                fe.setParent(param);
                fe.setCheck(false);
                mData.add(fe);
            }
            if(null != listener) listener.onSuccess(mLevelName,mData);
        }else{//发出没有数据错误提示
            if(null != listener){
                listener.onError(new Throwable("SimpleFilterBuilder has no item init error! you must init this with some choices ."));
            }
        }
    }
}
