package com.codyy.erpsportal.commons.controllers.fragments.dialogs;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.codyy.erpsportal.R;

/**
 * 确认Dialog
 * Created by gujiajia on 2017/5/8.
 */
public class ConfirmTextDialog extends DialogFragment {

    private final static String ARG_TEXT = "ARG_TEXT";

    private String mText = "";

    private OnConfirmListener mOnConfirmListener;

    public static ConfirmTextDialog newInstance(String text) {
        ConfirmTextDialog confirmTextDialog = new ConfirmTextDialog();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        confirmTextDialog.setArguments( bundle);
        return confirmTextDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        if (getArguments() != null) {
            mText = getArguments().getString(ARG_TEXT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_text, null);
        builder.setView(view);
        TextView textTv = (TextView) view.findViewById(R.id.tv_text);
        Button cancelBtn = (Button) view.findViewById(R.id.btn_cancel);
        Button confirmBtn = (Button) view.findViewById(R.id.btn_confirm);
        textTv.setText(mText);
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        confirmBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnConfirmListener != null) {
                    mOnConfirmListener.onConfirm();
                }
            }
        });
        return builder.create();
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        mOnConfirmListener = onConfirmListener;
    }

    public interface OnConfirmListener {
        void onConfirm();
    }
}
