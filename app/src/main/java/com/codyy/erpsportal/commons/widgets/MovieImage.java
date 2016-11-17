package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.codyy.erpsportal.commons.utils.Cog;

/**
 * Created by poe on 15-8-6.
 * 电影画布图片胶卷
 */
public class MovieImage extends ImageView {

    private static final String TAG = "MovieImage";
    private Paint mPaint;

    public MovieImage(Context context) {
        super(context);
        init();
    }

    public MovieImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MovieImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MovieImage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Cog.e(TAG, "imageview w:" + widthMeasureSpec + "  h:" + heightMeasureSpec);
        Cog.e(TAG,"imageview count w:"+getMeasuredWidth()+"  h:"+getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int width = getMeasuredWidth();
        int hight = getMeasuredHeight();

        Drawable drawable = getDrawable();
        if (null != drawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Cog.e(TAG,"bitmap w:"+bitmap.getWidth()+"  h:"+bitmap.getHeight());
            int bwidth = bitmap.getWidth();
            int bHight = bitmap.getHeight();

            Matrix matrix = new Matrix();
            if(bwidth < width || bHight < hight){
                float sx = (width*1.0f)/bwidth;//提高精确度否则图片裁剪有误差
                matrix.setScale(sx,sx);
                Bitmap nb = Bitmap.createBitmap(bitmap,0,0,bwidth,(int)(bHight*hight/(width*1.0f)),matrix,true);
                canvas.drawBitmap(nb,0,0,mPaint);
                nb.recycle();
            }else{
                 super.onDraw(canvas);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    private void init() {
        mPaint  =   new Paint();
    }

}
