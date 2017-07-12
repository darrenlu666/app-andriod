/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.media;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * 提示
 * Created by gujiajia on 2017/6/6.
 */

public class RaiseUpToast extends Toast {

    private Context mContext;
    private TextView mTextView;

    public RaiseUpToast(Context context) {
        super(context);
        this.mContext = context;
        initStyle();
    }

    @Override
    public void setText(@StringRes int resId) {
        mTextView.setText(resId);
    }

    @Override
    public void setText(CharSequence s) {
        mTextView.setText(s);
    }

    private void initStyle() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.toast_raise_up, null);
        mTextView = (TextView) view.findViewById(R.id.toast_text);
        setView(view);
        setGravity(Gravity.CENTER, 0, 0);
        updateToastAnim();
    }

    private void updateToastAnim() {
        try {
            Toast father = this;
            Object mTN = getField(father, "mTN");
            if (mTN != null) {
                Object mParams = getField(mTN, "mParams");
                if (mParams != null && mParams instanceof WindowManager.LayoutParams) {
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
                    params.windowAnimations = R.style.ToastAnimation;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取对象的私有变量
     */
    public static Object getField(Object instance, String name) throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Object object = null;
        if (instance != null && !TextUtils.isEmpty(name)) {
            Field field = instance.getClass().getDeclaredField(name);
            if (field != null) {
                field.setAccessible(true);
                object = field.get(instance);
            }
        }
        return object;
    }
}
