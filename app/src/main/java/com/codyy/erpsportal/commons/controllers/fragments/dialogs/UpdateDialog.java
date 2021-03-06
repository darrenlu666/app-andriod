/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.commons.controllers.fragments.dialogs;

import android.Manifest.permission;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.engine.NewVersionDownloader;
import com.codyy.erpsportal.commons.models.engine.rxpermissions.RxPermissions;
import com.codyy.erpsportal.commons.utils.ToastUtil;

import java.io.File;

import io.reactivex.functions.Consumer;

/**
 * 更新Dialog
 * Created by gujiajia on 2017/5/15.
 */

public class UpdateDialog extends DialogFragment {

    private final static String ARG_FORCE = "ARG_FORCE";

    /**
     * 强制更新时是否可以退出
     */
    private final static String ARG_EXITABLE = "ARG_EXITABLE";

    private final static String ARG_URL = "ARG_URL";

    private final static int STATUS_NOT_STARTED = 0;

    private final static int STATUS_STARTED = 1;

    private final static int STATUS_FINISHED = 2;

    private TextView mTextTv;

    private Button mCloseBtn;

    private Button mUpdateBtn;

    private LinearLayout mBtnsContainerLl;

    /**
     * 是否强制更新
     */
    private boolean mForce;

    /**
     * 强制更新时是否可以点返回退出，默认可以
     */
    private boolean mExitable = true;

    private String mUrl;

    private BroadcastReceiver mBroadcastReceiver;

    /**
     * 下载状态：0未开始 1下载中 2完成
     */
    private int mDownloadStatus = STATUS_NOT_STARTED;

    public static UpdateDialog newInstance(boolean forceUpdate, String url) {
        return newInstance(forceUpdate, url, true);
    }

    public static UpdateDialog newInstance(boolean forceUpdate, String url, boolean exitable) {
        UpdateDialog updateDialog = new UpdateDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_FORCE, forceUpdate);
        bundle.putString(ARG_URL, url);
        bundle.putBoolean(ARG_EXITABLE, exitable);
        updateDialog.setArguments(bundle);
        return updateDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        if (getArguments() != null) {
            mForce = getArguments().getBoolean(ARG_FORCE);
            mUrl = getArguments().getString(ARG_URL);
            mExitable = getArguments().getBoolean(ARG_EXITABLE, true);
        }
        if (mForce) {
            setCancelable(false);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update, null);
        mTextTv = (TextView) view.findViewById(R.id.tv_text);
        mCloseBtn = (Button) view.findViewById(R.id.btn_close);
        mUpdateBtn = (Button) view.findViewById(R.id.btn_update);
        mBtnsContainerLl = (LinearLayout) view.findViewById(R.id.ll_btns_container);
        if (mForce) {
            mTextTv.setText(R.string.new_version_detected_please_update);
            mCloseBtn.setVisibility(View.GONE);
        } else {
            mTextTv.setText(R.string.new_version_detected_do_update);
        }
        mCloseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mUpdateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDownloadStatus == STATUS_FINISHED) {
                    File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File apkFile = new File(file, "ErpsPortal.apk");
                    if (apkFile.exists()) {//apk下载完成，且文件存在
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setDataAndType(Uri.parse("file://" + apkFile.getAbsolutePath()),
                                "application/vnd.android.package-archive");
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(i);
                        return;
                    }
                }
                RxPermissions rxPermissions = new RxPermissions(getFragmentManager());
                rxPermissions.request(permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    startToDownload();
                                } else {
                                    ToastUtil.showToast(getContext(), R.string.cant_download_for_no_permission);
                                }
                            }
                        });
//                startToDownload();
            }
        });
        builder.setView(view);
        Dialog dialog = builder.create();
        return dialog;
    }

    public void startToDownload() {
        long downloaderId = NewVersionDownloader.download(getContext(), mUrl);
        mDownloadStatus = STATUS_STARTED;
        if (mForce) {
            listener(downloaderId);
            mTextTv.setText(R.string.new_version_downloading);
            mBtnsContainerLl.setVisibility(View.GONE);
        } else {
            dismiss();
        }
    }

    private void listener(final long downloaderId) {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //下载完成了会自动跳到安装界面
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloaderId) {
                    mDownloadStatus = STATUS_FINISHED;
                }
            }
        };

        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mForce) {
            if (mExitable) {
                getDialog().setOnKeyListener(new OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK
                                && mDownloadStatus != STATUS_STARTED) {//正在下载时不让用户退出，否则无法下次进入无法同步状态
                            getActivity().finish();
                            return true;
                        }
                        return false;
                    }
                });
            }

            if(mDownloadStatus == STATUS_FINISHED) {
                mTextTv.setText(R.string.new_version_detected_please_update);
                mBtnsContainerLl.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mForce && mBroadcastReceiver!= null) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
    }
}
