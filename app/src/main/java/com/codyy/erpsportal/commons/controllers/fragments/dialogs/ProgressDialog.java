package com.codyy.erpsportal.commons.controllers.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.view.WindowManager;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.ProgressBarView;

/**
 * Created by kmdai on 16-2-27.
 */
public class ProgressDialog extends DialogFragment {
    private ProgressBarView mProgressBar;
    private Dialog mDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialog = new Dialog(getContext(), R.style.input_dialog);
        mDialog.setContentView(R.layout.up_dialog);
        mProgressBar = (ProgressBarView) mDialog.findViewById(R.id.up_dailog_progress);
        mProgressBar.setMax(100);
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = UIUtils.dip2px(getContext(), 100);
        lp.height = UIUtils.dip2px(getContext(), 100);
        window.setAttributes(lp);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return mDialog;
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.up_dialog, container, false);
//        mProgressBar = (ProgressBarView) view.findViewById(R.id.up_dailog_progress);
//        mProgressBar.setmax(100);
//        return view;
//    }

    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
    }
}
