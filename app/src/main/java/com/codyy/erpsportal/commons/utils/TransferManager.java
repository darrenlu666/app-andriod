/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011, 2012, FrostWire(TM). All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.codyy.erpsportal.commons.utils;

import android.content.Context;

import com.codyy.erpsportal.commons.services.HttpLargeDownload;
import com.codyy.erpsportal.commons.services.HttpLargeDownload.HttpDownloadListener;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.CacheDao;
import com.codyy.erpsportal.commons.models.dao.DownloadDao;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.download.Transfer;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public final class TransferManager {
    private static final String TAG = "TransferManager";

    private final List<HttpLargeDownload> mDownloads; // 下载集合

    private final Map<String, HttpDownloadListener> mListenerMap;

    private static TransferManager sInstance;

    public static TransferManager instance() {
        if (sInstance == null) {
            sInstance = new TransferManager();
        }
        return sInstance;
    }

    private TransferManager() {
        mDownloads = new Vector<>();
        mListenerMap = new ConcurrentHashMap<>();
    }

    public int getLargeDownloadsSize() {
        int size = 0;
        if (mDownloads != null) {
            size = mDownloads.size();
        }
        return size;
    }

    /**
     * 退出程序或者重新下载时将之前的线程释放
     */
    public void cancelDownloadTasks() {
        if (mDownloads != null) {
            for (int i = 0; i < mDownloads.size(); i++) {
                mDownloads.get(i).stopDownLoad();
            }
        }
    }

    /**
     * 停止下载并删除下载记录
     *
     * @param largeDownload
     */
    public void cancelDownloadTask(HttpLargeDownload largeDownload) {
        if (mDownloads != null) {
            if (largeDownload != null) {
                largeDownload.stopDownLoad();
                mDownloads.remove(largeDownload);
            }
        }
    }

    public void add(HttpLargeDownload download) {
        mDownloads.add( download);
        HttpDownloadListener listener = mListenerMap.remove(download.getResId());
        if (listener != null) {
            download.setListener( listener);
        }
    }

    public void remove(HttpLargeDownload download) {
        mDownloads.remove( download);
    }

    public static void addDownload(HttpLargeDownload download) {
        instance().add( download);
    }

    public static void removeDownload(HttpLargeDownload download) {
        instance().remove( download);
    }

    /**
     * 暂停下载任务
     *
     * @param resId
     */
    public void pauseDownload(String resId) {
        //cancel task
        for (HttpLargeDownload transfer : mDownloads) {
            if (transfer.getResId().equals(resId)) {
                cancelDownloadTask(transfer);
                break;
            }
        }
    }

    public boolean isDownloading(String resId) {
        boolean result = false;
        for (HttpLargeDownload transfer : mDownloads) {
            if (transfer.getResId().equals(resId)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 动态设置 监听
     *
     * @param resId
     * @param listener
     */
    public void setHttpListener(String resId, HttpDownloadListener listener) {
        for (HttpLargeDownload transfer : mDownloads) {
            if (transfer.getResId().equals(resId)) {
                transfer.setListener(listener);
                return;
            }
        }
        mListenerMap.put(resId, listener);
    }

    /**
     * 批量删除下载任务
     *
     * @param resIds
     * @param context
     */
    public void deleteDownloads(Context context, String userId, List<String> resIds) {
        CacheDao cacheDao = new CacheDao(context.getApplicationContext());
        UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
        if (cacheDao.batchDelete(resIds, userInfo.getBaseUserId())) {
            Iterator<HttpLargeDownload> iterator = mDownloads.iterator();
            while (iterator.hasNext()) {
                HttpLargeDownload transfer = iterator.next();
                if (resIds.contains(transfer.getResId())) {
                    transfer.stopDownLoad();
                    iterator.remove();
                }
            }
            DownloadDao.instance(context.getApplicationContext()).batchDeleteByFileName(userId, resIds);
            File parentFolder = new File(SystemUtils.getCacheDirectory(), userId);
            if (parentFolder.exists() && parentFolder.isDirectory()) {
                File[] files = parentFolder.listFiles();
                for (File file : files) {
                    for (String resId : resIds) {
                        if (file.getName().contains(resId)) {
                            file.delete();
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取 http大文件下载合集
     *
     * @return
     */
    public List<Transfer> getHttpTransfers() {
        getUnFinishedDownloads();
        List<Transfer> transfers = new ArrayList<>();
        if (mDownloads != null) {
            transfers.addAll(mDownloads);
        }
        return transfers;
    }

    public HttpLargeDownload downHttpLarge(final Context context, String url,
                                           String filename, String userId, String resId) {
        String cacheDirPath = SystemUtils.getCachePath();
        if (cacheDirPath == null) return null;
        HttpLargeDownload downloader = new HttpLargeDownload(url, filename
                , cacheDirPath + File.separator + userId, context, userId, resId);
        downloader.start();
        addDownload(downloader);
        return downloader;
    }

    private void getUnFinishedDownloads() {
        for (int i = 0; i < mDownloads.size(); i++) {
            HttpLargeDownload d = mDownloads.get(i);
            if (d.isComplete() || d.isError()) {
                mDownloads.remove(i);
            }
        }
    }

}
