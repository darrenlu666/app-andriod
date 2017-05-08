package com.codyy.erpsportal.commons.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;


/**
 * Created by kmdai on 2015/4/22.
 */
public class ToastUtil {
    private static Toast toast;

    /**
     * @param text
     */
    public static void showToast( String text) {
        showToast(EApplication.instance(), text, Toast.LENGTH_SHORT);
    }

    /**
     * 显示snake bar .
     * @param text
     * @param view
     */
    public static void showSnake(String text ,View view ){
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }
    /**
     * @param context
     * @param text
     */
    public static void showToast(Context context, String text) {
        showToast(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, @StringRes int stringId) {
        showToast(context.getApplicationContext(), context.getString(stringId));
    }

    public static void showToast(Context context, String text, int duration) {
        if (context == null) return;
        if (toast == null) {
            toast = new Toast(context.getApplicationContext());
            View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.toast_layout, null);
            TextView textView = (TextView) view.findViewById(R.id.toast_text);
            textView.setText(text);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(duration);
            toast.setView(view);
        } else {
            View view = toast.getView();
            TextView textView = (TextView) view.findViewById(R.id.toast_text);
            textView.setText(text);
            toast.setView(view);
        }
        updateToastAnim(toast);
        toast.show();
    }

    /**
     * toast添加动画
     *
     * @param toast
     */
    private static void updateToastAnim(Toast toast) {
        if (toast != null) {
            // Toast 动画
            try {
                Object mTN = ReflectUtils.getField(toast, "mTN");
                if (mTN != null) {
                    Object mParams = ReflectUtils.getField(mTN, "mParams");
                    if (mParams != null && mParams instanceof WindowManager.LayoutParams) {
                        WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
                        params.windowAnimations = R.style.ToastAnimation;

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
