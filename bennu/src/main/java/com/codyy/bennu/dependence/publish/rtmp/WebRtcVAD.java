package com.codyy.bennu.dependence.publish.rtmp;

/**
 * Created by ghost on 2016/8/31.
 */
public class WebRtcVAD {

    private native boolean nativeInit(int sampleRate, int frameLen);
    private native void nativeRelease();
    private native boolean nativeSetMode(int mode);
    private native int nativeCheck(short[] checkData, int dataLen);

    static
    {
        System.loadLibrary("webrtcvad");
    }

    public boolean Init(int sampleRate, int frameLen){
        return nativeInit(sampleRate, frameLen);
    }

    public void Release(){
        nativeRelease();
    }

    public boolean SetMode(int mode){
        return  nativeSetMode(mode);
    }

    public int Process(short[] checkData, int dataLen){
        return nativeCheck(checkData, dataLen);
    }
}
