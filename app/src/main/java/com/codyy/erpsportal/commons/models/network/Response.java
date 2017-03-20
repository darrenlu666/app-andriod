package com.codyy.erpsportal.commons.models.network;

/**
 * 响应
 * Created by gujiajia on 2017/1/25.
 */

public interface Response {

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
