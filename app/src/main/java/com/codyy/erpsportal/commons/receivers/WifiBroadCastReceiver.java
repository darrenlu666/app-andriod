package com.codyy.erpsportal.commons.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.codyy.erpsportal.commons.utils.Cog;

/**
 * wifi关闭接收器
 */
public class WifiBroadCastReceiver extends BroadcastReceiver {

    private final static String TAG = "WifiBroadCastReceiver";

    /**
     * 关闭wifi的action通知
     */
    public static final String ACTION_WIFI_CLOSE = "com.coddy.action.wifi.close";
    /**
     * 打开wifi的action通知
     */
    public static final String ACTION_WIFI_OPEN = "com.coddy.action.wifi.open";

    private WifiChangeListener listener;

    public WifiBroadCastReceiver(WifiChangeListener listener){
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(listener != null ){
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            Cog.d(TAG, "mobNet connected = ", mobNetInfo.isConnected());
            Cog.d(TAG, "wifiNet connected = ", wifiNetInfo.isConnected());

            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                if(listener != null )listener.onWifiClose();
            } else {
                if(listener != null )listener.onWifiOpen();
            }
        }
    }

    /**
     * wifi 关闭监听器
     */
   public interface WifiChangeListener{

        /**
         * 关闭Wi-Fi
         */
        void onWifiClose();

        /**
         * 打开Wi-Fi
         */
        void onWifiOpen();
    }
}