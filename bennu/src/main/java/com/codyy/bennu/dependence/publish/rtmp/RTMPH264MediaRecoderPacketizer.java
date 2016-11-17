package com.codyy.bennu.dependence.publish.rtmp;

import java.io.IOException;

import android.util.Log;

import com.codyy.bennu.dependence.publish.rtp.AbstractPacketizer;

public class RTMPH264MediaRecoderPacketizer extends AbstractPacketizer implements Runnable
{
		
	private final static String TAG = "RTMPH264MediaRecoderPacketizer";

	private Thread t;

	//RandomAccessFile raf = null;

	private int naluLength = 0;
	private long timestamp = 0;
	private byte[] buf = new byte[H264VIDEOLENGTH];
	private final static int H264VIDEOLENGTH = 720 * 1280 * 3 / 2;
	
	private boolean isfirst = true;
	
	private long num = 0;
	private int  framerate = 20;
	
	private long lastTime = 0;
	private int h264FrameNo = 0;

	private RTMPClient observer = null;
	public void setObserver(RTMPClient observer)
	{
		this.observer = observer;
	}
	
	public RTMPH264MediaRecoderPacketizer() throws IOException 
	{
		super();
	}
	
	public void setVideoParams(int width, int height, int framerate) 
	{
		observer.SetVideoParams(width, height, framerate);
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
		}
	}
	
	byte[] spsAndPPS;
	public void setVideoConf(byte[] sps, byte[] pps) 
	{
		//this.SPS = sps;
		//this.PPS = pps;
		byte[] spsHeader = {0x00, 0x00, 0x00, 0x01, 0x27};
		byte[] ppsHeader = {0x00, 0x00, 0x00, 0x01, 0x28};
		int sps_len = sps.length;
		int pps_len = pps.length;
		int len = spsHeader.length + sps_len + ppsHeader.length + pps_len;
		spsAndPPS = new byte[len];
		int position = 0;
		System.arraycopy(spsHeader, 0, spsAndPPS, position, spsHeader.length);
		position += spsHeader.length;
		System.arraycopy(sps, 0, spsAndPPS, position, sps_len);
		position += sps_len;
		System.arraycopy(ppsHeader, 0, spsAndPPS, position, ppsHeader.length);
		position += ppsHeader.length;
		System.arraycopy(pps, 0, spsAndPPS, position, pps.length);
		//observer.putData(spsAndPPS, spsAndPPS.length, 0, RTMPClient.MEDIATYPE_VIDEO, 0, 0, 0);
		//client.setVideoConf(sps, pps);
	}	
	
	private byte[] SPS;
	private byte[] PPS;
	long timebase = 0;

	private Statistics stats = new Statistics();
	private int streamType = 1;
	private long delay = 0, oldtime = 0;
	
	public void run() 
	{
		long duration = 0, delta2 = 0;
		Log.d(TAG,"H264 packetizer started !");
		
		try {
			while (!Thread.interrupted()) 
			{

				oldtime = System.nanoTime();
				// We read a NAL units from the input stream and we send them
				send();
				// We measure how long it took to receive NAL units from the phone
				duration = System.nanoTime() - oldtime;
				
				// Every 3 secondes, we send two packets containing NALU type 7 (sps) and 8 (pps)
				// Those should allow the H264 stream to be decoded even if no SDP was sent to the decoder.				
				delta2 += duration/1000000;
				if (delta2>3000) 
				{
					delta2 = 0;
					//发送sps和pps
					//writeVideoConf(timestamp);
				}

				stats.push(duration);
				// Computes the average duration of a NAL unit
				delay = stats.average() /1000000;
				//Log.d(TAG,"duration: "+duration/1000000+" delay: "+delay/1000000);

			}
		} catch (IOException e) {
		} catch (InterruptedException e) {}

		Log.d(TAG,"H264 packetizer stopped !");

	}
	
	byte[] header = new byte[5];
	long currenttime;
	
	int i = 0;
	long now, oldnow, count = 0;
	long total = 0;
	float rate = 0;
	long videoStart = 0;
	
	//Reads a NAL unit in the FIFO and sends it.
	private void send() throws IOException, InterruptedException 
	{
		int len = 0, type;
		// NAL units are preceeded by their length, we parse the length
		fill(header,0,5);
		timestamp += delay;
		naluLength = header[3] & 0xFF | (header[2]&0xFF)<<8 | (header[1]&0xFF)<<16 | (header[0]&0xFF)<<24;
		if (naluLength>100000 || naluLength<0) 
		{
			resync();
		}
		
		if (videoStart == 0)
		{
			videoStart = System.nanoTime();
		}
		ts += 40;
		// Parses the NAL unit type
		type = header[4] & 0x1F;
		buf[0] = header[4];
		len = fill(buf, 1,  naluLength - 1);
		//Log.d("ts", String.format("RTMPH264Packetizer::run() ts=%d naluLength=%d type=%d", timestamp, naluLength, type));
		//observer.putData(buf, naluLength, 0, RTMPClient.MEDIATYPE_VIDEO, ts+videoStart/1000000, 0, 0);
	}
	
	
	private int fill(byte[] buffer, int offset,int length) throws IOException {
		int sum = 0, len;
		while (sum<length) {
			len = is.read(buffer, offset+sum, length-sum);
			if (len<0) {
				throw new IOException("End of stream");
			}
			else sum+=len;
		}
		return sum;
	}
	
	private void resync() throws IOException {
		int type;

		Log.e(TAG,"Packetizer out of sync ! Let's try to fix that...(NAL length: "+naluLength+")");

		while (true) {

			header[0] = header[1];
			header[1] = header[2];
			header[2] = header[3];
			header[3] = header[4];
			header[4] = (byte) is.read();

			type = header[4]&0x1F;

			if (type == 5 || type == 1) {
				naluLength = header[3]&0xFF | (header[2]&0xFF)<<8 | (header[1]&0xFF)<<16 | (header[0]&0xFF)<<24;
				if (naluLength>0 && naluLength<100000) {
					oldtime = System.nanoTime();
					Log.e(TAG,"A NAL unit may have been found in the bit stream !");
					break;
				}
				if (naluLength==0) {
					Log.e(TAG,"NAL unit with NULL size found...");
				} else if (header[3]==0xFF && header[2]==0xFF && header[1]==0xFF && header[0]==0xFF) {
					Log.e(TAG,"NAL unit with 0xFFFFFFFF size found...");
				}
			}

		}

	}
}
