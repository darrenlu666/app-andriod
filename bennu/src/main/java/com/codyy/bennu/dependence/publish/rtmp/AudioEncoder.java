package com.codyy.bennu.dependence.publish.rtmp;

import android.util.Log;

/**
 * 音频编码抽象接口
 */
public abstract class AudioEncoder
{
	final String TAG = "AudioEncoder";
	
	protected abstract int configure(AudioEngine.AudioQuality audioQuality);
	protected abstract int encode(byte[] data, int length, long captureTimeMs);
	protected abstract int close();
	
	protected AudioEngine.AudioQuality mAudioQuality = null;
	protected AudioCodec mAudioCodec = null;
	
	private MediaCallback mEncodedDataCallback = null;
	
	public AudioEncoder(AudioCodec audioCodec)
	{
		mAudioCodec = audioCodec;
	}
	
	public synchronized void registerEncodedDataCallback(MediaCallback dataCallBack) 
	{
		if (mEncodedDataCallback == null) {
			mEncodedDataCallback = dataCallBack;
		}
	}
	
	protected void sendEncodedData(byte[] data, int len, long captureTimeMs)
	{
		if (mEncodedDataCallback != null) {
			mEncodedDataCallback.onIncomingEncodedFrame(data, len, captureTimeMs);
		}
	}
}
