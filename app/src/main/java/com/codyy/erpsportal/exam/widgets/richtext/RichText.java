package com.codyy.erpsportal.exam.widgets.richtext;



import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;


import com.codyy.erpsportal.R;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by codyy on 2015/12/28.
 */
public class RichText extends TextView {

    private Drawable placeHolder, errorImage;//占位图，错误图
    private OnImageClickListener onImageClickListener;//图片点击回调
    private int d_w = 200;
    private int d_h = 200;

    public RichText(Context context) {
        this(context, null);
    }

    public RichText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RichText);
        placeHolder = typedArray.getDrawable(R.styleable.RichText_placeHolder);
        errorImage = typedArray.getDrawable(R.styleable.RichText_errorImage);

        d_w = typedArray.getDimensionPixelSize(R.styleable.RichText_default_width, d_w);
        d_h = typedArray.getDimensionPixelSize(R.styleable.RichText_default_height, d_h);

        if (placeHolder == null) {
            placeHolder = new ColorDrawable(Color.GRAY);
        }
        placeHolder.setBounds(0, 0, d_w, d_h);
        if (errorImage == null) {
            errorImage = new ColorDrawable(Color.GRAY);
        }
        errorImage.setBounds(0, 0, d_w, d_h);
        typedArray.recycle();
    }


    /**
     * 设置富文本
     *
     * @param text 富文本
     */
    public void setRichText(String text) {
        Spanned spanned = Html.fromHtml(text, asyncImageGetter, null);
        SpannableStringBuilder spannableStringBuilder;
        if (spanned instanceof SpannableStringBuilder) {
            spannableStringBuilder = (SpannableStringBuilder) spanned;
        } else {
            spannableStringBuilder = new SpannableStringBuilder(spanned);
        }

        ImageSpan[] imageSpans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ImageSpan.class);
        final List<String> imageUrls = new ArrayList<>();
        for (int i = 0, size = imageSpans.length; i < size; i++) {
            ImageSpan imageSpan = imageSpans[i];
            String imageUrl = imageSpan.getSource();
            int start = spannableStringBuilder.getSpanStart(imageSpan);
            int end = spannableStringBuilder.getSpanEnd(imageSpan);
            imageUrls.add(imageUrl);

            final int finalI = i;
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if (onImageClickListener != null) {
                        onImageClickListener.imageClicked(imageUrls, finalI);
                    }
                }
            };
            ClickableSpan[] clickableSpans = spannableStringBuilder.getSpans(start, end, ClickableSpan.class);
            if (clickableSpans != null && clickableSpans.length != 0) {
                for (ClickableSpan cs : clickableSpans) {
                    spannableStringBuilder.removeSpan(cs);
                }
            }
            spannableStringBuilder.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        super.setText(spanned);
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 异步加载图片（自定义图片加载依赖）
     */
    private Html.ImageGetter asyncImageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            if (source.matches("data:image.*base64.*")) {
                String base_64_source = source.replaceAll("data:image.*base64", "");
                byte[] data = Base64.decode(base_64_source, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Drawable image = new BitmapDrawable(getContext().getResources(), bitmap);
                image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
                return image;
            } else {
                final URLDrawable urlDrawable = new URLDrawable();
//                Target target = new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
//                        drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
//                        urlDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
//                        urlDrawable.setDrawable(drawable);
//                        RichText.this.setText(getText());
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Drawable errorDrawable) {
//                        urlDrawable.setBounds(errorDrawable.getBounds());
//                        urlDrawable.setDrawable(errorDrawable);
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//                        urlDrawable.setBounds(placeHolderDrawable.getBounds());
//                        urlDrawable.setDrawable(placeHolderDrawable);
//                    }
//                };
//                addTarget(target);
//               Picasso.with(getContext()).load(source).placeholder(placeHolder).error(errorImage).into(target);

                //使用fresco加载图片



                return urlDrawable;
            }
        }
    };

    private static final class URLDrawable extends BitmapDrawable {
        private Drawable drawable;

        @SuppressWarnings("deprecation")
        public URLDrawable() {
        }

        @Override
        public void draw(Canvas canvas) {
            if (drawable != null)
                drawable.draw(canvas);
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }
    }

    public void setPlaceHolder(Drawable placeHolder) {
        this.placeHolder = placeHolder;
        this.placeHolder.setBounds(0, 0, d_w, d_h);
    }

    public void setErrorImage(Drawable errorImage) {
        this.errorImage = errorImage;
        this.errorImage.setBounds(0, 0, d_w, d_h);
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    public interface OnImageClickListener {
        /**
         * 图片被点击后的回调方法
         *
         * @param imageUrls 本篇富文本内容里的全部图片
         * @param position  点击处图片在imageUrls中的位置
         */
        void imageClicked(List<String> imageUrls, int position);
    }
}
