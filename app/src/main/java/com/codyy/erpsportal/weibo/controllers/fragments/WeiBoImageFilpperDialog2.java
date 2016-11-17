package com.codyy.erpsportal.weibo.controllers.fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.widgets.model.entities.PhotoInfo;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.zoomable.ZoomableDraweeView;

import java.util.ArrayList;

/**
 * Created by kmdai on 16-9-28.
 */

public class WeiBoImageFilpperDialog2 extends DialogFragment {
    public final static String IMAGE_LIST = "image_list";
    public final static String IMAGE_INDEX = "image_index";
    private ArrayList<PhotoInfo> mPhotoInfos;
    private SimpleDraweeView mSimpleDraweeView;
    private int mIndex;
    private ViewPager mViewPager;
    private TextView mTitleTV;

    public static WeiBoImageFilpperDialog2 newInstance(ArrayList<PhotoInfo> photoInfos, int index) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(IMAGE_LIST, photoInfos);
        args.putInt(IMAGE_INDEX, index);

        WeiBoImageFilpperDialog2 fragment = new WeiBoImageFilpperDialog2();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndex = getArguments().getInt(IMAGE_INDEX, 0);
        mPhotoInfos = getArguments().getParcelableArrayList(IMAGE_LIST);
        setStyle(STYLE_NO_FRAME, R.style.weibo_flipper_dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_wei_bo_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager = (ViewPager) view.findViewById(R.id.weibo_iamge_viewpager);
        mViewPager.setVisibility(View.VISIBLE);
        mSimpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.weibo_image_content);
        mSimpleDraweeView.setVisibility(View.GONE);
        mTitleTV = (TextView) view.findViewById(R.id.weibo_iamge_title);
        mTitleTV.setText(mIndex + 1 + "/" + mPhotoInfos.size());
        mViewPager.setAdapter(new PreviewAdapter());
        mViewPager.setCurrentItem(mIndex, false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTitleTV.setText(position + 1 + "/" + mPhotoInfos.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class PreviewAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPhotoInfos == null ? 0 : mPhotoInfos.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ZoomableDraweeView view = new ZoomableDraweeView(container.getContext());
            view.setBackgroundResource(com.codyy.widgets.R.color.white);
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://" + mPhotoInfos.get(position).getPath())).setAutoRotateEnabled(true).build();
            view.setController(Fresco.newDraweeControllerBuilder().setImageRequest(imageRequest).setAutoPlayAnimations(true).build());
            GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(container.getResources()).setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER).build();
            view.setHierarchy(hierarchy);
            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return view;
        }
    }
}
