package com.codyy.erpsportal.onlinemeetings.models.entities;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 基本的coco消息实体类{command:'',body:''} .
 * Created by poe on 17-9-18.
 */

public class BaseCMDEntity<T> {
    private String command;//命令名
    private T body;//返回结果体.

    public  BaseCMDEntity parse(JSONObject result , Class<T> Calss ){
        if(null == result) return null;
        BaseCMDEntity parse = new BaseCMDEntity();
        parse.setCommand(result.optString("command"));
        JSONObject object = result.optJSONObject("body");
        T tt= new Gson().fromJson(object.toString(),Calss);
        parse.setBody(object);
        return parse;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
