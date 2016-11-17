package com.codyy.erpsportal.commons.exception;

import com.codyy.erpsportal.commons.utils.Cog;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by gujiajia on 2016/3/31.
 */
abstract class LogSaveRunner implements Runnable {

    private final static String TAG = "LogSaveRunner";

    private String mLogsDir;

    private static DateTimeFormatter sDayFormatter;

    private static DateTimeFormatter sTimeStampFormatter;

    static {
        sDayFormatter = DateTimeFormat.forPattern("yyyyMMdd");
        sTimeStampFormatter = DateTimeFormat.forPattern("[HH:mm:ss] ");
    }

    public LogSaveRunner() { }

    public LogSaveRunner(String logsDir) {
        this.mLogsDir = logsDir;
    }

    @Override
    public void run() {
        Cog.d(TAG, "mLogsDir");
        File dir = new File(mLogsDir);
        if (dir.mkdirs() || dir.isDirectory()) {
            FileWriter writer = null;
            try {
                new File(mLogsDir, ".nomedia").createNewFile();
                File file = new File(mLogsDir, obtainTodayString());
                writer = new FileWriter(file, true);
                writeLog(writer);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    abstract protected void writeLog(FileWriter writer) throws IOException;

    /**
     * 获取当前时间年月日
     *
     * @return
     */
    private static String obtainTodayString() {
        return sDayFormatter.print(System.currentTimeMillis());
    }

    protected static String obtainNowString() {
        return sTimeStampFormatter.print(System.currentTimeMillis());
    }
}
