package com.codyy.erpsportal.commons.exception;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by gujiajia on 2016/3/31.
 */
public class ThrowableSaveRunner extends LogSaveRunner {

    private Throwable mThrowable;

    public ThrowableSaveRunner(String logsDir, Throwable ex) {
        super(logsDir);
        this.mThrowable = ex;
    }

    protected void writeLog(FileWriter writer) throws IOException {
        if (mThrowable != null) {
            writer.append("Case By :\n")
                    .append(obtainNowString())
                    .append(mThrowable.toString())
                    .append('\n');
            StackTraceElement[] stacks = mThrowable.getStackTrace();
            if (stacks.length > 0) {
                for (StackTraceElement stack : stacks) {
                    writer.append(obtainNowString()).append(stack.toString()).append('\n');
                }
            }
            writer.flush();
        }
    }
}
