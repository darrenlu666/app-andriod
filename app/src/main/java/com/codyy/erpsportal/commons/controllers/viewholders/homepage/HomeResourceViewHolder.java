package com.codyy.erpsportal.commons.controllers.viewholders.homepage;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页(集团校)-优课资源
 * Created by poe on 17-8-7.
 */

public class HomeResourceViewHolder extends BaseRecyclerViewHolder<Resource> {

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
    public void setData(int position, Resource data) throws Throwable {
        ImageFetcher.getInstance(simpledraweeview.getContext()).fetchSmall( simpledraweeview, data.getIconUrl());
        textview.setText(data.getTitle());
    }

}
