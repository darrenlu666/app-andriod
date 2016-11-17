package com.codyy.bennu.dependence.publish.rtmp;

import java.nio.ByteBuffer;

public class WebRtcAudioProcess 
{
	private static final String TAG = "WebRtcAudioProcess";
	
	private ByteBuffer outBuffer = null;
	private int bufferOffset = 0;

	static 
	{
	    // Ordering of loading these shared libraries is significant.
	    //System.loadLibrary("webrtc_audiopreprocessing");
	    System.loadLibrary("webrtcapm");
	}
	
	public WebRtcAudioProcess() 
	{
		Create();
	}
	
	public int InitProcess(int sampleRate)
	{
		int ret = 0;
		ret = Init(sampleRate);
		return ret;
	}
	
	public int ProcessStream(byte[] data, int length, int totalDelayMS, byte[] out)
	{
		int ret = 0;
		if (outBuffer == null) {
			outBuffer = ByteBuffer.allocateDirect(length);
			bufferOffset = outBuffer.arrayOffset();
		}

		ret = Process(data, length, totalDelayMS, outBuffer);
		System.arraycopy(outBuffer.array(), bufferOffset, out, 0, length);
		return ret;
	}
	
	public int AnalyzeReverseStream(byte[] data, int length)
	{
		int ret = 0;
		ret = BufferFarend(data, length);
		return ret;
	}
	
	public int StopProcess()
	{
		int ret = 0;
		ret = Free();
		return ret;
	}
	
	private native int Create();
	private native int Free();
	private native int Init(int sampleRate);
	private native int Process(byte[] nearend, int nearendSize, int totalDelayMS, Object out);
	private native int BufferFarend(byte[] farend, int farendSize);
	
	public  native void SetAecm(boolean isAecm);
	public  native void SetNs(boolean isNs);
	public  native void SetAgc(boolean isAgc);
}
