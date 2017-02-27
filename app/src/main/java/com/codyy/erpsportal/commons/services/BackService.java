package com.codyy.erpsportal.commons.services;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import com.codyy.erpsportal.IMeetingService;
import com.codyy.erpsportal.commons.models.entities.MeetingConfig;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.CommandUtil;
import com.codyy.erpsportal.commons.utils.PullXmlUtils;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.CharBuffer;

public class BackService extends Service {
    private static final String TAG = "BackService";
    private static final long HEART_BEAT_RATE = 30 * 1000;
    private static final int MSG_SEND = 0x111;//发送消息.
    public static  String HOST = "coco.ppmeeting.com";
    public static  int PORT = 1888;
    private String mExtraStr;
    private String mTempMsg = "";
    private MeetingConfig mMeetingConfig;
    private ReadThread mReadThread;
    private BufferedReader in;
    private WeakReference<Socket> mSocket;
    // For heart Beat
    private Handler mHandler = new Handler();
    /**消息发送线程**/
    private HandlerThread mMessageThread ;
    private Handler mMessageHandler ;

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
            sendMsgs(CommandUtil.login(mMeetingConfig));
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
            sendMsgs(CommandUtil.keepLive(mMeetingConfig));
            Cog.d("PullXmlUtilskeepAlive", "--->keepAlive");
        }

        /**
         * 上线消息
         */
        @Override
        public void noticeOnLine() throws RemoteException {
            sendMsgs(CommandUtil.noticeOnLine(mMeetingConfig));
        }

        /**
         * 获取在线用户
         */
        @Override
        public void getGroupOnlineUser() throws RemoteException {
            sendMsgs(CommandUtil.getGroupOnlineUser(mMeetingConfig, mExtraStr));
        }

        /**
         * 发送群聊 COCO消息
         */
        @Override
        public void sendMsg(String msg) throws RemoteException {
            sendMsgs(CommandUtil.sendMsg(mMeetingConfig, msg, mExtraStr));
        }

        /**
         * 发送私聊 COCO消息
         */
        @Override
        public void sendSignalMsg(String msg, String id) throws RemoteException {
            sendMsgs(CommandUtil.sendSignalMsg(mMeetingConfig, msg, mExtraStr, id));
        }

        /**
         *设置免打扰
         * @param id 设置免打扰用户的ID
         * @param b 当b= true 免打扰,b=false 取消免打扰;
         */
        @Override
        public void setDisturb(String id, boolean b) {
            sendMsgs(CommandUtil.setDisturb(mMeetingConfig, id, b, mExtraStr));
        }

        /**
         *主持人设置某人发言
         * @param id 设置发言人的ID
         * @param b 当b= true 发言,b=false 取消发言;
         */
        @Override
        public void setSpokesman(String id, boolean b) {
            sendMsgs(CommandUtil.setSpokesman(mMeetingConfig, id, b, mExtraStr));
        }

        /**
         *主持人取消某人发言
         * @param id 被取消人的ID
         */
        @Override
        public void setCancelSpeak(String id) {
            sendMsgs(CommandUtil.setCancelSpeak(mMeetingConfig, id,mMeetingConfig.getGid(), mExtraStr));
        }

        /**
         *某人申请发言
         * @param id 设置申请发言人的ID
         * @param b 当b= true 发言,b=false 取消发言;
         */
        @Override
        public void setProposerSpeak(String id,String to, boolean b) {
            if(b){
                sendMsgs(CommandUtil.setProposerSpeak(mMeetingConfig, id,to, mExtraStr));
            }else{
                sendMsgs(CommandUtil.setCancelSpeak(mMeetingConfig, id,to, mExtraStr));
            }
        }

        /**
         *主持人请出人员
         * @param id 设置请出人员ID
         */
        @Override
        public void setAssignPeopleOut(String id) {
            sendMsgs(CommandUtil.setAssignPeopleOut(mMeetingConfig, id, mExtraStr));
        }

        /**
         *切演示模式
         */
        @Override
        public void setDemonstrationMode() {
            sendMsgs(CommandUtil.setDemonstrationMode(mMeetingConfig, mExtraStr));
        }

        /**
         *切视频模式
         */
        @Override
        public void setVideoMode() {
            sendMsgs(CommandUtil.setVideoMode(mMeetingConfig, mExtraStr));
        }

        /**
         *设置某人禁言
         * @param id 设置禁言人的ID
         * @param b 当b= true 禁言,b=false 取消禁言;
         */
        @Override
        public void setForbidSpeak(String id, boolean b) {
            sendMsgs(CommandUtil.setForbidSpeak(mMeetingConfig, id, b, mExtraStr));
        }

        /**
         *绑定参数并连接coco服务器
         */
        @Override
        public void bindConfig(MeetingConfig config) {
            mMeetingConfig = config;
            mExtraStr = "send_nick='" + URLEncoder.encode(config.getuName()) + "' cipher='" + config.getCipher() + "' serverType='" + config.getServerType() + "' enterpriseId='" + config.getEnterpriseId() + "' ";
            new InitSocketThread().start();
        }

        /**
         *演示文档 （白板的COCO消息比较特殊）,发送文档演示前，先发一条切换演示模式的消息
         */
        @Override
        public void setDemonstrationDoc(String to, String current, String from_null, String url, String id, String filename) {
            sendMsgs(CommandUtil.setDemonstrationDoc(mMeetingConfig, mExtraStr, to, current, from_null, url, id, filename));
        }

        /**
         *文档翻页
         */
        @Override
        public void setChangeDoc(String to, String current, String owner) {
            sendMsgs(CommandUtil.setChangeDoc(mMeetingConfig, mExtraStr, to, current, owner));
        }

        /**
         *文档缩放
         */
        @Override
        public void setDocZoom(String to, String index, String from_null, String owner) {
            sendMsgs(CommandUtil.setDocZoom(mMeetingConfig, mExtraStr, to, index, from_null, owner));
        }

        /**
         * 删除文档
         * @param to
         * @param id 文档Id
         */
        @Override
        public void  setDelectDoc(String to, String id) {
            sendMsgs(CommandUtil.setDelectDoc(mMeetingConfig, mExtraStr, to, id));
        }



    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return meetingService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initWriteThread();
    }

    private void initWriteThread() {
        mMessageThread = new HandlerThread("online-meeting-message");
        mMessageThread.start();
        mMessageHandler = new Handler(mMessageThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MSG_SEND:
                        try{
                            String obj = (String) msg.obj;
                            sendMsgThread(obj);
                        }catch (ClassCastException e){
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };


    }

    /**
     * 发送消息(外部方法)
     */
    public boolean sendMsgs(String msg) {
        Cog.d(TAG, "-------------->send = " + msg);
        mMessageHandler.sendMessage(mMessageHandler.obtainMessage(MSG_SEND,msg));
        return true;
    }

    /**
     * 发送消息(线程安全)
     * modified by poe
     * 修复7.0中的主线程中进行网络请求错误
     */
    private boolean sendMsgThread(String msg) {
        Cog.d(TAG, "-------------->send = " + msg);
        if (null == mSocket || null == mSocket.get()) {
            return false;
        }
        Socket soc = mSocket.get();
        try {
            if (!soc.isClosed() && !soc.isOutputShutdown()) {
                OutputStream os = soc.getOutputStream();
//                String message = msg + "\r\n";
                String message = msg + "\0";
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
            Cog.e(TAG,"ip :" +mMeetingConfig.getIp());
            //连接COCO
            Socket so = new Socket(mMeetingConfig.getIp(), mMeetingConfig.getPort());
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

    private void releaseResources() {
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
                if (sk != null) {
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
                    in = new BufferedReader(new InputStreamReader(
                            socket.getInputStream()));
                    while (!socket.isClosed() && !socket.isInputShutdown()
                            && isStart) {
                        try {
                            PullXmlUtils.parseXml(transInputStream(in));
                        } catch (Exception e) {
//                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 流转换，拆包
     *
     * @return
     * @throws IOException
     */
    public ByteArrayInputStream transInputStream(BufferedReader bufferedReader) throws IOException {
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        int length = bufferedReader.read(charBuffer);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < charBuffer.position(); i++) {
            sb.append(charBuffer.get(i));
        }
        String buffer = sb.toString();
        ByteArrayInputStream stream = null;
        if (buffer.length() > 0) {
            String newString = buffer.replace("\r", "");
            mTempMsg = mTempMsg + newString;
            if (isFullCommand(mTempMsg)) {
                stream = new ByteArrayInputStream(mTempMsg.getBytes("UTF-8"));
                Cog.d(TAG, "transInputStream:receive CocoMsg=", mTempMsg);
                mTempMsg = "";
                return stream;
            }
        }
        return stream;
    }

    /**
     * 判断是否是一条完整的命令，如果不是返回false
     *
     * @param str
     * @return
     */
    private boolean isFullCommand(String str) {
        int a = occurTimes(str, "<");
        int b = occurTimes(str, ">");
        Cog.d(TAG, "----------a-------------" + a);
        Cog.d(TAG, "----------b-------------" + b);
        if (a == 0 || b == 0) {
            return false;
        } else return a == b;

    }

    /**
     * 字符在字符串中出现的次数
     *
     * @param string
     * @param a
     * @return
     */
    public static int occurTimes(String string, String a) {
        int pos = -2;
        int n = 0;

        while (pos != -1) {
            if (pos == -2) {
                pos = -1;
            }
            pos = string.indexOf(a, pos + 1);
            if (pos != -1) {
                n++;
            }
        }
        return n;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(Build.VERSION.SDK_INT>= 18){
            mMessageThread.quitSafely();
        }else{
            mMessageThread.quit();
        }
    }
}
