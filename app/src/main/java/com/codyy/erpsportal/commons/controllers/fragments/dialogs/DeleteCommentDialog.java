package com.codyy.erpsportal.commons.controllers.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;

/**
 * 确认删除评论弹窗
 * Created by gujiajia on 2016/2/4.
 */
public class DeleteCommentDialog extends DialogFragment {

    private OnOkClickListener mOnOkClickListener;

    private final static String ARG_POSITION = "position";

    private final static String ARG_PROMPT = "prompt";

    private int mPosition;

    @StringRes
    private int mStringId;

    public static DeleteCommentDialog newInstance(int position, @StringRes int stringId) {
        DeleteCommentDialog deleteCommentDialog = new DeleteCommentDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        bundle.putInt(ARG_PROMPT, stringId);
        deleteCommentDialog.setArguments(bundle);
        return deleteCommentDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnOkClickListener) {
            mOnOkClickListener = (OnOkClickListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnOkClickListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt(ARG_POSITION);
        mStringId = getArguments().getInt(ARG_PROMPT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(mStringId)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mOnOkClickListener != null) {
                            mOnOkClickListener.onOkClickListener(mPosition);
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null
        );
        return builder.create();
    }

    public interface OnOkClickListener {
        void onOkClickListener(int position);
    }

    public void setOnOkClickListener(OnOkClickListener listener) {
        mOnOkClickListener = listener;
    }
}
