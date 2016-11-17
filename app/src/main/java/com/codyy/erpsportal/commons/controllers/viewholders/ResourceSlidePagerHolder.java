package com.codyy.erpsportal.commons.controllers.viewholders;

import android.view.View;

import com.codyy.erpsportal.commons.widgets.infinitepager.SlidePagerHolder;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.codyy.erpsportal.resource.utils.DraweeViewUtils;

/**
 * 资源幻灯片单页的ViewHolder
 * Created by gujiajia on 2016/10/26.
 */
public class ResourceSlidePagerHolder extends SlidePagerHolder<Resource> {

    public ResourceSlidePagerHolder(View view) {
        super(view);
    }

    @Override
    public void bindView(Resource resource) {
        Resource.addGotoResDetailsClickListener(container, resource);
        titleTv.setText(resource.getTitle());
        DraweeViewUtils.setPlaceHolderByResType(iconDv, resource.getType());
        ImageFetcher.getInstance(container).fetchSmall(iconDv, resource.getIconUrl());
    }
}
