package com.codyy.erpsportal.commons.services;

import android.content.Context;
import android.text.TextUtils;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.SystemUtils;
import com.codyy.erpsportal.commons.utils.TransferManager;
import com.codyy.erpsportal.commons.models.dao.CacheDao;
import com.codyy.erpsportal.commons.models.dao.DownloadDao;
import com.codyy.erpsportal.commons.models.entities.CacheItem;
import com.codyy.erpsportal.commons.models.entities.download.Transfer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileDownloadService {
    private static final String TAG = "FileDownloadService";

    public static void httpDownload(Context context, String userId) {
        List<CacheInfo> cacheInfoList = getAllCacheInfos(userId);//db里面的要下载的数据文件
        if (cacheInfoList == null || cacheInfoList.size() == 0) return;
        Cog.d(TAG, "TransferManager.instance().getLargeDownloadsSize()----->" + TransferManager.instance().getLargeDownloadsSize());
        List<CacheInfo> needDownloadCacheInfos = getDownloadFiles(userId, cacheInfoList);
        if (needDownloadCacheInfos.size() > 0) {
            httpDownload(context, needDownloadCacheInfos);
        }
    }

    /**
     * 筛选出 未下载的 、不重复的素材
     *
     * @param cacheInfos
     * @return
     */
    private static List<CacheInfo> getDownloadFiles(String userId, List<CacheInfo> cacheInfos) {
        List<CacheInfo> maps = FileDownloadService.getDifferentFile(cacheInfos, SystemUtils.getFilesByUserId(userId));
        return maps;
    }

    private static void httpDownload(Context context, List<CacheInfo> downloadFiles) {
        for (CacheInfo cacheInfo : downloadFiles) {
            String fileName = cacheInfo.getFileName();
            String downloadUrl = cacheInfo.getDownloadUrl();
            String resId = cacheInfo.getResId();
            if (TextUtils.isEmpty(downloadUrl)) {
                return;
            }

            getOneLargerDownload(context, cacheInfo);
        }
    }

    public static boolean getOneLargerDownload(Context context, String fileName, String downloadUrl,String userId, String resId) {
        CacheInfo cacheInfo = new CacheInfo(fileName, downloadUrl, "0", userId, resId);
        return getOneLargerDownload(context, cacheInfo);
    }

    /**
     * 开启一个新的下载
     *
     * @param context
     * @return boolean 是否可以下载
     */
    public synchronized static boolean getOneLargerDownload(Context context, CacheInfo cacheInfo) {
        boolean canDownload;
        List<Transfer> transfers = TransferManager.instance().getHttpTransfers();
        Cog.d(TAG, "getOneLargerDownload:transferSize=", transfers.size());

        int count = Engine.instance().getThreadPool().getActiveCount();
        if (count < 5) {
            canDownload = true;
            if (buildHttpTransferExist(transfers, cacheInfo)) {
                Cog.d(TAG, "getOneLargerDownload:filename=", cacheInfo.getFileName(), " is downloading");
            } else {
                Cog.d(TAG, "getOneLargerDownload:new http large downloaded");
                TransferManager.instance().downHttpLarge(context, cacheInfo.getDownloadUrl(),
                        cacheInfo.getFileName(), cacheInfo.getUserId(),cacheInfo.getResId());
                Cog.d(TAG, "getOneLargerDownload:filename=", cacheInfo.getFileName(), " is creating");
            }
        } else {
            //暂时等待
            canDownload = false;
            Cog.d(TAG,"当前下载已经超过5个，本次下载等待中.....");
        }

        return canDownload;
    }

    /**
     * 要建立的下载filename 是否 已经存在 了的下载
     *
     * @param transfers
     * @return
     */
    private static boolean buildHttpTransferExist(List<Transfer> transfers, CacheInfo cacheInfo) {
        Cog.d(TAG, "buildHttpTransferExist:filename：" + cacheInfo.getFileName());

        boolean result = false;
        Cog.d(TAG, "buildHttpTransferExist:transfers size:" + transfers.size());
        if (transfers.size() > 0) {
            for (Transfer transfer : transfers) {
                HttpLargeDownload http = (HttpLargeDownload) transfer;
                if (http.getDisplayName() == null) {
                    continue;
                }
                Cog.d(TAG, "buildHttpTransferExist:httpName：" + http.getDisplayName());
                if (cacheInfo.getFileName().equals(http.getDisplayName())) {
                    Cog.i(TAG, "buildHttpTransferExist:name：" + http.getDisplayName() + ", progress：" + http.getProgress());
                    if (http.getProgress() > 100) {
                        TransferManager.instance().cancelDownloadTask(http);
                        DownloadDao.instance(EApplication.instance()).delete(cacheInfo.getUserId(), cacheInfo.getResId());
                    }
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * 获取所有需要下载的素材(包含已下载的）
     *
     * @return
     */
    public static List<CacheInfo> getAllCacheInfos(String userId) {
        CacheDao cacheDao = new CacheDao(EApplication.instance());
        List<CacheItem> cacheItemList = cacheDao.getDownloadingData(userId);
        List<CacheInfo> cacheInfoList = new ArrayList<>(cacheItemList.size());

        for (int i = 0; i < cacheItemList.size(); i++) {
            CacheItem cacheItem = cacheItemList.get(i);
            Cog.i(TAG, cacheItem.getName() + "  type :" + cacheItem.getType());
            //if it is pause or pending to download
            cacheInfoList.add(new CacheInfo(cacheItem.getId() + cacheItem.getSuffix()
                    , cacheItem.getDownloadUrl()
                    , cacheItem.getResSize() + ""
                    , userId
                    , cacheItem.getId()));
        }
        return cacheInfoList;
    }

    /**
     * 获取存储中没有的缓存文件的对应的缓存信息。通过文件名匹配
     *
     * @param cacheInfos 缓存信息列表
     * @param fileNameList 需要比较的文件名列表 已经下载好的所有文件
     *
     * @return 所有未下载完成的素材列表
     */
    public static List<CacheInfo> getDifferentFile(List<CacheInfo> cacheInfos, List<String> fileNameList) {
        List<CacheInfo> newCacheInfos = new ArrayList<>();
        for (CacheInfo cacheInfo : cacheInfos) {
            String fileName = cacheInfo.getFileName();
            if (!newCacheInfos.contains(cacheInfo)) {//取不重复的map
                if (!fileNameList.contains(fileName)) {//未下载完成的、添加下载信息
                    newCacheInfos.add(cacheInfo);
                }
            }
        }
        return newCacheInfos;
    }

    /**
     * 判断文件是否已经下载过了
     * !!!please add an item in CacheDao if it does exits under the baseUserId  if you want to avoid redownload the video !
     *
     * @param resId 资源id
     * @return
     */
    public static boolean hasMp4Downloaded(String userId, String resId) {
        String fileName = resId + ".mp4";
        return hasCached(userId, fileName);
    }

    public static boolean hasMp3Downloaded(String userId, String resId) {
        String fileName = resId + ".mp3";
        return hasCached(userId, fileName);
    }

    public static boolean hasCached(String userId, String fileName) {
        String cachedFilePath = SystemUtils.obtainCachedFile(userId, fileName);
        Cog.d(TAG, "hasCached cachedFilePath=", cachedFilePath);
        File cachedFile = new File(cachedFilePath);
        return cachedFile.exists();
    }

    /**
     * 根据resID返回本地下载的文件路径
     *
     * @param resId
     * @return
     */
    public static String getCachedMp4File(String userId, String resId) {
        String fileName = resId + ".mp4";
        return getCachedFile(userId, fileName);
    }

    public static String getCachedFile(String userId, String fileName) {
        String userCachePath = SystemUtils.getCachePath() + File.separator + userId;
        return new File(userCachePath, fileName).getAbsolutePath();
    }

    public static File obtainCachedFile(String userId, String fileName) {
        String userCachePath = SystemUtils.getCachePath() + File.separator + userId;
        return new File(userCachePath, fileName);
    }
}
