package com.codyy.erpsportal.commons.models.personal;

import java.util.ArrayList;
import java.util.List;

/**
 * 家长孩子解析类
 * Created by poe on 16-8-19.
 */
public class StudentParse {
    private String result;//
    private String resourceFlag;// Y: N
    private String groupFlag;//Y
    private String message ;
    private List<Student> children;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResourceFlag() {
        return resourceFlag;
    }

    public void setResourceFlag(String resourceFlag) {
        this.resourceFlag = resourceFlag;
    }

    public String getGroupFlag() {
        return groupFlag;
    }

    public void setGroupFlag(String groupFlag) {
        this.groupFlag = groupFlag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Student> getChildren() {
        // DO: 16-10-8 去重处理
        if(children!=null && children.size()>0){
            List<Student> result = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            for(int i=0; i < children.size() ; i++){
                if(!ids.contains(children.get(i).getClassId())){
                    ids.add(children.get(i).getClassId());
                    result.add(children.get(i));
                }
            }

            if(result.size()>0){
                children = result;
            }
        }
        return children;
    }

    public void setChildren(List<Student> children) {
        this.children = children;
    }
}
