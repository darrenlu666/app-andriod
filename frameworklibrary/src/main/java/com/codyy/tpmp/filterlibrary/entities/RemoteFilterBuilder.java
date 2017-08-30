package com.codyy.tpmp.filterlibrary.entities;


import com.codyy.tpmp.filterlibrary.entities.interfaces.DataBuilder;
import com.codyy.tpmp.filterlibrary.entities.interfaces.ParamBuilder;
import com.codyy.tpmp.filterlibrary.interfaces.HttpGetInterface;
import com.google.gson.Gson;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 筛选-数据请求包裹类.
 * 实现单个burl请求返回数据结合功能.
 * {@link FilterModule#constructRemoteModule(int, FilterUser, HttpGetInterface)}
 * Created by poe on 28/04/17.
 */

public class RemoteFilterBuilder implements DataBuilder<FilterCell,List<FilterCell>> {

    private String url;

    ParamBuilder paramBuilder;

    //    private RequestSender requestSender;
    private HttpGetInterface httpGetInterface;
    /**
     * data revert .
     */
    private BuildListener<List<FilterCell>> buildListener;

    public RemoteFilterBuilder() {
    }

    public RemoteFilterBuilder(String url, HttpGetInterface requestSender) {
        this.url = url;
        this.httpGetInterface = requestSender;
    }

    @Override
    public void build(final FilterCell param, BuildListener<List<FilterCell>> listener) {
        this.buildListener = listener;
        // 非空校验
        if (null == httpGetInterface)
            throw new RuntimeException("HttpGetInterface has not be init !");
        if (null == url) throw new RuntimeException("url can not be null!");
        if (null == paramBuilder) throw new RuntimeException("paramBuilder can not be null!");
        //emit net request .
        Map<String, String> params = paramBuilder.getParams();
        if (null == param) params = new HashMap<>();

        httpGetInterface.sendRequest(url, params, new HttpGetInterface.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                parseJson(response, param);
            }
        }, new HttpGetInterface.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                if (null != buildListener) buildListener.onError(error);
            }
        });
    }

    /**
     * 处理返回的json 并完成回调
     * 如果error或者返回结果为空返回错误.
     *
     * @param response
     */
    private void parseJson(JSONObject response, FilterCell param) {
        FilterParse parse = new Gson().fromJson(response.toString(), FilterParse.class);
        if (null != parse && parse.getResult().equals("success")) {
            List<Filter> filters = parse.getList();
            List<FilterCell> mData = new ArrayList<>();
            //all
            FilterCell all = param.clone();
            all.setName(FilterConstants.STR_ALL);
            all.setParent(param);
            all.setCheck(true);
            mData.add(all);

            if (null != filters && filters.size() > 0) {
                for (Filter filter : filters) {
                    FilterCell fe = param.clone();
                    fe.setName(filter.getName());
                    fe.setId(filter.getId());
                    fe.setParent(param);
                    fe.setCheck(false);
                    mData.add(fe);
                }

            }
            //直属校
            if (null != parse.getHasDirect() && parse.getHasDirect().equals("Y")) {
                FilterCell school = param.clone();
                school.setName(FilterConstants.STR_SCHOOL_DIRECT);
                school.setParent(param);
                school.setCheck(false);
                mData.add(school);
            }

            // 不管结果是否为空都返回success, 上层去判断如何处理.
            if (null != buildListener) buildListener.onSuccess(parse.getLevelName(), mData);
        } else {
            if (null != buildListener) buildListener.onError(new Throwable("result error !"));
        }
    }

    public BuildListener<List<FilterCell>> getBuildListener() {
        return buildListener;
    }

    public void setBuildListener(BuildListener<List<FilterCell>> buildListener) {
        this.buildListener = buildListener;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ParamBuilder getParamBuilder() {
        return paramBuilder;
    }

    public void setParamBuilder(ParamBuilder paramBuilder) {
        this.paramBuilder = paramBuilder;
    }

    public HttpGetInterface getHttpGetInterface() {
        return httpGetInterface;
    }

    public void setHttpGetInterface(HttpGetInterface httpGetInterface) {
        this.httpGetInterface = httpGetInterface;
    }
}
