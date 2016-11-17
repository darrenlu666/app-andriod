package com.codyy.bennu.dependence.publish.rtmp;

public class GstreamerParcel 
{
	private static final String TAG = "GstreamerParcel";
	
	static 
	{
	    //System.loadLibrary("xxx");
	}
	
	public  native void Init();
	public  native void Free();
	public  native void Start();
	public  native void Stop();
	public  native void Pause();
	public  native boolean CreatePipeline();
	public  native boolean SetAudioFlag();
	public  native boolean SetVideoFlag();
	public  native boolean SetUri(String url);
	public  native boolean PushAudio(byte[] data, int length, long captureTimeMs);
	public  native boolean PushVideo(byte[] data, int length, long captureTimeMs);
	
	public GstreamerParcel() 
	{
	}
}
