package com.codyy.erpsportal.resource.utils;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.facebook.drawee.view.GenericDraweeView;

/**
 * Created by gujiajia on 2016/10/26.
 */

public class DraweeViewUtils {

    /**
     * 根据资源类型设置占位图
     * @param dv
     * @param type
     */
    public static void setPlaceHolderByResType(GenericDraweeView dv, String type) {
        if (Resource.TYPE_VIDEO.equals(type)){
            dv.getHierarchy().setPlaceholderImage(R.drawable.icon_default_video);
        } else {
            dv.getHierarchy().setPlaceholderImage(R.drawable.placeholder_img);
        }
    }
}
