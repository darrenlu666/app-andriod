package com.codyy.erpsportal.commons.exception;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by gujiajia on 2016/3/31.
 */
public class TextLogSaveRunner extends LogSaveRunner {
    private String mLogText;
    public TextLogSaveRunner(String logsDir, String text) {
        super(logsDir);
        this.mLogText = text;
    }

    @Override
    protected void writeLog(FileWriter writer) throws IOException {
        writer.append(mLogText)
            .append('\n');
        writer.flush();
    }
}
