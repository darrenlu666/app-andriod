package com.codyy.tpmp.filterlibrary.interfaces;

import java.util.Map;

/**
 * 数据获取接口
 * Created by poe on 11/05/17.
 */

public interface HttpGetInterface {

    /**
     * 获取数据
     * @param listener
     * @param errorListener
     */
    void sendRequest(String url, Map<String, String> param, Listener listener, ErrorListener errorListener);


    /**
     * 响应成功监听器
     * @param <T> 响应类型
     */
    interface Listener<T> {
        void onResponse(T response);
    }

    /**
     * 响应错误监听器
     */
    interface ErrorListener {
        void onErrorResponse(Throwable error);
    }
}
