package com.codyy.erpsportal.commons.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.NetworkUtils;
import com.codyy.erpsportal.commons.services.FileDownloadService;
import com.codyy.erpsportal.commons.utils.TransferManager;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

/**
 * Created by gujiajia on 2015/6/1.
 */
public class ConnectionChangeReceiver extends BroadcastReceiver{

    private final static String TAG = "ConnectionChangeReceiver";
    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Cog.d(TAG, "onReceive intent:" + intent);
        ImageFetcher.getInstance(context).updateState(context);

        //通知正在播放的视频组件
        if( null == mLocalBroadcastManager){
            mLocalBroadcastManager =   LocalBroadcastManager.getInstance(context);
        }

        //通知下载任务停止
        //如果关闭了 xG网络
        if(!SettingUtils.getInstance().getCacheState(context)){
            //变为Wi-Fi,通知下载继续
            if(NetworkUtils.isNetWorkTypeWifi(context)){
                Cog.i(TAG,"变为Wi-Fi,通知下载继续");
                UserInfo userInfo = UserInfoKeeper.obtainUserInfo();
                if (userInfo != null && !TextUtils.isEmpty( userInfo.getBaseAreaId())) {
                    FileDownloadService.httpDownload(context, userInfo.getBaseUserId());
                }
                mLocalBroadcastManager.sendBroadcastSync(new Intent(WifiBroadCastReceiver.ACTION_WIFI_OPEN));
                //网络变为移动网络，通知下载停止
            }else{
                Cog.i(TAG,"网络变为移动网络，通知下载停止");
                TransferManager.instance().cancelDownloadTasks();

                mLocalBroadcastManager.sendBroadcastSync(new Intent(WifiBroadCastReceiver.ACTION_WIFI_CLOSE));
            }
        }
    }

}
