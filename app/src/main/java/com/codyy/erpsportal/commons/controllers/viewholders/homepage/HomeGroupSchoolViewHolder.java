package com.codyy.erpsportal.commons.controllers.viewholders.homepage;

import android.graphics.drawable.Animatable;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.mainpage.GroupSchool;
import com.codyy.erpsportal.commons.utils.UriUtils;
import com.codyy.erpsportal.commons.widgets.AspectRatioDraweeView;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页(集团校)-优课资源
 * Created by poe on 17-8-7.
 */

public class HomeGroupSchoolViewHolder extends BaseRecyclerViewHolder<GroupSchool> {


    @Bind(R.id.sdv)
    AspectRatioDraweeView mSdv;
    @Bind(R.id.tv_school)
    TextView mTvSchool;
    @Bind(R.id.tag_tv)
    TextView mTagTv;

    public HomeGroupSchoolViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_home_group_school;
    }

    @Override
    public void setData(int position, GroupSchool data) throws Throwable {

        mTvSchool.setText(data.getClsSchoolName());
        mTagTv.setText(data.getSchoolTypeName());

        //设置默认图片.
//        ImageFetcher.getInstance(mSdv.getContext()).fetchSmall(mSdv, data.getCoverPic());

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(UriUtils.buildSmallImageUrl(data.getCoverPic()))
                .setTapToRetryEnabled(true)
                .setOldController(mSdv.getController())
                .setControllerListener(new ControllerListener<ImageInfo>() {
                    @Override
                    public void onSubmit(String id, Object callerContext) {

                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {

                    }

                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {

                    }

                    @Override
                    public void onIntermediateImageFailed(String id, Throwable throwable) {

                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        mSdv.setImageResource(R.drawable.ic_group_school_default);
                    }

                    @Override
                    public void onRelease(String id) {

                    }
                })
                .build();

        mSdv.setController(controller);
    }
}
