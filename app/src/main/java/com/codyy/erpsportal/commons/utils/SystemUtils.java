package com.codyy.erpsportal.commons.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

import com.codyy.erpsportal.EApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class SystemUtils {
    public static final String CACHE_FOLDER_NAME = "FileCaches";

    public static File getCacheDirectory() {
        File externalCacheDir = EApplication.instance().getExternalCacheDir();
        return FileUtils.createFolder(externalCacheDir, CACHE_FOLDER_NAME);
    }

    public static String getCachePath() {
        File externalCacheDir = EApplication.instance().getExternalCacheDir();
        if (externalCacheDir == null) return null;
        return externalCacheDir.getAbsolutePath() + File.separator + CACHE_FOLDER_NAME;
    }

    /**
     * 获取对应存储路径下的所有存在文件
     *
     * @return
     */
    public static List<String> getFilesByUserId(String userId) {
        File[] files = getFilePathList(userId);
        List<String> lists = new ArrayList<>();
        if (files != null && files.length > 0) {
            for (File file : files) {
                lists.add(file.getName());
            }
        }
        return lists;
    }

    public static String obtainCachedFile(String userId, String name) {
        return getCachePath() + File.separator + userId + File.separator + name;
    }

    public static File[] getFilePathList(String userId) {
        File parentFolder = SystemUtils.getCacheDirectory();
        File path = new File(parentFolder, userId);
        return path.listFiles();
    }

    /**
     * 得到屏幕的宽度
     *
     * @param aty
     * @return
     */
    public static int getScreenWidth(Activity aty) {
        DisplayMetrics metric = new DisplayMetrics();
        aty.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screenWidth = metric.widthPixels;  // 屏幕宽度（像素）
        return screenWidth;

    }

    /**
     * 得到屏幕的高度
     *
     * @param aty
     * @return
     */
    public static int getScreenHeight(Activity aty) {
        DisplayMetrics metric = new DisplayMetrics();
        aty.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screenHeight = metric.heightPixels;  // 屏幕宽度（像素）
        return screenHeight;
    }
}
