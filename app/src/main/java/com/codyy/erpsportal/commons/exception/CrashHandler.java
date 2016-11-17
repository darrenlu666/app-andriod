package com.codyy.erpsportal.commons.exception;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 处理Crash
 * Created by poe on 16-2-4.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler, Callback {

    private final static String TAG = "CrashHandler";

    private final static int MSG_DELAY_STOP = 345;

    private Context mContext = null;

    private static CrashHandler sInstance;

    private ExecutorService mExecutor;

    private String mLogsDir;

    private Handler mHandler;

    private UncaughtExceptionHandler mOldHandler;

    private CrashHandler() {
        mHandler = new Handler(this);
    }

    public void init(Context context) {
        this.mContext = context.getApplicationContext();
        if (mContext.getExternalCacheDir() != null) {
            this.mLogsDir = mContext.getExternalCacheDir() + "/crash_logs/";
        }
        mOldHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static synchronized CrashHandler getInstance() {
        if (sInstance == null) {
            sInstance = new CrashHandler();
        }
        return sInstance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        saveCrashLog(ex);
        // 收集异常信息 并且发送到服务器
        sendCrashReport(ex);
        // 处理异常
//        handleException();
        mOldHandler.uncaughtException(thread, ex);
    }

    private void saveCrashLog(Throwable ex) {
        if (mLogsDir == null) return;
        saveCrashLog(new ThrowableSaveRunner(mLogsDir, ex));
    }

    private void saveCrashLog(String log) {
        if (mLogsDir == null) return;
        saveCrashLog(new TextLogSaveRunner(mLogsDir, log));
    }

    private void saveCrashLog(LogSaveRunner runner) {
        if (mExecutor == null || mExecutor.isTerminated() || mExecutor.isShutdown()) {
            mExecutor = Executors.newSingleThreadExecutor();
        }
        mExecutor.execute(runner);
        mHandler.removeMessages(MSG_DELAY_STOP);
        mHandler.sendEmptyMessageDelayed(MSG_DELAY_STOP, 10 * 60 * 1000);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if(msg.what == MSG_DELAY_STOP) {
            mExecutor.shutdown();
            return true;
        }
        return false;
    }

    /**
     * 收集 crash的信息发送到服务器
     *
     * @param ex
     */
    private void sendCrashReport(Throwable ex) {
        //TODO 发送收集到的Crash信息到服务器
    }

    private void handleException() {
        final ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(mContext.getPackageName());
        killProcess(mContext.getPackageName());
    }

    public void doLog(String log) {
        if (TextUtils.isEmpty(log)) return;
        saveCrashLog(log);
    }

    public static void log(String log) {
        getInstance().doLog(log);
    }

    public static void log(Throwable throwable) {
        getInstance().doLog(throwable);
    }

    private void doLog(Throwable throwable) {
        if (throwable == null) return;
        saveCrashLog(throwable);
    }

    /**
     * kill 应用
     * @param packageName
     */
    public static void killProcess(String packageName) {
        String processId = "";
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec("ps");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String inline;
            while ((inline = br.readLine()) != null) {
                if (inline.contains(packageName)) {
                    break;
                }
            }
            br.close();
            StringTokenizer processInfoTokenizer = new StringTokenizer(inline);
            int count = 0;
            while (processInfoTokenizer.hasMoreTokens()) {
                count++;
                processId = processInfoTokenizer.nextToken();
                if (count == 2) {
                    break;
                }
            }
            r.exec("kill -15 " + processId);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
