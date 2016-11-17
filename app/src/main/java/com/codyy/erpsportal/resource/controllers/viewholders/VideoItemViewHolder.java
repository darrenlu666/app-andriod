package com.codyy.erpsportal.resource.controllers.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.codyy.erpsportal.resource.utils.DraweeViewUtils;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by gujiajia on 2015/9/14.
 */
public class VideoItemViewHolder extends AbsViewHolder<Resource> {

    private TextView titleTv;

    private SimpleDraweeView iconIvDrawee;

    @Override
    public int obtainLayoutId() {
        return R.layout.item_resource;
    }

    @Override
    public void mapFromView(View view) {
        iconIvDrawee = (SimpleDraweeView) view.findViewById(R.id.res_icon);
        titleTv = (TextView) view.findViewById(R.id.title);
    }

    @Override
    public void setDataToView(Resource resource, Context context) {
        titleTv.setText(resource.getTitle());
        String imageName = resource.getIconUrl();
        DraweeViewUtils.setPlaceHolderByResType(iconIvDrawee, resource.getType());
        ImageFetcher.getInstance(context).fetchSmall(iconIvDrawee, imageName);
    }
}
