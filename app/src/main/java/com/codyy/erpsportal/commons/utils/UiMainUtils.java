package com.codyy.erpsportal.commons.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.codyy.erpsportal.EApplication;

/**
 * 首页Ui工具类
 * Created by poe on 16-1-12.
 */
public class UiMainUtils {

    /**
     * 替换旧的api res.getColor(int color)
     * @param color
     * @return
     */
    public static int getColor(int color){
        return ContextCompat.getColor(EApplication.instance(),color);
    }

    /**
     * 三大应用的评论中的头像
     * @param server
     * @param headPic
     * @return
     */
    public static String getFullImagePath(String server,String headPic){
        if(null == server || headPic == null ) return "";
        return server+"/images/"+headPic;
    }

    public static void setBlogSendText(TextView tv ,String str){
        if(null == tv ) return;
        tv.setCompoundDrawables(null,null,null,null);
        tv.setText(str);
        tv.setGravity(Gravity.CENTER);
    }
    /**
     * 设置图片的DrawableLeft
     * @param tv
     * @param resId
     */
    public static void setDrawableLeft(TextView tv , @DrawableRes int resId , String str){
        Drawable drawable = ContextCompat.getDrawable(EApplication.instance() , resId);
        if(null != drawable){
            //set drawableLeft
            tv.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
            drawable.setBounds(0,0,drawable.getMinimumWidth() , drawable.getMinimumHeight());
            tv.setCompoundDrawables(drawable , null ,null ,null);
            //set text
            tv.setText(str);
        }
    }
    /**
     * 防止viewItem 在RecyclerView不填充Match_parent
     * @param context
     * @param resId
     * @return
     */
    public static View setMatchWidthAndWrapHeight(Context context , @LayoutRes int resId ){
        View itemView = LayoutInflater.from(context).inflate(resId,null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT ,ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(params);
        return itemView;
    }

    public static View setMatchAndActionBarHeight(Context context , @LayoutRes int resId ){
        View itemView = LayoutInflater.from(context).inflate(resId,null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT ,ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(params);
        return itemView;
    }
    /**
     * 窗口显示前显示输入法软键盘
     * @param inputMethodManager
     */
    public static void showKeyBoard(InputMethodManager inputMethodManager)
    {
        if(null != inputMethodManager){
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);// 调用此方法才能自动打开输入法软键盘
        }
    }

    /**
     * 隐藏输入法软键盘4
     * @param inputMethodManager
     */
    public static void hideKeyBoard(InputMethodManager inputMethodManager)
    {
        if(null != inputMethodManager){
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);// 输入法软键盘打开时关闭,关闭时打开
        }
    }
    /**
     * 4.4设置沉浸模式
     * @param act 必须为activity
     * @param color res引用
     */
    // TODO: 16-1-12  KitKat设置沉浸模式
    public static void setNavigationTintColor(Activity act , int color){
        /** style中定义了 values-19**/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(act , true);
        }
        //为状态栏着色
        SystemBarTintManager tintManager = new SystemBarTintManager(act);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(color);
    }

    // TODO: 16-1-12 设置导航栏是否为透明  added in 4.4.2
    @TargetApi(19)
    private static void setTranslucentStatus(Activity act , boolean on  ) {
        Window win = act.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
