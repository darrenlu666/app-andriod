package com.codyy.erpsportal.commons.services;

import android.os.Handler;
import android.os.RemoteException;

import com.codyy.erpsportal.commons.models.entities.MeetingConfig;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.CommandUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;

/**
 * Created by yangxinwu on 2015/9/5.
 */
public class SocketConnectUtils {
    public static SocketConnectUtils mSocUtils = null;
    private static final String TAG = "ChatService";
    private static final long HEART_BEAT_RATE = 30 * 1000;
    public static final String HOST = "coco.ppmeeting.com";
    public static final int PORT = 1888;
    private String mExtraStr;
    private MeetingConfig mMeetingConfig;
    private ReadThread mReadThread;
    private WeakReference<Socket> mSocket;
    // For heart Beat
    private Handler mHandler = new Handler();

    public SocketConnectUtils() {
        new InitSocketThread().start();
    }



    public static SocketConnectUtils getInstances() {
        if (mSocUtils == null ) {
            mSocUtils = new SocketConnectUtils();
        }
       return mSocUtils;
    }





    private Runnable heartBeatRunnable = new Runnable() {

        @Override
        public void run() {
            Cog.d(TAG, "-------------->send = xxx");

            boolean isSuccess = sendMsgs(CommandUtil.keepLive(mMeetingConfig));
            if (!isSuccess) {
                mHandler.removeCallbacks(heartBeatRunnable);
                mReadThread.release();
                Cog.d(TAG, "------releaseLastSocket-------->尝试重连");
                releaseLastSocket(mSocket);
                Cog.d(TAG, "-------------->尝试重连");
                new InitSocketThread().start();
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    /**
     * 连接coco
     */
    public void connectCoCo() throws RemoteException {
    }
    /**
     * 退出系统
     */
    public void exitSystem() throws RemoteException {
    }
    /**
     * 登录coco服务器
     */
    public void login() throws RemoteException {
        sendMsgs(CommandUtil.login(mMeetingConfig));
    }
    /**
     * 退出coco服务器
     */
    public void loginOut() throws RemoteException {
        releaseResources();
    }
    /**
     * 发送心跳
     */
    public void keepAlive() throws RemoteException {
        sendMsgs(CommandUtil.keepLive(mMeetingConfig));
        Cog.d("PullXmlUtilskeepAlive", "--->keepAlive");
    }
    /**
     * 上线消息
     */
    public void noticeOnLine() throws RemoteException {
        sendMsgs(CommandUtil.noticeOnLine(mMeetingConfig));
    }
    /**
     * 获取在线用户
     */
    public void getGroupOnlineUser() throws RemoteException {
        sendMsgs(CommandUtil.getGroupOnlineUser(mMeetingConfig, mExtraStr));
    }

    /**
     * 发送群聊 COCO消息
     */
    public void sendMsg(String msg) throws RemoteException {
        sendMsgs(CommandUtil.sendMsg(mMeetingConfig, msg, mExtraStr));
    }

    /**
     * 发送私聊 COCO消息
     */
    public void sendSignalMsg(String msg, String id) throws RemoteException {
        sendMsgs(CommandUtil.sendSignalMsg(mMeetingConfig, msg, mExtraStr, id));
    }

    /**
     *设置免打扰
     * @param id 设置免打扰用户的ID
     * @param b 当b= true 免打扰,b=false 取消免打扰;
     */
    public void setDisturb(String id,boolean b){
        sendMsgs(CommandUtil.setDisturb(mMeetingConfig, id, b, mExtraStr));
    }

    /**
     *主持人设置某人发言
     * @param id 设置发言人的ID
     * @param b 当b= true 发言,b=false 取消发言;
     */
    public void setSpokesman(String id,boolean b){
    }

    /**
     *某人申请发言
     * @param id 设置申请发言人的ID
     * @param b 当b= true 发言,b=false 取消发言;
     */
    public void setProposerSpeak(String id,boolean b){
    }

    /**
     *主持人请出人员
     * @param id 设置请出人员ID
     */
    public void setAssignPeopleOut(String id){

    }
    /**
     *切演示模式
     */
    public void setDemonstrationMode(){

    }
    /**
     *切视频模式
     */
    public void setVideoMode(){

    }
    /**
     *设置某人禁言
     * @param id 设置禁言人的ID
     * @param b 当b= true 禁言,b=false 取消禁言;
     */
    public void setForbidSpeak(String id,boolean b){

    }
    /**
     *绑定参数并连接coco服务器
     */
    public void bindConfig(MeetingConfig config){
        mMeetingConfig = config;
        mExtraStr = "send_nick='" + URLEncoder.encode(config.getuName()) + "' cipher='" + config.getCipher() + "' serverType='" + config.getServerType() + "' enterpriseId='" + config.getEnterpriseId() + "' ";
    }
    /**
     *演示文档 （白板的COCO消息比较特殊）,发送文档演示前，先发一条切换演示模式的消息
     */
    public void setDemonstrationDoc(String to,String current,String from_null,String url,String id,String filename ){
        sendMsgs(CommandUtil.setDemonstrationDoc(mMeetingConfig, mExtraStr,to,current,from_null,url,id,filename));
    }
    /**
     *文档翻页
     */
    public void setChangeDoc(String to,String current,String owner){
        sendMsgs(CommandUtil.setChangeDoc(mMeetingConfig,mExtraStr,to,current,owner));
    }
    /**
     *文档缩放
     */
    public void setDocZoom(String to,String index,String from_null,String owner){
        sendMsgs(CommandUtil.setDocZoom(mMeetingConfig,mExtraStr,to,index,from_null,owner));
    }



    /**
     * 发送消息
     */
    public boolean sendMsgs(String msg) {
        Cog.d(TAG, "-------------->send = " + msg);

        if (null == mSocket || null == mSocket.get()) {
            return false;
        }
        Socket soc = mSocket.get();
        try {
            if (!soc.isClosed() && !soc.isOutputShutdown()) {
                OutputStream os = soc.getOutputStream();
                String message = msg + "\r\n";
                os.write(message.getBytes());
                os.flush();
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void initSocket() {
        try {
            Socket so = new Socket(HOST, PORT);
            mSocket = new WeakReference<>(so);
            mReadThread = new ReadThread(so);
            mReadThread.start();
            mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//初始化成功后，就准备发送心跳包
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseResources(){
        mHandler.removeCallbacks(heartBeatRunnable);
        releaseLastSocket(mSocket);
    }


    /**
     * 释放socket
     */
    private void releaseLastSocket(WeakReference<Socket> mSocket) {
        try {
            if (null != mSocket) {
                Socket sk = mSocket.get();
                if (sk!=null) {
                    if (!sk.isClosed()) {
                        sk.close();
                    }
                }
                sk = null;
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 初始化socket
     */
    class InitSocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            initSocket();
        }
    }

    /**
     * 读取输入流
     */
    class ReadThread extends Thread {
        private WeakReference<Socket> mWeakSocket;
        private boolean isStart = true;

        public ReadThread(Socket socket) {
            mWeakSocket = new WeakReference<>(socket);
        }

        public void release() {
            isStart = false;
            releaseLastSocket(mWeakSocket);
        }

        @Override
        public void run() {
            super.run();
            Socket socket = mWeakSocket.get();
            if (null != socket) {
                try {
                    InputStream is = socket.getInputStream();
                    while (!socket.isClosed() && !socket.isInputShutdown()
                            && isStart) {
                        try {
                           // PullXmlUtils.parseXml(is);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
