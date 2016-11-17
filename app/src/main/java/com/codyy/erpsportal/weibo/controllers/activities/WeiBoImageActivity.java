package com.codyy.erpsportal.weibo.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.weibo.models.entities.WeiBoListInfo;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.zoomable.ZoomableDraweeView;

import java.util.ArrayList;

public class WeiBoImageActivity extends AppCompatActivity {
    public static final String SELECT_PAGE = "select_page";
    public static final String IMAGE_DATA = "image_data";
    private ViewPager mViewPager;
    private TextView mTitleTV;
    private ArrayList<WeiBoListInfo.ImageListEntity> mListEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wei_bo_image);
        mViewPager = (ViewPager) findViewById(R.id.weibo_iamge_viewpager);
        mTitleTV = (TextView) findViewById(R.id.weibo_iamge_title);
        mListEntities = getIntent().getParcelableArrayListExtra(IMAGE_DATA);
        int page = getIntent().getIntExtra(SELECT_PAGE, 0);
        mTitleTV.setText(page + 1 + "/" + mListEntities.size());
        mViewPager.setAdapter(new PreviewAdapter());
        mViewPager.setCurrentItem(page, false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTitleTV.setText(position + 1 + "/" + mListEntities.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static void start(Context context, int page, ArrayList<WeiBoListInfo.ImageListEntity> mListEntities) {
        Intent intent = new Intent(context, WeiBoImageActivity.class);
        intent.putParcelableArrayListExtra(IMAGE_DATA, mListEntities);
        intent.putExtra(SELECT_PAGE, page);
        context.startActivity(intent);
    }

    class PreviewAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mListEntities == null ? 0 : mListEntities.size();
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
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(mListEntities.get(position).getImage())).setAutoRotateEnabled(true).build();
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
    }

    public void onBackClick(View v) {
        this.finish();
    }
}
