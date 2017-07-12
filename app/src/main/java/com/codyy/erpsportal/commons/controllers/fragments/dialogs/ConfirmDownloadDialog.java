/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.codyy.erpsportal.commons.models.engine.NewVersionDownloader;


/**
 * 确认下载Dialog
 * Created by GuJiajia on 2015/5/19.
 */
public class ConfirmDownloadDialog extends DialogFragment {

    private final static String TAG = "ConfirmDownloadDialog";

    public final static String ARG_URL = "arg_url";

    public final static String ARG_VERSION = "arg_version";

    public String url;

    public String version;

    private OnCancelUpdateListener onOnCancelUpdateListener;

    public static ConfirmDownloadDialog newInstance(@NonNull String url,@NonNull String version) {
        ConfirmDownloadDialog dialog = new ConfirmDownloadDialog();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_URL, url);
        bundle.putString(ARG_VERSION, version);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCancelUpdateListener) {
            this.onOnCancelUpdateListener = (OnCancelUpdateListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        if (getArguments() != null) {
            this.url = getArguments().getString(ARG_URL);
            this.version = getArguments().getString(ARG_VERSION);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("检测到有新版，是否立即下载更新？");
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                url = "https://www.oschina.net/uploads/oschina-1.7.7.1.apk";
                NewVersionDownloader.download(getContext(), url);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onOnCancelUpdateListener != null)
                    onOnCancelUpdateListener.onCancelUpdate();
//                saveVersion();
            }
        });
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (onOnCancelUpdateListener != null)
            onOnCancelUpdateListener.onCancelUpdate();
    }

    public interface OnCancelUpdateListener {
        void onCancelUpdate();
    }
}
