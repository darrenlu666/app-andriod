/*
 * Copyright (C) 2011-2014 GUIGUI Simon, fyhertz@gmail.com
 * 
 * This file is part of libstreaming (https://github.com/fyhertz/libstreaming)
 * 
 * Spydroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

//package net.majorkernelpanic.streaming.rtp;
package com.codyy.bennu.dependence.publish.rtmp;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.annotation.SuppressLint;
import android.media.MediaCodec;

import com.codyy.bennu.dependence.publish.rtp.AbstractPacketizer;

/**
 * RFC 3640.  
 * 
 * Encapsulates AAC Access Units in RTP packets as specified in the RFC 3640.
 * This packetizer is used by the AACStream class in conjunction with the 
 * MediaCodec API introduced in Android 4.1 (API Level 16).       
 * 
 */
@SuppressLint("NewApi")
public class RTMPAACLATMPacketizer extends AbstractPacketizer implements Runnable {

	private final static String TAG = "RTMPAACLATMPacketizer";

	private Thread t;
	
	private RTMPClient observer = null;
	private MediaCodec mediaCodec;
	ByteBuffer[] outputBuffers;
	
	private int aacNo = 0;
	private byte[] aacBuf = new byte[1024];
	private boolean isfirst = true;
	
	public void setObserver(RTMPClient observer)
	{
		this.observer = observer;
	}

	public RTMPAACLATMPacketizer() 
	{
		super();
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
			} catch (IOException ignore) {}
			
			t.interrupt();
			
			try 
			{
				t.join();
			} 
			catch (InterruptedException e) {}
			t = null;
		}
	}
	
	public void setAudioParams(int samplerate) 
	{
		observer.SetAudioParams(samplerate);
	}
	
	public void setMediaCodec(MediaCodec mediaCodec)
	{
		this.mediaCodec = mediaCodec;
		outputBuffers = this.mediaCodec.getOutputBuffers();
	}

	@SuppressLint("NewApi")
	public void run() {

		while (!Thread.interrupted()) 
		{
			long start = System.nanoTime();
			readAndSendFrame();
//			long duration = (System.nanoTime() - start)/1000000;
//			Log.d("ll", String.format("onPreviewFrame() duration=%d ", duration));
//			if (duration < 23)
//			{
//				try {
//					Thread.sleep(23 - duration);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
		}
	}
	
	int id = 0;
	long lastpresentationTime = 0;
	public void readAndSendFrame()
	{
		id++;
		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();  
		int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);// 不等待
		if (outputBufferIndex >= 0) 
		{  
			if (bufferInfo.size != 0)
			{
				long now = System.currentTimeMillis();
				ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];  
				outputBuffer.position(bufferInfo.offset);
            	outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
            	
				outputBuffer.get(aacBuf, 0, bufferInfo.size);
				//observer.putData(aacBuf, bufferInfo.size, 0, RTMPClient.MEDIATYPE_AUDIO, bufferInfo.presentationTimeUs/1000, 0, aacNo);
				//使用System.currentTimeMillis()会出现卡顿
				//publisher.putData(aacBuf, bufferInfo.size, 0, RTMPClient.MEDIATYPE_AUDIO, System.nanoTime()/10000000, 0, aacNo);
				
				mediaCodec.releaseOutputBuffer(outputBufferIndex, false);    
			    //outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);  
			}
		}  
	}
	
	public void sendEncodedData(byte[] frame, int len, long timepoint)
	{
//		RTMPTagData tag = new RTMPTagData(frame, len, 0, RTMPClient.MEDIATYPE_AUDIO, timepoint, 0, 0);
//		observer.putData(tag);
	}
	
	
}
