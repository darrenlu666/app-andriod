package com.codyy.erpsportal.commons.models.entities.my;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 老师获取的班级信息解析类
 * Created by poe on 16-8-11.
 */
public class TeacherClassParse {
    private String result ;
    @SerializedName("teacherClassList")
    private List<ClassCont> dataList;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<ClassCont> getDataList() {
        // DO: 16-10-8 去重处理
        if(dataList!=null && dataList.size()>0){
            List<ClassCont> result = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            for(int i=0; i < dataList.size() ; i++){
                if(!ids.contains(dataList.get(i).getBaseClassId())){
                    ids.add(dataList.get(i).getBaseClassId());
                    result.add(dataList.get(i));
                }
            }

            if(result.size()>0){
                dataList = result;
            }
        }
        return dataList;
    }

    public void setDataList(List<ClassCont> dataList) {
        this.dataList = dataList;
    }
}
