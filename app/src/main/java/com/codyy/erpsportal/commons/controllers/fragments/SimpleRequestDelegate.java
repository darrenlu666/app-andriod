package com.codyy.erpsportal.commons.controllers.fragments;

/**
 * Created by poe on 3/8/17.
 */
import android.view.View;
import android.view.ViewGroup;

import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;

/**
 * 一次网络请求所需的最少接口
 */
public interface SimpleRequestDelegate<T> {
    /** 请求url**/
    String obtainAPI();
    /** 获取参数集合　**/
    HashMap<String, String> getParams() ;
    /**解析服务器返回的数据，可能是刷新，也可能是更多 **/
    void parseData(JSONObject response ,List<T> dataSource);
    /** 获取viewHolder**/
    BaseRecyclerViewHolder<T> getViewHolder(ViewGroup parent);
    /**ItemClickListener**/
    void OnItemClicked(View v, int position, T data);
    /** 获取最大上限 **/
    int getTotal();
}
