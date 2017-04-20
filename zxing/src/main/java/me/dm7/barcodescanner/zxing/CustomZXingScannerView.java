package me.dm7.barcodescanner.zxing;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.core.BarcodeScannerView;
import me.dm7.barcodescanner.core.DisplayUtils;
import me.dm7.barcodescanner.core.IViewFinder;

public class CustomZXingScannerView extends BarcodeScannerView {
    private static final String TAG = "ZXingScannerView";

    public interface ResultHandler {
        void handleResult(Result rawResult);
    }

    private MultiFormatReader mMultiFormatReader;
    public static final List<BarcodeFormat> ALL_FORMATS = new ArrayList<>();
    private List<BarcodeFormat> mFormats;
    private ResultHandler mResultHandler;

    static {
        ALL_FORMATS.add(BarcodeFormat.UPC_A);
        ALL_FORMATS.add(BarcodeFormat.UPC_E);
        ALL_FORMATS.add(BarcodeFormat.EAN_13);
        ALL_FORMATS.add(BarcodeFormat.EAN_8);
        ALL_FORMATS.add(BarcodeFormat.RSS_14);
        ALL_FORMATS.add(BarcodeFormat.CODE_39);
        ALL_FORMATS.add(BarcodeFormat.CODE_93);
        ALL_FORMATS.add(BarcodeFormat.CODE_128);
        ALL_FORMATS.add(BarcodeFormat.ITF);
        ALL_FORMATS.add(BarcodeFormat.CODABAR);
        ALL_FORMATS.add(BarcodeFormat.QR_CODE);
        ALL_FORMATS.add(BarcodeFormat.DATA_MATRIX);
        ALL_FORMATS.add(BarcodeFormat.PDF_417);
    }

    public CustomZXingScannerView(Context context) {
        super(context);
        initMultiFormatReader();
    }

    public CustomZXingScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initMultiFormatReader();
    }

    @Override
    protected IViewFinder createViewFinderView(Context context) {
        return new CustomViewFinderView(context);
    }

    private static class CustomViewFinderView extends View implements IViewFinder {
        private Rect mFramingRect;
        private static final int[] SCANNER_ALPHA = new int[]{0, 64, 128, 192, 255, 192, 128, 64};
        private int scannerAlpha;
        private int mDefaultLaserColor;
        private int mDefaultMaskColor;
        private int mDefaultBorderColor;
        private int mDefaultBorderStrokeWidth;
        private int mDefaultBorderLineLength;
        protected Paint mLaserPaint;
        protected Paint mFinderMaskPaint;
        protected Paint mCornerBorderPaint;
        protected Paint mBorderPaint;

        private String mPromptStr;
        protected TextPaint mPromptPaint;
        private float mTextStartY;

        protected int mBorderLineLength;
        protected boolean mSquareViewFinder;
        protected boolean isLaserNeeded;
        private float mHalfStrokeWidth;
        private float mQuarterStrokeWidth;

        public CustomViewFinderView(Context context) {
            super(context);
            this.init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.init();
        }

        private void init() {
            this.mDefaultLaserColor = this.getResources().getColor(me.dm7.barcodescanner.core.R.color.viewfinder_laser);
            this.mDefaultMaskColor = this.getResources().getColor(me.dm7.barcodescanner.core.R.color.viewfinder_mask);
            this.mDefaultBorderColor = this.getResources().getColor(me.dm7.barcodescanner.core.R.color.viewfinder_border);
            this.mDefaultBorderStrokeWidth = this.getResources().getDimensionPixelSize(R.dimen.border_width);
            this.mDefaultBorderLineLength = this.getResources().getDimensionPixelSize(R.dimen.border_length);

            mHalfStrokeWidth = mDefaultBorderStrokeWidth / 2f;
            mQuarterStrokeWidth = mDefaultBorderStrokeWidth / 6f;

            this.mLaserPaint = new Paint();
            this.mLaserPaint.setColor(this.mDefaultLaserColor);
            this.mLaserPaint.setStyle(Paint.Style.FILL);
            this.mFinderMaskPaint = new Paint();
            this.mFinderMaskPaint.setColor(this.mDefaultMaskColor);
            this.mCornerBorderPaint = new Paint();
            this.mCornerBorderPaint.setColor(this.mDefaultBorderColor);
            this.mCornerBorderPaint.setStyle(Paint.Style.STROKE);
            this.mCornerBorderPaint.setStrokeWidth( mDefaultBorderStrokeWidth);
            this.mBorderLineLength = this.mDefaultBorderLineLength;

            mBorderPaint = new Paint();
            mBorderPaint.setColor(Color.WHITE);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(mQuarterStrokeWidth);

            mPromptStr = "将二维码放入框内，即可自动扫描";
            mPromptPaint = new TextPaint();
            mPromptPaint.setColor(Color.WHITE);
            mPromptPaint.setTextSize(getResources().getDisplayMetrics().density * 16);
            mTextStartY = getResources().getDisplayMetrics().density * 16;
            FontMetricsInt fontMetricsInt = mPromptPaint.getFontMetricsInt();
            mTextStartY = mTextStartY + fontMetricsInt.descent - fontMetricsInt.ascent;
            setSquareViewFinder(true);//设置为正方形
        }

        public void setLaserColor(int laserColor) {
            this.mLaserPaint.setColor(laserColor);
        }

        public void setMaskColor(int maskColor) {
            this.mFinderMaskPaint.setColor(maskColor);
        }

        public void setBorderColor(int borderColor) {
            this.mCornerBorderPaint.setColor(borderColor);
        }

        public void setBorderStrokeWidth(int borderStrokeWidth) {
            this.mCornerBorderPaint.setStrokeWidth((float) borderStrokeWidth);
        }

        public void setBorderLineLength(int borderLineLength) {
            this.mBorderLineLength = borderLineLength;
        }

        public boolean isLaserNeeded() {
            return isLaserNeeded;
        }

        public void setLaserNeeded(boolean laserNeeded) {
            isLaserNeeded = laserNeeded;
        }

        public void setSquareViewFinder(boolean set) {
            this.mSquareViewFinder = set;
        }

        public void setupViewFinder() {
            this.updateFramingRect();
            this.invalidate();
        }

        public Rect getFramingRect() {
            return this.mFramingRect;
        }

        public void onDraw(Canvas canvas) {
            if (this.getFramingRect() != null) {
                this.drawViewFinderMask(canvas);
                this.drawViewFinderBorder(canvas);
                drawPromptText(canvas);
                if (isLaserNeeded) {
                    this.drawLaser(canvas);
                }
            }
        }

        private void drawPromptText(Canvas canvas) {
            StaticLayout staticLayout = new StaticLayout(mPromptStr, mPromptPaint, canvas.getWidth(),
                    Alignment.ALIGN_CENTER, 1f, 0f, false);
            canvas.save();
            canvas.translate(0, mTextStartY + mFramingRect.bottom);
            staticLayout.draw(canvas);
            canvas.restore();
        }

        public void drawViewFinderMask(Canvas canvas) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            Rect framingRect = this.getFramingRect();
            canvas.drawRect(0.0F, 0.0F, (float) width, (float) framingRect.top, this.mFinderMaskPaint);
            canvas.drawRect(0.0F, (float) framingRect.top, (float) framingRect.left, (float) (framingRect.bottom + 1), this.mFinderMaskPaint);
            canvas.drawRect((float) (framingRect.right + 1), (float) framingRect.top, (float) width, (float) (framingRect.bottom + 1), this.mFinderMaskPaint);
            canvas.drawRect(0.0F, (float) (framingRect.bottom + 1), (float) width, (float) height, this.mFinderMaskPaint);
        }

        public void drawViewFinderBorder(Canvas canvas) {
            Rect framingRect = getFramingRect();
            canvas.drawLine(framingRect.left - 1, framingRect.top - 1 - mHalfStrokeWidth, framingRect.left - 1, framingRect.top - 1 + mBorderLineLength, mCornerBorderPaint);
            canvas.drawLine(framingRect.left - 1, framingRect.top - 1, framingRect.left - 1 + mBorderLineLength, framingRect.top - 1, mCornerBorderPaint);
            canvas.drawLine(framingRect.left - 1, framingRect.bottom + 1 + mHalfStrokeWidth, framingRect.left - 1, framingRect.bottom + 1 - mBorderLineLength, mCornerBorderPaint);
            canvas.drawLine(framingRect.left - 1, framingRect.bottom + 1, framingRect.left - 1 + mBorderLineLength, framingRect.bottom + 1, mCornerBorderPaint);
            canvas.drawLine(framingRect.right + 1, framingRect.top - 1 - mHalfStrokeWidth, framingRect.right + 1, framingRect.top - 1 + mBorderLineLength, mCornerBorderPaint);
            canvas.drawLine(framingRect.right + 1, framingRect.top - 1, framingRect.right + 1 - mBorderLineLength, framingRect.top - 1, mCornerBorderPaint);
            canvas.drawLine(framingRect.right + 1, framingRect.bottom + 1 + mHalfStrokeWidth, framingRect.right + 1, framingRect.bottom + 1 - mBorderLineLength, mCornerBorderPaint);
            canvas.drawLine(framingRect.right + 1, framingRect.bottom + 1, framingRect.right + 1 - mBorderLineLength, framingRect.bottom + 1, mCornerBorderPaint);
            canvas.drawLine( framingRect.left - 1 + mBorderLineLength, framingRect.top - 1 - mQuarterStrokeWidth,
                    framingRect.right + 1 - mBorderLineLength, framingRect.top - 1 - mQuarterStrokeWidth, mBorderPaint);
            canvas.drawLine( framingRect.left - 1 - mQuarterStrokeWidth, framingRect.top - 1 + mBorderLineLength,
                    framingRect.left - 1 - mQuarterStrokeWidth, framingRect.bottom + 1 - mBorderLineLength, mBorderPaint);
            canvas.drawLine( framingRect.right + 1 + mQuarterStrokeWidth, framingRect.top - 1 + mBorderLineLength,
                    framingRect.right + 1 + mQuarterStrokeWidth, framingRect.bottom - 1 - mBorderLineLength, mBorderPaint);
            canvas.drawLine( framingRect.left - 1 + mBorderLineLength, framingRect.bottom + 1 + mQuarterStrokeWidth,
                    framingRect.right + 1 - mBorderLineLength, framingRect.bottom + 1 + mQuarterStrokeWidth, mBorderPaint);
        }

        public void drawLaser(Canvas canvas) {
            Rect framingRect = this.getFramingRect();
            this.mLaserPaint.setAlpha(SCANNER_ALPHA[this.scannerAlpha]);
            this.scannerAlpha = (this.scannerAlpha + 1) % SCANNER_ALPHA.length;
            int middle = framingRect.height() / 2 + framingRect.top;
            canvas.drawRect((float) (framingRect.left + 2), (float) (middle - 1), (float) (framingRect.right - 1), (float) (middle + 2), this.mLaserPaint);
            this.postInvalidateDelayed(80L, framingRect.left - 10, framingRect.top - 10, framingRect.right + 10, framingRect.bottom + 10);
        }

        protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
            this.updateFramingRect();
        }

        public synchronized void updateFramingRect() {
            Point viewResolution = new Point(this.getWidth(), this.getHeight());
            int orientation = DisplayUtils.getScreenOrientation(this.getContext());
            int width;
            int height;
            if (this.mSquareViewFinder) {
                if (orientation != 1) {
                    height = (int) ((float) this.getHeight() * 0.625F);
                    width = height;
                } else {
                    width = (int) ((float) this.getWidth() * 0.625F);
                    height = width;
                }
            } else if (orientation != 1) {
                height = (int) ((float) this.getHeight() * 0.625F);
                width = (int) (1.4F * (float) height);
            } else {
                width = (int) ((float) this.getWidth() * 0.75F);
                height = (int) (0.75F * (float) width);
            }

            if (width > this.getWidth()) {
                width = this.getWidth() - 50;
            }

            if (height > this.getHeight()) {
                height = this.getHeight() - 50;
            }

            int leftOffset = (viewResolution.x - width) / 2;
            int topOffset = (viewResolution.y - height) / 2;
            this.mFramingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
        }
    }

    public void setFormats(List<BarcodeFormat> formats) {
        mFormats = formats;
        initMultiFormatReader();
    }

    public void setResultHandler(ResultHandler resultHandler) {
        mResultHandler = resultHandler;
    }

    public Collection<BarcodeFormat> getFormats() {
        if (mFormats == null) {
            return ALL_FORMATS;
        }
        return mFormats;
    }

    private void initMultiFormatReader() {
        Map<DecodeHintType, Object> hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, getFormats());
        mMultiFormatReader = new MultiFormatReader();
        mMultiFormatReader.setHints(hints);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
//        Log.d(TAG, "onPreviewFrame mResultHandler=" + mResultHandler);
        if (mResultHandler == null) {
            return;
        }
        try {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();
            int width = size.width;
            int height = size.height;

            if (DisplayUtils.getScreenOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
                byte[] rotatedData = new byte[data.length];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++)
                        rotatedData[x * height + height - y - 1] = data[x + y * width];
                }
                int tmp = width;
                width = height;
                height = tmp;
                data = rotatedData;
            }

            Result rawResult = null;
            PlanarYUVLuminanceSource source = buildLuminanceSource(data, width, height);

            if (source != null) {
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                try {
                    rawResult = mMultiFormatReader.decodeWithState(bitmap);
                } catch (ReaderException re) {
//                    re.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException aoe) {
                    aoe.printStackTrace();
                } finally {
                    mMultiFormatReader.reset();
                }
            }

            final Result finalRawResult = rawResult;

            if (finalRawResult != null) {
//                Log.e(TAG, "onPreviewFrame finalRawResultText=" + finalRawResult.getText());
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mResultHandler != null) {
                            mResultHandler.handleResult(finalRawResult);
                        }
                    }
                });
            }
//            Log.e(TAG, "onPreviewFrame setOneShotPreviewCallback");
            camera.setOneShotPreviewCallback(this);
        } catch (RuntimeException e) {
            // TODO: Terrible hack. It is possible that this method is invoked after camera is released.
            Log.e(TAG, e.toString(), e);
        }
    }

    /**
     * 重新启动相机预览
     */
    public void resumeCameraPreview(ResultHandler resultHandler) {
        mResultHandler = resultHandler;
        super.resumeCameraPreview();
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = getFramingRectInPreview(width, height);
        if (rect == null) {
            return null;
        }
        // Go ahead and assume it's YUV rather than die.
        PlanarYUVLuminanceSource source = null;

        try {
            source = new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                    rect.width(), rect.height(), false);
        } catch (Exception e) {
        }

        return source;
    }
}
