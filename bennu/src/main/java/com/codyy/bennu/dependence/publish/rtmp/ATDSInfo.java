//package net.majorkernelpanic.streaming.rtp;
package com.codyy.bennu.dependence.publish.rtmp;
public class ATDSInfo
{
	public final static int ADTSHEADERMINLENGTH = 7;
	
	public boolean isADTS;
	public boolean protection;
	public int samplingRateIndex;
	public int profile;
	public int channel;
	public int frameLength;
	public int adtsHeaderLength;
	public byte[] audioCfg;
	
	public ATDSInfo()
	{
		isADTS = false;
		protection = false;
		samplingRateIndex = 0;
		profile = 0;
		channel = 0;
		frameLength = 0;
		adtsHeaderLength = 0;
		audioCfg = null;
	}

	public static boolean hasADTSHeader(byte[] frame, int len)
	{
		if (frame == null || len < ATDSInfo.ADTSHEADERMINLENGTH)
		{
			return false;
		}
		if ((frame[0] & 0xFF) == 0xFF && (frame[1] & 0xF0) == 0xF0) 
		{
			return true;
		}
		return false;
	}
	
	public static ATDSInfo parseADTS(byte[] frame, int len, boolean createConf)
	{
		ATDSInfo info = new ATDSInfo();
		if (hasADTSHeader(frame, len))
		{
			info.isADTS = true;
			boolean protection = (frame[1] & 0x01) > 0 ? true : false;
			//从流里面获取相关信息
			int samplingRateIndex = (frame[2] & 0x3C) >> 2; // 4
			int profile = ((frame[2] & 0xC0) >> 6 ) + 1;  // 2
			int channel = (frame[2] & 0x01) << 2 | (frame[3] & 0xC0) >> 6; // 1
			
			// The protection bit indicates whether or not the header contains the two extra bytes
			int adtsHeaderLength = (protection ? 7 : 9);
			int frameLength = (frame[3]&0x03) << 11 | 
							  (frame[4]&0xFF) << 3 | 
							  (frame[5]&0xFF) >> 5 ;
			frameLength -= adtsHeaderLength;
			
			info.protection = protection;
			info.samplingRateIndex = samplingRateIndex;
			info.profile = profile;
			info.channel = channel;
			info.adtsHeaderLength = adtsHeaderLength;
			info.frameLength = frameLength;
			if (createConf)
			{
				info.audioCfg = new byte[2];
				// 5 bits for the object type / 4 bits for the sampling rate / 4 bits for the channel / padding
				int config = (profile & 0x1F) << 11 | (samplingRateIndex & 0x0F) << 7 | (channel & 0x0F) << 3;
				info.audioCfg[0] = (byte) (config >> 8);
				info.audioCfg[1] = (byte) (config & 0xFF);
				//audioCfg[0] = 0x12;
				//audioCfg[1] = 0x10;
				
//				info.audioCfg = new byte[7];
//				//AF 00 13 90 56 E5 A5 48 00
//				//      80, 64, 25, 31, -4,
//				info.audioCfg[0] = 0x13;
//				info.audioCfg[1] = (byte) 0x90;
//				info.audioCfg[2] = 0x56;
//				info.audioCfg[3] = (byte) 0xE5;
//				info.audioCfg[4] = (byte) 0xA5;
//				info.audioCfg[5] = 0x48;
//				info.audioCfg[6] = 0x00;
			}	
		}
		return info;
	}
	
}
