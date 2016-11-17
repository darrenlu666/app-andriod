package com.codyy.erpsportal.weibo.controllers.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.weibo.models.entities.WeiBoListInfo;
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
 * 图片浏览
 * Created by kmdai on 16-5-10.
 */
public class WeiBoImageFilpperDialog extends DialogFragment {
    public final static String IMAGE_LIST = "image_list";
    public final static String IMAGE_INDEX = "image_index";
    private ViewPager mViewPager;
    private TextView mTitleTV;
    private OnShowing mOnShowing;
    private SimpleDraweeView mSimpleDraweeView;
    private View mRootView;
    private int mIndex;
    private ArrayList<WeiBoListInfo.ImageListEntity> mListEntities;
    private final static int ANI_TIME = 300;
    private AnimatorSet mAnimatorSet;

    public static WeiBoImageFilpperDialog newInstance(ArrayList<WeiBoListInfo.ImageListEntity> listEntities, int index) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(IMAGE_LIST, listEntities);
        args.putInt(IMAGE_INDEX, index);
        WeiBoImageFilpperDialog fragment = new WeiBoImageFilpperDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndex = getArguments().getInt(IMAGE_INDEX, 0);
        mListEntities = getArguments().getParcelableArrayList(IMAGE_LIST);
        setStyle(STYLE_NO_FRAME, R.style.weibo_flipper_dialog);
    }

    public void setOnShowing(OnShowing onShowing) {
        this.mOnShowing = onShowing;
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
        mRootView = view.findViewById(R.id.weibo_image_root_layout);
        mTitleTV = (TextView) view.findViewById(R.id.weibo_iamge_title);
        mSimpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.weibo_image_content);
        mTitleTV.setText(mIndex + 1 + "/" + mListEntities.size());
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        int index = mViewPager.getCurrentItem();
                        if (mOnShowing != null && mOnShowing.getEndView(index) != null) {
                            if (mAnimatorSet == null || !mAnimatorSet.isRunning()) {
//                                View view2 = mViewPager.findViewWithTag(getTagByPosition(index));
                                ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(mListEntities.get(index).getImage())).setAutoRotateEnabled(true).build();
                                mSimpleDraweeView.setController(Fresco.newDraweeControllerBuilder().setLowResImageRequest(ImageRequest.fromUri(Uri.parse(getSmall(mListEntities.get(index).getImage())))).setImageRequest(imageRequest)
                                        .setAutoPlayAnimations(true)
                                        .build());
                                mSimpleDraweeView.setBackgroundColor(Color.TRANSPARENT);
                                GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                                        .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                                        .setPlaceholderImage(getResources().getDrawable(R.drawable.placeholder_img))
                                        .build();
                                mSimpleDraweeView.setHierarchy(hierarchy);
                                mSimpleDraweeView.setVisibility(View.VISIBLE);
                                mViewPager.setVisibility(View.GONE);
                                addAnimationEnd(mOnShowing.getEndView(index), mSimpleDraweeView);
                            }
                        } else {
                            dismiss();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
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
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final ZoomableDraweeView view = new ZoomableDraweeView(container.getContext());
            view.setBackgroundResource(com.codyy.widgets.R.color.white);
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(mListEntities.get(position).getImage())).setAutoRotateEnabled(true).build();
            view.setController(Fresco.newDraweeControllerBuilder().setLowResImageRequest(ImageRequest.fromUri(Uri.parse(getSmall(mListEntities.get(position).getImage())))).setImageRequest(imageRequest)
                    .setAutoPlayAnimations(true)
                    .build());
            view.setBackgroundColor(Color.TRANSPARENT);
            GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                    .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                    .setPlaceholderImage(getResources().getDrawable(R.drawable.placeholder_img))
                    .build();
            view.setHierarchy(hierarchy);
            view.setTag(getTagByPosition(position));
            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return view;
        }
    }

    private String getTagByPosition(int position) {
        return "viewpager-index" + position;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewPager.setAdapter(new PreviewAdapter());
        mViewPager.setCurrentItem(mIndex, false);
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
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(mListEntities.get(mIndex).getImage())).setAutoRotateEnabled(true).build();
        mSimpleDraweeView.setController(Fresco.newDraweeControllerBuilder().setLowResImageRequest(ImageRequest.fromUri(Uri.parse(getSmall(mListEntities.get(mIndex).getImage())))).setImageRequest(imageRequest)
                .setAutoPlayAnimations(true)
                .build());
        mSimpleDraweeView.setBackgroundColor(Color.TRANSPARENT);
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setPlaceholderImage(getResources().getDrawable(R.drawable.placeholder_img))
                .build();
        mSimpleDraweeView.setHierarchy(hierarchy);
        mSimpleDraweeView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom > oldBottom) {
                    if (mOnShowing != null && mOnShowing.getStartView(mIndex) != null) {
                        addAnimationStart(mOnShowing.getStartView(mIndex), mSimpleDraweeView);
                    }
                }
            }
        });
    }

    private String getSmall(String url) {
        StringBuilder stringBuilder = new StringBuilder(url);
        int index = stringBuilder.lastIndexOf(".");
        String small = null;
        if (index > 0) {
            return stringBuilder.insert(index, ".small").toString();
        }
        return url;
    }


    /**
     * @param view1 开始view
     * @param view2 结束view
     */
    private void addAnimationStart(View view1, View view2) {
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();
        view1.getGlobalVisibleRect(startBounds);
        view2.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);
        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScaleX;
        float startScaleY;
        startScaleX = (float) startBounds.width() / finalBounds.width();
        startScaleY = (float) startBounds.height() / finalBounds.height();
        View tagetView = view2;
        tagetView.setPivotX(0f);
        tagetView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        if (mAnimatorSet == null) {
            mAnimatorSet = new AnimatorSet();
        } else if (mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
        mAnimatorSet.play(ObjectAnimator.ofFloat(tagetView, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(tagetView, View.Y, startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(tagetView, View.SCALE_X, startScaleX, 1f))
                .with(ObjectAnimator.ofFloat(tagetView, View.SCALE_Y, startScaleY, 1f));
        mAnimatorSet.setDuration(ANI_TIME);
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
//                mCurrentAnimator = null;
                mViewPager.setVisibility(View.VISIBLE);
                mSimpleDraweeView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
//                mCurrentAnimator = null;
            }
        });
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.weibo_image_dialog);
        animation.setDuration(ANI_TIME);
        mRootView.startAnimation(animation);
        mTitleTV.startAnimation(animation);
        mAnimatorSet.start();
//        mCurrentAnimator = mAnimatorSet;
    }

    /**
     * @param view1
     * @param view2
     */
    private void addAnimationEnd(View view1, View view2) {
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();
        view1.getGlobalVisibleRect(startBounds);
        mSimpleDraweeView.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);
        float startScaleX;
        float startScaleY;
        startScaleX = (float) startBounds.width() / finalBounds.width();
        startScaleY = (float) startBounds.height() / finalBounds.height();
        View tagetView = view2;
        tagetView.setPivotX(0f);
        tagetView.setPivotY(0f);
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(ObjectAnimator.ofFloat(tagetView, View.X, finalBounds.left, startBounds.left))
                .with(ObjectAnimator.ofFloat(tagetView, View.Y, finalBounds.top, startBounds.top))
                .with(ObjectAnimator.ofFloat(tagetView, View.SCALE_X, 1f, startScaleX))
                .with(ObjectAnimator.ofFloat(tagetView, View.SCALE_Y, 1f, startScaleY))
                .with(ObjectAnimator.ofFloat(tagetView, View.ALPHA, 1f, 0f));
        mAnimatorSet.setDuration(ANI_TIME);
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                dismiss();
            }
        });
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.weibo_image_dialog_hint);
        animation.setDuration(ANI_TIME);
        mRootView.startAnimation(animation);
        mTitleTV.startAnimation(animation);
        mAnimatorSet.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface OnShowing {
        View getStartView(int position);

        View getEndView(int position);
    }
}
