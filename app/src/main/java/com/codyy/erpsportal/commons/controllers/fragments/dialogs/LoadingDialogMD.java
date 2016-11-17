package com.codyy.erpsportal.commons.controllers.fragments.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.codyy.erpsportal.R;

/**
 * Created by kmdai on 16-2-24.
 */
public class LoadingDialogMD extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.progress_dialog);
        return dialog;
    }
}
