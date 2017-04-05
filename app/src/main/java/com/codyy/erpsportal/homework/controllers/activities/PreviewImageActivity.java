package com.codyy.erpsportal.homework.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.exam.controllers.activities.media.image.HackyViewPager;
import com.codyy.erpsportal.exam.controllers.activities.media.image.ZoomableDraweeView;
import com.codyy.erpsportal.homework.models.entities.student.ImageDetail;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 图片预览界面 多张可滑动，支持删除图片
 * Created by ldh on 2016/4/7.
 */
public class PreviewImageActivity extends ToolbarActivity {

    private final static String TAG = PreviewImageActivity.class.getSimpleName();

    public final static String EXTRA_DELETING = "com.codyy.erpsportal.EXTRA_DELETING";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_title)
    TextView mTitle;

    @Bind(R.id.view_pager)
    HackyViewPager mHackyViewPager;

    private HackyPagerAdapter mHackyPagerAdapter;

    private List<ImageDetail> mImageBeans;

    private ArrayList<ImageDetail> mDeletingImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitle.setText(getString(R.string.exam_image_preview));
        mImageBeans = getIntent().getParcelableArrayListExtra(EXTRA_DATA);
        Cog.d(TAG, mImageBeans.toString());
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (mImageBeans.size() > 0) {
                    mHackyPagerAdapter = new HackyPagerAdapter(PreviewImageActivity.this);
                    mHackyViewPager.setAdapter(mHackyPagerAdapter);
                }
            }
        });
        mDeletingImages = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_delete, menu);
        final MenuItem menuItem = menu.findItem(R.id.image_delete_menu);
        LinearLayout linearLayout = (LinearLayout) menuItem.getActionView();
        TextView textView = (TextView) linearLayout.findViewById(R.id.task_title);
        textView.setText(menuItem.toString());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(mHackyViewPager.getCurrentItem());
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public interface OnDeleteSuccess {
        void onPreviewResult(List<ImageDetail> result, String deleteUrl);
    }

    private static OnDeleteSuccess mOnDeleteSuccess;

    public static void setOnDeleteListener(OnDeleteSuccess listener) {
        mOnDeleteSuccess = listener;
    }

    public void removeItem(int position) {
        if (mImageBeans.size() > 0) {
            ImageDetail deletingImage = mImageBeans.remove(position);
            String deleteUrl = deletingImage.getPicUrl();
            mDeletingImages.add(deletingImage);
            mHackyPagerAdapter.notifyDataSetChanged();
            mOnDeleteSuccess.onPreviewResult(mImageBeans, deleteUrl);
            if (mImageBeans.size() == 0) {
                finish();
            }
        }
    }

    @Override
    public void finish() {
        if (mDeletingImages.size() == 0) {//什么都没删
            setResult(RESULT_CANCELED);
        } else {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DELETING, mDeletingImages);
            setResult(RESULT_OK, intent);
        }
        super.finish();
    }

    private class HackyPagerAdapter extends PagerAdapter {

        Context mContext;

        HackyPagerAdapter(Context activity) {
            this.mContext = activity;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mImageBeans.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            RelativeLayout relativeLayout = new RelativeLayout(container.getContext());
            container.addView(relativeLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ZoomableDraweeView photoView = new ZoomableDraweeView(container.getContext());
            RelativeLayout.LayoutParams photoViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            photoViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            DraweeController ctrl = Fresco.newDraweeControllerBuilder().setUri(
                    Uri.parse("file://" + mImageBeans.get(position).getPicUrl())).setTapToRetryEnabled(true).build();
            GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                    .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                    .setProgressBarImage(new ProgressBarDrawable())
                    .build();

            photoView.setController(ctrl);
            photoView.setHierarchy(hierarchy);
//            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            photoView.setLayoutParams(photoViewLayoutParams);
            relativeLayout.addView(photoView);
            return relativeLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_image_preview;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
    }

}
