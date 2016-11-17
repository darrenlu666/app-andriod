package com.codyy.erpsportal.commons.utils;

import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * Created by eachann on 2015/7/7.
 * Handler 弱引用，防止Handler引起的内存泄漏，退出程序后，WeakHandler 将被优先扫描并回收内存
 */
//防止Activity内存溢出
public abstract class WeakHandler<T> extends Handler {
    private WeakReference<T> mOwner;

    public WeakHandler(T owner) {
        mOwner = new WeakReference<>(owner);
    }

    public T getOwner() {
        return mOwner.get();
    }
}
