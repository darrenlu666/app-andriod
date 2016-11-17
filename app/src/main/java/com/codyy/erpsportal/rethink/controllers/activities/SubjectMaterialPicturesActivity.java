package com.codyy.erpsportal.rethink.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.ViewPagerFixed;
import com.codyy.erpsportal.commons.widgets.photodrawee.PhotoDrawee;
import com.codyy.erpsportal.perlcourseprep.models.entities.SubjectMaterialPicture;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.List;


public class SubjectMaterialPicturesActivity extends AppCompatActivity {

    private final static String TAG = "SubjectMaterialPicturesActivity";

    private final static String EXTRA_PICTURE_LIST = "EXTRA_PICTURE_LIST";

    private final static String EXTRA_POSITION = "EXTRA_POSITION";

    private Button mReturnBtn;

    private TextView mTitleTv;

    private TextView mCountTv;

    private TextView mCountMirrorTv;

    private ViewPagerFixed mViewPager;

    private int mPosition;

    private List<SubjectMaterialPicture> mPictures;

    private int mGalleryHeight;

    private int mGalleryWidth;

    private ImagePagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_material_pictures);
        mPictures = getIntent().getParcelableArrayListExtra(EXTRA_PICTURE_LIST);
        mPosition = getIntent().getIntExtra(EXTRA_POSITION, 0);
        acquireGalleryAreaSize();
        findViews();
        initViews();
    }

    private void acquireGalleryAreaSize() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            mGalleryHeight = getResources().getDisplayMetrics().heightPixels - actionBarHeight;
        }

        mGalleryWidth = getResources().getDisplayMetrics().widthPixels;
    }

    private void findViews() {
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mCountTv = (TextView) findViewById(R.id.tv_count);
        mCountMirrorTv = (TextView) findViewById(R.id.tv_count_mirror);
        mViewPager = (ViewPagerFixed) findViewById(R.id.vp_subject_material);
        mReturnBtn = (Button) findViewById(R.id.btn_return);
    }

    private void initViews() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                mTitleTv.setText(mPictures.get(position).getName());
                setCountTv( getString(R.string.fraction_line, position + 1, mPictures.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        mAdapter = new ImagePagerAdapter( mPictures);
        mViewPager.setAdapter( mAdapter);
        mTitleTv.setText( mPictures.get( mPosition).getName());
        setCountTv(getString(R.string.fraction_line, mPosition + 1, mPictures.size()));
        mViewPager.setCurrentItem( mPosition);
        mReturnBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                UIUtils.addExitTranAnim( SubjectMaterialPicturesActivity.this);
            }
        });
    }

    private void setCountTv(String countStr) {
        mCountTv.setText( countStr);
        mCountMirrorTv.setText( countStr);
    }

    public static void start(Activity activity, List<SubjectMaterialPicture> list, int position) {
        Intent intent = new Intent(activity, SubjectMaterialPicturesActivity.class);
        intent.putExtra(EXTRA_PICTURE_LIST, (ArrayList<SubjectMaterialPicture>)list);
        intent.putExtra(EXTRA_POSITION, position);
        activity.startActivity(intent);
        UIUtils.addEnterAnim( activity);
    }

    class ImagePagerAdapter extends PagerAdapter {

        private List<SubjectMaterialPicture> mPictures;

        ImagePagerAdapter(List<SubjectMaterialPicture> pictures) {
            this.mPictures = pictures;
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
        public Object instantiateItem(ViewGroup container, int position) {
            final PhotoDrawee photoDrawee = new PhotoDrawee(SubjectMaterialPicturesActivity.this);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(mPictures.get(position).getUrl()))
                    .setResizeOptions(new ResizeOptions(mGalleryWidth, mGalleryHeight))
                    .build();
            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            controller.setImageRequest(request);
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

            if(mPictures.get(position).getUrl()!=null && mPictures.get(position).getUrl().contains(".gif")){
                controller.setAutoPlayAnimations(true);
            }

            photoDrawee.setController(controller.build());
            container.addView(photoDrawee);
            return photoDrawee;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
