package com.codyy.erpsportal.resource.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UriUtils;
import com.codyy.erpsportal.commons.widgets.ViewPagerFixed;
import com.codyy.erpsportal.commons.widgets.photodrawee.OnViewTapListener;
import com.codyy.erpsportal.commons.widgets.photodrawee.PhotoDrawee;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.cache.disk.FileCache;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片查看
 * Created by gujiajia on 2016/7/6
 */
public class PicturesActivity extends AppCompatActivity {

    private final static String TAG = "PicturesActivity";

    private final static String EXTRA_PICTURE_LIST = "EXTRA_PICTURE_LIST";

    private final static String EXTRA_POSITION = "EXTRA_POSITION";

    private final static String EXTRA_SMALL_FIRST = "EXTRA_SMALL_FIRST";

    private ViewPagerFixed mViewPager;

    private int mPosition;

    private List<String> mPictures;

    private int mGalleryHeight;

    private int mGalleryWidth;

    private ImagePagerAdapter mAdapter;

    private boolean mSmallFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);
        initAttributes();
        findViews();
        initViews();
    }

    private void initAttributes() {
        mPictures = getIntent().getStringArrayListExtra(EXTRA_PICTURE_LIST);
        mPosition = getIntent().getIntExtra(EXTRA_POSITION, 0);
        mSmallFirst = getIntent().getBooleanExtra(EXTRA_SMALL_FIRST, false);
        acquireGalleryAreaSize();
    }

    private void acquireGalleryAreaSize() {
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        mGalleryHeight = getResources().getDisplayMetrics().heightPixels - statusBarHeight;
        mGalleryWidth = getResources().getDisplayMetrics().widthPixels;
    }

    private void findViews() {
        mViewPager = (ViewPagerFixed) findViewById(R.id.vp_subject_material);
    }

    private void initViews() {
        mAdapter = new ImagePagerAdapter(mPictures);
        mAdapter.setOnPageClickListener(new OnPageClickListener() {
            @Override
            public void onPageClick(int position) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
        mViewPager.setAdapter( mAdapter);
        mViewPager.setCurrentItem(mPosition);
    }

    public static void start(Activity activity, List<String> list, int position) {
        start(activity, list, position, false);
    }

    public static void start(Activity activity, List<String> list, int position, boolean smallFirst) {
        Intent intent = new Intent(activity, PicturesActivity.class);
        intent.putExtra(EXTRA_PICTURE_LIST, (ArrayList<String>)list);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_SMALL_FIRST, smallFirst);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    class ImagePagerAdapter extends PagerAdapter {

        private List<String> mPictures;

        private OnPageClickListener mOnPageClickListener;

        ImagePagerAdapter(List<String> pictures) {
            this.mPictures = pictures;
        }

        public void setOnPageClickListener(OnPageClickListener onPageClickListener) {
            mOnPageClickListener = onPageClickListener;
        }

        @Override
        public int getCount() {
            return mPictures==null? 0: mPictures.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container,final int position) {
            final PhotoDrawee photoDrawee = new PhotoDrawee(PicturesActivity.this);
            if (mSmallFirst) {
                FileCache fileCache = Fresco.getImagePipelineFactory().getMainFileCache();
                String url = UriUtils.buildSmallImageUrl(mPictures.get(position));
                CacheKey cacheKey = new SimpleCacheKey(url);
                BinaryResource binaryResource = fileCache.getResource(cacheKey);
                Drawable drawable = null;
                if (binaryResource != null) {
                    try {
                        drawable = Drawable.createFromResourceStream(container.getResources(), null,
                                binaryResource.openStream(), "src");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (drawable != null) {
                    GenericDraweeHierarchyBuilder builder =
                            new GenericDraweeHierarchyBuilder(getResources());
                    GenericDraweeHierarchy hierarchy = builder
                            .setPlaceholderImage(drawable)
                            .setFadeDuration(0)
                            .setPlaceholderImageScaleType(ScaleType.FIT_CENTER)
                            .setActualImageScaleType(ScaleType.FIT_CENTER)
                            .build();
                    photoDrawee.setHierarchy(hierarchy);
                }
            }

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(mPictures.get(position)))
                    .setResizeOptions(new ResizeOptions(mGalleryWidth, mGalleryHeight))
                    .build();
            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            controller.setImageRequest(request);
            controller.setAutoPlayAnimations(true);
            controller.setOldController(photoDrawee.getController());
            controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    if (imageInfo == null) {
                        return;
                    }
                    photoDrawee.update(imageInfo.getWidth(), imageInfo.getHeight());
                }
            });
            photoDrawee.setController(controller.build());
            container.addView(photoDrawee);
            if (mOnPageClickListener != null) {
                photoDrawee.setOnViewTapListener(new OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
                        if (mOnPageClickListener != null) mOnPageClickListener.onPageClick(position);
                    }
                });
            }
            return photoDrawee;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public interface OnPageClickListener{
        void onPageClick(int position);
    }
}
