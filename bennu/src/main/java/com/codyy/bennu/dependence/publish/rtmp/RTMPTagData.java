package com.codyy.bennu.dependence.publish.rtmp;

public class RTMPTagData 
{
	public byte[]  buf;
	public int     len;
	public int     type;//音频/视频
	public long    timepoint;
	public long    ts;
	public long    no; //测试用
	
	public RTMPTagData()
	{	
	}
	
	// 不需要开辟内存
	public RTMPTagData(byte[] frame, int frameLen, int offset, int type, long timepoint, long ts, long no, byte[] dst)
	{
		configRTMPTagData(frame, frameLen, offset, type, timepoint, ts, no, dst);
	}
	
	// 开辟新内存存放数据
	public RTMPTagData(byte[] frame, int frameLen, int offset, int type, long timepoint, long ts, long no)
	{
		configRTMPTagData(frame, frameLen, offset, type, timepoint, ts, no, null);
	}
	
	public RTMPTagData(byte[] frame, int frameLen, int type, long timepoint)
	{
		configRTMPTagData(frame, frameLen, 0, type, timepoint, 0, 0, null);
	}
	
	public void updateTimestamp(long timepoint) 
	{
		this.timepoint = timepoint;
	}
	
	private void configRTMPTagData(byte[] frame, int frameLen, int offset, int type, long timepoint, long ts, long no, byte[] dst)
	{
		if (frameLen <= 0 || offset >= frameLen)
		{
			return;
		}
		this.len = frameLen - offset;
		if (dst != null)
		{
			this.buf = dst;
		}
		else
		{
			this.buf = new byte[this.len];
		}
		System.arraycopy(frame, offset, this.buf, 0, this.len);
		this.type = type;
		this.timepoint = timepoint;
		this.ts = ts;
		this.no = no; 
	}
}
