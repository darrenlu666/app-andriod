package com.codyy.bennu.dependence.publish.rtmp;

public interface MediaCallback 
{
	void onIncomingCapturedFrame(byte[] data, int length, long captureTimeMs);
	void onIncomingEncodedFrame(byte[] data, int length, long captureTimeMs);
}
