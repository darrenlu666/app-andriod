package com.codyy.erpsportal.commons.models.engine;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;

/**
 * 新版本下载器
 * Created by gujiajia on 2017/5/16.
 */

public class NewVersionDownloader {

    public static long download(Context context, String url) {
        Cog.d("NewVersionDownloader", "+downloadNewVersion url=" + url);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("新版本下载中…");
        request.setTitle(context.getString(R.string.app_name));

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "ErpsPortal.apk");
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        return manager.enqueue(request);
    }

}
