/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;


/**
 * 发送消息弹出框
 * Created by gujiajia on 2015/4/27.
 */
public class SendMsgDialog extends DialogFragment implements View.OnClickListener {

    public OnSendClickListener mSendClickListener;

    private EditText mMsgText;

    private InputMethodManager mInputMethodManager;

    public static SendMsgDialog newInstance() {
        SendMsgDialog dialog = new SendMsgDialog();

        return dialog;
    }

    public SendMsgDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View popu = inflater.inflate(R.layout.dialog_input, null);
        mMsgText = (EditText) popu.findViewById(R.id.input_layout_editext);
        mMsgText.addTextChangedListener(watcher);
        popu.findViewById(R.id.input_layout_btn).setOnClickListener(this);
        Dialog commentDialog = new Dialog(getActivity(), R.style.input_dialog);
        commentDialog.setContentView(popu);
        Window window = commentDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        window.setGravity(Gravity.BOTTOM);
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
//        commentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                mInputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
//                mInputMethodManager.hideSoftInputFromWindow(mMsgText.getWindowToken(), 0);
//            }
//        });
        return commentDialog;
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() >= 150) {
                ToastUtil.showToast(getActivity(), "字数达到上限");
            }
        }
    };


    @Override
    public void onClick(View v) {
        String msg = mMsgText.getText().toString();
        if (TextUtils.isEmpty(msg)) {
            UIUtils.toast(getActivity(), "请输入内容！", Toast.LENGTH_SHORT);
            return;
        }
        if (mSendClickListener != null) {
            mSendClickListener.onSendClick(msg);
        }
    }

    public void setOnSendClickListener(OnSendClickListener onSendClickListener) {
        this.mSendClickListener = onSendClickListener;
    }

    public interface OnSendClickListener {
        void onSendClick(String msg);
    }
}
