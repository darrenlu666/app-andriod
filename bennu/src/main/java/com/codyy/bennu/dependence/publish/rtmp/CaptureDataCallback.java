package com.codyy.bennu.dependence.publish.rtmp;

public interface CaptureDataCallback 
{
	void onIncomingCapturedFrame(byte[] data, int length, long captureTime);
}
