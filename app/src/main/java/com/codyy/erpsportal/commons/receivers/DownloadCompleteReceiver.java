package com.codyy.erpsportal.commons.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.codyy.erpsportal.commons.utils.Cog;


/**
 * 下载更新完成监听器
 * Created by gujiajia on 2015/5/19.
 */
public class DownloadCompleteReceiver extends BroadcastReceiver {

    private final static String TAG = "DownloadCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Cog.d(TAG, "+onReceive intent:" + intent.toString());
        if (context.getApplicationContext().getPackageName().equals(intent.getPackage())) {
            Cog.d(TAG, "@onReceive intent:" + intent.toString());
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor c = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));

            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
                    installApk(context, uri);
                } else {
                    int reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
                    Cog.e(TAG, "Download not correct, status [" + status + "] reason [" + reason + "]");
                }
            }
        }
    }

    /**
     * 发送
     * @param context
     * @param uri
     */
    private void installApk(Context context, Uri uri) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");// 向用户显示数据
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 以新压入栈
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
