package com.codyy.erpsportal.commons.services;


import com.codyy.erpsportal.commons.utils.ThreadPool;

import java.io.File;


public interface IEngineService {

    public static final byte STATE_INVALID = -1;
    public static final byte STATE_STARTED = 10;
    public static final byte STATE_STARTING = 11;
    public static final byte STATE_STOPPED = 12;
    public static final byte STATE_STOPPING = 13;
    public static final byte STATE_DISCONNECTED = 14;

    public byte getState();

    public boolean isStarted();

    public boolean isStarting();

    public boolean isStopped();

    public boolean isStopping();

    public boolean isDisconnected();
    
    public void startServices();

    public void stopServices(boolean disconnected);

    public ThreadPool getThreadPool();

    public void notifyDownloadFinished(String displayName, File file);
    
    public boolean moveTempFile(File savePath);
    
    public boolean renameFile(File tempPath, String filename);
       
//    public void StopAllDownload();

}
