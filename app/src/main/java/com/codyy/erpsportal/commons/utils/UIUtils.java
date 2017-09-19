package com.codyy.erpsportal.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.models.dao.UserInfoDao;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public final class UIUtils {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("hh:mm:ss aa", Locale.US);

    /**
     * Localizable Number Format constant for the current default locale.
     */
    private static NumberFormat NUMBER_FORMAT0; // localized "#,##0"
    private static NumberFormat NUMBER_FORMAT1; // localized "#,##0.0"

    private static String[] BYTE_UNITS = new String[] { "b", "KB", "Mb", "Gb", "Tb" };

    public static String GENERAL_UNIT_KBPSEC = "KB/s";

    static {
        NUMBER_FORMAT0 = NumberFormat.getNumberInstance(Locale.getDefault());
        NUMBER_FORMAT0.setMaximumFractionDigits(0);
        NUMBER_FORMAT0.setMinimumFractionDigits(0);
        NUMBER_FORMAT0.setGroupingUsed(true);

        NUMBER_FORMAT1 = NumberFormat.getNumberInstance(Locale.getDefault());
        NUMBER_FORMAT1.setMaximumFractionDigits(1);
        NUMBER_FORMAT1.setMinimumFractionDigits(1);
        NUMBER_FORMAT1.setGroupingUsed(true);
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = EApplication.instance().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = EApplication.instance().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 处理特殊字符
     * 如果含有 <>替换为我们自己的数据 {&lg&gt}
     * @param input
     * @return
     */
    public static String filterCharacter(String input){
        if(null == input) return "";

        String result = input ;

        result = result.replaceAll("\"","&quot;");
        result = result.replaceAll("'","&apos;");
        if(result.contains("<")){
            result = result.replaceAll("<","&lt;");
        }
        if(result.contains(">")){
            result = result.replaceAll(">","&gt;");
        }

        return  result ;
    }


    /**
     * 判断字符串是否为时间戳.
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static void showToastMessage(Context context, String message, int duration) {
        if (context != null && message != null) {
            Toast.makeText(context, message, duration).show();
        }
    }

    public static void showShortMessage(Context context, String message) {
        showToastMessage(context, message, Toast.LENGTH_SHORT);
    }

    public static void showLongMessage(Context context, String message) {
        showToastMessage(context, message, Toast.LENGTH_LONG);
    }


    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String getBytesInHuman(float size) {
        int i = 0;
        for (i = 0; size > 1024; i++) {
            size /= 1024f;
        }
        return String.format(Locale.US, "%.2f %s", size, BYTE_UNITS[i]);
    }

    public static String getBytesInHuman(double size) {
        int i = 0;
        for (i = 0; size > 1024; i++) {
            size /= 1024f;
        }
        return String.format(Locale.US, "%.2f %s", size, BYTE_UNITS[i]);
    }

    /**
     * Converts an rate into a human readable and localized KB/s speed.
     */
    public static String rate2speed(double rate) {
        return NUMBER_FORMAT0.format(rate) + " " + GENERAL_UNIT_KBPSEC;
    }

    //main ui

    /**
     * 弹出消息
     *
     * @param context
     * @param msg
     * @param duration
     */
    public static void toast(Context context, String msg, int duration) {
        if (context == null) return;
        ToastUtil.showToast(context, msg, duration);
    }

    /**
     * 弹出消息
     *
     * @param resId
     * @param duration
     */
    public static void toast(@StringRes int resId, int duration) {
        ToastUtil.showToast(EApplication.instance(), EApplication.instance().getString(resId), duration);
    }

    /**
     * 弹出消息
     *
     * @param msg 消息内容
     * @param duration
     */
    public static void toast(String msg, int duration) {
        ToastUtil.showToast(EApplication.instance(), msg, duration);
    }

    /**
     * 添加退出动画
     */
    public static void addExitTranAnim(Activity activity) {
        activity.overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
    }

    /**
     * 添加进入动画
     *
     * @param activity
     */
    public static void addEnterAnim(Activity activity) {
        activity.overridePendingTransition(R.anim.slidemenu_show, R.anim.layout_hide);
    }

    /**
     * 获取ViewPager中的Fragment的TAG标签
     *
     * @param viewPagerId ViewPager的id
     * @param id itemId,通过FragmentPagerAdapter的getItemId(long position)获取
     * @return 碎片TAG
     */
    public static String obtainFragmentTag(int viewPagerId, long id) {
        return "android:switcher:" + viewPagerId + ":" + id;
    }


    /**
     * 获取 缩略图的 LOGIN
     * @param origin
     * @return
     */
    public static String getSmallImage(String origin){

        String suffix   =   origin.substring(origin.lastIndexOf("."));
        String small = origin.substring(0,origin.indexOf("."))+".small"+suffix;

        return URLConfig.IMAGE_URL+small;
    }

    /**
     * dp转化为 px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 设置为竖屏/退出全屏
     * @param activity
     */
    public static void setPortrait(Activity activity){
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //退出全屏
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(attrs);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 设置为横屏/全屏
     * @param activity
     */
    public  static void setLandscape(Activity activity){
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 过滤null字符串
     * @param input
     * @return
     */
    public static String filterNull(String input){

        String result = "";

        if(input == null){
            result = "";
        }else if(input.contains("null")){
            result = input.replace("null","");
        }else{
            result = input;
        }
        return result;
    }
    /**
     * 添加可点击的“没有数据，点击刷新”
     *
     * @param context
     * @param tv
     * @param span
     */
    public static void setEmptyViewClick(Context context, TextView tv, MyClickableSpan span) {
        if (context == null) return;//上下文已为空，不更新界面
        SpannableString spStr = new SpannableString(context.getString(R.string.click_to_refresh));
        spStr.setSpan(span, 5, 9, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(spStr);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static UserInfo getUser() {
        UserInfo userInfo;
        userInfo = UserInfoDao.find(EApplication.instance());
        return userInfo;
    }

    /**
     * 返回一个width 扩充全屏的item
     * @param layoutId
     * @param parent
     * @return
     */
    public static View getMatchWidthView(@LayoutRes int layoutId, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return  view;
    }

    public abstract static class MyClickableSpan extends ClickableSpan {

        private Context context;

        public MyClickableSpan(Context context) {
            this.context = context;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(context.getResources().getColor(R.color.green));
        }

        @Override
        public abstract void onClick(View widget);
    }

}