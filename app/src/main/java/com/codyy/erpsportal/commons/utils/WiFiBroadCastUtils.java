package com.codyy.erpsportal.commons.utils;

import android.content.IntentFilter;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.interfaces.IFragmentMangerInterface;
import com.codyy.erpsportal.commons.receivers.WifiBroadCastReceiver;
import com.codyy.erpsportal.commons.widgets.MyDialog;

/**
 * Created by caixingming on 2015/6/5.
 * 注册、解绑 Wifi相关的广播类
 */
public class WiFiBroadCastUtils {
    private static final String TAG = "WiFiBroadCastUtils";
    private LocalBroadcastManager mLocalBroadcastManager;
    private WifiBroadCastReceiver myBroadcastReceive;
    private PlayStateListener   mPlayStateListener;
    private MyDialog mNetDialog = null ;
    private IFragmentMangerInterface mFragmentManagerInterface = null;

    /**
     *
     * @param mPlayStateListener
     * 注册Wi-fi通知广播
     * 解绑：：destroy()
     */
    public WiFiBroadCastUtils(IFragmentMangerInterface fragmentMangerInterface,PlayStateListener mPlayStateListener) {
        this.mFragmentManagerInterface = fragmentMangerInterface;
        this.mPlayStateListener =   mPlayStateListener;
        registerMyBroadCast();
    }

    //注册
    private void registerMyBroadCast(){
        //注册广播
        mLocalBroadcastManager  =   LocalBroadcastManager.getInstance(EApplication.instance());
        myBroadcastReceive  =   new WifiBroadCastReceiver(new WifiBroadCastReceiver.WifiChangeListener(){
            @Override
            public void onWifiClose() {
//                "当前为非Wi-Fi状态,10分钟大约40M\n是否继续观看？"
                Cog.i(TAG," onWifiClose");
                if(null != mPlayStateListener) mPlayStateListener.stop();
                if(null != mNetDialog&& mNetDialog.isVisible()) mNetDialog.dismissAllowingStateLoss();
                mNetDialog = initDialog();
                try{
                    Cog.i(TAG," onWifiClose prepare alert !~"+mFragmentManagerInterface);
                    if(mFragmentManagerInterface !=null && mFragmentManagerInterface.getNewFragmentManager() != null){
                        mNetDialog.showAllowStateLoss(mFragmentManagerInterface.getNewFragmentManager(), "cache_clear");
                        Cog.i(TAG," onWifiClose prepare alert success!~"+mFragmentManagerInterface);
                    }
                }catch (IllegalStateException e){
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onWifiOpen() {
                Cog.i(TAG," onWifiOpen");
                if(null != mPlayStateListener){
                    mPlayStateListener.play();
                }
            }
        } );

        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(WifiBroadCastReceiver.ACTION_WIFI_CLOSE);
        iFilter.addAction(WifiBroadCastReceiver.ACTION_WIFI_OPEN);

        mLocalBroadcastManager.registerReceiver(myBroadcastReceive,iFilter);
    }

    private MyDialog initDialog(){
            MyDialog dialog = MyDialog.newInstance(EApplication.instance().getString(R.string.txt_dialog_wifi_play_video_tips), MyDialog.DIALOG_STYLE_TYPE_0, new MyDialog.OnclickListener() {
                @Override
                public void leftClick(MyDialog myDialog) {
                    myDialog.dismiss();
                }

                @Override
                public void rightClick(MyDialog myDialog) {
                    if(null != mPlayStateListener) mPlayStateListener.play();
                    myDialog.dismiss();
                }

                @Override
                public void dismiss() {

                }
            });
       return  dialog;
    }

    /**
     * 解除广播的绑定
     */
    public void destroy(){
        //解除广播接收器
        if(myBroadcastReceive != null && mLocalBroadcastManager != null){
            mLocalBroadcastManager.unregisterReceiver(myBroadcastReceive);
            mLocalBroadcastManager  = null;
            this.myBroadcastReceive = null;
        }
//        if(null != mContextWeakReference) mContextWeakReference.clear();
        this.mPlayStateListener =   null;
    }

    /**
     * 监听 控制状态并执行
     */
    public interface PlayStateListener{

        /**
         * 执行播放
         */
        void play();

        /**
         * 执行停止
         */
        void stop();

    }
}
