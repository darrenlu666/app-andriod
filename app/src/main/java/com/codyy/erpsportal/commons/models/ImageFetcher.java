package com.codyy.erpsportal.commons.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.SettingActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.homepage.SimpleControllerListener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.NetworkUtils;
import com.codyy.erpsportal.commons.utils.UriUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;

/**
 * 图片抓取器，会根据网络状态与设置判断是否获取图片
 * Created by gujiajia on 2015/6/1.
 */
public class ImageFetcher {

    private final static String TAG = "ImageFetcher";

    public boolean mShowImage;

    private static ImageFetcher sInstance;

    private ImageFetcher(Context context) {
        updateState(context.getApplicationContext());
    }

    public static ImageFetcher getInstance(View view) {
        if (sInstance == null) {
            sInstance = new ImageFetcher(view.getContext());
        }
        return sInstance;
    }

    public static ImageFetcher getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ImageFetcher(context);
        }
        return sInstance;
    }

    /**
     * 抓取缩略图，会自动给图片链接插入.small
     * 如http://your.image.url/imageName.png会转为
     *   http://your.image.url/imageName.small.png
     *
     * @param dv 显示图片的DraweeView
     * @param imageUrl 图片原始链接
     */
    public void fetchSmall(DraweeView dv, String imageUrl) {
        /*if (dv == null) return;
        if (mShowImage && imageUrl!=null && imageUrl.trim().length() > 0) {
            if (loadAnimatingGif(dv, imageUrl)) return;
            String uriStr = UriUtils.buildSmallImageUrl(imageUrl);
            if (!TextUtils.isEmpty(uriStr)) {
                dv.setImageURI(Uri.parse(uriStr));
                return;
            }
        }
        dv.setImageURI(null);*/
        fetchSmall(dv,imageUrl,true);
    }

    /**
     * 抓取缩略图，会自动给图片链接插入.smalll,
     * 如http://your.image.url/imageName.png会转为
     *  http://your.image.url/imageName.small.png
     *
     * @param dv 显示图片的DraweeView
     * @param imageUrl 图片原始链接
     */
    public void fetchSmall(DraweeView dv, String imageUrl,boolean isLoadGif) {
        if (dv == null) return;
        if (mShowImage && imageUrl!=null && imageUrl.trim().length() > 0) {
            if (isLoadGif && loadAnimatingGif(dv, imageUrl)) return;
            String uriStr = UriUtils.buildSmallImageUrl(imageUrl);
            if (!TextUtils.isEmpty(uriStr)) {
                dv.setImageURI(Uri.parse(uriStr));
                return;
            }
        }
        dv.setImageURI(null);
    }

    /**
     * 加载失败自动设置默认图片
     * @param dv
     * @param imageUrl
     * @param resId
     * @param isLoadGif
     */
    public void fetchSmallWithDefault(final DraweeView dv, String imageUrl, @DrawableRes final int resId, boolean isLoadGif) {
        if (dv == null) return;
        if (mShowImage && imageUrl!=null && imageUrl.trim().length() > 0) {
            if (isLoadGif && loadAnimatingGif(dv, imageUrl)) return;
            String uriStr = UriUtils.buildSmallImageUrl(imageUrl);
            if (!TextUtils.isEmpty(uriStr)) {
                DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                        .setUri(uriStr)
                        .setTapToRetryEnabled(true)
                        .setOldController(dv.getController())
                        .setAutoPlayAnimations(isLoadGif)
                        .setControllerListener(new SimpleControllerListener() {
                            @Override
                            public void onFailure(String id, Throwable throwable) {
                                dv.setImageResource(resId);
                            }
                        })
                        .build();

                dv.setController(draweeController);

                return;
            }
        }
        dv.setImageResource(resId);
    }

    /**
     * 抓取图片
     * @param dv 显示图片的DraweeView
     * @param url 图片链接
     */
    public void fetchImage( DraweeView dv, String url) {
        if (mShowImage && !TextUtils.isEmpty(url) && !url.endsWith("null")) {
            Cog.d(TAG, "fetchImage url=", url);
            if (loadAnimatingGif(dv, url)) return;
            dv.setImageURI( Uri.parse(url));
        } else {
            dv.setImageURI( null);
        }
    }

    /**
     * 尝试加载gif动图
     * @param dv
     * @param imageUrl
     * @return
     */
    private boolean loadAnimatingGif(DraweeView dv, String imageUrl) {
        Cog.d(TAG, "loadAnimatingGif imageUrl=", imageUrl);
        if (imageUrl.endsWith(".gif")) {
            DraweeController oldController = dv.getController();
            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setOldController(oldController)
                    .setUri(Uri.parse(imageUrl))
                    .setAutoPlayAnimations(true)
                    .build();
            dv.setController(draweeController);
            return true;
        }
        return false;
    }

    /**
     * 当前是否应该加载图片
     * @return
     */
    public boolean needShowImage() {
        return mShowImage;
    }

    /**
     * 更新状态
     */
    public void updateState(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SettingActivity.SHARE_PREFERENCE_SETTING, Context.MODE_PRIVATE);
        boolean showImageWifiOnly = sp.getBoolean(SettingActivity.KEY_IMAGE_WIFI_ONLY, false);
        updateState(context, showImageWifiOnly);
    }

    /**
     * 更新状态
     * @param showImageWifiOnly 是否只在wifi下获取图片
     */
    public void updateState(Context  context, boolean showImageWifiOnly) {
        if (showImageWifiOnly) {
            mShowImage = NetworkUtils.isNetWorkTypeWifi(context);
        } else {
            mShowImage = true;
        }
    }
}
