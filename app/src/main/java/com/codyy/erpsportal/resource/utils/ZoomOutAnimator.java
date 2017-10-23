package com.codyy.erpsportal.resource.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.codyy.erpsportal.commons.utils.Cog;

/**
 * 放大缩小动画
 * Created by gujiajia on 2016/7/20.
 */
public class ZoomOutAnimator {

    private final static String TAG = "ZoomOutAnimator";

    private Animator mCurrentAnimator;

    private int mShortAnimationDuration = 200;

    private AnimatorListenerAdapter mAnimatorListener;

    public void zoomImageFromThumb(Activity activity, final View thumbView) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

//        thumbView.bringToFront();

        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        activity.findViewById(android.R.id.content).getGlobalVisibleRect(finalBounds, globalOffset);
        Cog.d(TAG, "zoomImageFromThumb globalOffset=" + globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);
        Cog.d(TAG, "zoomImageFromThumb startBounds=" + startBounds);
        Cog.d(TAG, "zoomImageFromThumb finalBounds=" + finalBounds);

        float finalScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // 高限制
            finalScale = (float) finalBounds.height() / startBounds.height();
            float finalWidth = finalScale * startBounds.width();
            float deltaWidth = (finalBounds.width() - finalWidth) / 2;
            finalBounds.left += deltaWidth;
            finalBounds.right -= deltaWidth;
        } else {
            // 宽限制
            finalScale = (float) finalBounds.width() / startBounds.width();
            float finalHeight = finalScale * startBounds.height();
            float deltaHeight = (finalBounds.height() - finalHeight) / 2;
            finalBounds.top += deltaHeight;
            finalBounds.bottom -= deltaHeight;
        }

        Cog.d(TAG, "zoomImageFromThumb new startBounds=" + startBounds);
        Cog.d(TAG, "zoomImageFromThumb new finalBounds=" + finalBounds);

        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-in view in the place of the thumbnail.
//        thumbView.setAlpha(0f);
        thumbView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        thumbView.setPivotX(0f);
        thumbView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();

        set
                .play(ObjectAnimator.ofFloat(thumbView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(thumbView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(thumbView, View.SCALE_X, 1f, finalScale))
                .with(ObjectAnimator.ofFloat(thumbView, View.SCALE_Y, 1f, finalScale));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mAnimatorListener != null) mAnimatorListener.onAnimationEnd(animation);
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
        // and show the thumbnail instead of the expanded image.
//        final float startScaleFinal = startScale;
//        expandedImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mCurrentAnimator != null) {
//                    mCurrentAnimator.cancel();
//                }
//
//                // Animate the four positioning/sizing properties in parallel, back to their
//                // original values.
//                AnimatorSet set = new AnimatorSet();
//                set
//                        .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
//                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
//                        .with(ObjectAnimator
//                                .ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
//                        .with(ObjectAnimator
//                                .ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
//                set.setDuration(mShortAnimationDuration);
//                set.setInterpolator(new DecelerateInterpolator());
//                set.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        thumbView.setAlpha(1f);
//                        expandedImageView.setVisibility(View.GONE);
//                        mCurrentAnimator = null;
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//                        thumbView.setAlpha(1f);
//                        expandedImageView.setVisibility(View.GONE);
//                        mCurrentAnimator = null;
//                    }
//                });
//                set.start();
//                mCurrentAnimator = set;
//            }
//        });
    }

    public void goBack(View view, float y) {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        Cog.d(TAG, "goBack y=", y);
        Animator animator = ObjectAnimator.ofFloat(view, View.Y, view.getY(), y);
        animator.setDuration(200);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                mCurrentAnimator = null;
            }
        });
        animator.start();
        mCurrentAnimator = animator;
    }

    public boolean isOver() {
        return mCurrentAnimator == null;
    }

    public void setAnimatorListener(AnimatorListenerAdapter animatorListener) {
        mAnimatorListener = animatorListener;
    }
}
