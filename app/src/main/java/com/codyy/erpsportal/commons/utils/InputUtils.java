package com.codyy.erpsportal.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.codyy.erpsportal.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kmdai on 16-1-28.
 */
public class InputUtils {
    public final static String SHARE_NAME = "input_mode";
    /**
     * 匹配格式
     */
    private final static String REXG_STRING = "(\\[[^\\]\\[]*\\])";
    private final static Map<String, Integer> EMOJICON = new HashMap<>();

    static {
        EMOJICON.put("[傲慢]", R.drawable.smiley_0);
        EMOJICON.put("[白眼]", R.drawable.smiley_1);
        EMOJICON.put("[便便]", R.drawable.smiley_2);
        EMOJICON.put("[鄙视]", R.drawable.smiley_3);
        EMOJICON.put("[擦汗]", R.drawable.smiley_4);
        EMOJICON.put("[菜刀]", R.drawable.smiley_5);
        EMOJICON.put("[呲牙]", R.drawable.smiley_6);
        EMOJICON.put("[大哭]", R.drawable.smiley_7);
        EMOJICON.put("[得意]", R.drawable.smiley_8);
        EMOJICON.put("[发怒]", R.drawable.smiley_9);
        EMOJICON.put("[尴尬]", R.drawable.smiley_10);
        EMOJICON.put("[害羞]", R.drawable.smiley_11);
        EMOJICON.put("[汗]", R.drawable.smiley_12);
        EMOJICON.put("[憨笑]", R.drawable.smiley_13);
        EMOJICON.put("[花]", R.drawable.smiley_14);
        EMOJICON.put("[惊恐]", R.drawable.smiley_15);
        EMOJICON.put("[惊讶]", R.drawable.smiley_16);
        EMOJICON.put("[可爱]", R.drawable.smiley_17);
        EMOJICON.put("[抠鼻]", R.drawable.smiley_18);
        EMOJICON.put("[耍酷]", R.drawable.smiley_19);
        EMOJICON.put("[流泪]", R.drawable.smiley_20);
        EMOJICON.put("[难过]", R.drawable.smiley_21);
        EMOJICON.put("[撇嘴]", R.drawable.smiley_22);
        EMOJICON.put("[敲打]", R.drawable.smiley_23);
        EMOJICON.put("[亲亲]", R.drawable.smiley_24);
        EMOJICON.put("[色]", R.drawable.smiley_25);
        EMOJICON.put("[胜利]", R.drawable.smiley_26);
        EMOJICON.put("[示爱]", R.drawable.smiley_27);
        EMOJICON.put("[衰]", R.drawable.smiley_28);
        EMOJICON.put("[睡]", R.drawable.smiley_30);
        EMOJICON.put("[微笑]", R.drawable.smiley_31);
        EMOJICON.put("[偷笑]", R.drawable.smiley_32);
        EMOJICON.put("[吐]", R.drawable.smiley_33);
        EMOJICON.put("[委屈]", R.drawable.smiley_34);
        EMOJICON.put("[调皮]", R.drawable.smiley_35);
        EMOJICON.put("[心]", R.drawable.smiley_36);
        EMOJICON.put("[心裂]", R.drawable.smiley_37);
        EMOJICON.put("[嘘]", R.drawable.smiley_38);
        EMOJICON.put("[阴险]", R.drawable.smiley_39);
        EMOJICON.put("[疑问]", R.drawable.smiley_40);
        EMOJICON.put("[再见]", R.drawable.smiley_41);
        EMOJICON.put("[炸弹]", R.drawable.smiley_42);
        EMOJICON.put("[抓狂]", R.drawable.smiley_43);
        EMOJICON.put("[猪头]", R.drawable.smiley_44);
    }

    public static SpannableStringBuilder setEmojiSpan(Context context, SpannableStringBuilder spannableStringBuilder, int emojiSize) {
        Pattern pattern = Pattern.compile(REXG_STRING);
        Matcher matcher = pattern.matcher(spannableStringBuilder.toString());
        while (matcher.find()) {
            Drawable drawable = getDrawable(context, matcher.group());
            if (drawable != null) {
                drawable.setBounds(0, 0, emojiSize, emojiSize);
                VerticalImageSpan imageSpan = new VerticalImageSpan(drawable);
                spannableStringBuilder.setSpan(imageSpan, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder setEmojiSpanEdit(EditText editText, String str, int emojiSize) {
        Pattern pattern = Pattern.compile(REXG_STRING);
        Matcher matcher = pattern.matcher(str);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        while (matcher.find()) {
            Drawable drawable = getDrawable(editText.getContext(), matcher.group());
            if (drawable != null) {
                drawable.setBounds(0, 0, emojiSize, emojiSize);
                VerticalImageSpan imageSpan = new VerticalImageSpan(drawable);
                spannableStringBuilder.setSpan(imageSpan, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
//        editText.getText().append(spannableStringBuilder);
        editText.getText().insert(editText.getSelectionStart(), spannableStringBuilder);
        return spannableStringBuilder;
    }

    private static Drawable getDrawable(Context context, String name) {
        if (EMOJICON.containsKey(name)) {
            int a = EMOJICON.get(name);
            Drawable drawable = context.getResources().getDrawable(a);
            return drawable;
        }
        return null;
    }

    public static void hideSoftInputFromWindow(Activity activity, View view) {
        InputMethodManager mInputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view.hasFocus() || mInputMethodManager.isActive(view)) {
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
            //取消焦点
            view.setFocusable(false);
            //设置点击时获取焦点
            view.setFocusableInTouchMode(true);
        }
    }

    public static void showSoftInputFromWindow(Activity activity, View view) {
        InputMethodManager mInputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (mInputMethodManager != null) {
            //取消焦点
            view.setFocusable(true);
            view.requestFocus();
            //设置点击时获取焦点
            view.setFocusableInTouchMode(true);
            mInputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static int getKeyboardHeight(Activity paramActivity) {
        int height = getScreenHeight(paramActivity) - getStatusBarHeight(paramActivity)
                - getAppHeight(paramActivity);
        SharedPreferences sp = paramActivity.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
        if (height == 0) {
            height = sp.getInt("KeyboardHeight", UIUtils.dip2px(paramActivity,300));
        } else {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("KeyboardHeight", height);
            editor.commit();
        }
        return height;
    }

    /**
     * 可见屏幕高度
     **/
    public static int getAppHeight(Activity paramActivity) {
        Rect localRect = new Rect();
        paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        return localRect.height();
    }

    /**
     * 屏幕分辨率高
     **/
    public static int getScreenHeight(Activity paramActivity) {
        Display display = paramActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.heightPixels;
    }

    /**
     * statusBar高度
     **/
    public static int getStatusBarHeight(Activity paramActivity) {
        Rect localRect = new Rect();
        paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        return localRect.top;

    }

    public static int getAppContentHeight(Activity paramActivity) {
        return SystemUtils.getScreenHeight(paramActivity) - getStatusBarHeight(paramActivity)
                - getActionBarHeight(paramActivity) - getKeyboardHeight(paramActivity);
    }

    /**
     * 获取actiobar高度
     **/
    public static int getActionBarHeight(Activity paramActivity) {
        if (true) {
            return UIUtils.dip2px(paramActivity, 56);
        }
        int[] attrs = new int[]{android.R.attr.actionBarSize};
        TypedArray ta = paramActivity.obtainStyledAttributes(attrs);
        return ta.getDimensionPixelSize(0, UIUtils.dip2px(paramActivity, 56));
    }

    /**
     * 垂直居中的ImageSpan
     *
     * @author KenChung
     */
    public static class VerticalImageSpan extends ImageSpan {

        public VerticalImageSpan(Drawable drawable) {
            super(drawable);
        }

        public int getSize(Paint paint, CharSequence text, int start, int end,
                           Paint.FontMetricsInt fontMetricsInt) {
            Drawable drawable = getDrawable();
            Rect rect = drawable.getBounds();
            if (fontMetricsInt != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;

                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;

                fontMetricsInt.ascent = -bottom;
                fontMetricsInt.top = -bottom;
                fontMetricsInt.bottom = top;
                fontMetricsInt.descent = top;
            }
            return rect.right;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end,
                         float x, int top, int y, int bottom, Paint paint) {
            Drawable drawable = getDrawable();
            canvas.save();
            int transY = 0;
            transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
            canvas.translate(x, transY);
            drawable.draw(canvas);
            canvas.restore();
        }
    }
}
