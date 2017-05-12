package com.codyy.tpmp.filterlibrary.entities.interfaces;

/**
 * 数据构造器
 * @param <E> 输入参数
 * @param <T> 返回结果
 * Created by poe on 28/04/17.
 */
public interface DataBuilder<E extends Object,T extends Object> {

    /**
     * 开始构造
     * 1. 直接本地构造
     * 2. 通过网络请求构造
     * @return
     */
    void build(E param, BuildListener<T> listener);

    /**
     * 利用接口回调返回结果，
     * 异步实现，与Net网络接口同步.
     * @param <TT>
     */
    interface BuildListener<TT extends Object>{

        /**
         * 请求数据成功
         * @param levelName 当前模块的名称
         * @param result
         */
        void onSuccess(String levelName, TT result);

        /**
         * 请求数据错误.
         * @param t
         */
        void onError(Throwable t);
    }
}
