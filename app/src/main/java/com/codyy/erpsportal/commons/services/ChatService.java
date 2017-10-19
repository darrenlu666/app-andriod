package com.codyy.erpsportal.commons.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codyy.cocolibrary.COCO;
import com.codyy.cocolibrary.COCOListener;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.IMeetingService;
import com.codyy.erpsportal.commons.models.entities.MeetingConfig;
import com.codyy.erpsportal.commons.utils.CoCoUtils;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.onlinemeetings.utils.SendCMDUtils;

import java.net.URLEncoder;

import de.greenrobot.event.EventBus;

/**
 * 视频会议coco服务.
 * 聊天服务
 * created by poe 2017/09/22 (coco3.0)
 */
public class ChatService extends Service {
    private static final String TAG = "ChatService";
    private MeetingConfig mMeetingConfig;

    /**
     * 重连coco3.0.
     */
    private void reconnect() {
        String url = "ws://"+mMeetingConfig.getIp() + ":"+mMeetingConfig.getPort()+"/ws";
        COCO.getDefault().reConnectCOCO(url);
    }


    private IMeetingService.Stub meetingService = new IMeetingService.Stub() {
        /**
         * 连接coco
         */
        @Override
        public void connectCoCo() throws RemoteException {
        }

        /**
         * 退出系统
         */
        @Override
        public void exitSystem() throws RemoteException {
            releaseResources();
        }

        /**
         * 登录coco服务器
         */
        @Override
        public void login() throws RemoteException {
            sendMsgs(SendCMDUtils.login(mMeetingConfig));
        }

        /**
         * 退出coco服务器
         */
        @Override
        public void loginOut() throws RemoteException {
            releaseResources();
        }

        /**
         * 发送心跳
         */
        @Override
        public void keepAlive() throws RemoteException {
            Cog.d("meetingService", "--->keepAlive is disposed!");
        }

        /**
         * 上线消息
         */
        @Override
        public void noticeOnLine() throws RemoteException {
            sendMsgs(SendCMDUtils.addGroup(mMeetingConfig));
        }

        /**
         * 获取在线用户
         */
        @Override
        public void getGroupOnlineUser() throws RemoteException {
            sendMsgs(SendCMDUtils.getGroupOnlineUser(mMeetingConfig));
        }

        /**
         * 发送群聊 COCO消息
         */
        @Override
        public void sendMsg(String msg) throws RemoteException {
            sendMsgs(SendCMDUtils.sendMsg(mMeetingConfig, msg));
        }

        /**
         * 发送私聊 COCO消息
         */
        @Override
        public void sendSignalMsg(String msg, String id) throws RemoteException {
            sendMsgs(SendCMDUtils.sendSignalMsg(mMeetingConfig, msg, id));
        }

        /**
         *设置免打扰
         * @param id 设置免打扰用户的ID
         * @param b 当b= true 免打扰,b=false 取消免打扰;
         */
        @Override
        public void setDisturb(String id, boolean b) {
            sendMsgs(SendCMDUtils.setDisturb(mMeetingConfig, id, b));
        }

        /**
         *主持人设置某人发言
         * @param id 设置发言人的ID
         * @param b 当b= true 发言,b=false 取消发言;
         */
        @Override
        public void setSpokesman(String id, boolean b) {
            sendMsgs(SendCMDUtils.setSpokesman(mMeetingConfig, id, b));
        }

        /**
         *主持人取消某人发言
         * @param id 被取消人的ID
         */
        @Override
        public void setCancelSpeak(String id) {
            sendMsgs(SendCMDUtils.setCancelSpeak(mMeetingConfig, id,mMeetingConfig.getGid()));
        }

        /**
         *某人申请发言
         * @param id 设置申请发言人的ID
         * @param b 当b= true 发言,b=false 取消发言;
         */
        @Override
        public void setProposerSpeak(String id,String to, boolean b) {
            if(b){
                sendMsgs(SendCMDUtils.setProposerSpeak(mMeetingConfig, id,to));
            }else{
                sendMsgs(SendCMDUtils.setCancelSpeak(mMeetingConfig, id,to));
            }
        }

        /**
         *主持人请出人员
         * @param id 设置请出人员ID
         */
        @Override
        public void setAssignPeopleOut(String id) {
            sendMsgs(SendCMDUtils.setAssignPeopleOut(mMeetingConfig, id));
        }

        /**
         *切演示模式
         */
        @Override
        public void setDemonstrationMode() {
            sendMsgs(SendCMDUtils.setDemonstrationMode(mMeetingConfig));
        }

        /**
         *切视频模式
         */
        @Override
        public void setVideoMode() {
            sendMsgs(SendCMDUtils.setVideoMode(mMeetingConfig));
        }

        /**
         *设置某人禁言
         * @param id 设置禁言人的ID
         * @param b 当b= true 禁言,b=false 取消禁言;
         */
        @Override
        public void setForbidSpeak(String id, boolean b) {
            sendMsgs(SendCMDUtils.setForbidSpeak(mMeetingConfig, id, b));
        }

        /**
         *绑定参数并连接coco服务器
         */
        @Override
        public void bindConfig(MeetingConfig config) {
            mMeetingConfig = config;
            new InitSocketThread().start();
        }

        @Override
        public void createTable(String groupId, String tableName) throws RemoteException {
            sendMsgs(SendCMDUtils.createTableJson(groupId,tableName));
        }

        /**
         *演示文档 （白板的COCO消息比较特殊）,发送文档演示前，先发一条切换演示模式的消息
         */
        @Override
        public void setDemonstrationDoc(String to, String current, String from_null, String url, String id, String filename) {
            sendMsgs(SendCMDUtils.setDemonstrationDoc(mMeetingConfig, url,current, id, filename));
        }

        /**
         *文档翻页
         */
        @Override
        public void setChangeDoc(String to, String current, String owner) {
            sendMsgs(SendCMDUtils.setChangeDoc(mMeetingConfig, to, current, owner));
        }

        /**
         *文档缩放
         */
        @Override
        public void setDocZoom(String to, String index, String from_null, String owner) {
            sendMsgs(SendCMDUtils.setDocZoom(mMeetingConfig, to, index, from_null, owner));
        }

        /**
         * 删除文档
         * @param to
         * @param id 文档Id
         */
        @Override
        public void  setDelectDoc(String to, String id) {
            sendMsgs(SendCMDUtils.setDelectDoc(mMeetingConfig, to, id));
        }



    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return meetingService;
    }

    /**
     * 发送消息(外部方法)
     */
    public void sendMsgs(String msg) {
        sendMsgThread(msg);
    }

    /**
     * 发送消息(线程安全-在子线程中发送命令)
     * modified by poe
     * 修复7.0中的主线程中进行网络请求错误
     */
    private boolean sendMsgThread(String msg) {
        Cog.d(TAG, "-------------->send = " + msg);
        if (null == COCO.getDefault()) {
            return false;
        }
        COCO.getDefault().post(msg+ "\0");
        return true;
    }

    /**
     * 解绑COCOService.
     */
    private void releaseResources() {
        COCO.getDefault().unbind(getApplicationContext());
    }

    /**
     * 初始化socket
     */
    class InitSocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            String url = "ws://"+mMeetingConfig.getIp() + ":"+mMeetingConfig.getPort()+"/ws";
            COCO.getDefault().bind(getApplicationContext(), url, mCoCoListener);
        }
    }

    /**
     * 监听coco消息.
     * 打开/关闭
     * 接受服务器的消息.
     */
    private COCOListener mCoCoListener = new COCOListener() {
        @Override
        public void onOpen() {
            Log.i(TAG, "onOpen================>");
            EventBus.getDefault().post(new String(Constants.CONNECT_COCO));
        }

        @Override
        public void onMessage(String message) {
            Log.i(TAG, "onMessage==>" + message);
            try {
                CoCoUtils.parseJson(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.i(TAG, "onClose================>");
            // TODO: 17-9-18 连接关闭后释放本地的资源.
            releaseResources();
        }

        @Override
        public void onError(Exception e) {
            Log.e(TAG, "onError==>" + e.getMessage());
            // TODO: 17-9-18 出错后需要重连.  
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseResources();
    }
}
