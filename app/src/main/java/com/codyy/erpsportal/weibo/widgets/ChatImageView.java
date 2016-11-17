package com.codyy.erpsportal.weibo.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.codyy.erpsportal.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

/**
 * Created by kmdai on 16-8-30.
 */
public class ChatImageView extends View {
    private DraweeHolder<GenericDraweeHierarchy> mDraweeHolder;
    private boolean mInitialised = false;
    private NinePatch mNinePatch;
    private Bitmap mBitmap;
    private Paint mDrawBitMapPT;
    private Bitmap mBGBitmap;
    private Rect mRect;
    private Postprocessor mPostprocessor = new BasePostprocessor() {

        @Override
        public void process(final Bitmap bitmap) {
            mBGBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mBGBitmap);
            canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), mRect, null);
            mNinePatch.draw(canvas, new Rect(0, 0, getWidth(), getHeight()), mDrawBitMapPT);
            postInvalidate();
        }

        @Override
        public void process(Bitmap destBitmap, Bitmap sourceBitmap) {
            super.process(destBitmap, sourceBitmap);
        }
    };

    public ChatImageView(Context context) {
        super(context);
        this.init(context, null);
    }

    public ChatImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public ChatImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChatImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (!this.mInitialised) {
            this.mInitialised = true;
            int style = 1;
            if (attrs != null) {
                TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ChatImageView);
                style = attributes.getInt(R.styleable.ChatImageView_location, 1);
            }
            if (style == 2) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.weibo_talk_right);
            } else {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.weibo_talk_left);
            }
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
            this.mDraweeHolder = DraweeHolder.create(builder.build(), context);
//            mDraweeHolder.getTopLevelDrawable().setCallback(this);
            mNinePatch = new NinePatch(mBitmap, mBitmap.getNinePatchChunk(), null);
            mDrawBitMapPT = new Paint();
            mDrawBitMapPT.setAntiAlias(true);
            mDrawBitMapPT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));


            mBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder_img);
//            mBGBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(mBGBitmap);
//            canvas.drawBitmap(bitmap, -100, -100, null);
//            mNinePatch.draw(canvas, new Rect(0, 0, getWidth(), getHeight()), mDrawBitMapPT);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDraweeHolder.onAttach();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDraweeHolder.onDetach();
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        mDraweeHolder.onDetach();
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        mDraweeHolder.onAttach();
    }

    public void setStringUrl(String url) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setPostprocessor(mPostprocessor).build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(mDraweeHolder.getController())
                .build();
        mDraweeHolder.setController(controller);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        if (who == mDraweeHolder.getTopLevelDrawable()) {
            return true;
        }
        return super.verifyDrawable(who);
    }

    @Override
    protected void onDraw(Canvas canvas1) {
        super.onDraw(canvas1);
        if (mBGBitmap != null) {
            canvas1.drawBitmap(mBGBitmap, new Rect(0, 0, mBGBitmap.getWidth(), mBGBitmap.getHeight()), mRect, null);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mRect == null) {
            mRect = new Rect(0, 0, w, h);
        } else {
            mRect.right = w;
            mRect.bottom = h;
        }
    }
}
