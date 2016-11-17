/*
 * Copyright (C) 2011 GUIGUI Simon, fyhertz@gmail.com
 * 
 * This file is part of Spydroid (http://code.google.com/p/spydroid-ipcamera/)
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

import android.util.Log;

import com.codyy.bennu.dependence.publish.rtp.AbstractPacketizer;

/**
 * RFC 3640  
 *
 * This packetizer must be fed with an InputStream containing ADTS AAC. 
 * AAC will basically be rewrapped in an RTP stream and sent over the network.
 * This packetizer only implements the aac-hbr mode (High Bit-rate AAC) and
 * each packet only carry a single and complete AAC access unit.
 * 
 */
public class RTMPAACADTSPacketizer extends AbstractPacketizer implements Runnable 
{

	private final static String TAG = "RTMPAACADTSPacketizer";
	
	private Thread t;
	private boolean running = false;
	
	private RTMPClient observer = null;
	public void setObserver(RTMPClient observer)
	{
		this.observer = observer;
	}
		
	private long timebase = 0;
	private long lastTime = 0;
	
	private boolean isfirst = true;
	int mProfile, mSamplingRateIndex, mChannel, mConfig;
	/** There are 13 supported frequencies by ADTS. **/
	public static final int[] AUDIO_SAMPLING_RATES = {
		96000, // 0
		88200, // 1
		64000, // 2
		48000, // 3
		44100, // 4
		32000, // 5
		24000, // 6
		22050, // 7
		16000, // 8
		12000, // 9
		11025, // 10
		8000,  // 11
		7350,  // 12
		-1,   // 13
		-1,   // 14
		-1,   // 15
	};
	
	public RTMPAACADTSPacketizer() throws IOException 
	{
		super();
	}

	public void start() 
	{
		if (!running) 
		{
			running = true;
			t = new Thread(this);
			t.start();
			lastTime = System.currentTimeMillis();
			Log.d("time", String.format("RTMPAACADTSPacketizer startTime=%d", lastTime));
		}
	}

	public void stop() 
	{
		try 
		{
			running = false;
			is.close();
		} 
		catch (IOException ignore) 
		{
		}

		// We wait until the packetizer thread returns
		try 
		{
			t.join();
		} 
		catch (InterruptedException e) 
		{
		}
	}
	

	long audioStart = 0;
	public void run() 
	{
		long ts = 0;
		int frameLength;
		boolean protection;
		byte[] header = new byte[7];
		
		try 
		{
			while (running) 
			{
				
				long startWhen = System.nanoTime();
				// Synchronisation: ADTS packet starts with 12bits set to 1
				while (true) 
				{
					header[0] = (byte) is.read();				
					if ((header[0] & 0xFF) == 0xFF) 
					{
						header[1] = (byte) is.read();
						if ((header[1]&0xF0) == 0xF0) 
							break;
					}
				}
				
				// The protection bit indicates whether or not the header contains the two extra bytes
				protection = (header[1] & 0x01) > 0 ? true : false;
				// Parse adts header (ADTS packets start with a 7 or 9 byte long header)
				is.read(header, 2, 5);
				
//				// Read CRC if any
//				if (!protection) 
//					is.read(buffer, 7, 2);
				
				if (isfirst)
				{
					isfirst = false;
					audioStart = System.nanoTime();
					//从流里面获取相关信息
					mSamplingRateIndex = (header[2] & 0x3C) >> 2 ;
					mProfile = ((header[2] & 0xC0) >> 6 ) + 1 ;
					mChannel = (header[2] & 0x01) << 2 | (header[3] & 0xC0) >> 6 ;
					// 5 bits for the object type / 4 bits for the sampling rate / 4 bits for the channel / padding
					mConfig = 1 << 11 | mSamplingRateIndex << 7 | mChannel << 3;
					//Log.d("flv", String.format("mProfile=%d mSamplingRateIndex=%d mChannel=%d mConfig=%x", mProfile, mSamplingRateIndex, mChannel, mConfig));
					byte[] audioCfg = new byte[2];
					audioCfg[0] = (byte) (mConfig >> 8);
					audioCfg[1] = (byte) (mConfig & 0xFF);
					//client.setAudioConf(audioCfg, 2);
					//observer.putData(audioCfg, 2, 0, RTMPClient.MEDIATYPE_AUDIO, 0, ts, 0);
				}
		
				frameLength = (header[3] & 0x03) << 11 | 
							  (header[4] & 0xFF) << 3 | 
							  (header[5] & 0xE0) >> 5 ;
				frameLength = frameLength - 7;
				
				byte[] buf = new byte[frameLength];
				// Read frame
				is.read(buf, 0, frameLength);
				
				//
				ts += 1024 * 1000000 / AUDIO_SAMPLING_RATES[mSamplingRateIndex] / 1000;//单位ms
				//long currentTime = ts + audioStart/1000000;
				long currentTime = System.nanoTime()/1000000;
				//observer.putData(buf, frameLength, 0, MediaData.MEDIATYPE_AUDIO, currentTime, ts, 0);
				
				long delta = calcInterval();
				long duration = (System.nanoTime() - startWhen)/1000000;
				Log.d("ll", String.format("RTMPAACADTSPacketizer() duration=%d", duration));
				if (delta > 23)//默认执行周期为23ms
				{
					
				}
				else
				{
					long sleeptime = 23 - duration;
					if (sleeptime > 0)
					{
						try {
							Thread.sleep(sleeptime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		} 
		catch (IOException e) 
		{
		} 
		catch (ArrayIndexOutOfBoundsException e) 
		{
			Log.e(TAG,"ArrayIndexOutOfBoundsException: "+(e.getMessage()!=null?e.getMessage():"unknown error"));
			e.printStackTrace();
		} 
		finally 
		{
			running = false;
		}
	}
	
	long lasttime = 0;
	long calcInterval()
	{
		long currenttime = System.nanoTime()/1000;
		if (lasttime == 0)
		{
			lasttime = currenttime;
		}
		long delta = (currenttime - lasttime)/1000;//两帧之间间隔
		lasttime = currenttime;
		Log.d("ll", String.format("RTMPAACADTSPacketizer() delta=%d", delta));
		return delta;
	}
}
