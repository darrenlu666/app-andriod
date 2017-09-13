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

        if(null == data) return;
        textview.setText(data.getResourceName());
        if(TextUtils.isEmpty(mData.getResourceColumn())) return;
        int resId = R.drawable.icon_default_video;
        if("video".equals(mData.getResourceColumn())){
            resId = R.drawable.icon_default_video;
        }else{
            resId = R.drawable.placeholder_img;
        }
        ImageFetcher.getInstance(simpledraweeview).fetchSmallWithDefault(simpledraweeview,data.getThumbPath(),resId,true);
    }
}
