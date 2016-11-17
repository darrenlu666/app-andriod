package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.HomeWorkDetailItem;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.zoomable.ZoomableDraweeView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 课堂作业详情
 * Created by kmdai on 2015/8/18.
 */
public class HomeWorkItemDetailActivity extends Activity {
    @Bind(R.id.homework_item_detail_viewpager)
    ViewPager mViewPager;

    private List<HomeWorkDetailItem> mItems;

    private TextView mNumberText;

    private int mGalleryHeight;

    private int mGalleryWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homework_item_detail);
        ButterKnife.bind(this);

        mGalleryHeight = getResources().getDisplayMetrics().heightPixels - getResources().getDimensionPixelSize(R.dimen.title_height);
        mGalleryWidth = getResources().getDisplayMetrics().widthPixels;

        mItems = getIntent().getParcelableArrayListExtra("item");
        mViewPager.setAdapter(new ImagePagerAdapter(mItems));
        int index = getIntent().getIntExtra("number", 0);
        mViewPager.setCurrentItem(index, false);
        mNumberText = (TextView) findViewById(R.id.homework_item_text_number);
        mNumberText.setText(getString(R.string.fraction_line, index + 1, mItems.size()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mNumberText.setText(getString(R.string.fraction_line, position + 1, mItems.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @OnClick(R.id.btn_back)
    void backBtn() {
        this.finish();
    }

    class ImagePagerAdapter extends PagerAdapter {

        List<HomeWorkDetailItem> items;

        ImagePagerAdapter(List<HomeWorkDetailItem> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ZoomableDraweeView view = new ZoomableDraweeView(container.getContext());
            view.setBackgroundResource(com.codyy.widgets.R.color.white);
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(items.get(position).getBigImageUrl())).setAutoRotateEnabled(true).build();
            view.setController(Fresco.newDraweeControllerBuilder().setImageRequest(imageRequest)
                    .setAutoPlayAnimations(true)
                    .build());
            GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                    .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                    .setPlaceholderImage(getResources().getDrawable(R.drawable.placeholder_img))
                    .build();
            view.setHierarchy(hierarchy);
            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}