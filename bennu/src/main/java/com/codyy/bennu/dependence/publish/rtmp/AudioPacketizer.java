package com.codyy.bennu.dependence.publish.rtmp;

/**
 * 说明:RTMP的Messgae格式与FLV的Tag格式相同，可以按照FLV的格式来构造数据包，然后发送出去
 */
public class AudioPacketizer
{
	private boolean isFirstAudio = true;
	private byte[] audioConf = null;
	
	public AudioPacketizer()
	{
	}
	
	public boolean isAudioConf(byte[] buf, int len)
	{
		if (isFirstAudio)
		{
			isFirstAudio = false;
			return true;
		}
		return  false;
	}
	
	
	public void setAudioConf(byte[] buf, int len)
	{
		audioConf = new byte[len];
		System.arraycopy(buf, 0, audioConf, 0, len);
	}
	
	public void setAudioConf(int conf)
	{
		audioConf = new byte[2];
		audioConf[0] = (byte) (conf >> 8);
		audioConf[1] = (byte) (conf & 0xFF);
	}
	
	public void parseAudioConf(byte[] buf, int len)
	{
		setAudioConf(buf, len);
	}
	
	public byte[] makeAudioConf(int timeStamp)
	{
		return makeAudioTag(audioConf, audioConf.length, timeStamp, true);
	}
	
	public byte[] makeAudioConf(byte[] data, int length)
	{
		return makeAudioTag(data, length, 0, true);
	}
	
	public byte[] makeAudio(byte[] data, int length, int timeStamp)
	{
		return makeAudioTag(data, length, timeStamp, false);
	}
	
	//将AAC帧封装成Tag后发送---AAC数据去除头信息
	private byte[] makeAudioTag(byte[] data, int length, int timeStamp, boolean bAudioConig)
	{
		//TagHeader(11) + AudioHeader(2) + AudioData(size) + PreTagSize（4）
		int nBodySize = length + 2;
		int nAllocSize = 11 + nBodySize + 4;
		byte[] body = new byte[nAllocSize];
		
		//构造Tag Header
		body[0] = 0x08;  //表示Tag为音频
		body[1] = (byte) ((nBodySize >> 16) & 0xff);//Tag长度，对于AAC,Audio Header和Audio Data
		body[2] = (byte) ((nBodySize >>  8) & 0xff);
		body[3] = (byte) ((nBodySize >>  0) & 0xff);

		body[4] = (byte) ((timeStamp >> 16) & 0xff);//Tag的时间戳
		body[5] = (byte) ((timeStamp >>  8) & 0xff);
		body[6] = (byte) ((timeStamp >>  0) & 0xff);
		body[7] = (byte) ((timeStamp >> 24) & 0xff);

		body[8]  = 0;//StreamId
		body[9]  = 0;
		body[10] = 0;
		//
		/*
			If the SoundFormat indicates AAC, the SoundType should be set to 1 (stereo) and the
			SoundRate should be set to 3 (44 kHz). However, this does not mean that AAC audio in FLV
			is always stereo, 44 kHz data. Instead, the Flash Player ignores these values and extracts the
			channel and sample rate data is encoded in the AAC bitstream.
		*/
		//构造AudioTagHeader和AACPacketType
		{
			body[11] = (byte) ((10 << 4) |      // soundformat "10 == AAC"
					   (3  << 2) |      		// soundrate   "3  == 44-kHZ"
					   (1  << 1) |      		// soundsize   "1  == 16bit"
					   (1  << 0));       		// soundtype   "1  == Stereo"
			if (bAudioConig)
			{
				body[12] = 0x00;
			}
			else
			{
				body[12] = 0x01;
			}
		}
		System.arraycopy(data, 0, body, 13, length);
		//构造上一个Tag长度
		int nPreTagSize = 11 + nBodySize;
		body[nPreTagSize + 0] = (byte) ((nPreTagSize >> 24) & 0xff);
		body[nPreTagSize + 1] = (byte) ((nPreTagSize >> 16) & 0xff);
		body[nPreTagSize + 2] = (byte) ((nPreTagSize >>  8) & 0xff);
		body[nPreTagSize + 3] = (byte) ((nPreTagSize >>  0) & 0xff);
		return body;
	}
}
