
package com.codyy.bennu.dependence.publish.rtmp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.media.MediaCodec;
import android.util.Log;

import com.codyy.bennu.dependence.publish.hw.NV21Convertor;
import com.codyy.bennu.dependence.publish.rtp.AbstractPacketizer;

public class RTMPH264Packetizer extends AbstractPacketizer implements Runnable 
{	
	private final static String TAG = "RTMPH264Packetizer";

	private Thread t = null;
	private RTMPClient observer = null;
	private Semaphore sem = null;	// 信号量用于线程间同步
	private final Object mutex = new Object();
	
	private MediaCodec mediaCodec = null;
	private ByteBuffer[] outputBuffers = null;
	private ByteBuffer[] inputBuffers = null;
	private NV21Convertor convertor = null;
	
	private final static int NAULLENGTH = 720 * 1280 * 3 / 2;
	private byte[] buf = new byte[NAULLENGTH];
	
	private class RawVideoData
	{
		public byte[] buf;
		public int    len;
		public long   timestamp;
		public long   delta;
	}
	private List<RawVideoData> rawVideoList = null;
	private SimpleMemoryPool   rawVideoMemoryPool = null;
	
	public void setVideoBufferSize(int videoBufferSize)
	{
		if (rawVideoMemoryPool == null)
		{
			rawVideoMemoryPool = new SimpleMemoryPool(50, 5, videoBufferSize);
		}
	}
	
	public byte[] getBuffer() 
	{
		return rawVideoMemoryPool.pollBuffer();
	}
	
	public void putData(byte[] data, int size, long timepoint, long delta) 
	{
//		synchronized (mutex) 
//		{
			byte[] buffer = rawVideoMemoryPool.pollBuffer();
			if (buffer == null) return;
			System.arraycopy(data, 0, buffer, 0, size);
			RawVideoData rawVideoData = new RawVideoData();
			rawVideoData.buf = buffer;
			rawVideoData.len = size;
			rawVideoData.timestamp = timepoint;
			rawVideoData.delta = delta;
			rawVideoList.add(rawVideoData);
			sem.release();	
//			mutex.notify();// 唤醒线程
//		}
	}
	
	public RTMPH264Packetizer() throws IOException 
	{
		super();
		rawVideoList = Collections.synchronizedList(new LinkedList<RawVideoData>());
		sem = new Semaphore(0);
	}
	
	public void setObserver(RTMPClient observer)
	{
		this.observer = observer;
	}
	
	public void setEncoder(MediaCodec mediaCodec, NV21Convertor convertor)
	{
		this.convertor  = convertor;
		this.mediaCodec = mediaCodec;
		outputBuffers = this.mediaCodec.getOutputBuffers();
		inputBuffers = this.mediaCodec.getInputBuffers();
	}
	
	public void setVideoParams(int width, int height, int framerate) 
	{
		observer.SetVideoParams(width, height, framerate);
	}
	
	public void setVideoConf(byte[] sps, byte[] pps) 
	{
		//observer.setVideoConf(sps, pps);
	}	
	
	public void setVideoInit()
	{
		observer.setVideoInit();
	}
	
	public void start() 
	{
		if (t == null) 
		{
			t = new Thread(this);
			t.start();
		}	
	}
	
	public void stop() 
	{
		if (t != null) 
		{
			t.interrupt();
			try 
			{
				t.join();
			} 
			catch (InterruptedException e) {}
			t = null;
		}
	}
	
	public void run() 
	{
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		while (!Thread.interrupted()) 
		{
			try 
			{
				sem.acquire(1);
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				//e.printStackTrace();
				break;
			}
			
//			synchronized (mutex) 
//			{
//				// http://blog.csdn.net/ghsau/article/details/7433673
//				while (rawVideoList.size() == 0) 
//				{  
//					try 
//					{  
//						mutex.wait();  
//					} 
//					catch (InterruptedException e) 
//					{  
//						//e.printStackTrace();
//						break;  
//					}  	
//				} 
//			}
			
			encodeFrame();
			readAndSendFrame(0);
		}
	}
	
	public int encodeFrame()
	{
		int bufferIndex = mediaCodec.dequeueInputBuffer(0);
		if (bufferIndex >= 0 && rawVideoList.size() > 0) 
		{
			ByteBuffer inputBuffer = inputBuffers[bufferIndex];
			inputBuffer.clear();
			RawVideoData rawVideoData = rawVideoList.remove(0);
			long convertStart = System.nanoTime();
			if (convertor != null)
			{
				byte[] convertData = convertor.convert(rawVideoData.buf);//可能很耗时
				inputBuffer.put(convertData);
			}
			else
			{
				inputBuffer.put(rawVideoData.buf);
			}
			long convertSpend = (System.nanoTime() - convertStart)/1000000;
			long convertDelay = (System.nanoTime() - rawVideoData.timestamp)/1000000;
			//Log.d(TAG, String.format("delta=%d convertSpend=%d convertDelay=%d", rawVideoData.delta, convertSpend, convertDelay));
			mediaCodec.queueInputBuffer(bufferIndex, 0, rawVideoData.len, rawVideoData.timestamp/1000, 0);
			rawVideoMemoryPool.offerBuffer(rawVideoData.buf);
			return 0;
		}		
		else 
		{
			Log.e("ll","No buffer available !");
			return -1;
		}
	}
	
	public void readAndSendFrame(int timeoutUs)
	{
		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();  
		int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, timeoutUs);  // 不等待
		while (outputBufferIndex >= 0) 
		{  
			if (bufferInfo.size != 0)
			{
				//calcInterval();
				ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];  
				outputBuffer.position(bufferInfo.offset);
            	outputBuffer.limit(bufferInfo.offset + bufferInfo.size);			
            	int naluLength = bufferInfo.size;
				outputBuffer.get(buf, 0, naluLength);
				long delay = System.nanoTime()/1000000 - bufferInfo.presentationTimeUs/1000;
				//Log.d(TAG, String.format("RTMPH264Packetizer() total delay=%d", delay));
				
				sendEncodedData(buf, naluLength, bufferInfo.presentationTimeUs/1000);
				mediaCodec.releaseOutputBuffer(outputBufferIndex, false);  
			    outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);  
			}
		} 	
	}
	
	public void sendEncodedData(byte[] frame, int len, long timepoint)
	{
//		int type = RTMPClient.parseNaulType(frame, len);
//		if (type != RTMPClient.NAL_PPS && type != RTMPClient.NAL_SPS)
//		{		
//		}
		//observer.putData(frame, len, 0, RTMPClient.MEDIATYPE_VIDEO, timepoint, 0, 0);
	}
	
	private long lasttime = 0;
	void calcInterval()
	{
		long currenttime = System.nanoTime()/1000;
		if (lasttime == 0)
		{
			lasttime = currenttime;
		}
		long delta = (currenttime - lasttime)/1000;//两帧之间间隔
		lasttime = currenttime;
		Log.d(TAG, String.format("RTMPH264Packetizer() delta=%d", delta));
	}
}
