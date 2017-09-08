package com.codyy.erpsportal.commons.controllers.viewholders.homepage;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.mainpage.MainResource;
import com.codyy.erpsportal.commons.utils.UriUtils;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页(集团校)-优课资源
 * Created by poe on 17-8-7.
 */

public class HomeResourceViewHolder extends BaseRecyclerViewHolder<MainResource> {

    @Bind(R.id.firstpageclass_item_simpledraweeview)
    SimpleDraweeView simpledraweeview;
    @Bind(R.id.firstpageclass_item_textview)
    TextView textview;

    public HomeResourceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.firstpageclass_item_layout;
    }

    @Override
    public void setData(int position, MainResource data) throws Throwable {
        textview.setText(data.getResourceName());
        //placeholder_img
        //icon_default_video
        if(null == data) return;
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setUri(UriUtils.buildSmallImageUrl(data.getThumbPath()))
                .setTapToRetryEnabled(true)
                .setOldController(simpledraweeview.getController())
                .setAutoPlayAnimations((data.getThumbPath()!=null && data.getThumbPath().contains(".gif")))
                .setControllerListener(new SimpleControllerListener() {
                    @Override
                    public void onFailure(String id, Throwable throwable) {

                        if(TextUtils.isEmpty(data.getResourceColumn())) return;
                        if("video".equals(data.getResourceColumn())){
                            simpledraweeview.setImageResource(R.drawable.icon_default_video);
                        }else{
                            simpledraweeview.setImageResource(R.drawable.placeholder_img);
                        }
                    }
                })
                .build();

        simpledraweeview.setController(draweeController);
    }
}
