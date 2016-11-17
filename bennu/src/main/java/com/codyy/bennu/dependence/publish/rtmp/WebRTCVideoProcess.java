package com.codyy.bennu.dependence.publish.rtmp;

import java.nio.ByteBuffer;

public class WebRTCVideoProcess {
	
	private native void processVideoFrame(byte[] buffer, int length, int width, int height, int rotate, Object outbuffer);
	
	static 
	{
		//System.loadLibrary("webrtc_yuv");
		System.loadLibrary("yuv_jni");
    }
	
	public WebRTCVideoProcess() 
	{
	}
	
	void ProcessVideoFrame(byte[] buffer, int length, int width, int height, int rotate, ByteBuffer outbuffer)
	{
		processVideoFrame(buffer, length, width, height, rotate, outbuffer);
	}

}
