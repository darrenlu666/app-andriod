/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;

import com.codyy.erpsportal.R;

/**
 * 加载中Dialog
 * Created by JiajiaGu on 2014/12/31.
 */
public class LoadingDialog extends DialogFragment {

    private OnCancelListener mOnCancelListener;

    private boolean mCancelable;

    public static LoadingDialog newInstance(@StringRes int resId) {
        LoadingDialog instance = new LoadingDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("stringId", resId);
        instance.setArguments(bundle);
        return instance;
    }

    public static LoadingDialog newInstance(boolean cancelable) {
        LoadingDialog instance = new LoadingDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("cancelable", cancelable);
        instance.setArguments(bundle);
        return instance;
    }

    public static LoadingDialog newInstance() {
        LoadingDialog instance = new LoadingDialog();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCancelable = getArguments() != null && getArguments().getBoolean("cancelable", false);
        setCancelable(mCancelable);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.TransDialog);
        dialog.setContentView(R.layout.dialog_loading);
        TextView msgTv = (TextView) dialog.findViewById(R.id.loading_text);
        Bundle bundle = getArguments();
        if (bundle != null) {
            int stringId = bundle.getInt("stringId");
            if (stringId != 0) {
                msgTv.setText(stringId);
            }
        }
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
             super.dismiss();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mOnCancelListener != null) {
            mOnCancelListener.onCancel();
        }
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        mOnCancelListener = onCancelListener;
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    public interface OnCancelListener {
        void onCancel();
    }
}
