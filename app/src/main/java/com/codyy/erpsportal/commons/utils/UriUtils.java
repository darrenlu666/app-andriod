package com.codyy.erpsportal.commons.utils;


import com.codyy.url.URLConfig;

/**
 * Created by gujiajia on 2015/4/28.
 */
public class UriUtils {

    /**
     * 获取小图链接
     *
     * @param imageName
     * @return
     */
    public static String obtainSmallImageUrl(String imageName) {
        StringBuilder sb = new StringBuilder(URLConfig.IMAGE_URL);
        int dotIndex = imageName.lastIndexOf('.');
//        String suffix = imageName.substring(dotIndex);//like .jpg .png
//        sb.append(imageName.substring(0, dotIndex));
//        return sb.append(".small").append(suffix).toString();

        String suffix;
        if (dotIndex > 0) {
            suffix = imageName.substring(dotIndex);//like .jpg .png
            sb.append(imageName.substring(0, dotIndex));
            return sb.append(".small").append(suffix).toString();
        } else {
            sb.append(imageName);
            return sb.toString();
        }
    }

    public static String buildSmallImageUrl(String imageUrl) {
        int dotIndex = imageUrl.lastIndexOf('.');
        if (dotIndex > 0) {
            String suffix = imageUrl.substring(dotIndex);//like .jpg .png
            return imageUrl.substring(0, dotIndex) + ".small" + suffix;
        } else {
            return imageUrl;
        }
    }

    public static String getSmall(String pic) {
        StringBuilder stringBuilder = new StringBuilder(pic);
        int a = stringBuilder.lastIndexOf(".");
        if (a > 0) {
            return stringBuilder.insert(stringBuilder.lastIndexOf("."), ".small").toString();
        }
        return pic;
    }

    public static String getImageUrl(String name) {
        return URLConfig.IMAGE_URL + name;
    }
}
