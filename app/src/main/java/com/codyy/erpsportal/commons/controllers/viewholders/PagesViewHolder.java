package com.codyy.erpsportal.commons.controllers.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.codyy.erpsportal.commons.widgets.infinitepager.HolderCreator;
import com.codyy.erpsportal.commons.widgets.infinitepager.SlidePagerAdapter;
import com.codyy.erpsportal.commons.widgets.infinitepager.SlideView;
import com.codyy.erpsportal.resource.models.entities.Resource;

import java.util.List;

/**
 * 自动翻页幻灯片ViewHolder
 * Created by gujiajia on 2015/8/6.
 */
public class PagesViewHolder extends RecyclerView.ViewHolder{

    private final static String TAG = "PagesViewHolder";

    private SlideView mSlideView;

    public PagesViewHolder(View view) {
        super(view);
        mSlideView = (SlideView) view;
    }

    public void bindView(List<Resource> resources) {
//        ResourcePagerAdapter adapter = new ResourcePagerAdapter(resources);
        HolderCreator<ResourceSlidePagerHolder> holderCreator = new HolderCreator<ResourceSlidePagerHolder>(){
            @Override
            public ResourceSlidePagerHolder create(View view) {
                return new ResourceSlidePagerHolder(view);
            }
        };
        SlidePagerAdapter slidePagerAdapter = new SlidePagerAdapter(resources, holderCreator);
        mSlideView.setAdapter(slidePagerAdapter);
    }

    /**
     * 开始自动滚
     */
    public void startToScroll() {
        mSlideView.startToScroll();
    }

    /**
     * 停止自动滚
     */
    public void stopScrolling() {
        mSlideView.stopScrolling();;
    }
}
