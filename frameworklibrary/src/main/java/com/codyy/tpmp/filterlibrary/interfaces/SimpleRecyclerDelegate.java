package com.codyy.tpmp.filterlibrary.interfaces;

import android.view.View;
import android.view.ViewGroup;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * 代理处理单个列表类数据接口集合
 * {@link com.codyy.tpmp.frameworklibrary.acitivities.BaseHttpActivity}
 * Created by poe on 3/6/17.
 */
public interface SimpleRecyclerDelegate<T> {
    /** 请求url**/
    String obtainAPI();
    /** 获取参数集合　**/
    HashMap<String, String> getParams(boolean isRefreshing) ;
    /**解析服务器返回的数据，可能是刷新，也可能是更多 **/
    void parseData(JSONObject response, boolean isRefreshing);
    /** 获取viewHolder**/
    BaseRecyclerViewHolder<T> getViewHolder(ViewGroup parent, int ViewHolderType);
    /**ItemClickListener**/
    void OnItemClicked(View v, int position, T data);
    /** 获取最大上限 **/
    int getTotal();
}
