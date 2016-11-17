package com.codyy.erpsportal.weibo.controllers.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.ProgressBarView;

/**
 * Created by kmdai on 16-4-14.
 */
public class WeiBoUpVideoDialogFragment extends DialogFragment {
    private ProgressBarView mProgressBarView;
    private OnCancel mOnCancel;
    private TextView mProgressTV;
    private int mShowNB;
    private int mAllNB;

    public static WeiBoUpVideoDialogFragment newInstance() {
        Bundle args = new Bundle();
        WeiBoUpVideoDialogFragment fragment = new WeiBoUpVideoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnCancel(OnCancel onCancel) {
        mOnCancel = onCancel;
        setCancelable(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(UIUtils.dip2px(getActivity(), 250), UIUtils.dip2px(getActivity(), 150));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.TransDialog);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.weibo_upvideo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBarView = (ProgressBarView) view.findViewById(R.id.up_dailog_progress);
        mProgressBarView.setCanClick(false);
        mProgressTV = (TextView) view.findViewById(R.id.weibo_upvideo_progress);
        mProgressBarView.setMax(100);
        mProgressTV.setText(mShowNB + "/" + mAllNB);
        view.findViewById(R.id.weibo_upvideo_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCancel != null) {
                    mOnCancel.cancel();
                    dismiss();
                }
            }
        });
    }

    public void upProgress(int progress) {
        if (mProgressBarView != null) {
            mProgressBarView.setProgress(progress);
        }
    }

    public void setProgress(int now, int all) {
        mShowNB = now;
        mAllNB = all;
        if (mProgressTV != null) {
            mProgressTV.setText(mShowNB + "/" + mAllNB);
        }
    }

    public interface OnCancel {
        void cancel();
    }
}
