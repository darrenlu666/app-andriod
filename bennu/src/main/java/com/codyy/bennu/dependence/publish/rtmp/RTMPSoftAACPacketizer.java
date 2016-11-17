
package com.codyy.bennu.dependence.publish.rtmp;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.util.Log;

import com.codyy.bennu.dependence.publish.rtp.AbstractPacketizer;

public class RTMPSoftAACPacketizer extends AbstractPacketizer implements Runnable
{
	private final static String TAG = "RTMPSoftAACPacketizer";
	
	// Send AAC data via RTMP
	private RTMPClient observer = null;
	
	private Thread t = null;
	private Semaphore sync = null;//用于线程间同步
	
	// Use WebRTC(APM) to eliminate echo. Handle max 10 ms @ 48 kHz
	private WebRtcAudioProcess audioProcess = null;
	private boolean isAecm = false;
	private boolean isNs   = false;
	private boolean isAgc  = false;
	
	private FAACEncoder faacEncoder = null;
	private int samplesInput  = 0;
	private int pcmBufferSize = 0;
	private int aacBufferSize = 0;
	// Code 2048 bytes at a time for FAAC
	private byte[] pcmBuffer   = null;
	private byte[] encodedData = null;
	private int  pcmDataSize = 0;
	
	private boolean isFirstAAC = true;
	
	private class RawAudioData
	{
		public byte[] buf;
		public int    len;
	}
	
	private List<RawAudioData> farendAudioList = null;
	private SimpleMemoryPool   farendAudioMemoryPool = null;
	
	private List<RawAudioData> rawAudioList = null;
	private SimpleMemoryPool   rawAudioMemoryPool = null;
	
	private boolean isPlaying = false;
	public boolean isPlaying() 
	{
		return isPlaying;
	}
	
	// Receive 10 ms |frame| from player
	public void DeliverAudioPlayData(byte[] data, int length)
	{
		if (isAecm || isNs || isAgc) 
		{
			if (isPlaying == false)
			{
				isPlaying = true;
				farendAudioMemoryPool = new SimpleMemoryPool(50, 5, length);
			}
			byte[] buf = farendAudioMemoryPool.pollBuffer();
			if (buf == null) return;
			System.arraycopy(data, 0, buf, 0, length);
			RawAudioData farendAudioData = new RawAudioData();
			farendAudioData.buf = buf;
			farendAudioData.len = length;
			farendAudioList.add(farendAudioData);
		}
	}
	
	public RTMPSoftAACPacketizer() throws IOException 
	{
		super();
		faacEncoder = new FAACEncoder();
		audioProcess = new WebRtcAudioProcess();
		rawAudioList = Collections.synchronizedList(new LinkedList<RawAudioData>());
		farendAudioList = Collections.synchronizedList(new LinkedList<RawAudioData>());
		sync = new Semaphore(0);
	}
	
	public void openEncoder(int sampleRate, int channels)
	{
		faacEncoder.AACEncoderOpen(sampleRate, channels);
		aacBufferSize = faacEncoder.AACEncoderGetMaxOutputBytes(); // 768
		samplesInput  = faacEncoder.AACEncoderGetSamplesInput();   // 1024
		pcmBufferSize = samplesInput * (16 / 8) * channels;        // 2048
		pcmBuffer = new byte[pcmBufferSize];
		encodedData = new byte[aacBufferSize];
		
		audioProcess.InitProcess(sampleRate);
		
		setAudioParams(sampleRate);
	}
	
	public void setObserver(RTMPClient observer)
	{
		this.observer = observer;
	}
	
	public void setAudioParams(int samplerate) 
	{
		observer.SetAudioParams(samplerate);
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
			try 
			{
				is.close();
			} 
			catch (IOException e) {}
			t.interrupt();
			try 
			{
				t.join();
			} 
			catch (InterruptedException e) {}
			t = null;
			faacEncoder.AACEncoderClose();	
			audioProcess.StopProcess();
		}
	}
	
	public void SetAecm(boolean isAecm) 
  	{
		Log.d(TAG, "SetAecm=" + isAecm);
		this.isAecm = isAecm;
		audioProcess.SetAecm(isAecm);
	}

	public void SetNs(boolean isNs)
	{
		Log.d(TAG, "SetNs=" + isNs);
		this.isNs = isNs;
		audioProcess.SetNs(isNs);
	}

	public void SetAgc(boolean isAgc) 
	{
		Log.d(TAG, "SetAgc=" + isAgc);
		this.isAgc = isAgc;
		audioProcess.SetAgc(isAgc);
	}
	
	public void putData(byte[] data, int size) 
	{	
		if (rawAudioMemoryPool == null)
		{
			rawAudioMemoryPool = new SimpleMemoryPool(50, 5, size);
		}
		byte[] processData = rawAudioMemoryPool.pollBuffer();
		if (processData == null) return;
		
		if (isPlaying && (isAecm || isNs || isAgc)) 
		{
			// 接收到的先缓存起来，在接收线程中送入APM，每次只处理10ms数据
			long count = farendAudioList.size();
			if (count > 0)
			{
				RawAudioData farendAudioData = farendAudioList.remove(0);
				audioProcess.AnalyzeReverseStream(farendAudioData.buf, farendAudioData.len);
				farendAudioMemoryPool.offerBuffer(farendAudioData.buf);
			}
			audioProcess.ProcessStream(data, size, 0, processData);	
		} 
		else
		{
			System.arraycopy(data, 0, processData, 0, size);
		}
		RawAudioData rawAudioData = new RawAudioData();
		rawAudioData.buf = processData;
		rawAudioData.len = size;
		rawAudioList.add(rawAudioData);
		sync.release();	
	}

	public void run() 
	{
		while (!Thread.interrupted()) 
		{	
			try 
			{
				sync.acquire(1);
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				//e.printStackTrace();
				break;
			}
			encodeAndSend();
		}
	}
	
	private int encodeAndSend()
	{	
		long count = rawAudioList.size();
		if (count > 0)
		{
			RawAudioData rawAudioData = rawAudioList.remove(0);
			int  rawDataSize = rawAudioData.len;
			int  rawPos = 0;
			
			while (rawDataSize > 0)
			{
				int copySize = 0;
				int needCopySize = pcmBufferSize - pcmDataSize;
				if (rawDataSize < needCopySize)
				{
					copySize = rawDataSize;
				}
				else
				{
					copySize = needCopySize;
				}
				
				System.arraycopy(rawAudioData.buf, rawPos, pcmBuffer, pcmDataSize, copySize);
				rawDataSize -= copySize;
				rawPos += copySize;
				pcmDataSize += copySize;
				
				if (pcmDataSize == pcmBufferSize)
				{
					pcmDataSize = 0;
					int ret = faacEncoder.AACEncoderEncode(pcmBuffer, pcmBufferSize, encodedData, encodedData.length);
					if (ret > 0)
					{
						// TODO: 时间戳使用采集时时间
						sendEncodedData(encodedData, ret, System.nanoTime()/1000000);
					}
					else
					{
						return -1;
					}
				}
			}
			rawAudioMemoryPool.offerBuffer(rawAudioData.buf);
		}
		return 0;
	}
	
	public void sendEncodedData(byte[] frame, int len, long timepoint)
	{
		ATDSInfo info = null;
		if (!ATDSInfo.hasADTSHeader(frame, len))
		{
			return;
		}
		
		if (isFirstAAC)
		{
			isFirstAAC = false;
			info = ATDSInfo.parseADTS(frame, len, true);
		}
		else
		{
			info = ATDSInfo.parseADTS(frame, len, false);
		}
		
		if (info.isADTS) 
		{	
			if (info.audioCfg != null)
			{
				//observer.putData(info.audioCfg, info.audioCfg.length, 0, RTMPClient.MEDIATYPE_AUDIO, 0, 0, 0);
			}
			//observer.putData(frame, len, info.adtsHeaderLength, RTMPClient.MEDIATYPE_AUDIO, timepoint, 0, 0);
		}
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
		Log.d(TAG, String.format("record RTMPSoftAACPacketizer() delta=%d", delta));
	}
}
