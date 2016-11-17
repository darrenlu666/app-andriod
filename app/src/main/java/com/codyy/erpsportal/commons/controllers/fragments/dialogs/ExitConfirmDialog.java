package com.codyy.erpsportal.commons.controllers.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.codyy.erpsportal.R;

/**
 * 退出选择框
 * Created by ldh on 2015/9/24.
 */
public class ExitConfirmDialog extends DialogFragment {

    public static ExitConfirmDialog newInstance(DialogInterface.OnClickListener onClickListener) {

        ExitConfirmDialog fragment = new ExitConfirmDialog();
        fragment.setOnClickListener(onClickListener);
        return fragment;
    }

    private DialogInterface.OnClickListener mOnClickListener;

    public void setOnClickListener(DialogInterface.OnClickListener onClickListener){
        mOnClickListener = onClickListener;
    }

    public ExitConfirmDialog(){}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.sure_to_exit).setPositiveButton(android.R.string.yes,mOnClickListener)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismissAllowingStateLoss();
                    }
                });

        Dialog dialog = builder.create();
        return dialog;
    }
}
