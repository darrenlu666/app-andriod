package com.codyy.erpsportal.commons.models.entities.mainpage;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析相同类型的{result: 'success' , data:[]}
 * Created by poe on 17-8-22.
 */

public class FormatJsonParse<T> {

    private String result;
    private String message;
    private List<T> data;

    /**
     * 解析数据.
     * @param result
     * @param Calss
     * @return
     */
    public FormatJsonParse parse(JSONObject result ,Class<T> Calss ){
        if(null == result) return null;
        FormatJsonParse parse = new FormatJsonParse();
        parse.setResult(result.optString("result"));
        parse.setMessage(result.optString("message"));

        JSONArray array = result.optJSONArray("data");
        List<T> data = new ArrayList<>();
        if(null != array && array.length()>0){

            for(int i=0;i<array.length();i++){
                JSONObject object = array.optJSONObject(i);

                T tt= new Gson().fromJson(object.toString(),Calss);
                data.add(tt);
            }
        }
        parse.setData(data);

        return parse;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
