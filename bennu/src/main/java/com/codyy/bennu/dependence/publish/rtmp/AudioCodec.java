
package com.codyy.bennu.dependence.publish.rtmp;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.os.Build;
import android.util.Log;

/**
 * 音频编码负责把收到的数据编码为AAC格式
 * {@link android.media.MediaCodec ,Vo-aacenc}
 */
public class AudioCodec
{
	private final static String TAG = "AudioCodec";
	private Thread t = null;
	private Semaphore sync = null;
	private AudioEncoder aacEncoder = null;
	//	private MediaCallback mEncodedDataCallback = null;
	// Code 2048 bytes at a time for FAAC
	private static int pcmBufferSize = 2048;
	private byte[] pcmBuffer  = null;
	private int    pcmDataSize = 0;

	private class RawAudioData
	{
		byte[] buf;
		int    len;
		long   ts;
	}
	private List<RawAudioData>  mFrameQueue = null;

	public AudioCodec()
	{
		String manufacturer = android.os.Build.MANUFACTURER;// 获取手机厂商
		Log.d(TAG, manufacturer);
		//String mtype = android.os.Build.MODEL; // 手机型号
//		int version = Build.VERSION.SDK_INT;
//		if(version >= Build.VERSION_CODES.LOLLIPOP){
//			aacEncoder = new MediaCodecAAC(this);
//		}else{
			if (manufacturer.equalsIgnoreCase("samsung") /*|| manufacturer.equalsIgnoreCase("Xiaomi")*/) {
				Log.i(TAG,"samsung or Xiaomi start MediaCodecAAC");
				aacEncoder = new MediaCodecAAC(this);
			} else {
				Log.i(TAG,"It is not the samsung or Xiaomi start VoAACEncoder");
				aacEncoder = new VoAACEncoder(this);
				//Log.d(TAG, "Use VoAAC");
			}
//		}


		//aacEncoder = new FAACEncoder(this);	
		mFrameQueue = Collections.synchronizedList(new LinkedList<RawAudioData>());
		sync = new Semaphore(0);
	}
	
	public void registerEncodedDataCallback(MediaCallback dataCallBack) 
	{
		aacEncoder.registerEncodedDataCallback(dataCallBack);
	}
	
	public void putCapturedFrame(byte[] data, int length, long captureTimeMs)
	{
		if (data != null && length > 0) {
			handleAudioData(data, length, captureTimeMs);
		}	
	}
	
	public int configure(AudioEngine.AudioQuality audioQuality)
	{
		if (pcmBuffer == null)
		{
			pcmBufferSize = pcmBufferSize * audioQuality.channel;
			pcmBuffer = new byte[pcmBufferSize];
		}

		if (aacEncoder.configure(audioQuality) == -1) {
			return  -1;
		}
		return 0;
	}
	
	public void start() 
	{
		if (t == null) {
			t = new Thread(new AudioEncodeThread());
			t.start();
		}
	}
	
	public void stop() 
	{
		if (t != null) {
    		t.interrupt();
			try {
				t.join();
			} 
			catch (InterruptedException e) {}
			t = null;
		}
		
		pcmDataSize = 0;
		mFrameQueue.clear();
		aacEncoder.close();
	}
	
	public void release()
	{
//        aacEncoder.close();
	}
	
	class AudioEncodeThread implements Runnable 
	{
		@Override
		public void run() 
		{
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			while (!Thread.interrupted()) {
				try {
					sync.acquire(1);
				} catch (InterruptedException e) {
					//Log.e(TAG, "acquire sync failed");
					break;
				}
				encodeAndSend();
			}
			Log.d(TAG, "Leave audio encode thread");
		}
	}

	// 如果采集的数据量不符合要求，先缓存，等达到一定数据量再送给编码器进行编码
	private int handleAudioData(byte[] data, int length, long captureTimeMs)
	{
/*
		RawAudioData audioData = new RawAudioData();
		audioData.buf = data;
		audioData.len = length;
		audioData.ts = captureTimeMs;
		mFrameQueue.add(audioData);
		sync.release();

		return 0;
*/

		int  rawDataSize = length;
		int  rawPos = 0;
		while (rawDataSize > 0)
		{
			int copySize = 0;
			int needCopySize = pcmBufferSize - pcmDataSize;
			if (rawDataSize < needCopySize) {
				copySize = rawDataSize;
			} else {
				copySize = needCopySize;
			}
			
			System.arraycopy(data, rawPos, pcmBuffer, pcmDataSize, copySize);
			rawDataSize -= copySize;
			rawPos += copySize;
			pcmDataSize += copySize;
			
			if (pcmDataSize == pcmBufferSize) {
				pcmDataSize = 0;

				byte[] buffer = new byte[pcmBufferSize];
				System.arraycopy(pcmBuffer, 0, buffer, 0, pcmBufferSize);
				
				RawAudioData audioData2 = new RawAudioData();
				audioData2.buf = buffer;
				audioData2.len = pcmBufferSize;
				audioData2.ts = captureTimeMs;
				mFrameQueue.add(audioData2);
				
				sync.release();	
			}
		}
		return 0;

	}
	
	private int encodeAndSend()
	{
		long count = mFrameQueue.size();
		if (count > 0) {
			RawAudioData audioData = mFrameQueue.remove(0);
			int ret = aacEncoder.encode(audioData.buf, audioData.len, audioData.ts);
			return ret;
		}
		return -1;
	}
}
