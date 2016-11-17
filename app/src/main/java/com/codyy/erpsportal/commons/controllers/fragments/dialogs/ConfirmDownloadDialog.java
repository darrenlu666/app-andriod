package com.codyy.erpsportal.commons.controllers.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.codyy.erpsportal.commons.utils.Cog;


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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnCancelUpdateListener) {
            this.onOnCancelUpdateListener = (OnCancelUpdateListener) activity;
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("检测到有新版，是否立即下载更新？");
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                url = "https://www.oschina.net/uploads/oschina-1.7.7.1.apk";
                downloadNewVersion(url);
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
//        saveVersion();
    }

    /**
     * 如果取消了就保存版本号，到时再设置中显示
     */
//    private void saveVersion(){
//        Cog.d(TAG, "+saveVersion");
//        SharedPreferences sp = getActivity().getSharedPreferences(SettingActivity.SHARE_PREFERENCE_SETTING, Activity.MODE_PRIVATE);
//        sp.edit().putString(SettingActivity.KEY_NEW_VERSION_CODE, version).apply();
//    }

    private void downloadNewVersion(String url){
        Cog.d(TAG, "+downloadNewVersion url=" + url);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("新版本下载中…");
        request.setTitle("互动学习平台");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "ErpsPortal.apk");
        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        long id = manager.enqueue(request);
    }

    public interface OnCancelUpdateListener {
        void onCancelUpdate();
    }
}
