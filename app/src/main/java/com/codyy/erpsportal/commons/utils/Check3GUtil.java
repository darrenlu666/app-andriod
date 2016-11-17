package com.codyy.erpsportal.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.widgets.MyDialog;
import java.lang.ref.WeakReference;

/**
 * Created by poe on 15-7-27.
 * 检测3G环境工作类
 */
public class Check3GUtil {
    private String TAG = "Check3GUtil";
    private static Check3GUtil mInstance ;
    private Check3GUtil(){
    }

    public synchronized static Check3GUtil instance(){
        if(null == mInstance){
            mInstance = new Check3GUtil();
        }
        return  mInstance;
    }

    /**
     * 监听网络类型/分类处理
     * @param listener
     */
    public void CheckNetType(Context context , OnWifiListener listener){
        WeakReference<Context> owner = new WeakReference<>(context);
        FragmentManager fragmentManager  ;
        Activity activity  ;
        MyDialog dialog = initDialog(listener);
        if(!VideoDownloadUtils.isConnected(context)){
            if(listener!= null){
                listener.onNetError();
            }
        }else{
            if(!NetworkUtils.isNetWorkTypeWifi(context)){//3G/4G
                if(null != owner.get()){
                    activity = (Activity) context;
                    if(null != activity && activity instanceof FragmentActivity){
                        fragmentManager = ((FragmentActivity)activity).getSupportFragmentManager();
                        dialog.show(fragmentManager,"check3G");
                    }
                }
            }else{//wifi 继续 ...
                if(listener!= null){
                    listener.onContinue();
                }
            }
        }
    }

    private static MyDialog initDialog(final OnWifiListener listener) {
        return MyDialog.newInstance(EApplication.instance().getString(R.string.txt_dialog_wifi_play_video_tips),
                MyDialog.DIALOG_STYLE_TYPE_0,
                "取消观看",
                "继续观看" ,
                new MyDialog.OnclickListener() {
                    @Override
                    public void leftClick(MyDialog myDialog) {
                        myDialog.dismiss();
                    }

                    @Override
                    public void rightClick(MyDialog myDialog) {
                        myDialog.dismiss();
                        if(null != listener){
                            listener.onContinue();
                        }
                    }

                    @Override
                    public void dismiss() {

                    }
                });
    }

    /**
     * 网络变化接口类
     */
    public interface  OnWifiListener{
        /**
         * 没有网络
         */
        void onNetError();

        /**
         * 在3G情况下->继续
         */
        void onContinue();
    }
}
