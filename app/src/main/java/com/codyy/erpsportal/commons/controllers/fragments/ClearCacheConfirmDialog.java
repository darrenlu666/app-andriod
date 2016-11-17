package com.codyy.erpsportal.commons.controllers.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.codyy.erpsportal.R;

/**
 * 清除缓存确认
 */
public class ClearCacheConfirmDialog extends DialogFragment {
	
	private OnClickListener mOnOkClickListener;
	
	public static ClearCacheConfirmDialog newInstance(OnClickListener onOkClickListener) {
		ClearCacheConfirmDialog fragment = new ClearCacheConfirmDialog();
        fragment.setOnOkClickListener( onOkClickListener);
		return fragment;
	}
	
    public ClearCacheConfirmDialog(){}

    public void setOnOkClickListener(OnClickListener onOkClickListener) {
        this.mOnOkClickListener = onOkClickListener;
    }

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.sure_to_clear_cache)
			.setPositiveButton( android.R.string.yes, mOnOkClickListener)
			.setNegativeButton( android.R.string.cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismissAllowingStateLoss();
				}
			});
		Dialog dialog = builder.create();
		return dialog;
	}
}
