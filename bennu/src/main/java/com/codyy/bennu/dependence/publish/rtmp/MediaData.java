package com.codyy.bennu.dependence.publish.rtmp;

// 
public class MediaData 
{
	public final static int STATE_CREATE 	= 0x00;
	public final static int STATE_CAPTURE 	= 0x01;
	public final static int STATE_PROCESS	= 0x02;
	public final static int STATE_ENCODE  	= 0x03;
	public final static int STATE_ERROR 	= 0x04;
	
	public final static int MEDIATYPE_AUDIO = 0x01;
	public final static int MEDIATYPE_VIDEO = 0x02;
	
	private byte[] buf = null;
	private int    capacity = 0;
	private int    len = 0;
	private long   timepoint = 0;
	private int    type = MEDIATYPE_AUDIO;

	// for test
	private long   captureTime   = 0;
	private long   processedTime = 0;
	private long   encodedTime   = 0;
	private int    state = STATE_CREATE;
	
	
	public static MediaData createVideoData(int length)
	{
		MediaData mediaData = new MediaData(length, MEDIATYPE_VIDEO);
		return mediaData;
	}
	
	public static MediaData createVideoData(byte[] data, int length)
	{
		return createVideoData(data, 0, length);
	}
	
	public static MediaData createVideoData(byte[] data, int offset, int length)
	{
		MediaData mediaData = new MediaData(data, offset, length, MEDIATYPE_VIDEO);
		return mediaData;
	}
	
	public static MediaData createAudioData(int length)
	{
		MediaData mediaData = new MediaData(length, MEDIATYPE_AUDIO);
		return mediaData;
	}

	public static MediaData createAudioData(byte[] data, int length)
	{
		return createAudioData(data, 0, length);
	}
	
	public static MediaData createAudioData(byte[] data, int offset, int length)
	{
		MediaData mediaData = new MediaData(data, offset, length, MEDIATYPE_AUDIO);
		return mediaData;
	}
	
	public static boolean checkType(int type)
	{
		if (type == MEDIATYPE_AUDIO || type == MEDIATYPE_VIDEO)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean isVideo()
	{
		return (type == MEDIATYPE_VIDEO) ? true : false;
	}

	public boolean isAudio()
	{
		return (type == MEDIATYPE_AUDIO) ? true : false;
	}

	public byte[] getData()
	{
		return buf;
	}

	public int getDataLength()
	{
		return len;
	}

	public long getTimestamp()
	{
		return timepoint;
	}

	public int getMediaType()
	{
		return type;
	}

	public long getCaptureTime()
	{
		return captureTime;
	}

	public long getProcessedTime()
	{
		return processedTime;
	}

	public long getEncodedTime()
	{
		return encodedTime;
	}

	public void setTimestamp(long timepoint)
	{
		this.timepoint = timepoint;
	}

	public void updateCaptureTime()
	{
		setCaptureTime(0);
	}

	public void setCaptureTime(long captureTime)
	{
		if (captureTime == 0)
		{
			this.captureTime = System.currentTimeMillis();
		}
		else
		{
			this.captureTime = captureTime;
		}
		this.processedTime = this.captureTime;
		this.encodedTime = this.captureTime;
		this.state = STATE_CAPTURE;
	}

	public void updateProcessedTime()
	{
		setProcessedTime(0);
	}

	public void setProcessedTime(long processedTime)
	{
		if (processedTime == 0)
		{
			this.processedTime = System.currentTimeMillis();
		}
		else
		{
			this.processedTime = processedTime;
		}
		this.state = MediaData.STATE_PROCESS;
	}

	public void updateEncodedTime()
	{
		setEncodedTime(0);
	}

	public void setEncodedTime(long encodedTime)
	{
		if (encodedTime == 0)
		{
			this.encodedTime = System.nanoTime();
		}
		else
		{
			this.encodedTime = encodedTime;
		}
		this.state = MediaData.STATE_ENCODE;
	}

	public void updateData(byte[] data, int length)
	{
		updateData(data, 0, length);
	}
	
	public void updateData(byte[] data, int offset, int length)
	{
		int realLength = length - offset;
		if (data != null && realLength > 0)
		{
			if (this.capacity < realLength)
			{
				this.buf = new byte[realLength];
				this.capacity = realLength;
			}
			System.arraycopy(data, offset, this.buf, 0, realLength);
			this.len = realLength;
		}
	}
	
	private MediaData(int capacity, int type)
	{
		allocMemory(capacity);
		setType(type);
	}

	private MediaData(byte[] data, int offset, int length, int type)
	{	
		int realLength = length - offset;
		if (data != null && realLength > 0)
		{
			allocMemory(realLength);
			updateData(data, offset, length);
			setType(type);
		}
	}
	
	private void allocMemory(int capacity)
	{
		if (capacity > 0)
		{
			this.buf = new byte[capacity];
			this.capacity = capacity;
		}
		else
		{
			throw new RuntimeException("capacity is less than zero");
		}
	}
	
	private void setType(int type)
	{
		if (checkType(type))
		{
			this.type = type;
		}
		else
		{
			throw new RuntimeException("type is invalid");
		}
	}
	
}
