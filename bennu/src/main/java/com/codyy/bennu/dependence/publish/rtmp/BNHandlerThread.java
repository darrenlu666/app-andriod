package com.codyy.bennu.dependence.publish.rtmp;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by ghost on 2015/11/12.
 */
public class BNHandlerThread extends HandlerThread{
    private Handler mWorkerHandler;

    public BNHandlerThread(String name) {
        super(name);
    }

    public void postTask(Runnable task){
        mWorkerHandler.post(task);
    }

    public void prepareHandler(){
        mWorkerHandler = new Handler(getLooper());
    }
}
