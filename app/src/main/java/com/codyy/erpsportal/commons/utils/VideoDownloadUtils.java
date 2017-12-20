package com.codyy.erpsportal.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.dao.CacheDao;
import com.codyy.erpsportal.commons.models.entities.CacheItem;
import com.codyy.erpsportal.resource.models.entities.ResourceDetails;
import com.codyy.erpsportal.commons.services.FileDownloadService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 视频下载类
 * Created by gujiajia on 2015/5/3.
 */
public class VideoDownloadUtils {

    private static CacheDao sCacheDao;


    /**
     * 下载本地文件 .
     * 如果存在 给出提示 ,否则开始下载 ！
     * @param resourceDetails
     */
    public static boolean downloadVideo(ResourceDetails resourceDetails, String downloadUrl , String baseUserId){
        boolean result = false;
        if( null == resourceDetails || null == resourceDetails.getId() || TextUtils.isEmpty(downloadUrl)){
            ToastUtil.showToast(EApplication.instance(), EApplication.instance().getString(R.string.txt_dialog_download_error));
        } else {
            String resId = resourceDetails.getId();
            if (FileDownloadService.hasCached(baseUserId, resId + ".mp4")) {
                ToastUtil.showToast(EApplication.instance(), EApplication.instance().getString(R.string.video_downloaded_already));
                if (!checkMyDownloadResource(resourceDetails, baseUserId)) {
                    createAndInsertCacheItem(resourceDetails, CacheItem.DOWNLOAD_TYPE_VIDEO, downloadUrl, baseUserId);
                }
            } else if (TransferManager.instance().isDownloading(resId)){
                ToastUtil.showToast(EApplication.instance(), EApplication.instance().getString(R.string.txt_dialog_download_exist_downloading));
                if (!checkMyDownloadResource(resourceDetails, baseUserId)) {
                    createAndInsertCacheItem(resourceDetails, CacheItem.DOWNLOAD_TYPE_VIDEO, downloadUrl, baseUserId);
                }
            } else if (!VideoDownloadUtils.isConnected(EApplication.instance())){
                ToastUtil.showToast(EApplication.instance(),EApplication.instance().getString(R.string.net_error));
            } else if(!NetworkUtils.isDownloadEnable(EApplication.instance())){
                ToastUtil.showToast(EApplication.instance(),EApplication.instance().getString(R.string.net_switch_close));
            } else {
                ToastUtil.showToast(EApplication.instance(), EApplication.instance().getString(R.string.add_download_successfully));
                createAndInsertCacheItem(resourceDetails, CacheItem.DOWNLOAD_TYPE_VIDEO, downloadUrl, baseUserId);
                FileDownloadService.httpDownload(EApplication.instance(), baseUserId);
                result = true;
            }
        }
        return result;
    }

    /**
     * 下载音频
     * @param resourceDetails
     * @param downloadUrl
     * @param baseUserId
     */
    public static boolean downloadAudio(ResourceDetails resourceDetails, String downloadUrl , String baseUserId) {
        boolean result = false;
        if(null == resourceDetails || null == resourceDetails.getId()|| TextUtils.isEmpty(downloadUrl)){
            ToastUtil.showToast(EApplication.instance(),EApplication.instance().getString(R.string.txt_dialog_download_error));
        }else{
            String resId = resourceDetails.getId();
            if (FileDownloadService.hasCached(baseUserId, resId + ".mp3")) {
                ToastUtil.showToast(EApplication.instance(), EApplication.instance().getString(R.string.audio_downloaded_already));
                if (!checkMyDownloadResource(resourceDetails, baseUserId)) {
                    createAndInsertCacheItem(resourceDetails, CacheItem.DOWNLOAD_TYPE_AUDIO, downloadUrl, baseUserId);
                }
            } else if (TransferManager.instance().isDownloading(resId)){//是否已经在下载中了
                ToastUtil.showToast(EApplication.instance(), EApplication.instance().getString(R.string.txt_dialog_download_exist_downloading));
                if (!checkMyDownloadResource(resourceDetails, baseUserId)) {
                    createAndInsertCacheItem(resourceDetails, CacheItem.DOWNLOAD_TYPE_AUDIO, downloadUrl, baseUserId);
                }
            } else if (!VideoDownloadUtils.isConnected(EApplication.instance())){
                ToastUtil.showToast(EApplication.instance(), EApplication.instance().getString(R.string.net_error));
            } else if(!NetworkUtils.isDownloadEnable(EApplication.instance())){
                ToastUtil.showToast(EApplication.instance(), EApplication.instance().getString(R.string.net_switch_close));
            } else {
                ToastUtil.showToast(EApplication.instance(), EApplication.instance().getString(R.string.add_download_successfully));
                createAndInsertCacheItem(resourceDetails, CacheItem.DOWNLOAD_TYPE_AUDIO, downloadUrl, baseUserId);
                FileDownloadService.httpDownload(EApplication.instance(), baseUserId);
                result = true;
            }
        }
        return result;
    }

    /**
     * 添加图片缓存项
     * @param resourceDetails
     * @param baseUserId
     */
    public static void addImageCacheItem(ResourceDetails resourceDetails, String baseUserId) {
        if (sCacheDao == null) {
            sCacheDao = new CacheDao(EApplication.instance());
        }
        String downloadUrl = resourceDetails.getDownloadUrl();
        int lastIndexOfDot = downloadUrl.lastIndexOf('.');
        String suffix = downloadUrl.substring(lastIndexOfDot);
        CacheItem ci = new CacheItem(resourceDetails);
        ci.setType(CacheItem.DOWNLOAD_TYPE_IMAGE);
        ci.setSuffix(suffix);
        ci.setDownloadUrl(downloadUrl);
        ci.setBaseUserId(baseUserId);
        ci.setState(CacheItem.STATE_COMPLETE);
        ci.setProgress(100);
        sCacheDao.insert(ci);
    }

    private static void createAndInsertCacheItem(ResourceDetails resourceDetails, String downloadType,
                                                 String downloadUrl, String baseUserId) {
        if (sCacheDao == null) {
            sCacheDao = new CacheDao(EApplication.instance());
        }

        CacheItem ci = new CacheItem(resourceDetails);
        //设置资源类型...
        ci.setType(downloadType);
        if (CacheItem.DOWNLOAD_TYPE_AUDIO.equals(downloadType)) {
            ci.setSuffix(".mp3");
        } else {
            ci.setSuffix(".mp4");
        }
        //设置新的下载地址...解决文件名为中文问题
        if(downloadUrl.contains("=") && downloadUrl.length() > downloadUrl.indexOf("=")){
            String head = downloadUrl.substring(0 , downloadUrl.lastIndexOf("=")+1);
            String file = downloadUrl.substring(downloadUrl.lastIndexOf("=")+1);
            try {
                file = URLEncoder.encode(file, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            downloadUrl = head + file ;
        }
        ci.setDownloadUrl(downloadUrl);
        ci.setBaseUserId(baseUserId);
        sCacheDao.insert(ci);
    }

    /**
     * 检测视频是否在我的下载列表中，if not -> add one item .
     * @param baseUserId
     */
    private static boolean checkMyDownloadResource(ResourceDetails resourceDetails, String baseUserId) {
        if (null != resourceDetails) {
            if (sCacheDao == null) {
                sCacheDao = new CacheDao(EApplication.instance());
            }
            //存在我的用户下载记录
            if(sCacheDao.isExist(resourceDetails.getId() , baseUserId)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断网络是否连接
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager con=(ConnectivityManager)context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        NetworkInfo internetNetworkInfo = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean internet = internetNetworkInfo != null &&internetNetworkInfo.isConnectedOrConnecting();
        if(wifi|internet){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 将时间变为秒的格式
     *
     * @param time
     */
    public static String switchTime(int time) {
        int H = time / 3600;
        int M = (time % 3600) / 60;
        int S = (time % 3600) % 60;
        String HH, MM, SS;
        if (H < 10) {
            HH = "0" + H;
        } else {
            HH = "" + H;
        }
        if (M < 10) {
            MM = "0" + M;
        } else {
            MM = "" + M;
        }
        if (S < 10) {
            SS = "0" + S;
        } else {
            SS = "" + S;
        }
        return HH + ":" + MM + ":" + SS;
    }

}
