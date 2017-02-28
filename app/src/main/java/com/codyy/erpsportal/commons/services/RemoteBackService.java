package com.codyy.erpsportal.commons.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.codyy.erpsportal.IBackService;
import com.codyy.erpsportal.commons.models.entities.RemoteDirectorConfig;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Commands;
import com.codyy.erpsportal.commons.utils.RemotePullXmlUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.nio.CharBuffer;

/**
 * 远程导播服务
 * Created by yangxinwu on 2015/8/31.
 */
public class RemoteBackService extends Service {
    private static final String TAG = "RemoteBackService";
    private static final long HEART_BEAT_RATE = 30 * 1000;

    /**
     * 发送内容的消息
     */
    private static final int MSG_SEND = 1;

    /**
     * 发送心跳的消息
     */
    private static final int MSG_HEART_BEAT = 2;

    private String mTempMsg = "";
    private String mExtraStr;
    /**
     * 远程导播配置
     */
    private RemoteDirectorConfig mRemoteDirectorConfig;
    private ReadThread mReadThread;
    private WeakReference<Socket> mSocket;

    private Callback mHeartBeatCallback = new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Cog.d(TAG, "heartBeatCallback:" + msg);
            if (msg.what == MSG_HEART_BEAT) {
                if (msg.arg1 == 1) {//成功发送心跳包
                    mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
                } else {
                    mHandler.removeCallbacks(heartBeatRunnable);
                    mReadThread.release();
                    if (mSendHandler != null && mSendHandler.getLooper() != null) {
                        mSendHandler.getLooper().quit();
                    }
                    Cog.d(TAG, "--releaseLastSocket-->尝试重连");
                    releaseLastSocket(mSocket);
                    Cog.d(TAG, "-->尝试重连");
                    // new InitSocketThread().start();
                }
            }
            return true;
        }
    };

    // For heart Beat
    private Handler mHandler = new Handler(mHeartBeatCallback);

    private Runnable heartBeatRunnable = new Runnable() {

        @Override
        public void run() {
            Cog.d(TAG, "-->send heart beat");
            sendMessage("<root from='" + mRemoteDirectorConfig.getUid()
                    + "' to='" + mRemoteDirectorConfig.getMid()
                    + "' type='keepAlive' serverType='" + mRemoteDirectorConfig.getServerType()
                    + "' enterpriseId='" + mRemoteDirectorConfig.getEnterpriseId() + "'/>", true);
        }
    };
    private IBackService.Stub iBackService = new IBackService.Stub() {
        /**
         * 登录coco服务器
         * @throws RemoteException
         */
        @Override
        public void login() throws RemoteException {
            sendMessage(Commands.login(mRemoteDirectorConfig));
        }

        @Override
        public void noticeOnLine() throws RemoteException {
            sendMessage(Commands.noticeOnLine(mRemoteDirectorConfig));
        }

        /**
         * 退出coco服务器
         */
        @Override
        public void loginOut() throws RemoteException {
            sendMessage(Commands.loginOut(mRemoteDirectorConfig));
        }

        /**
         * 心跳包接口
         */
        @Override
        public void keepAlive() throws RemoteException {
            sendMessage("<root from='" + mRemoteDirectorConfig.getUid() + "' to='" + mRemoteDirectorConfig.getMid() + "' type='keepAlive' serverType='" + mRemoteDirectorConfig.getServerType() + "' enterpriseId='" + mRemoteDirectorConfig.getEnterpriseId() + "'/>");//就发送一个\r\n过去 如果发送失败，就重新初始化一个socket
        }

        /**
         * 退出系统
         */
        @Override
        public void exitSystem() throws RemoteException {
            releaseResources();
        }

        /**
         * @param command * 特效切换
         *                '淡入淡出',  "4"
         *                '左上', : "0"
         *                '左下', : "2"
         *                '右上', : "1"
         *                '右下', : "3"
         *                不启用特效：-1
         * @param seq 自增序列
         * @throws RemoteException
         */
        @Override
        public void sceneStyle(String command, int seq) throws RemoteException {
            sendMessage(Commands.sceneStyle(command, seq, mRemoteDirectorConfig, mExtraStr));
        }

        /**
         * @param command * 画面模式
         *
         *                '左上', : "0"
         *                '左下', : "2"
         *                '右上', : "1"
         *                '右下', : "3"
         *                不启用特效：4
         * @param seq 自增序列
         * @throws RemoteException
         */
        @Override
        public void videoStitchMode(String command, int seq) throws RemoteException {
            sendMessage(Commands.videoStitchMode(command, seq, mRemoteDirectorConfig, mExtraStr));
        }


        /**
         *
         * @param mode auto 自动,manual 手动
         * @param seq 自增序列
         * @throws RemoteException
         */
        @Override
        public void directorMode(String mode, int seq) throws RemoteException {
            sendMessage(Commands.directorMode(mode, seq, mRemoteDirectorConfig, mExtraStr));
        }

        /**
         *
         * @param mode 1:启动台标;-1 ：不启动台标
         * @param seq 自增序列
         * @throws RemoteException
         */
        @Override
        public void setLogo(String mode, int seq) throws RemoteException {
            sendMessage(Commands.setLogo(mode, seq, mRemoteDirectorConfig, mExtraStr));
        }

        /**
         * 勾选资源录制项
         * @param mode 录制模式
         * @param seq 序列
         * @throws RemoteException
         */
        @Override
        public void setVideoRecord(String mode, String flag, int seq) throws RemoteException {
            Cog.d(TAG, "setVideoRecord mode=", mode, ",flag=", flag, ",seq=", seq);
            sendMessage(Commands.setVideoRecord(mode, flag, seq, mRemoteDirectorConfig, mExtraStr));
        }

        @Override
        public void setSubTitle(String mode, int seq) throws RemoteException {
            sendMessage(Commands.setSubTitle(mode, seq, mRemoteDirectorConfig, mExtraStr));
        }

        /**
         * 切换录制模式
         * @param mode 切换模式
         * @param flag flag
         * @param recordArr 设置要录的画面
         * @param seq  序列
         * @throws RemoteException
         */
        @Override
        public void setChangeRecordMode(String mode, String flag, String recordArr, int seq) throws RemoteException {
            Cog.d(TAG, "setChangeRecordMode mode=", mode, ",flag=", flag, ",seq=", seq);
            sendMessage(Commands.setChangeRecordMode(mode, flag, recordArr, seq, mRemoteDirectorConfig, mExtraStr));
        }


        /**
         *
         * @param mode 1:启动字幕;-1 不启动字幕
         * @param seq 自增序列
         * @throws RemoteException
         */
        @Override
        public void setVideoHead(String mode, int seq) throws RemoteException {
            sendMessage(Commands.setVideoHead(mode, seq, mRemoteDirectorConfig, mExtraStr));
        }

        /**
         *
         * @param mode 1:启动字幕;-1 不启动字幕
         * @param seq 自增序列
         * @throws RemoteException
         */
        @Override
        public void setVideoEnd(String mode, int seq) throws RemoteException {
            sendMessage(Commands.setVideoEnd(mode, seq, mRemoteDirectorConfig, mExtraStr));
        }

        /**
         *
         * @param mode 0:开始/继续录制;1:暂停录制;2:停止录制
         * @param seq 自增序列
         * @throws RemoteException
         */
        @Override
        public void setRecordState(int mode, int seq) throws RemoteException {
            sendMessage(Commands.setRecordState(mode, seq, mRemoteDirectorConfig, mExtraStr));
        }

        /**
         * 预置位
         * @param flight         机位号
         * @param presetPosition 预置位，参数 0 1 2 3 4 5 6 7 8
         * @param seq            自增序列
         * @throws RemoteException
         */
        @Override
        public void setPresetPosition(int flight, int presetPosition, int seq) throws RemoteException {
            sendMessage(Commands.setPresetPosition(flight, presetPosition, seq, mRemoteDirectorConfig, mExtraStr));
        }

        /**
         *
         * @param flight 机位索引
         * @param function near:变焦+,far:变焦-,in:变倍+,out:变倍-,up:上,down:下,left:左,right:右
         * @param action up or down
         * @param seq    自增序列
         * @throws RemoteException
         */
        @Override
        public void setVideoMove(String flight, String function, String action, int seq) throws RemoteException {
            sendMessage(Commands.setVideoMove(flight, function, action, seq, mRemoteDirectorConfig, mExtraStr));
        }

        /**
         * 切换主画面
         *
         * @param position 当前主画面位置
         * @param flag    当title=='vga' true,title!='vga' false;
         * @param seq     自增序列
         */
        @Override
        public void changeVideoMain(String position, boolean flag, int seq) throws RemoteException {
            sendMessage(Commands.changeVideoMain(position, flag, seq, mRemoteDirectorConfig, mExtraStr));
        }

        /**
         * 基础数据绑定
         * @param config 基础配置
         * @throws RemoteException
         */
        @Override
        public void bindConfig(RemoteDirectorConfig config) throws RemoteException {
            mRemoteDirectorConfig = config;
            mExtraStr = "send_nick='" + config.getuName() + "' cipher='" + config.getCipher() + "' serverType='" + config.getServerType() + "' enterpriseId='" + config.getEnterpriseId() + "' ";
            new InitSocketThread().start();
        }

        /**
         * @param mode "startClass"上课 "finishClass"下课 "silentOn"静音，app需要自行禁止播放器声音 "silentOff" 取消静音，app需要自行恢复播放器声音
         * @param seq 自增序列
         */
        @Override
        public void setClassAndVoice(String mode, int seq) throws RemoteException {
            sendMessage(Commands.setClassAndVoice(mode, seq, mRemoteDirectorConfig, mExtraStr));
        }

        @Override
        public void setSubViewCenter(int seq, int index, int x, int y, int width, int hight) {
            sendMessage(Commands.setSubViewCenter(seq, index, x, y, width, hight, mRemoteDirectorConfig, mExtraStr));
        }

    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBackService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 发送消息
     * @param msg 消息内容
     * @param keepAlive 是否是心跳包
     * @return 是否成功
     */
    public boolean sendMessage(String msg, boolean keepAlive) {
        Cog.d(TAG, "-->send = " + msg);
        if (null == mSocket || null == mSocket.get()) {
            return false;
        }
        if (mSendHandler != null) {
            mSendHandler.sendMessage(mSendHandler.obtainMessage(keepAlive?MSG_HEART_BEAT: MSG_SEND, msg));
        }
        return true;
    }

    /**
     * 发送消息
     */
    public boolean sendMessage(String msg) {
        return sendMessage(msg, false);
    }

    private void initSocket() {
        try {
            Socket so = new Socket(mRemoteDirectorConfig.getIp(), mRemoteDirectorConfig.getPort());
            mSocket = new WeakReference<>(so);
            mReadThread = new ReadThread(so);
            mReadThread.start();
            HandlerThread sendThread = new HandlerThread("sendMsg2Coco");
            sendThread.start();
            mSendHandler = new Handler(sendThread.getLooper(), mSendCallback);
            mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//初始化成功后，就准备发送心跳包
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handler mSendHandler;

    private Callback mSendCallback = new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_SEND || msg.what == MSG_HEART_BEAT) {
                Cog.d(TAG, "-->send = " + msg);
                String message = (String) msg.obj;
                boolean sendResult = true;
                Socket soc = mSocket.get();
                if (null == mSocket || null == soc) {
                    sendResult = false;
                } else {
                    try {
                        if (!soc.isClosed() && !soc.isOutputShutdown()) {
                            OutputStream os = soc.getOutputStream();
                            String finalMsg = message + "\r\n";
                            os.write(finalMsg.getBytes());
                            os.flush();
                        } else {
                            sendResult = false;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        sendResult = false;
                    }
                }
                if(msg.what == MSG_HEART_BEAT) {//如果是心跳，要返回是否发送成功的结果
                    if (sendResult) {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_HEART_BEAT, 1, 0));
                    } else {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_HEART_BEAT, 0, 0));
                    }
                }
            }
            return true;
        }
    };

    private void releaseResources() {
        mHandler.removeCallbacks(heartBeatRunnable);
        releaseLastSocket(mSocket);
    }


    /**
     * 释放socket
     */
    private void releaseLastSocket(WeakReference<Socket> socketWeakRef) {
        try {
            if (null != socketWeakRef) {
                Socket sk = socketWeakRef.get();
                if (sk != null) {
                    if (!sk.isClosed()) {
                        sk.close();
                    }
                }
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

        ReadThread(Socket socket) {
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
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            socket.getInputStream()));
                    while (!socket.isClosed() && !socket.isInputShutdown()
                            && isStart) {
                        try {
                            RemotePullXmlUtils.parseXml(transInputStream(in));
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


    /**
     * 流转换，拆包
     *
     * @return ByteArrayInputStream
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
        ByteArrayInputStream stream;
        if (buffer.length() > 0) {
            String newString = buffer.replace("\r", "");
            mTempMsg = mTempMsg + newString;
            if (isFullCommand(mTempMsg)) {
                stream = new ByteArrayInputStream(mTempMsg.getBytes("UTF-8"));
                Cog.d(TAG, "transInputStream:receive CocoMsg=" + mTempMsg);
                mTempMsg = "";
                return stream;
            }
        }
        return null;
    }

    /**
     * 判断是否是一条完整的命令，如果不是返回false
     *
     * @param str command
     * @return true or false
     */
    private boolean isFullCommand(String str) {
        int a = occurTimes(str, "<");
        int b = occurTimes(str, ">");
        return !(a == 0 || b == 0) && a == b;
    }

    /**
     * 字符在字符串中出现的次数
     *
     * @param string 字符串
     * @param a      字符
     * @return 字符在字符串中出现的次数
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

}
